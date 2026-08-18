package library;

import book.BookDTO;
import loan.LoanDTO;
import member.MemberDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LibraryService {

    private final JdbcTemplate jdbc;

    public LibraryService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ==========================================
    // LÅNTAGAREN
    // ==========================================

    // Hämtar alla böcker med kopplade författare och kategorier
    public List<BookDTO> getAllBooksDTO() {
        String sql = """
            SELECT b.id, b.title, b.isbn, b.year_published, b.available_copies,
                   GROUP_CONCAT(DISTINCT CONCAT(a.first_name, ' ', a.last_name) SEPARATOR ', ') AS authors,
                   GROUP_CONCAT(DISTINCT c.name SEPARATOR ', ') AS categories
            FROM books b
            LEFT JOIN book_authors ba ON b.id = ba.book_id
            LEFT JOIN authors a ON ba.author_id = a.id
            LEFT JOIN book_categories bc ON b.id = bc.book_id
            LEFT JOIN categories c ON bc.category_id = c.id
            GROUP BY b.id
        """;

        return jdbc.queryForList(sql).stream()
                .map(this::mapToBookDTO)
                .toList();
    }

    // Filtrerar fram böcker som finns inne på lager
    public List<BookDTO> getAvailableBooks() {
        return getAllBooksDTO().stream()
                .filter(book -> book.availableCopies() > 0)
                .toList();
    }

    // Söker böcker på titel/författare samt filtrerar på kategori via Streams
    public List<BookDTO> searchAndFilterBooks(String query, String category) {
        return getAllBooksDTO().stream()
                .filter(b -> query.isBlank() ||
                        b.title().toLowerCase().contains(query.toLowerCase()) ||
                        (b.authors() != null && b.authors().toLowerCase().contains(query.toLowerCase())))
                .filter(b -> category.isBlank() ||
                        (b.categories() != null && b.categories().toLowerCase().contains(category.toLowerCase())))
                .toList();
    }

    // Skapar ett lån och minskar bokens lagersaldo
    @Transactional
    public boolean borrowBook(int bookId, int memberId) {
        int updated = jdbc.update(
                "UPDATE books SET available_copies = available_copies - 1 WHERE id = ? AND available_copies > 0",
                bookId
        );
        if (updated == 0) {
            return false;
        }

        LocalDate today = LocalDate.now();
        jdbc.update(
                "INSERT INTO loans (book_id, member_id, loan_date, due_date) VALUES (?, ?, ?, ?)",
                bookId, memberId, today, today.plusWeeks(2)
        );
        return true;
    }

    // Återlämnar ett aktivt lån och ökar bokens lagersaldo
    @Transactional
    public boolean returnBook(int bookId, int memberId) {
        String findLoanSql = "SELECT id FROM loans WHERE book_id = ? AND member_id = ? AND return_date IS NULL";
        List<Integer> loanIds = jdbc.query(findLoanSql, (rs, rowNum) -> rs.getInt("id"), bookId, memberId);

        if (loanIds.isEmpty()) {
            return false;
        }

        int loanId = loanIds.getFirst();
        jdbc.update("UPDATE loans SET return_date = ? WHERE id = ?", LocalDate.now(), loanId);
        jdbc.update("UPDATE books SET available_copies = available_copies + 1 WHERE id = ?", bookId);
        return true;
    }

    // Förlänger återlämningsdatumet för ett specifikt lån
    public boolean extendLoan(int bookId, int memberId, int extraDays) {
        String sql = """
            UPDATE loans 
            SET due_date = DATE_ADD(due_date, INTERVAL ? DAY) 
            WHERE book_id = ? AND member_id = ? AND return_date IS NULL
        """;
        int rowsAffected = jdbc.update(sql, extraDays, bookId, memberId);
        return rowsAffected > 0;
    }

    // Hämtar en medlems profiluppgifter via medlems-ID
    public Optional<MemberDTO> getMemberProfile(int memberId) {
        String sql = "SELECT * FROM members WHERE id = ?";
        List<MemberDTO> members = jdbc.query(sql, (rs, rowNum) -> new MemberDTO(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("first_name") + " " + rs.getString("last_name"),
                rs.getString("email"),
                rs.getDate("membership_date").toLocalDate(),
                rs.getString("membership_type"),
                rs.getString("status")
        ), memberId);

        return members.stream().findFirst();
    }

    // Hämtar alla lån för en medlem
    public List<LoanDTO> getMemberLoans(int memberId) {
        String sql = """
        SELECT l.id, l.book_id, b.title AS book_title, l.member_id,
               CONCAT(m.first_name, ' ', m.last_name) AS member_name,
               l.loan_date, l.due_date, l.return_date
        FROM loans l
        JOIN books b ON l.book_id = b.id
        JOIN members m ON l.member_id = m.id
        WHERE l.member_id = ? AND l.return_date IS NULL
    """;
        return jdbc.queryForList(sql, memberId).stream()
                .map(this::mapToLoanDTO)
                .toList();
    }

    // Hämtar enbart aktiva (ej återlämnade) lån för en medlem
    public List<LoanDTO> getActiveMemberLoans(int memberId) {
        String sql = """
        SELECT l.id, l.book_id, b.title AS book_title, l.member_id,
               CONCAT(m.first_name, ' ', m.last_name) AS member_name,
               l.loan_date, l.due_date, l.return_date
        FROM loans l
        JOIN books b ON l.book_id = b.id
        JOIN members m ON l.member_id = m.id
        WHERE l.member_id = ? AND l.return_date IS NULL
    """;
        return jdbc.queryForList(sql, memberId).stream()
                .map(this::mapToLoanDTO)
                .toList();
    }

    // Uppdaterar namn och e-post för en medlem
    public boolean updateMemberProfile(int memberId, String firstName, String lastName, String email) {
        String sql = "UPDATE members SET first_name = ?, last_name = ?, email = ? WHERE id = ?";
        return jdbc.update(sql, firstName, lastName, email, memberId) > 0;
    }

    // ==========================================
    // BIBLIOTEKARIE
    // ==========================================

    // Skapar en ny medlem och returnerar dess genererade ID
    @Transactional
    public Optional<Integer> createMember(String firstName, String lastName, String email) {
        String sql = "INSERT INTO members (first_name, last_name, email, membership_date, membership_type, status) VALUES (?, ?, ?, ?, 'standard', 'active')";
        int rows = jdbc.update(sql, firstName, lastName, email, LocalDate.now());

        if (rows > 0) {
            Integer newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            return Optional.ofNullable(newId);
        }
        return Optional.empty();
    }

    // Hämtar alla pågående lån i hela biblioteket
    public List<LoanDTO> getAllActiveLoans() {
        String sql = """
            SELECT l.id, l.book_id, b.title AS book_title, l.member_id,
                   CONCAT(m.first_name, ' ', m.last_name) AS member_name,
                   l.loan_date, l.due_date, l.return_date
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            WHERE l.return_date IS NULL
        """;
        return jdbc.queryForList(sql).stream()
                .map(this::mapToLoanDTO)
                .toList();
    }

    // Registrerar en ny bok och kopplar eventuell författare och kategori
    @Transactional
    public boolean addBook(String title, String isbn, int year, int copies, int authorId, int categoryId) {
        String sql = "INSERT INTO books (title, isbn, year_published, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";
        int rows = jdbc.update(sql, title, isbn, year, copies, copies);
        if (rows == 0) return false;

        Integer bookId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        if (bookId != null) {
            if (authorId > 0) jdbc.update("INSERT IGNORE INTO book_authors (book_id, author_id) VALUES (?, ?)", bookId, authorId);
            if (categoryId > 0) jdbc.update("INSERT IGNORE INTO book_categories (book_id, category_id) VALUES (?, ?)", bookId, categoryId);
            return true;
        }
        return false;
    }

    // Redigerar titel och publiceringsår på en bok
    public boolean editBook(int bookId, String title, int year) {
        return jdbc.update("UPDATE books SET title = ?, year_published = ? WHERE id = ?", title, year, bookId) > 0;
    }

    // Raderar en bok ur systemet
    public boolean deleteBook(int bookId) {
        return jdbc.update("DELETE FROM books WHERE id = ?", bookId) > 0;
    }

    // Skapar en ny författare och returnerar dess genererade ID
    @Transactional
    public Optional<Integer> addAuthor(String firstName, String lastName, String nationality) {
        String sql = "INSERT INTO authors (first_name, last_name, nationality) VALUES (?, ?, ?)";
        int rows = jdbc.update(sql, firstName, lastName, nationality);

        if (rows > 0) {
            Integer newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            return Optional.ofNullable(newId);
        }
        return Optional.empty();
    }

    // Uppdaterar uppgifter för en författare
    public boolean editAuthor(int authorId, String firstName, String lastName, String nationality) {
        return jdbc.update("UPDATE authors SET first_name = ?, last_name = ?, nationality = ? WHERE id = ?",
                firstName, lastName, nationality, authorId) > 0;
    }

    // Kopplar en befintlig bok till en befintlig kategori
    public boolean linkBookToCategory(int bookId, int categoryId) {
        return jdbc.update("INSERT IGNORE INTO book_categories (book_id, category_id) VALUES (?, ?)", bookId, categoryId) > 0;
    }

    // ==========================================
    // MAPPERS
    // ==========================================

    // Omvandlar en databasrad från MySQL till ett BookDTO-objekt
    private BookDTO mapToBookDTO(Map<String, Object> row) {
        return new BookDTO(
                ((Number) row.get("id")).intValue(),
                (String) row.get("title"),
                (String) row.get("isbn"),
                row.get("year_published") != null ? ((Number) row.get("year_published")).intValue() : 0,
                ((Number) row.get("available_copies")).intValue(),
                (String) row.get("authors"),
                (String) row.get("categories")
        );
    }

    // Omvandlar en databasrad från MySQL till ett LoanDTO-objekt med förseningskontroll
    private LoanDTO mapToLoanDTO(Map<String, Object> row) {
        LocalDate dueDate = ((Date) row.get("due_date")).toLocalDate();
        LocalDate returnDate = row.get("return_date") != null ? ((Date) row.get("return_date")).toLocalDate() : null;
        boolean isOverdue = returnDate == null && LocalDate.now().isAfter(dueDate);

        return new LoanDTO(
                ((Number) row.get("id")).intValue(),
                ((Number) row.get("book_id")).intValue(),
                (String) row.get("book_title"),
                ((Number) row.get("member_id")).intValue(),
                (String) row.get("member_name"),
                ((Date) row.get("loan_date")).toLocalDate(),
                dueDate,
                returnDate,
                isOverdue
        );
    }
}
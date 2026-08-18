package loan;

import java.time.LocalDate;

// Oföränderlig databehållare (Record) för att överföra låneinformation till menyer och rapporter
public record LoanDTO(
        int id,                 // Lånets unika ID-nummer
        int bookId,             // Den lånade bokens ID-nummer
        String bookTitle,       // Bokens titel
        int memberId,           // Låntagarens medlems-ID
        String memberName,      // Låntagarens fullständiga namn
        LocalDate loanDate,     // Datum då lånet skapades
        LocalDate dueDate,      // Datum då boken senast ska återlämnas
        LocalDate returnDate,   // Faktiskt returdatum (null om det fortfarande är aktivt)
        boolean isOverdue       // Flagga som anger om lånet har passerat sitt förfallodatum
) {}
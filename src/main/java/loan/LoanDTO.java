package loan;

import java.time.LocalDate;

public record LoanDTO(
        int id,
        int bookId,
        String bookTitle,
        int memberId,
        String memberName,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate,
        boolean isOverdue
) {}
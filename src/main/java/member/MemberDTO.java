package member;

import java.time.LocalDate;

public record MemberDTO(
        int id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        LocalDate membershipDate,
        String membershipType,
        String status
) {}
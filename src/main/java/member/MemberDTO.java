package member;

import java.time.LocalDate;

// Oföränderlig databehållare (Record) för att överföra medlemsprofilens data till gränssnittet
public record MemberDTO(
        int id,                     // Medlemmens unika ID-nummer[cite: 16]
        String firstName,           // Medlemmens förnamn[cite: 16]
        String lastName,            // Medlemmens efternamn[cite: 16]
        String fullName,            // Hela namnet sammanslaget[cite: 16]
        String email,               // Medlemmens e-postadress[cite: 16]
        LocalDate membershipDate,   // Datum då medlemskapet skapades[cite: 16]
        String membershipType,      // Typ av medlemskap (t.ex. standard, premium)[cite: 16]
        String status               // Medlemmens nuvarande status (t.ex. active)[cite: 16]
) {}
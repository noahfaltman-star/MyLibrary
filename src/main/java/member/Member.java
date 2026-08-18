package member;

import java.time.LocalDate;
import user.User;

// Subklass som representerar en låntagare och ärver basklassen User
public class Member extends User {
    // Medlemsspecifika egenskaper
    private LocalDate membershipDate;
    private String membershipType;
    private String status;

    // Konstruktor som sätter grunduppgifter via super() samt medlemsspecifika fält
    public Member(int id, String firstName, String lastName, String email,
                  LocalDate membershipDate, String membershipType, String status) {
        super(id, firstName, lastName, email);
        this.membershipDate = membershipDate;
        this.membershipType = membershipType;
        this.status = status;
    }

    // Överskuggar metoden för att ge en dynamisk rollbeskrivning med typ och status
    @Override
    public String getRoleDescription() {
        return "Låntagare (" + membershipType + ") - Status: " + status;
    }

    // Getters och setters för medlemmens fält (inkapsling)
    public LocalDate getMembershipDate() { return membershipDate; }
    public String getMembershipType() { return membershipType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

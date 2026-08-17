package member;

import java.time.LocalDate;
import user.User;

public class Member extends User {
    private LocalDate membershipDate;
    private String membershipType;
    private String status;

    public Member(int id, String firstName, String lastName, String email,
                  LocalDate membershipDate, String membershipType, String status) {
        super(id, firstName, lastName, email);
        this.membershipDate = membershipDate;
        this.membershipType = membershipType;
        this.status = status;
    }

    @Override
    public String getRoleDescription() {
        return "Låntagare (" + membershipType + ") - Status: " + status;
    }

    public LocalDate getMembershipDate() { return membershipDate; }
    public String getMembershipType() { return membershipType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

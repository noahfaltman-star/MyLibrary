package user;

// Abstrakt basklass som definierar gemensamma egenskaper för alla användartyper
public abstract class User {
    // Gemensamma fält för alla användare
    private int id;
    private String firstName;
    private String lastName;
    private String email;

    // Konstruktor för att initiera grundläggande användardata
    public User(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Abstrakt metod som tvingar subklasser att definiera sin egen rollbeskrivning (polymorfism)
    public abstract String getRoleDescription();

    // Hjälpmetod för att slå ihop förnamn och efternamn
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters och setters för inkapsling av användaruppgifter
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
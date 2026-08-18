package library;

import user.User;

// Subklass som ärver gemensamma användaregenskaper från basklassen User
public class Librarian extends User {

    // Konstruktor som skickar vidare administratörens uppgifter till basklassen
    public Librarian(int id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    // Överskuggar metoden för att ge en specifik rollbeskrivning (polymorfism)
    @Override
    public String getRoleDescription() {
        return "Bibliotekarie (Administratör)";
    }
}
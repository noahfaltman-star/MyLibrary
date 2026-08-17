package library;

import user.User;

public class Librarian extends User {
    public Librarian(int id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    @Override
    public String getRoleDescription() {
        return "Bibliotekarie (Administratör)";
    }
}
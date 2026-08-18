package book;

public class Book {
    // Instansvariabler för bokens egenskaper i systemet
    private int id;
    private String title;
    private String isbn;
    private int yearPublished;
    private int totalCopies;
    private int availableCopies;

    // Konstruktor för att initiera ett nytt Book-objekt med alla värden
    public Book(int id, String title, String isbn, int yearPublished, int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.yearPublished = yearPublished;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Getters för att hämta bokens information utanför klassen (inkapsling)
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public int getYearPublished() { return yearPublished; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
}
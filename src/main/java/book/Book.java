package book;

public class Book {
    private int id;
    private String title;
    private String isbn;
    private int yearPublished;
    private int totalCopies;
    private int availableCopies;

    public Book(int id, String title, String isbn, int yearPublished, int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.yearPublished = yearPublished;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public int getYearPublished() { return yearPublished; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
}

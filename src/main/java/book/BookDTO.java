package book;

public record BookDTO(
        int id,
        String title,
        String isbn,
        int yearPublished,
        int availableCopies,
        String authors,
        String categories
) {}

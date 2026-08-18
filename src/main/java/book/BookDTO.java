package book;

// Oföränderlig databehållare (Record) för att överföra bokinformation till vyer/menyer
public record BookDTO(
        int id,
        String title,
        String isbn,
        int yearPublished,
        int availableCopies,
        String authors,
        String categories
) {}

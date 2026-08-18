package library;

import book.BookDTO;
import loan.LoanDTO;
import member.MemberDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class LibraryMenu implements CommandLineRunner {

    private final LibraryService libraryService;
    private final Scanner scanner = new Scanner(System.in);

    public LibraryMenu(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // Startpunkt som kör igång konsolapplikationen via Spring Boot
    @Override
    public void run(String... args) {
        while (true) {
            System.out.println("""
                \n========================================
                      📚 BIBLIOTEKSSYSTEM - START
                ========================================
                1. Gå till meny för LÅNTAGARE
                2. Gå till meny för BIBLIOTEKARIE
                0. Avsluta
                ----------------------------------------""");
            System.out.print("Välj roll: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> borrowerMenu();
                case "2" -> librarianMenu();
                case "0" -> {
                    System.out.println("Avslutar programmet. Hej då!");
                    return;
                }
                default -> System.out.println("Ogiltigt val!");
            }
        }
    }

    // Menystyrning och val för låntagare
    private void borrowerMenu() {
        while (true) {
            System.out.println("""
                \n--- 👤 MENY: LÅNTAGARE ---
                1. Visa tillgängliga böcker
                2. Sök & Filtrera böcker
                3. Låna en bok
                4. Återlämna en bok
                5. Förläng ett lån
                6. Visa profilsida & aktiva lån
                7. Uppdatera min profilinformation
                0. Tillbaka till huvudmenyn
                --------------------------""");
            System.out.print("Välj alternativ: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                // Listar alla böcker som finns inne
                case "1" -> printBooks(libraryService.getAvailableBooks());

                // Söker böcker via text och kategorifilter
                case "2" -> {
                    System.out.print("Sökord (titel/författare, eller Enter): ");
                    String q = scanner.nextLine().trim();
                    System.out.print("Filtrera på Kategori (eller Enter): ");
                    String cat = scanner.nextLine().trim();
                    printBooks(libraryService.searchAndFilterBooks(q, cat));
                }

                // Registrerar ett nytt lån för medlemmen
                case "3" -> {
                    System.out.print("Ditt Medlems-ID: ");
                    int mId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Bok-ID att låna: ");
                    int bId = Integer.parseInt(scanner.nextLine().trim());
                    if (libraryService.borrowBook(bId, mId)) System.out.println("✅ Boken har lånats!");
                    else System.out.println("❌ Boken är slut i lager eller felaktigt ID.");
                }

                // Lämnar tillbaka ett aktivt lån
                case "4" -> {
                    System.out.print("Ditt Medlems-ID: ");
                    int mId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Bok-ID att återlämna: ");
                    int bId = Integer.parseInt(scanner.nextLine().trim());
                    if (libraryService.returnBook(bId, mId)) System.out.println("✅ Boken är återlämnad!");
                    else System.out.println("❌ Kunde inte återlämna (inget aktivt lån på denna bok hittades).");
                }

                // Förlänger lånetiden för en lånad bok
                case "5" -> {
                    System.out.print("Ditt Medlems-ID: ");
                    int mId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Bok-ID att förlänga: ");
                    int bId = Integer.parseInt(scanner.nextLine().trim());
                    if (libraryService.extendLoan(bId, mId, 14)) System.out.println("✅ Lånet förlängdes med 14 dagar!");
                    else System.out.println("❌ Kunde inte förlänga (aktivt lån saknas).");
                }

                // Visar medlemmens personuppgifter och pågående lån
                case "6" -> {
                    System.out.print("Ditt Medlems-ID: ");
                    int mId = Integer.parseInt(scanner.nextLine().trim());
                    final int currentMemberId = mId;

                    libraryService.getMemberProfile(currentMemberId).ifPresentOrElse(m -> {
                        System.out.printf("%n📄 PROFIL: %s | E-post: %s | Typ: %s | Status: %s%n",
                                m.fullName(), m.email(), m.membershipType(), m.status());
                        System.out.println("Dina aktiva lån:");
                        libraryService.getActiveMemberLoans(currentMemberId).forEach(this::printLoan);
                    }, () -> System.out.println("Medlem hittades inte."));
                }

                // Uppdaterar medlemmens personuppgifter
                case "7" -> {
                    System.out.print("Ditt Medlems-ID: ");
                    int mId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Nytt förnamn: ");
                    String fn = scanner.nextLine().trim();
                    System.out.print("Nytt efternamn: ");
                    String ln = scanner.nextLine().trim();
                    System.out.print("Ny e-post: ");
                    String em = scanner.nextLine().trim();
                    if (libraryService.updateMemberProfile(mId, fn, ln, em)) System.out.println("✅ Profil uppdaterad!");
                    else System.out.println("❌ Uppdatering misslyckades.");
                }
                case "0" -> { return; }
                default -> System.out.println("Ogiltigt val.");
            }
        }
    }

    // Menystyrning och administrativa verktyg för bibliotekarien
    private void librarianMenu() {
        while (true) {
            System.out.println("""
                \n--- 📖 MENY: BIBLIOTEKARIE ---
                1. Skapa nytt låntagarkonto
                2. Se alla aktiva lån
                3. Lägg till en bok i sortimentet
                4. Redigera en bok
                5. Ta bort en bok
                6. Lägg till författare
                7. Redigera författare
                8. Koppla bok till kategori
                0. Tillbaka till huvudmenyn
                ------------------------------""");
            System.out.print("Välj alternativ: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                // Skapar ett nytt medlemskonto och visar det nya ID-numret
                case "1" -> {
                    System.out.print("Förnamn: "); String fn = scanner.nextLine().trim();
                    System.out.print("Efternamn: "); String ln = scanner.nextLine().trim();
                    System.out.print("E-post: "); String em = scanner.nextLine().trim();

                    libraryService.createMember(fn, ln, em).ifPresentOrElse(
                            id -> System.out.printf("✅ Medlem skapad! Nytt Medlems-ID: %d%n", id),
                            () -> System.out.println("❌ Kunde inte skapa medlem.")
                    );
                }

                // Visar samtliga aktiva utlåningar i biblioteket
                case "2" -> {
                    List<LoanDTO> active = libraryService.getAllActiveLoans();
                    System.out.println("\n--- Alla aktiva lån (" + active.size() + " st) ---");
                    active.forEach(this::printLoan);
                }

                // Registrerar en ny bok i katalogen
                case "3" -> {
                    System.out.print("Titel: "); String t = scanner.nextLine().trim();
                    System.out.print("ISBN: "); String i = scanner.nextLine().trim();
                    System.out.print("Publiceringsår: "); int y = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Antal exemplar: "); int c = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Författar-ID (0 för ingen): "); int aId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Kategori-ID (0 för ingen): "); int cId = Integer.parseInt(scanner.nextLine().trim());
                    if (libraryService.addBook(t, i, y, c, aId, cId)) System.out.println("✅ Bok tillagd!");
                    else System.out.println("❌ Kunde inte lägga till bok.");
                }

                // Uppdaterar titel och år på en befintlig bok
                case "4" -> {
                    System.out.print("Bok-ID att redigera: "); int id = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Ny titel: "); String t = scanner.nextLine().trim();
                    System.out.print("Nytt år: "); int y = Integer.parseInt(scanner.nextLine().trim());
                    if (libraryService.editBook(id, t, y)) System.out.println("✅ Bok uppdaterad!");
                    else System.out.println("❌ Uppdatering misslyckades.");
                }

                // Tar bort en bok ur sortimentet
                case "5" -> {
                    System.out.print("Bok-ID att ta bort: "); int id = Integer.parseInt(scanner.nextLine().trim());
                    if (libraryService.deleteBook(id)) System.out.println("✅ Bok raderad!");
                    else System.out.println("❌ Kunde inte radera bok.");
                }

                // Skapar en författare och returnerar dess ID-nummer
                case "6" -> {
                    System.out.print("Förnamn: "); String fn = scanner.nextLine().trim();
                    System.out.print("Efternamn: "); String ln = scanner.nextLine().trim();
                    System.out.print("Nationalitet: "); String nat = scanner.nextLine().trim();

                    libraryService.addAuthor(fn, ln, nat).ifPresentOrElse(
                            id -> System.out.printf("✅ Författare skapad! Författar-ID: %d%n", id),
                            () -> System.out.println("❌ Fel uppstod när författaren skulle skapas.")
                    );
                }

                // Ändrar information om en befintlig författare
                case "7" -> {
                    System.out.print("Författar-ID: "); int aId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Nytt förnamn: "); String fn = scanner.nextLine().trim();
                    System.out.print("Nytt efternamn: "); String ln = scanner.nextLine().trim();
                    System.out.print("Ny nationalitet: "); String nat = scanner.nextLine().trim();
                    if (libraryService.editAuthor(aId, fn, ln, nat)) System.out.println("✅ Författare uppdaterad!");
                    else System.out.println("❌ Fel uppstod.");
                }

                // Kopplar en bok till en kategori
                case "8" -> {
                    System.out.print("Bok-ID: "); int bId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Kategori-ID: "); int cId = Integer.parseInt(scanner.nextLine().trim());
                    if (libraryService.linkBookToCategory(bId, cId)) System.out.println("✅ Bok kopplad till kategori!");
                    else System.out.println("❌ Fel uppstod.");
                }
                case "0" -> { return; }
                default -> System.out.println("Ogiltigt val.");
            }
        }
    }

    // Formaterar och skriver ut en lista med böcker i konsolen
    private void printBooks(List<BookDTO> books) {
        System.out.println("\n----------------- BÖCKER -----------------");
        if (books.isEmpty()) System.out.println("Inga böcker hittades.");
        books.forEach(b -> System.out.printf("[%d] \"%s\" (%d) | Författare: %s | Genrer: %s | Lediga: %d st%n",
                b.id(), b.title(), b.yearPublished(), b.authors() != null ? b.authors() : "Okänd",
                b.categories() != null ? b.categories() : "Inga", b.availableCopies()));
    }

    // Formaterar och skriver ut lånestatus för ett enskilt lån
    private void printLoan(LoanDTO l) {
        String status = l.returnDate() != null ? "Återlämnad (" + l.returnDate() + ")" : (l.isOverdue() ? "⚠️ FÖRSENAD!" : "Aktivt");
        System.out.printf("  - Låne-ID: %d | Bok: \"%s\" (ID: %d) | Medlem: %s (ID: %d) | Förfaller: %s | Status: %s%n",
                l.id(), l.bookTitle(), l.bookId(), l.memberName(), l.memberId(), l.dueDate(), status);
    }
}
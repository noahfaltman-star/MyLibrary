# 📚 Bibliotekssystem (MyLibrary)

Ett konsolbaserat bibliotekshanteringssystem byggt med **Java**, **Spring Boot** och **Spring JDBC / MySQL**. Applikationen hanterar böcker, författare, låntagare och utlåningar via ett textbaserat gränssnitt i terminalen.

---

## 🚀 Funktioner

### 👤 Låntagare

- **Visa tillgängliga böcker:** Se böcker som finns i lager direkt.
- **Sök & Filtrera:** Sök efter titel/författare eller filtrera på genre/kategori.
- **Låna bok:** Registrera nya lån med automatisk saldouppdatering.
- **Återlämna bok:** Lämna tillbaka lån och återställ bokens lagerstatus.
- **Förläng lån:** Förläng lånetiden med 14 dagar.
- **Profil & Aktiva lån:** Se medlemsuppgifter och alla pågående lån.
- **Uppdatera profil:** Ändra namn och kontaktuppgifter.

### 📖 Bibliotekarie (Administratör)

- **Skapa låntagarkonto:** Registrera nya medlemmar och generera medlems-ID.
- **Se alla aktiva lån:** Få en överblick över alla pågående lån samt förseningsstatus.
- **Hantera böcker:** Lägg till nya böcker, redigera detaljer eller ta bort titlar.
- **Hantera författare:** Skapa och redigera författare.
- **Kategorisering:** Koppla böcker till kategorier och genrer.

---

## 🛠 Teknisk Stack & Arkitektur

- **Språk:** Java 21+
- **Ramverk:** Spring Boot 3
- **Databas:** MySQL & Spring JdbcTemplate
- **OOP & Design Patterns:**
- **Arv & Polymorfism:** Basklassen `User` med subklasserna `Member` och `Librarian`.
- **DTO (Data Transfer Objects):** Records (`BookDTO`, `LoanDTO`, `MemberDTO`) för säker dataöverföring mellan databas och konsolvy.
- **Lambdas & Streams:** Filtrering och sökning av böcker och lån.
- **Transaktionshantering:** `@Transactional` för säkra databastransaktioner vid utlån och återlämning.

---

## 📥 Installation & Körning

1. Skapa databasen genom att köra `src/main/bibliotek.sql` i MySQL.
2. Skapa filen `src/main/java/application.properties` och lägg in era lokala uppgifter.
3. Öppna projektet i er IDE och starta applikationen via `BibliotekApplication` i `src/main/java/library/BibliotekApplication`.

### Exempel på `src/main/java/application.properties`:

```properties
spring.application.name=MyLibrary

spring.datasource.url=jdbc:mysql://localhost:3306/bibliotek?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=Your password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

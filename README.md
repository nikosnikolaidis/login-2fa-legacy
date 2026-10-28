# 2. Login με 2FA: στάδιο 3 (τελικό)

## Τι είναι
Μικρή εφαρμογή (Java 8, Maven, χωρίς framework) που προσομοιώνει σύνδεση χρήστη σε δύο βήματα: **username + password**, και μετά **κωδικός TOTP** (6 ψηφία, 30 δευτερόλεπτα, συμβατός με Google Authenticator). Έχει δική της υλοποίηση TOTP (RFC 6238) και Base32.

Φτιάχτηκε **σκόπιμα με κακές πρακτικές** για δοκιμή της πλατφόρμας ανάλυσης. Δεν προορίζεται για παραγωγή.

## Στοιχεία project
| | |
|---|---|
| Γλώσσα / Java | Java, source/target 1.8 |
| Build | Maven (`pom.xml`) |
| Αρχεία κώδικα | 5 main + 1 test (`.java`) |
| Μέγεθος | ~330 γραμμές |
| Tests | 1 (JUnit 4.12), πολύ χαμηλή κάλυψη |

## Αρχεία
| Αρχείο | Ρόλος |
|---|---|
| `pom.xml` | Ρυθμίσεις build και εξαρτήσεις (παλιές και αχρησιμοποίητες) |
| `Main.java` | Διαδραστική γραμμή εντολών: ζητά username, password και, αν χρειαστεί, κωδικό 2FA |
| `AuthConfig.java` | Σταθερές: κοινό μυστικό TOTP, κωδικός admin, βήμα χρόνου, ψηφία |
| `UserStore.java` | Αποθήκευση χρηστών στη μνήμη (alice, bob, admin) και hashing κωδικών |
| `TotpService.java` | Base32 decoding, δημιουργία και επαλήθευση TOTP, backup codes |
| `LoginService.java` | Ροή σύνδεσης: έλεγχος χρήστη, κωδικού και 2FA |
| `TotpServiceTest.java` | 1 test με γνωστό διάνυσμα δοκιμής (RFC 4226) |

## Πώς τρέχει
```bash
mvn package
java -cp target/classes gr.demo.legacy.auth.Main
```
Δοκιμαστικός χρήστης: `alice` με κωδικό `password123`. Ο κωδικός 2FA προκύπτει από το μυστικό `JBSWY3DPEHPK3PXP` (προσθέστε το σε οποιαδήποτε εφαρμογή authenticator). Είναι δοκιμαστικά δεδομένα, όχι πραγματικά διαπιστευτήρια.

## Τι αναμένεται να εντοπίσει η πλατφόρμα (απαντήσεις για τον διαχειριστή)
**Εξαρτήσεις**
- `jackson-databind 2.9.8`: πολλαπλά γνωστά CVE (deserialization).
- `log4j 1.2.17`: end-of-life και με γνωστά CVE.
- `commons-codec 1.10`: παλιά έκδοση.
- Οι τρεις βιβλιοθήκες δεν χρησιμοποιούνται στον κώδικα (unused dependencies).
- Java 1.8 αντί για σύγχρονη LTS.

**Ασφάλεια**
- MD5 για κωδικούς, χωρίς salt (`UserStore.hash`).
- Hardcoded κοινό μυστικό TOTP για όλους τους χρήστες και hardcoded κωδικός admin (`AuthConfig`).
- `java.util.Random` αντί για `SecureRandom` στα backup codes.
- Καταγραφή (`System.out`) του password, του κωδικού 2FA και του **αναμενόμενου** κωδικού.
- Καμία προστασία brute force: ο μετρητής προσπαθειών είναι καθολικός, δεν κλειδώνει ποτέ και δεν είναι ανά χρήστη.
- Σύγκριση hash και κωδικού με `equals` (όχι constant-time). Πιθανό NPE αν αποτύχει το hashing.
- Backup codes χωρίς hashing και χωρίς δυνατότητα επαλήθευσης.
- HMAC-SHA1 (ισχύει στο πρότυπο TOTP, αλλά αξίζει σχόλιο).

**Ποιότητα κώδικα**
- Υπερβολικά πολλά σχόλια που επαναλαμβάνουν τον κώδικα.
- Βαθιά φωλιασμένα `if/else` στο `LoginService.login`.
- Η `login` επιστρέφει strings (`"OK"`, `"WRONG_CODE"`) αντί για enum.
- Διπλότυπη λογική: το `30` γράφεται ξανά στο `LoginService` αντί για `AuthConfig.TIME_STEP`.
- Magic numbers (`1000000`, `99999999`, `10`, `8`).
- Συνένωση strings σε loop για padding των ψηφίων, αντί για `String.format("%06d")`.
- Χειροποίητος Base32 decoder (με `ALPHABET.indexOf`), ενώ υπάρχουν έτοιμες βιβλιοθήκες.
- `catch (Exception)` που επιστρέφει `null`. `StringBuffer` αντί για `StringBuilder`.
- Χαμηλή κάλυψη tests (δεν ελέγχεται το `LoginService`).

**Πού υπάρχει πιο έξυπνος τρόπος γραφής**
- `SecureRandom` για τα backup codes.
- `String.format("%06d", otp)`.
- Early returns και enum για το αποτέλεσμα της σύνδεσης.
- bcrypt/Argon2 αντί για MD5 (και ξεχωριστό μυστικό ανά χρήστη).

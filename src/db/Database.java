//Η κλάση αυτή "προετοιμάζει" τη βάση δεδομένων για χρήση από το πρόγραμμα
//και φροντίζει ώστε οι πίνακες να υπάρχουν πριν αρχίσει
// να προσθέτει βιβλία, μέλη ή δανεισμούς ο χρήστης.
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    //Η URL για σύνδεση με τη βάση δεδομένων SQLite σε μορφή String
    private static final String URL = "jdbc:sqlite:library.db";


    public static Connection connect() throws SQLException {
        //Δημιουργεί τη σύνδεση με βάση τo String URL
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            //Ενεργοποιεί το foreign key constraint της SQL προκειμένου να μην υπάρξουν προβλήματα.
            //Σημαντικό για τον πίνακα loans
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        //Επιστρέφει τη σύνδεση
        return conn;
    }

    public static void initialize() {
        //Οι SQL εντολές για τη δημιουργία πινάκων
        String sqlBooks = """
            CREATE TABLE IF NOT EXISTS books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                isbn TEXT NOT NULL UNIQUE,
                genre TEXT,
                available INTEGER NOT NULL CHECK (available IN (0, 1))
            );
            """;

        String sqlMembers = """
            CREATE TABLE IF NOT EXISTS members (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                firstname TEXT NOT NULL,
                lastname TEXT NOT NULL,
                email TEXT UNIQUE,
                phone TEXT
            );
            """;

        String sqlLoans = """
            CREATE TABLE IF NOT EXISTS loans (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_id INTEGER NOT NULL,
                member_id INTEGER NOT NULL,
                loan_date TEXT NOT NULL,
                return_date TEXT,

                FOREIGN KEY (book_id)
                    REFERENCES books(id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE,

                FOREIGN KEY (member_id)
                    REFERENCES members(id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
            """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            //Δημιουργία πινάκων
            stmt.execute(sqlBooks);
            stmt.execute(sqlMembers);
            stmt.execute(sqlLoans);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

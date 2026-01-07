package dao;

import db.Database;
import model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    //ΣΗΜΑΝΤΙΚΟ: Τα PreparedStatement χρησιμοποιούνται για την αποφυγή SQL injection

    //Προσθέτει ένα νέο βιβλίο στη βάση δεδομένων
    public static void addBook(Book book) throws SQLException {

        String sql = """
            INSERT INTO books (title, author, isbn, genre, available)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Αντιστοίχιση τιμών από το αντικείμενο Book
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getGenre());
            ps.setInt(5, book.isAvailable() ? 1 : 0); // boolean → INTEGER

            ps.executeUpdate();
        }
    }


    // Επιστρέφει όλα τα βιβλία από τη βάση δεδομένων
    public static List<Book> getAllBooks() throws SQLException {

        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Μετατροπή κάθε γραμμής της βάσης σε αντικείμενο Book
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("genre"),
                        rs.getInt("available") == 1
                );
                books.add(book);
            }
        }
        return books;
    }

    // Αναζητά βιβλία με βάση τίτλο, συγγραφέα ή είδος
    public static List<Book> searchBooks(String keyword) throws SQLException {

        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT * FROM books
            WHERE title LIKE ? OR author LIKE ? OR genre LIKE ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("genre"),
                        rs.getInt("available") == 1
                ));
            }
        }
        return books;
    }

    // Ενημερώνει τα στοιχεία ενός βιβλίου
    public static void updateBook(Book book) throws SQLException {

        String sql = """
            UPDATE books
            SET title = ?, author = ?, isbn = ?, genre = ?, available = ?
            WHERE id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getGenre());
            ps.setInt(5, book.isAvailable() ? 1 : 0);
            ps.setInt(6, book.getId());

            ps.executeUpdate();
        }
    }

    /* ===================== ΔΙΑΓΡΑΦΗ ΒΙΒΛΙΟΥ ===================== */

    // Διαγράφει ένα βιβλίο με βάση το ID
    public static void deleteBook(int bookId) throws SQLException {

        // Check if book has active loans
        String checkLoansSql = """
        SELECT COUNT(*) as count FROM loans 
        WHERE book_id = ? AND return_date IS NULL
    """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(checkLoansSql)) {

            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt("count") > 0) {
                throw new SQLException("Cannot delete book: it has active loans. Please return the book first.");
            }
        }

        // If no active loans, safe to delete
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ps.executeUpdate();
        }
    }

    // Αλλάζει τη διαθεσιμότητα ενός βιβλίου
    public static void setAvailability(int bookId, boolean available) throws SQLException {

        String sql = "UPDATE books SET available = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, available ? 1 : 0);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }
}

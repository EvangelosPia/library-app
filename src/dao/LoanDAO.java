package dao;

import db.Database;
import model.Loans;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

   //Δημιουργεί νέο δανεισμό

    public static void addLoan(int bookId, int memberId) throws SQLException {
        String sql = """
            INSERT INTO loans (book_id, member_id, loan_date, return_date)
            VALUES (?, ?, ?, NULL)
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            ps.setString(3, LocalDate.now().toString()); // YYYY-MM-DD format

            ps.executeUpdate();

            // Ενημέρωση διαθεσιμότητας βιβλίου
            setBookAvailability(bookId, false);
        }
    }

  //Επιστρέφει όλους τους δανεισμούς με πληροφορίες βιβλίων και μελών

    public static List<Loans> getAllLoans() throws SQLException {
        List<Loans> loans = new ArrayList<>();
        String sql = """
            SELECT l.id, l.book_id, l.member_id, l.loan_date, l.return_date,
                   b.title AS book_title,
                   m.firstname || ' ' || m.lastname AS member_name
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            ORDER BY l.loan_date DESC
        """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LocalDate loanDate = rs.getString("loan_date") != null
                        ? LocalDate.parse(rs.getString("loan_date"))
                        : null;

                LocalDate returnDate = rs.getString("return_date") != null
                        ? LocalDate.parse(rs.getString("return_date"))
                        : null;

                Loans loan = new Loans(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        loanDate,
                        returnDate
                );
                loans.add(loan);
            }
        }
        return loans;
    }

    //Επιστρέφει τους ενεργούς δανεισμούς (που δεν έχουν επιστραφεί)

    public static List<Loans> getActiveLoans() throws SQLException {
        List<Loans> loans = new ArrayList<>();
        String sql = """
            SELECT l.id, l.book_id, l.member_id, l.loan_date, l.return_date,
                   b.title AS book_title,
                   m.firstname || ' ' || m.lastname AS member_name
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            WHERE l.return_date IS NULL
            ORDER BY l.loan_date DESC
        """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LocalDate loanDate = LocalDate.parse(rs.getString("loan_date"));

                Loans loan = new Loans(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        loanDate,
                        null // returnDate is NULL for active loans
                );
                loans.add(loan);
            }
        }
        return loans;
    }

    //Επιστροφή βιβλίου

    public static void returnBook(int loanId) throws SQLException {
        // Πρώτα βρες το book_id
        int bookId = getBookIdFromLoan(loanId);

        // Ενημέρωση του δανεισμού με return_date
        String sql = "UPDATE loans SET return_date = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, LocalDate.now().toString());
            ps.setInt(2, loanId);
            ps.executeUpdate();
        }

        // Ενημέρωση διαθεσιμότητας βιβλίου
        setBookAvailability(bookId, true);
    }

    //Αναζητά δανεισμούς με βάση keyword

    public static List<Loans> searchLoans(String keyword) throws SQLException {
        List<Loans> loans = new ArrayList<>();
        String sql = """
            SELECT l.id, l.book_id, l.member_id, l.loan_date, l.return_date,
                   b.title AS book_title,
                   m.firstname || ' ' || m.lastname AS member_name
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            WHERE b.title LIKE ? OR m.firstname LIKE ? OR m.lastname LIKE ?
            ORDER BY l.loan_date DESC
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";
            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LocalDate loanDate = rs.getString("loan_date") != null
                        ? LocalDate.parse(rs.getString("loan_date"))
                        : null;

                LocalDate returnDate = rs.getString("return_date") != null
                        ? LocalDate.parse(rs.getString("return_date"))
                        : null;

                loans.add(new Loans(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        loanDate,
                        returnDate
                ));
            }
        }
        return loans;
    }

    // ==================== HELPER METHODS ====================

    //Βρίσκει το book_id από loan_id

    private static int getBookIdFromLoan(int loanId) throws SQLException {
        String sql = "SELECT book_id FROM loans WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("book_id");
            }
        }
        throw new SQLException("Loan not found with id: " + loanId);
    }

   //Ενημέρωση διαθεσιμότητας βιβλίου

    private static void setBookAvailability(int bookId, boolean available) throws SQLException {
        String sql = "UPDATE books SET available = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, available ? 1 : 0);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }
}
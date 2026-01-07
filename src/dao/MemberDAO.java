package dao;

import db.Database;
import model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    //ΣΗΜΑΝΤΙΚΟ: Τα PreparedStatement χρησιμοποιούνται για την αποφυγή SQL injection

    //Προσθέτει ένα νέο βιβλίο στη βάση δεδομένων
    public static void addMember(Member member) throws SQLException {

        String sql = """
            INSERT INTO members (firstname, lastname, email, phone)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Αντιστοίχιση τιμών από το αντικείμενο Book
            ps.setString(1, member.getFirstname());
            ps.setString(2, member.getLastname());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPhone());

            ps.executeUpdate();
        }
    }


    // Επιστρέφει όλα τα βιβλία από τη βάση δεδομένων
    public static List<Member> getAllMembers() throws SQLException {

        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Μετατροπή κάθε γραμμής της βάσης σε αντικείμενο Book
            while (rs.next()) {
                Member member = new Member(


                        rs.getInt("id"),              // ← ADD THIS!
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("email"),
                        rs.getString("phone")

                );
                members.add(member);
            }
        }
        return members;
    }

    // Αναζητά βιβλία με βάση τίτλο, συγγραφέα ή είδος
    public static List<Member> searchMember(String keyword) throws SQLException {

        List<Member> members = new ArrayList<>();
        String sql = """
            SELECT * FROM members
            WHERE firstname LIKE ? OR lastname LIKE ? OR email LIKE ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                members.add(new Member(
                        rs.getInt("id"),              // ← ADD THIS!
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("email"),
                        rs.getString("phone")


                ));
            }
        }
        return members;
    }

    // Ενημερώνει τα στοιχεία ενός βιβλίου
    public static void updateMember(Member member) throws SQLException {

        String sql = """
            UPDATE members
            SET firstname = ?, lastname = ?, email = ?, phone = ?
            WHERE id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getFirstname());
            ps.setString(2, member.getLastname());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPhone());
            ps.setInt(5, member.getId());

            ps.executeUpdate();
        }
    }

    /* ===================== ΔΙΑΓΡΑΦΗ ΒΙΒΛΙΟΥ ===================== */

    // Διαγράφει ένα βιβλίο με βάση το ID
    public static void deleteMember(int memberId) throws SQLException {

        // ΠΡΩΤΑ: Λάβε όλους τους ενεργούς δανεισμούς για αυτό το μέλος και σημείωσε τα βιβλία ως διαθέσιμα
        String getLoansSql = """
    SELECT book_id FROM loans 
    WHERE member_id = ? AND return_date IS NULL
""";

        try (Connection conn = Database.connect();
             PreparedStatement psLoans = conn.prepareStatement(getLoansSql)) {

            psLoans.setInt(1, memberId);
            ResultSet rs = psLoans.executeQuery();

            // Ενημέρωσε όλα τα βιβλία από τους ενεργούς δανεισμούς πίσω σε διαθέσιμα
            String updateBookSql = "UPDATE books SET available = 1 WHERE id = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateBookSql)) {
                while (rs.next()) {
                    int bookId = rs.getInt("book_id");
                    psUpdate.setInt(1, bookId);
                    psUpdate.executeUpdate();
                }
            }
        }

        // ΜΕΤΑ: Διέγραψε το μέλος (το CASCADE θα διαγράψει τους δανεισμούς)
        String deleteSql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {

            ps.setInt(1, memberId);
            ps.executeUpdate();
        }
    }

}

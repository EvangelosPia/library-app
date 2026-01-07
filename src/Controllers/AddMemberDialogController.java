package Controllers;

import dao.MemberDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Member;

import java.sql.SQLException;

public class AddMemberDialogController {

    @FXML private TextField firstnameField;
    @FXML private TextField lastnameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Label messageLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Member memberToEdit; // Null για νέο μέλος, όχι null για επεξεργασία

    @FXML
    public void initialize() {
        // Σύνδεση των κουμπιών με τις μεθόδους
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());
    }

    //Χρησιμοποιείται για επεξεργασία υπάρχοντος βιβλίου
    //Γεμίζει τα πεδία με τα τρέχοντα δεδομένα

    public void setMemberToEdit(Member member) {
        this.memberToEdit = member;

        firstnameField.setText(member.getFirstname());
        lastnameField.setText(member.getLastname());
        emailField.setText(member.getEmail());
        phoneField.setText(member.getPhone());

    }

    //Αποθηκεύει το μέλος (νέο ή ενημερωμένο)

    private void handleSave() {
        // Έλεγχος εγκυρότητας
        if (!validateInput()) {
            return;
        }

        try {
            if (memberToEdit == null) {
                //Δημιουργία νέου βιβλίου
                Member newMember = new Member(
                        firstnameField.getText().trim(),
                        lastnameField.getText().trim(),
                        emailField.getText().trim(),
                        phoneField.getText().trim()

                );

                MemberDAO.addMember(newMember);
                showSuccess("✓ Member added successfully!");

                //Καθαρισμός των πεδίων για το επόμενο βιβλίο
                clearFields();

            } else {
                //Ενημέρωση υπάρχοντος βιβλίου
                memberToEdit.setFirstname(firstnameField.getText().trim());
                memberToEdit.setLastname(lastnameField.getText().trim());
                memberToEdit.setEmail(emailField.getText().trim());
                memberToEdit.setPhone(phoneField.getText().trim());

                MemberDAO.updateMember(memberToEdit);
                showSuccess("✓ Member updated successfully!");

                //Κλείσιμο του παραθύρου μετά από επεξεργασία
                closeDialog();
            }

        } catch (SQLException e) {
            // Έλεγχος για duplicate ISBN
            if (e.getMessage().contains("UNIQUE constraint failed") ||
                    e.getMessage().contains("SQLITE_CONSTRAINT")) {
                showError("⚠ A member with this email already exists!");
            } else {
                showError("Database error: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    //Ακυρώνει και κλείνει το παράθυρο

    private void handleCancel() {
        closeDialog();
    }

    //Επικυρώνει τα πεδία εισόδου

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (firstnameField.getText().trim().isEmpty()) {
            errors.append("First name is required.\n");
        }

        if (lastnameField.getText().trim().isEmpty()) {
            errors.append("Last name is required.\n");
        }

        if (emailField.getText().trim().isEmpty()) {
            errors.append("Email is required.\n");
        }

        if (phoneField.getText().trim().isEmpty()) {
            errors.append("Phone is required.\n");
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }

        messageLabel.setVisible(false);
        return true;
    }

    //Καθαρίζει όλα τα πεδία εισόδου

    private void clearFields() {
        firstnameField.clear();
        lastnameField.clear();
        emailField.clear();
        phoneField.clear();
        firstnameField.requestFocus(); // Επιστροφή focus στο πρώτο πεδίο
    }

    //Εμφανίζει μήνυμα λάθους

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setStyle("-fx-text-fill: white; -fx-background-color: #e74c3c; -fx-font-size: 12px; -fx-padding: 10px; -fx-background-radius: 5px;");
    }

    //Εμφανίζει μήνυμα επιτυχίας

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setStyle("-fx-text-fill: white; -fx-background-color: #27ae60; -fx-font-size: 13px; -fx-padding: 10px; -fx-background-radius: 5px; -fx-font-weight: bold;");
    }

    //Κλείνει το παράθυρο διαλόγου

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
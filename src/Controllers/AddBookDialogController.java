package Controllers;

import dao.BookDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Book;

import java.sql.SQLException;

public class AddBookDialogController {

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;
    @FXML private TextField genreField;
    @FXML private CheckBox availableCheckBox;
    @FXML private Label messageLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Book bookToEdit; // Null για νέο βιβλίο, όχι null για επεξεργασία

    @FXML
    public void initialize() {
        // Σύνδεση των κουμπιών με τις μεθόδους
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());
    }

    //Χρησιμοποιείται για επεξεργασία υπάρχοντος βιβλίου
    //Γεμίζει τα πεδία με τα τρέχοντα δεδομένα

    public void setBookToEdit(Book book) {
        this.bookToEdit = book;

        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        genreField.setText(book.getGenre());
        availableCheckBox.setSelected(book.isAvailable());
    }

    //Αποθηκεύει το βιβλίο (νέο ή ενημερωμένο)

    private void handleSave() {
        // Έλεγχος εγκυρότητας
        if (!validateInput()) {
            return;
        }

        try {
            if (bookToEdit == null) {
                //Δημιουργία νέου βιβλίου
                Book newBook = new Book(
                        titleField.getText().trim(),
                        authorField.getText().trim(),
                        isbnField.getText().trim(),
                        genreField.getText().trim(),
                        availableCheckBox.isSelected()
                );

                BookDAO.addBook(newBook);
                showSuccess("✓ Book added successfully!");

                //Καθαρισμός των πεδίων για το επόμενο βιβλίο
                clearFields();

            } else {
                //Ενημέρωση υπάρχοντος βιβλίου
                bookToEdit.setTitle(titleField.getText().trim());
                bookToEdit.setAuthor(authorField.getText().trim());
                bookToEdit.setIsbn(isbnField.getText().trim());
                bookToEdit.setGenre(genreField.getText().trim());
                bookToEdit.setAvailable(availableCheckBox.isSelected());

                BookDAO.updateBook(bookToEdit);
                showSuccess("✓ Book updated successfully!");

                //Κλείσιμο του παραθύρου μετά από επεξεργασία
                closeDialog();
            }

        } catch (SQLException e) {
            // Έλεγχος για duplicate ISBN
            if (e.getMessage().contains("UNIQUE constraint failed") ||
                    e.getMessage().contains("SQLITE_CONSTRAINT")) {
                showError("⚠ A book with this ISBN already exists!");
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

        if (titleField.getText().trim().isEmpty()) {
            errors.append("Title is required.\n");
        }

        if (authorField.getText().trim().isEmpty()) {
            errors.append("Author is required.\n");
        }

        if (isbnField.getText().trim().isEmpty()) {
            errors.append("ISBN is required.\n");
        }

        if (genreField.getText().trim().isEmpty()) {
            errors.append("Genre is required.\n");
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
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        genreField.clear();
        availableCheckBox.setSelected(true);
        titleField.requestFocus(); // Επιστροφή focus στο πρώτο πεδίο
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
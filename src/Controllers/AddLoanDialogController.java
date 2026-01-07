package Controllers;

import dao.BookDAO;
import dao.LoanDAO;
import dao.MemberDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Book;
import model.Member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AddLoanDialogController {

    @FXML private TextField bookSearchField;
    @FXML private ComboBox<Book> bookComboBox;
    @FXML private Label bookInfoLabel;

    @FXML private TextField memberSearchField;
    @FXML private ComboBox<Member> memberComboBox;

    @FXML private TextField loanDateField;
    @FXML private Label messageLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private ObservableList<Book> allAvailableBooks;
    private ObservableList<Member> allMembers;

    @FXML
    public void initialize() {
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        loanDateField.setText(LocalDate.now().format(formatter));

        loadAvailableBooks();
        loadMembers();

        setupBookComboBox();
        setupMemberComboBox();

        setupBookSearch();
        setupMemberSearch();
    }

    private void loadAvailableBooks() {
        try {
            List<Book> allBooks = BookDAO.getAllBooks();
            List<Book> availableBooks = allBooks.stream()
                    .filter(Book::isAvailable)
                    .collect(Collectors.toList());

            allAvailableBooks = FXCollections.observableArrayList(availableBooks);
            bookComboBox.setItems(allAvailableBooks);

            if (availableBooks.isEmpty()) {
                bookInfoLabel.setText("⚠ No available books to loan!");
                bookInfoLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 10px;");
                saveButton.setDisable(true);
            } else {
                bookInfoLabel.setText("✓ " + availableBooks.size() + " available books shown");
                bookInfoLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 10px;");
            }

        } catch (SQLException e) {
            showError("Failed to load books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMembers() {
        try {
            List<Member> members = MemberDAO.getAllMembers();
            allMembers = FXCollections.observableArrayList(members);
            memberComboBox.setItems(allMembers);

            if (members.isEmpty()) {
                showError("No members found! Please add members first.");
                saveButton.setDisable(true);
            }

        } catch (SQLException e) {
            showError("Failed to load members: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupBookComboBox() {
        bookComboBox.setConverter(new StringConverter<Book>() {
            @Override
            public String toString(Book book) {
                if (book == null) return "";
                return book.getId() + " - " + book.getTitle() + " by " + book.getAuthor();
            }

            @Override
            public Book fromString(String string) {
                return null;
            }
        });
    }

    private void setupMemberComboBox() {
        memberComboBox.setConverter(new StringConverter<Member>() {
            @Override
            public String toString(Member member) {
                if (member == null) return "";
                return member.getId() + " - " + member.getFirstname() + " " + member.getLastname();
            }

            @Override
            public Member fromString(String string) {
                return null;
            }
        });
    }

    private void setupBookSearch() {
        bookSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                bookComboBox.setItems(allAvailableBooks);
                return;
            }

            String searchTerm = newValue.toLowerCase().trim();
            ObservableList<Book> filteredBooks = allAvailableBooks.stream()
                    .filter(book ->
                            book.getTitle().toLowerCase().contains(searchTerm) ||
                                    book.getAuthor().toLowerCase().contains(searchTerm) ||
                                    book.getIsbn().toLowerCase().contains(searchTerm) ||
                                    String.valueOf(book.getId()).contains(searchTerm)
                    )
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            bookComboBox.setItems(filteredBooks);

            if (!filteredBooks.isEmpty() && !bookComboBox.isShowing()) {
                bookComboBox.show();
            }
        });
    }

    private void setupMemberSearch() {
        memberSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                memberComboBox.setItems(allMembers);
                return;
            }

            String searchTerm = newValue.toLowerCase().trim();
            ObservableList<Member> filteredMembers = allMembers.stream()
                    .filter(member ->
                            member.getFirstname().toLowerCase().contains(searchTerm) ||
                                    member.getLastname().toLowerCase().contains(searchTerm) ||
                                    member.getEmail().toLowerCase().contains(searchTerm) ||
                                    String.valueOf(member.getId()).contains(searchTerm)
                    )
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            memberComboBox.setItems(filteredMembers);

            if (!filteredMembers.isEmpty() && !memberComboBox.isShowing()) {
                memberComboBox.show();
            }
        });
    }

    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        Book selectedBook = bookComboBox.getValue();
        Member selectedMember = memberComboBox.getValue();

        try {
            LoanDAO.addLoan(selectedBook.getId(), selectedMember.getId());
            showSuccess("✓ Loan created successfully!");

            // Καθαρισμός πεδίων για επόμενο δανεισμό
            clearFields();

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (bookComboBox.getValue() == null) {
            errors.append("Please select a book.\n");
        }

        if (memberComboBox.getValue() == null) {
            errors.append("Please select a member.\n");
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }

        messageLabel.setVisible(false);
        return true;
    }

    private void clearFields() {
        bookSearchField.clear();
        memberSearchField.clear();
        bookComboBox.setValue(null);
        memberComboBox.setValue(null);

        // Επαναφόρτωση διαθέσιμων βιβλίων
        loadAvailableBooks();

        bookSearchField.requestFocus();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setStyle("-fx-text-fill: white; -fx-background-color: #e74c3c; -fx-font-size: 11px; -fx-padding: 8px; -fx-background-radius: 5px;");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setStyle("-fx-text-fill: white; -fx-background-color: #27ae60; -fx-font-size: 12px; -fx-padding: 8px; -fx-background-radius: 5px; -fx-font-weight: bold;");
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
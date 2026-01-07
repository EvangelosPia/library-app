package Controllers;

import dao.BookDAO;
import dao.LoanDAO;
import dao.MemberDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Book;
import model.Loans;
import model.Member;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LibraryController {

    // ==================== BOOKS TAB ====================
    @FXML private TextField searchBooksField;
    @FXML private Button searchBooksBtn;
    @FXML private Button addBookBtn;
    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, Integer> bookIdCol;
    @FXML private TableColumn<Book, String> bookTitleCol;
    @FXML private TableColumn<Book, String> bookAuthorCol;
    @FXML private TableColumn<Book, String> bookIsbnCol;
    @FXML private TableColumn<Book, String> bookGenreCol;
    @FXML private TableColumn<Book, Boolean> bookAvailableCol;
    @FXML private Button editBookBtn;
    @FXML private Button deleteBookBtn;

    // ==================== MEMBERS TAB ====================
    @FXML private TextField searchMembersField;
    @FXML private Button searchMembersBtn;
    @FXML private Button addMemberBtn;
    @FXML private TableView<Member> membersTable;
    @FXML private TableColumn<Member, Integer> memberIdCol;
    @FXML private TableColumn<Member, String> memberFirstnameCol;
    @FXML private TableColumn<Member, String> memberLastnameCol;
    @FXML private TableColumn<Member, String> memberEmailCol;
    @FXML private TableColumn<Member, String> memberPhoneCol;
    @FXML private Button editMemberBtn;
    @FXML private Button deleteMemberBtn;

    // ==================== LOANS TAB ====================
    @FXML private TextField searchLoansField;
    @FXML private Button searchLoansBtn;
    @FXML private Button newLoanBtn;
    @FXML private TableView<Loans> loansTable;
    @FXML private TableColumn<Loans, Integer> loanIdCol;
    @FXML private TableColumn<Loans, Integer> loanBookIdCol;
    @FXML private TableColumn<Loans, String> loanBookTitleCol;
    @FXML private TableColumn<Loans, Integer> loanMemberIdCol;
    @FXML private TableColumn<Loans, String> loanMemberNameCol;
    @FXML private TableColumn<Loans, LocalDate> loanDateCol;
    @FXML private TableColumn<Loans, LocalDate> returnDateCol;
    @FXML private Button returnBookBtn;
    @FXML private Button refreshBtn;  // ← CHANGED from viewLoanDetailsBtn

    // ==================== DASHBOARD TAB ====================
    @FXML private Label totalBooksLabel;
    @FXML private Label availableBooksLabel;
    @FXML private Label totalMembersLabel;
    @FXML private Label activeLoansLabel;
    @FXML private ListView<String> recentActivityList;

    // ==================== STATUS BAR ====================
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        setupBooksTable();
        setupMembersTable();
        setupLoansTable();
        setupEventHandlers();

        loadBooks();
        loadMembers();
        loadLoans();
        updateDashboardStats();
    }

    // ==================== SETUP METHODS ====================

    private void setupBooksTable() {
        bookIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookIsbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookGenreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        bookAvailableCol.setCellValueFactory(new PropertyValueFactory<>("available"));

        bookAvailableCol.setCellFactory(column -> new TableCell<Book, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Yes" : "No");
                    setStyle(item ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                            : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void setupMembersTable() {
        memberIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberFirstnameCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        memberLastnameCol.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        memberEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        memberPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void setupLoansTable() {
        loanIdCol.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        loanBookIdCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        loanBookTitleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        loanMemberIdCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        loanMemberNameCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        loanDateCol.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        returnDateCol.setCellFactory(column -> new TableCell<Loans, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else if (item == null) {
                    setText("Active");
                    setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: #27ae60;");
                }
            }
        });
    }

    private void setupEventHandlers() {
        // Books
        addBookBtn.setOnAction(event -> handleAddBook());
        editBookBtn.setOnAction(event -> handleEditBook());
        deleteBookBtn.setOnAction(event -> handleDeleteBook());
        searchBooksBtn.setOnAction(event -> handleSearchBooks());
        searchBooksField.setOnAction(event -> handleSearchBooks());

        // Members
        addMemberBtn.setOnAction(event -> handleAddMember());
        editMemberBtn.setOnAction(event -> handleEditMember());
        deleteMemberBtn.setOnAction(event -> handleDeleteMember());
        searchMembersBtn.setOnAction(event -> handleSearchMembers());
        searchMembersField.setOnAction(event -> handleSearchMembers());

        // Loans
        newLoanBtn.setOnAction(event -> handleNewLoan());
        returnBookBtn.setOnAction(event -> handleReturnBook());
        searchLoansBtn.setOnAction(event -> handleSearchLoans());
        searchLoansField.setOnAction(event -> handleSearchLoans());
        refreshBtn.setOnAction(event -> handleRefresh());  // ← ADDED
    }

    // ==================== LOAD DATA METHODS ====================

    private void loadBooks() {
        try {
            List<Book> books = BookDAO.getAllBooks();
            booksTable.setItems(FXCollections.observableArrayList(books));
            updateStatus("Loaded " + books.size() + " books");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load books: " + e.getMessage());
            updateStatus("Error loading books");
            e.printStackTrace();
        }
    }

    private void loadMembers() {
        try {
            List<Member> members = MemberDAO.getAllMembers();
            membersTable.setItems(FXCollections.observableArrayList(members));
            updateStatus("Loaded " + members.size() + " members");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load members: " + e.getMessage());
            updateStatus("Error loading members");
            e.printStackTrace();
        }
    }

    private void loadLoans() {
        try {
            List<Loans> loans = LoanDAO.getAllLoans();
            loansTable.setItems(FXCollections.observableArrayList(loans));
            updateStatus("Loaded " + loans.size() + " loans");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load loans: " + e.getMessage());
            updateStatus("Error loading loans");
            e.printStackTrace();
        }
    }

    // ==================== BOOK HANDLERS ====================

    public void handleAddBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/newScenes/AddBook.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Book");
            Image logo = new Image("/ui/images/LibraryLogo.png");
            dialogStage.getIcons().add(logo);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addBookBtn.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            dialogStage.setOnHidden(event -> {
                loadBooks();
                updateDashboardStats();
            });

            dialogStage.show();
            updateStatus("Add Book dialog opened");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleEditBook() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/newScenes/AddBook.fxml"));
            Parent root = loader.load();

            AddBookDialogController controller = loader.getController();
            controller.setBookToEdit(selectedBook);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Book");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(editBookBtn.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            dialogStage.setOnHidden(event -> {
                loadBooks();
                updateDashboardStats();
            });

            dialogStage.show();
            updateStatus("Edit Book dialog opened");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleDeleteBook() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Book");
        confirmation.setContentText("Are you sure you want to delete:\n\n\"" +
                selectedBook.getTitle() + "\" by " + selectedBook.getAuthor() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                BookDAO.deleteBook(selectedBook.getId());
                loadBooks();
                updateDashboardStats();
                updateStatus("Book deleted: " + selectedBook.getTitle());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete book: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void handleSearchBooks() {
        String keyword = searchBooksField.getText().trim();

        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }

        try {
            List<Book> results = BookDAO.searchBooks(keyword);
            booksTable.setItems(FXCollections.observableArrayList(results));
            updateStatus("Found " + results.size() + " books matching \"" + keyword + "\"");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Search failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== MEMBER HANDLERS ====================

    public void handleAddMember() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/newScenes/AddMember.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Member");
            Image logo = new Image("/ui/images/LibraryLogo.png");
            dialogStage.getIcons().add(logo);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addMemberBtn.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            dialogStage.setOnHidden(event -> {
                loadMembers();
                updateDashboardStats();
            });

            dialogStage.show();
            updateStatus("Add Member dialog opened");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleEditMember() {
        Member selectedMember = membersTable.getSelectionModel().getSelectedItem();

        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a member to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/newScenes/AddMember.fxml"));
            Parent root = loader.load();

            AddMemberDialogController controller = loader.getController();
            controller.setMemberToEdit(selectedMember);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Member");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(editMemberBtn.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            dialogStage.setOnHidden(event -> {
                loadMembers();
                updateDashboardStats();
            });

            dialogStage.show();
            updateStatus("Edit Member dialog opened");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleDeleteMember() {
        Member selectedMember = membersTable.getSelectionModel().getSelectedItem();

        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a member to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Member");
        confirmation.setContentText("Are you sure you want to delete:\n\n" +
                selectedMember.getFirstname() + " " + selectedMember.getLastname() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                MemberDAO.deleteMember(selectedMember.getId());
                loadMembers();
                loadLoans();  // ← ADDED: Refresh loans too since member was deleted
                updateDashboardStats();
                updateStatus("Member deleted: " + selectedMember.getFirstname() + " " + selectedMember.getLastname());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member deleted successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete member: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void handleSearchMembers() {
        String keyword = searchMembersField.getText().trim();

        if (keyword.isEmpty()) {
            loadMembers();
            return;
        }

        try {
            List<Member> results = MemberDAO.searchMember(keyword);
            membersTable.setItems(FXCollections.observableArrayList(results));
            updateStatus("Found " + results.size() + " members matching \"" + keyword + "\"");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Search failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== LOAN HANDLERS ====================

    public void handleNewLoan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/newScenes/AddLoan.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Loan");
            Image logo = new Image("/ui/images/LibraryLogo.png");
            dialogStage.getIcons().add(logo);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(newLoanBtn.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            dialogStage.setOnHidden(event -> {
                loadLoans();
                loadBooks();
                updateDashboardStats();
            });

            dialogStage.show();
            updateStatus("New Loan dialog opened");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleReturnBook() {
        Loans selectedLoan = loansTable.getSelectionModel().getSelectedItem();

        if (selectedLoan == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a loan to return.");
            return;
        }

        if (selectedLoan.isReturned()) {
            showAlert(Alert.AlertType.INFORMATION, "Already Returned",
                    "This book has already been returned on " + selectedLoan.getReturnDate());
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Return");
        confirmation.setHeaderText("Return Book");
        confirmation.setContentText("Mark this loan as returned?\n\n" +
                "Book: " + selectedLoan.getBookTitle() + "\n" +
                "Member: " + selectedLoan.getMemberName());

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                LoanDAO.returnBook(selectedLoan.getLoanId());
                loadLoans();
                loadBooks();
                updateDashboardStats();
                updateStatus("Book returned: " + selectedLoan.getBookTitle());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to return book: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void handleSearchLoans() {
        String keyword = searchLoansField.getText().trim();

        if (keyword.isEmpty()) {
            loadLoans();
            return;
        }

        try {
            List<Loans> results = LoanDAO.searchLoans(keyword);
            loansTable.setItems(FXCollections.observableArrayList(results));
            updateStatus("Found " + results.size() + " loans matching \"" + keyword + "\"");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Search failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== REFRESH HANDLER ====================

    public void handleRefresh() {
        loadBooks();
        loadMembers();
        loadLoans();
        updateDashboardStats();
        updateStatus("✓ All data refreshed");
    }

    // ==================== DASHBOARD ====================

    private void updateDashboardStats() {
        try {
            List<Book> allBooks = BookDAO.getAllBooks();
            List<Member> allMembers = MemberDAO.getAllMembers();
            List<Loans> activeLoans = LoanDAO.getActiveLoans();

            int totalBooks = allBooks.size();
            int availableBooks = (int) allBooks.stream().filter(Book::isAvailable).count();
            int totalMembers = allMembers.size();

            totalBooksLabel.setText(String.valueOf(totalBooks));
            availableBooksLabel.setText(String.valueOf(availableBooks));
            totalMembersLabel.setText(String.valueOf(totalMembers));
            activeLoansLabel.setText(String.valueOf(activeLoans.size()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== UTILITY METHODS ====================

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
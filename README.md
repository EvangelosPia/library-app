~ Library Management System

A desktop application built with JavaFX for managing books, members, and loans in a library. This system provides a clean and intuitive interface for librarians
to handle daily operations efficiently. This is a semester project done for the class : PROGRAMMING LANGUAGE III (JAVA9/JAVAFX) PROGRAMMING OF GRAPHICAL USER
INTERFACE APPLICATIONS


## IMPORTANT 
The logo is a free design from canva and it is used for aesthetic purposes, not commercial use

## Features

- **Book Management**: Add, edit, delete, and search books with detailed information (title, author, ISBN, genre, availability)
- **Member Management**: Maintain a database of library members with contact information
- **Loan Tracking**: Create and manage book loans, track due dates, and process returns
- **Dashboard**: View real-time statistics including total books, available books, total members, and active loans
- **Search Functionality**: Quick search across books, members, and loans
- **Data Persistence**: All data stored locally using SQLite database

## Screenshots

Main view of the desktop application with the option to view
<img width="1579" height="984" alt="LibraryApp" src="https://github.com/user-attachments/assets/5c981287-7977-4ec2-8e6d-0e071a2cb41a" />

View of the app after the user pressed the "Add Book" Button

<img width="554" height="610" alt="AddBook" src="https://github.com/user-attachments/assets/e1006b21-220c-407f-858b-ab17daba7067" />

View of the Dashboard
<img width="1569" height="984" alt="DashView" src="https://github.com/user-attachments/assets/5fbb61d1-ef86-49dd-89a1-3c9d8f1491a3" />




##  Technologies Used

- **JavaFX 25.0.1**: UI framework
- **SQLite**: Embedded database
- **JDBC**: Database connectivity
- **FXML**: UI layout definitions
- **CSS**: Custom styling

##  Prerequisites

Before running this application, ensure you have:

1. **Java Development Kit (JDK) 17 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

2. **JavaFX SDK 25.0.1 REQUIRED**
   - Download from [Gluon](https://gluonhq.com/products/javafx/)
   - Extract to a location on your system (e.g., `C:\javafx-sdk-25.0.1`)
   - **IMPORTANT**: JavaFX is NOT included automatically with the JDK. You must download and install the SDK separately!

3. **SQLite JDBC Driver**
   - Already included in the `lib` folder (`sqlite-jdbc-3.51.1.0.jar`)

##  Project Structure

```
LibraryApp/
│
├── src/
│   ├── App/
│   │   └── Main.java                      # Application entry point
│   │
│   ├── Controllers/
│   │   ├── AddBookDialogController.java   # Book add/edit dialog controller
│   │   ├── AddMemberDialogController.java # Member add/edit dialog controller
│   │   ├── AddLoanDialogController.java   # Loan creation dialog controller
│   │   └── LibraryController.java         # Main application controller
│   │
│   ├── dao/
│   │   ├── BookDAO.java                   # Book data access object
│   │   ├── MemberDAO.java                 # Member data access object
│   │   └── LoanDAO.java                   # Loan data access object
│   │
│   ├── db/
│   │   └── Database.java                  # Database connection manager
│   │
│   ├── model/
│   │   ├── Book.java                      # Book entity
│   │   ├── Member.java                    # Member entity
│   │   └── Loans.java                     # Loan entity
│   │
│   └── ui/
│       ├── library.fxml                   # Main FXML layout file
│       ├── style.css                      # Main stylesheet
│       └── images/                        # App images
│       └── newScenes/                     # Secondary FXML layout files(for popup windows) and their stylesheets
│
├── lib/
│   └── sqlite-jdbc-3.51.1.0.jar          # SQLite JDBC driver
│
└── library.db                             # SQLite database (created on first run)
```

##  Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/library-app.git
cd library-app
```

### 2. Configure JavaFX

#### Option A: Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Go to **File → Project Structure → Libraries**
3. Click **+** and select **Java**
4. Navigate to your JavaFX SDK `lib` folder and select all JAR files
5. Click **OK**

#### Option B: Using Eclipse

1. Right-click project → **Build Path → Configure Build Path**
2. Go to **Libraries** tab
3. Click **Add External JARs**
4. Navigate to your JavaFX SDK `lib` folder and select all JAR files
5. Click **Apply and Close**

### 3. Configure VM Options 

**WARNING**: Without this step, the application will NOT run!

Add the following VM arguments to your run configuration:

```
--module-path "PATH_TO_JAVAFX_SDK/lib" --add-modules javafx.controls,javafx.fxml
```

Replace `PATH_TO_JAVAFX_SDK` with your actual JavaFX SDK path.

**Example for Windows:**
```
--module-path "C:\javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml
```

**Example for Mac/Linux:**
```
--module-path "/path/to/javafx-sdk-25.0.1/lib" --add-modules javafx.controls,javafx.fxml
```

#### Instructions for IntelliJ IDEA:
1. Go to **Run → Edit Configurations**
2. Select your Main class
3. Add the VM options in the **VM options** field
4. Click **Apply** and **OK**

#### Instructions for Eclipse:
1. Go to **Run → Run Configurations**
2. Select your Main class
3. Go to **Arguments** tab
4. Add to **VM arguments** field
5. Click **Apply** and **Run**

### 4. Run the Application

Run `Main.java` from the `App` package.

### Managing Books

1. Navigate to the **Books** tab
2. Click **+ Add Book** to add a new book
3. Select a book and click **Edit** to modify its details
4. Select a book and click **Delete** to remove it
5. Use the search bar to find specific books
6. Click **Refresh** to reload the book list

### Managing Members

1. Navigate to the **Members** tab
2. Click **+ Add Member** to register a new member
3. Select a member and click **Edit** to update their information
4. Select a member and click **Delete** to remove them
5. Use the search bar to find specific members

### Managing Loans

1. Navigate to the **Loans** tab
2. Click **+ New Loan** to create a loan
3. Select the book and member from dropdown lists
4. Click **Create Loan**
5. To return a book, select the loan and click **Return Book**
6. Use the search bar to find specific loans

### Dashboard

View real-time statistics on the **Dashboard** tab:
- Total number of books
- Available books
- Total members
- Active loans

## Database Schema

The application uses three main tables:

### Books Table
- `id` (INTEGER, PRIMARY KEY)
- `title` (TEXT)
- `author` (TEXT)
- `isbn` (TEXT, UNIQUE)
- `genre` (TEXT)
- `available` (BOOLEAN)

### Members Table
- `id` (INTEGER, PRIMARY KEY)
- `firstname` (TEXT)
- `lastname` (TEXT)
- `email` (TEXT)
- `phone` (TEXT)

### Loans Table
- `loan_id` (INTEGER, PRIMARY KEY)
- `book_id` (INTEGER, FOREIGN KEY)
- `member_id` (INTEGER, FOREIGN KEY)
- `loan_date` (DATE)
- `return_date` (DATE)

## Troubleshooting

### "Error: JavaFX runtime components are missing"

This error means JavaFX is not configured correctly. Make sure you:
1. Have downloaded the JavaFX SDK from Gluon
2. Have added the VM options with the correct path to JavaFX SDK
3. The path in VM options is correct and points to the `lib` folder of JavaFX

**Solution:**
```
--module-path "C:\CORRECT_PATH\javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml
```

### "java.sql.SQLException: no such table"

The database will be created automatically on first run. If you encounter this error, delete `library.db` and restart the application.

### UI not loading correctly

Ensure all FXML files and CSS files are in the correct `ui/` folder and the paths in the code match the file locations.

### Application doesn't start at all

1. Check that JDK is properly installed
2. Check that VM options have been added to the run configuration
3. Check that the path in VM options is correct
4. Make sure the SQLite JDBC driver is in the `lib/` folder

##  License

This project is licensed under the MIT License - see the LICENSE file for details.

##  Authors

- Evangelos Pia - *Initial work* - https://github.com/EvangelosPia

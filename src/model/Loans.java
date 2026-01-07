package model;

import java.time.LocalDate;

public class Loans {

    private int loanId;
    private int bookId;
    private String bookTitle;
    private int memberId;
    private String memberName;
    private LocalDate loanDate;
    private LocalDate returnDate;

    //Constructor για την δημιουργία δανείων στην database
    public Loans(int loanId, int bookId, String bookTitle, int memberId, String memberName, LocalDate loanDate, LocalDate returnDate) {

        this.loanId = loanId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.memberId = memberId;
        this.memberName = memberName;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    //Getters και Setters
    public int getLoanId() {
        return loanId;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }
}

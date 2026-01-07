package model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private boolean  available;

    //Constructor για την δημιουργία βιβλίων στην database
    public Book(String title,String author,String isbn,String genre,boolean available){
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.available = available;
    }
    //Constructor για την εμφάνιση των βιβλίων από την database
    public Book(int id,String title,String author,String isbn,String genre,boolean available){
        this.id=id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.available = available;
    }

    //Getters και Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title=title;
    }


    public String getAuthor(){
        return author;
    }
    public void setAuthor(String author){
        this.author=author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn){
        this.isbn=isbn;
    }


    public String getGenre(){
        return genre;
    }
    public void setGenre(String genre){
        this.genre=genre;
    }

    public boolean isAvailable() {
        return available;
    }


    public void setAvailable(boolean available){
        this.available=available;
    }


    }



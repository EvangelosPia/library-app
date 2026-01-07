package model;

public class Member {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;

    //Constructor για την δημιουργία μελών στην database
    public Member(String firstname,String lastname,String email,String phone){
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;

    }
    //Constructor για την εμφάνιση των μελών από την database
    public Member(int id,String firstname,String lastname,String email,String phone){
        this.id=id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;

    }

    //Getters και Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname(){
        return firstname;
    }

    public void setFirstname(String firstname){
        this.firstname=firstname;
    }


    public String getLastname(){
        return lastname;
    }
    public void setLastname(String lastname){
        this.lastname=lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email){
        this.email=email;
    }


    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
}



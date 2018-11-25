package it_geeks.info.gawla_app.Models;

public class User {

    private String name;

    private String email;

    private String membership;

    private String password;

    private String password_confirmation;

    public User(String name, String email, String membership, String password, String password_confirmation) {
        this.name = name;
        this.email = email;
        this.membership = membership;
        this.password = password;
        this.password_confirmation = password_confirmation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }

    public void setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
    }
}

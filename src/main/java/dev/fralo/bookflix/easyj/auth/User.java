package dev.fralo.bookflix.easyj.orm;

import dev.fralo.bookflix.easyj.annotations.orm.Column;
import dev.fralo.bookflix.easyj.annotations.orm.Table;

@Table(name = "users")
public class User extends Model {

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    // Constructors
    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters/Setters
    public int getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
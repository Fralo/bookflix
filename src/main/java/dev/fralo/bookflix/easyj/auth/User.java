package dev.fralo.bookflix.easyj.auth;

import dev.fralo.bookflix.easyj.annotations.orm.Column;
import dev.fralo.bookflix.easyj.annotations.orm.Table;
import dev.fralo.bookflix.easyj.orm.Model;

@Table(name = "users")
public class User extends Model {

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

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
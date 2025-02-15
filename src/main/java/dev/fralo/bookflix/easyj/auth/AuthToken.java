package dev.fralo.bookflix.easyj.auth;

import dev.fralo.bookflix.easyj.annotations.orm.Column;
import dev.fralo.bookflix.easyj.annotations.orm.Table;
import dev.fralo.bookflix.easyj.orm.Model;
import dev.fralo.bookflix.easyj.orm.User;
import dev.fralo.bookflix.easyj.utils.RandomStringGenerator;

@Table(name = "auth_tokens")
public class AuthToken extends Model {

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "value")
    private String value;

    public AuthToken() {}

    public AuthToken(User user) {
        this.userId = user.getId();
        this.value = RandomStringGenerator.generateRandomString(64);
    }

    public AuthToken(int userId) {
        this.userId = userId;
        this.value = RandomStringGenerator.generateRandomString(64);
    }

    // Getters/Setters
    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public int getUserId() {
        return userId;
    }
}
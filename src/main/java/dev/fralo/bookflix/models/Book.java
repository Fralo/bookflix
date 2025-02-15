package dev.fralo.bookflix.models;

import dev.fralo.bookflix.easyj.annotations.orm.Column;
import dev.fralo.bookflix.easyj.annotations.orm.Table;
import dev.fralo.bookflix.easyj.orm.Model;

@Table(name = "books")
public class Book extends Model {

    @Column(name = "user_id")
    private int user_id;
    
    @Column(name="author")
    private String author;
    
    @Column(name="title")
    private String title;
    
    @Column(name="completed")
    private boolean completed;

    public Book() {}

    public Book(int user_id, String author, String title) {
        this.user_id = user_id;
        this.author = author;
        this.title = title;
        this.completed = false;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
}

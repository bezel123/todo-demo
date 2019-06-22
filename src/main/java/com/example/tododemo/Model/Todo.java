package com.example.tododemo.Model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "todos")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String title;
    private String description;
    private java.sql.Date dueDate;
    private boolean done;

    public Todo(String title, String description, Date dueDate, boolean done) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(checkTitleSize(title)){
            this.title = title;
        }
        //TODO: add error
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(checkDescSize(description)){
            this.description = description;
        }
        //TODO: add error
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    private boolean checkTitleSize(String s) {
        return s.length() >= 1 && s.length() <= 30;
    }

    private boolean checkDescSize(String s) {
        return s.length() >= 0 && s.length() <= 500;
    }

}
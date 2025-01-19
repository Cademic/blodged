package com.gcu.data.entities;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("USERS")
public class UserEntity {

    @Id
    long id = 0;

    @Column("password")
    String password;

    @Column("email")
    String email;

    @Column("username")
    String username;

    @Column("created_date")
    Timestamp createdDate = new Timestamp(System.currentTimeMillis());

    @Column("bio")
    String bio = null;

    @Column("is_private")
    boolean isPrivate = false;

    public UserEntity(String password, String email, String username) {
        this.password = password;
        this.email = email;
        this.username = username;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /*public UserEntity(long id, String password, String email, String username) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.username = username;
    }*/
    
}

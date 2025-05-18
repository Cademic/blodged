package com.gcu.data.entities;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
/**
 * User model entity to interract with the databse
 */
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


    @Column("gravatar_email")
    String gravatarEmail = null;

    public UserEntity(String password, String email, String username) {
        this.password = password;
        this.email = email;
        this.username = username;
    }
/**
 * getter for createdDate
 * @return the date that the user was created
 */
    public Timestamp getCreatedDate() {
        return createdDate;
    }
/**
 * setter for createdDate
 * @param createdDate the new date that the user was created
 */
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
/**
 * getter for bio
 * @return the bio of the user
 */
    public String getBio() {
        return bio;
    }
/**
 * setter for bio
 * @param bio the new bio for the user
 */
    public void setBio(String bio) {
        this.bio = bio;
    }
/**
 * getter for isPrivate
 * @return whether the user is private or not
 */
    public boolean isPrivate() {
        return isPrivate;
    }
/**
 * setter for isPrivate
 * @param isPrivate a boolean on whether the user is private or not
 */
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
/**
 * getter for id
 * @return the id of the user
 */
    public long getId() {
        return id;
    }
/**
 * setter for id
 * @param id the new id of the user
 */
    public void setId(long id) {
        this.id = id;
    }
/**
 * getter for password
 * @return the password for the user
 */
    public String getPassword() {
        return password;
    }
/**
 * setter for password
 * @param password the new password for the user
 */
    public void setPassword(String password) {
        this.password = password;
    }
/**
 * getter for email
 * @return the email of the user 
 */
    public String getEmail() {
        return email;
    }
/**
 * setter for email
 * @param email the new email for the user
 */
    public void setEmail(String email) {
        this.email = email;
    }
/**
 * getter for username
 * @return the username of the user
 */
    public String getUsername() {
        return username;
    }
/**
 * setter for username
 * @param username the new username for the user
 */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * get the gravatar email of the user
     * @return email address
     */
    public String getGravatarEmail() {
        return gravatarEmail;
    }

    /**
     * set the gravatar email
     * @param gravatarEmail the email address
     */
    public void setGravatarEmail(String gravatarEmail) {
        this.gravatarEmail = gravatarEmail;
    }


    /*public UserEntity(long id, String password, String email, String username) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.username = username;
    }*/
    
}

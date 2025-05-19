package com.blodged.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.blodged.data.service.UserBusinessService;
import com.blodged.model.db.UserModel;

import jakarta.annotation.PostConstruct;
/**
 * A class that provides some methods to handle accounts
 */
@Repository
public class AccountService {

    public enum ExistsResult {
        DOES_NOT("This account does not exist!"),
        USERNAME_MATCH("This username is already in use!"),
        EMAIL_MATCH("This email is already in use!");

        public String errorMessage;
        ExistsResult(String errorMessage) {
            this.errorMessage=errorMessage;
        }

    }

    record UserSearchResults(boolean found, UserModel user){}

    private static List<UserModel> users = new ArrayList<>();
    private @Autowired UserBusinessService userBusinessService;

    @PostConstruct
    public void initialize() {
        users.addAll(this.userBusinessService.getAllUsers());
    }

    /**
     * A method to get a user based on their email
     * @param email the email of the user you want to get
     * @return UserSearchResults contains a boolean of if the user is found, and then the user. NULL if no user.
     */
    public UserSearchResults getUser(String email) {
        List<UserModel> results = users.stream().filter(u -> u.getEmail().equals(email)).toList();
        if (!results.isEmpty()) { // should only ever be one
            return new UserSearchResults(true, results.get(0)) ;
        } else {
            return new UserSearchResults(false, null);
        }
    }
    /**
     * A method to check if a user exists
     * @param newUser A UserModel of the user to check if it exists
     * @return ExistsResult returns either EMAIL_MATCH, USERNAME_MATCH, or DOES_NOT based on whether the user already exists
     */
    public ExistsResult exists(UserModel newUser) {
        if (users.stream().anyMatch(u -> u.getEmail().equals(newUser.getEmail()))) {
            return ExistsResult.EMAIL_MATCH;
        } else if (users.stream().anyMatch(u -> u.getUsername().equals(newUser.getUsername()))){
            return ExistsResult.USERNAME_MATCH;
        } else {
            return ExistsResult.DOES_NOT;
        }
    }
    /**
     * Adds a new user to the memory, does not save to the database.
     * @param newUser a UserModel of the user you want to add
     */
    public void add(UserModel newUser) {
        users.add(newUser);
    }
    /**
     * Removes a user from memory based on userId. Does not delete from the database.
     * @param userId the ID of the user you want to delete.
     */
    public void remove(long userId) {
        users.remove((int) users.stream().filter(u -> u.getId() == userId).map(u -> users.indexOf(u)).findFirst().get());
    }

}

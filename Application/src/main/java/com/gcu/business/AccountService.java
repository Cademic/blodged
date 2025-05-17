package com.gcu.business;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gcu.data.service.UserBusinessService;
import com.gcu.model.db.UserModel;

import jakarta.annotation.PostConstruct;

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

    public UserSearchResults getUser(String email) {
        List<UserModel> results = users.stream().filter(u -> u.getEmail().equals(email)).toList();
        if (!results.isEmpty()) { // should only ever be one
            return new UserSearchResults(true, results.get(0)) ;
        } else {
            return new UserSearchResults(false, null);
        }
    }
    public ExistsResult exists(UserModel newUser) {
        if (users.stream().anyMatch(u -> u.getEmail().equals(newUser.getEmail()))) {
            return ExistsResult.EMAIL_MATCH;
        } else if (users.stream().anyMatch(u -> u.getUsername().equals(newUser.getUsername()))){
            return ExistsResult.USERNAME_MATCH;
        } else {
            return ExistsResult.DOES_NOT;
        }
    }
    public void add(UserModel newUser) {
        users.add(newUser);
    }
    public void remove(long userId) {
        users.remove((int) users.stream().filter(u -> u.getId() == userId).map(u -> users.indexOf(u)).findFirst().get());
    }

}

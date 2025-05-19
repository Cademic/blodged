package com.blodged.business;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.blodged.business.AccountService.UserSearchResults;
import com.blodged.data.service.UserBusinessService;
import com.blodged.model.db.PostModel;
import com.blodged.model.db.UserModel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * manages in-app authentication for every user using a session scope
 */
@Service
@SessionScope
public class AuthService {



    private @Autowired AccountService accountService;
    private @Autowired UserBusinessService userBusinessService;
    private @Autowired AuthenticationManager authenticationManager;
    private @Autowired PasswordEncoder passwordEncoder;

    /**
     * the status of the completion of the login for a user
     */
    public enum AuthStatus {
        SUCCESS("Login Success! This is not an error"),
        INCORRECT_PASSWORD("Incorrect password for {{email}}"),
        DOESNT_EXIST("{{email}} does not exist!"),
        NOT_LOGGED_IN("You are not logged in!");

        public String errorMessage;
        AuthStatus(String errorMessage) {
            this.errorMessage=errorMessage;
        }
    }

    private AuthStatus status = AuthStatus.NOT_LOGGED_IN;
    private UserModel loggedInUser;
    public boolean hasLoggedIn;
    public boolean hasClearedSession;

    /**
     * complete the login by binding a spring security user to our user model
     * @param u spring security login info
     */
    public void completeLogin(UserDetails u) {
        UserModel um = userBusinessService.getByEmail(u.getUsername());
        if (um != null) {
            hasLoggedIn = true;
            status = AuthStatus.SUCCESS;
            loggedInUser = um;
        }
    }

    /**
     * login using spring security manually by skipping the form login step
     * @param email the email of the user to log in
     * @param password the password of the user to log in
     * @param req the latest HttpServletRequest of the user that wants to log in
     * @return true if successful, false if not
     */
    public boolean manualLoginSpringSecurity(String email, String password, HttpServletRequest req) {
        UsernamePasswordAuthenticationToken tkn = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authenticationManager.authenticate(tkn);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession sess = req.getSession(true);
        sess.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) return true;
        return false;
    }

    /**
     * register a new user
     * @param username the username to register
     * @param email the email to register
     * @param password the password to register
     * @param req the latest request of the session that wants to register
     * @return true if successful, false if not
     */
    public boolean register(String username, String email, String password, HttpServletRequest req) {
        UserModel m = userBusinessService.create(username, passwordEncoder.encode(password), email);
        if (m!= null) {
            /*loggedInUser = m;
            accountService.add(m);*/
            boolean res = manualLoginSpringSecurity(email, password, req);
            if (res) {
                loggedInUser = m;
                status = AuthStatus.SUCCESS;
                accountService.add(m);
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * checks if the user is logged in on both our service and spring security
     * @return true if logged in, false if not
     */
    public boolean isLoggedIn() {
        boolean ssLoggedIn = SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null;
        return (loggedInUser != null) && status == AuthStatus.SUCCESS && ssLoggedIn;
    }

    /**
     * get the email of the logged-in user
     * @return the email
     */
    public String getEmail() {
        return loggedInUser.getEmail();
    }

    /**
     * log the user out via our service
     * after logging out via our service, it will log them out using spring security
     */
    public void logout() {
        status = AuthStatus.NOT_LOGGED_IN;
        loggedInUser = null;
        springSecurityLogout(false, null);
    }

    /**
     * log the user out with the context of an HttpServletRequest, which is more comprehensive
     * @param request the latest request of the user that wants to log out
     */
    public void logout(HttpServletRequest request) {
        status = AuthStatus.NOT_LOGGED_IN;
        loggedInUser = null;
        springSecurityLogout(true, request);
    }

    /**
     * log the user out using spring security
     * @param dropSession whether to destroy the user's session
     * @param request the latest request of the user that wants to log out
     */
    private void springSecurityLogout(boolean dropSession, HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        if (dropSession && request != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
    }

    /**
     * get the currently logged-in user
     * @return UserModel of the currently logged-in user
     */
    public UserModel getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * set the currently logged-in user
     * @param m the user to log in
     */
    public void setLoggedInUser(UserModel m) {
        this.loggedInUser = m;
    }
}

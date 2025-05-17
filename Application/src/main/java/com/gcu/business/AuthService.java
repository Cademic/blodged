package com.gcu.business;

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

import com.gcu.business.AccountService.UserSearchResults;
import com.gcu.data.service.UserBusinessService;
import com.gcu.model.db.PostModel;
import com.gcu.model.db.UserModel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.List;

@Service
@SessionScope
public class AuthService {



    private @Autowired AccountService accountService;
    private @Autowired UserBusinessService userBusinessService;
    private @Autowired AuthenticationManager authenticationManager;
    private @Autowired PasswordEncoder passwordEncoder;

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

    // TODO to be removed by spring security
    public AuthStatus authenticate(String email, String password) {
        // if (SELECT * FROM USERS)...
        UserSearchResults res = accountService.getUser(email);
        if (res.found() && password.equals(res.user().getPassword())) {
            status = AuthStatus.SUCCESS;
            loggedInUser = res.user();
            return status;
        } else if (res.found() && !res.user().getPassword().equals(password)) {
            status = AuthStatus.INCORRECT_PASSWORD;
            return status;
        } else {
            status = AuthStatus.DOESNT_EXIST;
            return status;
        }
    }

    public void completeLogin(UserDetails u) {
        UserModel um = userBusinessService.getByEmail(u.getUsername());
        if (um != null) {
            hasLoggedIn = true;
            status = AuthStatus.SUCCESS;
            loggedInUser = um;
        }
    }

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
    public boolean isLoggedIn() {
        boolean ssLoggedIn = SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null;
        return (loggedInUser != null) && status == AuthStatus.SUCCESS && ssLoggedIn;
    }
    public String getEmail() {
        return loggedInUser.getEmail();
    }
    public void logout() {
        status = AuthStatus.NOT_LOGGED_IN;
        loggedInUser = null;
        springSecurityLogout(false, null);
    }
    public void logout(HttpServletRequest request) {
        status = AuthStatus.NOT_LOGGED_IN;
        loggedInUser = null;
        springSecurityLogout(true, request);
    }
    private void springSecurityLogout(boolean dropSession, HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        if (dropSession && request != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
    }
    public UserModel getLoggedInUser() {
        return loggedInUser;
    }
    public boolean update(UserModel newModel) {
        if (isLoggedIn()) {
            return userBusinessService.updateUser(loggedInUser, newModel);
        } else {
            return false;
        }
    }
    public List<PostModel> getPostsForUser() {
        if (isLoggedIn()) {
            return userBusinessService.getPostsForUser(loggedInUser);
        } else {
            return Collections.emptyList();
        }
    }
    // TODO idk if we also have to update stuff in spring security
    public void setLoggedInUser(UserModel m) {
        this.loggedInUser = m;
    }
}

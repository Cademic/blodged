package com.blodged.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.blodged.business.AccountService;
import com.blodged.business.AuthService;
import com.blodged.data.service.FollowsBusinessService;
import com.blodged.data.service.LikesBusinessService;
import com.blodged.data.service.UserBusinessService;
import com.blodged.model.db.PostModel;
import com.blodged.model.db.UserModel;
import com.blodged.model.request.AuthModel;
import com.blodged.model.request.DeleteAccountModel;
import com.blodged.model.request.FollowPostModel;
import com.blodged.model.request.LoginModel;
import com.blodged.model.request.RegisterModel;
import com.blodged.model.request.UpdateUserModel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * the controller for the /accounts/ URLs
 */
@Controller
@RequestMapping("/accounts")
public class AccountsController {

    private @Autowired AuthService auth;
    private @Autowired UserBusinessService userBusinessService;
    private @Autowired AccountService accountService;
    private @Autowired FollowsBusinessService followsBusinessService;
    private @Autowired LikesBusinessService likesBusinessService;
    private @Autowired PasswordEncoder passwordEncoder;

    /**
     * the page to login to the site
     */
    @GetMapping("/login")
    public String getLogin(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("loginModel", new LoginModel());
        model.addAttribute("error", error);
        return "login";
    }
    /**
     * the page to register to the site
     */
    @GetMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("registerModel", new RegisterModel());
        return "register";
    }
    /**
     * register to the site
     */
    @PostMapping("/register")
    public String postRegister(@Valid RegisterModel rMod, BindingResult bindingResult, Model model, HttpServletRequest req) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        boolean res = auth.register(rMod.getUsername(), rMod.getEmail(), rMod.getPassword(), req);
        if (res) {
            return "redirect:/";
        } else {
            bindingResult.addError(new ObjectError("RegisterError", "That account already exists!"));
            return "register";
        }

    }
    /**
     * the page for updating account options/settings
     */
    @GetMapping("/options")
    public String getMethodName(Model model, @RequestParam(required = false) String toEdit) {
        if (auth.isLoggedIn()) {
            model.addAttribute("user", auth.getLoggedInUser());
            if (toEdit != null) {
                model.addAttribute("toEdit", toEdit);
            } else {
                model.addAttribute("toEdit", "none");
            }
            return "accountOptions";
        } else {
            return "redirect:/accounts/login";
        }
    }
    /**
     * update a user's options
     */
    @PostMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UpdateUserModel entity, BindingResult b) {
        boolean success = true;
        if (!b.hasErrors()) {
            if (auth.isLoggedIn() && auth.getLoggedInUser().getId() == entity.getUserId()) {
                UserModel u = auth.getLoggedInUser();
                switch(entity.getKeyToUpdate()) {
                    case "username":
                        u.setUsername(entity.getValueToUpdate());
                        break;
                    case "password":
                        u.setPassword(passwordEncoder.encode(entity.getValueToUpdate()));
                        break;
                    case "email":
                        u.setEmail(entity.getValueToUpdate());
                        break;
                    case "bio":
                        u.setBio(entity.getValueToUpdate());
                        break;
                    case "private":
                        boolean bb = Boolean.parseBoolean(entity.getValueToUpdate());
                        u.setPrivate(bb);
                        break;
                }
                success = userBusinessService.updateUser(u, u);
                if (!success) b.addError(new ObjectError("error", "Could not save user!"));
                else auth.setLoggedInUser(u);
            } else {
                b.addError(new ObjectError("error", "Could not authenticate user!"));
                success = false;
            }
        } else success = false;
        if (success) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(b.getAllErrors().get(0).getDefaultMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * delete an account and its associated posts/likes
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DeleteAccountModel entity, HttpServletRequest req) {
        // at this point, auth.getLoggedInUser() will always be null
        UserModel toDelete = userBusinessService.getFromId(entity.getUserId());
        if (auth.isLoggedIn() && auth.getLoggedInUser().getId() == toDelete.getId()) {
            auth.logout(req);
            followsBusinessService.unfollowAll(toDelete.getId());
            boolean success = userBusinessService.deleteUser(toDelete, entity.isDeletePosts());
            if (success) {
                accountService.remove(toDelete.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Could not delete!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Not authorized!", HttpStatus.UNAUTHORIZED);
        }
    }
    /**
     * check if an email and password combo is a valid authentication
     */
    @PostMapping("/auth")
    public ResponseEntity<?> auth(@RequestBody AuthModel entity) {
        if (auth.isLoggedIn() && entity.getUserId() == auth.getLoggedInUser().getId()) {
            if (passwordEncoder.matches(entity.getPassword(), auth.getLoggedInUser().getPassword())) {
                return new ResponseEntity<>("", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not authorized!", HttpStatus.UNAUTHORIZED);
            }
        } else {
            UserModel ue = userBusinessService.getFromId(entity.getUserId());
            if (ue != null && ue.getPassword().equals(entity.getPassword())) {
                return new ResponseEntity<>("", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not authorized!", HttpStatus.UNAUTHORIZED);
            }
        }
    }
    /**
     * view a user's profie
     */
    @GetMapping("/view/{userId}")
    public String viewUser(@PathVariable long userId, Model model, @RequestParam(required = false) String toEdit) {
        model.addAttribute("toEdit", toEdit == null ? -1 : Long.parseLong(toEdit));
        model.addAttribute("user", auth.getLoggedInUser());
        model.addAttribute("viewing", userBusinessService.getFromId(userId));
        model.addAttribute("following", auth.getLoggedInUser() != null && followsBusinessService.doesUserFollow(auth.getLoggedInUser().getId(), userId));
        model.addAttribute("followersCount", followsBusinessService.countFollowers(userId));
        model.addAttribute("followingCount", followsBusinessService.countFollowing(userId));
        model.addAttribute("postPage", false);
        List<PostModel> posts = userBusinessService.getPostsForUser(userBusinessService.getFromId(userId));
        model.addAttribute("posts", userBusinessService.getPostsForUser(userBusinessService.getFromId(userId)));
        model.addAttribute("likes", auth.isLoggedIn() ? likesBusinessService.getUserLikes(auth.getLoggedInUser().getId(), posts) : likesBusinessService.emptyLikes(posts));
        return "userProfile";
    }
    /**
     * follow a user
     */
    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestBody FollowPostModel entity) {
        UserModel m = userBusinessService.getFromId(entity.getUserToFollow());
        if (m != null) {
            if (auth.isLoggedIn()) {
                followsBusinessService.follow(auth.getLoggedInUser().getId(), m.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User does not exist!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Not authorized!", HttpStatus.UNAUTHORIZED);
        }
    }
    /**
     * unfollow a user
     */
    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollow(@RequestBody FollowPostModel entity) {
        if (auth.isLoggedIn()) {
            followsBusinessService.unfollow(auth.getLoggedInUser().getId(), entity.getUserToFollow());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Not authorized!", HttpStatus.UNAUTHORIZED);
        }
    }
    
    



}

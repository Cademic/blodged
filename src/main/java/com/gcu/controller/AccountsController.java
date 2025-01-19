package com.gcu.controller;

import com.gcu.business.AccountService;
import com.gcu.business.AuthService;
import com.gcu.data.service.FollowsBusinessService;
import com.gcu.data.service.LikesBusinessService;
import com.gcu.business.AuthService.AuthStatus;
import com.gcu.data.service.UserBusinessService;
import com.gcu.model.db.PostModel;
import com.gcu.model.db.UserModel;
import com.gcu.model.request.AuthModel;
import com.gcu.model.request.DeleteAccountModel;
import com.gcu.model.request.FollowPostModel;
import com.gcu.model.request.LoginModel;
import com.gcu.model.request.RegisterModel;
import com.gcu.model.request.UpdateUserModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/accounts")
public class AccountsController {

    private @Autowired AuthService auth;
    private @Autowired UserBusinessService userBusinessService;
    private @Autowired AccountService accountService;
    private @Autowired FollowsBusinessService followsBusinessService;
    private @Autowired LikesBusinessService likesBusinessService;
    private @Autowired PasswordEncoder passwordEncoder;

    // public
    @GetMapping("/login")
    public String getLogin(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("loginModel", new LoginModel());
        model.addAttribute("error", error);
        return "login";
    }
    // public
    @GetMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("registerModel", new RegisterModel());
        return "register";
    }
    // public
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
    // to be removed by spring security
    @GetMapping("/logout")
    public String logoutPost(Model model) {
        auth.logout();
        return "redirect:/";
    }
    // private user
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
    // private user
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
    // private user
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
    // public
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
    // public
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
    // private user
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
    // private user
    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollow(@RequestBody FollowPostModel entity) {
        if (auth.isLoggedIn()) {
            followsBusinessService.unfollow(auth.getLoggedInUser().getId(),entity.getUserToFollow());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Not authorized!", HttpStatus.UNAUTHORIZED);
        }
    }
    
    



}

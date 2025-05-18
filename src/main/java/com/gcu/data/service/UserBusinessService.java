package com.gcu.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gcu.data.entities.LikeEntity;
import com.gcu.data.entities.PostEntity;
import com.gcu.data.entities.UserEntity;
import com.gcu.data.entities.metadata.UserMetadataEntity;
import com.gcu.data.repository.FollowsRepo;
import com.gcu.data.repository.LikesRepository;
import com.gcu.data.repository.PostRepo;
import com.gcu.data.repository.UserRepo;
import com.gcu.model.db.PostModel;
import com.gcu.model.db.UserModel;
import com.gcu.model.db.metadata.UserMetadata;
/**
 * A business logic class that contains most logic needed to make users and the database interract correctly
 */
@Service
public class UserBusinessService implements UserDetailsService {

    @Autowired
    private UserRepo service;
    @Autowired
    private PostRepo postService;
    @Autowired
    private FollowsRepo followsRepo;
    private @Autowired LikesRepository likesRepository;

    public UserBusinessService(UserRepo service, PostRepo postsBusinessService) {
        this.service=service;
        this.postService=postsBusinessService;
    }
/**
 * Gets all the users from the database
 * @return returns a list of type UserModel that contains all the users.
 */
    public List<UserModel> getAllUsers() {
        List<UserModel> updated = new ArrayList<>();
        for (UserEntity ue : service.findAll()) {
            updated.add(new UserModel(ue.getPassword(), ue.getUsername(), ue.getEmail(), ue.getId(), ue.getBio(), ue.getCreatedDate(), ue.isPrivate(), ue.getGravatarEmail()));
        }
        return updated;
    }
/**
 * Method to create a new user and save it to the database
 * @param username username of the new user
 * @param password password of the new user
 * @param email email of the new user
 * @return returns a UserModel of the new user
 */
    public UserModel create(String username, String password, String email) {
        UserEntity en = new UserEntity(password, email, username);
        if (service.testExists(username, email).isEmpty()) {
            en = service.save(en);
            return new UserModel(en.getPassword(), en.getUsername(), en.getEmail(), en.getId(), en.getBio(), en.getCreatedDate(), en.isPrivate(), en.getGravatarEmail());
        } else {
            return null;
        }
    }
/**
 * Method to get a user based on Id
 * @param id the id of the user to get
 * @return a UserModel that is the user
 * @return null if no user is found
 */
    public UserModel getFromId(long id) {
        UserEntity en = service.findById(id).orElse(null);
        if (en != null) return new UserModel(en.getPassword(), en.getUsername(), en.getEmail(), en.getId(), en.getBio(), en.getCreatedDate(), en.isPrivate(), en.getGravatarEmail());
        else return null;
    }
    /**
     * Method to get the username based on a user Id
     * @param username username of the user to find the id for
     * @return the id of the user
     */
    public long idFromUsername(String username) {
        UserEntity e = service.findByUsername(username);
        if (e != null) {
            return e.getId();
        } else {
            return -1;
        }
    }
    /**
     * Method to update a user in the db
     * @param oldUser the original user
     * @param newUser the new user
     * @return a boolean based on the success of the operation
     */
    public boolean updateUser(UserModel oldUser, UserModel newUser) {
        if (oldUser.getId() == newUser.getId()) {
            UserEntity oldUserEntity = service.findById(oldUser.getId()).orElse(null);
            if (oldUserEntity != null) {
                adaptIfNotNull(oldUserEntity, newUser);
                UserEntity m = service.save(oldUserEntity);
                return m!=null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    /**
     * Method to get a user by email
     * @param email the email of the user to get
     * @return UserModel of the found user
     * @return null if no user was found
     */
    public UserModel getByEmail(String email) {
        UserEntity u = service.findByEmail(email);
        if (u != null) {
            return new UserModel(u.getPassword(), u.getUsername(), u.getEmail(), u.getId(), u.getBio(), u.getCreatedDate(), u.isPrivate(), u.getGravatarEmail());
        } else return null;
    }
    /**
     * Method to delete a user
     * @param user UserModel of user to delete
     * @param deletePosts boolean on whether to delete their posts
     * @return boolean based on success of operation
     */
    public boolean deleteUser(UserModel user, boolean deletePosts) {
        UserEntity userEntity = service.findById(user.getId()).orElse(null);
        if (userEntity != null) {
                for (LikeEntity e : likesRepository.likesForUser(user.getId())) {
                    likesRepository.delete(e);
                }
                followsRepo.clearUser(user.getId());
                for (PostModel m : getPostsForUser(user)) {
                   PostEntity pe = postService.findById(m.getId()).orElse(null);
                    if (pe != null) {
                        postService.delete(pe);
                    }
                   
                }
            service.delete(userEntity);
            return true;
        } else {
            return false;
        }
    }
    private void adaptIfNotNull(UserEntity old, UserModel newM) {
        if (newM.getBio() != null) old.setBio(newM.getBio());
        if (newM.getCreatedDate() != null) old.setCreatedDate(newM.getCreatedDate());
        if (newM.getEmail() != null) old.setEmail(newM.getEmail());
        if (newM.getUsername() != null) old.setUsername(newM.getUsername());
        if (newM.getPassword() != null) old.setPassword(newM.getPassword());
        if (newM.getGravatarEmail() != null) old.setGravatarEmail(newM.getGravatarEmail());
        old.setPrivate(newM.isPrivate());
    }
    /**
     * Method to get the posts for a specific user from the db
     * @param m UserModel of the user to get the posts for
     * @return list of type PostModel that contains all the posts for the user
     */
    public List<PostModel> getPostsForUser(UserModel m) {
        List<PostEntity> dbPosts = postService.findByUser(m.getId());
        List<PostModel> postDomain = new ArrayList<>();
        for (PostEntity e : dbPosts) {
            PostModel pm = new PostModel(m, e.getPostContent(), e.getId());
            pm.setLikeCount(likesRepository.likesForPost(pm.getId()).size());
            postDomain.add(pm);
        }
        return postDomain;
    }
/**
 * Method to get all the meta data for all the users
 * @return returns a List of type UserMetaData
 */
    public List<UserMetadata> getAllUsersMeta() {
        return service.getAllMetadata().stream().map(ume -> new UserMetadata(ume.id, ume.username, ume.bio, ume.createdDate, ume.followedByCount, ume.followingCount, ume.likesGiven)).collect(Collectors.toList());
    }
    /**
     * Method to get a specific user's meta data
     * @param username username of the user to get the metadata for
     * @return returns an object of type UserMetaData of the user
     */
    public UserMetadata getUserMeta(String username) {
        UserMetadataEntity ume = service.getMetadata(username);
        return new UserMetadata(ume.id, ume.username, ume.bio, ume.createdDate, ume.followedByCount, ume.followingCount, ume.likesGiven);
    }
    /**
     * Method to get all the users that are followed by a specific user
     * @param username username of the user
     * @return returns a list of the usernames that the specific user is following
     */
    public List<String> getUsersFollowedBy(String username) {
        return service.getUserFollowedBy(username);
    }
    /**
     * Method to get all the users that are following a specific user
     * @param username username of the user
     * @return returns a list of the usernames that are following a specific user
     */
    public List<String> getUsersFollowing(String username) {
        return service.getUserFollowing(username);
    }
    /**
     * gets a user based on a specific username
     * @param username username of user to get
     * @return object of type UserModel of the user
     */
    public UserModel getFromUsername(String username) {
        UserEntity ue= service.findByUsername(username);
        return new UserModel(ue.getPassword(), ue.getUsername(), ue.getEmail(), ue.getId(), ue.getBio(), ue.getCreatedDate(), ue.isPrivate(), ue.getGravatarEmail());
    }
    /**
     * Method to check if an email already exists in the db
     * @param email email to check
     * @return boolean based off of if it exists or not
     */
    public boolean emailExists(String email) {
        return service.emailExists(email) != null;
    }

    /**
     * IMPORTANT! this loads the user by email, not by username
     * replace username with email in any fields that might have to do with userdetails
     * IE regard username as email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity user = service.findByEmail(email);
        if (user != null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
            return new User(user.getEmail(), user.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("Email " + email + " not found!");
        }

    }
}

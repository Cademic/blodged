package com.gcu.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gcu.data.entities.metadata.UserMetadataEntity;
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
import com.gcu.data.repository.FollowsRepo;
import com.gcu.data.repository.LikesRepository;
import com.gcu.data.repository.PostRepo;
import com.gcu.data.repository.UserRepo;
import com.gcu.model.db.PostModel;
import com.gcu.model.db.metadata.UserMetadata;
import com.gcu.model.db.UserModel;

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

    public List<UserModel> getAllUsers() {
        List<UserModel> updated = new ArrayList<>();
        for (UserEntity ue : service.findAll()) {
            updated.add(new UserModel(ue.getPassword(), ue.getUsername(), ue.getEmail(), ue.getId(), ue.getBio(), ue.getCreatedDate(), ue.isPrivate()));
        }
        return updated;
    }

    public UserModel create(String username, String password, String email) {
        UserEntity en = new UserEntity(password, email, username);
        if (service.testExists(username, email).isEmpty()) {
            en = service.save(en);
            return new UserModel(en.getPassword(), en.getUsername(), en.getEmail(), en.getId(), en.getBio(), en.getCreatedDate(), en.isPrivate());
        } else {
            return null;
        }
    }

    public UserModel getFromId(long id) {
        UserEntity en = service.findById(id).orElse(null);
        if (en != null) return new UserModel(en.getPassword(), en.getUsername(), en.getEmail(), en.getId(), en.getBio(), en.getCreatedDate(), en.isPrivate());
        else return null;
    }
    public long idFromUsername(String username) {
        UserEntity e = service.findByUsername(username);
        if (e != null) {
            return e.getId();
        } else {
            return -1;
        }
    }
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
    public UserModel getByEmail(String email) {
        UserEntity u = service.findByEmail(email);
        if (u != null) {
            return new UserModel(u.getPassword(), u.getUsername(), u.getEmail(), u.getId(), u.getBio(), u.getCreatedDate(), u.isPrivate());
        } else return null;
    }
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
        old.setPrivate(newM.isPrivate());
    }
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

    public List<UserMetadata> getAllUsersMeta() {
        return service.getAllMetadata().stream().map(ume -> new UserMetadata(ume.id, ume.username, ume.bio, ume.createdDate, ume.followedByCount, ume.followingCount, ume.likesGiven)).collect(Collectors.toList());
    }
    public UserMetadata getUserMeta(String username) {
        UserMetadataEntity ume = service.getMetadata(username);
        return new UserMetadata(ume.id, ume.username, ume.bio, ume.createdDate, ume.followedByCount, ume.followingCount, ume.likesGiven);
    }
    // users followed by {username}
    public List<String> getUsersFollowedBy(String username) {
        return service.getUserFollowedBy(username);
    }
    // users following {username} user_following = {username}
    public List<String> getUsersFollowing(String username) {
        return service.getUserFollowing(username);
    }
    public UserModel getFromUsername(String username) {
        UserEntity ue= service.findByUsername(username);
        return new UserModel(ue.getPassword(), ue.getUsername(), ue.getEmail(), ue.getId(), ue.getBio(), ue.getCreatedDate(), ue.isPrivate());
    }
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

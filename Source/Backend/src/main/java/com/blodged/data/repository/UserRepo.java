package com.blodged.data.repository;
import java.util.List;

import com.blodged.data.entities.metadata.UserMetadataEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.blodged.data.entities.UserEntity;

/**
 * Database interaction object for the users table
 */
public interface UserRepo extends CrudRepository<UserEntity, Long> {
    /**
     * find a user by their username
     * @param username the username to search
     * @return the user entity of found, null if not
     */
    @Query(value = "SELECT * FROM users WHERE username = :username")
    UserEntity findByUsername(String username);

    /**
     * test if a username and/or email exists on a user
     * @param username the username to check
     * @param email the email to check
     * @return a list of applicable users
     */
    @Query(value = "SELECT * FROM users WHERE username = :username OR email = :email")
    List<UserEntity> testExists(String username, String email);

    /**
     * test if just an email is taken by a user
     * @param email the email to check
     * @return the user if it is taken, null if not 
     */
    @Query(value = "SELECT * FROM users WHERE email = :email")
    UserEntity emailExists(String email);

    /**
     * select and build the API representation of a user based on their username
     * @param username the username to search
     * @return the API representation of the user with that username
     */
    @Query(value = """
            SELECT users.id, users.username, users.bio, users.created_date,
                        COALESCE(fbc, 0) as followedByCount,
                        COALESCE(fc, 0) as followingCount,
                        COALESCE(lc, 0) as likesGiven
                        FROM users
                        LEFT OUTER JOIN (SELECT user_followed as fbc_id, count(*) fbc FROM follows GROUP BY fbc_id) i
                        ON i.fbc_id = users.id
                        LEFT OUTER JOIN (SELECT user_following as fb_id, count(*) fc FROM follows GROUP BY fb_id) as j
                        ON j.fb_id = users.id
                        LEFT OUTER JOIN (SELECT user_id, count(*) lc FROM likes GROUP BY user_id) as k
                        ON k.user_id = users.id
                        WHERE username = :username;""")
    UserMetadataEntity getMetadata(String username);

    /**
     * select and build the API representation of all users
     * @return the API representation of all users
     */
    @Query(value = """
            SELECT users.id, users.username, users.bio, users.created_date,
                        COALESCE(fbc, 0) as followedByCount,
                        COALESCE(fc, 0) as followingCount,
                        COALESCE(lc, 0) as likesGiven
                        FROM users
                        LEFT OUTER JOIN (SELECT user_followed as fbc_id, count(*) fbc FROM follows GROUP BY fbc_id) i
                        ON i.fbc_id = users.id
                        LEFT OUTER JOIN (SELECT user_following as fb_id, count(*) fc FROM follows GROUP BY fb_id) as j
                        ON j.fb_id = users.id
                        LEFT OUTER JOIN (SELECT user_id, count(*) lc FROM likes GROUP BY user_id) as k
                        ON k.user_id = users.id;""")
    List<UserMetadataEntity> getAllMetadata();

    /**
     * select a user based on their email
     * @param email the email to search
     * @return the user with that email
     */
    @Query(value = "SELECT * FROM users WHERE email = :email")
    UserEntity findByEmail(String email);

    /**
     * get a string list representation of the usernames following a specified user
     * @param username the username of the user who is followed
     * @return a list of the usernames following that user
     */
    @Query(value = "SELECT j.username as username FROM follows " +
            "LEFT JOIN (SELECT id as user_id, username FROM users GROUP BY id) as i " +
            "ON follows.user_following = i.user_id " +
            "LEFT JOIN (SELECT id as user_id, username FROM users GROUP BY id) as j " +
            "ON follows.user_followed = j.user_id " +
            "WHERE i.username = :username;")
    List<String> getUserFollowedBy(String username);

    /**
     * get a string list representation of the username that a specified user is following 
     * @param username the username to check
     * @return the usernames that are followed by the aforementioned username
     */
    @Query(value="SELECT j.username as username FROM follows LEFT JOIN (SELECT id as user_id, username FROM users GROUP BY id) as i ON follows.user_followed = i.user_id LEFT JOIN (SELECT id as user_id, username FROM users GROUP BY id) as j ON follows.user_following = j.user_id WHERE i.username = :username;")
    List<String> getUserFollowing(String username);

}

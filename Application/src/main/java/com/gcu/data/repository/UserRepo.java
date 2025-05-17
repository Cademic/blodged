package com.gcu.data.repository;
import java.util.List;

import com.gcu.data.entities.metadata.UserMetadataEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.gcu.data.entities.UserEntity;
public interface UserRepo extends CrudRepository<UserEntity, Long> {
    
    @Query(value = "SELECT * FROM users WHERE username = :username")
    UserEntity findByUsername(String username);

    @Query(value = "SELECT * FROM users WHERE username = :username OR email = :email")
    List<UserEntity> testExists(String username, String email);

    @Query(value = "SELECT * FROM users WHERE email = :email")
    UserEntity emailExists(String email);

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

    @Query(value = "SELECT * FROM users WHERE email = :email")
    UserEntity findByEmail(String email);

    @Query(value = "SELECT j.username as username FROM follows " +
            "LEFT JOIN (SELECT id as user_id, username FROM users GROUP BY id) as i " +
            "ON follows.user_following = i.user_id " +
            "LEFT JOIN (SELECT id as user_id, username FROM users GROUP BY id) as j " +
            "ON follows.user_followed = j.user_id " +
            "WHERE i.username = :username;")
    List<String> getUserFollowedBy(String username);

    @Query(value="SELECT j.username as username FROM follows LEFT JOIN (SELECT id as user_id, username FROM users GROUP BY id) as i ON follows.user_followed = i.user_id LEFT JOIN (SELECT id as user_id, username FROM users GROUP BY id) as j ON follows.user_following = j.user_id WHERE i.username = :username;")
    List<String> getUserFollowing(String username);

}

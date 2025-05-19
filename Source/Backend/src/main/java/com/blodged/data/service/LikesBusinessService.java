package com.blodged.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blodged.data.repository.LikesRepository;
import com.blodged.model.db.PostModel;
/**
 * Business logic class to handle the logic for likes
 */
@Service
public class LikesBusinessService {

    private @Autowired LikesRepository likesRepo;
    /**
     * Method to get the likes for a specific user
     * @param userId the id of the user to get the likes for
     * @param posts the posts to get the likes for
     * @return returns a hash map of a long for the post id, and a boolean if the user has liked the post or not
     */
    public Map<Long, Boolean> getUserLikes(long userId, List<PostModel> posts) {
        Map<Long, Boolean> domain = new HashMap<>();
        for (PostModel p : posts) {
            if (likesRepo.userHasLiked(userId, p.getId()) != null) {
                domain.put(p.getId(), true);
            } else {
                domain.put(p.getId(), false);
            }
        }
        return domain;
    }
    /**
     * this method clears all the likes for the sent in posts
     * @param posts the posts to clear all the likes for
     * @return returns a hashmap with the long as the post id, and the boolean for whether the post has likes or not, which will be false.
     */
    public Map<Long, Boolean> emptyLikes(List<PostModel> posts) {
        Map<Long, Boolean> domain = new HashMap<>();
        for (PostModel p : posts) {
            domain.put(p.getId(), false);
        }
        return domain;
    }
    
}

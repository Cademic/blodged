package com.gcu.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gcu.data.repository.LikesRepository;
import com.gcu.model.db.PostModel;

@Service
public class LikesBusinessService {

    private @Autowired LikesRepository likesRepo;

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
    public Map<Long, Boolean> emptyLikes(List<PostModel> posts) {
        Map<Long, Boolean> domain = new HashMap<>();
        for (PostModel p : posts) {
            domain.put(p.getId(), false);
        }
        return domain;
    }
    
}

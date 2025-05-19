package com.blodged.controller.REST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blodged.business.AuthService;
import com.blodged.business.PostService;
import com.blodged.data.repository.LikesRepository;
import com.blodged.data.service.PostsBusinessService;
import com.blodged.model.db.PostModel;
import com.blodged.model.db.UserModel;
import com.blodged.model.db.metadata.PostMetadata;
import com.blodged.model.request.CreatePostRequest;
import com.blodged.model.request.EditPostModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping("/api/posts")
//@Api(value = "Posts API", tags = "Posts")
public class PostsRestControllerPublic {

    private @Autowired PostsBusinessService postsBusinessService;
    private @Autowired LikesRepository likesRepository;
    private @Autowired PostService postService;
    private @Autowired AuthService authService;
    
    @Operation(summary = "Retrieve all posts metadata", responses = {@ApiResponse(responseCode = "200", description = "returns a list", content = @Content(schema = @Schema(implementation = PostMetadata.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/all")
    public ResponseEntity<?> getAllPosts() {
        try {
            List<PostMetadata> posts = postsBusinessService.getAllPostMeta();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Retrieve metadata for a specific post by ID",  responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PostMetadata.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getPostById(@PathVariable(value = "id") long id) {
        try {
            PostMetadata post = postsBusinessService.getMetaById(id);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Retrieve posts metadata for a specific user by username",  responses = {@ApiResponse(responseCode = "200", description = "returns a list", content = @Content(schema = @Schema(implementation = PostMetadata.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getPostsByUsername(@PathVariable(value = "username") String username) {
        try {
            List<PostMetadata> post = postsBusinessService.getMetaByUsername(username);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Retrieve a list of users who liked a specific post",  responses = {@ApiResponse(responseCode = "200", description = "returns a list", content = @Content(schema = @Schema(implementation = String.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/likedBy/{postId}")
    public ResponseEntity<?> getPostLikedBy(@PathVariable(value = "postId") long postId) {
        try {
            List<String> likes = likesRepository.usersWhoLikePost(postId);
            return new ResponseEntity<>(likes, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "Create a new post", responses = {
        @ApiResponse(responseCode = "201", description = "Post created successfully", content = @Content(schema = @Schema(implementation = PostModel.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request) {
        try {
            UserModel currentUser = authService.getLoggedInUser();
            if (currentUser == null) {
                return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
            }
            
            postService.createPost(currentUser, request.getContent());
            PostModel post = postService.getPosts().stream()
                .filter(p -> p.getPostUser().getId() == currentUser.getId() && p.getPostContent().equals(request.getContent()))
                .findFirst().orElse(null);
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "Update an existing post", responses = {
        @ApiResponse(responseCode = "200", description = "Post updated successfully", content = @Content(schema = @Schema(implementation = PostModel.class))),
        @ApiResponse(responseCode = "401", description = "User not authorized to edit this post"),
        @ApiResponse(responseCode = "404", description = "Post not found"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable(value = "id") long id, @RequestBody EditPostModel editModel) {
        try {
            PostModel post = postService.getPostById(id);
            
            if (post == null) {
                return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
            }
            
            UserModel currentUser = authService.getLoggedInUser();
            if (currentUser == null || post.getPostUser().getId() != currentUser.getId()) {
                return new ResponseEntity<>("Not authorized to edit this post", HttpStatus.UNAUTHORIZED);
            }
            
            postService.updateContent(post, editModel.getContent());
            return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "Delete a post", responses = {
        @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
        @ApiResponse(responseCode = "401", description = "User not authorized to delete this post"),
        @ApiResponse(responseCode = "404", description = "Post not found"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable(value = "id") long id) {
        try {
            PostModel post = postService.getPostById(id);
            
            if (post == null) {
                return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
            }
            
            UserModel currentUser = authService.getLoggedInUser();
            if (currentUser == null || post.getPostUser().getId() != currentUser.getId()) {
                return new ResponseEntity<>("Not authorized to delete this post", HttpStatus.UNAUTHORIZED);
            }
            
            postService.deletePost(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
package com.gcu.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gcu.business.AuthService;
import com.gcu.data.service.FollowsBusinessService;
import com.gcu.data.service.LikesBusinessService;
import com.gcu.business.PostService;
import com.gcu.data.service.UserBusinessService;
import com.gcu.model.db.PostModel;
import com.gcu.model.db.ReplyModel;
import com.gcu.model.request.EditPostModel;
import com.gcu.model.request.PostCreationModel;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/posts")
public class PostController {

    private @Autowired AuthService authService;
    private @Autowired PostService postService;
    private @Autowired UserBusinessService userBusinessService;
    private @Autowired FollowsBusinessService followsBusinessService;
    private @Autowired LikesBusinessService likesBusinessService;

	// public
    @GetMapping("/view/{postId}")
	public String viewPost(@PathVariable("postId") long postId, Model model, @RequestParam(required = false) String toEdit, @RequestParam(required = false) String toEditReply) {
		if (authService.isLoggedIn()) {
			model.addAttribute("user", authService.getLoggedInUser());
		}
		long userId = postService.getPostById(postId).getPostUser().getId();
		model.addAttribute("toEdit", toEdit == null ? -1 : Long.parseLong(toEdit));
		model.addAttribute("toEditReply", toEditReply == null ? -1 : Long.parseLong(toEditReply));
        model.addAttribute("viewing", userBusinessService.getFromId(userId));
        model.addAttribute("following", authService.getLoggedInUser() != null ? followsBusinessService.doesUserFollow(authService.getLoggedInUser().getId(), userId) : false);
        model.addAttribute("followersCount", followsBusinessService.countFollowers(userId));
        model.addAttribute("followingCount", followsBusinessService.countFollowing(userId));
		model.addAttribute("postPage", true);
		postService.refresh();
		List<PostModel> posts = new ArrayList<PostModel>();
		posts.add(postService.getPostById(postId));
		model.addAttribute("posts", posts);
		if (authService.isLoggedIn()) {
			model.addAttribute("likes", likesBusinessService.getUserLikes(authService.getLoggedInUser().getId(), posts));
		} else {
			model.addAttribute("likes", likesBusinessService.emptyLikes(posts));
		}
		
		List<ReplyModel> replies = postService.getReplies(postId);
		model.addAttribute("replies", replies);
		
		return "postPage";
	}
	// n/a
	private boolean canModifyPost(PostModel p) {
		return authService.isLoggedIn() && (p.getPostUser().getId() == authService.getLoggedInUser().getId());
	}
	// private user
	@PutMapping("/edit/{postId}")
	public ResponseEntity<String> editPost(@PathVariable("postId") long postId, @RequestBody EditPostModel model) {
		PostModel p = postService.getPostById(postId);
		if (p != null) {
			if (canModifyPost(p)) {
				postService.updateContent(p, model.getContent());
				return new ResponseEntity<>("", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Not authorized to edit this post!", HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>("That post doesn't exist!", HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	// private user
	@DeleteMapping("/delete/{postId}")
	public ResponseEntity<String> deletePost(@PathVariable("postId") long postId) {
		PostModel p = postService.getPostById(postId);
		if (p != null) {
			if (canModifyPost(p)) {
				postService.deletePost(p.getId());
				return new ResponseEntity<>("", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Not authorized to delete this post!", HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>("That post doesn't exist!", HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	// private user
    @GetMapping("/create")
	public String createPost(Model model) {
		model.addAttribute("postCreationModel", new PostCreationModel());
		model.addAttribute("user", authService.getLoggedInUser());
		return "createPost";
	}
	// private user
	@PostMapping(value="/create", params= ("post"))
	public String postCreateValid(@Valid PostCreationModel postCreationModel, BindingResult bindingResult, Model model) {
		model.addAttribute("user", authService.getLoggedInUser());

			if (bindingResult.hasErrors()) {
				return "createPost";
			}
			postService.createPost(authService.getLoggedInUser(), postCreationModel.getPostContent());
			return "redirect:/";
	}
	// private user
	@PostMapping(value="/create", params=("back"))
	public String postCreateCancel(@Valid PostCreationModel postCreationModel, BindingResult bindingResult, Model model) {
		model.addAttribute("user", authService.getLoggedInUser());
		return "redirect:/";
	}
	
}

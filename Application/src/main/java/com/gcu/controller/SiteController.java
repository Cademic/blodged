package com.gcu.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gcu.business.AuthService;
import com.gcu.data.service.FollowsBusinessService;
import com.gcu.data.service.LikesBusinessService;
import com.gcu.business.PostService;
import com.gcu.data.service.UserBusinessService;
import com.gcu.model.db.PostModel;
import com.gcu.model.db.UserModel;





@Controller
@RequestMapping("/")
public class SiteController {

	private @Autowired AuthService authService;
	private @Autowired PostService postService;
	private @Autowired FollowsBusinessService followsBusinessService;
	private @Autowired UserBusinessService userBusinessService;
	private @Autowired LikesBusinessService likesBusinessService;

	// public
	@GetMapping("/")
	public String root(Model model, @RequestParam(required = false) String toEdit) {
		if (authService.isLoggedIn()) {
			model.addAttribute("user", authService.getLoggedInUser());
		}
		model.addAttribute("postPage", false);
		model.addAttribute("toEdit", toEdit == null ? -1 : Long.parseLong(toEdit));
		postService.refresh();
		List<PostModel> posts = postService.getPosts();
		model.addAttribute("posts", posts);
		if (authService.isLoggedIn()) {
			model.addAttribute("likes", likesBusinessService.getUserLikes(authService.getLoggedInUser().getId(), posts));
		} else {
			model.addAttribute("likes", likesBusinessService.emptyLikes(posts));
		}
		return "index";
	}

	// private user
	@GetMapping("/following")
	public String following(Model model, @RequestParam(required = false) String toEdit) {
		model.addAttribute("postPage", false);
		model.addAttribute("toEdit", toEdit == null ? -1 : Long.parseLong(toEdit));

		if (authService.isLoggedIn()) {
			model.addAttribute("user", authService.getLoggedInUser());
			List<UserModel> follows = followsBusinessService.userFollows(authService.getLoggedInUser().getId());
			List<PostModel> posts = new ArrayList<>();
			for (UserModel m : follows) {
				posts.addAll(userBusinessService.getPostsForUser(m));
			}
			Collections.sort(posts);
			model.addAttribute("followedUsers", follows);
			if (authService.isLoggedIn()) {
				model.addAttribute("likes", likesBusinessService.getUserLikes(authService.getLoggedInUser().getId(), posts));
			} else {
				model.addAttribute("likes", likesBusinessService.emptyLikes(posts));
			}
			model.addAttribute("posts", posts);
			return "following";
		} else return "index";
	}
	// private user
	@PostMapping("/unlike/{postId}")
	public ResponseEntity<?> unlike (@PathVariable("postId") long postId) {

		if (authService.isLoggedIn()) {
			boolean success = postService.unlikePost(authService.getLoggedInUser().getId(), postId);
			if (success) {
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Could not delete like!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<>("Not authorized!", HttpStatus.UNAUTHORIZED);
		}
	}
	// private user
	@PostMapping(value="/like/{postId}")
	public ResponseEntity<?> postLike(@PathVariable("postId") long postId) {
		if (authService.isLoggedIn()) {
			boolean success = postService.likePost(authService.getLoggedInUser().getId(), postId);
			if (success) {
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Could not save like!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<>("Not authorized!", HttpStatus.UNAUTHORIZED);
		}

			//postService.incLikes(Integer.parseInt(postId));
	}
	// public
	@GetMapping("/search")
	public String search(Model model, @RequestParam(required = false) String searchTerm, @RequestParam(required = false) String toEdit) {
		model.addAttribute("user", authService.getLoggedInUser());
		model.addAttribute("postPage", false);
		List<PostModel> posts;
		if (searchTerm != null && searchTerm != "") {
			posts = postService.search(searchTerm);
			Collections.reverse(posts);
			model.addAttribute("posts", posts);
			model.addAttribute("searchTerm", searchTerm);
		} else {
			posts = postService.getPosts();
			model.addAttribute("posts", posts);
		}
		if (authService.isLoggedIn()) {
			model.addAttribute("likes", likesBusinessService.getUserLikes(authService.getLoggedInUser().getId(), posts));
		} else {
			model.addAttribute("likes", likesBusinessService.emptyLikes(posts));
		}
		model.addAttribute("toEdit", toEdit == null ? -1 : Long.parseLong(toEdit));
		return "index";
	}

	@GetMapping("/apiSummary/")
	public String apiSummary() {
		return "apiSummary";
	}

	@GetMapping("/api/")
	public ResponseEntity<?> api(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String header = req.getHeader("Content-Type");
		if (header == null || !header.equals("application/json")) {
			res.sendRedirect("/apiSummary/");
		} else {
			return new ResponseEntity<>("Welcome to our API! All endpoints require authentication.", HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

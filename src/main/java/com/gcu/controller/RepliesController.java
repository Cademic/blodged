package com.gcu.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;

import com.gcu.business.AuthService;
import com.gcu.business.PostService;
import com.gcu.data.service.ReplyBusinessService;
import com.gcu.model.db.ReplyModel;
import com.gcu.model.request.EditPostModel;
import com.gcu.model.request.ReplyCreationModel;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * the controller for the replies endpoints
 */
@Controller
@RequestMapping("/replies")
public class RepliesController {
 
    private @Autowired AuthService authService; 
    private @Autowired PostService postService;
    private @Autowired ReplyBusinessService replyBusinessService;

    /**
     * the page to create a new reply
     */
    @GetMapping("/create/{postId}")
	public String createReply(@PathVariable("postId") long postId, Model model) {
		if (authService.isLoggedIn()) {
			model.addAttribute("user", authService.getLoggedInUser());
		}
		model.addAttribute("replyCreationModel", new ReplyCreationModel());
		model.addAttribute("post", postService.getPostById(postId));
		model.addAttribute("id", postId);
		return "createReply";
	}
    /**
     * create a new reply
     */
	@PostMapping(value="/create/{postId}", params= ("post"))
	public String replyCreateValid(@PathVariable("postId") long postId, @Valid ReplyCreationModel replyCreationModel, BindingResult bindingResult, Model model) {
		model.addAttribute("user", authService.getLoggedInUser());

			if (bindingResult.hasErrors()) {
				return "createReply";
			}
			ReplyModel r = postService.createReply(postId, authService.getLoggedInUser(), replyCreationModel.getReplyContent());
			return "redirect:/posts/view/{postId}?scrollTo=reply-content-" + r.getId();
	}
    /**
     * cancel creating a new reply
     */
	@PostMapping(value="/create/{postId}", params=("back"))
	public String replyCreateCancel(@PathVariable("postId") long postId,  @Valid ReplyCreationModel replyCreationModel, BindingResult bindingResult, Model model) {
		model.addAttribute("user", authService.getLoggedInUser());
		return "redirect:/posts/view/{postId}";
	}
    /**
     * check if a user can edit a reply
     */
    private boolean canModifyReply(ReplyModel reply) {
        return authService.isLoggedIn() && (reply.getUser().getId() == authService.getLoggedInUser().getId());
    }
    /**
     * update a reply
     */
    @PutMapping("/edit/{replyId}") // we can reuse edit post model because we just need content
    public ResponseEntity<String> edit(@PathVariable("replyId") long replyId, @RequestBody EditPostModel model) {
        ReplyModel m = replyBusinessService.getById(replyId);
        if (m != null) {
            if (canModifyReply(m)) {
                replyBusinessService.setContent(m, model.getContent());
                return new ResponseEntity<>("", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not authorized to edit reply!", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("Reply doesn't exist!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * delete a reply
     */
    @DeleteMapping("/delete/{replyId}")
    public ResponseEntity<String> delete(@PathVariable("replyId") long replyId) {
        ReplyModel m = replyBusinessService.getById(replyId);
        if (m != null) {
            if (canModifyReply(m)) {
                replyBusinessService.delete(m);
                return new ResponseEntity<>("", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not authorized to delete reply!", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("Reply doesn't exist!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

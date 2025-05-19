package com.blodged.model.db;

import java.util.ArrayList;
import java.util.List;
/**
 * A model for a post
 */
public class PostModel extends DBModel implements Comparable<PostModel> {

	private UserModel postUser;
	private String postContent;
	private List<ReplyModel> replies;
	private int likeCount = 0;
	/**
	 * setter for likeCount
	 * @param likeCount the new amount of likes the post has
	 */
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	//create post
	public PostModel(UserModel postUser, String postContent, long entityId) {
		super(entityId);
		this.postUser = postUser;
		this.postContent = postContent;
		replies = new ArrayList<ReplyModel>();
	}
	public PostModel() {
		super(-1);
		this.postUser = new UserModel();
		this.postContent = "invalid Post";
		replies = new ArrayList<ReplyModel>();
	}
	/**
	 * getter for postContent
	 * @return the content of the post
	 */
	public String getPostContent() {
		return this.postContent;
	}
	/**
	 * a method to get the username of the post's owner
	 * @return the username of the post's owner if the owner exists
	 * @return DELETED_ACCOUNT if there is no owner of the post
	 */
	public String getPostUserName() {
		if (postUser != null) {
			return this.postUser.getUsername();
		} else {
			return "DELETED_ACCOUNT";
		}
	}
	/**
	 * getter for postUser
	 * @return object of type UserModel that is the user that owns the post
	 */
	public UserModel getPostUser() {
		return postUser;
	}
	/**
	 * getter for likeCount
	 * @return the amount of likes the post has
	 */
	public int getLikeCount() {
		return likeCount;
	}
	/**
	 * A method that adds a new reply to the list of replies
	 * @param reply an object of type ReplyModel that is the reply you want to add
	 */
	public void createReply(ReplyModel reply) {
		replies.add(reply);
		
	}
	/**
	 * getter for replies
	 * @return returns a list of type ReplyModel that contains all the replies for the post
	 */
	public List<ReplyModel> getReplies() {
		return replies;
	}
	/**
	 * setter for replies
	 * @param list list of replies if you want to set the replies
	 */
	public void setReplies(List<ReplyModel> list) {
		this.replies = list;
	}
	/**
	 * setter for content
	 * @param newContent the new content you want to set 
	 */
	public void setContent(String newContent) {
		this.postContent = newContent;
	}
	/**
	 * Sorts based on like count
	 */
	@Override
	public int compareTo(PostModel o) {
		// TODO we should be sorting by date instead, but the posts db object does not track date
		if (likeCount == o.getLikeCount()) return 0;
		return this.getLikeCount() > o.getLikeCount() ? 1 : -1;
	}
	
}

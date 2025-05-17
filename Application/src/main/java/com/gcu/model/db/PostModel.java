package com.gcu.model.db;

import java.util.ArrayList;
import java.util.List;

public class PostModel extends DBModel implements Comparable<PostModel> {

	private UserModel postUser;
	private String postContent;
	private List<ReplyModel> replies;
	private int likeCount = 0;
	
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
	public String getPostContent() {
		return this.postContent;
	}
	public String getPostUserName() {
		if (postUser != null) {
			return this.postUser.getUsername();
		} else {
			return "DELETED_ACCOUNT";
		}
	}
	public UserModel getPostUser() {
		return postUser;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void createReply(ReplyModel reply) {
		replies.add(reply);
		
	}
	
	public List<ReplyModel> getReplies() {
		return replies;
	}
	public void setReplies(List<ReplyModel> list) {
		this.replies = list;
	}

	public void setContent(String newContent) {
		this.postContent = newContent;
	}
	@Override
	public int compareTo(PostModel o) {
		// TODO we should be sorting by date instead, but the posts db object does not track date
		if (likeCount == o.getLikeCount()) return 0;
		return this.getLikeCount() > o.getLikeCount() ? 1 : -1;
	}
	
}

package com.gcu.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReplyCreationModel {
    @Size(min=1,max=10000,message="Reply must be between 1 and 10000 characters")
    @NotNull(message="You cannot create an empty reply.")
	private String replyContent;
	public String getReplyContent() {
		return this.replyContent;
	}
	public void setReplyContent(String postContent) {
		this.replyContent = postContent;	
	}
}

package com.gcu.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostCreationModel {
    @Size(min=1,max=10000,message="Post must be between 1 and 10000 characters")
    @NotNull(message="You cannot create an empty post.")
	private String postContent;
	
	public String getPostContent() {
		return this.postContent;
	}
	public void setPostContent(String postContent) {
		this.postContent = postContent;	
	}
}

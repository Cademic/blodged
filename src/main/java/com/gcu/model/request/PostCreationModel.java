package com.gcu.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
/**
 * A model class that thymeleaf uses with forms to store data in. 
 * An instance of this class is sent to the POST controller with the data from the html form. 
 * This model is used to get the data for the new post from the html form 
 */
public class PostCreationModel {
    @Size(min=1,max=10000,message="Post must be between 1 and 10000 characters")
    @NotNull(message="You cannot create an empty post.")
	private String postContent;
	/**
	 * getter for post content
	 * @return the content of the new post
	 */
	public String getPostContent() {
		return this.postContent;
	}
	/**
	 * setter for post content
	 * @param postContent the post content that you want to set
	 */
	public void setPostContent(String postContent) {
		this.postContent = postContent;	
	}
}

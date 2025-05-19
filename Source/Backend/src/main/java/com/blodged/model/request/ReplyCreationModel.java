package com.blodged.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
/**
 * A model class that thymeleaf uses with forms to store data in. 
 * An instance of this class is sent to the POST controller with the data from the html form. 
 * This model is used to get the data for a new reply from the html form
 */
public class ReplyCreationModel {
    @Size(min=1,max=10000,message="Reply must be between 1 and 10000 characters")
    @NotNull(message="You cannot create an empty reply.")
	private String replyContent;
	/**
	 * getter for replyContent
	 * @return returns the content for the new reply
	 */
	public String getReplyContent() {
		return this.replyContent;
	}
	/**
	 * setter for replyContent
	 * @param postContent the content of the reply
	 */
	public void setReplyContent(String postContent) {
		this.replyContent = postContent;	
	}
}

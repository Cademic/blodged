package com.gcu.model.request;
/**
 * A model class that thymeleaf uses with forms to store data in. 
 * An instance of this class is sent to the POST controller with the data from the html form. 
 * This model is used to get the new content for a post edit from the html form
 */
public class EditPostModel {
    
    private String content;

    /**
     * getter for content
     * @return the content of the post
     */
    public String getContent() {
        return content;
    }
/**
 * setter for content
 * @param content the post content that you want to set
 */
    public void setContent(String content) {
        this.content = content;
    }



}

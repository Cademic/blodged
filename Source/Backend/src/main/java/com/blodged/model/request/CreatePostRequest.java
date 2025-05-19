package com.blodged.model.request;

/**
 * Request model for creating a new post
 */
public class CreatePostRequest {
    
    private String content;
    
    /**
     * Default constructor
     */
    public CreatePostRequest() {}
    
    /**
     * Constructor with content
     * 
     * @param content Post content
     */
    public CreatePostRequest(String content) {
        this.content = content;
    }
    
    /**
     * Get post content
     * 
     * @return The post content
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Set post content
     * 
     * @param content The post content
     */
    public void setContent(String content) {
        this.content = content;
    }
} 
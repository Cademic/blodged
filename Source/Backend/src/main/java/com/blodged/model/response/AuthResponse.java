package com.blodged.model.response;

/**
 * Response model for authentication results
 */
public class AuthResponse {
    
    private long id;
    private String email;
    private String username;
    private String role;
    
    /**
     * Default constructor
     */
    public AuthResponse() {}
    
    /**
     * Constructor with all fields
     * 
     * @param id User ID
     * @param email User email
     * @param username User username
     * @param role User role
     */
    public AuthResponse(long id, String email, String username, String role) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.role = role;
    }

    /**
     * Get the user ID
     * 
     * @return User ID
     */
    public long getId() {
        return id;
    }

    /**
     * Set the user ID
     * 
     * @param id User ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the user email
     * 
     * @return User email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the user email
     * 
     * @param email User email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the username
     * 
     * @return Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username
     * 
     * @param username Username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the user role
     * 
     * @return User role
     */
    public String getRole() {
        return role;
    }

    /**
     * Set the user role
     * 
     * @param role User role
     */
    public void setRole(String role) {
        this.role = role;
    }
} 
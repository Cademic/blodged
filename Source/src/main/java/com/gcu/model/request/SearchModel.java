package com.gcu.model.request;
/**
 * A model class that thymeleaf uses with forms to store data in. 
 * An instance of this class is sent to the POST controller with the data from the html form. 
 * This model is used to get the search term from the html form. 
 */
public class SearchModel {
    private String searchTerm;
    /**
     * getter for searchTerm
     * @return returns the search term.
     */
    public String getSearchTerm() {
        return searchTerm;
    }
    /**
     * setter for searchTerm
     * @param searchTerm the term that you want to set
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }



}

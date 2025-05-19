package com.blodged.model.db;
/**
 * abstract class that contains properties that every object in the database has
 */
public abstract class DBModel {
    protected long id;
    /**
     * getter for id
     * @return returns the id of the item from the db
     */
    public long getId() {
        return id;
    }
    protected DBModel(long id) {
        this.id=id;
    }
}

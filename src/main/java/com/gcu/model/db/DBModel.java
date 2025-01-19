package com.gcu.model.db;

public abstract class DBModel {
    protected long id;
    public long getId() {
        return id;
    }
    protected DBModel(long id) {
        this.id=id;
    }
}

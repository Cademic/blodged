package com.gcu.data.entities.metadata;

import jakarta.annotation.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
/**
 * API representation of the users database object
 */
@Table("users")
public class UserMetadataEntity {

    @Id
    public long id = 0;
    @Column
    public String username;
    @Column
    @Nullable
    public String bio;
    @Column
    public Timestamp createdDate;
    @Column(value = "followedByCount")
    public int followedByCount;
    @Column(value = "followingCount")
    public int followingCount;
    @Column(value = "likesGiven")
    public  int likesGiven;


}

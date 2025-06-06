package com.blodged.data.entities.metadata;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * API representation of the post database object
 */
@Table("posts")
public class PostMetadataEntity {

    @Id
    public long id;

    @Column(value = "authorUsername")
    public String authorUsername;
    @Column(value = "post_content")
    public String postContent;
    @Column(value = "likeCount")
    public int likeCount;

}

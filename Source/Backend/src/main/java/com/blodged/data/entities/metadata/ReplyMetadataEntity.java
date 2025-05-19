package com.blodged.data.entities.metadata;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
/**
 * API representation of the reply database object
 */
@Table("replies")
public class ReplyMetadataEntity {

    @Id
    public long id;
    @Column("replyAuthorUsername")
    public String replyAuthorUsername;
    @Column("post_id")
    public long postId;
    @Column("content")
    public String replyContent;

}

package com.gcu.data.entities.metadata;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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

package com.ll.exam.mutbooks.app.hasTag.entity;

import com.ll.exam.mutbooks.app.base.entity.BaseEntity;
import com.ll.exam.mutbooks.app.keyword.entity.Keyword;
import com.ll.exam.mutbooks.app.post.entity.Post;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class HashTag extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    private Post post;
    @ManyToOne
    @ToString.Exclude
    private Keyword keyword;
}

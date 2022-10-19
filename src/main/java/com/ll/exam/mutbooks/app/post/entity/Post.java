package com.ll.exam.mutbooks.app.post.entity;

import com.ll.exam.mutbooks.app.base.entity.BaseEntity;
import com.ll.exam.mutbooks.app.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
public class Post extends BaseEntity {
    private String subject;
    private String content;
    @ManyToOne(fetch = LAZY)
    private Member author;

}
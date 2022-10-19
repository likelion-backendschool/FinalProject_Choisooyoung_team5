package com.ll.exam.mutbooks.app.keyword.entity;

import com.ll.exam.mutbooks.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Keyword extends BaseEntity {
    private String content;

    public String getListUrl() {
        return "/post/list?kwType=keyword&kw=%s".formatted(content);
    }
}

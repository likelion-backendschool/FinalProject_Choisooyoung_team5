package com.ll.exam.mutbooks.app.hasTag.repository;

import com.ll.exam.mutbooks.app.hasTag.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    Optional<HashTag> findByPostIdAndKeywordId(Long postId, Long keywordId);

    List<HashTag> findAllByPostId(Long postId);

    List<HashTag> findAllByPostIdIn(long[] postIds);
}

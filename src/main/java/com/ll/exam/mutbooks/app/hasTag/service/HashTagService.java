package com.ll.exam.mutbooks.app.hasTag.service;

import com.ll.exam.mutbooks.app.hasTag.entity.HashTag;
import com.ll.exam.mutbooks.app.hasTag.repository.HashTagRepository;
import com.ll.exam.mutbooks.app.keyword.entity.Keyword;
import com.ll.exam.mutbooks.app.keyword.service.KeywordService;
import com.ll.exam.mutbooks.app.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashTagService {
    private final HashTagRepository hashTagRepository;

    private final KeywordService keywordService;
    public void applyHashTags(Post post, String hashTagContents) {
        List<HashTag> oldHashTags = getHashTags(post);

        List<String> keywordContents = Arrays.stream(hashTagContents.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0).toList();

        List<HashTag> needToDelete = new ArrayList<>();

        for (HashTag oldHashTag : oldHashTags) {
            boolean contains = keywordContents.stream().anyMatch(s -> s.equals(oldHashTag.getKeyword().getContent()));

            if (!contains) {
                needToDelete.add(oldHashTag);
            }
        }

        needToDelete.forEach(hashTag -> {
            hashTagRepository.delete(hashTag);
        });

        keywordContents.forEach(keywordContent -> {
            saveHashTag(post, keywordContent);
        });
    }

    private HashTag saveHashTag(Post post, String keywordContent) {
        Keyword keyword = keywordService.save(keywordContent);

        Optional<HashTag> opHashTag = hashTagRepository.findByPostIdAndKeywordId(post.getId(), keyword.getId());

        if (opHashTag.isPresent()) {
            return opHashTag.get();
        }

        HashTag hashTag = HashTag.builder()
                .post(post)
                .keyword(keyword)
                .build();

        hashTagRepository.save(hashTag);

        return hashTag;
    }

    public List<HashTag> getHashTags(Post post) {
        return hashTagRepository.findAllByPostId(post.getId());
    }
}

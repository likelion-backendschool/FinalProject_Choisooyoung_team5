package com.ll.exam.mutbooks.app.keyword.service;

import com.ll.exam.mutbooks.app.keyword.entity.Keyword;
import com.ll.exam.mutbooks.app.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;

    public Keyword save(String keywordContent) {
        Optional<Keyword> optKeyword = keywordRepository.findByContent(keywordContent);

        if ( optKeyword.isPresent() ) {
            return optKeyword.get();
        }

        Keyword keyword = Keyword
                .builder()
                .content(keywordContent)
                .build();

        keywordRepository.save(keyword);

        return keyword;
    }
}

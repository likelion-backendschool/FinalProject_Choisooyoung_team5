package com.ll.exam.mutbooks.app.post.service;

import com.ll.exam.mutbooks.app.member.entity.Member;
import com.ll.exam.mutbooks.app.post.entity.Post;
import com.ll.exam.mutbooks.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public Post write(Member author, String subject, String content) {
        Post post = Post.builder()
                .subject(subject)
                .content(content)
                .author(author)
                .build();
        postRepository.save(post);

        return post;
    }

    public Optional<Post> findForPrintById(Long id) {
        Optional<Post> opPost = findById(id);

        if (opPost.isEmpty()) return opPost;

        return opPost;
    }

    private Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }
}

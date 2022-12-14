package com.ll.exam.mutbooks.app.post.controller;

import com.ll.exam.mutbooks.app.member.entity.Member;
import com.ll.exam.mutbooks.app.post.entity.Post;
import com.ll.exam.mutbooks.app.post.exception.ActorCanNotDeleteException;
import com.ll.exam.mutbooks.app.post.exception.ActorCanNotModifyException;
import com.ll.exam.mutbooks.app.post.form.PostForm;
import com.ll.exam.mutbooks.app.post.service.PostService;
import com.ll.exam.mutbooks.app.security.dto.MemberContext;
import com.ll.exam.mutbooks.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite() {
        return "post/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@AuthenticationPrincipal MemberContext memberContext, @Valid PostForm postForm) {
        Member author = memberContext.getMember();
        Post post = postService.write(author, postForm.getSubject(), postForm.getContent(), postForm.getHashTagContents());
        return "redirect:/post/" + post.getId() + "?msg=" + Ut.url.encode("%d번 글이 생성되었습니다.".formatted(post.getId()));
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Post post = postService.findForPrintById(id).get();

        model.addAttribute("post", post);

        return "post/detail";
    }

    @GetMapping("list")
    public String list(Model model) {
        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        return "post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model) {
        Post post = postService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        if (postService.actorCanModify(actor, post) == false) {
            throw new ActorCanNotModifyException();
        }

        model.addAttribute("post", post);

        return "post/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, @Valid PostForm postForm) {
        Post post = postService.findById(id).get();

        Member actor = memberContext.getMember();

        if (postService.actorCanModify(actor, post) == false) {
            throw new ActorCanNotModifyException();
        }

        postService.modify(post, postForm.getSubject(), postForm.getContent());

        return "redirect:/post/" + post.getId() + "?msg=" + Ut.url.encode("%d번 글이 수정되었습니다.".formatted(post.getId()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Post post = postService.findById(id).get();

        if (!post.getAuthor().getUsername().equals(memberContext.getUsername())) {
            throw new ActorCanNotDeleteException();
        }
        postService.delete(post);

        return "redirect:/post/list?msg=" + Ut.url.encode("%d번 글이 삭제되었습니다.".formatted(post.getId()));
    }

}

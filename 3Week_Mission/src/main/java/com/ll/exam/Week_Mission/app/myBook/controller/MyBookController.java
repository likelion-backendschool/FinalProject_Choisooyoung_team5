package com.ll.exam.Week_Mission.app.myBook.controller;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.myBook.entity.MyBook;
import com.ll.exam.Week_Mission.app.myBook.service.MyBookService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/myBook")
public class MyBookController {
    private final MyBookService myBookService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member member = memberContext.getMember();
        List<MyBook> myBooks = myBookService.findAllByMemberId(member.getId());
        model.addAttribute("myBooks", myBooks);
        return "myBook/list";
    }
}

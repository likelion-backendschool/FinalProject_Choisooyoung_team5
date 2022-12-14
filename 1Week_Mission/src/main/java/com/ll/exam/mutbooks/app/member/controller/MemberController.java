package com.ll.exam.mutbooks.app.member.controller;

import com.ll.exam.mutbooks.app.member.entity.Member;
import com.ll.exam.mutbooks.app.member.form.JoinForm;
import com.ll.exam.mutbooks.app.member.form.PswForm;
import com.ll.exam.mutbooks.app.member.service.MemberService;
import com.ll.exam.mutbooks.app.security.dto.MemberContext;
import com.ll.exam.mutbooks.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin(HttpServletRequest request) {
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/member/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }

        return "member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
        memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getEmail(), joinForm.getNickname());

        return "redirect:/member/login?msg=" + Ut.url.encode("회원가입이 완료되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String showModify() {
        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@AuthenticationPrincipal MemberContext context, String email, String nickname) {
        Member member = memberService.findByUsername(context.getUsername()).get();

        memberService.modify(member, email, nickname);

        // 기존에 세션에 저장된 MemberContext 객체의 내용을 수정하는 코드 시작
        context.setModifyDate(member.getModifyDate());
        context.setNickname(nickname);
        context.setEmail(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(context, member.getPassword(), context.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 기존에 세션에 저장된 MemberContext 객체의 내용을 수정하는 코드 끝

        return "redirect:/member/profile?msg=" + Ut.url.encode("회원정보가 수정되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile() {
        return "member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyPassword")
    public String showModifyPassword() {
        return "member/modifyPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modifyPassword")
    public String modifyPassword(@AuthenticationPrincipal MemberContext context, @Valid PswForm pswForm) {
        Member member = memberService.findByUsername(context.getUsername()).get();

        if (passwordEncoder.matches(pswForm.getOldPassword(),member.getPassword())) {
            memberService.modifyPassword(member, pswForm.getPassword());

            return "redirect:/member/profile?msg=" + Ut.url.encode("비밀번호가 변경되었습니다.");
        }
        else return "redirect:/member/modifyPassword?errorMsg=" + Ut.url.encode("비밀번호가 올바르지 않습니다.");

    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findUsername")
    public String showFindUsername() {
        return "member/findUsername";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findUsername")
    public String findUsername(String email) {
        Member member = memberService.findByEmail(email);
        if (member != null) {
            return "redirect:/member/findUsername?msg=" + Ut.url.encode("아이디는 " + member.getUsername() + " 입니다.");
        }
        else return "redirect:/member/findUsername?errorMsg=" + Ut.url.encode("이메일 정보가 올바르지 않습니다.");
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findPassword")
    public String showFindPassword() {
        return "member/findPassword";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findPassword")
    public String findPassword(String username, String email) {
        Member member = memberService.findByUsernameAndEmail(username, email);

        if (member != null) {
            memberService.sendEmailAndChangePassword(username ,email);
            return "redirect:/member/findPassword?msg=" + Ut.url.encode("해당 이메일로 임시번호를 발송하였습니다.");
        }
        else return "redirect:/member/findPassword?errorMsg=" + Ut.url.encode("입력하신 정보가 올바르지 않습니다.");
    }
}
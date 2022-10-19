package com.ll.exam.mutbooks.service;

import com.ll.exam.mutbooks.app.member.entity.Member;
import com.ll.exam.mutbooks.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MemberServiceTests {
    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("작가 닉네임이 있는 회원가입")
    void t1() {
        String username = "user10";
        String password = "1234";
        String email = "user10@test.com";
        String nickname = "작가1";

        memberService.join(username, password, email, nickname);

        Member foundMember = memberService.findByUsername("user10").get();
        assertThat(foundMember.getCreateDate()).isNotNull();
        assertThat(foundMember.getUsername()).isNotNull();
        assertThat(foundMember.getNickname()).isNotNull();
        assertThat(passwordEncoder.matches(password, foundMember.getPassword())).isTrue();
    }

    @Test
    @DisplayName("작가 닉네임이 없는 회원가입")
    void t2() {
        String username = "user11";
        String password = "1234";
        String email = "user11@test.com";

        memberService.join(username, password, email);
        Member foundMember = memberService.findByUsername("user11").get();
        assertThat(foundMember.getCreateDate()).isNotNull();
        assertThat(foundMember.getUsername()).isNotNull();
        assertThat(foundMember.getNickname()).isNull();
        assertThat(passwordEncoder.matches(password, foundMember.getPassword())).isTrue();
    }
}
package com.ll.exam.mutbooks.app.member.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PswForm {
    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String password;
    @NotEmpty
    private String passwordConfirm;
}

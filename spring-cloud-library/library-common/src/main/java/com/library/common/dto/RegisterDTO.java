package com.library.common.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String realName;
    private String role;
    private String phone;
    private String email;
}

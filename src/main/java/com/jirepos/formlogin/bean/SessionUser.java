package com.jirepos.formlogin.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/** 세션에 저장할 사용자 정보이다. */
@Getter
@Setter 
@AllArgsConstructor
@NoArgsConstructor
public class SessionUser {
    /** 이름 */
    private String name;
    /** 패스워드 */
    private String password;
    /** 이메일 */
    private String email;
    /** 역할 */
    private String role;
    /** 사진 URL */
    private String picture; 
    /** accessToken */
    private String accessToken; 
}///~

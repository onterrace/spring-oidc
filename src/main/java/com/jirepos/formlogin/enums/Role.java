package com.jirepos.formlogin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/** 
 * 로그인 사용자 역할. 스프링 시큐리티에서는 역할에 따라 접근을 제한할 수 있는데 
 * enum을 생성하여 사용한다. 
 */
@AllArgsConstructor
@Getter
public enum Role {
    // 접두사 ROLE_ 을 붙여야 한다.
    /** Member 역할 */
    MEMBER("ROLE_MEMBER"),
    /** ADMIN 역할  */
    ADMIN("ROLE_ADMIN");
    
    private String value;
}//~

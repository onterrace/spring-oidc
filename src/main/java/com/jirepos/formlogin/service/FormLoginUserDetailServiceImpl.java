package com.jirepos.formlogin.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jirepos.formlogin.enums.Role;

import lombok.RequiredArgsConstructor;

/**
 * 스프링 시큐리티에서 Form Login 사용 시 사용자 인증 서비스이다. 
 */
// UserDetailsService를 구현해야 한다. 
@Service
@RequiredArgsConstructor
public class FormLoginUserDetailServiceImpl implements UserDetailsService  {

    /** 패스워드 인코더 */
    private final PasswordEncoder passwordEncoder;
    // 실제 개발에서는 사용자를 조회할 Repository를 선언해야 한다. 
    // (1) private final UserRepository userRepository;
    /**
     * 인증된 사용자 정보를 반환한다. 
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return createUser(username);
    }

    
    /** UserDetails 인스턴스를 생성한다. */
    private UserDetails createUser(String username) {
        // 사용자 정보를 DB에서 조회한다.
        // UserInfo userInfo = userRepository.findByUsername(username);
        // 조회한 사용자 정보를 이용하여 UserDetails 객체를 생성한다.
        // 권한을 String으로 변환한다. 
        // 적절한 사용자 권한을 설정한다
        // 사용자 정보를 UserDetails 타입으로 변환한다.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(Role.ADMIN.getValue());  // ADMIN
        return User.builder()
                .username(username)
                // 사용자를 등록할 때 패스워드는 암호화 되어 저장이 되어야 한다. 
                // 패스워드를 암호화 하기 위해서는 Spring Security가 사용하는 동일한  PasswordEncoder를 사용해야 한다. 
                // 실제 개발에서는 encode() 하지 않는다. 즉, DB에서 조회한 값 그대로 설정한다. 
                .password( passwordEncoder.encode("1234"))
                .authorities(authority)
                .build();
    }

}///~

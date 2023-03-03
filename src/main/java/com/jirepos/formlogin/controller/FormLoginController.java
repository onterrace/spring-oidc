package com.jirepos.formlogin.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.extern.slf4j.Slf4j;

/** Spring Scurity Form Login 테스트용 컨트롤러이다. */
@Controller
@Slf4j
public class FormLoginController {

    /** 사용자 정의 로그인 페이지 */
    @GetMapping("/formlogin")
    public String login() {
        // Security Configuration Java에서 설정한 로그인 페이지이다.
        // http.formLogin()
        // .loginPage("/formlogin") // default 는 /login
        return "formlogin";
    }

    /** 로그인 후 랜딩 페이지 */
    @GetMapping("/home")
    public String home() {
        // Security Configuration Java에서 설정한 로그인 페이지이다.
        // http.formLogin()
        // .loginPage("/formlogin") // default 는 /login
        // .defaultSuccessUrl("/home", true); // 로그인 인증을 성공하면 이동하는 페이지를 등록한다. true 설정을 해 주어야 동작
        return "home";
    }

    /** 로그인 사용자 정보 표시 */
    @GetMapping("/user-info")
    public String userInfo(HttpServletRequest request, Model model) {

        // 로그인을 통해 인증된 유저 정보는 Security Context Holder에 저장되며 아래와 같이 가져올 수 있다. 
        // SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // import org.springframework.security.core.annotation.AuthenticationPrincipal;
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails user = (UserDetails) obj;
        log.debug("user: {}", user.getUsername());
        System.out.println("user: " + user.getUsername());
        // request.setAttribute("user", user);

        model.addAttribute("user", user); // mustache에서 사용하려면 Model 객체에 담아야 한다.
        model.addAttribute("greeting", "Hello");
        request.setAttribute("greeting", "Hello"); // mustache에서 사용할 수 없다.
        return "userinfo";

    }

}

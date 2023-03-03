package com.jirepos.oauth2.controller;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.jirepos.core.util.ServletUtils;

/** OAuth2, OpenID connect 로그인 테스트를 위한 컨트롤러이다. */
@Controller
public class OAuthController {

    /** Social Login을 위한 페이지를 반환 */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** OIDC로그인 후 사용자 정보 조회  */
    @GetMapping("/oidcuser")
    public String oidcuser(Model model) {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (obj instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) obj;
            Map<String, Object> claimMap = oidcUser.getClaims();
            for (String key : claimMap.keySet()) {
                System.out.println(key + " : " + claimMap.get(key));
            }
            model.addAttribute("oidcuser", claimMap); 
            // userInfo가 null임 
            // model.addAttribute("oidcuser", oidcUser.getUserInfo());
        }
        // OAuth2  처리해야 함 
        // 세션에서 accessToken을 가져옴
        Optional<HttpServletRequest> opt = ServletUtils.getRequest();
        HttpServletRequest request = opt.get();
        String accessToken = (String)request.getSession().getAttribute("accessToken");
        model.addAttribute("accessToken", accessToken);
        
        return "oidcuser";
    }

    
    @GetMapping("/login/{providerType}")
    public String login (HttpServletRequest request, HttpServletResponse response, @PathVariable String providerType) {
        // /oauth2/authorization/google 
        // 스프링 시큐리티에서 기본적으로 제공하는 로그인 URL
        // 로그아웃과 마찬가지로 개발자가 별도의 컨트롤러를 생성할 필요 없음
        return "redirect:/oauth2/authorization/" + providerType;
    }
    
}

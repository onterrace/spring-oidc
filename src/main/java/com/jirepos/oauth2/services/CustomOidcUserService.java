package com.jirepos.oauth2.services;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.jirepos.core.util.ServletUtils;

import lombok.RequiredArgsConstructor;

/**
 * OpenID connect 로그인 시 사용자 정보를 얻기 위해 사용한다. 
 */
@RequiredArgsConstructor
@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Open ID Connect인 경우 User name Attribute Key 가 sub 이기 때문에 재정의함
        ClientRegistration clientRegistration = ClientRegistration
                .withClientRegistration(userRequest.getClientRegistration())
                .userNameAttributeName("sub")
                .build();
        OidcUserRequest oidcUserRequest = new OidcUserRequest(clientRegistration, userRequest.getAccessToken(),
                userRequest.getIdToken(), userRequest.getAdditionalParameters());
        
        Optional<HttpServletRequest> opt = ServletUtils.getRequest();
        HttpServletRequest request = opt.get();
        // Access Token을 저장해 놓아야 나중에 API 호출 시 사용할 수 있음
        // OidcUserRequest의 getAccessToken().getTokenValue()를 사용하여 Access Token을 얻을 수 있음
        request.getSession().setAttribute("accessToken", userRequest.getAccessToken().getTokenValue());
        
        OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService = new OidcUserService();
        OidcUser oidcUser = oidcUserService.loadUser(oidcUserRequest);

        // debug 
        System.out.println("Id Token:" + oidcUser.getIdToken());
        // Id Token:org.springframework.security.oauth2.core.oidc.OidcIdToken@18c4aa01
        Map<String, Object> attributes = oidcUser.getClaims();
        attributes.forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });

        // 아래는 Map<String, Object> attributes = oidcUser.getClaims(); 코드에서 출력된 결과이다. 
        // 
        // at_hash:UX7FOfXTC45dqomxV3o4eKg
        // sub:986690764843726136643
        // email_verified:true
        // iss:https://accounts.google.com
        // given_name:Ejin
        // locale:ko
        // nonce:8mWALrPLDFQM6grYMDc_hcllc4KJvBPh0-2INkaTHCY
        // picture:https://lh3.googleusercontent.com/a/AEdFTp4UGpzcxuuMCRgtT99XP5T1IpfXPI-bq9t7sfRA=s96-c
        // aud:[123456679076-sm8rp0rvvmjpcdfesgf88d9bciv2vtr5.apps.googleusercontent.com]
        // azp:123456679076-sm8rp0rvvmjpcdfesgf88d9bciv2vtr5.apps.googleusercontent.com
        // name:User1 Kim
        // exp:2023-02-06T09:55:05Z  // 유효시간, 1시간 
        // family_name:Parkm
        // iat:2023-02-06T08:55:05Z  // 발급시간
        // email:user1@gmail.com
        //
        return oidcUser;
    }
}

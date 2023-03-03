package com.jirepos.oauth2.services;

import java.util.Collections;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.jirepos.core.util.ServletUtils;
import com.jirepos.formlogin.bean.SessionUser;
import com.jirepos.oauth2.attrs.OAuthAttributes;

import lombok.RequiredArgsConstructor;

/** OAuth2 로그인 시 로그인 처리를 위한 클래스이다. */
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        
        // DefaultOAuth2UserService는 표준 OAuth 2.0 제공자를 지원하는 OAuth2UserService 구현체이다. 
        // https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/client/userinfo/DefaultOAuth2UserService.html

        // loadUser(OAuth2UserRequest userRequest) 메서드는 UserInfo Endpoint에서 사용자를 로드하는 데 사용된다. 
        // https://velog.io/@leeeeeyeon/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%8B%9C%ED%81%90%EB%A6%AC%ED%8B%B0%EC%99%80-OAuth-2.0%EC%9C%BC%EB%A1%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
        // 
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/client/registration/ClientRegistration.html
        // 
        // ClientRegistration 은 OAuth 2.0 또는 OpenID Connect 1.0 공급자에 대한 클라이언트 등록을 나타낸다. 
        // 메소드들:
        //  getClientId() - the client identifier 
        //  getClientSecret() - the client secret
        //  getRedirectUri() - the uri (or uri template) for the redirection endpoint.
        // 
        // applicaiton.yml의  spring.security.oauth2.client.registration.google이 registrationID가 된다. 
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        // userNameAttributeName은  최종 사용자의 이름 또는 식별자를 참조하는 UserInfo 응답에 반환된 속성의 이름입니다.
        // 이것은 OAuth를 지원하는 소셜 서비스들 간의 약속이라고 보면됩니다.
        // 어떤 소셜 서비스든 그 서비스에서 각 계정마다의 유니크한 ID 값을 전달해 주겠다는 의미입니다.
        // * 구글은 sub라는 필드가 유니크한 필드
        // * 네이버는 id라는 필드가 유니크 필드입니다. 
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스입니다.
        // A representation of a client registration with an OAuth 2.0 or OpenID Connect 1.0 Provider.
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // Session에 담을 사용자 정보를 생성한다. 
        // 
        // SessionUser sessUser = new SessionUser(attributes.getName(), attributes.getEmail(), attributes.getPicture());
        SessionUser sessUser = new SessionUser();
        sessUser.setName(attributes.getName());
        sessUser.setEmail(attributes.getEmail());
        sessUser.setPicture(attributes.getPicture());


        // 세션에 저장한다. 
        Optional<HttpServletRequest> request = ServletUtils.getRequest();
        if(!request.isEmpty()){ 
            HttpServletRequest req = request.get();
            req.getSession().setAttribute("user", sessUser);
        }
        //  OAuth2User를 생성하여 반환한다. 
        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")) // OAuth2.0 token과 연관된 GreantedAuthoriies의 Collection 
            , attributes.getAttributes()  // user에 관한 attributes 
            , attributes.getNameAttributeKey()  // getAttributes()에서 사용자의 "name"을 접근하기 위해 사용하는 키 
            );
    }
}///~

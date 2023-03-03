package com.jirepos.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.jirepos.core.util.ServletUtils;
import com.jirepos.oauth2.handlers.AuthFailHandler;
import com.jirepos.oauth2.handlers.AuthSuccessHandler;
import com.jirepos.oauth2.services.CustomOAuth2UserService;
import com.jirepos.oauth2.services.CustomOidcUserService;

import lombok.RequiredArgsConstructor;

/**
 * 스프링 시큐리티 구성 파일이다. 
 * 
 * 스프링 버전이 업데이트 됨에 따라 WebSecurityConfigurerAdapter와 그 외 몇 가지들이 Deprecated 되었다. 
 * 기존에는 WebSecurityConfigurerAdapter를 상속받아 설정을 오버라이딩 하는 방식이었는데 바뀐 방식에서는 상속받아 
 * 오버라이딩하지 않고 모두 Bean으로 등록을 한다. 
 */
@EnableWebSecurity // Spring Security를 활성화 시킨다. 
@Configuration
// final 필드에 대해 생성자를 만들어주는 lombok의 annotation.
// 새로운 필드를 추가할 때 다시 생성자를 만드는 번거로움을 없앨 수 있다. ( @Autowired 사용하지 않고 의존성 주입 )
@RequiredArgsConstructor
public class WebSecurityConfig {

    /** OAuth2 로그인 시 로그인 처리를 위한 서비스 클래스 */
    private final CustomOAuth2UserService customOAuth2UserService;
    /** OIDC로그인 시 로그인 처리를 위한 서비스 클래스 */
    private final CustomOidcUserService customOidcUserService;
    /** 로그인 성공 시 처리할 핸들러 */
    // private final SuccessHandler successHandler;
    /** 로그인 실패 시 처리할 핸들러 */
    private final AuthFailHandler authFailHandler;

    // 공식 홈페이지를 보면, spring security 5.7이상에서 더 이상 WebSecurityConfigurerAdapter 사용을
    // 권장하지 않는다고 한다. 스프링 버전이 업데이트 됨에 따라 WebSecurityConfigurerAdapter와 그 외 몇
    // 가지들이 Deprecated 되었다. 기존에는 WebSecurityConfigurerAdapter를 상속받아 설정을 오버라이딩 하는
    // 방식이었는데 바뀐 방식에서는 상속받아 오버라이딩하지 않고 모두 Bean으로 등록을 한다.
    // SecurityFilterChain Bean 등록을 통해 해결한다.
    // ​SecurityFilterChain은 Filter 보다 먼저 실행된다.
    // Custom filter가 Spring Security Filter 보다 먼저 동작하게 하려면 application.yml에 다음과 같이 추가할 수 있다.
    // ```properties
    // spring.security.filter.order=10
    // ```
    // 이렇게 하면 Spring Security Filter 앞에 10개의 Filter를 넣을 수 있는 공간이 생긴 셈이다.


    /**
     * 정적자원에 대한 접근 시 모두 허용을 하는 FilterChain Bean을 반환한다.
     * 
     * 이 메소드가 반환하는 필터체인이 가장 먼저 실행된다. Order 어노테이션은 낮은 숫자가 우선순위가 높다.
     */
    // SecurityProperties.BASIC_AUTH_ORDER는 응용 프로그램 끝점에 대한 기본 인증을 구성하는 데 사용되는
    // SecurityFilterChain에 적용되는 순서이다.
    // @Order(SecurityProperties.BASIC_AUTH_ORDER -1)로 설정하면
    // @Order(SecurityProperties.BASIC_AUTH_ORDER) 보다 먼저 적용된다.
    // @Order(숫자)는 적은 숫자를 먼저 실행한다.
    //
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER - 1)
    public SecurityFilterChain filterStaticResources(HttpSecurity http) throws Exception {
        // http.requestMatchers()는 HttpServletRequest를 사용하여 요청을 매핑하는 데 사용할 수 있는
        // RequestMatcher를 구성하는 데 사용할 수 있다.
        http.requestMatchers(matchers -> matchers.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .antMatchers("/static/**") // static/ 모두 허용
                .antMatchers("/resources/**") // resources/ 모두 허용
                .antMatchers("/public/**") // public/ 모두 허용
        )
                .authorizeRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * 정적 자원을 제외한 보안 필터 적용을 위한 FilterChain Bean을 반환한다.
     */
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterMain(HttpSecurity http) throws Exception {

        http.httpBasic(); // http basic 사용. 사용자 인증방법으로는 HTTP Basic Authentication을 사용한다.;
        // http.cors().disable() // cors 방지
        http.csrf().disable();   // csrf 방지
        this.setHeaders(http);   // 보안 헤더 설정, 헤더 설정은 여기서 한다. 
        this.authorizeRequests(http);  // 요청 매처 권한 설정
        this.formLogin(http); // form login 설정
        // this.logout(http); // logout 설정

        // 로그인 유지라는 체크박스에 체크를 하고 로그인을 하면 쿠키를 생성하고, 쿠키를 삭제하지 않으면 다음 로그인 시에도 
        // 쿠키를 사용하여 로그인 하도록 하는 것을 의미
        // http.rememberMe().disable(); // 로그인 유지 기능 disable

        // this.sessionManagement(http); // Session 관련 설정

        // http.exceptionHandling()
        //   .authenticationEntryPoint(authenticationEntryPoint()) // 인증 실패시 401
        //   .accessDeniedHandler(accessDeniedHandler()); // 인가(권한) 실패시
        // accessDeniedHandler() 에서 처리하도록 한다. 403처리


        this.configureOauth2(http); // oauth2 로그인 설정

        return http.build();
    }// :


    /**
     * 이 메소드에서 설정한 패턴에 해당하는 요청은 인증을 필요로 한다.
     */
    private void authorizeRequests(HttpSecurity http) throws Exception {

        // 특정경로 지정해서 권한을 설정할때 antMatchers, mvcMatchers가 있다.
        //   - antMatchers는 제공된 개미 패턴과 일치하는 경우에만 호출되고,
        //   - mvcMatchers는 제공된 Spring MVC 패턴과 일치할 때 호출된다.
        // 예를들면, 
        //   - antMatchers(”/info”) 하면 /info URL과 매핑 되지만
        //   - mvcMatchers(”/info”)는 /info/, /info.html 이 매핑이 가능하다.

        // authorizeRequests()는 보안을 유지해야 하는 URL과 해당 URL에 액세스할 수 있는 역할을 지정할 수 있다.
        // 예를 들어 모든 사용자가 다른 URL에 액세스하도록 허용하면서 'ADMIN' 역할을 가진 사용자에게만 URL에 대한
        // 액세스를 제한할 수 있다. http 요청에 대해서 모든 사용자가 /** 경로로 요청할 수 있지만, /member/**
        // , /admin/** 경로는 인증된 사용자만 요청이 가능하다.

        http.authorizeRequests() // HttpServletRequest 요청 URL에 따라 접근 권한을 설정합니다.
                .antMatchers("/teams/**").permitAll() // Teams 허용
                .antMatchers("/login/**").permitAll() // login 허용
                .antMatchers("/graph/**").permitAll() // login 허용
                .antMatchers("/login").permitAll() // login 허용
                .antMatchers("/formlogin").permitAll() // login 허용
                .antMatchers("/logout").permitAll() // logout 허용
                .antMatchers("/**/oauth2/**").permitAll() // oauth2는 허용해야 함
                .antMatchers("/**/google/gmail-list").permitAll() // oauth2는 허용해야 함
                .antMatchers("/admin/**").hasRole("ADMIN") // admin 경로는 ADMIN 권한을 가진 사용자만 접근 가능
                // .antMatchers("/admin").hasRole("ADMIN") // admin 경로는 ADMIN 권한을 가진 사용자만 접근 가능
                .anyRequest().authenticated(); // anyRequest()는 모든 요청에 대해 인증된 사용자에게만 허용
    }

    /**
     * logout 처리 설정
     */
    private void logout(HttpSecurity http) throws Exception {
        // 스프링 시큐리티가 제공하는 Logout 기능은 세션 무효화, 인증토큰 삭제, 쿠키 정보 삭제, 로그인 페이지로 리다이렉트 등이 있다.
        // Spring Security는 로그 아웃 후 사용자를 특정 URL로 리디렉션 할 수있는 가능성을 제공, 이 것을 피하려면
        // disable()을 사용하면 된다.
        // logout()은 Logout 관련 설정을 진행할 수 있는 LogoutConfigurer를 반환한다.
        http.logout().disable(); // logout 방지
        // http.logout() // 로그 아웃을 진행
        //  .logoutUrl("/logout") // 로그아웃 URL 지정, 기본 주소는 /logout
        //  .logoutSuccessUrl("/") // 로그아웃 성공 후 이동할 경로 지정 .
        //  .deleteCookies("JSESSIONID", "remember-me") // 로그아웃 후 쿠키 삭제
        //  .addLogoutHandler(null) // 로그아웃 핸들러 추가
        //  .logoutSuccessHandler(null) // 로그아웃 성공 후 핸들러 추가
        //  .invalidateHttpSession(true); // 세션을 삭제하는 것을 지정 (세션이 삭제되면 쿠키도 삭제된다)
    }

    /**
     * 세션 처리 설정
     */
    private void sessionManagement(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionFixation()
                // 세션 유지를 위해 세션 아이디를 변경한다. 인증에 성공할 때마다 새로운 세션을 생성하고, 
                // 새로운 JSESSIONID를 발급. 서블릿 3.1에서 기본값
                .changeSessionId() 
                // SessionCreationPolicy.ALWAYS 스프링 시큐리티가 항상 새로운 세션을 생성
                // SessionCreationPolicy.NEVER 스프링 시큐리티가 새로운 세션을 생성하지 않음
                // SessionCreationPolicy.IF_REQUIRED 스프링 시큐리티가 필요 시에 생성(기본값).
                // SessionCreationPolicy.STATELESS 스프링 시큐리티가 새로운 세션을 생성하지 않음. 이 경우 세션을 사용하는 요청은
                // 세션을 사용하지 않습니다.
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1) // 같은 아이디로 1명만 로그인 할 수 있음
                // 신규 로그인 사용자의 로그인이 허용되고, 기존 사용자는 세션아웃 됨. true 면 현재 사용자 인증 실패
                .maxSessionsPreventsLogin(false); 
        // .expiredUrl(expiredUrl) // 세션이 만료되면 이동하는 페이지를 지정한다. 
        // 만료된 세션에 대한 전략을 지정한다. 
        // .expiredSessionStrategy(securitySessionExpiredStrategy.setDefaultUrl("/caution/session_out.html"));
    }

    /**
     * 보안 헤더 설정
     */
    private void setHeaders(HttpSecurity http) throws Exception {
        // Headers에 대해서는 다음을 참조한다. 
        // https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/headers.html

        // http 헤더에서 X-Frame-Options 속성을 가르키는데 frame 부분을 컨트롤 해준다.
        // 디폴트 값은 deny이며 어떠한 사이트에도 frame 상에서 보여질수 없게 된다.
        // - SAMEORIGIN : 같은 도메인에서만 프레임을 사용할 수 있습니다.
        // - DENY : 프레임을 사용할 수 없습니다.
        // - ALLOW-FROM : 특정 도메인에서만 프레임을 사용할 수 있습니다.
        //
        // 예) X-Frame-Options: DENY
        // 예) X-Frame-Options: SAMEORIGIN
        http.headers()
                .frameOptions().disable(); // frame 옵션 방지
        // .frameOptions().sameOrigin();   // frame 옵션 설정. 동일 도메인에서 iframe에 접근가능. 
        // .frameOptions().deny();         // frame 옵션 설정. 동일 도메인에서 iframe에 접근가능. 

        // X-XSS-Protection 헤더를 사용하여 XSS 공격을 방어합니다.
        //  - 1 : XSS 필터를 활성화합니다.
        //  - 0 : XSS 필터를 비활성화합니다.
        // 예) X-XSS-Protection: 1; mode=block
        http.headers()
                .xssProtection().block(false); // 1; mode=block : XSS 필터를 활성화하고, XSS 공격이 감지되면 응답을 차단한다.

        // X-Content-Type-Options 헤더를 사용하여 MIME 스니핑 공격을 방어한다. 
        // nosniff : 브라우저가 Content-Type을 무시하고, 서버가 제공한 MIME 타입을 사용한다. 
        // 예) X-Content-Type-Options: nosniff
        http.headers().contentTypeOptions().disable();

        // Content-Security-Policy 헤더를 사용하여 XSS 공격을 방어한다. 
        //   - default-src : 기본적으로 허용할 소스를 지정한다.
        //   - script-src : 스크립트를 로드할 소스를 지정한다.
        //   - style-src : 스타
        // http.headers().contentSecurityPolicy(csp
        // -> csp.policyDirectives("script-src 'self'   https://trustedscripts.example.com; object-src   https://trustedplugins.example.com; report-uri /csp-report-endpoint/"));


        // HTTPS는 보안을 강화하기 위한 기술로, 웹 접속시 HTTPS(HTTP over Secure Socket Layer) 프로토콜을 사용하도록 강제하는 기능이다.
        // Strict-Transport-Security 헤더를 사용하여 HTTPS를 사용하도록 유도한다. 
        http.headers().httpStrictTransportSecurity().disable();


        // http.headers().httpStrictTransportSecurity()
        //   .maxAgeInSeconds(31536000) // max-age : HTTPS를 사용하는 시간을 초 단위로 지정한다. 
        //   .includeSubDomains(true) // includeSubDomains : 서브 도메인도 HTTPS를 사용하도록 유도한다.
        //   .preload(true); // preload : HSTS preload list에 등록되어 있으면 HTTPS를 사용하지 않는 경우에도 HTTPS로 접속하도록 유도한다. 

    }// :




    /** 스프링 폼 로그인 설정 */
    private void formLogin(HttpSecurity http) throws Exception {
        // 사용자가 Server에 특정 URL을 요청하였을 때 해당 URL이 인증이 필요할 경우 Server는 Login 페이지를 return하게 된다. 
        // 사용자는 Login 페이지에서 ID와 Password를 입력하고, Server는 입력받은 ID와 Password를 확인하여 인증을 진행한다. 
        // 이러한 과정을 거친 후 사용자가 원래 접속하려던 url에 접속 요청을 할 경우 세션에 저장된 인증 토근으로 접근을 할 수 있게되며
        // 세션에 인증토큰이 있는 동안은 해당 사용자가 인증된 사용자라 판단하여 인증을 유지하게 된다. 
        // http.formLogin().disable(); // form login 방지
        // http.formLogin(); // 이 것만 설정하면 SpringBoot에서 제공하는 디폴트 로그인 화면 표시
        // - 로그인 되지 않았으면 /login 페이지로 이동
        // - 로그인 실패하면 /login?error 페이지로 이동
        // OAuth2와 URL이 겹치지 않도록 설정 
        http.formLogin() // form login
            .loginPage("/formlogin")   // 사용자 정의 로그인 페이지 경로와 로그인 인증 경로를 등록한다.
            .loginProcessingUrl("/perform_login") // 로그인 처리 URL, default URL은 /login, loginPage()를 수정했으면 이것을 지정해야 한다. 
            .defaultSuccessUrl("/home", true)     // 로그인 인증을 성공하면 이동하는 페이지를 등록. true 설정을 해 주어야 동작함.
            // .failureUrl("/formlogin") // 로그인 실패 후 이동 페이지. failureHnalder() 구현시 동작하지 않음
        // .usernameParameter("username") // 아이디 파라미터명 설정
        // .passwordParameter("password") // 비밀번호 파라미터명 설정
        
        // <로그인 성공 후 핸들러>
        // 아래는 직접 구현한 방법.
        // .successHandler(new AuthenticationSuccessHandler(){
        //          @Override
        //          public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
        //                  , Authentication authentication) throws IOException, ServletException {
        //              System.out.println("authentication : " + authentication.getName());
        //              response.sendRedirect("/"); // 인증이 성공한 후에는 root로 이동
        //          }
        // })
        // <로그인 실패 후 처리 핸들러>
        // 아래와 같이 처리할 수도 있고, SimpleUrlAuthenticationFailureHandler을 구현하는 방법도 있음 
        // .failureHandler(new AuthenticationFailureHandler() {
        //     @Override
        //     public void onAuthenticationFailure(HttpServletRequest request,
        //         HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //         // 로그인이 실패한 경우 여기서 처리 
        //         System.out.println("exception : " + exception.getMessage());
        //         // 여기서 redirect 해줘야 함, 에러 메시지 함께 보내줘야 함
        //         response.sendRedirect("/formlogin?error"); // 인증이 실패한 후에는 formlogin으로 이동
        //     }
        // })
        .failureHandler(authFailHandler)
        .permitAll() // 모든 사용자가 접근 가능
        .and()
        // 로그 아웃 설정
        .logout()
            .logoutUrl("/perform_logout") // 로그아웃 처리 URL, default URL은 /logout
            .logoutSuccessUrl("/formlogin") // 로그아웃 성공 후 이동 페이지
            .deleteCookies("JSESSIONID") // 로그아웃 후 쿠키 삭제
            .invalidateHttpSession(true) // 로그아웃 후 세션 무효화
            .permitAll();
    }// :

    /**
     * OAuth2 로그인 설정
     */
    private void configureOauth2(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2Login -> {
            oauth2Login
                    .loginPage("/login")
                    .failureUrl("/login?error")
                    // .defaultSuccessUrl("/board/index", true) // 사용자를 지정된 URL로 리디렉션
                    // google에 등록된 redirection URI로 도메인 부분을 제외하면 경로를 변경할 수 없다.
                    // http[s]://[도메인]/login/oauth2/code/google
                    .userInfoEndpoint(userInfoEndpoint -> { // / oauth2Login 성공 이후의 설정을 시작

                        // userInfoEndpoint()은 로그인 성공 후 사용자 정보를 가져올 때의 설정을 담당
                        // UserInfo 엔드포인트는 OpenID Connect 인증으로 인증되는 사용자에 대한 청구를 리턴한다. 
                        // 사용자에 대한 청구를 얻으려면 클라이언트는 액세스 토큰을 신임 정보로 사용하여 UserInfo 엔드포인트에 대한 요청을 작성해야 한다. 
                        // 액세스 토큰은 OpenID Connect 인증을 통해 얻은 것이어야 한다. 
                        // 액세스 토큰이 표시하는 사용자에 대한 청구는 해당 청구에 대한 이름-값 쌍의 콜렉션을 포함하는 JSON 오브젝트로 리턴된다. 
                        // 아래와 비슷한 응답을 받을 수 있다. 
                        //     HTTP/1.1 200 OK
                        //     Content-Type: application/json
                        //     Cache-Control: no-store
                        //     Pragma: no-cache
                        //    {
                        //       "sub"          : "bob",
                        //       "groupIds"     : [ "bobsdepartment","administrators" ], 
                        //       "given_name"   : "Bob",
                        //       "name"         : "Bob Smith",
                        //       "email"        : "bob@mycompany.com",
                        //       "phone_number" : "+1 (604) 555-1234;ext5678",
                        //       "address"      : { "formatted" : "123 Main St., Anytown, TX 77777" },
                        //       "picture"      : "http://mycompany.com/bob_photo.jpg"
                        //    }                        
                        userInfoEndpoint
                                .userService(customOAuth2UserService) // OAuth2 로그인
                                .oidcUserService(customOidcUserService); // OIDC 로그인

                    })
                    .successHandler(new AuthSuccessHandler("/oidcuser"))
                    .failureHandler(authFailHandler);

        });
    }// :


    /**
     * SuccessHandler를 Bean으로 생성하는 방법을 보여주기 위한 예시이다. 
     * @return
     */
    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler2() {
        return new SimpleUrlAuthenticationSuccessHandler("/board/index");
    }

    /**
     * 권한 없음 처리.
     * 
     * 403 에러는 접근 권한 없는 url 요청 시 반환되는 응답코드이다. 
     * 만약, 403에러 페이지가 아닌 다른 처리를 하고 싶다면 AccessDeniedHandler Bean을 생성한다. 
     * 
     * HttpSecurity.exceptionHandling()을 해줘야 한다.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, e) -> {
            response.sendRedirect("/error/403");
            // response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            // response.setContentType("text/plain;charset=UTF-8");
            // response.getWriter().write("ACCESS DENIED");
            // response.getWriter().flush();
            // response.getWriter().close();
        };
    }

    /**
     * 인증 실패시 처리. filterChain()의 http.authorizeRequests()에서 인증정보가 없으면 여기서 처리한다.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        // 로그인페이지 이동, 401오류 코드 전달
        return (request, response, e) -> {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // // Content-Type은 http2.js에서 사용하므로 정확히 맞추어야 한다.
            // response.setContentType("text/plain;charset=UTF-8");
            // response.getWriter().write("UNAUTHORIZED");
            // response.getWriter().flush();
            // response.getWriter().close();
            ServletUtils.responseUnauthorized(request, response);
        };
    }

    /**
     * 패스워드를 암호화하기 위해서 사용한다. 동일한 방식으로 암호화를 해야 비교할 수 있다.
     * UserDetailService를 구현한 클래스에 주입하기 위해서 @Bean으로 생성한다.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        // BCrypt가 가장 많이쓰이는 해싱 방법
        // 패스워드 인크립트할 때 사용
        return new BCryptPasswordEncoder();
    }
}///
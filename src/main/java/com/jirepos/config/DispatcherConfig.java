package com.jirepos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/** 스프링 Auto Configuration을 커스텀하기 위한 구성 파일 */
@Configuration
public class DispatcherConfig implements WebMvcConfigurer {

  /** Default Servlet을 활성화 한다. */
  public void configureDefaultServletHandling(
      org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  /**
   * src/main/resources/public, src/main/resources/static 정적 리소스 폴더를 사용할 수 있도록
   * 설정한다.
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // url을 지정하여 실제 리소스 경로를 설정
    // registry.addResourceHandler("/public/**").addResourceLocations("classpath:/public/",
    // "classpath:/static/");// .setCachePeriod(0);
    registry.addResourceHandler("/public/**").addResourceLocations("classpath:/public/");// .setCachePeriod(0);
    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");// .setCachePeriod(0);
  }


}

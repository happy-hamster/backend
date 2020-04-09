package de.sakpaas.backend.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class InterceptorAppConfig implements WebMvcConfigurer {
  private final RequestInterceptor requestInterceptor;

  @Autowired
  public InterceptorAppConfig(RequestInterceptor requestInterceptor) {
    this.requestInterceptor = requestInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestInterceptor);
  }
}
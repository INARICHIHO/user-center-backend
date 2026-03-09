package com.inari.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("None");      // 允许跨域携带 Cookie
        serializer.setUseSecureCookie(true); // 必须为 true，因为 Render 是 HTTPS
        serializer.setCookiePath("/");
        serializer.setDomainName("user-center-backend.onrender.com"); // 你的后端域名
        return serializer;
    }
}

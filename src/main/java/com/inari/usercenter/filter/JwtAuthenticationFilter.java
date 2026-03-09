package com.inari.usercenter.filter;

import com.inari.usercenter.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // 1. 处理预检请求（OPTIONS）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // 直接放行，让后续的CORS配置（或过滤器中的头）生效
            chain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        // 2. 放行登录和注册接口（无需token）
        if (path.contains("/login") || path.contains("/register")) {
            chain.doFilter(request, response);
            return;
        }

        // 3. 获取并验证token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                request.setAttribute("username", username);
                chain.doFilter(request, response);
                return;
            }
        }

        // 4. token无效或缺失：返回401，并添加CORS头以保证前端能捕获错误
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        // 明确允许前端域名（务必替换为您的实际前端地址）
        response.setHeader("Access-Control-Allow-Origin", "https://user-center-pied.vercel.app");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.getWriter().write("{\"code\":40100,\"message\":\"未登录\",\"data\":null}");
    }
}
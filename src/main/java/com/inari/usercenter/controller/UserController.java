package com.inari.usercenter.controller;

import com.inari.usercenter.dto.*;
import com.inari.usercenter.model.User;
import com.inari.usercenter.service.UserService;
import com.inari.usercenter.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;   // 注入 JWT 工具类

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request.getUsername(), request.getPassword());
            UserDTO dto = convertToDTO(user);
            return ApiResponse.success(dto);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<LoginResult> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            // 生成 JWT token
            String token = jwtUtil.generateToken(user.getUsername());
            LoginResult result = new LoginResult();
            result.setToken(token);
            result.setUserInfo(convertToDTO(user));
            return ApiResponse.success(result);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/current")
    public ApiResponse<UserDTO> getCurrentUser(HttpServletRequest request) {
        // 从请求头中获取 token（过滤器已解析并放入属性）
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return new ApiResponse<>(40100, "未登录", null);
        }
        // 根据用户名查询用户（需要 userService 提供 findByUsername 方法）
        User user = userService.findByUsername(username);
        if (user == null) {
            return new ApiResponse<>(40100, "用户不存在", null);
        }
        return ApiResponse.success(convertToDTO(user));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        // JWT 无状态，服务器端无需操作，前端删除 token 即可
        // 但可以保留一个空接口供前端调用
        return ApiResponse.success(null);
    }

    @GetMapping("/search")
    public ApiResponse<List<UserDTO>> searchUsers(@RequestParam(required = false) String username) {
        List<UserDTO> users = userService.searchUsers(username);
        return ApiResponse.success(users);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }

    // 辅助方法：将 User 实体转换为 UserDTO
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatar(user.getAvatar());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
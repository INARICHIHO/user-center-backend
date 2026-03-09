package com.inari.usercenter.controller;

import com.inari.usercenter.dto.*;
import com.inari.usercenter.model.User;
import com.inari.usercenter.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

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
    public ApiResponse<UserDTO> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            // 将用户对象存入 Session
            httpRequest.getSession().setAttribute("user", user);
            UserDTO dto = convertToDTO(user);
            return ApiResponse.success(dto);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/current")
    public ApiResponse<UserDTO> getCurrentUser(HttpServletRequest request) {
        // 从 Session 中获取用户
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            // 未登录，返回 40100 错误码（与前端 Mock 一致）
            return new ApiResponse<>(40100, "未登录", null);
        }
        return ApiResponse.success(convertToDTO(user));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        request.getSession().invalidate(); // 销毁 session
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
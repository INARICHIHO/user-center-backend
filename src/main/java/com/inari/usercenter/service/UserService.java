package com.inari.usercenter.service;

import com.inari.usercenter.dto.UserDTO;
import com.inari.usercenter.model.User;
import com.inari.usercenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 注册
    public User register(String username, String password) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 真实项目需要加密，这里先简单处理
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // 登录
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    public List<UserDTO> searchUsers(String keyword) {
        List<User> users;
        if (keyword == null || keyword.isEmpty()) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findByUsernameContaining(keyword);
        }
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatar(user.getAvatar());
        dto.setEmail(user.getEmail());
        return dto;
    }
}

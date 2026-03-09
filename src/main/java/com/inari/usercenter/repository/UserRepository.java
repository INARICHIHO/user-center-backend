package com.inari.usercenter.repository;

import com.inari.usercenter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // 根据用户名查询用户（用于登录和检查重复）
    Optional<User> findByUsername(String username);
    List<User> findByUsernameContaining(String username);
}

package com.library.user.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.library.common.dto.LoginDTO;
import com.library.common.dto.RegisterDTO;
import com.library.common.dto.Result;
import com.library.common.entity.User;
import com.library.common.vo.UserVO;
import com.library.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * User login - returns JWT token + user info
     * Protected by Sentinel QPS rate limiting
     */
    @PostMapping("/login")
    @SentinelResource(value = "login", blockHandler = "loginBlockHandler")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * User registration - returns JWT token + user info
     * Protected by Sentinel QPS rate limiting
     */
    @PostMapping("/register")
    @SentinelResource(value = "register", blockHandler = "registerBlockHandler")
    public Result<Map<String, Object>> register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    /**
     * Get current user info (requires auth)
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        return userService.getUserInfo();
    }

    /**
     * Admin API: list all users (requires ADMIN role, enforced by Gateway)
     */
    @GetMapping("/list")
    public Result<List<UserVO>> listUsers() {
        return userService.listUsers();
    }

    /**
     * Internal API: get user by ID (called by other services via Feign)
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error(404, "User not found");
        }
        // Don't expose password
        user.setPassword(null);
        return Result.success(user);
    }

    // ========== Sentinel Block Handlers ==========

    public Result<Map<String, Object>> loginBlockHandler(LoginDTO loginDTO, BlockException ex) {
        return Result.error(429, "Too many login attempts. Please try again later.");
    }

    public Result<Map<String, Object>> registerBlockHandler(RegisterDTO registerDTO, BlockException ex) {
        return Result.error(429, "Too many registration attempts. Please try again later.");
    }
}

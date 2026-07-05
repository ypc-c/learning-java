package org.score.studentscoresystem.controller;

import org.score.studentscoresystem.common.Result;
import org.score.studentscoresystem.dto.LoginDTO;
import org.score.studentscoresystem.entity.User;
import org.score.studentscoresystem.service.UserService;
import org.score.studentscoresystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        
        return Result.success(data);
    }

    @PostMapping("/reset-password")
    public Result<?> resetPassword(@RequestParam String phone, @RequestParam String newPassword) {
        User user = userService.findByPhone(phone);
        if (user == null) {
            return Result.error("手机号不存在");
        }
        
        boolean success = userService.resetPassword(user.getId(), newPassword);
        return success ? Result.success() : Result.error("密码重置失败");
    }

    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestHeader("Authorization") String token,
                                    @RequestParam String oldPassword,
                                    @RequestParam String newPassword) {
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserById(userId);
        
        if (!user.getPassword().equals(oldPassword)) {
            return Result.error("原密码错误");
        }
        
        boolean success = userService.resetPassword(userId, newPassword);
        return success ? Result.success() : Result.error("密码修改失败");
    }
}

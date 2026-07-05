package com.library.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.constant.Constants;
import com.library.common.dto.LoginDTO;
import com.library.common.dto.RegisterDTO;
import com.library.common.dto.Result;
import com.library.common.entity.User;
import com.library.common.utils.JwtUtil;
import com.library.common.vo.UserVO;
import com.library.user.mapper.UserMapper;
import com.library.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Result<Map<String, Object>> login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        if (username == null || password == null || username.trim().isEmpty()) {
            return Result.error(400, "Username and password must not be empty");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username.trim());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return Result.error(401, "Username or password is incorrect");
        }

        if (user.getStatus() == 0) {
            return Result.error(403, "Account has been disabled");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.error(401, "Username or password is incorrect");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // Return token + user info so frontend can store userId/role
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("userId", user.getId());
        resultMap.put("username", user.getUsername());
        resultMap.put("role", user.getRole());
        return Result.success("Login successful", resultMap);
    }

    @Override
    public Result<Map<String, Object>> register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();

        if (username == null || password == null || username.trim().isEmpty()) {
            return Result.error(400, "Username and password must not be empty");
        }

        if (password.length() < 6) {
            return Result.error(400, "Password must be at least 6 characters");
        }

        // Check duplicate username
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username.trim());
        if (userMapper.selectCount(wrapper) > 0) {
            return Result.error(400, "Username already exists");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(registerDTO.getRealName() != null ? registerDTO.getRealName() : username);
        // Security: Always force role to STUDENT — admin accounts must be created manually
        user.setRole(Constants.ROLE_STUDENT);
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setStatus(1);

        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // Return token + user info so frontend can store userId/role
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("userId", user.getId());
        resultMap.put("username", user.getUsername());
        resultMap.put("role", user.getRole());
        return Result.success("Registration successful", resultMap);
    }

    @Override
    public Result<UserVO> getUserInfo() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "Not logged in");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "User not found");
        }

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return Result.success(vo);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Result<List<UserVO>> listUsers() {
        List<User> users = userMapper.selectList(null);
        List<UserVO> voList = users.stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(voList);
    }

    private Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();
        String userIdHeader = request.getHeader(Constants.HEADER_USER_ID);
        if (userIdHeader != null) {
            return Long.valueOf(userIdHeader);
        }
        return null;
    }
}

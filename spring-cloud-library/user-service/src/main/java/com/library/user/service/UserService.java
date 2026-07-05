package com.library.user.service;

import com.library.common.dto.LoginDTO;
import com.library.common.dto.RegisterDTO;
import com.library.common.dto.Result;
import com.library.common.entity.User;
import com.library.common.vo.UserVO;

import java.util.List;
import java.util.Map;

public interface UserService {
    Result<Map<String, Object>> login(LoginDTO loginDTO);
    Result<Map<String, Object>> register(RegisterDTO registerDTO);
    Result<UserVO> getUserInfo();
    User getUserById(Long id);
    Result<List<UserVO>> listUsers();
}

package com.library.borrow.feign;

import com.library.common.dto.Result;
import com.library.common.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFeignFallback implements UserFeignClient {

    @Override
    public Result<User> getUserById(Long id) {
        return Result.error(503, "User service is currently unavailable");
    }
}

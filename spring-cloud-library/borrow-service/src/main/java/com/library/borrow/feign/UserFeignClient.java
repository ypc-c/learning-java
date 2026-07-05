package com.library.borrow.feign;

import com.library.common.dto.Result;
import com.library.common.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/user", fallback = UserFeignFallback.class)
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<User> getUserById(@PathVariable("id") Long id);
}

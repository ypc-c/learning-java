package com.library.common.utils;

import com.library.common.dto.Result;

public class ResultUtil {

    public static <T> Result<T> success(T data) {
        return Result.success(data);
    }

    public static <T> Result<T> success(String message, T data) {
        return Result.success(message, data);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return Result.error(code, message);
    }

    public static <T> Result<T> error(String message) {
        return Result.error(message);
    }
}

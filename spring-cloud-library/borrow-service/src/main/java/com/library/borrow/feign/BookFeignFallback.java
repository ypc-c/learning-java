package com.library.borrow.feign;

import com.library.common.dto.Result;
import com.library.common.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookFeignFallback implements BookFeignClient {

    @Override
    public Result<Book> getBookById(Long id) {
        return Result.error(503, "Book service is currently unavailable");
    }

    @Override
    public Result<String> updateStock(Long id, int delta) {
        return Result.error(503, "Book service is currently unavailable");
    }
}

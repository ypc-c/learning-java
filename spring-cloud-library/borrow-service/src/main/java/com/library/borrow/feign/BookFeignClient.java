package com.library.borrow.feign;

import com.library.common.dto.Result;
import com.library.common.entity.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "book-service", path = "/book", fallback = BookFeignFallback.class)
public interface BookFeignClient {

    @GetMapping("/{id}")
    Result<Book> getBookById(@PathVariable("id") Long id);

    @PutMapping("/{id}/stock")
    Result<String> updateStock(@PathVariable("id") Long id, @RequestParam("delta") int delta);
}

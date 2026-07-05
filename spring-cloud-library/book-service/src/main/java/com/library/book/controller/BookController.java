package com.library.book.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.book.service.BookService;
import com.library.common.dto.BookDTO;
import com.library.common.dto.Result;
import com.library.common.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * List books with pagination and search
     */
    @GetMapping("/list")
    public Result<Page<Book>> listBooks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return bookService.listBooks(page, size, keyword, category);
    }

    /**
     * Get book detail by ID
     */
    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    /**
     * Add a new book (admin only via gateway)
     */
    @PostMapping
    public Result<String> addBook(@RequestBody BookDTO bookDTO) {
        return bookService.addBook(bookDTO);
    }

    /**
     * Update book info (admin only via gateway)
     */
    @PutMapping("/{id}")
    public Result<String> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        return bookService.updateBook(id, bookDTO);
    }

    /**
     * Delete a book (admin only via gateway)
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id);
    }

    /**
     * Internal API: update stock (called by borrow-service via Feign)
     */
    @PutMapping("/{id}/stock")
    public Result<String> updateStock(@PathVariable Long id, @RequestParam int delta) {
        boolean success = bookService.updateStock(id, delta);
        if (success) {
            return Result.success("Stock updated successfully");
        }
        return Result.error(400, "Insufficient stock or book not found");
    }
}

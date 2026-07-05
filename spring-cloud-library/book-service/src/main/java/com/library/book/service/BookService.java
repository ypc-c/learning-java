package com.library.book.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.dto.BookDTO;
import com.library.common.dto.Result;
import com.library.common.entity.Book;

public interface BookService {
    Result<Page<Book>> listBooks(Integer page, Integer size, String keyword, String category);
    Result<Book> getBookById(Long id);
    Result<String> addBook(BookDTO bookDTO);
    Result<String> updateBook(Long id, BookDTO bookDTO);
    Result<String> deleteBook(Long id);
    boolean updateStock(Long id, int delta);
}

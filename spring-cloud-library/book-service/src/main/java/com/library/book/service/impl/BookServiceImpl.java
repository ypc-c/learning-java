package com.library.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.book.mapper.BookMapper;
import com.library.book.service.BookService;
import com.library.common.dto.BookDTO;
import com.library.common.dto.Result;
import com.library.common.entity.Book;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public Result<Page<Book>> listBooks(Integer page, Integer size, String keyword, String category) {
        Page<Book> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, 1);

        if (category != null && !category.isEmpty()) {
            wrapper.eq(Book::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Book::getTitle, keyword)
                    .or().like(Book::getAuthor, keyword)
                    .or().like(Book::getIsbn, keyword));
        }
        wrapper.orderByDesc(Book::getCreateTime);

        bookMapper.selectPage(pageParam, wrapper);
        return Result.success(pageParam);
    }

    @Override
    public Result<Book> getBookById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null || book.getStatus() == 0) {
            return Result.error(404, "Book not found");
        }
        return Result.success(book);
    }

    @Override
    @Transactional
    public Result<String> addBook(BookDTO bookDTO) {
        if (bookDTO.getTitle() == null || bookDTO.getTitle().trim().isEmpty()) {
            return Result.error(400, "Book title must not be empty");
        }

        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        book.setAvailableCopies(bookDTO.getTotalCopies() != null ? bookDTO.getTotalCopies() : 1);
        book.setStatus(1);

        bookMapper.insert(book);
        return Result.success("Book added successfully");
    }

    @Override
    @Transactional
    public Result<String> updateBook(Long id, BookDTO bookDTO) {
        Book book = bookMapper.selectById(id);
        if (book == null || book.getStatus() == 0) {
            return Result.error(404, "Book not found");
        }

        if (bookDTO.getTitle() != null) book.setTitle(bookDTO.getTitle());
        if (bookDTO.getAuthor() != null) book.setAuthor(bookDTO.getAuthor());
        if (bookDTO.getIsbn() != null) book.setIsbn(bookDTO.getIsbn());
        if (bookDTO.getPublisher() != null) book.setPublisher(bookDTO.getPublisher());
        if (bookDTO.getCategory() != null) book.setCategory(bookDTO.getCategory());
        if (bookDTO.getDescription() != null) book.setDescription(bookDTO.getDescription());
        if (bookDTO.getCoverUrl() != null) book.setCoverUrl(bookDTO.getCoverUrl());
        if (bookDTO.getTotalCopies() != null) {
            int diff = bookDTO.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(bookDTO.getTotalCopies());
            book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));
        }

        bookMapper.updateById(book);
        return Result.success("Book updated successfully");
    }

    @Override
    @Transactional
    public Result<String> deleteBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null || book.getStatus() == 0) {
            return Result.error(404, "Book not found");
        }

        book.setStatus(0);
        bookMapper.updateById(book);
        return Result.success("Book removed successfully");
    }

    @Override
    public boolean updateStock(Long id, int delta) {
        int rows = bookMapper.updateStock(id, delta);
        return rows > 0;
    }
}

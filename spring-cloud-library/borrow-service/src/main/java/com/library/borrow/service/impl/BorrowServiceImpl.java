package com.library.borrow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.borrow.feign.BookFeignClient;
import com.library.borrow.feign.UserFeignClient;
import com.library.borrow.mapper.BorrowMapper;
import com.library.borrow.message.OverdueMessageSource;
import com.library.borrow.service.BorrowService;
import com.library.common.constant.Constants;
import com.library.common.dto.OverdueMessage;
import com.library.common.dto.Result;
import com.library.common.entity.Book;
import com.library.common.entity.BorrowRecord;
import com.library.common.entity.User;
import com.library.common.vo.BorrowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private BookFeignClient bookFeignClient;

    @Autowired
    private OverdueMessageSource overdueMessageSource;

    @Override
    @Transactional
    public Result<String> borrowBook(Long userId, Long bookId, Integer dueDays) {
        // Validate user exists
        Result<User> userResult = userFeignClient.getUserById(userId);
        if (userResult.getCode() != 200 || userResult.getData() == null) {
            return Result.error(400, "User not found");
        }
        User user = userResult.getData();

        // Validate book exists and is available
        Result<Book> bookResult = bookFeignClient.getBookById(bookId);
        if (bookResult.getCode() != 200 || bookResult.getData() == null) {
            return Result.error(400, "Book not found");
        }
        Book book = bookResult.getData();

        if (book.getAvailableCopies() <= 0) {
            return Result.error(400, "Book is currently unavailable");
        }

        // Check if user already borrowed this book and hasn't returned
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getBookId, bookId)
                .eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWED);
        if (borrowMapper.selectCount(wrapper) > 0) {
            return Result.error(400, "You have already borrowed this book");
        }

        // Decrease available stock
        Result<String> stockResult = bookFeignClient.updateStock(bookId, -1);
        if (stockResult.getCode() != 200) {
            return Result.error(400, "Failed to update book stock");
        }

        // Create borrow record
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowTime(LocalDateTime.now());
        record.setDueTime(LocalDateTime.now().plusDays(dueDays != null ? dueDays : 30));
        record.setStatus(Constants.BORROW_STATUS_BORROWED);
        borrowMapper.insert(record);

        log.info("User {} borrowed book {} (id: {}), due: {}",
                userId, book.getTitle(), bookId, record.getDueTime());

        return Result.success("Book borrowed successfully. Due date: " +
                record.getDueTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    @Override
    @Transactional
    public Result<String> returnBook(Long recordId) {
        BorrowRecord record = borrowMapper.selectById(recordId);
        if (record == null) {
            return Result.error(404, "Borrow record not found");
        }

        if (!Constants.BORROW_STATUS_BORROWED.equals(record.getStatus()) &&
            !Constants.BORROW_STATUS_OVERDUE.equals(record.getStatus())) {
            return Result.error(400, "Book has already been returned");
        }

        // Increase available stock
        bookFeignClient.updateStock(record.getBookId(), 1);

        // Update record
        record.setReturnTime(LocalDateTime.now());
        record.setStatus(Constants.BORROW_STATUS_RETURNED);
        borrowMapper.updateById(record);

        log.info("Borrow record {} returned, book id: {}", recordId, record.getBookId());
        return Result.success("Book returned successfully");
    }

    @Override
    public Result<List<BorrowVO>> getMyBorrows() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "Not logged in");
        }

        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
                .orderByDesc(BorrowRecord::getCreateTime);

        List<BorrowRecord> records = borrowMapper.selectList(wrapper);
        List<BorrowVO> voList = records.stream().map(record -> {
            BorrowVO vo = new BorrowVO();
            vo.setId(record.getId());
            vo.setUserId(record.getUserId());
            vo.setBookId(record.getBookId());
            vo.setBorrowTime(record.getBorrowTime());
            vo.setDueTime(record.getDueTime());
            vo.setReturnTime(record.getReturnTime());
            vo.setStatus(record.getStatus());

            // Get book title via Feign
            try {
                Result<Book> bookResult = bookFeignClient.getBookById(record.getBookId());
                if (bookResult.getCode() == 200 && bookResult.getData() != null) {
                    vo.setBookTitle(bookResult.getData().getTitle());
                }
            } catch (Exception e) {
                log.warn("Failed to get book info for id: {}", record.getBookId());
                vo.setBookTitle("Unknown Book");
            }

            return vo;
        }).collect(Collectors.toList());

        return Result.success(voList);
    }

    @Override
    public Result<List<BorrowVO>> getAllBorrows() {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BorrowRecord::getCreateTime);
        List<BorrowRecord> records = borrowMapper.selectList(wrapper);

        List<BorrowVO> voList = records.stream().map(record -> {
            BorrowVO vo = new BorrowVO();
            vo.setId(record.getId());
            vo.setUserId(record.getUserId());
            vo.setBookId(record.getBookId());
            vo.setBorrowTime(record.getBorrowTime());
            vo.setDueTime(record.getDueTime());
            vo.setReturnTime(record.getReturnTime());
            vo.setStatus(record.getStatus());

            try {
                Result<Book> bookResult = bookFeignClient.getBookById(record.getBookId());
                if (bookResult.getCode() == 200 && bookResult.getData() != null) {
                    vo.setBookTitle(bookResult.getData().getTitle());
                }
            } catch (Exception e) {
                vo.setBookTitle("Unknown Book");
            }

            try {
                Result<User> userResult = userFeignClient.getUserById(record.getUserId());
                if (userResult.getCode() == 200 && userResult.getData() != null) {
                    vo.setUserName(userResult.getData().getRealName());
                }
            } catch (Exception e) {
                vo.setUserName("Unknown User");
            }

            return vo;
        }).collect(Collectors.toList());

        return Result.success(voList);
    }

    /**
     * Scheduled task: Check overdue records every hour
     * For demo/testing, runs every 30 seconds
     */
    @Override
    @Scheduled(fixedRate = 60000)
    public void checkAndHandleOverdueRecords() {
        log.debug("Running overdue check...");
        List<BorrowRecord> overdueRecords = borrowMapper.findOverdueRecords();

        if (overdueRecords.isEmpty()) {
            return;
        }

        for (BorrowRecord record : overdueRecords) {
            try {
                // Update status to OVERDUE
                record.setStatus(Constants.BORROW_STATUS_OVERDUE);
                borrowMapper.updateById(record);

                // Send overdue message via Spring Cloud Stream (RabbitMQ)
                OverdueMessage message = new OverdueMessage();
                message.setUserId(record.getUserId());
                message.setBookId(record.getBookId());
                message.setDueTime(record.getDueTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

                try {
                    Result<Book> bookResult = bookFeignClient.getBookById(record.getBookId());
                    if (bookResult.getCode() == 200 && bookResult.getData() != null) {
                        message.setBookTitle(bookResult.getData().getTitle());
                    }
                } catch (Exception e) {
                    message.setBookTitle("Unknown Book");
                }

                try {
                    Result<User> userResult = userFeignClient.getUserById(record.getUserId());
                    if (userResult.getCode() == 200 && userResult.getData() != null) {
                        message.setUsername(userResult.getData().getUsername());
                    }
                } catch (Exception e) {
                    message.setUsername("unknown");
                }

                overdueMessageSource.send(message);
                log.info("Sent overdue message for borrow record: {}, user: {}, book: {}",
                        record.getId(), record.getUserId(), record.getBookId());

            } catch (Exception e) {
                log.error("Failed to process overdue record: {}", record.getId(), e);
            }
        }
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

package com.library.borrow.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.library.borrow.service.BorrowService;
import com.library.common.dto.Result;
import com.library.common.vo.BorrowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    /**
     * Borrow a book (requires auth)
     */
    @PostMapping
    @SentinelResource(value = "borrow", blockHandler = "borrowBlockHandler")
    public Result<String> borrowBook(@RequestParam Long userId,
                                     @RequestParam Long bookId,
                                     @RequestParam(defaultValue = "30") Integer dueDays) {
        return borrowService.borrowBook(userId, bookId, dueDays);
    }

    /**
     * Return a book
     */
    @PostMapping("/return/{recordId}")
    public Result<String> returnBook(@PathVariable Long recordId) {
        return borrowService.returnBook(recordId);
    }

    /**
     * Get current user's borrow records
     */
    @GetMapping("/my")
    public Result<List<BorrowVO>> getMyBorrows() {
        return borrowService.getMyBorrows();
    }

    /**
     * Get all borrow records (admin only via gateway)
     */
    @GetMapping("/list")
    public Result<List<BorrowVO>> getAllBorrows() {
        return borrowService.getAllBorrows();
    }

    /**
     * Trigger overdue check manually (for testing)
     */
    @PostMapping("/overdue-check")
    public Result<String> triggerOverdueCheck() {
        borrowService.checkAndHandleOverdueRecords();
        return Result.success("Overdue check completed");
    }

    public Result<String> borrowBlockHandler(Long userId, Long bookId, Integer dueDays, BlockException ex) {
        return Result.error(429, "Too many borrow requests. Please try again later.");
    }
}

package com.library.borrow.service;

import com.library.common.dto.Result;
import com.library.common.vo.BorrowVO;

import java.util.List;

public interface BorrowService {
    Result<String> borrowBook(Long userId, Long bookId, Integer dueDays);
    Result<String> returnBook(Long recordId);
    Result<List<BorrowVO>> getMyBorrows();
    Result<List<BorrowVO>> getAllBorrows();
    void checkAndHandleOverdueRecords();
}

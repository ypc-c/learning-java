package com.library.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BorrowVO {
    private Long id;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private String userName;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private String status;
}

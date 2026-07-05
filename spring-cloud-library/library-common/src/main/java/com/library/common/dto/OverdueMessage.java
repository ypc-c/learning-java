package com.library.common.dto;

import lombok.Data;

@Data
public class OverdueMessage {
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private String dueTime;
    private String username;
}

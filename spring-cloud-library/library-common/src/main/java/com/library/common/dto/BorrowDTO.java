package com.library.common.dto;

import lombok.Data;

@Data
public class BorrowDTO {
    private Long userId;
    private Long bookId;
    private Integer dueDays;
}

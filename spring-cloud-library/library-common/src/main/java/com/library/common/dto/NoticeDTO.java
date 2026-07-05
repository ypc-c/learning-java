package com.library.common.dto;

import lombok.Data;

@Data
public class NoticeDTO {
    private String title;
    private String content;
    private String type;
    private Long targetUserId;
}

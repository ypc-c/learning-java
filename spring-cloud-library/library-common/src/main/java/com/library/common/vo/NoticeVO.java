package com.library.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeVO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Long targetUserId;
    private Integer isRead;
    private LocalDateTime createTime;
}

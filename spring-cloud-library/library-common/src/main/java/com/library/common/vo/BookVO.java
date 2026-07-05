package com.library.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookVO {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String category;
    private String description;
    private String coverUrl;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer status;
    private LocalDateTime createTime;
}

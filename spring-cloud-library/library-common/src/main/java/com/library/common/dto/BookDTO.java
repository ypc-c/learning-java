package com.library.common.dto;

import lombok.Data;

@Data
public class BookDTO {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String category;
    private String description;
    private String coverUrl;
    private Integer totalCopies;
}

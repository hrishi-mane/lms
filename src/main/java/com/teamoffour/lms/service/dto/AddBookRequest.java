package com.teamoffour.lms.service.dto;

import lombok.Data;

@Data
public class AddBookRequest {
    private String isbn;
    private String title;
    private String author;
    private String category;
    private Integer publicationYear;
    private Integer copiesAvailable;
}
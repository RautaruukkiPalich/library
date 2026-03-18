package com.app.modules.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BookFilter {
    private String title;
    private String genre;
    private Boolean isAvailable;
    private Integer pubYearFrom;
    private Integer pubYearTo;

    public BookFilter() {
    }
}

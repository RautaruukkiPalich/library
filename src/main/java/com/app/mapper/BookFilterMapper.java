package com.app.mapper;

import com.app.dto.BookQueryParamsDTO;
import com.app.filter.BookFilter;

public class BookFilterMapper {
    public static BookFilter toFilter(BookQueryParamsDTO.TitleGenre dto){
        return new BookFilter(dto.getTitle(), dto.getGenre(), null, null, null);
    }

    public static BookFilter available(){
        return new BookFilter(null, null, true, null, null);
    }

    public static BookFilter year(Integer year){
        return new BookFilter(null, null, null, year, year);
    }

    public static BookFilter betweenYears(Integer yearFrom, Integer yearTo){
        return new BookFilter(null, null, null, yearFrom, yearTo);
    }
}

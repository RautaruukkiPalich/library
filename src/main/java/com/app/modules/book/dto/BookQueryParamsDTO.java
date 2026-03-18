package com.app.modules.book.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class BookQueryParamsDTO {

    @Setter
    @Getter
    public static class TitleGenre {
        private String title;
        private String genre;

        public TitleGenre() {
        }

        public TitleGenre(String title, String genre) {
            this.genre = genre;
            this.title = title;
        }
    }

    @Setter
    @Getter
    public static class PubYears {

        private Integer from;
        private Integer to;

        public PubYears() {
        }

        public PubYears(Integer from, Integer to) {
            this.from = from;
            this.to = to;
        }
    }
}

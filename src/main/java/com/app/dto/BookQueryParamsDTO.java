package com.app.dto;

public class BookQueryParamsDTO {

    public static class TitleGenre{
        private String title;
        private String genre;

        public TitleGenre(){}

        public TitleGenre(String title, String genre){
            this.genre = genre;
            this.title = title;
        }

        public String getTitle() { return title; }
        public String getGenre() { return genre; }

        public void setTitle(String title) { this.title = title; }
        public void setGenre(String genre) { this.genre = genre; }
    }

    public static class PubYears {

        private Integer from;
        private Integer to;

        public PubYears(){}

        public PubYears(Integer from, Integer to){
            this.from = from;
            this.to = to;
        }

        public Integer getFrom() {return this.from; }
        public Integer getTo() {return this.to; } 

        public void setFrom(Integer year) { this.from = year; }
        public void setTo(Integer year) { this.to = year; }
    }


}

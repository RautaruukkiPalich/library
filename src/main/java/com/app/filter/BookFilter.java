package com.app.filter;

public class BookFilter {
    private String title;
    private String genre;
    private Boolean isAvailable;
    private Integer pubYearFrom;
    private Integer pubYearTo;

    public BookFilter(){}

    public BookFilter(String title, String genre, Boolean isAvailable, Integer pubYearFrom, Integer pubYearTo){
        this.genre = genre;
        this.title = title;
        this.isAvailable = isAvailable;
        this.pubYearFrom = pubYearFrom;
        this.pubYearTo = pubYearTo;
    }

    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public Boolean isAvailable() {return isAvailable; }
    public Integer getPubYearFrom() {return pubYearFrom; }
    public Integer getPubYearTo() {return pubYearTo; }

    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setAvailable(Boolean isAvailable) {this.isAvailable = isAvailable; }
    public void setPubYearFrom(Integer year) {this.pubYearFrom = year; }
    public void setPubYearTo(Integer year) {this.pubYearTo = year; }
}

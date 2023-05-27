package com.cookandroid.traveldiary;

public class DiaryEntry {
    private String title;
    private String date;
    private String content;

    public DiaryEntry(String title, String date, String content) {
        this.title = title;
        this.date = date;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}

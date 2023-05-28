package com.cookandroid.traveldiary;

public class ScheduleEntry {
    private String title;
    private String startDate;
    private String endDate;
    private String content;

    public ScheduleEntry(String title, String startDate, String endDate, String content) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package com.cookandroid.traveldiary;

public class ExpenditureEntry {
    private String title;
    private String spendDate;
    private String price;

    public ExpenditureEntry(String title, String spendDate, String price) {
        this.title = title;
        this.spendDate = spendDate;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getSpendDate() {
        return spendDate;
    }

    public String getPrice() {
        return price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSpendDate(String spendDate) {
        this.spendDate = spendDate;
    }
}

package com.suraj.dailyexpenses.data;


import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by suraj on 1/5/17.
 */
public class BasicItem extends RealmObject implements Serializable {
    private int day;
    private int month;
    private int year;
    private int amount;

    private long timestamp;

    private String date;
    private String dayName;
    private String reason;

    private String tag;


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BasicItem() {
    }

    public BasicItem(BasicItem basicItem) {
        this.setDate(basicItem.getDate());
        this.setReason(basicItem.getReason());
        this.setAmount(basicItem.getAmount());
        this.setTimestamp(basicItem.getTimestamp());
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;

        String[] splts = date.split("/");
        String[] splts1 = splts[0].split(" ");

        dayName = splts1[0];

        day = Integer.parseInt(splts1[1]);

        month = Integer.parseInt(splts[1]);
        year = Integer.parseInt(splts[2]);

    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}

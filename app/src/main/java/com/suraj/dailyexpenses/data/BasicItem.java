package com.suraj.dailyexpenses.data;


import io.realm.RealmObject;

/**
 * Created by suraj on 1/5/17.
 */
public class BasicItem extends RealmObject{
    private String date;
    private String dayName;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    private String reason;
    private int day;
    private int month;
    private int year;

    public boolean isInFrequent() {
        return inFrequent;
    }

    public void setInFrequent(boolean inFrequent) {
        this.inFrequent = inFrequent;
    }

    private boolean inFrequent;

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

    int amount;
    long timestamp;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;

        String[] splts = date.split("/");
        String[] splts1 = splts[0].split(" ");

        dayName = splts1[0];

        day = Integer.parseInt(splts1[1]);

        month=Integer.parseInt(splts[1]);
        year=Integer.parseInt(splts[2]);

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


}

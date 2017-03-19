package com.suraj.dailyexpenses.data;

import io.realm.RealmObject;

/**
 * Created by suraj on 18/3/17.
 */
public class Item extends RealmObject {
    private String date;
    private String reason;

    private int amount;

    private long timestamp;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

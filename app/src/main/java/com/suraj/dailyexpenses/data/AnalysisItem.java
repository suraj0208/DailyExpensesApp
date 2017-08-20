package com.suraj.dailyexpenses.data;

/**
 * Created by suraj on 14/8/17.
 */
public class AnalysisItem {
    private String name;
    private int averagePrice;
    private long averageTimestamp;
    private int count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void calculateRunningAverageTimestamp(long newTimestamp){
        averageTimestamp = (averageTimestamp/count) * (count-1/count) + (newTimestamp/count);
    }

    private void calculateRunningAveragePrice(int newPrice){
        averagePrice = (int)((double)(averagePrice/count) *(double) (count-1/count) +(double) (newPrice/count));
    }

    public void analyze(long newTimestamp,int newPrice){
        count++;
        calculateRunningAverageTimestamp(newTimestamp);
        calculateRunningAveragePrice(newPrice);
    }

    public int getAveragePrice() {
        return averagePrice;
    }

    public long getAverageTimestamp() {
        return averageTimestamp;
    }
}

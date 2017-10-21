package com.suraj.dailyexpenses.data;

import java.util.ArrayList;
import java.util.List;

public class TagItemsHolder {
    private String tagName;
    private List<BasicItem> basicItems;

    public int getSum() {
        return sum;
    }

    public String getTagName() {
        return tagName;
    }

    private int sum = 0;

    public TagItemsHolder(String tagName) {
        this.tagName = tagName;
        this.basicItems = new ArrayList<>();
    }

    public void addToList(BasicItem basicItem) {
        if (basicItem == null)
            return;

        basicItems.add(basicItem);
    }

    public void addToSum(int val) {
        sum += val;
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.equals(tagName);
    }
}
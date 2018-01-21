package com.suraj.dailyexpenses.widgets;

import com.suraj.dailyexpenses.data.BasicItem;

import java.util.Comparator;

public class SortingComparator implements Comparator<BasicItem> {
    public int getType() {
        return type;
    }

    private int type = 0;

    /**
     * 0 date a-z
     * 1 date z-a
     * 2 amount a-z
     * 3 amount z-a
     */

    @Override
    public int compare(BasicItem b1, BasicItem b2) {
        switch (type) {
            case 0:
                if (b1.getYear() != b2.getYear()) {
                    return b1.getYear() - b2.getYear();
                } else {
                    if (b1.getMonth() != b2.getMonth()) {
                        return b1.getMonth() - b2.getMonth();
                    } else {
                        return b1.getDay() - b2.getDay();
                    }
                }

            case 1:
                if (b1.getYear() != b2.getYear()) {
                    return b2.getYear() - b1.getYear();
                } else {
                    if (b1.getMonth() != b2.getMonth()) {
                        return b2.getMonth() - b1.getMonth();
                    } else {
                        return b2.getDay() - b1.getDay();
                    }
                }

            case 2:
                return b1.getAmount() - b2.getAmount();

            case 3:
                return b2.getAmount() - b1.getAmount();

        }

        return 0;
    }

    public void setType(int type) {
        this.type = type;
    }
}
package com.suraj.dailyexpenses.widgets;

import com.suraj.dailyexpenses.data.TagItemsHolder;

import java.util.Comparator;

public class TagsComparator implements Comparator<TagItemsHolder> {
    public int getType() {
        return type;
    }

    private int type = 0;

    /**
     * 0 amount a-z
     * 1 amount z-a
     */

    @Override
    public int compare(TagItemsHolder b1, TagItemsHolder b2) {
        switch (type) {
            case 0:
                return b1.getSum() - b2.getSum();

            case 1:
                return b2.getSum() - b1.getSum();
        }

        return 0;
    }

    public void setType(int type) {
        this.type = type;
    }
}
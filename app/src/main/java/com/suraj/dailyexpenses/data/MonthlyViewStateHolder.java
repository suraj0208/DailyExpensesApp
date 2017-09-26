package com.suraj.dailyexpenses.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

public class MonthlyViewStateHolder implements Serializable{
    private HashSet<String> currentTags;

    private boolean invert = false;

    public MonthlyViewStateHolder(){
        currentTags = new HashSet<>();
    }

    public void removeElement(String element){
        currentTags.remove(element);
    }

    public void addElement(String element){
        currentTags.add(element);
    }

    public void addAllElements(Collection<String> elements){
        currentTags.addAll(elements);
    }

    public boolean isElementIncluded(String element){
        return currentTags.contains(element);
    }

    public boolean isInvertMode() {
        return invert;
    }

    public void setInvertMode(boolean invert) {
        this.invert = invert;
    }

}
package com.suraj.dailyexpenses.data;

import com.suraj.dailyexpenses.Utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MonthlyViewStateHolder implements Serializable{
    public HashSet<String> getCurrentTags() {
        return currentTags;
    }

    private HashSet<String> currentTags;
    //exclude mode  - false, include mode - true
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

    public boolean isElementAllowed(String element){
        return currentTags.contains(element) == invert;
    }

    public boolean isElementIncludedInList(String element){
        return currentTags.contains(element);
    }

    public boolean isInvertMode() {
        return invert;
    }

    /*public void invertList(){
        List<String> allTags = Utils.getAllTags();

        for(String tag: allTags){
            if(currentTags.contains(tag)){
                currentTags.remove(tag);
            }else{
                currentTags.add(tag);
            }
        }
    }*/

    public void setInvertMode(boolean invert) {
        this.invert = invert;
    }
}
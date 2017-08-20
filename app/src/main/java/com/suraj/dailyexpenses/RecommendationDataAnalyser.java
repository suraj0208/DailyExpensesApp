package com.suraj.dailyexpenses;

import android.content.Context;

import com.suraj.dailyexpenses.data.AnalysisItem;
import com.suraj.dailyexpenses.data.BasicItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by suraj on 14/8/17.
 */
public class RecommendationDataAnalyser {
    private Context context;
    private HashMap<String, AnalysisItem> analysisItemHashMap;

    public RecommendationDataAnalyser(Context context){
        this.context = context;
        analysisItemHashMap = new HashMap<>();
        Utils.initRealm(context);
    }

    public void analyze(){
        ArrayList<BasicItem> basicItems = Utils.getAllItemsFromDatabase();

        for(BasicItem basicItem:basicItems){
            AnalysisItem analysisItem = analysisItemHashMap.get(basicItem.getReason());

            if(analysisItem==null){
                analysisItem = new AnalysisItem();
            }
            analysisItem.analyze(basicItem.getTimestamp(),basicItem.getAmount());
            analysisItemHashMap.put(basicItem.getReason(),analysisItem);
        }

        for (String reason:analysisItemHashMap.keySet()) {
            System.out.println(reason + " " +  analysisItemHashMap.get(reason).getAveragePrice());
            System.out.println(reason + " "+  analysisItemHashMap.get(reason).getAverageTimestamp());
        }
    }

}

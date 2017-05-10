package com.suraj.dailyexpenses;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by suraj on 18/3/17.
 */
interface InflationManager {
    void onGetView(int position, View convertView, ViewGroup parent);
}
package com.suraj.dailyexpenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.suraj.dailyexpenses.data.Day;

import java.util.List;

/**
 * Created by suraj on 29/3/17.
 */
public class DaysAdapter  extends ArrayAdapter{
    private List<Day> daysList;
    private Context context;
    private InflationManager inflationManager;


    public DaysAdapter(Context context, List<Day> daysList, InflationManager inflationManager) {
        super(context, R.layout.item_row);

        this.daysList = daysList;
        this.context = context;
        this.inflationManager = inflationManager;
    }

    @Override
    public int getCount() {
        return daysList.size();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_row, parent, false);

        inflationManager.onGetView(position,rowView,parent);

        return rowView;

    }
}

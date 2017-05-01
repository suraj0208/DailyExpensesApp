package com.suraj.dailyexpenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.suraj.dailyexpenses.data.BasicItem;

import java.util.List;

/**
 * Created by suraj on 29/3/17.
 */
public class BasicItemsAdapter extends ArrayAdapter{
    private List<BasicItem> basicItemsList;
    private Context context;
    private InflationManager inflationManager;


    public BasicItemsAdapter(Context context, List<BasicItem> basicItemsList, InflationManager inflationManager) {
        super(context, R.layout.item_row);

        this.basicItemsList = basicItemsList;
        this.context = context;
        this.inflationManager = inflationManager;
    }

    @Override
    public int getCount() {
        return basicItemsList.size();

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

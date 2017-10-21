package com.suraj.dailyexpenses;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.TagItemsHolder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BrowseTagsAdapter extends ArrayAdapter{
    private Context context;
    private List<TagItemsHolder> tagItemsHolderList;
    private InflationManager inflationManager;

    public BrowseTagsAdapter(Context context, List<TagItemsHolder> tagItemsHolders, InflationManager inflationManager) {
        super(context, 0);
        this.context= context;
        this.tagItemsHolderList = tagItemsHolders;
        this.inflationManager = inflationManager;
    }

    @Override
    public int getCount() {
        return this.tagItemsHolderList.size();
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
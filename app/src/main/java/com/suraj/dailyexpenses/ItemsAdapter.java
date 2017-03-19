package com.suraj.dailyexpenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.Item;

import java.util.List;

/**
 * Created by suraj on 18/3/17.
 */
public class ItemsAdapter extends ArrayAdapter<Item> {
    private List<Item> itemList;
    private Context context;

    public ItemsAdapter(Context context, List<Item> items) {
        super(context, R.layout.item_row);

        this.itemList = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_row, parent, false);

        Item item    = itemList.get(position);

        ((TextView)rowView.findViewById(R.id.tvItemName)).setText(item.getReason());
        ((TextView)rowView.findViewById(R.id.tvItemAmount)).setText(context.getResources().getString(R.string.rs,item.getAmount()));

        return rowView;
    }
}
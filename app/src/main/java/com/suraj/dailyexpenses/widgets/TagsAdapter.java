package com.suraj.dailyexpenses.widgets;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.suraj.dailyexpenses.R;
import com.suraj.dailyexpenses.data.MonthlyViewStateHolder;

import java.util.List;

public class TagsAdapter extends ArrayAdapter {
    MonthlyViewStateHolder monthlyViewStateHolder;
    List<String> tags;
    Context context;

    public TagsAdapter(Context context, List<String> tags, MonthlyViewStateHolder monthlyViewStateHolder) {
        super(context, R.layout.tag_row);
        this.monthlyViewStateHolder = monthlyViewStateHolder;
        this.tags = tags;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tags == null ? 0 : tags.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.tag_row, parent, false);

        final TextView tv = (TextView) rowView.findViewById(R.id.tvTagName);
        tv.setText(tags.get(position));

        if (monthlyViewStateHolder.isElementIncludedInList(tags.get(position))) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }


           /* final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkboxTag);

            if (monthlyViewStateHolder.isElementAllowed(tags.get(position))) {
                checkBox.setChecked(true);
            }


            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()) {
                        monthlyViewStateHolder.addElement(tags.get(position));
                    } else {
                        monthlyViewStateHolder.removeElement(tags.get(position));
                    }
                }
            });*/

        return rowView;
    }
}
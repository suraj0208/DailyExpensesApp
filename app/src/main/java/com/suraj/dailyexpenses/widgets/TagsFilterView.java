package com.suraj.dailyexpenses.widgets;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.suraj.dailyexpenses.R;
import com.suraj.dailyexpenses.data.MonthlyViewStateHolder;

import java.util.List;

public class TagsFilterView {
    private Context context;
    private List<String> tags;
    private MonthlyViewStateHolder monthlyViewStateHolder;
    private AlertDialog alertDialog = null;
    private final View dialogView;
    private final ListView listViewTags;

    public TagsFilterView(Context context, List<String> tags, MonthlyViewStateHolder monthlyViewStateHolder) {
        this.context = context;
        this.tags = tags;
        this.monthlyViewStateHolder = monthlyViewStateHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        dialogView = layoutInflater.inflate(R.layout.dialog_tags, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        listViewTags = (ListView) dialogView.findViewById(R.id.listViewTags);

        TagsAdapter itemsAdapter =
                new TagsAdapter(context, tags, monthlyViewStateHolder);
        //new ArrayAdapter<>(MonthExpensesActivity.this, android.R.layout.simple_list_item_1, tags);

        builder.setView(dialogView);

        listViewTags.setAdapter(itemsAdapter);

        alertDialog = builder.create();

    }

    public void dismiss(){
        alertDialog.dismiss();
    }

    public void show(){
        alertDialog.show();
    }

    public void setDismissListener(View.OnClickListener dismissListner){
        Button btnDone = (Button) dialogView.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(dismissListner);
    }

    public void setTagClickListener(AdapterView.OnItemClickListener tagClickListener){
        listViewTags.setOnItemClickListener(tagClickListener);
    }
}
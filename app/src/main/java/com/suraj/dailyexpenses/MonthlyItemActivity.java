package com.suraj.dailyexpenses;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.BasicItem;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by suraj on 1/5/17.
 */

public class MonthlyItemActivity extends AppCompatActivity {
    private int clickedMonth;

    private String itemName;

    private ArrayList<String> monthList;
    private ArrayList<BasicItem> basicItemList;
    private ArrayList<BasicItem> bakMonthItemList;

    private View frameLayoutItemDetails;
    private View frameLayoutMonthlyDetails;

    private TextView textView;

    private Button btnMonthlyDetailsMonthName;
    private Button btnItemDetails;

    private Spinner spinMonths;

    private ListView listView;

    private AdapterView.OnItemClickListener onItemClickListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_expenses);

        itemName = getIntent().getStringExtra(Utils.ITEM_INTENT_STRING);

        if (itemName == null)
            finish();

        spinMonths = (Spinner) findViewById(R.id.spinMonths);
        btnMonthlyDetailsMonthName = (Button) findViewById(R.id.btnMonthlyDetailsMonthName);
        btnItemDetails = (Button) findViewById(R.id.btnItemDetails);

        frameLayoutItemDetails = findViewById(R.id.frameLayoutItemDetails);
        frameLayoutMonthlyDetails = findViewById(R.id.frameLayoutMonthlyDetails);

        monthList = Utils.getMonthsFromDatabase();
        Collections.sort(monthList, Utils.monthComparator);
        spinMonths.setAdapter(new ArrayAdapter<>(MonthlyItemActivity.this, android.R.layout.simple_spinner_dropdown_item, monthList));

        listView = (ListView) findViewById(R.id.lstViewDaysOfMonth);
        textView = (TextView) findViewById(R.id.tvExpenditureForMonth);

        onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                clickedMonth = basicItemList.get(i).getMonth();

                for (int j = 0; j < monthList.size(); j++) {

                    String clickedMonthString = Utils.getMonthNameFromNumber(clickedMonth);

                    if (monthList.get(j).equals(clickedMonthString)) {
                        spinMonths.setSelection(j);
                        break;
                    }

                }
                updateUIForMonth();
            }
        };

        updateUIForAllMonths();

        spinMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                clickedMonth = Utils.getMonthNumberFromString(monthList.get(i));
                updateUIForMonth();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void updateUIForAllMonths() {
        spinMonths.setVisibility(View.GONE);

        basicItemList = Utils.getExpenditureForItem(itemName);
        Collections.sort(basicItemList, Utils.dateComparator);

        bakMonthItemList = basicItemList;

        listView.setAdapter(new BasicItemsAdapter(getApplicationContext(), bakMonthItemList, new AllMonthsInflationManager(bakMonthItemList)));

        listView.setOnItemClickListener(onItemClickListener);

        frameLayoutItemDetails.setVisibility(View.VISIBLE);
        frameLayoutMonthlyDetails.setVisibility(View.GONE);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW,R.id.frameLayoutItemDetails);
        layoutParams.setMargins(0,10,0,0);
        listView.setLayoutParams(layoutParams);

        btnItemDetails.setText(getString(R.string.expnditureForItem,itemName,Utils.getSum(bakMonthItemList)));
    }

    private void updateUIForMonth() {
        spinMonths.setVisibility(View.VISIBLE);

        basicItemList = Utils.getExpenditureForItemForMonth(clickedMonth);
        Collections.sort(basicItemList, Utils.dateComparator);

        listView.setAdapter(new BasicItemsAdapter(getApplicationContext(), basicItemList, new MonthInflationManager(basicItemList)));
        textView.setText("" + Utils.getSum(basicItemList));

        listView.setOnItemClickListener(null);

        frameLayoutItemDetails.setVisibility(View.INVISIBLE);
        frameLayoutMonthlyDetails.setVisibility(View.VISIBLE);

        btnMonthlyDetailsMonthName.setText(spinMonths.getSelectedItem().toString());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW,R.id.frameLayoutMonthlyDetails);
        layoutParams.setMargins(0,10,0,0);
        listView.setLayoutParams(layoutParams);

    }

    @Override
    public void onBackPressed() {
        if (listView.getOnItemClickListener() == null) {
            updateUIForAllMonths();
            return;
        }

        super.onBackPressed();

    }
}

class AllMonthsInflationManager implements InflationManager {
    private ArrayList<BasicItem> basicItemList;

    AllMonthsInflationManager(ArrayList<BasicItem> basicItemList) {
        this.basicItemList = basicItemList;
    }

    @Override
    public void onGetView(int position, View rowView, ViewGroup parent) {
        BasicItem basicItem = basicItemList.get(position);
        ((TextView) rowView.findViewById(R.id.tvItemName)).setText(Utils.getMonthNameFromNumber(basicItem.getMonth()));
        ((TextView) rowView.findViewById(R.id.tvItemAmount)).setText(Utils.getContext().getString(R.string.rs, basicItem.getAmount()));
    }
}

class MonthInflationManager implements InflationManager {
    private ArrayList<BasicItem> basicItemList;

    MonthInflationManager(ArrayList<BasicItem> basicItemList) {
        this.basicItemList = basicItemList;
    }

    @Override
    public void onGetView(int position, View rowView, ViewGroup parent) {
        BasicItem basicItem = basicItemList.get(position);
        ((TextView) rowView.findViewById(R.id.tvItemName)).setText(basicItem.getDate());
        ((TextView) rowView.findViewById(R.id.tvItemAmount)).setText(Utils.getContext().getString(R.string.rs, basicItem.getAmount()));
    }
}
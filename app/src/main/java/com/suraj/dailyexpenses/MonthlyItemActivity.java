package com.suraj.dailyexpenses;

import android.content.Intent;
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
    private TextView tvExpenseItemName;

    private Button btnMonthlyDetailsMonthName;
    private Button btnItemDetails;

    private Spinner spinMonths;
    private Spinner spinYear;

    private ListView listView;

    private AdapterView.OnItemClickListener onItemClickListenerMonthDetails;
    private AdapterView.OnItemClickListener onItemClickListenerOpenDay;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_expenses);

        itemName = getIntent().getStringExtra(Utils.ITEM_INTENT_STRING);

        if (itemName == null)
            finish();

        frameLayoutItemDetails = findViewById(R.id.frameLayoutItemDetails);
        frameLayoutMonthlyDetails = findViewById(R.id.frameLayoutMonthlyDetails);

        textView = (TextView) findViewById(R.id.tvExpenditureForMonth);
        tvExpenseItemName = (TextView) findViewById(R.id.tvExpenseItemName);

        btnMonthlyDetailsMonthName = (Button) findViewById(R.id.btnMonthlyDetailsMonthName);
        btnItemDetails = (Button) findViewById(R.id.btnItemDetails);

        spinMonths = (Spinner) findViewById(R.id.spinMonths);
        spinYear = (Spinner) findViewById(R.id.spinYear);

        listView = (ListView) findViewById(R.id.lstViewDaysOfMonth);

        monthList = Utils.getMonthsFromDatabase();
        Collections.sort(monthList, Utils.monthComparator);
        spinMonths.setAdapter(new ArrayAdapter<>(MonthlyItemActivity.this, android.R.layout.simple_spinner_dropdown_item, monthList));

        onItemClickListenerMonthDetails = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                clickedMonth = basicItemList.get(i).getMonth();
                int year = basicItemList.get(i).getYear();

                for (int j = 0; j < monthList.size(); j++) {

                    String clickedMonthString = Utils.getMonthNameFromNumber(clickedMonth);

                    if (monthList.get(j).equals(clickedMonthString)) {
                        spinMonths.setSelection(j);
                        break;
                    }

                }
                updateUIForMonth(clickedMonth, year);
            }
        };

        onItemClickListenerOpenDay = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MonthlyItemActivity.this, ViewExpensesActivity.class);
                intent.putExtra(Utils.DATE_INTENT_STRING, basicItemList.get(i).getDate());
                startActivity(intent);
            }
        };

        updateUIForAllMonths();

        spinMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                clickedMonth = Utils.getMonthNumberFromString(monthList.get(i));
                int year = Integer.parseInt(spinYear.getSelectedItem().toString());

                updateUIForMonth(clickedMonth, year);
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

        listView.setOnItemClickListener(onItemClickListenerMonthDetails);

        frameLayoutItemDetails.setVisibility(View.VISIBLE);
        frameLayoutMonthlyDetails.setVisibility(View.GONE);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.frameLayoutItemDetails);
        layoutParams.setMargins(0, 10, 0, 0);
        listView.setLayoutParams(layoutParams);

        tvExpenseItemName.setVisibility(View.INVISIBLE);

        btnItemDetails.setText(getString(R.string.expnditureForItem, itemName, Utils.getSum(bakMonthItemList)));
    }

    private void updateUIForMonth(int month, int year) {
        spinMonths.setVisibility(View.VISIBLE);

        basicItemList = Utils.getExpenditureForItemForMonth(month, year);
        Collections.sort(basicItemList, Utils.dateComparator);

        listView.setAdapter(new BasicItemsAdapter(getApplicationContext(), basicItemList, new MonthInflationManager(basicItemList)));
        textView.setText("" + Utils.getSum(basicItemList));

        listView.setOnItemClickListener(onItemClickListenerOpenDay);

        frameLayoutItemDetails.setVisibility(View.INVISIBLE);
        frameLayoutMonthlyDetails.setVisibility(View.VISIBLE);

        btnMonthlyDetailsMonthName.setText(spinMonths.getSelectedItem().toString());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.frameLayoutMonthlyDetails);
        layoutParams.setMargins(0, 10, 0, 0);
        listView.setLayoutParams(layoutParams);

        tvExpenseItemName.setVisibility(View.VISIBLE);
        tvExpenseItemName.setText(getString(R.string.expenseItem, itemName));

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
        ((TextView) rowView.findViewById(R.id.tvItemName)).setText(Utils.getMonthNameFromNumber(basicItem.getMonth()) + " " + basicItem.getYear());
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
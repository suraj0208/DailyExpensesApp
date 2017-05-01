package com.suraj.dailyexpenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.BasicItem;

import java.util.ArrayList;
import java.util.Collections;

public class MonthExpensesActivity extends AppCompatActivity implements InflationManager {
    private String monthNumber;
    private ArrayList<BasicItem> basicItems;

    private Spinner spinMonth;
    private TextView tvExpenditureForMonth;
    private ListView listViewExpensesDays;

    private static MonthExpensesActivity monthExpensesActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_expenses);

        spinMonth = (Spinner) findViewById(R.id.spinMonths);
        tvExpenditureForMonth = (TextView) findViewById(R.id.tvExpenditureForMonth);
        listViewExpensesDays = (ListView) findViewById(R.id.lstViewDaysOfMonth);

        ArrayList<String> monthList = Utils.getMonthsFromDatabase();
        spinMonth.setAdapter(new ArrayAdapter<>(MonthExpensesActivity.this, android.R.layout.simple_spinner_dropdown_item, monthList));
        tvExpenditureForMonth.setText(getResources().getString(R.string.expnditureMonth, Utils.getExpensesForMonth(Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString()))));

        if (getIntent().getStringExtra(Utils.MONTH_NUMBER_INTENT_STRING) == null) {
            spinMonth.setSelection(monthList.size() - 1);
            monthNumber = "" + Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString());
            basicItems = Utils.getDataForMonth(monthNumber);
        } else {
            monthNumber = getIntent().getStringExtra(Utils.MONTH_NUMBER_INTENT_STRING);
            basicItems = Utils.getDataForMonth(monthNumber);

            String monthName = Utils.getMonthNameFromNumber(Integer.parseInt(monthNumber));

            int i;
            for (i = 0; i < monthList.size(); i++) {
                if (monthList.get(i).equals(monthName)) {
                    spinMonth.setSelection(i);
                    break;
                }
            }

            if (i == monthList.size()) {
                monthList.add(monthName);
                spinMonth.setSelection(monthList.size() - 1);
            }
        }

        updateListView();

        listViewExpensesDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String date = basicItems.get(i).getDate();
                Intent intent = new Intent(MonthExpensesActivity.this, ViewExpensesActivity.class);
                intent.putExtra(Utils.DATE_INTENT_STRING, date);
                startActivity(intent);
            }
        });

        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(monthExpensesActivity!=null){
            monthExpensesActivity.finish();
        }
        monthExpensesActivity=this;


    }

    @Override
    public void onGetView(int position, View rowView, ViewGroup parent) {
        BasicItem basicItem = basicItems.get(position);
        ((TextView) rowView.findViewById(R.id.tvItemName)).setText(basicItem.getDate());
        ((TextView) rowView.findViewById(R.id.tvItemAmount)).setText(getResources().getString(R.string.rs, basicItem.getAmount()));
    }

    private void updateListView() {
        monthNumber = "" + Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString());
        basicItems = Utils.getDataForMonth(monthNumber);
        Collections.sort(basicItems, Utils.dateComparator);

        BasicItemsAdapter basicItemsAdapter = new BasicItemsAdapter(getApplicationContext(), basicItems, this);
        listViewExpensesDays.setAdapter(basicItemsAdapter);
        tvExpenditureForMonth.setText(getResources().getString(R.string.expnditureMonth, Utils.getExpensesForMonth(Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString()))));

    }
}

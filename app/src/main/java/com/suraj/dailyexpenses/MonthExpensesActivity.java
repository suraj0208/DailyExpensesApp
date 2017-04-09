package com.suraj.dailyexpenses;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.Day;

import java.util.ArrayList;
import java.util.Collections;

public class MonthExpensesActivity extends AppCompatActivity implements InflationManager {
    private String monthNumber;
    private ArrayList<Day> days;

    private Spinner spinMonth;
    private TextView tvExpenditureForMonth;
    private ListView listViewExpensesDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_expenses);

        spinMonth = (Spinner)findViewById(R.id.spinMonths);
        tvExpenditureForMonth = (TextView)findViewById(R.id.tvExpenditureForMonth);
        listViewExpensesDays = (ListView)findViewById(R.id.lstViewDaysOfMonth);

        ArrayList<String> monthList = Utils.getMonthList();
        spinMonth.setAdapter(new ArrayAdapter<>(MonthExpensesActivity.this, android.R.layout.simple_spinner_dropdown_item,monthList ));
        tvExpenditureForMonth.setText(getResources().getString(R.string.expnditureMonth,Utils.getExpensesForMonth(Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString()))));


        if(getIntent().getStringExtra("monthNumber")==null){
            spinMonth.setSelection(monthList.size()-1);
            monthNumber = Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString());
            days = Utils.getDatesForMonth(monthNumber);
        }else{
            monthNumber = getIntent().getStringExtra("monthNumber");
            days = Utils.getDatesForMonth(monthNumber);

            for(int i=0;i<days.size();i++){
                String date = days.get(i).getDate();
                String currentMonth = date.split("/")[1];

                if(currentMonth.equals(monthNumber)){
                    spinMonth.setSelection(i);
                    break;
                }
            }
        }


        updateListView();

        listViewExpensesDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String date = days.get(i).getDate();
                Intent intent    = new Intent(MonthExpensesActivity.this,ViewExpensesActivity.class);
                intent.putExtra("date",date);
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
    }

    @Override
    public void onGetView(int position, View rowView, ViewGroup parent) {
        Day day = days.get(position);
        ((TextView)rowView.findViewById(R.id.tvItemName)).setText(day.getDate());
        ((TextView)rowView.findViewById(R.id.tvItemAmount)).setText(getResources().getString(R.string.rs,day.getExpenses()));

    }

    private void updateListView(){
        monthNumber = Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString());
	days = Utils.getDatesForMonth(monthNumber);
        Collections.sort(days,Utils.dateComparator);

        DaysAdapter daysAdapter = new DaysAdapter(getApplicationContext(),days,this);
        listViewExpensesDays.setAdapter(daysAdapter);
        tvExpenditureForMonth.setText(getResources().getString(R.string.expnditureMonth,Utils.getExpensesForMonth(Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString()))));

    }
}

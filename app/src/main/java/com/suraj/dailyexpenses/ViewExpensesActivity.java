package com.suraj.dailyexpenses;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.Item;

import java.util.ArrayList;

public class ViewExpensesActivity extends AppCompatActivity {

    private Spinner spinDates;
    private TextView tvExpenditureForDate;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        spinDates = (Spinner) findViewById(R.id.spinDates);
        tvExpenditureForDate = (TextView) findViewById(R.id.tvExpenditureForDate);
        listView = (ListView) findViewById(R.id.lstViewItems);

        ArrayList<String> dates = Utils.getAvailableDates();

        spinDates.setAdapter(new ArrayAdapter<>(ViewExpensesActivity.this, android.R.layout.simple_spinner_dropdown_item, dates));


        spinDates.setSelection(dates.size() - 1);
        String date = spinDates.getSelectedItem().toString();
        ArrayList<Item> items = Utils.getItemsForDate(date);

        listView.setAdapter(new ItemsAdapter(getApplicationContext(), items));
        tvExpenditureForDate.setText(getResources().getString(R.string.expnditureDay, Utils.getExpenditure(date)));

        spinDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String date = spinDates.getSelectedItem().toString();
                ArrayList<Item> items = Utils.getItemsForDate(date);

                listView.setAdapter(new ItemsAdapter(getApplicationContext(), items));
                tvExpenditureForDate.setText(getResources().getString(R.string.expnditureDay, Utils.getExpenditure(date)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }
}

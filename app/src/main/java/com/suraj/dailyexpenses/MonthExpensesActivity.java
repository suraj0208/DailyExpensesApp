package com.suraj.dailyexpenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.BasicItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MonthExpensesActivity extends AppCompatActivity implements InflationManager {
    private boolean showInfrequent;

    private int monthNumber;

    private ArrayList<BasicItem> basicItems;

    private Spinner spinMonth;

    private TextView tvExpenditureForMonth;

    private ListView listViewExpensesDays;

    private Button btnMonthlyDetailsMonthName;

    private static MonthExpensesActivity monthExpensesActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_expenses);

        spinMonth = (Spinner) findViewById(R.id.spinMonths);

        tvExpenditureForMonth = (TextView) findViewById(R.id.tvExpenditureForMonth);

        listViewExpensesDays = (ListView) findViewById(R.id.lstViewDaysOfMonth);

        btnMonthlyDetailsMonthName = (Button) findViewById(R.id.btnMonthlyDetailsMonthName);

        showInfrequent = true;

        (findViewById(R.id.tvExpenseItemName)).setVisibility(View.GONE);

        ArrayList<String> monthList = Utils.getMonthsFromDatabase();
        Collections.sort(monthList, Utils.monthComparator);
        spinMonth.setAdapter(new ArrayAdapter<>(MonthExpensesActivity.this, android.R.layout.simple_spinner_dropdown_item, monthList));

        if (monthList.size() > 0 && getIntent().getIntExtra(Utils.MONTH_NUMBER_INTENT_STRING,-1) == -1) {
            spinMonth.setSelection(monthList.size() - 1);
            monthNumber = Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString());
            basicItems = Utils.getDataForMonth(monthNumber, showInfrequent);
        } else if (monthList.size() > 0) {
            monthNumber = getIntent().getIntExtra(Utils.MONTH_NUMBER_INTENT_STRING,-1);
            basicItems = Utils.getDataForMonth(monthNumber, showInfrequent);

            String monthName = Utils.getMonthNameFromNumber(monthNumber);

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
        } else if (monthList.size() == 0) {
            Calendar c = Calendar.getInstance();
            monthList.add(Utils.getMonthNameFromNumber(c.get(Calendar.MONTH) + 1));
            spinMonth.setAdapter(new ArrayAdapter<>(MonthExpensesActivity.this, android.R.layout.simple_spinner_dropdown_item, monthList));
        }
        tvExpenditureForMonth.setText("" + Utils.getExpensesForMonth(Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString()), showInfrequent));

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

        ensureSingleInstanceOnActivityStack();
    }

    private void ensureSingleInstanceOnActivityStack() {
        if (MonthExpensesActivity.monthExpensesActivity != null) {
            MonthExpensesActivity.monthExpensesActivity.finish();
        }
        MonthExpensesActivity.monthExpensesActivity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_monthly_expenses, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_infrequents:
                showInfrequent = !showInfrequent;
                if (showInfrequent) {
                    item.setTitle(getString(R.string.all));
                    item.setIcon(null);
                    Utils.showToast(getString(R.string.showingAllNotify));
                } else {
                    item.setIcon(R.drawable.ic_infrequents);
                    Utils.showToast(getString(R.string.excludingInfrequentNotify));
                }
                updateListView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetView(int position, View rowView, ViewGroup parent) {
        BasicItem basicItem = basicItems.get(position);
        ((TextView) rowView.findViewById(R.id.tvItemName)).setText(basicItem.getDate());
        ((TextView) rowView.findViewById(R.id.tvItemAmount)).setText(getResources().getString(R.string.rs, basicItem.getAmount()));
    }

    private void updateListView() {
        monthNumber = Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString());
        basicItems = Utils.getDataForMonth(monthNumber, showInfrequent);
        Collections.sort(basicItems, Utils.dateComparator);

        BasicItemsAdapter basicItemsAdapter = new BasicItemsAdapter(getApplicationContext(), basicItems, this);
        listViewExpensesDays.setAdapter(basicItemsAdapter);
        tvExpenditureForMonth.setText("" + Utils.getExpensesForMonth(Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString()), showInfrequent));
        btnMonthlyDetailsMonthName.setText(spinMonth.getSelectedItem().toString());
    }
}

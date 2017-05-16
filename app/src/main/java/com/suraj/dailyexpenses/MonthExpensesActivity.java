package com.suraj.dailyexpenses;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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
    private AlertDialog alertDialog;

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

        if (monthList.size() > 0 && getIntent().getIntExtra(Utils.MONTH_NUMBER_INTENT_STRING, -1) == -1) {
            spinMonth.setSelection(monthList.size() - 1);
            monthNumber = Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString());
            basicItems = Utils.getDataForMonth(monthNumber, showInfrequent);
        } else if (monthList.size() > 0) {
            monthNumber = getIntent().getIntExtra(Utils.MONTH_NUMBER_INTENT_STRING, -1);
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

        displayDetailsInViews();

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
                displayDetailsInViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setUpSortDialog();

        ensureSingleInstanceOnActivityStack();
    }

    private void setUpSortDialog() {
        final Utils.SortingComparator sortComparator = new Utils.SortingComparator();

        class SortClickListener implements View.OnClickListener {
            private AlertDialog alertDialog;

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tvSortDateAtoZ:
                        sortComparator.setType(0);
                        Collections.sort(basicItems, sortComparator);
                        updateListView();
                        alertDialog.dismiss();
                        break;

                    case R.id.tvSortDateZtoA:
                        sortComparator.setType(1);
                        Collections.sort(basicItems, sortComparator);
                        updateListView();
                        alertDialog.dismiss();
                        break;

                    case R.id.tvSortAmountAtoZ:
                        sortComparator.setType(2);
                        Collections.sort(basicItems, sortComparator);
                        updateListView();
                        alertDialog.dismiss();
                        break;

                    case R.id.tvSortAmountZtoA:
                        sortComparator.setType(3);
                        Collections.sort(basicItems, sortComparator);
                        updateListView();
                        alertDialog.dismiss();
                        tvExpenditureForMonth.setText("↓");
                        break;

                }
            }

            public void setAlertDialog(AlertDialog alertDialog) {
                this.alertDialog = alertDialog;
            }
        }

        final SortClickListener sortClickListener = new SortClickListener();

        LayoutInflater layoutInflater = LayoutInflater.from(MonthExpensesActivity.this);

        View dialogView = layoutInflater.inflate(R.layout.dialog_sort, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MonthExpensesActivity.this);

        final TextView[] textViews = new TextView[]{ (TextView) dialogView.findViewById(R.id.tvSortDateAtoZ), (TextView) dialogView.findViewById(R.id.tvSortDateZtoA),(TextView) dialogView.findViewById(R.id.tvSortAmountAtoZ), (TextView) dialogView.findViewById(R.id.tvSortAmountZtoA)};

        for(int i=0;i<textViews.length;i++){
            if(i==sortComparator.getType())
                textViews[i].setTextColor(ContextCompat.getColor(getApplication(), R.color.colorAccent));
            else
                textViews[i].setTextColor(ContextCompat.getColor(getApplication(), android.R.color.primary_text_light));

            textViews[i].setOnClickListener(sortClickListener);
        }

        builder.setView(dialogView);


        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                for(int i=0;i<textViews.length;i++){
                    if(i==sortComparator.getType())
                        textViews[i].setTextColor(ContextCompat.getColor(getApplication(), R.color.colorAccent));
                    else
                        textViews[i].setTextColor(ContextCompat.getColor(getApplication(), android.R.color.primary_text_light));
                }
            }
        });

        alertDialog = builder.create();
        sortClickListener.setAlertDialog(alertDialog);
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
                displayDetailsInViews();
                break;

            case R.id.action_sort:
                alertDialog.show();
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

    private void displayDetailsInViews() {
        getItemsFromDB();
        updateListView();
    }

    private void updateListView() {
        BasicItemsAdapter basicItemsAdapter = new BasicItemsAdapter(getApplicationContext(), basicItems, this);
        listViewExpensesDays.setAdapter(basicItemsAdapter);
        tvExpenditureForMonth.setText("" + Utils.getExpensesForMonth(Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString()), showInfrequent));
        btnMonthlyDetailsMonthName.setText(spinMonth.getSelectedItem().toString());
    }

    private void getItemsFromDB() {
        monthNumber = Utils.getMonthNumberFromString(spinMonth.getSelectedItem().toString());
        basicItems = Utils.getDataForMonth(monthNumber, showInfrequent);
        Collections.sort(basicItems, Utils.dateComparator);
    }

}

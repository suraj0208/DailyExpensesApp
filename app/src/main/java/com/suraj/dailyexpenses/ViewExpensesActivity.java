package com.suraj.dailyexpenses;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.Item;

import java.util.ArrayList;

public class ViewExpensesActivity extends AppCompatActivity implements InflationManager {
    ArrayList<Item> items;

    private Spinner spinDates;
    private TextView tvExpenditureForDate;
    private TextView tvExpenditureForMonth;
    private ListView listView;

    private static ViewExpensesActivity viewExpensesActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        spinDates = (Spinner) findViewById(R.id.spinDates);
        tvExpenditureForDate = (TextView) findViewById(R.id.tvExpenditureForDate);
        tvExpenditureForMonth = (TextView) findViewById(R.id.tvExpenditureForCurrentMonth);

        listView = (ListView) findViewById(R.id.lstViewItems);

        ArrayList<String> dates = Utils.getAllDatesInDatabase();

        spinDates.setAdapter(new ArrayAdapter<>(ViewExpensesActivity.this, android.R.layout.simple_spinner_dropdown_item, dates));

        String date;

        if (getIntent().getStringExtra(Utils.DATE_INTENT_STRING) == null) {
            spinDates.setSelection(dates.size() - 1);
            date = spinDates.getSelectedItem().toString();
        } else {
            date = getIntent().getStringExtra(Utils.DATE_INTENT_STRING);
            int i;

            for (i = 0; i < dates.size(); i++) {
                if (dates.get(i).equals(date)) {
                    spinDates.setSelection(i);
                    break;
                }
            }

            if (i == dates.size()) {
                dates.add(date);
                spinDates.setAdapter(new ArrayAdapter<>(ViewExpensesActivity.this, android.R.layout.simple_spinner_dropdown_item, dates));
                spinDates.setSelection(dates.size() - 1);
                date = spinDates.getSelectedItem().toString();
            }
        }

        items = Utils.getItemsForDate(date);

        updateListView();

        spinDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String date = spinDates.getSelectedItem().toString();
                items = Utils.getItemsForDate(date);

                updateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                new AlertDialog.Builder(ViewExpensesActivity.this)
                        .setTitle("Sure?")
                        .setMessage("Delete selected item?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.deleteFromDatabase(items.get(i));
                                items.remove(i);
                                updateListView();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ViewExpensesActivity.this, MonthlyItemActivity.class);
                intent.putExtra(Utils.ITEM_INTENT_STRING, items.get(i).getReason());
                startActivity(intent);
            }
        });

        findViewById(R.id.btnViewForMonth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewExpensesActivity.this, MonthExpensesActivity.class);

                intent.putExtra(Utils.MONTH_NUMBER_INTENT_STRING, spinDates.getSelectedItem().toString().split("/")[1]);
                startActivity(intent);
            }
        });

        if (viewExpensesActivity != null) {
            viewExpensesActivity.finish();
        }
        viewExpensesActivity = this;

    }

    private void updateListView() {
        String date = spinDates.getSelectedItem().toString();
        listView.setAdapter(new ItemsAdapter(getApplicationContext(), items, this));
        tvExpenditureForDate.setText(getResources().getString(R.string.expnditureDay, Utils.getExpenditureForDate(date)));
        tvExpenditureForMonth.setText(getResources().getString(R.string.expnditureMonth, Utils.getExpensesForMonth(Integer.parseInt(date.split("/")[1]))));
    }

    @Override
    public void onGetView(int position, View rowView, ViewGroup parent) {

        Item item = items.get(position);

        ((TextView) rowView.findViewById(R.id.tvItemName)).setText(item.getReason());
        ((TextView) rowView.findViewById(R.id.tvItemAmount)).setText(getResources().getString(R.string.rs, item.getAmount()));

    }
}

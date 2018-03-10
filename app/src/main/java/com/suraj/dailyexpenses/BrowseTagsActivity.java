package com.suraj.dailyexpenses;

import android.content.DialogInterface;
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

import com.suraj.dailyexpenses.data.TagItemsHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrowseTagsActivity extends AppCompatActivity implements InflationManager {
    private ListView lstViewBrowseTags;
    private List<TagItemsHolder> tagItemsHolderList;
    private Utils.TagsComparator tagsComparator;
    private AlertDialog alertDialog;
    private Spinner spinMonth;
    private Spinner spinYear;
    private int monthNumber;
    private int year;
    private TextView tvExpenditureForMonth;
    private Button btnMonthlyDetailsMonthName;
    private ArrayList<String> monthList;
    private ArrayList<String> yearList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_tags);

        lstViewBrowseTags = (ListView) findViewById(R.id.activity_browse_tags_list_view_tags_details);

        spinMonth = (Spinner) findViewById(R.id.activity_browse_tags_spin_months);
        spinYear = (Spinner) findViewById(R.id.activity_browse_tags_spin_year);

        tagsComparator = new Utils.TagsComparator();
        tagsComparator.setType(1);

        tvExpenditureForMonth = (TextView) findViewById(R.id.activity_browse_tags_tv_expenditure_month);

        btnMonthlyDetailsMonthName = (Button) findViewById(R.id.activity_browse_tags_btn_monthly_details_month_name);

        monthNumber = getIntent().getIntExtra(Utils.MONTH_NUMBER_INTENT_STRING, -1);
        year = getIntent().getIntExtra(Utils.YEAR_INTENT_STRING, -1);

        setUpSpinner();
        setUpMonthDetailsViews();


        updateListView();
        setUpSortDialog();
    }

    private void setUpMonthDetailsViews() {
        tvExpenditureForMonth.setText("" + Utils.getExpensesForMonth(monthNumber, year, null));
        btnMonthlyDetailsMonthName.setText(Utils.getMonthNameFromNumber(monthNumber));
    }

    private void setUpSpinner() {
        monthList = Utils.getMonthsFromDatabase();
        Collections.sort(monthList, Utils.monthComparator);
        monthList.add(0, "All");
        int i = 0;

        if (monthList.size() > 1) {
            i = monthList.indexOf(Utils.getMonthNameFromNumber(monthNumber));

            if (i == -1) {
                monthList.add(Utils.getMonthNameFromNumber(monthNumber));
            }

        }

        spinMonth.setAdapter(new ArrayAdapter<>(BrowseTagsActivity.this, android.R.layout.simple_spinner_dropdown_item, monthList));

        if (i != -1) {
            spinMonth.setSelection(i);
        }

        yearList = Utils.getYearsFromDatabase();
        Collections.sort(yearList);

        i = 0;
        if (yearList.size() == 0) {
            yearList.add("" + year);
        } else {
            i = yearList.indexOf("" + year);

            if (i == -1) {
                yearList.add("" + year);
            }
        }

        spinYear.setAdapter(new ArrayAdapter<>(BrowseTagsActivity.this, android.R.layout.simple_spinner_dropdown_item, yearList));

        if (i != -1) {
            spinYear.setSelection(i);
        }

        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                monthNumber = Utils.getMonthNumberFromString(monthList.get(spinMonth.getSelectedItemPosition()));
                tagItemsHolderList = Utils.getTagData(monthNumber, year);
                Collections.sort(tagItemsHolderList, tagsComparator);
                updateListView();
                setUpMonthDetailsViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                year = Integer.parseInt(yearList.get(i));
                tagItemsHolderList = Utils.getTagData(monthNumber, year);
                Collections.sort(tagItemsHolderList, tagsComparator);
                updateListView();
                setUpMonthDetailsViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateListView() {
        tagItemsHolderList = Utils.getTagData(monthNumber, year);

        if (tagItemsHolderList.size() == 0) {
            tagItemsHolderList.add(new TagItemsHolder("-1"));
        }

        Collections.sort(tagItemsHolderList, tagsComparator);

        lstViewBrowseTags.setAdapter(new BrowseTagsAdapter(this, tagItemsHolderList, this));
    }

    @Override
    public void onGetView(int position, View convertView, ViewGroup parent) {
        TextView tvTagName = (TextView) convertView.findViewById(R.id.tvItemName);
        TextView tvTagAmount = (TextView) convertView.findViewById(R.id.tvItemAmount);

        if (tagItemsHolderList.get(position).getTagName().equals("-1")) {
            tvTagName.setText(getString(R.string.noData));
            tvTagAmount.setVisibility(View.INVISIBLE);
            return;
        }

        tvTagName.setText(tagItemsHolderList.get(position).getTagName());
        tvTagAmount.setText("" + tagItemsHolderList.get(position).getSum());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browse_tags, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort:
                alertDialog.show();
                break;
        }

        return true;

    }

    private void setUpSortDialog() {
        class SortClickListener implements View.OnClickListener {
            private AlertDialog alertDialog;

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tvSortAmountAtoZ:
                        tagsComparator.setType(0);
                        Collections.sort(tagItemsHolderList, tagsComparator);
                        updateListView();
                        alertDialog.dismiss();
                        break;

                    case R.id.tvSortAmountZtoA:
                        tagsComparator.setType(1);
                        Collections.sort(tagItemsHolderList, tagsComparator);
                        updateListView();
                        alertDialog.dismiss();
                        break;


                }
            }

            public void setAlertDialog(AlertDialog alertDialog) {
                this.alertDialog = alertDialog;
            }
        }

        final SortClickListener sortClickListener = new SortClickListener();

        LayoutInflater layoutInflater = LayoutInflater.from(BrowseTagsActivity.this);

        View dialogView = layoutInflater.inflate(R.layout.dialog_sort_tags, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(BrowseTagsActivity.this);

        final TextView[] textViews = new TextView[]{(TextView) dialogView.findViewById(R.id.tvSortAmountAtoZ), (TextView) dialogView.findViewById(R.id.tvSortAmountZtoA)};

        for (int i = 0; i < textViews.length; i++) {
            if (i == tagsComparator.getType())
                textViews[i].setTextColor(ContextCompat.getColor(getApplication(), R.color.colorAccent));
            else
                textViews[i].setTextColor(ContextCompat.getColor(getApplication(), android.R.color.primary_text_light));

            textViews[i].setOnClickListener(sortClickListener);
        }

        builder.setView(dialogView);


        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                for (int i = 0; i < textViews.length; i++) {
                    if (i == tagsComparator.getType())
                        textViews[i].setTextColor(ContextCompat.getColor(getApplication(), R.color.colorAccent));
                    else
                        textViews[i].setTextColor(ContextCompat.getColor(getApplication(), android.R.color.primary_text_light));
                }
            }
        });

        alertDialog = builder.create();
        sortClickListener.setAlertDialog(alertDialog);
    }
}

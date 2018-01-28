package com.suraj.dailyexpenses;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.BasicItem;
import com.suraj.dailyexpenses.data.MonthlyViewStateHolder;
import com.suraj.dailyexpenses.widgets.TagsFilterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class StatsActivity extends AppCompatActivity {
    private List<BasicItem> basicItems;
    private TextView tvDailyAverage;
    private TextView tvMostExpensive;
    private TextView tvWeeklyExpenses;
    private TextView tvWeekendDetails;
    private Spinner spinMonths;
    private Spinner spinYear;
    private MonthlyViewStateHolder monthlyViewStateHolder;
    private Button btnSettings;
    private TagsFilterView tagsFilterView;
    private List<String> tags;
    private int month;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        monthlyViewStateHolder = new MonthlyViewStateHolder();
        tags = Utils.getAllTags();

        loadCurrentSettings();

        initViews();

        month = getIntent().getIntExtra(Utils.MONTH_NUMBER_INTENT_STRING, -1);
        year = getIntent().getIntExtra(Utils.YEAR_INTENT_STRING, -1);

        initSpinners();

        month = Utils.getMonthNumberFromString(spinMonths.getSelectedItem().toString());
        year = Integer.parseInt(spinYear.getSelectedItem().toString());

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagsFilterView.show();
            }
        });

        //To Do: Handle month list empty condition here
        displayStatsForMonth();

    }

    private void loadCurrentSettings() {
        monthlyViewStateHolder.addAllElements(Utils.getSharedPreferences().getStringSet(Utils.SETTINGS_DEFAULT_TAG_FILTER_STATS, new HashSet<String>()));
        monthlyViewStateHolder.setInvertMode(Utils.getSharedPreferences().getBoolean(Utils.SETTINGS_DEFAULT_TAG_FILTER_MODE_STATS, false));
    }

    private void initSpinners() {
        int i;

        final ArrayList<String> monthList = Utils.getMonthsFromDatabase();
        Collections.sort(monthList, Utils.monthComparator);
        spinMonths.setAdapter(new ArrayAdapter<>(StatsActivity.this, android.R.layout.simple_spinner_dropdown_item, monthList));

        spinMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                month = Utils.getMonthNumberFromString(monthList.get(i));
                displayStatsForMonth();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        i = monthList.indexOf(Utils.getMonthNameFromNumber(month));

        if (i != -1) {
            spinMonths.setSelection(i);
        }

        final ArrayList<String> yearList = Utils.getYearsFromDatabase();
        Collections.sort(yearList);
        spinYear.setAdapter(new ArrayAdapter<>(StatsActivity.this, android.R.layout.simple_spinner_dropdown_item, yearList));

        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                year = Integer.parseInt(yearList.get(i));
                displayStatsForMonth();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        i = yearList.indexOf("" + year);

        if (i != -1) {
            spinYear.setSelection(i);
        }

    }

    private void displayStatsForMonth() {

        /*
        Average
         */
        basicItems = Utils.getDataForMonth(month, year, monthlyViewStateHolder);

        if (basicItems.size() == 0) {
            tvDailyAverage.setText("No data available");
            return;
        }

        tvDailyAverage.setText(getString(R.string.daily_average_string, getDailyAverage(basicItems)));


        /*
        Weekly data
         */

        String weeklyData = "";
        int i = 0;
        int sums[] = getWeeklyData(basicItems);
        String[] numerics = {"first", "second", "third", "fourth", "fifth"};

        while (i < 5 && sums[i] > 0) {
            weeklyData += getString(R.string.week_expense_string, numerics[i], sums[i]);
            weeklyData += "\n";
            i++;
        }

        tvWeeklyExpenses.setText(weeklyData);

        /*
        Weekend
        */

        String weekendData = "";
        i = 0;
        int weekendSums[] = getWeekendData(basicItems);

        while (i < 5 && weekendSums[i] > 0) {
            weekendData += getString(R.string.weekend_expense_string, numerics[i], weekendSums[i]);
            weekendData += "\n";
            i++;
        }

        tvWeekendDetails.setText(weekendData);

        /*
        Max
         */
        basicItems = Utils.getAllItemsForMonth(month);
        BasicItem basicItemMax = getMaxFromMonth(basicItems, monthlyViewStateHolder);
        tvMostExpensive.setText(getString(R.string.most_expensive_string, basicItemMax.getReason(), basicItemMax.getAmount()));
    }

    private void initViews() {
        spinMonths = (Spinner) findViewById(R.id.stat_month_spinner);
        spinYear = (Spinner) findViewById(R.id.stat_year_spinner);
        btnSettings = (Button) findViewById(R.id.btn_stat_setting);

        tvDailyAverage = (TextView) findViewById(R.id.tv_daily_average);
        tvMostExpensive = (TextView) findViewById(R.id.tv_most_expensive);
        tvWeeklyExpenses = (TextView) findViewById(R.id.tv_weekly_expenses);
        tvWeekendDetails = (TextView) findViewById(R.id.tv_weekends);

        tagsFilterView = new TagsFilterView(this, tags, monthlyViewStateHolder);

        tagsFilterView.setDismissListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayStatsForMonth();
                tagsFilterView.dismiss();
            }
        });

        tagsFilterView.setTagClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final TextView tv = (TextView) view.findViewById(R.id.tvTagName);

                if (monthlyViewStateHolder.isElementIncludedInList(tags.get(i))) {
                    tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    monthlyViewStateHolder.removeElement(tags.get(i));
                } else {
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    monthlyViewStateHolder.addElement(tags.get(i));
                }

            }
        });

        tagsFilterView.setSwitchEnabled(false);
    }

    public BasicItem getMaxFromMonth(List<BasicItem> basicItems, MonthlyViewStateHolder monthlyViewStateHolder) {
        int max = 0;
        BasicItem basicItemMax = new BasicItem();

        for (BasicItem basicItem : basicItems) {
            if (!monthlyViewStateHolder.isElementAllowed(basicItem.getTag()))
                continue;

            int prev = max;

            max = Math.max(max, basicItem.getAmount());

            if (prev != max)
                basicItemMax = basicItem;
        }

        return basicItemMax;
    }

    public int getDailyAverage(List<BasicItem> basicItems) {
        int sum = 0;
        int days = 0;

        for (BasicItem basicItem : basicItems) {
            sum += basicItem.getAmount();
            days++;
        }

        return sum / days;
    }

    public int[] getWeeklyData(List<BasicItem> basicItems) {
        int days = 0;
        int sums[] = new int[5];

        int i = 0;

        for (BasicItem basicItem : basicItems) {
            sums[i] += basicItem.getAmount();

            days++;

            if (days % 7 == 0) {
                days = 0;
                i++;
            }
        }
        return sums;
    }

    public int[] getWeekendData(List<BasicItem> basicItems) {
        int sums[] = new int[5];
        int i = 0;
        int days = 0;

        for (BasicItem basicItem : basicItems) {
            if (!(basicItem.getDayName().equals("Fri") || basicItem.getDayName().equals("Sat") || basicItem.getDayName().equals("Sun")))
                continue;

            sums[i] += basicItem.getAmount();

            days++;

            if (days % 3 == 0) {
                i++;
                days = 0;
            }

        }
        return sums;
    }
}

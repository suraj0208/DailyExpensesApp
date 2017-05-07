package com.suraj.dailyexpenses;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.Item;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_expenses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_exportSpecific:

                getInputDialogBuilder((spinDates.getSelectedItem().toString() + ".csv").replaceAll("/", "-"))
                        .setTitle(getResources().getString(R.string.enterFileName))
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestSelfPermission();
                                try {
                                    String fileName = ((EditText) ((AlertDialog) dialogInterface).findViewById(R.id.etFileName)).getText().toString();

                                    if (fileName.length() == 0)
                                        return;

                                    fileName = getFileName(fileName);

                                    if (!checkDir())
                                        return;

                                    FileWriter fileWriter = new FileWriter(new File(Utils.SDCARD_DIRECTORY + "/" + fileName));
                                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                                    bufferedWriter.write("Item");
                                    bufferedWriter.write(",");
                                    bufferedWriter.write("Amount ( Rs )");
                                    bufferedWriter.write("\n");

                                    for (Item item : items) {
                                        bufferedWriter.write(item.getReason());
                                        bufferedWriter.write(",");
                                        bufferedWriter.write("" + item.getAmount());
                                        bufferedWriter.write("\n");
                                    }

                                    bufferedWriter.close();
                                    fileWriter.close();

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .show();

                return true;

            case R.id.action_exportEntire:
                Calendar calendar = Calendar.getInstance();

                String date = Utils.getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK))
                        + " " + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH)+ 1) + "-" + calendar.get(Calendar.YEAR);

                date+="-all";

                getInputDialogBuilder(date + ".csv")
                        .setTitle(getResources().getString(R.string.enterFileName))
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestSelfPermission();

                                try {
                                    String fileName = ((EditText) ((AlertDialog) dialogInterface).findViewById(R.id.etFileName)).getText().toString();

                                    if (fileName.length() == 0)
                                        return;

                                    fileName = getFileName(fileName);

                                    if (!checkDir())
                                        return;


                                    FileWriter fileWriter = new FileWriter(new File(Utils.SDCARD_DIRECTORY + "/" + fileName));
                                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                                    bufferedWriter.write("Date");
                                    bufferedWriter.write(",");
                                    bufferedWriter.write("Item");
                                    bufferedWriter.write(",");
                                    bufferedWriter.write("Amount ( Rs )");
                                    bufferedWriter.write("\n");

                                    String prevDate=null;

                                    for (Item item : Utils.getAllItemsFromDatabase()) {
                                        if(!item.getDate().equals(prevDate)){
                                            bufferedWriter.write(item.getDate());
                                        }else{
                                            bufferedWriter.write("");
                                        }
                                        bufferedWriter.write(",");
                                        bufferedWriter.write(item.getReason());
                                        bufferedWriter.write(",");
                                        bufferedWriter.write("" + item.getAmount());
                                        bufferedWriter.write("\n");
                                        prevDate=item.getDate();
                                    }

                                    bufferedWriter.close();
                                    fileWriter.close();

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .show();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getFileName(String fileName) {
        fileName = fileName.replaceAll("/", "-");

        if (!fileName.endsWith(".csv")) {
            fileName = fileName + ".csv";
        }

        return fileName;
    }

    private boolean checkDir() {
        File dir = new File(Utils.SDCARD_DIRECTORY);

        if (!dir.exists())
            if (!dir.mkdirs()) {
                Utils.showToast(getResources().getString(R.string.error));
                return false;
            }

        return true;
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

    public void requestSelfPermission() {
        if (ContextCompat.checkSelfPermission(ViewExpensesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewExpensesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private AlertDialog.Builder getInputDialogBuilder(String defaultText) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View view = layoutInflater.inflate(R.layout.dialog_view, null);

        EditText etFileName = (EditText) view.findViewById(R.id.etFileName);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        etFileName.setInputType(InputType.TYPE_CLASS_TEXT);

        etFileName.setText(defaultText);

        builder.setView(view);

        return builder;
    }
}
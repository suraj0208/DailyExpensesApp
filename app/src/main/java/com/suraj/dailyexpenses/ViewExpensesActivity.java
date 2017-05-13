package com.suraj.dailyexpenses;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.BasicItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ViewExpensesActivity extends AppCompatActivity implements InflationManager {

    private int dayOfWeek;
    private int day;
    private int month;
    private int year;

    private String date;

    private ArrayList<BasicItem> items;

    private TextView tvExpenditureForDate;
    private TextView tvExpenditureForMonth;

    private Button btnCurrentDate;
    private Button btnCurrentMonth;

    private ListView listView;

    private static ViewExpensesActivity viewExpensesActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);


        tvExpenditureForDate = (TextView) findViewById(R.id.tvExpenditureForDate);
        tvExpenditureForMonth = (TextView) findViewById(R.id.tvExpenditureForCurrentMonth);

        btnCurrentDate = (Button) findViewById(R.id.btnCurrentDate);
        btnCurrentMonth = (Button) findViewById(R.id.btnCurrentMonth);

        listView = (ListView) findViewById(R.id.lstViewItems);

        Calendar calendar = Calendar.getInstance();

        if (getIntent().getStringExtra(Utils.DATE_INTENT_STRING) == null) {
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH) + 1;
            year = calendar.get(Calendar.YEAR);
        } else {
            date = getIntent().getStringExtra(Utils.DATE_INTENT_STRING);
            String[] splits = date.split("/");

            day = Integer.parseInt(splits[0].split(" ")[1]);
            month = Integer.parseInt(splits[1]);
            year = Integer.parseInt(splits[2]);

            calendar.set(year, month - 1, day);

            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }

        setDateString();

        updateListView();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
               /* new AlertDialog.Builder(ViewExpensesActivity.this)
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
                        .show();*/

                Intent intent = new Intent(ViewExpensesActivity.this,EditItemActivity.class);
                intent.putExtra(Utils.TIMESTAMP_INTENT_STRING,items.get(i).getTimestamp());
                startActivity(intent);
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

        findViewById(R.id.btnCurrentMonth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewExpensesActivity.this, MonthExpensesActivity.class);
                intent.putExtra(Utils.MONTH_NUMBER_INTENT_STRING, month);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnCurrentDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(999);
            }
        });

        ensureSingleInstanceOnActivityStack();

        showTipsForButtons();
        requestSelfPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    private void setDateString() {
        date = Utils.getDayOfWeekString(dayOfWeek) + " " + day + "/" + month + "/" + year;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month - 1, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int year, int month, int day) {
                    ViewExpensesActivity.this.day = day;
                    ViewExpensesActivity.this.month = month + 1;
                    ViewExpensesActivity.this.year = year;

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);

                    ViewExpensesActivity.this.dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                    setDateString();
                    updateListView();
                }
            };


    private void ensureSingleInstanceOnActivityStack() {
        if (ViewExpensesActivity.viewExpensesActivity != null) {
            ViewExpensesActivity.viewExpensesActivity.finish();
        }
        ViewExpensesActivity.viewExpensesActivity = this;
    }

    private void showTipsForButtons() {
        MaterialShowcaseView materialShowcaseSelectDate = new MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.btnCurrentDate))
                .setDismissText(R.string.tipOk)
                .setContentText(R.string.tipSetDate)
                .setDismissOnTargetTouch(true)
                .setDismissOnTouch(true)
                .withRectangleShape()
                .build();

        MaterialShowcaseView materialShowcaseViewClickMonth = new MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.btnCurrentMonth))
                .setDismissText(R.string.tipOk)
                .setContentText(R.string.tipMonthClick)
                .setDismissOnTargetTouch(true)
                .setDismissOnTouch(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        showTipsForListView();
                    }
                })
                .withRectangleShape()
                .build();


        MaterialShowcaseSequence sequence;

        sequence = Utils.getMaterialShowcaseSequence(this, Utils.VIEW_EXPENSES_ACTIVITY_BUTTON_TIPS, new MaterialShowcaseView[]{materialShowcaseSelectDate,materialShowcaseViewClickMonth});

        if (sequence != null) {
            sequence.start();

            if(sequence.hasFired())
                showTipsForListView();

        }


    }

    private void showTipsForListView() {

        MaterialShowcaseSequence sequence;

        MaterialShowcaseView materialShowcaseViewSingleClick = new MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.lstViewItems))
                .setDismissText(R.string.tipOk)
                .setContentText(R.string.tipListViewSingleClick)
                .setDismissOnTargetTouch(true)
                .setDismissOnTouch(true)
                .withRectangleShape()
                .build();

        MaterialShowcaseView materialShowcaseViewLongPress = new MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.lstViewItems))
                .setDismissText(R.string.tipGotIt)
                .setContentText(R.string.tipListViewLongPress)
                .setDismissOnTargetTouch(true)
                .setDismissOnTouch(true)
                .withRectangleShape()
                .build();

        if (listView.getAdapter().getCount() > 0) {
            sequence = Utils.getMaterialShowcaseSequence(this, Utils.VIEW_EXPENSES_ACTIVITY_LISTVIEW_TIPS, new MaterialShowcaseView[]{materialShowcaseViewSingleClick,materialShowcaseViewLongPress});

            if (sequence != null)
                sequence.start();

        }
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

                getInputDialogBuilder((date + ".csv").replaceAll("/", "-"))
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

                                    for (BasicItem item : items) {
                                        bufferedWriter.write(item.getReason());
                                        bufferedWriter.write(",");
                                        bufferedWriter.write("" + item.getAmount());
                                        bufferedWriter.write("\n");
                                    }

                                    bufferedWriter.close();
                                    fileWriter.close();
                                    Utils.showToast(getString(R.string.exportSuccessfulNotify));

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
                        + " " + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);

                date += "-all";

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

                                    String prevDate = null;

                                    for (BasicItem item : Utils.getAllItemsFromDatabase()) {
                                        if (!item.getDate().equals(prevDate)) {
                                            bufferedWriter.write(item.getDate());
                                        } else {
                                            bufferedWriter.write("");
                                        }
                                        bufferedWriter.write(",");
                                        bufferedWriter.write(item.getReason());
                                        bufferedWriter.write(",");
                                        bufferedWriter.write("" + item.getAmount());
                                        bufferedWriter.write("\n");
                                        prevDate = item.getDate();
                                    }

                                    bufferedWriter.close();
                                    fileWriter.close();
                                    Utils.showToast(getString(R.string.exportSuccessfulNotify));


                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .show();
                return true;

            case R.id.action_backup:
                requestSelfPermission();
                Utils.backup();
                return true;

            case R.id.action_restore:
                requestSelfPermission();
                Utils.restore(true);
                return true;


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
        items = Utils.getItemsForDate(date);
        listView.setAdapter(new BasicItemsAdapter(getApplicationContext(), items, this));
        tvExpenditureForDate.setText("" + Utils.getExpenditureForDate(date, true));
        tvExpenditureForMonth.setText("" + Utils.getExpensesForMonth(Integer.parseInt(date.split("/")[1]), true));

        btnCurrentDate.setText(day + "/" + month);
        btnCurrentMonth.setText(Utils.getMonthNameFromNumber(Integer.parseInt(date.split("/")[1])));

    }


    @Override
    public void onGetView(int position, View rowView, ViewGroup parent) {

        BasicItem item = items.get(position);

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
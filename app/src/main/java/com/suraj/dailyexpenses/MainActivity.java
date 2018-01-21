package com.suraj.dailyexpenses;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.BasicItem;
import com.suraj.dailyexpenses.data.MonthlyViewStateHolder;
import com.suraj.dailyexpenses.widgets.TagSelectorView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity {
    private int year;
    private int month;
    private int day;

    private HashSet<String> commonReasons;

    private TextView tvDate;
    private TextView tvTodayExpenditure;
    private TextView tvPickDate;

    private Button btnSetDate;

    private EditText etSpendReason;
    private EditText etSpentAmount;

    private EditText etTag;

    private static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.initRealm(getApplicationContext());

        commonReasons = new HashSet<>();

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTodayExpenditure = (TextView) findViewById(R.id.tvTodaysExpenditure);
        tvPickDate = (TextView) findViewById(R.id.tvPickDate);

        etSpendReason = (EditText) findViewById(R.id.etSpendReason);
        etSpendReason.requestFocus();
        etSpentAmount = (EditText) findViewById(R.id.etSpentAmount);

        etTag = (EditText) findViewById(R.id.etTag);
        etTag.setText(Utils.DEFAULT_TAG_NAME);

        initReasonViews();
        initTagViews();
        initAmountViews();
        initSaveButton();
        initViewButtons();
        loadSettings();

        btnSetDate = (Button) findViewById(R.id.btnSetDate);
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(999);
            }
        });

        Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        initPickerViews(day, month);

        showDateOnTextView(calendar.get(Calendar.DAY_OF_WEEK), year, month + 1, day);

        showTodayExpenditure();
        setupTips();

        ensureSingleInstanceOnActivityStack();
        loadFromNotification();

//        HashMap<String,Integer> stringIntegerHashMap = Utils.getTopItemsForMonth(4);
//
//        int total = Utils.getExpensesForMonth(4);
//        System.out.println("total " + total);
//
//        int i=0;
//
//        for(String k:stringIntegerHashMap.keySet()){
//            System.out.println(k+ " " +stringIntegerHashMap.get(k));
//            PieChartActivity.angles[i] = ((double)(stringIntegerHashMap.get(k)*360))/total;
//            i++;
//        }
//        startActivity(new Intent(MainActivity.this, PieChartActivity.class));

    }

    private void loadSettings() {
        etTag.setText(Utils.getSharedPreferences().getString(Utils.SETTINGS_DEFAULT_TAG, Utils.DEFAULT_TAG_NAME));
    }

    private void initTagViews() {
        final List<String> tags = Utils.getAllTags();
        MonthlyViewStateHolder monthlyViewStateHolder = new MonthlyViewStateHolder();
        final TagSelectorView tagSelectorView = new TagSelectorView(MainActivity.this, tags, monthlyViewStateHolder);

        tagSelectorView.setTagClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                etTag.setText(tags.get(i));
                tagSelectorView.dismiss();
            }
        });

        tagSelectorView.setDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                etTag.setSelection(etTag.getText().length());
            }
        });

        class BooleanHolder {
            boolean bool;
        }

        final BooleanHolder prevFocus = new BooleanHolder();

        etTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!prevFocus.bool) {
                    tagSelectorView.show();
                }

                prevFocus.bool = b;

            }
        });

        etTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagSelectorView.show();
            }
        });
    }

    private void loadFromNotification() {

        if (getIntent().getExtras() == null)
            return;

        String name = getIntent().getStringExtra("name");
        int rs = getIntent().getIntExtra("rs", -1);

        if (rs != -1) {
            etSpentAmount.setText("" + rs);
        }
        if (name != null) {
            etSpendReason.setText(name);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(965778);
    }

    private void initPickerViews(int day, int month) {
        tvPickDate.setText("" + day);
        btnSetDate.setText(Utils.getMonthNameFromNumber(month + 1));
    }

    private void ensureSingleInstanceOnActivityStack() {
        if (MainActivity.mainActivity != null) {
            MainActivity.mainActivity.finish();
        }
        MainActivity.mainActivity = this;
    }

    private void setupTips() {

        MaterialShowcaseView materialShowcaseViewDate = buildCircularMaterialShowcaseView(findViewById(R.id.btnSetDate), getString(R.string.tipOk), getString(R.string.tipSetDate));

        MaterialShowcaseView materialShowcaseViewCommonItems = buildRectangularMaterialShowcaseView(findViewById(R.id.hzScrollViewReasons), getString(R.string.tipGotIt), getString(R.string.tipCommonItems), true);

        MaterialShowcaseView materialShowcaseViewAdjust = buildRectangularMaterialShowcaseView(findViewById(R.id.llPlusMinusButtons), getString(R.string.tipStart), getString(R.string.tipAdjustAndSave), false);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(100);

        materialShowcaseViewDate.setConfig(config);

        MaterialShowcaseSequence sequence = Utils.getMaterialShowcaseSequence(this, Utils.MAIN_ACTIVITY_TIPS, new MaterialShowcaseView[]{materialShowcaseViewDate, materialShowcaseViewCommonItems, materialShowcaseViewAdjust});

        if (sequence != null)
            sequence.start();


    }

    private MaterialShowcaseView buildCircularMaterialShowcaseView(View view, String dismiss, String content) {
        return new MaterialShowcaseView.Builder(this)
                .setTarget(view)
                .setDismissText(dismiss)
                .setContentText(content)
                .setDismissOnTouch(true)
                .setDismissOnTargetTouch(true)
                .build();
    }

    private MaterialShowcaseView buildRectangularMaterialShowcaseView(View view, String dismiss, String content, boolean span) {
        return new MaterialShowcaseView.Builder(this)
                .setTarget(view)
                .setDismissText(dismiss)
                .setContentText(content)
                .setDismissOnTouch(true)
                .setDismissOnTargetTouch(true)
                .withRectangleShape(span)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTodayExpenditure();
    }

    private void initViewButtons() {
        (findViewById(R.id.btnViewDay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ViewExpensesActivity.class).putExtra(Utils.DATE_INTENT_STRING, tvDate.getText().toString()));
            }
        });

        (findViewById(R.id.btnViewMonth)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MonthExpensesActivity.class);
                intent.putExtra(Utils.MONTH_NUMBER_INTENT_STRING, month + 1);
                intent.putExtra(Utils.YEAR_INTENT_STRING, year);
                startActivity(intent);
            }
        });

        (findViewById(R.id.btnBrowseTags)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BrowseTagsActivity.class);
                intent.putExtra(Utils.MONTH_NUMBER_INTENT_STRING, month + 1);
                intent.putExtra(Utils.YEAR_INTENT_STRING, year);
                startActivity(intent);
            }
        });
    }

    private void showTodayExpenditure() {
        tvTodayExpenditure.setText(getResources().getString(R.string.todaysExpenditure, Utils.getExpenditureForDate(tvDate.getText().toString(), null)));
    }

    private void initSaveButton() {
        Button btnSave = (Button) findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reason = etSpendReason.getText().toString().trim();
                String tag = etTag.getText().toString().trim();
                String amount = etSpentAmount.getText().toString().trim();

                if (reason.length() == 0 || tag.length() == 0 || amount.length() == 0) {
                    Utils.showToast(getResources().getString(R.string.validDataError));
                    return;
                }

                try {
                    int intAmount = Integer.parseInt(amount);

                    if (intAmount <= 0) {
                        Utils.showToast(getResources().getString(R.string.validDataError));
                        return;
                    }

                    Utils.saveInDatabase(tvDate.getText().toString(), reason, intAmount, tag);
                    tvTodayExpenditure.setText(getResources().getString(R.string.todaysExpenditure, Utils.getExpenditureForDate(tvDate.getText().toString(), null)));
                    etSpentAmount.setText("");
                    etSpendReason.setText("");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                MainActivity.this.closeIME(etSpendReason);

            }
        });

    }

    private void initAmountViews() {
        Button[] btnAmounts = new Button[]{(Button) findViewById(R.id.btn10),
                (Button) findViewById(R.id.btn20),
                (Button) findViewById(R.id.btn30),
                (Button) findViewById(R.id.btn40),
                (Button) findViewById(R.id.btn50),
                (Button) findViewById(R.id.btn100)};

        View.OnClickListener amountsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                int amount;
                if (etSpentAmount.getText().toString().length() == 0) {
                    etSpentAmount.setText(btn.getText());
                } else {
                    amount = Integer.parseInt(etSpentAmount.getText().toString());
                    amount += Integer.parseInt(btn.getText().toString());
                    etSpentAmount.setText("" + amount);
                }
            }
        };

        for (Button button : btnAmounts)
            button.setOnClickListener(amountsOnClickListener);


        (findViewById(R.id.btnAmountPlus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int amount = Integer.parseInt(etSpentAmount.getText().toString());
                    amount++;
                    etSpentAmount.setText("" + amount);

                } catch (NumberFormatException ne) {
                    ne.printStackTrace();
                }

            }
        });

        (findViewById(R.id.btnAmountMinus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int amount = Integer.parseInt(etSpentAmount.getText().toString());
                    amount--;

                    if (amount < 0)
                        amount = 0;

                    etSpentAmount.setText("" + amount);
                } catch (NumberFormatException ne) {
                    ne.printStackTrace();
                }
            }
        });
    }

    private void initReasonViews() {
        final View hzScrollView = (findViewById(R.id.hzScrollViewReasons));

        final String[] reasons = new String[]{
                getResources().getString(R.string.breakfast),
                getResources().getString(R.string.tea),
                getResources().getString(R.string.snacks),
                getResources().getString(R.string.lunch),
                getResources().getString(R.string.dinner)
        };

        Button[] btnReasons = new Button[]{(Button) findViewById(R.id.btnBreakfastText),
                (Button) findViewById(R.id.btnTeaText),
                (Button) findViewById(R.id.btnSnacksText),
                (Button) findViewById(R.id.btnLunchText),
                (Button) findViewById(R.id.btnDinnerText),
        };

        for (int i = 0; i < btnReasons.length; i++) {
            final int index = i;
            commonReasons.add(reasons[i]);
            btnReasons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    etSpendReason.setText(reasons[index]);
                }
            });
        }


        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.tvDate);
        layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.etTag);

        layoutParams.setMargins(((RelativeLayout.LayoutParams) etSpendReason.getLayoutParams()).leftMargin, ((RelativeLayout.LayoutParams) etSpendReason.getLayoutParams()).topMargin, ((RelativeLayout.LayoutParams) etSpendReason.getLayoutParams()).rightMargin, 0);

        etSpendReason.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etSpendReason.getText().length() == 0) {
                    //etTag.setVisibility(View.GONE);
                    hzScrollView.setVisibility(View.VISIBLE);
                    etSpendReason.setLayoutParams(layoutParams);
                } else {
                    hzScrollView.setVisibility(View.GONE);
                    //etTag.setVisibility(View.VISIBLE);
                    etSpendReason.setLayoutParams(layoutParams);
                }

                if (commonReasons.contains(etSpendReason.getText().toString())) {
                    //etTag.setVisibility(View.GONE);
                    hzScrollView.setVisibility(View.VISIBLE);
                    etSpendReason.setLayoutParams(layoutParams);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int year, int month, int day) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    initPickerViews(day, month);
                    showDateOnTextView(dayOfWeek, year, month + 1, day);
                    showTodayExpenditure();
                }
            };

    private void showDateOnTextView(int dayOfWeek, int year, int month, int day) {
        tvDate.setText(new StringBuilder().append(Utils.getDayOfWeekString(dayOfWeek)).append(" ").append(day).append("/")
                .append(month).append("/").append(year));
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    public void closeIME(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_exportEntire:
                Calendar calendar = Calendar.getInstance();

                String date = Utils.getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK))
                        + " " + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);

                date += "-all";

                Utils.getInputDialogBuilder(this, date + ".csv")
                        .setTitle(getResources().getString(R.string.enterFileName))
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.requestSelfPermission(MainActivity.this);

                                try {
                                    String fileName = ((EditText) ((AlertDialog) dialogInterface).findViewById(R.id.etFileName)).getText().toString();

                                    if (fileName.length() == 0)
                                        return;

                                    fileName = Utils.makeCSVFileName(fileName);

                                    if (!Utils.checkDir())
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
                                            bufferedWriter.write(",");
                                            bufferedWriter.write("Total");
                                            bufferedWriter.write(",");
                                            bufferedWriter.write("" + Utils.getExpenditureForDate(prevDate, null));
                                            bufferedWriter.write("\n");
                                            bufferedWriter.write("\n");
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
                Utils.requestSelfPermission(MainActivity.this);
                Utils.backup();
                return true;

            case R.id.action_restore:
                Utils.requestSelfPermission(MainActivity.this);
                Utils.restore(true);
                return true;

            case R.id.action_stats:
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                intent.putExtra(Utils.MONTH_NUMBER_INTENT_STRING, month + 1);
                intent.putExtra(Utils.YEAR_INTENT_STRING, year);
                startActivity(intent);
                break;

            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}

package com.suraj.dailyexpenses;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        commonReasons = new HashSet<>();

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTodayExpenditure = (TextView) findViewById(R.id.tvTodaysExpenditure);
        tvPickDate = (TextView) findViewById(R.id.tvPickDate);

        etSpendReason = (EditText) findViewById(R.id.etSpendReason);
        etSpendReason.requestFocus();
        etSpentAmount = (EditText) findViewById(R.id.etSpentAmount);

        etTag = (EditText) findViewById(R.id.etTag);
        etTag.setText(R.string.daily_tag);

        initReasonViews();
        initTagViews();
        initAmountViews();
        initSaveButton();
        initViewButtons();

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

        Utils.initRealm(getApplicationContext());
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

    private void initTagViews() {
        class BooleanHolder {
            boolean bool;
        }

        final BooleanHolder prevFocus = new BooleanHolder();

        etTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!prevFocus.bool) {
                    showTagsDialog();
                }

                if (b) {
                    prevFocus.bool = true;
                } else {
                    prevFocus.bool = false;
                }

            }
        });

        etTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTagsDialog();
            }
        });
    }

    public void showTagsDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

        View dialogView = layoutInflater.inflate(R.layout.dialog_tags, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        ListView listViewTags = (ListView) dialogView.findViewById(R.id.listViewTags);

        dialogView.findViewById(R.id.btnDone).setVisibility(View.GONE);

        final List<String> tags = Utils.getAllTags();

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, tags);

        builder.setView(dialogView);

        listViewTags.setAdapter(itemsAdapter);


        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        listViewTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                etTag.setText(tags.get(i));
                alertDialog.dismiss();
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
                startActivity(new Intent(MainActivity.this, MonthExpensesActivity.class));
            }
        });
    }

    private void showTodayExpenditure() {
        tvTodayExpenditure.setText(getResources().getString(R.string.todaysExpenditure, Utils.getExpenditureForDate(tvDate.getText().toString(), true)));
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
                    tvTodayExpenditure.setText(getResources().getString(R.string.todaysExpenditure, Utils.getExpenditureForDate(tvDate.getText().toString(), true)));
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
                etSpentAmount.setText(btn.getText());
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


}

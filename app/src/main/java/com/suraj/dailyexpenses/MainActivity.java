package com.suraj.dailyexpenses;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashSet;

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

    private CheckBox chkBoxInfrequent;

    private static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commonReasons = new HashSet<>();

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTodayExpenditure = (TextView) findViewById(R.id.tvTodaysExpenditure);
        tvPickDate = (TextView)findViewById(R.id.tvPickDate);

        etSpendReason = (EditText) findViewById(R.id.etSpendReason);
        etSpentAmount = (EditText) findViewById(R.id.etSpentAmount);

        chkBoxInfrequent = (CheckBox) findViewById(R.id.chkboxInfrequent);

        initReasonViews();
        initAmountViews();
        initSaveButton();
        initViewButtons();

        btnSetDate = (Button)findViewById(R.id.btnSetDate);
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

        initPickerViews(day,month);

        showDateOnTextView(calendar.get(Calendar.DAY_OF_WEEK), year, month + 1, day);

        Utils.initRealm(getApplicationContext());
        showTodayExpenditure();
        setupTips();

        ensureSingleInstanceOnActivityStack();

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

    private void initPickerViews(int day,int month) {
        tvPickDate.setText(""+day);
        btnSetDate.setText(Utils.getMonthNameFromNumber(month+1));
    }

    private void ensureSingleInstanceOnActivityStack() {
        if(MainActivity.mainActivity!=null){
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
                if (etSpendReason.getText().length() == 0 || etSpentAmount.getText().length() == 0) {
                    Utils.showToast(getResources().getString(R.string.validDataError));
                    return;
                }

                Utils.saveInDatabase(tvDate.getText().toString(), etSpendReason.getText().toString().trim(), Integer.parseInt(etSpentAmount.getText().toString().trim()), chkBoxInfrequent.isChecked());
                tvTodayExpenditure.setText(getResources().getString(R.string.todaysExpenditure, Utils.getExpenditureForDate(tvDate.getText().toString(), true)));
                etSpentAmount.setText("");
                etSpendReason.setText("");
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
        layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.chkboxInfrequent);

        layoutParams.setMargins(((RelativeLayout.LayoutParams) etSpendReason.getLayoutParams()).leftMargin, ((RelativeLayout.LayoutParams) etSpendReason.getLayoutParams()).topMargin, ((RelativeLayout.LayoutParams) etSpendReason.getLayoutParams()).rightMargin, 0);

        etSpendReason.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etSpendReason.getText().length() == 0) {
                    chkBoxInfrequent.setVisibility(View.GONE);
                    hzScrollView.setVisibility(View.VISIBLE);
                    etSpendReason.setLayoutParams(layoutParams);
                } else {
                    hzScrollView.setVisibility(View.GONE);
                    chkBoxInfrequent.setVisibility(View.VISIBLE);
                    etSpendReason.setLayoutParams(layoutParams);
                }

                if (commonReasons.contains(etSpendReason.getText().toString())) {
                    chkBoxInfrequent.setVisibility(View.GONE);
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
                    initPickerViews(day,month);
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

package com.suraj.dailyexpenses;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    private int year;
    private int month;
    private int day;

    private HashSet<String> commonReasons;

    private TextView tvDate;
    private TextView tvTodayExpenditure;

    private EditText etSpendReason;
    private EditText etSpentAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commonReasons = new HashSet<>();

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTodayExpenditure = (TextView) findViewById(R.id.tvTodaysExpenditure);

        etSpendReason = (EditText) findViewById(R.id.etSpendReason);
        etSpentAmount = (EditText) findViewById(R.id.etSpentAmount);

        initReasonViews();
        initAmountViews();
        initSaveButton();
        initViewButtons();

        (findViewById(R.id.btnSetDate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(999);
            }
        });

        Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDate(calendar.get(Calendar.DAY_OF_WEEK), year, month + 1, day);

        Utils.initRealm(getApplicationContext());
        showTodayExpenditure();
    }

    private void initViewButtons() {
        (findViewById(R.id.btnViewDay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ViewExpensesActivity.class).putExtra("date",tvDate.getText().toString()));
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        showDate(dayOfWeek, year, month + 1, day);

        tvTodayExpenditure.setText(getResources().getString(R.string.todaysExpenditure, Utils.getExpenditure(tvDate.getText().toString())));
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

                Utils.saveInDatabase(tvDate.getText().toString(), etSpendReason.getText().toString(), Integer.parseInt(etSpentAmount.getText().toString()));
                tvTodayExpenditure.setText(getResources().getString(R.string.todaysExpenditure, Utils.getExpenditure(tvDate.getText().toString())));
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
                try{
                    int amount = Integer.parseInt(etSpentAmount.getText().toString());
                    amount++;
                    etSpentAmount.setText(""+amount);

                }catch (NumberFormatException ne){
                    ne.printStackTrace();
                }

            }
        });

        (findViewById(R.id.btnAmountMinus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    int amount = Integer.parseInt(etSpentAmount.getText().toString());
                    amount--;

                    if(amount<0)
                        amount=0;

                    etSpentAmount.setText(""+amount);
                }catch (NumberFormatException ne){
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

        etSpendReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etSpendReason.getText().length()==0){
                    hzScrollView.setVisibility(View.VISIBLE);
                }else{
                    hzScrollView.setVisibility(View.GONE);
                }

                if(commonReasons.contains(etSpendReason.getText().toString())){
                    hzScrollView.setVisibility(View.VISIBLE);
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
                    showDate(dayOfWeek, year, month + 1, day);
                }
            };

    private void showDate(int dayOfWeek, int year, int month, int day) {
        tvDate.setText(new StringBuilder().append(Utils.getDayOfWeekText(dayOfWeek)).append(" ").append(day).append("/")
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

    public void closeIME(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



}

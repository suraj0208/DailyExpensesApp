package com.suraj.dailyexpenses;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.suraj.dailyexpenses.data.BasicItem;

public class EditItemActivity extends AppCompatActivity {
    private BasicItem basicItem;

    private EditText etEditItemReason;
    private EditText etEditItemAmount;

    private CheckBox checkboxEditInfrequent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);


        if (getIntent().getLongExtra(Utils.TIMESTAMP_INTENT_STRING, -1) == -1) {
            Utils.showToast(getString(R.string.error));
            finish();
        }

        basicItem = Utils.getBasicItemFromDataBase(getIntent().getLongExtra(Utils.TIMESTAMP_INTENT_STRING, -1));

        (etEditItemReason = (EditText) findViewById(R.id.etEditItemReason)).setText(basicItem.getReason());
        (etEditItemAmount = (EditText) findViewById(R.id.etEditItemAmount)).setText(Integer.toString(basicItem.getAmount()));
        //(checkboxEditInfrequent = (CheckBox) findViewById(R.id.checkboxEditInfrequent)).setChecked(basicItem.isInFrequent());

        (findViewById(R.id.btnEditDelete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EditItemActivity.this)
                        .setTitle("Sure?")
                        .setMessage(R.string.deleteConfirmation)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.deleteFromDatabase(basicItem);
                                Utils.showToast(R.string.deleteSuccess);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit_save:
                if (etEditItemAmount.getText().length() == 0 || etEditItemReason.getText().length() == 0) {
                    Utils.showToast(R.string.validDataError);
                    break;
                }


                BasicItem newBasicItem = new BasicItem(basicItem);

                newBasicItem.setReason(etEditItemReason.getText().toString());
                newBasicItem.setAmount(Integer.parseInt(etEditItemAmount.getText().toString()));
                //newBasicItem.setInFrequent(checkboxEditInfrequent.isChecked());

                Utils.editItem(newBasicItem);
                Utils.showToast(R.string.editSuccess);
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}

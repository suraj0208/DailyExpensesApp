package com.suraj.dailyexpenses;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.suraj.dailyexpenses.data.BasicItem;

import java.util.List;

public class EditItemActivity extends AppCompatActivity {
    private BasicItem basicItem;

    private EditText etEditItemReason;
    private EditText etEditItemAmount;

    private EditText etEditTag;


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
        (etEditTag = (EditText) findViewById(R.id.etEditTag)).setText(basicItem.getTag());

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

        class BooleanHolder {
            boolean bool;
        }

        final BooleanHolder prevFocus = new BooleanHolder();

        etEditTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        etEditTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTagsDialog();
            }
        });
    }

    public void showTagsDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(EditItemActivity.this);

        View dialogView = layoutInflater.inflate(R.layout.dialog_tags, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(EditItemActivity.this);

        ListView listViewTags = (ListView) dialogView.findViewById(R.id.listViewTags);

        dialogView.findViewById(R.id.btnDone).setVisibility(View.GONE);

        final List<String> tags = Utils.getAllTags();

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(EditItemActivity.this, android.R.layout.simple_list_item_1, tags);

        builder.setView(dialogView);

        listViewTags.setAdapter(itemsAdapter);


        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        listViewTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                etEditTag.setText(tags.get(i));
                alertDialog.dismiss();
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
                if (etEditItemAmount.getText().length() == 0 || etEditItemReason.getText().length() == 0 || etEditTag.getText().length() == 0) {
                    Utils.showToast(R.string.validDataError);
                    break;
                }


                BasicItem newBasicItem = new BasicItem(basicItem);

                newBasicItem.setReason(etEditItemReason.getText().toString());
                newBasicItem.setAmount(Integer.parseInt(etEditItemAmount.getText().toString()));
                newBasicItem.setTag(etEditTag.getText().toString());

                Utils.editItem(newBasicItem);
                Utils.showToast(R.string.editSuccess);
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}

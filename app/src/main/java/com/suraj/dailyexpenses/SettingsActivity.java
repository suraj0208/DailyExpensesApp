package com.suraj.dailyexpenses;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.suraj.dailyexpenses.data.MonthlyViewStateHolder;
import com.suraj.dailyexpenses.widgets.TagSelectorView;
import com.suraj.dailyexpenses.widgets.TagsFilterView;

import java.util.HashSet;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private EditText etDefaultTag;
    private Button btnDefaultTagFilter;
    private Button btnDefaultTagFilterStats;
    private boolean defaultTagFilterMode;
    private MonthlyViewStateHolder monthlyViewStateHolder;
    private TagSelectorView tagSelectorView;
    private TagsFilterView tagsFilterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setitngs);
        initViews();

        setCurrentSettings();

        //call it here - to set proper strike through
        initTagSelectorView();

        setListeners();

    }

    public void setTagListeners(){
        final List<String> tags = Utils.getAllTags();
        tagsFilterView = new TagsFilterView(this, tags, monthlyViewStateHolder);

        tagsFilterView.setDismissListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagsFilterView.dismiss();
            }
        });

        tagsFilterView.setTagClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final TextView tv = (TextView) view.findViewById(R.id.tvTagName);

                if (monthlyViewStateHolder.isElementIncluded(tags.get(i))) {
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    monthlyViewStateHolder.removeElement(tags.get(i));
                } else {
                    tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    monthlyViewStateHolder.addElement(tags.get(i));
                }
            }
        });

        tagsFilterView.setSwitchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch aSwitch = (Switch) view;

                monthlyViewStateHolder.setInvertMode(!monthlyViewStateHolder.isInvertMode());

                if (monthlyViewStateHolder.isInvertMode()) {
                    aSwitch.setText(getString(R.string.include_these));
                } else {
                    aSwitch.setText(getString(R.string.exclude_these));
                }

                defaultTagFilterMode = monthlyViewStateHolder.isInvertMode();
            }
        });
    }

    private void setListeners() {
        btnDefaultTagFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTagListeners();
                tagsFilterView.show();
            }
        });

        btnDefaultTagFilterStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTagListeners();
                tagsFilterView.show();
            }
        });

        btnDefaultTagFilterStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagSelectorView.show();
            }
        });

        class BooleanHolder {
            boolean bool;
        }

        final BooleanHolder prevFocus = new BooleanHolder();

        etDefaultTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!prevFocus.bool) {
                    tagSelectorView.show();
                }

                prevFocus.bool = b;

            }
        });

        etDefaultTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagSelectorView.show();
            }
        });
    }

    private void setCurrentSettings() {
        etDefaultTag.setText(Utils.getStringFromSharedPreferences(Utils.SETTINGS_DEFAULT_TAG));

        monthlyViewStateHolder = new MonthlyViewStateHolder();

        monthlyViewStateHolder.addAllElements(Utils.getSharedPreferences().getStringSet(Utils.SETTINGS_DEFAULT_TAG_FILTER, new HashSet<String>()));
        monthlyViewStateHolder.setInvertMode(Utils.getSharedPreferences().getBoolean(Utils.SETTINGS_DEFAULT_TAG_FILTER_MODE, false));
    }

    private void initViews() {
        etDefaultTag = (EditText) findViewById(R.id.setting_et_default_tag);

        btnDefaultTagFilter = (Button) findViewById(R.id.setting_btn_default_filter);
        btnDefaultTagFilterStats = (Button) findViewById(R.id.setting_btn_default_filter_stats);

        etDefaultTag.setFocusable(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSettings();
    }

    private void saveSettings() {
        Utils.putStringInSharedPreferences(Utils.SETTINGS_DEFAULT_TAG, etDefaultTag.getText().toString());

        Utils.getEditor().putStringSet(Utils.SETTINGS_DEFAULT_TAG_FILTER, monthlyViewStateHolder.getCurrentTags());
        Utils.getEditor().putBoolean(Utils.SETTINGS_DEFAULT_TAG_FILTER_MODE, defaultTagFilterMode);

        Utils.getEditor().apply();
    }

    public void initTagSelectorView() {
        final List<String> tags = Utils.getAllTags();
        MonthlyViewStateHolder monthlyViewStateHolder = new MonthlyViewStateHolder();
        tagSelectorView = new TagSelectorView(SettingsActivity.this, tags, monthlyViewStateHolder);

        tagSelectorView.setTagClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                etDefaultTag.setText(tags.get(i));
                tagSelectorView.dismiss();
            }
        });

        tagSelectorView.setDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                etDefaultTag.setSelection(etDefaultTag.getText().length());
            }
        });
    }
}
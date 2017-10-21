package com.suraj.dailyexpenses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.suraj.dailyexpenses.data.BasicItem;
import com.suraj.dailyexpenses.data.MonthlyViewStateHolder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by suraj on 18/3/17.
 */
public class Utils {
    public static final String MAIN_ACTIVITY_TIPS = "v2MainActivityTips";
    public static final String VIEW_EXPENSES_ACTIVITY_BUTTON_TIPS = "v3ViewExpensesActivityButtonTips";
    public static final String VIEW_EXPENSES_ACTIVITY_LISTVIEW_TIPS = "v3ViewExpensesActivityListViewTips";

    public static final String MONTH_NUMBER_INTENT_STRING = "monthNumber";
    public static final String DATE_INTENT_STRING = "date";
    public static final String ITEM_INTENT_STRING = "basicitem";
    public static final String TIMESTAMP_INTENT_STRING = "basicitem";

    public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory() + "/DailyExpenses";

    public static final String REMINDERS_SHARED_PREFERENCE_STRING = "reminders";
    public static final String MONTHLY_STATE_HOLDER_INTENT_STRING = "monthly_state_holder";

    public static Comparator<Object> dateComparator;
    public static Comparator<String> monthComparator;

    public static Comparator<String> sortingComparator;

    private static Realm realm;

    private static Context context;

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static RealmResults<BasicItem> tempRealmResults;

    static class SortingComparator implements Comparator<BasicItem> {
        public int getType() {
            return type;
        }

        private int type = 0;

        /**
         * 0 date a-z
         * 1 date z-a
         * 2 amount a-z
         * 3 amount z-a
         */

        @Override
        public int compare(BasicItem b1, BasicItem b2) {
            switch (type) {
                case 0:
                    if (b1.getYear() != b2.getYear()) {
                        return b1.getYear() - b2.getYear();
                    } else {
                        if (b1.getMonth() != b2.getMonth()) {
                            return b1.getMonth() - b2.getMonth();
                        } else {
                            return b1.getDay() - b2.getDay();
                        }
                    }

                case 1:
                    if (b1.getYear() != b2.getYear()) {
                        return b2.getYear() - b1.getYear();
                    } else {
                        if (b1.getMonth() != b2.getMonth()) {
                            return b2.getMonth() - b1.getMonth();
                        } else {
                            return b2.getDay() - b1.getDay();
                        }
                    }

                case 2:
                    return b1.getAmount() - b2.getAmount();

                case 3:
                    return b2.getAmount() - b1.getAmount();

            }

            return 0;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static void initRealm(Context context) {
        Realm.init(context);
        Utils.context = context;

        try {
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException ex) {
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
            Realm.deleteRealm(realmConfiguration);
            realm = Realm.getDefaultInstance();
            ex.printStackTrace();
        }

        dateComparator = getDateComparator();
        monthComparator = getMonthComparator();

    }

    public static Context getContext() {
        return context;
    }

    private static Comparator<Object> getDateComparator() {
        return new Comparator<Object>() {
            @Override
            public int compare(Object s_o, Object t1_o) {
                String s;
                String t1;
                if (s_o instanceof String && t1_o instanceof String) {
                    s = (String) s_o;
                    t1 = (String) t1_o;
                } else if (s_o instanceof BasicItem && t1_o instanceof BasicItem) {
                    BasicItem b1 = ((BasicItem) s_o);
                    BasicItem b2 = ((BasicItem) t1_o);

                    if (b1.getYear() != b2.getYear()) {
                        return b1.getYear() - b2.getYear();
                    } else {
                        if (b1.getMonth() != b2.getMonth()) {
                            return b1.getMonth() - b2.getMonth();
                        } else {
                            return b1.getDay() - b2.getDay();
                        }
                    }

                } else {
                    return -1;
                }

                int year1 = Integer.parseInt(s.split("/")[2]);
                int month1 = Integer.parseInt(s.split("/")[1]);
                int day1 = Integer.parseInt(s.split("/")[0].split(" ")[1]);

                int year2 = Integer.parseInt(t1.split("/")[2]);
                int month2 = Integer.parseInt(t1.split("/")[1]);
                int day2 = Integer.parseInt(t1.split("/")[0].split(" ")[1]);

                if (year1 != year2) {
                    return year1 - year2;
                } else {
                    if (month1 != month2) {
                        return month1 - month2;
                    } else {
                        return day1 - day2;
                    }
                }
            }
        };
    }

    private static Comparator<String> getMonthComparator() {
        return new Comparator<String>() {
            @Override
            public int compare(String s_o, String t1_o) {
                return getMonthNumberFromString(s_o) - getMonthNumberFromString(t1_o);
            }
        };
    }

    public static BasicItem getBasicItemFromDataBase(long timestamp) {
        return realm.where(BasicItem.class).equalTo("timestamp", timestamp).findFirst();
    }

    public static boolean saveInDatabase(String date, String reason, int amount, String tag) {
        try {
            realm.beginTransaction();

            BasicItem basicItem = realm.createObject(BasicItem.class);
            basicItem.setDate(date);
            basicItem.setReason(reason);
            basicItem.setAmount(amount);
            basicItem.setTimestamp(System.currentTimeMillis());
            basicItem.setTag(tag);
            realm.commitTransaction();
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static ArrayList<BasicItem> getAllItemsFromDatabase() {
        RealmQuery<BasicItem> itemRealmQuery = realm.where(BasicItem.class);

        ArrayList<BasicItem> items = new ArrayList<>();

        for (BasicItem BasicItem : itemRealmQuery.findAll()) {
            items.add(BasicItem);
        }

        Collections.sort(items, dateComparator);

        return items;
    }


    public static ArrayList<BasicItem> getItemsForDate(String date, MonthlyViewStateHolder monthlyViewStateHolder) {
        RealmQuery<BasicItem> realmQuery = realm.where(BasicItem.class).equalTo("date", date);

        ArrayList<BasicItem> results = new ArrayList<>();

        for (BasicItem basicItem : realmQuery.findAll()) {
            if (monthlyViewStateHolder != null && monthlyViewStateHolder.isElementIncluded(basicItem.getTag()) == monthlyViewStateHolder.isInvertMode())
                continue;

            results.add(basicItem);
        }

        Collections.sort(results, new Comparator<BasicItem>() {
            @Override
            public int compare(BasicItem BasicItem, BasicItem t1) {
                return (int) (BasicItem.getTimestamp() - t1.getTimestamp());
            }
        });

        return results;
    }

    public static int getExpenditureForDate(String date, MonthlyViewStateHolder monthlyViewStateHolder) {
        int sum = 0;
        ArrayList<BasicItem> results = getItemsForDate(date, monthlyViewStateHolder);

        for (BasicItem basicItem : results) {
            if (monthlyViewStateHolder != null && monthlyViewStateHolder.isElementIncluded(basicItem.getTag()) == monthlyViewStateHolder.isInvertMode())
                continue;

            sum += basicItem.getAmount();
        }

        return sum;
    }

    public static ArrayList<String> getAllDatesInDatabase() {
        RealmResults<BasicItem> realmResults = realm.where(BasicItem.class).findAll();

        HashSet<String> datesHashSet = new HashSet<>();
        ArrayList<String> dates = new ArrayList<>();


        for (BasicItem BasicItem : realmResults)
            datesHashSet.add(BasicItem.getDate());

        dates.addAll(datesHashSet);

        Collections.sort(dates, dateComparator);
        return dates;

    }

    public static String getDayOfWeekString(int i) {
        String days[] = new String[]{"PlaceHolder", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return days[i];
    }

    public static void showToast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int id) {
        Toast.makeText(context, context.getString(id), Toast.LENGTH_SHORT).show();
    }

    public static void deleteFromDatabase(BasicItem BasicItem) {
        realm.beginTransaction();

        RealmResults<BasicItem> realmResults = realm.where(BasicItem.class).equalTo("timestamp", BasicItem.getTimestamp()).findAll();
        realmResults.deleteAllFromRealm();

        realm.commitTransaction();

    }

    public static ArrayList<String> getMonthsFromDatabase() {
        RealmQuery<BasicItem> itemRealmQuery = realm.where(BasicItem.class);
        RealmResults<BasicItem> realmResults = itemRealmQuery.findAll();

        HashSet<String> monthSet = new HashSet<>();

        for (BasicItem BasicItem : realmResults) {
            String date = BasicItem.getDate();

            String currentMonth = date.split("/")[1];

            monthSet.add(getMonthNameFromNumber(Integer.parseInt(currentMonth)));
        }

        return new ArrayList<>(monthSet);
    }

    public static int getExpensesForMonth(int month, MonthlyViewStateHolder monthlyViewStateHolder) {
        RealmQuery<BasicItem> itemRealmQuery = realm.where(BasicItem.class);
        RealmResults<BasicItem> realmResults = itemRealmQuery.findAll();
        int sum = 0;
        for (BasicItem basicItem : realmResults) {

            int currentMonth = basicItem.getMonth();

            if (currentMonth != month || (monthlyViewStateHolder != null && monthlyViewStateHolder.isElementIncluded(basicItem.getTag()) == monthlyViewStateHolder.isInvertMode()))
                continue;

            sum += basicItem.getAmount();

        }

        return sum;
    }

    public static String getMonthNameFromNumber(int month) {
        String[] months = {"PLACE_HOLDER", "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
        return months[month];
    }

    public static int getMonthNumberFromString(String monthName) {
        HashMap<String, Integer> stringStringHashMap = new HashMap<>();

        stringStringHashMap.put("Jan", 1);
        stringStringHashMap.put("Feb", 2);
        stringStringHashMap.put("Mar", 3);
        stringStringHashMap.put("Apr", 4);
        stringStringHashMap.put("May", 5);
        stringStringHashMap.put("June", 6);
        stringStringHashMap.put("July", 7);
        stringStringHashMap.put("Aug", 8);
        stringStringHashMap.put("Sept", 9);
        stringStringHashMap.put("Oct", 10);
        stringStringHashMap.put("Nov", 11);
        stringStringHashMap.put("Dec", 12);

        return stringStringHashMap.get(monthName);
    }

    public static ArrayList<BasicItem> getDataForMonth(int month, MonthlyViewStateHolder monthlyViewStateHolder) {
        RealmQuery<BasicItem> itemRealmQuery = realm.where(BasicItem.class);
        RealmResults<BasicItem> realmResults = itemRealmQuery.findAll();

        TreeMap<String, Integer> monthExpensesTreeMap = new TreeMap<>();

        for (BasicItem basicItem : realmResults) {
            String date = basicItem.getDate();

            if (basicItem.getMonth() != month || (monthlyViewStateHolder != null && monthlyViewStateHolder.isElementIncluded(basicItem.getTag()) == monthlyViewStateHolder.isInvertMode()))
                continue;

                Integer current = monthExpensesTreeMap.get(date);
                if (current == null) {
                    monthExpensesTreeMap.put(date, basicItem.getAmount());
                } else {
                    monthExpensesTreeMap.put(date, current + basicItem.getAmount());
                }


        }

        ArrayList<BasicItem> basicItems = new ArrayList<>();

        for (String date : monthExpensesTreeMap.keySet()) {
            BasicItem basicItem = new BasicItem();
            basicItem.setDate(date);
            basicItem.setAmount(monthExpensesTreeMap.get(date));
            basicItems.add(basicItem);
        }
        return basicItems;

    }

    public static ArrayList<BasicItem> getExpenditureForItem(String itemName) {
        RealmQuery<BasicItem> itemRealmQuery = realm.where(BasicItem.class).equalTo("reason", itemName, Case.INSENSITIVE);
        RealmResults<BasicItem> realmResults = itemRealmQuery.findAll();

        tempRealmResults = realmResults;

        HashMap<Integer, Integer> monthItemExpenses = new HashMap<>();

        for (BasicItem BasicItem : realmResults) {

            String date = BasicItem.getDate();

            int currentMonth = Integer.parseInt(date.split("/")[1]);
            Integer current = monthItemExpenses.get(currentMonth);

            if (current == null) {
                monthItemExpenses.put(currentMonth, BasicItem.getAmount());
            } else {
                monthItemExpenses.put(currentMonth, current + BasicItem.getAmount());
            }
        }

        ArrayList<BasicItem> basicItems = new ArrayList<>();

        for (Integer month : monthItemExpenses.keySet()) {
            BasicItem basicItem = new BasicItem();
            basicItem.setMonth(month);
            basicItem.setAmount(monthItemExpenses.get(month));
            basicItems.add(basicItem);
        }

        return basicItems;

    }

    public static int getSum(List<BasicItem> basicItemList) {
        int sum = 0;
        for (BasicItem basicItem : basicItemList)
            sum += basicItem.getAmount();

        return sum;
    }

    public static ArrayList<BasicItem> getExpenditureForItemForMonth(int clickedMonth) {
        RealmResults<BasicItem> realmResults = tempRealmResults;


        HashMap<String, Integer> monthItemExpenses = new HashMap<>();

        for (BasicItem BasicItem : realmResults) {

            String date = BasicItem.getDate();

            if (Integer.parseInt(date.split("/")[1]) != clickedMonth)
                continue;


            Integer current = monthItemExpenses.get(date);

            if (current == null) {
                monthItemExpenses.put(date, BasicItem.getAmount());
            } else {
                monthItemExpenses.put(date, current + BasicItem.getAmount());
            }
        }

        ArrayList<BasicItem> basicItems = new ArrayList<>();

        for (String date : monthItemExpenses.keySet()) {
            BasicItem basicItem = new BasicItem();
            basicItem.setDate(date);
            basicItem.setAmount(monthItemExpenses.get(date));
            basicItems.add(basicItem);
        }

        return basicItems;
    }

    private static void invalidateTempResults() {
        tempRealmResults = null;
    }


    public static HashMap<String, Integer> getTopItemsForMonth(int month) {
        RealmQuery<BasicItem> itemRealmQuery = realm.where(BasicItem.class);

        RealmResults<BasicItem> realmResults = itemRealmQuery.findAll();

        HashMap<String, Integer> monthItemExpenses = new HashMap<>();

        for (BasicItem BasicItem : realmResults) {

            String date = BasicItem.getDate();

            int currentMonth = Integer.parseInt(date.split("/")[1]);

            if (currentMonth != month)
                continue;


            Integer current = monthItemExpenses.get(BasicItem.getReason());

            if (current == null) {
                monthItemExpenses.put(BasicItem.getReason(), BasicItem.getAmount());
            } else {
                monthItemExpenses.put(BasicItem.getReason(), current + BasicItem.getAmount());
            }
        }

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();

        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(monthItemExpenses.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> integerIntegerEntry, Map.Entry<String, Integer> t1) {
                return t1.getValue() - integerIntegerEntry.getValue();
            }
        });

        for (int i = 0; i < 4; i++) {
            stringIntegerHashMap.put(entries.get(i).getKey(), entries.get(i).getValue());
        }

        return stringIntegerHashMap;
    }

    public static MaterialShowcaseSequence getMaterialShowcaseSequence(Activity activity, String ID, MaterialShowcaseView[] materialShowcaseViews) {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(100);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, ID);

        sequence.setConfig(config);

        try {
            Field field = sequence.getClass().getDeclaredField("mShowcaseQueue");
            field.setAccessible(true);

            Queue<MaterialShowcaseView> queue = (Queue<MaterialShowcaseView>) field.get(sequence);

            for (MaterialShowcaseView materialShowcaseView : materialShowcaseViews)
                queue.add(materialShowcaseView);

            return sequence;

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void backup() {
        File file = new File(Utils.SDCARD_DIRECTORY + "/backup.csv");

        if (file.exists())
            file.delete();

        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (BasicItem basicItem : Utils.getAllItemsFromDatabase()) {
                bufferedWriter.write(basicItem.getDate());
                bufferedWriter.write(",");
                bufferedWriter.write(basicItem.getReason());
                bufferedWriter.write(",");
                bufferedWriter.write("" + basicItem.getAmount());
                bufferedWriter.write(",");
                bufferedWriter.write("" + basicItem.getTimestamp());
                bufferedWriter.write(",");
                bufferedWriter.write("" + basicItem.getTag());
                bufferedWriter.write("\n");
            }

            bufferedWriter.close();
            fileWriter.close();

            Utils.showToast(context.getString(R.string.backupCompleteNotify));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void deleteEntireDatabase() {
        realm.beginTransaction();
        RealmQuery<BasicItem> itemRealmQuery = realm.where(BasicItem.class);
        itemRealmQuery.findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static void exportMonthlyDetails() {
        File file = new File(Utils.SDCARD_DIRECTORY + "/monthly_details.csv");

        if (file.exists())
            file.delete();

        ArrayList<String> months = Utils.getMonthsFromDatabase();

        HashMap<String, Integer> detailsHashMap = new HashMap<>();

        for (BasicItem basicItem : Utils.getAllItemsFromDatabase()) {

            String currentMonth = Utils.getMonthNameFromNumber(basicItem.getMonth()) + " " + basicItem.getYear();

            Integer currentAmount = detailsHashMap.get(currentMonth);

            if (currentAmount == null) {
                detailsHashMap.put(currentMonth, basicItem.getAmount());
            } else {
                detailsHashMap.put(currentMonth, currentAmount + basicItem.getAmount());
            }
        }

        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(detailsHashMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> stringIntegerEntry, Map.Entry<String, Integer> t1) {
                String[] monthYear1 = stringIntegerEntry.getKey().split(" ");

                int month1 = Utils.getMonthNumberFromString(monthYear1[0]);
                int year1 = Integer.parseInt(monthYear1[1]);

                String[] monthYear2 = t1.getKey().split(" ");

                int month2 = Utils.getMonthNumberFromString(monthYear2[0]);
                int year2 = Integer.parseInt(monthYear2[1]);

                if (year1 != year2) {
                    return year1 - year2;
                }


                return month1 - month2;
            }
        });

        try

        {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("Month");
            bufferedWriter.write(",");
            bufferedWriter.write("Amount ( Rs )");
            bufferedWriter.write("\n");

            for (Map.Entry<String, Integer> entry : list) {
                bufferedWriter.write(entry.getKey());
                bufferedWriter.write(",");
                bufferedWriter.write("" + entry.getValue());
                bufferedWriter.write("\n");
            }

            bufferedWriter.close();
            fileWriter.close();

            Utils.showToast(context.getString(R.string.exportSuccessfulNotify));

        } catch (
                IOException e
                )

        {
            e.printStackTrace();
        }

    }

    public static void restore(boolean wipeDatabase) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(Utils.SDCARD_DIRECTORY + "/backup.csv")));
            String s;

            if (wipeDatabase)
                Utils.deleteEntireDatabase();

            realm.beginTransaction();

            while (((s = bufferedReader.readLine()) != null)) {
                String[] splits = s.split(",");
                BasicItem basicItem = realm.createObject(BasicItem.class);
                basicItem.setDate(splits[0]);
                basicItem.setReason(splits[1]);
                basicItem.setAmount(Integer.parseInt(splits[2]));
                basicItem.setTimestamp(Long.parseLong(splits[3]));
                basicItem.setTag(splits[4]);

            }

            realm.commitTransaction();
            Utils.showToast(context.getString(R.string.restoreCompleteNotify));


        } catch (FileNotFoundException e) {
            Utils.showToast(context.getString(R.string.backupNotFoundNotify));
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void editItem(BasicItem givenBasicItem) {
        realm.beginTransaction();
        BasicItem basicItem = realm.where(BasicItem.class).equalTo("timestamp", givenBasicItem.getTimestamp()).findFirst();
        basicItem.setReason(givenBasicItem.getReason());
        basicItem.setAmount(givenBasicItem.getAmount());
        basicItem.setDate(givenBasicItem.getDate());
        basicItem.setTag(givenBasicItem.getTag());
        realm.commitTransaction();
    }

    public static void putStringInSharedPreferences(String name, String value) {
        if (context == null) {
            Log.e("com.suraj.dailyexpenses", "context null in Utils");
            return;
        }

        initPrefs();

        editor.putString(name, value);
        editor.apply();

    }

    public static void putStringSetInSharedPreferences(String name, Set<String> value) {
        if (context == null) {
            Log.e("com.suraj.dailyexpenses", "context null in Utils");
            return;
        }

        initPrefs();
        editor.putStringSet(name, value);
        editor.apply();

    }

    public static Set<String> getStringSetFromSharedPreferences(String name, boolean returnRef) {
        if (context == null) {
            Log.e("com.suraj.dailyexpenses", "context null in Utils");
            return new HashSet<>();
        }

        initPrefs();

        if (returnRef)
            return sharedPreferences.getStringSet(name, new HashSet<String>());

        return new HashSet<>(sharedPreferences.getStringSet(name, new HashSet<String>()));

    }

    private static void initPrefs() {
        if (sharedPreferences == null) {
            if (context == null) {
                Log.e("com.suraj.dailyexpenses", "context null in Utils");
                return;
            }

            sharedPreferences = context.getSharedPreferences("my_prefs", 0);
            editor = sharedPreferences.edit();
        }
    }

    public static ArrayList<String> getAllTags() {
        RealmQuery<BasicItem> itemRealmQuery = realm.where(BasicItem.class);


        Set<String> tagSet = new HashSet<>();

        ArrayList<String> items = new ArrayList<>();

        for (BasicItem basicItem : itemRealmQuery.findAll()) {
            tagSet.add(basicItem.getTag());
        }

        items.addAll(tagSet);
        Collections.sort(items);

        return items;
    }

    public static AlertDialog.Builder getInputDialogBuilder(Context context, String defaultText) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.dialog_view, null);

        EditText etFileName = (EditText) view.findViewById(R.id.etFileName);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        etFileName.setInputType(InputType.TYPE_CLASS_TEXT);

        etFileName.setText(defaultText);

        builder.setView(view);

        return builder;
    }

    public static void requestSelfPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    public static String makeCSVFileName(String fileName) {
        fileName = fileName.replaceAll("/", "-");

        if (!fileName.endsWith(".csv")) {
            fileName = fileName + ".csv";
        }

        return fileName;
    }

    public static boolean checkDir() {
        File dir = new File(Utils.SDCARD_DIRECTORY);

        if (!dir.exists())
            if (!dir.mkdirs()) {
                Utils.showToast(context.getResources().getString(R.string.error));
                return false;
            }

        return true;
    }

}

/*
TODO
fix last day total in export all
fix total on first line in export all

 */
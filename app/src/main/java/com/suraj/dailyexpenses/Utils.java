package com.suraj.dailyexpenses;

import android.content.Context;
import android.widget.Toast;

import com.suraj.dailyexpenses.data.Day;
import com.suraj.dailyexpenses.data.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by suraj on 18/3/17.
 */
public class Utils {
    public static final String MONTH_NUMBER_INTENT_STRING = "monthNumber";
    public static final String DATE_INTENT_STRING = "date";

    private static Realm realm;
    private static Context context;

    public static Comparator<Object> dateComparator;

    public static void initRealm(Context context) {
        Realm.init(context);
        realm = Realm.getDefaultInstance();
        Utils.context = context;

        dateComparator = getDateComparator();

    }

    private static Comparator<Object> getDateComparator(){
        return new Comparator<Object>() {
            @Override
            public int compare(Object s_o, Object t1_o) {
                String s;
                String t1;
                if (s_o instanceof String && t1_o instanceof String) {
                    s = (String) s_o;
                    t1 = (String) t1_o;
                }else if (s_o instanceof Day && t1_o instanceof Day){
                    s=((Day)s_o).getDate();
                    t1=((Day)t1_o).getDate();
                }else{
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

    public static boolean saveInDatabase(String date, String reason, int amount) {
        try {
            realm.beginTransaction();

            Item item = realm.createObject(Item.class);
            item.setDate(date);
            item.setReason(reason);
            item.setAmount(amount);

            item.setTimestamp(System.currentTimeMillis());

            realm.commitTransaction();
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static ArrayList<Item> getItemsForDate(String date) {
        RealmQuery<Item> realmQuery = realm.where(Item.class).equalTo("date", date);

        ArrayList<Item> results = new ArrayList<>();

        for (Item item : realmQuery.findAll()) {
            results.add(item);
        }

        Collections.sort(results, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item t1) {
                return (int) (item.getTimestamp() - t1.getTimestamp());
            }
        });

        return results;
    }

    public static int getExpenditureForDate(String date) {
        int sum = 0;
        ArrayList<Item> results = getItemsForDate(date);

        for (Item item : results)
            sum += item.getAmount();

        return sum;
    }

    public static ArrayList<String> getAllDatesInDatabase() {
        RealmResults<Item> realmResults = realm.where(Item.class).findAll();

        HashSet<String> datesHashSet = new HashSet<>();
        ArrayList<String> dates = new ArrayList<>();


        for (Item item : realmResults)
            datesHashSet.add(item.getDate());

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

    public static void deleteFromDatabase(Item item) {
        realm.beginTransaction();

        RealmResults<Item> realmResults = realm.where(Item.class).equalTo("timestamp", item.getTimestamp()).findAll();
        realmResults.deleteAllFromRealm();

        realm.commitTransaction();

    }

    public static ArrayList<String> getMonthsFromDatabase() {
        RealmQuery<Item> itemRealmQuery = realm.where(Item.class);
        RealmResults<Item> realmResults = itemRealmQuery.findAll();

        HashSet<String> monthSet = new HashSet<>();

        for (Item item : realmResults) {
            String date = item.getDate();

            String currentMonth = date.split("/")[1];

            monthSet.add(getMonthStringFromNumber(Integer.parseInt(currentMonth)));
        }

        return new ArrayList<>(monthSet);
    }

    public static int getExpensesForMonth(String month) {
        RealmQuery<Item> itemRealmQuery = realm.where(Item.class);
        RealmResults<Item> realmResults = itemRealmQuery.findAll();
        int sum = 0;
        for (Item item : realmResults) {
            String date = item.getDate();

            String currentMonth = date.split("/")[1];

            if (currentMonth.equals(month)) {
                sum += item.getAmount();
            }
        }
        return sum;
    }

    public static String getMonthStringFromNumber(int month) {
        String[] months = {"PLACE_HOLDER", "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
        return months[month];
    }

    public static String getMonthNumberFromString(String monthName) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();

        stringStringHashMap.put("Jan", "1");
        stringStringHashMap.put("Feb", "2");
        stringStringHashMap.put("Mar", "3");
        stringStringHashMap.put("Apr", "4");
        stringStringHashMap.put("May", "5");
        stringStringHashMap.put("June", "6");
        stringStringHashMap.put("July", "7");
        stringStringHashMap.put("Aug", "8");
        stringStringHashMap.put("Sept", "9");
        stringStringHashMap.put("Oct", "10");
        stringStringHashMap.put("Nov", "11");
        stringStringHashMap.put("Dec", "12");

        return stringStringHashMap.get(monthName);
    }

    public static ArrayList<Day> getDataForMonth(String month) {
        RealmQuery<Item> itemRealmQuery = realm.where(Item.class);
        RealmResults<Item> realmResults = itemRealmQuery.findAll();

        TreeMap<String, Integer> monthExpensesTreeMap = new TreeMap<>();

        for (Item item : realmResults) {
            String date = item.getDate();

            String currentMonth = date.split("/")[1];

            if (currentMonth.equals(month)) {
                Integer current = monthExpensesTreeMap.get(date);
                if (current == null) {
                    monthExpensesTreeMap.put(date, item.getAmount());
                } else {
                    monthExpensesTreeMap.put(date, current + item.getAmount());
                }

            }
        }

        ArrayList<Day> days = new ArrayList<>();

        for (String date : monthExpensesTreeMap.keySet()) {
            Day day = new Day();
            day.setDate(date);
            day.setExpenses(monthExpensesTreeMap.get(date));
            days.add(day);
        }
        return days;

    }

    public static ArrayList<Item> getExpenditureForItem(String itemName){
        RealmQuery<Item> itemRealmQuery = realm.where(Item.class).equalTo("reason",itemName, Case.INSENSITIVE);
        RealmResults<Item> realmResults = itemRealmQuery.findAll();

        return null;


    }

}

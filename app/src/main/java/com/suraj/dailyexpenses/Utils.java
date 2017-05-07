package com.suraj.dailyexpenses;

import android.content.Context;
import android.widget.Toast;

import com.suraj.dailyexpenses.data.BasicItem;
import com.suraj.dailyexpenses.data.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public static final String ITEM_INTENT_STRING = "item";

    private static Realm realm;
    public static Context context;

    public static Comparator<Object> dateComparator;
    public static Comparator<String> monthComparator;


    private static RealmResults<Item> tempRealmResults;

    public static void initRealm(Context context) {
        Realm.init(context);
        realm = Realm.getDefaultInstance();
        Utils.context = context;

        dateComparator = getDateComparator();
        monthComparator = getMonthComparator();

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
                return getMonthNumberFromString(s_o)-getMonthNumberFromString(t1_o);
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

            monthSet.add(getMonthNameFromNumber(Integer.parseInt(currentMonth)));
        }

        return new ArrayList<>(monthSet);
    }

    public static int getExpensesForMonth(int month) {
        RealmQuery<Item> itemRealmQuery = realm.where(Item.class);
        RealmResults<Item> realmResults = itemRealmQuery.findAll();
        int sum = 0;
        for (Item item : realmResults) {
            String date = item.getDate();

            int currentMonth = Integer.parseInt(date.split("/")[1]);

            if (currentMonth==month) {
                sum += item.getAmount();
            }
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
        stringStringHashMap.put("June",6);
        stringStringHashMap.put("July", 7);
        stringStringHashMap.put("Aug", 8);
        stringStringHashMap.put("Sept", 9);
        stringStringHashMap.put("Oct", 10);
        stringStringHashMap.put("Nov", 11);
        stringStringHashMap.put("Dec", 12);

        return stringStringHashMap.get(monthName);
    }

    public static ArrayList<BasicItem> getDataForMonth(String month) {
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
        RealmQuery<Item> itemRealmQuery = realm.where(Item.class).equalTo("reason", itemName, Case.INSENSITIVE);
        RealmResults<Item> realmResults = itemRealmQuery.findAll();

        tempRealmResults = realmResults;

        HashMap<Integer, Integer> monthItemExpenses = new HashMap<>();

        for (Item item : realmResults) {

            String date = item.getDate();

            int currentMonth = Integer.parseInt(date.split("/")[1]);
            Integer current = monthItemExpenses.get(currentMonth);

            if (current == null) {
                monthItemExpenses.put(currentMonth, item.getAmount());
            } else {
                monthItemExpenses.put(currentMonth, current + item.getAmount());
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
        int sum =0 ;
        for(BasicItem basicItem:basicItemList)
            sum+=basicItem.getAmount();

        return sum;
    }

    public static ArrayList<BasicItem> getExpenditureForItemForMonth(int clickedMonth) {
        RealmResults<Item> realmResults = tempRealmResults;


        HashMap<String, Integer> monthItemExpenses = new HashMap<>();

        for (Item item : realmResults) {

            String date = item.getDate();

            if(Integer.parseInt(date.split("/")[1])!=clickedMonth)
                continue;


            Integer current = monthItemExpenses.get(date);

            if (current == null) {
                monthItemExpenses.put(date, item.getAmount());
            } else {
                monthItemExpenses.put(date, current + item.getAmount());
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

    private static void invalidateTempResults(){
        tempRealmResults = null;
    }


    public static HashMap<String,Integer> getTopItemsForMonth(int month){
        RealmQuery<Item> itemRealmQuery = realm.where(Item.class);

        RealmResults<Item> realmResults = itemRealmQuery.findAll();

        HashMap<String, Integer> monthItemExpenses = new HashMap<>();

        for (Item item : realmResults) {

            String date = item.getDate();

            int currentMonth = Integer.parseInt(date.split("/")[1]);

            if(currentMonth!=month)
                continue;


            Integer current = monthItemExpenses.get(item.getReason());

            if (current == null) {
                monthItemExpenses.put(item.getReason(), item.getAmount());
            } else {
                monthItemExpenses.put(item.getReason(), current + item.getAmount());
            }
        }

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();

        ArrayList<Map.Entry<String,Integer>> entries =  new ArrayList<>(monthItemExpenses.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> integerIntegerEntry, Map.Entry<String, Integer> t1) {
                return t1.getValue()-integerIntegerEntry.getValue();
            }
        });

        for(int i=0;i<4;i++){
            stringIntegerHashMap.put(entries.get(i).getKey(),entries.get(i).getValue());
        }

        return stringIntegerHashMap;
    }
}

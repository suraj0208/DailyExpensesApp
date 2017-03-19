package com.suraj.dailyexpenses;

import android.content.Context;
import android.widget.Toast;

import com.suraj.dailyexpenses.data.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by suraj on 18/3/17.
 */
public class Utils {
    private static Realm realm;
    private static Context context;

    public static void initRealm(Context context) {
        Realm.init(context);
        realm = Realm.getDefaultInstance();
        Utils.context=context;
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

    public static int getExpenditure(String date) {
        int sum = 0;
        ArrayList<Item> results = getItemsForDate(date);

        for (Item item : results)
            sum += item.getAmount();

        return sum;
    }

    public static ArrayList<String> getAvailableDates() {
        RealmResults<Item> realmResults = realm.where(Item.class).findAll();

        HashSet<String> datesHashSet = new HashSet<>();
        ArrayList<String> dates = new ArrayList<>();


        for (Item item : realmResults)
            datesHashSet.add(item.getDate());

        dates.addAll(datesHashSet);

        Collections.sort(dates, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
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
        });
        return dates;

    }
    public static String getDayOfWeekText(int i){
        String days[] = new String[]{ "PlaceHolder","Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return  days[i];
    }

    public static void showToast(String string) {
        Toast.makeText(context,string,Toast.LENGTH_SHORT).show();
    }
}

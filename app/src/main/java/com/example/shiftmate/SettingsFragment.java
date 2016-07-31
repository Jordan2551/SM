package com.example.shiftmate;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.Currencies.Currency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
  If you're developing for Android 3.0 (API level 11) and higher, you should use a PreferenceFragment to display your list of Preference objects.
   You can add a PreferenceFragment to any activity—you don't need to use PreferenceActivity.
  Fragments provide a more flexible architecture for your application, compared to using activities alone, no matter what kind of activity you're building.
  As such, we suggest you use PreferenceFragment to control the display of your settings instead of PreferenceActivity when possible.

 */
public class SettingsFragment extends PreferenceFragment {


    public static final String KEY_CURRENCY_LIST_PREF = "currency_list";
    public static final String KEY_PAY_PER_HOUR = "payPerHour";

    public static ListPreference currencyPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);//Set the layout that this PreferenceFragment will contain

        //region Currency List Preference
        currencyPref = (ListPreference) findPreference(KEY_CURRENCY_LIST_PREF);

        String[] entries = new String[DataSource.currencies.currencyList.size()];

        for (int i = 0; i < DataSource.currencies.currencyList.size(); i++) {
            entries[i] = DataSource.currencies.currencyList.get(i).currencyName;
        }

        //Must update both setEntires and setEntryValues
        currencyPref.setEntries(entries);
        currencyPref.setEntryValues(entries);

        //endregion

    }

}


package com.shiftbuddy.app;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.shiftbuddy.app.Database.DataSource;

/*
  If you're developing for Android 3.0 (API level 11) and higher, you should use a PreferenceFragment to display your list of Preference objects.
   You can add a PreferenceFragment to any activity—you don't need to use PreferenceActivity.
  Fragments provide a more flexible architecture for your application, compared to using activities alone, no matter what kind of activity you're building.
  As such, we suggest you use PreferenceFragment to control the display of your settings instead of PreferenceActivity when possible.

 */
public class SettingsFragment extends PreferenceFragment {

    //Preference key consts
    public static final String KEY_CURRENCY_LIST = "currencyList";
    public static final String KEY_PAY_PER_HOUR_SWITCH = "hourlyWageSwitch";
    public static final String KEY_PAY_PER_HOUR = "hourlyWage";
    public static final String KEY_TIPS_SWITCH = "tipsSwitch";
    public static final String KEY_SALES_SWITCH = "salesSwitch";
    public static final String KEY_SALES_PERCENTAGE = "salesPercentage";

    public static ListPreference currencyPref;
    public static SwitchPreference hourlyWageSwitch;
    public static EditTextPreference hourlyWage;
    public static SwitchPreference salesEnabledPref;
    public static EditTextPreference salesPercentagePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);//Set the layout that this PreferenceFragment will contain

        currencyPref = (ListPreference) findPreference(KEY_CURRENCY_LIST);

        String[] entries = new String[DataSource.currencies.currencyList.size()];

        for (int i = 0; i < DataSource.currencies.currencyList.size(); i++) {
            entries[i] = DataSource.currencies.currencyList.get(i).currencyName;
        }

        //Must update both setEntires and setEntryValues
        currencyPref.setEntries(entries);
        currencyPref.setEntryValues(entries);

        hourlyWageSwitch = (SwitchPreference) findPreference(KEY_PAY_PER_HOUR_SWITCH);
        hourlyWage = (EditTextPreference) findPreference(KEY_PAY_PER_HOUR);

        salesEnabledPref = (SwitchPreference) findPreference(KEY_SALES_SWITCH);
        salesPercentagePref = (EditTextPreference) findPreference(KEY_SALES_PERCENTAGE);

    }

}


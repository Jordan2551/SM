package com.example.shiftbuddy;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    //Preference key consts
    public static final String KEY_CURRENCY_LIST = "currencyList";
    public static final String KEY_PAY_PER_HOUR_SWITCH = "hourlyWageSwitch";
    public static final String KEY_PAY_PER_HOUR = "hourlyWage";
    public static final String KEY_TIPS_SWITCH = "tipsSwitch";
    public static final String KEY_SALES_SWITCH = "salesSwitch";
    public static final String KEY_SALES_PERCENTAGE = "salesPercentage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        setTitle("Settings");

    }

    /**
     * Get the selected index of the preference list
     *
     * @return returns the selected item index of the list
     * otherwise, returns 0 if the list is not initialized or the particular index is not found (-1)
     */
    public static int getCurrencyListSelectedIndex() {
        if (SettingsFragment.currencyPref != null && SettingsFragment.currencyPref.findIndexOfValue(SettingsFragment.currencyPref.getValue()) != -1)
            return SettingsFragment.currencyPref.findIndexOfValue(SettingsFragment.currencyPref.getValue());

        else
            return 0;
    }

}

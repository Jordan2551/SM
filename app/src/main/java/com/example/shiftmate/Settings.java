package com.example.shiftmate;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

public class Settings extends Activity {

    //Consts for each preference key
    public static final String KEY_CURRENCY_LIST = "currency_list";
    public static final String KEY_PAY_PER_HOUR = "payPerHour";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        //This takes our SettingsFragment class and set's it's xml layout to this activity
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        //Calling this during onCreate() ensures that your application is properly initialized with default settings, which your application might need to read in order to determine some behaviors
        //(such as whether to download data while on a cellular network).
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    }

    /**
     * DESC
     * Get the selected index of the preference list
     * @return
     * returns the selected item index of the list
     * otherwise, returns 0 if the list is not initialized for other reasons
     */
    public static int getCurrencyListSelectedIndex(){
        if(SettingsFragment.currencyPref != null)
            return SettingsFragment.currencyPref.findIndexOfValue(SettingsFragment.currencyPref.getValue());
        else
            return 0;
    }

}

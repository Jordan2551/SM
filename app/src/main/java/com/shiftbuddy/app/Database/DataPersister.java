package com.shiftbuddy.app.Database;

import android.support.v7.app.AppCompatActivity;

import com.shiftbuddy.app.Database.Tables.Currencies.Currencies;
import com.shiftbuddy.app.Database.Tables.Shifts.Shifts;

/**
 * Created by jorda_000 on 8/31/2016.
 */
public class DataPersister extends AppCompatActivity{

    //The onStart method is a callback method for when an activity has been sitting in the background for a long time and gains focus once again.
    //In this onStart method it is important to obtain all possibly lost universal data that is common to more than one activity, that got cleared up by the garbage collector.
    public void onStart(){

        if(DataSource.shifts == null){
            DataSource.shifts = new Shifts();
            DataSource.shifts.GetRecords(DataSource.shifts.tableName);
        }

        if(DataSource.currencies == null)
            DataSource.currencies = new Currencies();

        super.onStart();

    }

}

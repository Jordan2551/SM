package com.example.shiftmate.Database;

import android.content.Context;

import com.example.shiftmate.Database.Connector.DBConnector;
import com.example.shiftmate.Database.Tables.Currencies.Currencies;
import com.example.shiftmate.Database.Tables.Currencies.Currency;
import com.example.shiftmate.Database.Tables.Shifts.Shifts;
import com.example.shiftmate.MainActivity;

import java.util.ArrayList;

/**
 * Created by jorda_000 on 2/4/2016.
 * Contains generic functions for pulling/inserting data for the different DB tables
 */
public class DataSource {

    private static final String LOGTAG = "DataSource";

    //region Table & Data List Pointers

    public static Shifts shifts = new Shifts();
    public  static Currencies currencies = new Currencies();

    //endregion

    public static void DeleteRecord(String tableName, long Id){
        DBConnector.database.delete(tableName, "Id = " + Id, null);
    }

    public static void DeleteAllRecords(String tableName){
        DBConnector.database.execSQL("DELETE FROM " + tableName + ";");
    }

}

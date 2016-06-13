package com.example.shiftmate.Database;

import android.database.Cursor;
import android.util.Log;

import com.example.shiftmate.Database.Connector.DBConnector;
import com.example.shiftmate.Database.Tables.Shifts.Shift;
import com.example.shiftmate.Database.Tables.Shifts.Shifts;
import com.example.shiftmate.Database.Tables.TableItem;

import java.util.ArrayList;

/**
 * Created by jorda_000 on 2/4/2016.
 * Contains generic functions for pulling/inserting data for the different DB tables
 */
public class DataSource {

    private static final String LOGTAG = "DataSource";

    //region Table Pointers

    public static Shifts shifts = new Shifts();

    //endregion

    public static void DeleteRecord(String tableName, long Id){
        DBConnector.database.delete(tableName, "Id = " + Id, null);
    }

    public static void DeleteAllRecords(String tableName){
        DBConnector.database.execSQL("DELETE FROM " + tableName + ";");
    }

}

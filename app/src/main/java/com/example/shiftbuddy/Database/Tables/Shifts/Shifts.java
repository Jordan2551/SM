package com.example.shiftbuddy.Database.Tables.Shifts;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.shiftbuddy.BackupAgent.SBBackupAgentHelper;
import com.example.shiftbuddy.Database.Connector.DBConnector;
import com.example.shiftbuddy.Database.DataSource;
import com.example.shiftbuddy.Database.Tables.TableItem;
import com.example.shiftbuddy.MainActivity;
import com.example.shiftbuddy.Shared.UniversalFunctions;
import com.example.shiftbuddy.Shared.UniversalVariables;

import java.util.ArrayList;

import static android.app.backup.BackupManager.dataChanged;

/**
 * Created by jorda_000 on 2/3/2016.
 * A database table l for the Shifts table
 */
public class Shifts extends TableItem {

    //Contains entries of this table's l class
    public static ArrayList<Shift> shiftList = new ArrayList<Shift>();

    // region Table Column Name Structure
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_PUNCHINDT = "punchInDT";
    private static final String COLUMN_PUNCHOUTDT = "punchOutDT";
    private static final String COLUMN_TOTALTMINUTES = "totalHours";
    private static final String COLUMN_BREAKTIME = "breakTime";
    private static final String COLUMN_TOTAL_PAY = "totalPay";
    private static final String COLUMN_TIPS = "tips";
    private static final String COLUMN_SALES = "sales";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_PAY_PER_HOUR = "payPerHour";
    private static final String COLUMN_SALES_PERCENTAGE = "salesPercentage";
    private static final String COLUMN_WAGE__ENABLED = "wageEnabled";
    private static final String COLUMN_COMMISION__ENABLED = "commisionEnabled";


    //endregion

    //region Constructor

    public Shifts() {

        super("Shifts", "SHIFTS", new String[]{COLUMN_ID, COLUMN_PUNCHINDT, COLUMN_PUNCHOUTDT, COLUMN_TOTALTMINUTES, COLUMN_BREAKTIME, COLUMN_TOTAL_PAY, COLUMN_TIPS, COLUMN_SALES, COLUMN_NOTES, COLUMN_PAY_PER_HOUR, COLUMN_SALES_PERCENTAGE, COLUMN_WAGE__ENABLED, COLUMN_COMMISION__ENABLED},
                "CREATE TABLE Shifts ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_PUNCHINDT + " TEXT, "
                        + COLUMN_PUNCHOUTDT + " TEXT, "
                        + COLUMN_TOTALTMINUTES + " INTEGER, "
                        + COLUMN_BREAKTIME + " INTEGER, "
                        + COLUMN_TIPS + " DOUBLE, "
                        + COLUMN_SALES + " DOUBLE, "
                        + COLUMN_NOTES + " TEXT, "
                        + COLUMN_TOTAL_PAY + " DOUBLE, "
                        + COLUMN_PAY_PER_HOUR + " DOUBLE, "
                        + COLUMN_SALES_PERCENTAGE + " DOUBLE, "
                        + COLUMN_WAGE__ENABLED + " BOOLEAN, "
                        + COLUMN_COMMISION__ENABLED + " BOOLEAN);");
    }

    //endregion

    //Creates a new Shift entry in the Shifts table
    public void CreateShift(String tableName, Shift shift) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_PUNCHINDT, shift.punchInDT);
        values.put(COLUMN_PUNCHOUTDT, shift.punchOutDT);
        values.put(COLUMN_TOTALTMINUTES, shift.totalMinutes);
        values.put(COLUMN_BREAKTIME, shift.breakTime);
        values.put(COLUMN_TOTAL_PAY, shift.totalPay);
        values.put(COLUMN_TIPS, shift.tips);
        values.put(COLUMN_SALES, shift.sales);
        values.put(COLUMN_NOTES, shift.notes);
        values.put(COLUMN_TOTAL_PAY, shift.totalPay);
        values.put(COLUMN_PAY_PER_HOUR, shift.payPerHour);
        values.put(COLUMN_SALES_PERCENTAGE, shift.salesPercentage);
        values.put(COLUMN_WAGE__ENABLED, shift.wageEnabled);
        values.put(COLUMN_COMMISION__ENABLED, shift.commisionEnabled);

        DBConnector.database.insert(tableName, null, values);

        DBConnector.requestBackup();

        //Refresh the shift list
        GetRecords(tableName);

    }

    //Updates/Ends a Shift according to the shift's Id
    public void UpdateOrEndShift(String tableName, Shift shift) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_PUNCHOUTDT, shift.punchInDT);
        values.put(COLUMN_PUNCHOUTDT, shift.punchOutDT);
        values.put(COLUMN_TOTALTMINUTES, shift.totalMinutes);
        values.put(COLUMN_TOTAL_PAY, shift.totalPay);
        values.put(COLUMN_TIPS, shift.tips);
        values.put(COLUMN_SALES, shift.sales);
        values.put(COLUMN_NOTES, shift.notes);
        values.put(COLUMN_PAY_PER_HOUR, shift.payPerHour);
        values.put(COLUMN_SALES_PERCENTAGE, shift.salesPercentage);

        DBConnector.database.update(tableName, values, "Id = " + shift.Id, null);

        DBConnector.requestBackup();

        //Refresh the shift list
        GetRecords(tableName);

    }

    public void GetRecords(String tableName) {

        Cursor cursor = DBConnector.database.query(tableName, columnNameStructure,
                null, null, null, null, null);

        Log.i(logTag, "Returned " + cursor.getCount() + " rows");

        //Update the shift list with records acquired from the DB
        shiftList = CursorToList(cursor);

    }

    public ArrayList<Shift> CursorToList(Cursor cursor) {

        ArrayList<Shift> tableItems = new ArrayList<Shift>();

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                Shift shift = new Shift();
                shift.Id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                shift.punchInDT = cursor.getString(cursor.getColumnIndex(COLUMN_PUNCHINDT));
                shift.punchOutDT = cursor.getString(cursor.getColumnIndex(COLUMN_PUNCHOUTDT));
                shift.totalMinutes = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTALTMINUTES));
                shift.breakTime = cursor.getInt(cursor.getColumnIndex(COLUMN_BREAKTIME));
                shift.totalPay = cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_PAY));
                shift.tips = cursor.getDouble(cursor.getColumnIndex(COLUMN_TIPS));
                shift.sales = cursor.getDouble(cursor.getColumnIndex(COLUMN_SALES));
                shift.notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
                shift.payPerHour = cursor.getDouble(cursor.getColumnIndex(COLUMN_PAY_PER_HOUR));
                shift.salesPercentage = cursor.getDouble(cursor.getColumnIndex(COLUMN_SALES_PERCENTAGE));
                shift.wageEnabled = cursor.getInt(cursor.getColumnIndex(COLUMN_WAGE__ENABLED)) > 0;
                shift.commisionEnabled = cursor.getInt(cursor.getColumnIndex(COLUMN_COMMISION__ENABLED)) > 0;

                tableItems.add(shift);

            }

        }

        return tableItems;

    }

    //Retrieves the last shift with a punch out date of none
    public static int getLastOpenShift() {

        int index = -1;

        for (int i = 0; i < Shifts.shiftList.size(); i++) {
            if (Shifts.shiftList.get(i).punchOutDT.equals(Shift.PUNCHOUT_NONE))
                index = i;
        }

        return index;

    }

}


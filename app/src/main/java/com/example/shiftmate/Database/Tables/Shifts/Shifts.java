package com.example.shiftmate.Database.Tables.Shifts;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.shiftmate.Database.Connector.DBConnector;
import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.TableItem;
import com.example.shiftmate.Shared.UniversalFunctions;
import com.example.shiftmate.Shared.UniversalVariables;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jorda_000 on 2/3/2016.
 * A database table model for the Shifts table
 */
public class Shifts extends TableItem {

    //Contains entries of this table's model class
    public static ArrayList<Shift> shiftList = new ArrayList<Shift>();

    // region Table Column Name Structure
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_PUNCHINDT = "punchInDT";
    private static final String COLUMN_PUNCHOUTDT = "punchOutDT";
    private static final String COLUMN_TOTALTMINUTES = "totalHours";
    private static final String COLUMN_BREAKTIME = "breakTime";
    private static final String COLUMN_PAYPERHOUR = "payPerHour";
    private static final String COLUMN_TIPS = "tips";
    private static final String COLUMN_SALES = "sales";
    private static final String COLUMN_NOTES = "notes";


    //endregion

    //region Constructor

    public Shifts() {

        super("Shifts", "SHIFTS", new String[]{COLUMN_ID, COLUMN_PUNCHINDT, COLUMN_PUNCHOUTDT, COLUMN_TOTALTMINUTES, COLUMN_BREAKTIME, COLUMN_PAYPERHOUR, COLUMN_TIPS, COLUMN_SALES, COLUMN_NOTES},
                "CREATE TABLE Shifts ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_PUNCHINDT + " TEXT, "
                        + COLUMN_PUNCHOUTDT + " TEXT, "
                        + COLUMN_TOTALTMINUTES + " INTEGER, "
                        + COLUMN_BREAKTIME + " INTEGER, "
                        + COLUMN_TIPS + " INTEGER, "
                        + COLUMN_SALES + " INTEGER, "
                        + COLUMN_NOTES + " TEXT, "
                        + COLUMN_PAYPERHOUR + " INTEGER);");

    }

    //endregion

    //Creates a new Shift entry in the Shifts table
    public void CreateShift(String tableName, Shift shift) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_PUNCHINDT, shift.punchInDT);
        values.put(COLUMN_PUNCHOUTDT, shift.punchOutDT);
        values.put(COLUMN_TOTALTMINUTES, shift.totalMinutes);
        values.put(COLUMN_BREAKTIME, shift.breakTime);
        values.put(COLUMN_PAYPERHOUR, shift.payPerHour);
        values.put(COLUMN_TIPS, shift.tips);
        values.put(COLUMN_SALES, shift.sales);
        values.put(COLUMN_NOTES, shift.notes);

        DBConnector.database.insert(tableName, null, values);

        //Refresh the shift list
        GetRecords(tableName);

    }

    //Ends a quick shift by updating the Punchout datetime of the supplied Shift Id, as well as tips, sales, notes, etc
    public void EndShift(String tableName, Shift shift) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_PUNCHOUTDT, UniversalFunctions.dateToString(UniversalVariables.dateFormatDateTimeString, DateTime.now(), null));
        values.put(COLUMN_TOTALTMINUTES, shift.totalMinutes);
        values.put(COLUMN_TIPS, shift.tips);
        values.put(COLUMN_SALES, shift.sales);
        values.put(COLUMN_NOTES, shift.notes);

        DBConnector.database.update(tableName, values, "Id = " + shift.Id, null);

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
                shift.payPerHour = cursor.getInt(cursor.getColumnIndex(COLUMN_PAYPERHOUR));
                shift.tips = cursor.getInt(cursor.getColumnIndex(COLUMN_TIPS));
                shift.sales = cursor.getInt(cursor.getColumnIndex(COLUMN_SALES));
                shift.notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));

                tableItems.add(shift);

            }

        }

        return tableItems;

    }


    public void DeleteTableEntry(String tableName, long Id) {

    }

    //endregion

}


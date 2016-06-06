package com.example.shiftmate.Database.Tables;

import android.database.Cursor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by jorda_000 on 2/3/2016.
 * A super class for every database table to implement from
 */
public class TableItem {

    //Shared properties for children
    public static String tableName;
    public static String logTag;
    public static String createQuery;
    public static String[] columnNameStructure;//Contains the column structure of the table(for pulling data from the db according to this models column name structure)


    public TableItem(String tableName, String logTag, String[] columnNameStructure, String createQuery) {

        this.tableName = tableName;
        this.logTag = logTag;
        this.columnNameStructure = columnNameStructure;
        this.createQuery = createQuery;

    }

}





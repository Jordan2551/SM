package com.example.shiftmate.Shared;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;

/**
 * Created by jorda_000 on 6/3/2016.
 *
 * A class which contains variables for universal use, such as date formats
 *
 */
public class UniversalVariables {

    //The datetime format for all dates in this program
    public static DateTimeFormatter dateFormatDateTime = DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm a");
    public static SimpleDateFormat dateFormatDateTime2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");//DDOS FIX THE 2 THING
    public static SimpleDateFormat dateFormatDate = new SimpleDateFormat("EEE, d MMM yyyy");
    public static SimpleDateFormat dateFormatTime = new SimpleDateFormat("hh:mm a");

}

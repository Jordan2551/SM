package com.shiftbuddy.app.Shared;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by jorda_000 on 6/3/2016.
 *
 * A class which contains variables for universal use, such as date formats
 *
 */
public class UniversalVariables {

    //The datetime format for all dates in this program. Used for converting strings to dates
    public static final DateTimeFormatter dateFormatDateTime = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm a");
    public static final DateTimeFormatter dateFormatDateTimeMilitary = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");
    public static final DateTimeFormatter dateFormatDate = DateTimeFormat.forPattern("MM/dd/yyyy");
    public static final DateTimeFormatter dateFormatDateTimeDisplay = DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm a");
    public static final DateTimeFormatter dateFormatTime = DateTimeFormat.forPattern("hh:mm a");
    public static final DateTimeFormatter dateFormatTimeMilitary = DateTimeFormat.forPattern("HH:mm");


    //The datetime format for all the dates in this program. Used for converting dates to strings
    public static final String dateFormatDateTimeString = "MM/dd/yyyy hh:mm a";
    public static final String dateFormatDateTimeMilitaryString = "MM/dd/yyyy HH:mm";
    public static final String dateFormatDateString = "MM/dd/yyyy";
    public static final String dateFormatTimeDisplayString = "hh:mm a";
    public static final String dateFormatTimeMilitaryString = "HH:mm";

    public static final String dateFormatDateTimeDisplayString = "EEE, d MMM yyyy hh:mm a";
    public static final String dateFormatDateDisplayString = "EEE, d MMM yyyy";

}

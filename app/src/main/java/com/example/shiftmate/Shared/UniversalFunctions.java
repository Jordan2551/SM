package com.example.shiftmate.Shared;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jorda_000 on 6/3/2016.
 *
 * A class which holds functions used by several other classes
 */
public class UniversalFunctions {


    //Gets the total hours and minutes given an integer (representing minutes)
    //ARGUMENTS:
    //int minutes: the total number of minutes for the time period
    //RETURNS:
    //int[2] containing the total hours and minutes in that respective index order
    public static int[] getHoursAndMinutes(int minutes)
    {
        int totalHours = minutes / 60;
        int totalMinutes = (int) Math.round(((minutes / 60.0) - (minutes / 60)) * 60);
        return new int[]{totalHours, totalMinutes};
    }
//DDOS WRITE ABOUT THESE ABOUT CALENDAR FOR STRING TO STRING
    public static String dateToString(String format, DateTime dateJoda, Date dateJava){

        if(dateJoda != null)
            return dateJoda.toString(format);

        else
            return new SimpleDateFormat(format).format(dateJava);
    }

    public static DateTime stringToDate(DateTimeFormatter formatter, String stringToConvert){
        return DateTime.parse(stringToConvert, formatter);
    }

    public static String changeDateStringFormat(DateTimeFormatter formatterDTF, String formatterStr, String stringToChange){
        return stringToDate(formatterDTF, stringToChange).toString(formatterStr);
    }


}

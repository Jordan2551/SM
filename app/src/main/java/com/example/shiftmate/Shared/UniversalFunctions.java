package com.example.shiftmate.Shared;

import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.Shifts.Shift;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jorda_000 on 6/3/2016.
 * <p>
 * A class which holds functions used by several other classes
 */
public class UniversalFunctions {


    //Gets the total hours and minutes given an integer (representing minutes)
    //ARGUMENTS:
    //int minutes: the total number of minutes for the time period
    //RETURNS:
    //int[2] containing the total hours and minutes in that respective index order
    public static int[] getHoursAndMinutes(int minutes) {
        int totalHours = minutes / 60;
        int totalMinutes = (int) Math.round(((minutes / 60.0) - (minutes / 60)) * 60);
        return new int[]{totalHours, totalMinutes};
    }

    /**
     * Calculates the total hours and minutes of all of the Shift objects in the shiftList
     *
     * @param shiftList the list of shifts to acquire the total duration from
     * @return The total duration of all the shifts
     */
    public static int[] getAllShiftDurations(ArrayList<Shift> shiftList) {

        int totalShiftMinutes = 0;
        int[] totalHoursAndMinutes = new int[]{0,0};//Holds the values from the getHoursAndMinutes function

        for (Shift shift: shiftList) {
                totalHoursAndMinutes = getHoursAndMinutes(shift.totalMinutes);
                totalShiftMinutes += totalHoursAndMinutes[0] * 60 + totalHoursAndMinutes[1];
        }

        return getHoursAndMinutes(totalShiftMinutes);

    }

    /**
     * Adds all shift's total pay from the supplied Shift list
     *
     * @param shiftList the list of shifts to acquire the total pay from
     * @return The total pay for all the shifts
     */
    public static double getAllShiftsTotalPay(ArrayList<Shift> shiftList){

        double totalPay = 0;

        for (Shift shift: shiftList) {
            totalPay += shift.totalPay;
        }

        return totalPay;

    }

    //DDOS WRITE ABOUT THESE ABOUT CALENDAR FOR STRING TO STRING
    public static String dateToString(String format, DateTime dateJoda, Date dateJava) {

        if (dateJoda != null)
            return dateJoda.toString(format);

        else
            return new SimpleDateFormat(format).format(dateJava);
    }

    public static DateTime stringToDateTime(DateTimeFormatter formatter, String stringToConvert) {
        return DateTime.parse(stringToConvert, formatter);
    }

    public static String changeDateStringFormat(DateTimeFormatter formatterDTF, String formatterStr, String stringToChange) {
        return stringToDateTime(formatterDTF, stringToChange).toString(formatterStr);
    }

    //Converts a java date object to the corresponding JodaTime DateTime object
    public static DateTime dateToDateTime(String formatter, DateTimeFormatter formatterDTF,  Date javaDate){
        return DateTime.parse(dateToString(formatter, null, javaDate), formatterDTF);
    }

    //Converts a JodaTime DateTime object to the corresponding Java Date object
    public static Date dateTimeToDate(String formatter, DateTime jodaDate) throws ParseException {
        return new SimpleDateFormat(formatter).parse(dateToString(formatter, jodaDate, null));
    }

    /**
     * Returns a range of two dates as a string according to if they have more of a day difference.
     * Example: 6/10/2016 06:23AM -> 6/10/2016 09:23PM is on the same day (6/10/2016) so we would return: 6/10/2016 06:23AM -> 09:23PM
     * However, if the days are different like 6/10/2016 06:23AM -> 6/11/2016 09:23PM then we would return: 6/10/2016 06:23AM -> 6/11/2016 09:23PM
     *
     * @param shiftBeginDateString
     * @param shiftEndDateString
     * @return A string composing an easy to read shift date range
     */
    public static String getShiftDateRangeString(String shiftBeginDateString, String shiftEndDateString) {

        DateTime shiftBeginDate = stringToDateTime(UniversalVariables.dateFormatDateTime, shiftBeginDateString);
        DateTime shiftEndDate = stringToDateTime(UniversalVariables.dateFormatDateTime, shiftEndDateString);

        if (shiftBeginDate.dayOfMonth().get() == shiftEndDate.dayOfMonth().get())
            return changeDateStringFormat(UniversalVariables.dateFormatDateTime, UniversalVariables.dateFormatDateDisplayString, shiftBeginDateString) + System.getProperty("line.separator") +
                    changeDateStringFormat(UniversalVariables.dateFormatDateTime, UniversalVariables.dateFormatTimeString, shiftBeginDateString) + " - " +
                    changeDateStringFormat(UniversalVariables.dateFormatDateTime, UniversalVariables.dateFormatTimeString,shiftEndDateString);

        else
            return changeDateStringFormat(UniversalVariables.dateFormatDateTime, UniversalVariables.dateFormatDateTimeDisplayString, shiftBeginDateString) + System.getProperty("line.separator") +
                    changeDateStringFormat(UniversalVariables.dateFormatDateTime, UniversalVariables.dateFormatDateTimeDisplayString, shiftEndDateString);


    }


    /**
     * Checks if a date is within a specific date range (the specified date target is inclusive to the date range!)
     *
     * @param startDate The start date for the date range
     * @param endDate The end date for the date range
     * @param targetDate The target date to be checked in the date range
     * @return An indication if the target date is equal to or within the end points of the date range
     */
    public static boolean isDateInRangeInclusive(DateTime startDate, DateTime endDate, DateTime targetDate){

        //Check if the target is not before the start date which means: the target date is equal or larger than the start date
        // AND check if the start date is not after the end date which means: the target date is equal or smaller than the end date
        return !targetDate.toLocalDate().isBefore(startDate.toLocalDate()) && !targetDate.toLocalDate().isAfter(endDate.toLocalDate());
    }

}

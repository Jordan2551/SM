package com.shiftbuddy.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shiftbuddy.app.Database.DataSource;
import com.shiftbuddy.app.Database.Tables.Shifts.Shift;
import com.shiftbuddy.app.Shared.UniversalFunctions;
import com.shiftbuddy.app.Shared.UniversalVariables;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;

/**
 * Created by jorda_000 on 2/16/2016.
 */
public class ShiftListAdapter extends ArrayAdapter<Shift> {


    public ShiftListAdapter(Context context, int resource, List<Shift> listItems) {
        super(context, resource, listItems);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        //Run a null check on the supplied view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_shift, parent, false);
        }

        //Get the requested data from the shift list
        Shift requestedItem = DataSource.shifts.shiftList.get(position);

        //Acquire references to visual components of the view
        TextView shiftStartTV = (TextView) convertView.findViewById(R.id.shiftStartTV);
        TextView shiftEndTV = (TextView) convertView.findViewById(R.id.shiftEndTV);
        TextView breakTimeTV = (TextView) convertView.findViewById(R.id.breakTimeTV);
        TextView totalHoursTV = (TextView) convertView.findViewById(R.id.totalHoursText);
        TextView totalPaidHoursTV = (TextView) convertView.findViewById(R.id.totalPayHoursTV);
        ImageView shiftStatusIV = (ImageView) convertView.findViewById(R.id.shiftStatusIV);


        //Supply the components with data
        shiftStartTV.setText(requestedItem.punchInDT);

        if (requestedItem.punchOutDT.equals(Shift.PUNCHOUT_NONE)) {

            shiftEndTV.setText("Shift in progress");
            totalHoursTV.setText("N/A");

        } else {

            shiftEndTV.setText(requestedItem.punchOutDT);

            try {

                DateTime punchInDT = DateTime.parse(requestedItem.punchInDT, UniversalVariables.dateFormatDateTime);
                DateTime punchOutDT = DateTime.parse(requestedItem.punchOutDT, UniversalVariables.dateFormatDateTime);
                Period datePeriod = new Period(punchInDT, punchOutDT);

                //Get the hour difference between two dates. We convert the days, weeks and months to hours as well!
                int hourDifference = datePeriod.getHours() + datePeriod.getDays() * 24 + ((datePeriod.getWeeks() * 7) * 24) + (((datePeriod.getMonths() * 4) * 7) * 24) + ((((datePeriod.getYears() * 12) * 4) * 7) * 24);

                //Get the minute difference between two dates
                int minuteDifference = datePeriod.getMinutes();

                //Automatically delete dummy shifts (shifts that have an hour and minute difference of 0)
                if(hourDifference == 0 && minuteDifference == 0)
                    DataSource.DeleteRecord(DataSource.shifts.tableName, requestedItem.Id);

                else {

                    //Get the total paid hours and minutes by passing in the total time in minutes and subtracting the break time from that total time
                    int[] totalPaidHrsAndMins = UniversalFunctions.getHoursAndMinutes((hourDifference * 60 + minuteDifference) - requestedItem.breakTime);

                    totalHoursTV.setText(String.valueOf(hourDifference + ":" + minuteDifference));
                    totalPaidHoursTV.setText(String.valueOf(totalPaidHrsAndMins[0] + ":" + totalPaidHrsAndMins[1]));

                    if(requestedItem.breakTime == 0)
                        breakTimeTV.setText("No break");

                    else
                        breakTimeTV.setText(Integer.toString(requestedItem.breakTime));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return convertView;
    }


}

package com.example.shiftmate.ViewShifts;

import android.app.DatePickerDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.Shifts.Shift;
import com.example.shiftmate.R;
import com.example.shiftmate.Shared.UniversalFunctions;
import com.example.shiftmate.Shared.UniversalVariables;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.Calendar;


import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;


public class ViewShiftsNEW extends AppCompatActivity {

    //Shift Range Spinner option consts
    private static final byte SPINNER_OPTION_ALL_SHIFTS = 0;
    private static final byte SPINNER_OPTION_SHIFTS_BY_DATE = 1;
    private static final byte SPINNER_OPTION_SHIFTS_BY_WEEK = 2;
    private static final byte SPINNER_OPTION_SHIFTS_BY_YEAR = 3;

    private static final byte COLUMN_COUNT = 7;

    //A list containing Shifts which have been selected according to a filter.
    //For example: a date filter from 7/15/2016 - 7/22/2016 will make this list contain
    //Only shifts from within that date range
    private ArrayList<Shift> filteredShiftsList = new ArrayList<Shift>();

    //These strings hold the shift begin and end dates in a MM/dd/yyyy format
    //These strings get initialized with the current date by default
    String shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, DateTime.now(), null);
    String shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, DateTime.now().plusDays(1), null);

    private Calendar myCalendar = Calendar.getInstance();

    private Button prevIntervalBtn;
    private Button nxtIntervalBtn;

    private TextView fromText;
    private TextView toText;
    private TextView shiftFilterDateFromText;
    private TextView shiftFilterDateToText;

    private TextView totalHoursTextView;
    private SortableTableView tableView;
    private Spinner shiftRangeSpinner;

    DatePickerDialog.OnDateSetListener shiftFilterDateFromSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            //Convert the returned date from the calendar object to DateTime. This is because java Date sucks balls
            DateTime selectedDate = UniversalFunctions.dateToDateTime(UniversalVariables.dateFormatDateString, UniversalVariables.dateFormatDate, myCalendar.getTime());

            if (shiftRangeSpinner.getSelectedItemPosition() == SPINNER_OPTION_SHIFTS_BY_WEEK) {

                //If the date filteration by week was selected, then set the from date to the sunday of that same selected dates' week.
                //Note: we set the date minus 7 days because JodaTime has monday as it's first day of the week, and setting sunday without - 7 days will simply return the sunday of the next week.
                selectedDate = selectedDate.withDayOfWeek(DateTimeConstants.SUNDAY).minusDays(7);

                //Set to date automatically when the filteration by week option is selected
                //Set the to date to saturday (the last day of the week)
                shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, selectedDate.plusDays(6), null);
                shiftFilterDateToText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

            }

            shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, selectedDate, null);
            shiftFilterDateFromText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

            setFilteredShifts(shiftRangeSpinner.getSelectedItemPosition());
            tableView.setDataAdapter(new ShiftTableAdapter(getBaseContext(), filteredShiftsList));

        }

    };

    DatePickerDialog.OnDateSetListener shiftFilterDateToSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            //Convert the returned date from the calendar object to DateTime. This is because java Date sucks balls
            DateTime selectedDate = UniversalFunctions.dateToDateTime(UniversalVariables.dateFormatDateString, UniversalVariables.dateFormatDate, myCalendar.getTime());

            shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, selectedDate, null);
            shiftFilterDateToText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

            setFilteredShifts(shiftRangeSpinner.getSelectedItemPosition());
            tableView.setDataAdapter(new ShiftTableAdapter(getBaseContext(), filteredShiftsList));

        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shifts_new);

        fromText = (TextView) findViewById(R.id.fromText);
        toText = (TextView) findViewById(R.id.toText);

        prevIntervalBtn = (Button) findViewById(R.id.prevIntervalBtn);
        nxtIntervalBtn = (Button) findViewById(R.id.nxtIntervalBtn);

        prevIntervalBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Set the new date week range interval to the week before the selected week in the shiftFilterDateFrom
                DateTime previousWeekStart =  UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom).minusDays(7);

                shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, previousWeekStart, null);
                shiftFilterDateFromText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

                shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, previousWeekStart.plusDays(6), null);
                shiftFilterDateToText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                //Refresh the filtered shifts
                setFilteredShifts(shiftRangeSpinner.getSelectedItemPosition());
                tableView.setDataAdapter(new ShiftTableAdapter(getBaseContext(), filteredShiftsList));

            }
        });

        nxtIntervalBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Set the new date week range interval to the week after the selected week in the shiftFilterDateFrom
                DateTime nextWeekStart =  UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom).plusDays(7);

                shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, nextWeekStart, null);
                shiftFilterDateFromText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

                shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, nextWeekStart.plusDays(6), null);
                shiftFilterDateToText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                //Refresh the filtered shifts
                setFilteredShifts(shiftRangeSpinner.getSelectedItemPosition());
                tableView.setDataAdapter(new ShiftTableAdapter(getBaseContext(), filteredShiftsList));

            }
        });

        shiftFilterDateFromText = (TextView) findViewById(R.id.shiftsFilterFromText);
        shiftFilterDateFromText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

        shiftFilterDateFromText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(ViewShiftsNEW.this, shiftFilterDateFromSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        shiftFilterDateToText = (TextView) findViewById(R.id.shiftsFilterToText);
        shiftFilterDateToText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

        shiftFilterDateToText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(ViewShiftsNEW.this, shiftFilterDateToSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        totalHoursTextView = (TextView) findViewById(R.id.totalHoursTextView);

        tableView = (SortableTableView) findViewById(R.id.tableView);

        tableView.setColumnWeight(0, 4);
        tableView.setColumnWeight(1, 2);
        tableView.setColumnWeight(2, 2);
        tableView.setColumnWeight(5, 2);
        tableView.setColumnWeight(6, 2);

        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, new String[]{"Date", "Duration", "Break", "Pay", "Tips", "Sales", "Notes"}));

        tableView.setColumnComparator(0, new ViewShiftsComparators().getDateComparator());
        tableView.setColumnComparator(1, new ViewShiftsComparators().getShiftDurationComparator());
        tableView.setColumnComparator(2, new ViewShiftsComparators().getBreakComparator());
        tableView.setColumnComparator(3, new ViewShiftsComparators().getPayComparator());
        tableView.setColumnComparator(4, new ViewShiftsComparators().getTipsComparator());
        tableView.setColumnComparator(5, new ViewShiftsComparators().getSalesComparator());

        int colorEvenRows = ContextCompat.getColor(this, R.color.LIGHT_GREY);
        int colorOddRows = ContextCompat.getColor(this, R.color.SILVER);

        tableView.setDataRowColorizer(TableDataRowColorizers.alternatingRows(colorEvenRows, colorOddRows));

        shiftRangeSpinner = (Spinner) findViewById(R.id.shiftRangeSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.view_shifts_spinner_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        shiftRangeSpinner.setAdapter(adapter);

        //Select the first item by default (show all shifts in the database)
        shiftRangeSpinner.setSelection(0);

        //Handle the selection of different indexes on the spinner
        shiftRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(position){

                    case SPINNER_OPTION_SHIFTS_BY_WEEK:

                        //When the filter by week option is selected, we want to set the current week by default
                        DateTime firstDayOfWeek = DateTime.now().withDayOfWeek(DateTimeConstants.SUNDAY).minusDays(7);
                        shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, firstDayOfWeek, null);
                        shiftFilterDateFromText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));
                        shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, firstDayOfWeek.plusDays(6), null);
                        shiftFilterDateToText.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                        break;

                }

                setFilteredShifts(shiftRangeSpinner.getSelectedItemPosition());
                tableView.setDataAdapter(new ShiftTableAdapter(getBaseContext(), filteredShiftsList));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * Fills the filteredShiftList with all of the Shift objects which match the specified filteration method
     *
     * @param filterBy The filteration method, which indicates how the filteredShiftList will get filtered
     */
    private void setFilteredShifts(int filterBy) {

        //Reset the filteredShiftList
        filteredShiftsList = new ArrayList<Shift>();

        updateGUI(filterBy);

        switch (filterBy) {

            case SPINNER_OPTION_ALL_SHIFTS:

                for (Shift shift : DataSource.shifts.shiftList) {
                    if (!shift.punchOutDT.equals(Shift.PUNCHOUT_NONE))//Ignore shifts with no punch out datetime
                        filteredShiftsList.add(shift);
                }

                break;

            case SPINNER_OPTION_SHIFTS_BY_DATE:
            case SPINNER_OPTION_SHIFTS_BY_WEEK:

                //Only allow a date range where the before date is before the after date
                if (UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom).isBefore(UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateTo))
                        || (UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom).isEqual(UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateTo)))) {

                    //An interval object so we can track which shift start dates fall within the specified date range the user defines
                    Interval dateInterval = new Interval(UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom), UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateTo));

                    for (Shift shift : DataSource.shifts.shiftList) {

                        if (!shift.punchOutDT.equals(Shift.PUNCHOUT_NONE))

                            try {

                                DateTime shiftStart = UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDateTime, shift.punchInDT);

                                if (UniversalFunctions.isDateInRangeInclusive
                                        (UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom),
                                                UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateTo),
                                                UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDateTime, shift.punchInDT)))
                                    filteredShiftsList.add(shift);


                            } catch (Exception e) {

                            }
                    }

                } else
                    Toast.makeText(this, R.string.Shift_Begin_Before_End, Toast.LENGTH_SHORT).show();


                break;

        }

        //After we acquire the filtered shift list above, we send it over to calculate the total duration
        //In hours for all of the shifts to display for totalHoursTextView
        int[] totalShiftsHourDuration = UniversalFunctions.getAllShiftDurations(filteredShiftsList);

        totalHoursTextView.setText(totalShiftsHourDuration[0] + ":" + String.format("%02d", totalShiftsHourDuration[1]));

    }

    //Show/hides the necessary components according to the spinner option selected
    private void updateGUI(int spinnerSelection){


        switch(spinnerSelection){

            case SPINNER_OPTION_ALL_SHIFTS:

                fromText.setVisibility(View.GONE);
                toText.setVisibility(View.GONE);
                shiftFilterDateFromText.setVisibility(View.GONE);
                shiftFilterDateToText.setVisibility(View.GONE);
                prevIntervalBtn.setVisibility(View.GONE);
                nxtIntervalBtn.setVisibility(View.GONE);

                break;

            case SPINNER_OPTION_SHIFTS_BY_DATE:

                fromText.setVisibility(View.VISIBLE);
                toText.setVisibility(View.VISIBLE);
                shiftFilterDateFromText.setVisibility(View.VISIBLE);
                shiftFilterDateToText.setVisibility(View.VISIBLE);
                prevIntervalBtn.setVisibility(View.GONE);
                nxtIntervalBtn.setVisibility(View.GONE);
                shiftFilterDateToText.setEnabled(true);

                break;

            case SPINNER_OPTION_SHIFTS_BY_WEEK:

                fromText.setVisibility(View.VISIBLE);
                toText.setVisibility(View.VISIBLE);
                shiftFilterDateFromText.setVisibility(View.VISIBLE);
                shiftFilterDateToText.setVisibility(View.VISIBLE);
                prevIntervalBtn.setVisibility(View.VISIBLE);
                nxtIntervalBtn.setVisibility(View.VISIBLE);
                shiftFilterDateToText.setEnabled(false);

                break;

        }

    }


}

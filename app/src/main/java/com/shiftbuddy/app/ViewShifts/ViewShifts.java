package com.shiftbuddy.app.ViewShifts;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shiftbuddy.app.Database.DataSource;
import com.shiftbuddy.app.Database.Tables.Shifts.Shift;
import com.shiftbuddy.app.NewShiftActivity;
import com.shiftbuddy.app.R;
import com.shiftbuddy.app.Settings;
import com.shiftbuddy.app.Shared.Animator;
import com.shiftbuddy.app.Shared.UniversalFunctions;
import com.shiftbuddy.app.Shared.UniversalVariables;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;


import java.util.ArrayList;
import java.util.Calendar;


import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;


public class ViewShifts extends AppCompatActivity {

    //Shift Range Spinner option consts
    private final byte SPINNER_OPTION_ALL_SHIFTS = 0;
    private final byte SPINNER_OPTION_SHIFTS_BY_DATE = 1;
    private final byte SPINNER_OPTION_SHIFTS_BY_WEEK = 2;
    private final byte SPINNER_OPTION_SHIFTS_BY_MONTH = 3;
    private final byte SPINNER_OPTION_SHIFTS_BY_YEAR = 4;

    private static final byte COLUMN_COUNT = 7;

    static final int UPDATE_SHIFT_REQUEST_CODE = 1; // Request code for startActivityForResult for NewShift callback

    //A list containing Shifts which have been selected according to a filter.
    //For example: a date filter from 7/15/2016 - 7/22/2016 will make this list contain
    //Only shifts from within that date range
    private ArrayList<Shift> filteredShiftsList = new ArrayList<Shift>();

    //These strings hold the shift begin and end dates in a MM/dd/yyyy format
    //These strings get initialized with the current date by default
    String shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, DateTime.now(), null);
    String shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, DateTime.now().plusDays(1), null);

    private Calendar myCalendar = Calendar.getInstance();

    private ImageButton prevIntervalBtn;
    private ImageButton nxtIntervalBtn;
    private ImageButton totalPayMoreInfoButton;
    private ImageButton helpButton;

    private LinearLayout dateFromLL;
    private LinearLayout dateToLL;
    private LinearLayout totalPayMoreInfoLL;

    private TextView paycheckText;
    private TextView totalHoursText;
    private TextView basePayText;
    private TextView totalTipsText;

    private Button shiftsFilterFromButton;
    private Button shiftsFilterToButton;

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

            switch (shiftRangeSpinner.getSelectedItemPosition()) {

                case SPINNER_OPTION_SHIFTS_BY_WEEK:

                    //If the date filteration by week was selected, then set the from date to the sunday of that same selected dates' week.
                    //Note: we set the date minus 7 days because JodaTime has monday as it's first day of the week, and setting sunday without - 7 days will simply return the sunday of the next week.
                    selectedDate = selectedDate.withDayOfWeek(DateTimeConstants.SUNDAY).minusDays(7);

                    //Set to date automatically when the filteration by week option is selected
                    //Set the to date to saturday (the last day of the week)
                    shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, selectedDate.plusDays(6), null);
                    shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                    break;

                case SPINNER_OPTION_SHIFTS_BY_MONTH:

                    //If the date filteration by week was selected, then set the from date to the sunday of that same selected dates' week.
                    //Note: we set the date minus 7 days because JodaTime has monday as it's first day of the week, and setting sunday without - 7 days will simply return the sunday of the next week.
                    selectedDate = selectedDate.withDayOfMonth(1);

                    //Set to date automatically when the filteration by week option is selected
                    //Set the to date to saturday (the last day of the week)
                    shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, selectedDate.plusMonths(1).minusDays(1), null);
                    shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                    break;

                case SPINNER_OPTION_SHIFTS_BY_YEAR:

                    //If the date filteration by year was selected, then set the from date to the first day of the selected year
                    selectedDate = selectedDate.withDate(selectedDate.getYear(), 1, DateTimeConstants.JANUARY);

                    //Set to date automatically when the filteration by year option is selected
                    //Set the to date to saturday (the last day of the week)
                    shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, selectedDate.plusYears(1).minusDays(1), null);
                    shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                    break;

            }

            shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, selectedDate, null);
            shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

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
            shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

            setFilteredShifts(shiftRangeSpinner.getSelectedItemPosition());
            tableView.setDataAdapter(new ShiftTableAdapter(getBaseContext(), filteredShiftsList));

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shifts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("View Shifts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateFromLL = (LinearLayout) findViewById(R.id.dateFromLL);
        dateToLL = (LinearLayout) findViewById(R.id.dateToLL);
        totalPayMoreInfoLL = (LinearLayout) findViewById(R.id.totalPayMoreInfoLL);

        totalPayMoreInfoLL.setVisibility(View.GONE);

        paycheckText = (TextView) findViewById(R.id.paycheckText);

        prevIntervalBtn = (ImageButton) findViewById(R.id.prevIntervalBtn);
        nxtIntervalBtn = (ImageButton) findViewById(R.id.nxtIntervalBtn);
        totalPayMoreInfoButton = (ImageButton) findViewById(R.id.totalPayMoreInfoButton);
        helpButton = (ImageButton) findViewById(R.id.helpButton);

        prevIntervalBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DateTime previousDateStart = UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom);

                switch (shiftRangeSpinner.getSelectedItemPosition()) {

                    case SPINNER_OPTION_SHIFTS_BY_WEEK:

                        //Set date from to previous week (sunday-satruday of previous week)
                        shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, previousDateStart.minusDays(7), null);
                        shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

                        shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, previousDateStart.minusDays(1), null);
                        shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                        break;

                    case SPINNER_OPTION_SHIFTS_BY_MONTH:

                        //Set date from to previous week (sunday-satruday of previous week)
                        shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, previousDateStart.minusMonths(1), null);
                        shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

                        shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, previousDateStart.minusDays(1), null);
                        shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                        break;

                    case SPINNER_OPTION_SHIFTS_BY_YEAR:

                        //Set date from to previous year
                        shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, previousDateStart.minusYears(1), null);
                        shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

                        shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, previousDateStart.minusDays(1), null);
                        shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                        break;

                }

                //Refresh the filtered shifts
                setFilteredShifts(shiftRangeSpinner.getSelectedItemPosition());
                tableView.setDataAdapter(new ShiftTableAdapter(getBaseContext(), filteredShiftsList));

            }

        });

        nxtIntervalBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //builder.show();

                //Set the new date week range interval to the week after the selected week in the shiftFilterDateFrom
                DateTime nextDateStart = UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom);

                switch (shiftRangeSpinner.getSelectedItemPosition()) {

                    case SPINNER_OPTION_SHIFTS_BY_WEEK:

                        //Set date from to next week (sunday-satruday of next week)
                        shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, nextDateStart.plusDays(7), null);
                        shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

                        shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, nextDateStart.plusDays(13), null);
                        shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                        break;

                    case SPINNER_OPTION_SHIFTS_BY_MONTH:

                        //Set date from to next week (sunday-satruday of next week)
                        shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, nextDateStart.plusMonths(1), null);
                        shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

                        shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, nextDateStart.plusMonths(2).minusDays(1), null);
                        shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                        break;

                    case SPINNER_OPTION_SHIFTS_BY_YEAR:

                        //Set date from to next year
                        shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, nextDateStart.plusYears(1), null);
                        shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

                        shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, nextDateStart.plusYears(2).minusDays(1), null);
                        shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));


                        break;

                }

                //Refresh the filtered shifts
                setFilteredShifts(shiftRangeSpinner.getSelectedItemPosition());
                tableView.setDataAdapter(new ShiftTableAdapter(getBaseContext(), filteredShiftsList));

            }
        });

        totalPayMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalPayMoreInfoLL.getVisibility() == View.GONE) {
                    totalPayMoreInfoButton.setBackgroundResource(R.drawable.ic_expand_less);
                    totalPayMoreInfoLL.setVisibility(View.VISIBLE);
                    Animator.fade(totalPayMoreInfoLL, true);

                } else {
                    totalPayMoreInfoButton.setBackgroundResource(R.drawable.ic_expand_more);
                    Animator.fade(totalPayMoreInfoLL, false);
                    totalPayMoreInfoLL.setVisibility(View.GONE);
                }
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ViewShifts.this)
                        .setTitle("Help")
                        .setIcon(R.drawable.ic_help)
                        .setMessage(getText(R.string.view_shifts_help))
                        .show();
            }
        });

        shiftsFilterFromButton = (Button) findViewById(R.id.shiftsFilterFromButton);
        shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));

        shiftsFilterFromButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(ViewShifts.this, shiftFilterDateFromSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        shiftsFilterToButton = (Button) findViewById(R.id.shiftsFilterToButton);
        shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

        shiftsFilterToButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(ViewShifts.this, shiftFilterDateToSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        totalHoursText = (TextView) findViewById(R.id.totalHoursText);
        basePayText = (TextView) findViewById(R.id.basePayText);
        totalTipsText = (TextView) findViewById(R.id.totalTipsText);

        tableView = (SortableTableView) findViewById(R.id.tableView);

        tableView.addDataClickListener(new ShiftClickListener());

        tableView.setColumnWeight(0, 0);
        tableView.setColumnWeight(1, 4);
        tableView.setColumnWeight(2, 2);
        tableView.setColumnWeight(3, 2);
        tableView.setColumnWeight(4, 2);
        tableView.setColumnWeight(5, 2);
        tableView.setColumnWeight(6, 2);
        tableView.setColumnWeight(7, 2);
        tableView.setColumnWeight(8, 3);

        //Note: the Id colum is there for getting the specified shift's Id for functionality like deleting or updating the selected shift from the table
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, new String[]{"Id", "Date", "Duration", "Break", "Total", "Wage", "Sales", "Tips", "Notes"}));

        tableView.setColumnComparator(1, new ViewShiftsComparators().getDateComparator());
        tableView.setColumnComparator(2, new ViewShiftsComparators().getShiftDurationComparator());
        tableView.setColumnComparator(3, new ViewShiftsComparators().getBreakComparator());
        tableView.setColumnComparator(4, new ViewShiftsComparators().gettotalPayComparator());
        tableView.setColumnComparator(5, new ViewShiftsComparators().gettotalPayPerHourComparator());
        tableView.setColumnComparator(6, new ViewShiftsComparators().getSalesComparator());
        tableView.setColumnComparator(7, new ViewShiftsComparators().getTipsComparator());

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

                switch (position) {

                    case SPINNER_OPTION_SHIFTS_BY_WEEK:

                        //When the filter by week option is selected, we want to set the current week by default
                        DateTime firstDayOfWeek = DateTime.now().withDayOfWeek(DateTimeConstants.SUNDAY).minusDays(7);
                        shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, firstDayOfWeek, null);
                        shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));
                        shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, firstDayOfWeek.plusDays(6), null);
                        shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                        break;

                    case SPINNER_OPTION_SHIFTS_BY_MONTH:

                        //When the filter by week option is selected, we want to set the current week by default
                        DateTime firstDayOfMonth = DateTime.now().withDayOfMonth(1);
                        shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, firstDayOfMonth, null);
                        shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));
                        shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, firstDayOfMonth.plusMonths(1).minusDays(1), null);
                        shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

                        break;

                    case SPINNER_OPTION_SHIFTS_BY_YEAR:

                        DateTime firstDayOfYear = DateTime.now().withDate(DateTime.now().getYear(), 1, DateTimeConstants.JANUARY);
                        shiftFilterDateFrom = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, firstDayOfYear, null);
                        shiftsFilterFromButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateFrom));
                        shiftFilterDateTo = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateString, firstDayOfYear.plusYears(1).minusDays(1), null);
                        shiftsFilterToButton.setText(UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDate, UniversalVariables.dateFormatDateDisplayString, shiftFilterDateTo));

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

    //A custom table row click handler
    private class ShiftClickListener implements TableDataClickListener<Shift> {
        @Override
        //Overrides the onDataClicked event to obtain the data of the shift that was clicked on the table
        public void onDataClicked(int rowIndex, final Shift clickedShift) {

            //Show the user a dialog of options regarding the clicked shift
            new AlertDialog.Builder(ViewShifts.this)
                    .setTitle("Choose Option")
                    .setIcon(R.drawable.ic_options)
                    .setItems(new String[]{"Update Shift", "Delete", "Cancel"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {

                            switch (position) {
                                case 0:

                                    //Transition to the NewShiftActivity with the data for the selected Shift to update
                                    Intent intent = new Intent(ViewShifts.this, NewShiftActivity.class);

                                    intent.putExtra("updateShiftRequest", true);
                                    intent.putExtra("shiftEndIndex", DataSource.shifts.shiftList.indexOf(clickedShift));
                                    intent.putExtra("punchInDT", clickedShift.punchInDT);
                                    intent.putExtra("punchOutDT", clickedShift.punchOutDT);
                                    intent.putExtra("totalPay", clickedShift.totalPay);
                                    intent.putExtra("breakTime", clickedShift.breakTime);
                                    intent.putExtra("notes", clickedShift.notes);
                                    intent.putExtra("tips", clickedShift.tips);
                                    intent.putExtra("sales", clickedShift.sales);

                                    startActivityForResult(intent, UPDATE_SHIFT_REQUEST_CODE);

                                    break;

                                case 1:

                                    //Add a second confirmation dialog for deletion
                                    new AlertDialog.Builder(ViewShifts.this)
                                            .setTitle("Delete this shift?")
                                            .setPositiveButton("Cancel", null)
                                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    //The user clicked the delete button, so delete the Shift, and reset the table view's data to reflect the deleted shift
                                                    DataSource.DeleteRecord(DataSource.shifts.tableName, clickedShift.Id);
                                                    DataSource.shifts.GetRecords(DataSource.shifts.tableName);
                                                    setFilteredShifts(shiftRangeSpinner.getSelectedItemPosition());
                                                    tableView.setDataAdapter(new ShiftTableAdapter(getBaseContext(), filteredShiftsList));

                                                }
                                            }).show();

                                    break;
                            }
                        }
                    })
                    .setIcon(android.R.drawable.sym_def_app_icon)
                    .show();

        }

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
            case SPINNER_OPTION_SHIFTS_BY_MONTH:
            case SPINNER_OPTION_SHIFTS_BY_YEAR:

                //Only allow a date range where the before date is before the after date
                if (UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom).isBefore(UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateTo))
                        || (UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom).isEqual(UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateTo)))) {

                    //An interval object so we can track which shift start dates fall within the specified date range the user defines
                    Interval dateInterval = new Interval(UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom), UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateTo));

                    for (Shift shift : DataSource.shifts.shiftList) {

                        if (!shift.punchOutDT.equals(Shift.PUNCHOUT_NONE))

                            try {

                                DateTime shiftStart = UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDateTimeMilitary, shift.punchInDT);

                                if (UniversalFunctions.isDateInRangeInclusive
                                        (UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateFrom),
                                                UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDate, shiftFilterDateTo),
                                                UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDateTimeMilitary, shift.punchInDT)))
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

        paycheckText.setText(DataSource.currencies.currencyList.get(Settings.getCurrencyListSelectedIndex()).currencySymbol + UniversalFunctions.getAllShiftsTotalPay(filteredShiftsList, UniversalFunctions.GETALLSHIFTSTOTALPAY_TOTAL_PAY));
        basePayText.setText(DataSource.currencies.currencyList.get(Settings.getCurrencyListSelectedIndex()).currencySymbol + UniversalFunctions.getAllShiftsTotalPay(filteredShiftsList, UniversalFunctions.GETALLSHIFTSTOTALPAY_BASE_PAY));
        totalTipsText.setText(DataSource.currencies.currencyList.get(Settings.getCurrencyListSelectedIndex()).currencySymbol + UniversalFunctions.getAllShiftsTotalPay(filteredShiftsList, UniversalFunctions.GETALLSHIFTSTOTALPAY_TIPS_ONLY));
        totalHoursText.setText(totalShiftsHourDuration[0] + ":" + String.format("%02d", totalShiftsHourDuration[1]));

    }

    //Show/hides the necessary components according to the spinner option selected
    private void updateGUI(int spinnerSelection) {


        switch (spinnerSelection) {

            case SPINNER_OPTION_ALL_SHIFTS:

                Animator.fade(dateFromLL, false);
                Animator.fade(dateToLL, false);
                Animator.fade(prevIntervalBtn, false);
                Animator.fade(nxtIntervalBtn, false);
                dateFromLL.setVisibility(View.GONE);
                dateToLL.setVisibility(View.GONE);
                prevIntervalBtn.setVisibility(View.GONE);
                nxtIntervalBtn.setVisibility(View.GONE);

                break;

            case SPINNER_OPTION_SHIFTS_BY_DATE:

                dateFromLL.setVisibility(View.VISIBLE);
                dateToLL.setVisibility(View.VISIBLE);
                Animator.fade(dateFromLL, true);
                Animator.fade(dateToLL, true);
                Animator.fade(prevIntervalBtn, false);
                Animator.fade(nxtIntervalBtn, false);
                prevIntervalBtn.setVisibility(View.GONE);
                nxtIntervalBtn.setVisibility(View.GONE);
                shiftsFilterToButton.setEnabled(true);

                break;

            case SPINNER_OPTION_SHIFTS_BY_WEEK:
            case SPINNER_OPTION_SHIFTS_BY_MONTH:
            case SPINNER_OPTION_SHIFTS_BY_YEAR:

                dateFromLL.setVisibility(View.VISIBLE);
                dateToLL.setVisibility(View.VISIBLE);
                prevIntervalBtn.setVisibility(View.VISIBLE);
                nxtIntervalBtn.setVisibility(View.VISIBLE);
                Animator.fade(dateFromLL, true);
                Animator.fade(dateToLL, true);
                Animator.fade(prevIntervalBtn, true);
                Animator.fade(nxtIntervalBtn, true);
                shiftsFilterToButton.setEnabled(false);

                break;

        }

    }

    //Gets a response from NetShiftActivity regarding the updating of a shift
    // Once the NewShiftActivity finishes closing a quick shift we reset the tableView
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == UPDATE_SHIFT_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                setFilteredShifts(shiftRangeSpinner.getSelectedItemPosition());
                tableView.setDataAdapter(new ShiftTableAdapter(getBaseContext(), filteredShiftsList));
            }
        }
    }


}

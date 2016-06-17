package com.example.shiftmate.ViewShifts;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.Shifts.Shift;
import com.example.shiftmate.Database.Tables.Shifts.Shifts;
import com.example.shiftmate.R;
import com.example.shiftmate.Shared.UniversalFunctions;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    private TextView totalHoursTextView;
    private SortableTableView tableView;
    private Spinner shiftRangeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shifts_new);

        totalHoursTextView = (TextView) findViewById(R.id.totalHoursTextView);

        tableView = (SortableTableView) findViewById(R.id.tableView);

        tableView.setColumnWeight(0, 3);
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

        //Select the first item by default
        shiftRangeSpinner.setSelection(0);

        //MAYBE THE MODDED DAT SHOULD BE IN THE ADAPTER
        shiftRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setFilteredShifts(position);
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

        switch (filterBy) {

            case SPINNER_OPTION_ALL_SHIFTS:

                for (Shift shift : DataSource.shifts.shiftList) {
                    if (!shift.punchOutDT.equals(Shift.PUNCHOUT_NONE))//Ignore shifts with no punch out datetime
                        filteredShiftsList.add(shift);
                }

                break;

            case SPINNER_OPTION_SHIFTS_BY_DATE:

                //An interval object so we can track which shift start dates fall within the specified date range the user defines
                Interval dateInterval = null;

                for (Shift shift : DataSource.shifts.shiftList) {

                    if (!shift.punchOutDT.equals(Shift.PUNCHOUT_NONE))

                        dateInterval = new Interval(DateTime.parse(shift.punchInDT), DateTime.parse(shift.punchOutDT));

                    //if the shift start date falls within the DDOS#2 ADD SPECIFIED DATE RANGE then add that shit to the filteredShiftList
                    if (dateInterval.contains(DateTime.parse(shift.punchInDT)))
                        filteredShiftsList.add(shift);

                }

        break;

        }

        //After we acquire the filtered shift list above, we send it over to calculate the total duration
        //In hours for all of the shifts to display for totalHoursTextView
        int[] totalShiftsHourDuration = UniversalFunctions.getAllShiftDurations(filteredShiftsList);

        totalHoursTextView.setText(totalShiftsHourDuration[0] + ":" + String.format("%02d", totalShiftsHourDuration[1]));

}


}

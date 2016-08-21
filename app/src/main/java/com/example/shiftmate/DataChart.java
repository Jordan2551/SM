package com.example.shiftmate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.Shifts.Shift;
import com.example.shiftmate.Shared.UniversalFunctions;
import com.example.shiftmate.Shared.UniversalVariables;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Months;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class DataChart extends Activity {

    //region Spinner Option Consts

    private final byte SPINNER_OPTION_SHIFT_DURATIONS = 0;
    private final byte SPINNER_OPTION_TOTAL_PAY = 1;
    private final byte SPINNER_OPTION_TOTAL_PAY_WAGE = 2;
    private final byte SPINNER_OPTION_TOTAL_SALES = 3;
    private final byte SPINNER_OPTION_TOTAL_TIPS = 4;

    private final byte SPINNER_OPTION_ALL_MONTHS = 0;
    private final byte SPINNER_OPTION_JANUARY = 1;
    private final byte SPINNER_OPTION_FEBRUARY = 2;
    private final byte SPINNER_OPTION_MARCH = 3;
    private final byte SPINNER_OPTION_APRIL = 4;
    private final byte SPINNER_OPTION_MAY = 5;
    private final byte SPINNER_OPTION_JUNE = 6;
    private final byte SPINNER_OPTION_JULY = 7;
    private final byte SPINNER_OPTION_AUGUST = 8;
    private final byte SPINNER_OPTION_SEPTEMBER = 9;
    private final byte SPINNER_OPTION_OCTOBER = 10;
    private final byte SPINNER_OPTION_NOVEMBER = 11;
    private final byte SPINNER_OPTION_DECEMBER = 12;

    //endregion

    private BarChart barChart;

    Spinner dataTypeSpinner;
    Spinner yearSpinner;
    Spinner monthSpinner;

    Button generateGraphButton;

    //Contains all of the shifts that will be relevant to the graph according to what the user has selected to be displayed on the graph
    private ArrayList<Shift> filteredShiftsList = new ArrayList<Shift>();

    //Contains the entires for the BarChart
    List<BarEntry> entries = new ArrayList<>();

    //The data set containing the BarChart entries
    BarDataSet barDataSet;

    //Contains all of the strings that appear on the labels for the bar chart data
    ArrayList<String> dataLabels = new ArrayList<String>();

    //Contains the barDataSet
    BarData barData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_chart);

        barChart = (BarChart) findViewById(R.id.barChart);
        barChart.setPinchZoom(true);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

        dataTypeSpinner = (Spinner) findViewById(R.id.dataTypeSpinner);
        yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.data_chart_data_spinner_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataTypeSpinner.setAdapter(adapter);

        ArrayList<String> yearList = new ArrayList<>();

        for (int i = 2010; i <= DateTime.now().year().get(); i++)
            yearList.add(String.valueOf(i));

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, yearList);
        yearSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.data_chart_month_spinner_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);

        generateGraphButton = (Button) findViewById(R.id.generateGraphButton);

        generateGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilteredShifts(SPINNER_OPTION_TOTAL_PAY, 2016, SPINNER_OPTION_ALL_MONTHS);
            }
        });

    }

    /**
     * Fills the filteredShiftList with all of the Shift objects which match the specified filteration method
     *
     * @param filterData The filteration method, which indicates how the filteredShiftList will get filtered
     */
    private void setFilteredShifts(int filterData, int filterYear, int filterMonth) {

        //Reset the filteredShiftList, bar chart data entries and labels
        filteredShiftsList = new ArrayList<Shift>();
        entries = new ArrayList<>();
        dataLabels = new ArrayList<String>();

        try {
            //The user has selected the yearly graph option
            if (filterMonth == SPINNER_OPTION_ALL_MONTHS) {

                //Because we are basing the graph on the 12 months, we add 12 instances of BarEntry to the entries list
                //So that when we loop and find relevant data in the shiftList, we can add that data to the relevant month
                //Also, we add the name of the month to the dataLabels array
                for (int i = 0; i < 12; i++) {
                    entries.add(new BarEntry(0, i));
                    dataLabels.add(new DateFormatSymbols().getMonths()[i]);
                }

                for (Shift shift : DataSource.shifts.shiftList) {

                    DateTime shiftDateTime = UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDateTime, shift.punchInDT);

                    //The iterated shifts' year matches the requested year filter
                    if (shiftDateTime.year().get() == filterYear) {

                        if (filterData == SPINNER_OPTION_SHIFT_DURATIONS)
                            entries.get(shiftDateTime.getMonthOfYear() - 1).setVal(entries.get(shiftDateTime.getMonthOfYear() - 1).getVal() + (shift.totalMinutes / 60));

                        else if (filterData == SPINNER_OPTION_TOTAL_PAY)
                            entries.get(shiftDateTime.getMonthOfYear() - 1).setVal((float) (entries.get(shiftDateTime.getMonthOfYear() - 1).getVal() + shift.totalPay));

                        else if (filterData == SPINNER_OPTION_TOTAL_PAY_WAGE)
                            entries.get(shiftDateTime.getMonthOfYear() - 1).setVal((float) (entries.get(shiftDateTime.getMonthOfYear() - 1).getVal() + (shift.payPerHour * (shift.totalMinutes / 60))));

                        else if (filterData == SPINNER_OPTION_TOTAL_SALES)
                            entries.get(shiftDateTime.getMonthOfYear() - 1).setVal((float) (entries.get(shiftDateTime.getMonthOfYear() - 1).getVal() + shift.sales));

                        else if (filterData == SPINNER_OPTION_TOTAL_TIPS)
                            entries.get(shiftDateTime.getMonthOfYear() - 1).setVal((float) (entries.get(shiftDateTime.getMonthOfYear() - 1).getVal() + shift.tips));

                    }
                }

                //Set the zoom of the chart so that the month labels are all visible to the user
                barChart.zoom(2, 2, 0, 0);

            }

            barDataSet = new BarDataSet(entries, "Dates");

            barData = new BarData(dataLabels, barDataSet);
            barChart.setData(barData);
            barChart.invalidate(); // refresh
        }

        catch(Exception e){
            String a = e.getMessage();
        }
    }

}

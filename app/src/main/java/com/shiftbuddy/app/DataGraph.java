package com.shiftbuddy.app;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.shiftbuddy.app.Database.DataPersister;
import com.shiftbuddy.app.Database.DataSource;
import com.shiftbuddy.app.Database.Tables.Shifts.Shift;
import com.shiftbuddy.app.Shared.UniversalFunctions;
import com.shiftbuddy.app.Shared.UniversalVariables;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class DataGraph extends DataPersister {

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
    ImageButton zoomInButton;
    ImageButton zoomOutButton;
    ImageButton helpButton;

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
        setContentView(R.layout.activity_data_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Shift Graphs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        yearSpinner.setSelection(yearSpinner.getCount() - 1);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.data_chart_month_spinner_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);

        generateGraphButton = (Button) findViewById(R.id.generateGraphButton);

        generateGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilteredShifts(dataTypeSpinner.getSelectedItemPosition(), Integer.valueOf(yearSpinner.getSelectedItem().toString()), monthSpinner.getSelectedItemPosition());
            }
        });

        zoomInButton = (ImageButton) findViewById(R.id.zoomInButton);
        zoomInButton.setOnClickListener(zoomInOut);

        zoomOutButton = (ImageButton) findViewById(R.id.zoomOutButton);
        zoomOutButton.setOnClickListener(zoomInOut);

        helpButton = (ImageButton) findViewById(R.id.helpButton);

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DataGraph.this)
                        .setTitle("Help")
                        .setIcon(R.drawable.ic_help)
                        .setMessage(getString(R.string.data_chart_help))
                        .show();
            }
        });
    }

    //A click listener for when the zoom in/out button is selected
    View.OnClickListener zoomInOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.hashCode() == zoomInButton.hashCode())
                barChart.zoomIn();
            else
                barChart.zoomOut();

        }
    };

    /**
     * Fills the filteredShiftList with all of the Shift objects which match the specified filteration method
     *
     * @param filterData The filteration method, which indicates how the filteredShiftList will get filtered
     */
    private void setFilteredShifts(int filterData, int filterYear, int filterMonth) {

        //Reset the fit of the graph
        barChart.fitScreen();

        //Reset the filteredShiftList, bar chart data entries and labels
        filteredShiftsList = new ArrayList<Shift>();
        entries = new ArrayList<>();
        dataLabels = new ArrayList<String>();

        boolean shiftFound = false;//Keeps track if at least a single qualified shift for the graph was found

        //The user has selected the yearly graph option
        if (filterMonth == SPINNER_OPTION_ALL_MONTHS) {

            //Because we are basing the graph on the 12 months, we add 12 instances of BarEntry to the entries list
            //So that when we loop and find relevant data in the shiftList, we can add that data to the relevant month
            for (int i = 0; i < 12; i++)
                entries.add(new BarEntry(0, i));

            //Get the values for the x axis labels for the 12 months
            dataLabels = getXAxisValues();

            for (Shift shift : DataSource.shifts.shiftList) {

                DateTime shiftDateTime = UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDateTimeMilitary, shift.punchInDT);

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

                    shiftFound = true;

                }
            }

            //Set the zoom of the chart so that the month labels are all visible to the user
            barChart.zoom(2.2f, 0, 0, 0);
            barDataSet = new BarDataSet(entries, dataTypeSpinner.getSelectedItem().toString() + " for " + yearSpinner.getSelectedItem().toString());

        }

        //The user has selected to view a graph for a specific month
        else {

            //This graph is based on the entire month. So get the amount of days in that month and add the number of entires accordingly
            for (int i = 0; i < new DateTime(filterYear, filterMonth, 1, 0, 0).getChronology().dayOfMonth().getMaximumValue(); i++) {
                entries.add(new BarEntry(0, i));
                dataLabels.add(String.valueOf(i + 1));
            }

            for (Shift shift : DataSource.shifts.shiftList) {

                DateTime shiftDateTime = UniversalFunctions.stringToDateTime(UniversalVariables.dateFormatDateTimeMilitary, shift.punchInDT);

                //The iterated shifts' year matches the requested year & month filters
                if (shiftDateTime.year().get() == filterYear && shiftDateTime.getMonthOfYear() == filterMonth) {

                    if (filterData == SPINNER_OPTION_SHIFT_DURATIONS)
                        entries.get(shiftDateTime.getDayOfMonth() - 1).setVal(entries.get(shiftDateTime.getDayOfMonth() - 1).getVal() + (shift.totalMinutes / 60));

                    else if (filterData == SPINNER_OPTION_TOTAL_PAY)
                        entries.get(shiftDateTime.getDayOfMonth() - 1).setVal((float) (entries.get(shiftDateTime.getDayOfMonth() - 1).getVal() + shift.totalPay));

                    else if (filterData == SPINNER_OPTION_TOTAL_PAY_WAGE)
                        entries.get(shiftDateTime.getDayOfMonth() - 1).setVal((float) (entries.get(shiftDateTime.getDayOfMonth() - 1).getVal() + (shift.payPerHour * (shift.totalMinutes / 60))));

                    else if (filterData == SPINNER_OPTION_TOTAL_SALES)
                        entries.get(shiftDateTime.getDayOfMonth() - 1).setVal((float) (entries.get(shiftDateTime.getDayOfMonth() - 1).getVal() + shift.sales));

                    else if (filterData == SPINNER_OPTION_TOTAL_TIPS)
                        entries.get(shiftDateTime.getDayOfMonth() - 1).setVal((float) (entries.get(shiftDateTime.getDayOfMonth() - 1).getVal() + shift.tips));

                    shiftFound = true;

                }
            }

            //Set the zoom of the chart so that the month labels are all visible to the user
            barChart.zoom(3.9f, 0, 0, 0);
            barDataSet = new BarDataSet(entries, dataTypeSpinner.getSelectedItem().toString() + " for " + monthSpinner.getSelectedItem().toString() + yearSpinner.getSelectedItem().toString());

        }

        //If not a single shift was found, we want to reset the entries and labels lists so that the graph will display the "no data found" string to the user
        if (!shiftFound)
            barChart.clear();

        else {

            barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            barData = new BarData(dataLabels, barDataSet);
            barChart.setData(barData);
            barChart.setDescription(getChartDescrition(filterData));
            barChart.animateXY(1200, 1200);
            barChart.invalidate(); // refresh

        }

    }

    //Gets the value of the x axis for months
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Jan");
        xAxis.add("Feb");
        xAxis.add("Mar");
        xAxis.add("Apr");
        xAxis.add("May");
        xAxis.add("Jun");
        xAxis.add("Jul");
        xAxis.add("Aug");
        xAxis.add("Sep");
        xAxis.add("Oct");
        xAxis.add("Nov");
        xAxis.add("Dec");
        return xAxis;
    }

    private String getChartDescrition(int filterData) {

        switch (filterData) {

            case SPINNER_OPTION_SHIFT_DURATIONS:
                return "Shift durations (hours)";
            case SPINNER_OPTION_TOTAL_PAY:
                return "Total pay in " + DataSource.currencies.currencyList.get(Settings.getCurrencyListSelectedIndex()).currencySymbol;
            case SPINNER_OPTION_TOTAL_PAY_WAGE:
                return "Total pay (wage) in " + DataSource.currencies.currencyList.get(Settings.getCurrencyListSelectedIndex()).currencySymbol;
            case SPINNER_OPTION_TOTAL_SALES:
                return "Total sales in " + DataSource.currencies.currencyList.get(Settings.getCurrencyListSelectedIndex()).currencySymbol;
            case SPINNER_OPTION_TOTAL_TIPS:
                return "Total tips in " + DataSource.currencies.currencyList.get(Settings.getCurrencyListSelectedIndex()).currencySymbol;

        }
        return "";
    }

}

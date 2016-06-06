package com.example.shiftmate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.shiftmate.Database.Connector.DBConnector;
import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.Shifts.Shift;
import com.example.shiftmate.Shared.UniversalFunctions;
import com.example.shiftmate.Shared.UniversalVariables;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//Indicates what data and Text Views should be updated according to the corresponding Text View which started the DatePicker/TimePicker dialog
enum UpdateRequest{

    UPDATE_BEGIN_DATE,
    UPDATE_END_DATE,
    UPDATE_BEGIN_TIME,
    UPDATE_END_TIME,

}

public class NewShiftActivity extends AppCompatActivity {

    //DDOS#1 see if you can find a more elegant way to implement the DatePickerDialog
    //Currently, we must have a DatePickerDialog.OnDateSetListener object for each
    //TextView that is associated with a DatePickerDialog. This is because the onDataSet callback function does not return the view that initially called
    //The DatePickerDialog, so we can't dynamically know for which date TextView to set the returned date for


    //region Variables & References

    Button ssButton;

    //These strings hold the shift begin and end dates in string format
    //These strings get initialized with the current date by default
    String shiftBeginDate = UniversalVariables.dateFormatDate.format(new Date());
    String shiftEndDate = UniversalVariables.dateFormatDate.format(new Date());

    //These strings hold the shift being and end times in string format
    //These strings get initialized with the current time by default
    String shiftBeginTime = UniversalVariables.dateFormatTime.format(new Date());
    String shiftEndTime = UniversalVariables.dateFormatTime.format(new Date());

    //A period object to determine the hour and minute difference between two dates
    Period datePeriod;
    int hourDifference, minuteDifference;

    int breakTime = Shift.NO_BREAK;

    //Holds the total paid hours and minutes according to the hourDifference and minuteDifference passed along with the break time (if applicable, in minutes)
    int[] totalPaidHrsAndMins;

    TextView breakTimeText;

    TextView shiftBeginDateText;
    TextView shiftEndDateText;

    TextView shiftBeginTimeText;
    TextView shiftEndTimeText;

    TextView totalHoursText;
    TextView totalPaidHoursText;

    Calendar myCalendar = Calendar.getInstance();

    //region shiftBeginDate

    //Initialize an event that gets called once a DatePickerDialog returns with the date data
    DatePickerDialog.OnDateSetListener shiftBeginDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateGUIAndData(UpdateRequest.UPDATE_BEGIN_DATE);

        }

    };

    //endregion

    //region shiftEndDate

    //DDOS#1
    //Initialize an event that gets called once a DatePickerDialog returns with the date data
    DatePickerDialog.OnDateSetListener shiftEndDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateGUIAndData(UpdateRequest.UPDATE_END_DATE);

        }

    };

    //endregion

    //region shiftBeginTime

    //Initialize an event that gets called once a DatePickerDialog returns with the date data
    TimePickerDialog.OnTimeSetListener shiftBeginTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
            updateGUIAndData(UpdateRequest.UPDATE_BEGIN_TIME);

        }

    };

    //endregion

    //region shiftEndTime

    //Initialize an event that gets called once a DatePickerDialog returns with the date data
    TimePickerDialog.OnTimeSetListener shiftEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
            updateGUIAndData(UpdateRequest.UPDATE_END_TIME);

        }

    };

    //endregion

    //region Update Functions

    //Updates the strings representing the dates and the corresponding TextViews with the selected dates from the DatePickerDialogs
    private void updateGUIAndData(UpdateRequest updateRequest){


        //Finally, set our TextView to the selected date by the DatePickerDialog

        //Update the requested data and the Text View for that data
        switch(updateRequest) {

            case UPDATE_BEGIN_DATE:
                shiftBeginDate = ((UniversalVariables.dateFormatDate.format(myCalendar.getTime())));
                shiftBeginDateText.setText(shiftBeginDate);
                break;

            case UPDATE_END_DATE:
                shiftEndDate = ((UniversalVariables.dateFormatDate.format(myCalendar.getTime())));
                shiftEndDateText.setText(shiftEndDate);
                break;

            case UPDATE_BEGIN_TIME:
                shiftBeginTime = ((UniversalVariables.dateFormatTime.format(myCalendar.getTime())));
                shiftBeginTimeText.setText(shiftBeginTime);
                break;

            case UPDATE_END_TIME:
                shiftEndTime = ((UniversalVariables.dateFormatTime.format(myCalendar.getTime())));
                shiftEndTimeText.setText(shiftEndTime);
                break;

        }

        //Update the period according to the data updated above
        updatePeriod();

        //Update Total Hours and Total Paid Hours
        updateTotalAndPaidHours();

    }

    //Updates the period and gets the hour and minute difference for the currently selected shift being and end dates
    private void updatePeriod(){

        datePeriod = new Period(DateTime.parse(shiftBeginDate + " " + shiftBeginTime, UniversalVariables.dateFormatDateTime),
                DateTime.parse(shiftEndDate + " " + shiftEndTime, UniversalVariables.dateFormatDateTime));

        //Get the hour difference between two dates. We convert the days, weeks and months to hours as well!
        hourDifference = datePeriod.getHours() + datePeriod.getDays() * 24 + ((datePeriod.getWeeks() * 7) * 24) + (((datePeriod.getMonths() * 4) * 7) * 24) + ((((datePeriod.getYears() * 12) * 4) * 7) * 24);

        //Get the minute difference between two dates
        minuteDifference = datePeriod.getMinutes();

    }

    private void updateTotalAndPaidHours(){


        try {

            //The shift begin date is before the shift end date
            if(UniversalVariables.dateFormatDate.parse(shiftBeginDate).before(UniversalVariables.dateFormatDate.parse(shiftEndDate)))
            {

                    //If the total shift time in minutes is larger than the break time then set the Total Hours and Paid Hours Text Views accordingly
                    if(hourDifference * 60 + minuteDifference > breakTime) {

                        totalHoursText.setText(hourDifference + ":" + minuteDifference);
                        totalPaidHrsAndMins = UniversalFunctions.getHoursAndMinutes((hourDifference * 60 + minuteDifference) - breakTime);
                        totalPaidHoursText.setText(totalPaidHrsAndMins[0] + ":" + totalPaidHrsAndMins[1]);

                    }

                    //Otherwise, the break time (in minutes) is larger than the total shift time (in minutes) then set the Total Hours and Paid Hours Text Views to 0
                    else
                    {
                        totalHoursText.setText("0");
                        totalPaidHoursText.setText("0");
                    }

                }

            //The shift being date is after the shift end date or the shift begin and end date are equal, which means we set the Total Hours and Paid Hours Text Views to 0
            else
            {
                totalHoursText.setText("0");
                totalPaidHoursText.setText("0");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ssButton = (Button) findViewById(R.id.ssButton);

        ssButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //If the start of the shift is before the end of the shift date
                    if(UniversalVariables.dateFormatDate.parse(shiftBeginDate).before(UniversalVariables.dateFormatDate.parse(shiftEndDate)) ||  UniversalVariables.dateFormatDate.parse(shiftBeginDate).equals(UniversalVariables.dateFormatDate.parse(shiftEndDate)))
                    {

                        Shift shift = new Shift();
                        shift.punchInDT = shiftBeginDate + " " + shiftBeginTime;
                        shift.punchOutDT = shiftEndDate + " " + shiftEndTime;
                        shift.payPerHour = 0;


                            //If the total time of the shift (in minutes) is bigger or equal to the break time then allow the user to save the shift
                            if(hourDifference * 60 + minuteDifference >= breakTime)
                                shift.breakTime = breakTime;

                            //Otherwise, the break time is greater than the shift time, which would yield a negative total paid time (doesn't make sense)
                            //Prompt the user with a toast and exit the function
                            else{
                                Toast.makeText(getApplicationContext(), "Break time has to be less than the total shift time.", Toast.LENGTH_LONG).show();
                                return;
                            }

                        DataSource.shifts.CreateShift(DataSource.shifts.tableName, shift);

                        Toast.makeText(getApplicationContext(), String.format("Your shift of %d:%d hours has been created!", hourDifference, minuteDifference), Toast.LENGTH_LONG).show();

                        //After the new shift was created and the confirmation toast was displayed, finish with this activity and go backk to the main activity
                        finish();

                    }

                    //Otherwise, prompt the user with a toast
                    else
                        Toast.makeText(getApplicationContext(), "Shift begin date must be before shift end date.", Toast.LENGTH_LONG).show();


                } catch (ParseException e) {
                    e.printStackTrace();
                }



            }
        });

        breakTimeText = (TextView) findViewById(R.id.breakTimeText);

        breakTimeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //Get the value for the break time. If there is no value then set to NO_BREAKK (0)
            @Override
            public void afterTextChanged(Editable s) {

                if(breakTimeText.length() > 0)
                     breakTime =  Integer.parseInt(breakTimeText.getText().toString());

                else
                    breakTime = Shift.NO_BREAK;

                //Update the total and paid hours data, because break time affects this data
                updateTotalAndPaidHours();

            }

        });

        shiftBeginDateText = (TextView) findViewById(R.id.shiftBeginDateText);
        shiftBeginDateText.setText(shiftBeginDate);

        shiftBeginDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Initialize a new instance of the DatePickerDialog class with the current date & display the DatePickerDialog itself
                //In the arguments we have:
                //The reference to the context class (this)
                //The over-ridden callback function defined in "date"
                //The object of type Calendar which we use to get the current year, month and day for the DatePickerDialog to be initialized with
                new DatePickerDialog(NewShiftActivity.this, shiftBeginDateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        shiftEndDateText = (TextView) findViewById(R.id.shiftEndDateText);
        shiftEndDateText.setText(shiftEndDate);

        //DDOS#1
        shiftEndDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Initialize a new instance of the DatePickerDialog class with the current date & display the DatePickerDialog itself
                //In the arguments we have:
                //The reference to the context class (this)
                //The over-ridden callback function defined in "date"
                //The object of type Calendar which we use to get the current year, month and day for the DatePickerDialog to be initialized with
                new DatePickerDialog(NewShiftActivity.this, shiftEndDateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        shiftBeginTimeText = (TextView) findViewById(R.id.shiftBeginTimeText);
        shiftBeginTimeText.setText(shiftBeginTime);

        shiftBeginTimeText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Initialize a new instance of the DatePickerDialog class with the current date & display the DatePickerDialog itself
                //In the arguments we have:
                //The reference to the context class (this)
                //The over-ridden callback function defined in "date"
                //The object of type Calendar which we use to get the current year, month and day for the DatePickerDialog to be initialized with
                new TimePickerDialog(NewShiftActivity.this, shiftBeginTimeSetListener, myCalendar
                        .get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE),
                        false).show();

            }
        });

        shiftEndTimeText = (TextView) findViewById(R.id.shiftEndTimeText);
        shiftEndTimeText.setText(shiftEndTime);

        //DDOS#1
        shiftEndTimeText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Initialize a new instance of the DatePickerDialog class with the current date & display the DatePickerDialog itself
                //In the arguments we have:
                //The reference to the context class (this)
                //The over-ridden callback function defined in "date"
                //The object of type Calendar which we use to get the current year, month and day for the DatePickerDialog to be initialized with
                new TimePickerDialog(NewShiftActivity.this, shiftEndTimeSetListener, myCalendar
                        .get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE),
                        false).show();

            }
        });

        totalHoursText = (TextView) findViewById(R.id.totalHoursText);
        totalHoursText.setText("");

        totalPaidHoursText = (TextView) findViewById(R.id.totalPaidHoursText);
        totalPaidHoursText.setText("");

    }



}

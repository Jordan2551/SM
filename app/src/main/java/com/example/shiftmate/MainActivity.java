package com.example.shiftmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.shiftmate.Database.Connector.DBConnector;
import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.Shifts.Shift;
import com.example.shiftmate.Database.Tables.Shifts.Shifts;
import com.example.shiftmate.Shared.UniversalFunctions;
import com.example.shiftmate.Shared.UniversalVariables;
import com.example.shiftmate.ViewShifts.ViewShiftsNEW;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static DBConnector dbConnector;

    static final int END_SHIFT_REQUEST_CODE = 1;  // Request code for startActivityForResult for NewShift callback

    //region Variables & References

    Button nsButton;
    Button qsButton;
    Button vsButton;
    Button drButton;
    Button testButton;
    TextView latestShiftTV;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        //Open the database connection
        dbConnector = new DBConnector(this);
        dbConnector.openConnection();

        //Acquire all shift records so we can use the shift list data globally, determine below if the latest shift has ended or not
        //And if we should or should not display the latestShiftTV
        DataSource.shifts.GetRecords(DataSource.shifts.tableName);

        latestShiftTV = (TextView) findViewById(R.id.latestShiftTV);

        //region New Shift Button

        nsButton = (Button) findViewById(R.id.nsButton);

        //Transfer the user to the NewShiftActivity
        nsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, NewShiftActivity.class);
                startActivity(intent);

            }
        });

        //endregion

        //region Test

        testButton = (Button) findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateGUI();

            }
        });

        //endreigon


        //region Quick Shift Button

        //This button opens a new quick shift (adds a new Shift entry into the DB) only if there is no
        //other open shift in the database already, otherwise, this button will offer to "end shift" which will
        //change the punchout time of that detected open shift which will close that open shift.
        qsButton = (Button) findViewById(R.id.qsButton);

        qsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int lastOpenShiftId = Shift.getLastOpenShift();

                //End the shift if there is at least one shift without a punch out date time (an open shift)
                if (lastOpenShiftId != -1) {

                    //Transition to the NewShiftActivity with the data for the punchInDT
                    Intent intent = new Intent(MainActivity.this, NewShiftActivity.class);
                    intent.putExtra("punchInDT", Shifts.shiftList.get(lastOpenShiftId).punchInDT);
                    intent.putExtra("shiftEndId", Shifts.shiftList.get(lastOpenShiftId).Id);
                    startActivityForResult(intent, END_SHIFT_REQUEST_CODE);

                }

                //Otherwise, we add a new quick shift
                else {

                    Shift shift = new Shift();
                    shift.punchInDT = UniversalFunctions.dateToString(UniversalVariables.dateFormatDateTimeString, DateTime.now(), null);
                    shift.punchOutDT = Shift.PUNCHOUT_NONE;
                    shift.breakTime = Shift.NO_BREAK;
                    shift.payPerHour = 40;

                    DataSource.shifts.CreateShift(DataSource.shifts.tableName, shift);

                    UpdateGUI();

                }

            }
        });


        //endregion

        //region View Shifts Button

        vsButton = (Button) findViewById(R.id.vsButton);

        //When the quick shift button is clicked we want to add a new database record that contains
        //an id and a start date and time(we leave the end date as null to indicate we have not finished a shift yet)
        vsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(MainActivity.this, ViewShiftsActivity.class);
                Intent intent = new Intent(MainActivity.this, ViewShiftsNEW.class);
                startActivity(intent);

            }
        });

        //endregion

        //region Delete Records Button

        drButton = (Button) findViewById(R.id.drButton);

        //When the quick shift button is clicked we want to add a new database record that contains
        //an id and a start date and time(we leave the end date as null to indicate we have not finished a shift yet)
        drButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataSource.DeleteAllRecords(DataSource.shifts.tableName);
                UpdateGUI();

            }
        });

        //endregion

        //Request to update the buttons displayed for the GUI based on the database's data
        UpdateGUI();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    //Updates the GUI's selectable buttons depending on the records of the database
    public void UpdateGUI() {

        //If there is at least 1 shift with a punchOutDT of PUNCHOUT_NONE (an open shift) then we display "End Shift" and some helpful GUI elements
        //Otherwise, there is no open shift so we just display "Quick Shift" to let the user open a new quick shift
        if (Shift.getLastOpenShift() != -1) {

            latestShiftTV.setText("Latest Quick Shift Started: " + UniversalFunctions.changeDateStringFormat(UniversalVariables.dateFormatDateTime, UniversalVariables.dateFormatDateTimeDisplayString, DataSource.shifts.shiftList.get(DataSource.shifts.shiftList.size() - 1).punchInDT));
            latestShiftTV.setVisibility(View.VISIBLE);
            qsButton.setText("End Shift");

        } else {

            latestShiftTV.setText("Select 'Quick Shift' to punch in!");
            qsButton.setText("Quick Shift");

        }

    }

    //Gets a response from NetShiftActivity regarding the ending of a quick shift
    //Once the NewShiftActivity finishes closing a quick shift we update the GUI
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == END_SHIFT_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                UpdateGUI();
            }
        }
    }

}

package com.example.shiftmate;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.Shifts.Shifts;

import org.w3c.dom.Text;

public class ViewShiftsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shifts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creates a new instance of our custom made arrayadapter for a shift item
        ShiftListAdapter adapter = new ShiftListAdapter(this, android.R.layout.simple_list_item_1, DataSource.shifts.shiftList);

        //Get reference to ListView so we can hook up the array adapter
        ListView shiftsLV = (ListView) findViewById(R.id.shiftsLV);
        shiftsLV.setAdapter(adapter);

    }

}

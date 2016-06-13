package com.example.shiftmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;

public class ViewShiftsNEW extends AppCompatActivity {


    private static final String[][] dataToShow = { { "This", "is", "a", "test" },
            { "and", "a", "second", "test" } };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shifts_new);

        TableView<String[]> tableView = (TableView<String[]>) findViewById(R.id.tableView);
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, dataToShow));

    }
}

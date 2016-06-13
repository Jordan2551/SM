package com.example.shiftmate.ViewShifts;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.Shifts.Shift;
import com.example.shiftmate.Database.Tables.Shifts.Shifts;
import com.example.shiftmate.R;

import java.util.Comparator;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;

public class ViewShiftsNEW extends AppCompatActivity {


    private static final byte COLUMN_COUNT = 6;
    private static String[][] tableData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shifts_new);

        tableData = new String[DataSource.shifts.shiftList.size()][COLUMN_COUNT];

        for (int i = 0; i < DataSource.shifts.shiftList.size(); i++) {

                tableData[i][0] = Shifts.shiftList.get(i).punchInDT + System.getProperty("line.separator") + DataSource.shifts.shiftList.get(i).punchOutDT;
                tableData[i][1] = Integer.toString(DataSource.shifts.shiftList.get(i).breakTime);
                tableData[i][2] = Integer.toString(DataSource.shifts.shiftList.get(i).payPerHour);
                tableData[i][3] = Integer.toString(DataSource.shifts.shiftList.get(i).tips);
                tableData[i][4] = Integer.toString(DataSource.shifts.shiftList.get(i).sales);
                tableData[i][5] = DataSource.shifts.shiftList.get(i).notes;

        }

        try {

            SortableTableView tableView = (SortableTableView) findViewById(R.id.tableView);

            tableView.setColumnWeight(0, 3);
            tableView.setColumnWeight(1, 2);
            tableView.setColumnWeight(5, 2);

            tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, new String[]{"Date", "Break", "Pay", "Tips", "Sales", "Notes"}));

            tableView.setColumnComparator(0, new ViewShiftsComparators().getDateComparator());
            tableView.setColumnComparator(1, new ViewShiftsComparators().getBreakComparator());
            tableView.setColumnComparator(2, new ViewShiftsComparators().getPayComparator());
            tableView.setColumnComparator(3, new ViewShiftsComparators().getTipsComparator());
            tableView.setColumnComparator(4, new ViewShiftsComparators().getSalesComparator());

            int colorEvenRows = ContextCompat.getColor(this, R.color.LIGHT_GREY);
            int colorOddRows = ContextCompat.getColor(this, R.color.SILVER);

            tableView.setDataRowColorizer(TableDataRowColorizers.alternatingRows(colorEvenRows, colorOddRows));
            tableView.setDataAdapter(new ShiftTableAdapter(this));

        } catch (Exception ex) {
            String s = ex.getMessage();
        }

    }



}

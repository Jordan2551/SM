package com.example.shiftmate.ViewShifts;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shiftmate.Database.DataSource;
import com.example.shiftmate.Database.Tables.Shifts.Shift;
import com.example.shiftmate.R;
import com.example.shiftmate.Shared.UniversalFunctions;
import com.example.shiftmate.Shared.UniversalVariables;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class ShiftTableAdapter extends TableDataAdapter<Shift> {

    private final byte TEXT_SIZE = 18;

    private int[] totalHoursAndMinutes = new int[]{0,0};//Holds the values from the getHoursAndMinutes function

    public ShiftTableAdapter(Context context, ArrayList<Shift> filteredShiftsList) {
        super(context, filteredShiftsList);

    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {

        Shift shift = getRowData(rowIndex);
        View renderedView = null;

        renderedView = renderView(columnIndex, shift);
        return renderedView;

    }

    //Renders the TextView according to the column index requested for the adapter, as well as for the row index requested(which represents the data index in our shiftList).
    //The TextView returned depends on the row and column indexes.
    //For example: column 0 row 0 will return the first index data according to the shift date property.
    private View renderView(int columnIndex, Shift shift) {

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView textViewToAdd = new TextView(getContext());
        textViewToAdd.setTextSize(TEXT_SIZE);
        textViewToAdd.setTextColor(ContextCompat.getColor(getContext(), R.color.BLUE_GREY));

        switch (columnIndex) {

            case 0:
                textViewToAdd.setMaxLines(0);
                textViewToAdd.setText(String.valueOf(shift.Id));
                textViewToAdd.setTextSize(0);
                break;
            case 1:
                textViewToAdd.setMaxLines(2);
                textViewToAdd.setText(UniversalFunctions.getShiftDateRangeString(shift.punchInDT, shift.punchOutDT));
                textViewToAdd.setTextSize(17);
                break;
            case 2:
                totalHoursAndMinutes = UniversalFunctions.getHoursAndMinutes(shift.totalMinutes);
                textViewToAdd.setText(totalHoursAndMinutes[0] + ":" + totalHoursAndMinutes[1]);
                textViewToAdd.setPadding(90, 0, 0, 0);
                break;
            case 3:
                textViewToAdd.setText(Integer.toString(shift.breakTime));
                textViewToAdd.setPadding(90, 0, 0, 0);
                break;
            case 4:
                textViewToAdd.setText(Integer.toString(shift.payPerHour));
                textViewToAdd.setPadding(90, 0, 0, 0);
                break;
            case 5:
                textViewToAdd.setText(Integer.toString(shift.tips));
                textViewToAdd.setPadding(90, 0, 0, 0);
                break;
            case 6:
                textViewToAdd.setText(Integer.toString(shift.sales));
                textViewToAdd.setPadding(90, 0, 0, 0);
                break;
            case 7:
                textViewToAdd.setText(shift.notes);
                textViewToAdd.setPadding(40, 0, 0, 0);
                break;

        }

        layout.addView(textViewToAdd);

        return layout;

    }

}

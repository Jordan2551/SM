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

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class ShiftTableAdapter extends TableDataAdapter<Shift> {

    private final byte TEXT_SIZE = 18;

    public ShiftTableAdapter(Context context) {
        super(context, DataSource.shifts.shiftList);

    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {

        Shift shift = getRowData(rowIndex);
        View renderedView = null;

        renderedView = renderView(rowIndex, columnIndex);
        return renderedView;

    }

    //Renders the TextView according to the column index requested for the adapter, as well as for the row index requested(which represents the data index in our shiftList).
    //The TextView returned depends on the row and column indexes.
    //For example: column 0 row 0 will return the first index data according to the shift date property.
    private View renderView(int rowIndex, int columnIndex){

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView textViewToAdd = new TextView(getContext());
        textViewToAdd.setTextSize(TEXT_SIZE);
        textViewToAdd.setTextColor(ContextCompat.getColor(getContext(), R.color.BLUE_GREY));

        switch (columnIndex) {

            case 0:
                textViewToAdd.setMaxLines(2);
                textViewToAdd.setText(DataSource.shifts.shiftList.get(rowIndex).punchInDT + System.getProperty("line.separator") + DataSource.shifts.shiftList.get(rowIndex).punchOutDT);
                break;
            case 1:
                textViewToAdd.setText(Integer.toString(DataSource.shifts.shiftList.get(rowIndex).breakTime));
                textViewToAdd.setPadding(90,0,0,0);
                break;
            case 2:
                textViewToAdd.setText(Integer.toString(DataSource.shifts.shiftList.get(rowIndex).payPerHour));
                textViewToAdd.setPadding(90,0,0,0);
                break;
            case 3:
                textViewToAdd.setText(Integer.toString(DataSource.shifts.shiftList.get(rowIndex).tips));
                textViewToAdd.setPadding(90,0,0,0);
                break;
            case 4:
                textViewToAdd.setText(Integer.toString(DataSource.shifts.shiftList.get(rowIndex).sales));
                textViewToAdd.setPadding(90,0,0,0);
                break;
            case 5:
                textViewToAdd.setText(DataSource.shifts.shiftList.get(rowIndex).notes);
                textViewToAdd.setPadding(40,0,0,0);
                break;

        }

        layout.addView(textViewToAdd);
        return layout;

    }

}

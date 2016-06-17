package com.example.shiftmate.Database.Tables.Shifts;

import com.example.shiftmate.Database.Connector.DBConnector;

/**
 * Created by jorda_000 on 2/6/2016.
 * A model representing a Shift
 */
public class Shift {

    //Indicates the value of a shift that has no punchout time yet(the shift has not ended)
    public static final String PUNCHOUT_NONE = "-1";
    public static final int NO_BREAK = 0;
    public static final int NO_TIPS = 0;
    public static final int NO_SALES = 0;

    public long Id;
    public String punchInDT;
    public String punchOutDT;
    public int totalMinutes;
    public int breakTime;
    public int payPerHour;
    public int tips;
    public int sales;
    public String notes;

    public static int getLastOpenShift() {

        int index = -1;

        for (int i = 0; i < Shifts.shiftList.size(); i++) {
            if (Shifts.shiftList.get(i).punchOutDT.equals(Shift.PUNCHOUT_NONE))
                index = i;
        }

        return index;

    }

}

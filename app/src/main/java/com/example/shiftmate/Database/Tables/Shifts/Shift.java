package com.example.shiftmate.Database.Tables.Shifts;

/**
 * Created by jorda_000 on 2/6/2016.
 */
public class Shift {

    //Indicates the value of a shift that has no punchout time yet(the shift has not ended)
    public static final String PUNCHOUT_NONE = "-1";
    public static final int NO_BREAK = 0;

    public long Id;
    public String punchInDT;
    public String punchOutDT;
    public int breakTime;
    public int payPerHour;

}

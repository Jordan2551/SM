package com.example.shiftmate.Database.Tables.Shifts;

import com.example.shiftmate.Database.Connector.DBConnector;

/**
 * Created by jorda_000 on 2/6/2016.
 * A model representing a Shift
 */
public class Shift {

    //Indicates the value of a shift that has no punchout time yet(the shift has not ended)
    public static final String PUNCHOUT_NONE = "-1";

    public long Id;
    public String punchInDT;
    public String punchOutDT;
    public int totalMinutes;
    public int breakTime;
    public Double totalPay;
    public Double tips;
    public Double sales;
    public String notes;
    public Double payPerHour;
    public Double salesPercentage;
    public Boolean wageEnabled;
    public Boolean commisionEnabled;

}

package com.example.shiftmate.ViewShifts;

import com.example.shiftmate.Database.Tables.Shifts.Shift;

import java.util.Comparator;

/**
 * Contains all of the comparators for the ViewShifts Activity so that the table can be sorted by the requested column
 * Created by jorda_000 on 6/12/2016.
 */
public class ViewShiftsComparators {


    public static Comparator getDateComparator() {
        return new dateComparator();
    }

    public static Comparator getBreakComparator() {
        return new breakComparator();
    }

    public static Comparator getPayComparator() {
        return new payComparator();
    }

    public static Comparator getTipsComparator() {
        return new tipsComparator();
    }

    public static Comparator getSalesComparator() {
        return new salesComparator();
    }


    private static class dateComparator implements Comparator<Shift> {
        @Override
        public int compare(Shift lhs, Shift rhs) {
            return lhs.punchInDT.compareTo(rhs.punchInDT);
        }
    }

    private static class breakComparator implements Comparator<Shift> {
        @Override
        public int compare(Shift lhs, Shift rhs) {
            if (lhs.breakTime > rhs.breakTime) return -1;
            else
                return 1;
        }
    }

    private static class payComparator implements Comparator<Shift> {
        @Override
        public int compare(Shift lhs, Shift rhs) {
            if (lhs.payPerHour > rhs.payPerHour) return -1;
            else
                return 1;
        }
    }

    private static class tipsComparator implements Comparator<Shift> {
        @Override
        public int compare(Shift lhs, Shift rhs) {
            if (lhs.tips > rhs.tips) return -1;
            else
                return 1;
        }
    }

    private static class salesComparator implements Comparator<Shift> {
        @Override
        public int compare(Shift lhs, Shift rhs) {
            if (lhs.sales > rhs.sales) return -1;
            else
                return 1;
        }
    }


}

package com.nxg.composeplane;

import android.os.Handler;

import org.junit.Test;


public class JavaUnitTest {

    private Handler handler;

    //先定义 常量
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    private enum WeekDays {
        SUNDAY(0), MONDAY(1), TUESDAY(2), WEDNESDAY(3),
        THURSDAY(4), FRIDAY(5), SATURDAY(6);
        private int value;

        WeekDays(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    WeekDays today = WeekDays.SUNDAY;

    public void setToday(WeekDays today) {
        this.today = today;
    }

    public WeekDays getToday() {
        return today;
    }

    @Test
    void testEnum() {
        switch (today) {
            case SUNDAY:
                break;
            case MONDAY:
                break;
            case TUESDAY:
                break;
            case WEDNESDAY:
                break;
            case THURSDAY:
                break;
            case FRIDAY:
                break;
            case SATURDAY:
                break;
            default:
                break;
        }
    }


    @Test
    void testIfNull() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Test
    void testIfNullInit() {
        if (handler != null) {
            handler = new Handler();
        }
    }

    @Test
    void testIfElse() {
        if (handler != null) {
            System.out.println("handler not null ");
        } else {
            System.out.println("handler null ");
        }
    }

    void testBuilder(){

    }
}

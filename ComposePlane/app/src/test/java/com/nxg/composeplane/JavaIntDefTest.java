package com.nxg.composeplane;

import androidx.annotation.IntDef;

import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class JavaIntDefTest {

    //先定义 常量
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    //定义＠WeekDays注解
    @IntDef({SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WeekDays {
    }

    //声明变量，限制变量的取值范围
    @WeekDays
    int currentDay = SUNDAY;

    //添加@WeekDays注解，限制传入值的范围
    public void setCurrentDay(@WeekDays int currentDay) {
        this.currentDay = currentDay;
    }

    //添加@WeekDays注解，限制返回值的范围
    @WeekDays
    public int getCurrentDay() {
        return currentDay;
    }

    @Test
    void testEnum() {
        // 声明变量，限制变量的取值范围
        @WeekDays int today = getCurrentDay();
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
        setCurrentDay(SUNDAY);
    }

}

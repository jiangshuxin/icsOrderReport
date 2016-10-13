package com.handpay.arch.stat.ics.support;

import java.util.Calendar;

public class HalfMonthSupport {
	public static String getDayRange(String queryDate) {
		int year = Integer.parseInt(queryDate.substring(0, 4));
		int month = Integer.parseInt(queryDate.substring(4, 6));
		int day = Integer.parseInt(queryDate.substring(6));
		if (day > 15) {
			return year + getMonthStr(month) + "01" + "-" + year + getMonthStr(month) + "15";
		} else if (month == 1) {
			return (year - 1) + "1216" + "-" + (year - 1) + "12" + getLastDay(year - 1, 12);
		} else {
			return year + getMonthStr(month - 1) + "16" + "-" + year + getMonthStr(month - 1) + getLastDay(year, month - 1);
		}
	}
	
	private static String getMonthStr(int month) {
		if (month > 10) {
			return String.valueOf(month);
		} else {
			return "0" + month;
		}
	}
	
	private static int getLastDay(int year, int month) {
		Calendar calendar = Calendar.getInstance(); 
		calendar.set(Calendar.YEAR, year);   
		calendar.set(Calendar.MONTH, month-1);
		return calendar.getActualMaximum(Calendar.DATE);
	}
}

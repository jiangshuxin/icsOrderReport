package com.handpay.arch.stat.ics.support;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.handpay.arch.stat.ics.domain.DateRange;
import com.handpay.arch.stat.ics.support.MetaData.MixType;

public class AppSupport {
    public static final SimpleDateFormat SDF = new SimpleDateFormat(Constants.DATE_PATTERN);
    
//	public static String TODAY = SDF.format(new Date());
	
	public static String getToday() {
		return SDF.format(new Date());
	}
	
	public static boolean isMall(int id) {
		return (MixType.MallSum.getId() == id) || (MixType.MallUndone.getId()  == id);
	}
	public static boolean isMall(MixType type) {
		return (MixType.MallSum.equals(type) || MixType.MallUndone.equals(type));
	}
	
	/**
	 * 根据时间构建6个月期间
	 * @param date
	 * @return
	 */
	public static DateRange buildSixMonthRange(String date) {
		DateRange range = new DateRange();
		range.setEndDate(date);
		range.setStartDate(beforeSixMonthDay(date));
		return range;
	}
	
	/**
	 * 根据时间构建最近半月区间
	 * @param date
	 * @return
	 */
	public static DateRange buildRecentHalfMonthRange(String date) {
		DateRange range = new DateRange();
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6));
		int day = Integer.parseInt(date.substring(6));
		
		if (day > 15) {
			range.setStartDate(year + getMonthStr(month) + "01");
			range.setEndDate(year + getMonthStr(month) + "15");
		} else if (month == 1) {
			range.setStartDate((year - 1) + "1216");
			range.setEndDate((year - 1) + "12" + getLastDay(year - 1, 12));
		} else {
			range.setStartDate(year + getMonthStr(month - 1) + "16");
			range.setEndDate(year + getMonthStr(month - 1) + getLastDay(year, month - 1));
		}
		return range;
	}
	
	private static String beforeSixMonthDay(String date) {
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6));
		return getPreMonth(year, month) + date.substring(6);
	}
	
	/**
	 * 6个月前日期
	 * @param year: 2016
	 * @param month: 01
	 * @return 201507
	 */
	private static String getPreMonth(int year, int month) {
		return month > 6 ? (year + getMonthStr(month - 6)) : ((year - 1) + getMonthStr(month + 6));
	}
	
	private static String getMonthStr(int month) {
		return month > 9 ? String.valueOf(month) : "0" + month;
	}
	
	/**
	 * 根据年月获取当月多少天
	 * @param year: 2016
	 * @param month: 2
	 * @return 29
	 */
	private static int getLastDay(int year, int month) {
		Calendar calendar = Calendar.getInstance(); 
		calendar.set(Calendar.YEAR, year);   
		calendar.set(Calendar.MONTH, month-1);
		return calendar.getActualMaximum(Calendar.DATE);
	}
}

package com.handpay.arch.stat.ics.domain;

import com.handpay.arch.stat.ics.support.Constants;

public class DateRange {
	private String startDate;  //开始日期
	private String endDate;  //结束日期
	
	public DateRange() {}
	public DateRange(String startDate, String endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	public DateRange (String mergeStr) {
		this.startDate = mergeStr.split(Constants.SEPERATOR)[0];
		this.endDate = mergeStr.split(Constants.SEPERATOR)[1];
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	@Override
	public String toString() {
		return startDate + Constants.SEPERATOR + endDate;
	}
	
}

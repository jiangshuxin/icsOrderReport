package com.handpay.arch.stat.ics.domain;

import com.handpay.arch.stat.ics.domain.MetaData.StatType;

public class Stat {
	private String queryDate;
	private String orderDate;
	private int orderCount;
	private int undoneCount;
	private StatType statType;
	
	public Stat(int ordinal) {
		this.statType = StatType.values()[ordinal];
	}
	
	public String getId() {
		return statType.toString() + "-" + orderDate;
	}
	public String getQueryDate() {
		return queryDate;
	}
	public void setQueryDate(String queryDate) {
		this.queryDate = queryDate;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public StatType getStatType() {
		return statType;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public int getUndoneCount() {
		return undoneCount;
	}
	public void setUndoneCount(int undoneCount) {
		this.undoneCount = undoneCount;
	}
}
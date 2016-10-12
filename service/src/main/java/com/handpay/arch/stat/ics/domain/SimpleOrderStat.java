package com.handpay.arch.stat.ics.domain;

public class SimpleOrderStat {
	private String orderDate;
	private int orderNum;
	
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public int getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
	@Override
	public String toString() {
		return "SimpleOrderStat {orderDate:" + orderDate + ", orderNum:" + orderNum + "}";
	}
}

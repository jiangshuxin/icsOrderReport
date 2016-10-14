package com.handpay.arch.stat.ics.domain;

import com.handpay.arch.stat.ics.domain.MetaData.StatType;

public class Stat {
	private String queryDate;
	private String orderDate;
	private int orderCount;
	private int undoneCount;
	private StatType statType;

	public Stat() {
	}
	public Stat(int ordinal) {
		this.statType = StatType.values()[ordinal];
	}
	public Stat(int ordinal, String orderDate) {	
		this.statType = StatType.values()[ordinal];
		this.orderDate = orderDate;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderDate == null) ? 0 : orderDate.hashCode());
		result = prime * result + ((statType == null) ? 0 : statType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stat other = (Stat) obj;
		if (orderDate == null) {
			if (other.orderDate != null)
				return false;
		} else if (!orderDate.equals(other.orderDate))
			return false;
		if (statType != other.statType)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Stat {queryDate=" + queryDate + ", statType=" + statType + ", orderDate=" + orderDate + ", orderCount=" + orderCount
				+ ", undoneCount=" + undoneCount + "}";
	}
	
}
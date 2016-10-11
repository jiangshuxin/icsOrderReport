package com.handpay.arch.stat.ics.domain;

public class MetaData {
	public enum StatType {
		OnlineBatch(0), OfflineBatch(1), YangguangOnline(2), Mall(3);
		private StatType(int ordinal){}
	}
}

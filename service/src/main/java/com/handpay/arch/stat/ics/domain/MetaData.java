package com.handpay.arch.stat.ics.domain;

public class MetaData {
	public enum StatType {
		OnlineBatch(0), OfflineBatch(1), SunshineOnline(2), Mall(3);
		private StatType(int ordinal){}
	}
	
	public enum CountType {
		Sum(0), Undone(4);
		private CountType(int ordinal){}
	}
	
	public enum MixType {
		OnlineSum(0+0),
		OnlineUndone(0+4),
		OfflineSum(1+0),
		OfflineUndone(1+4),
		SunshineSum(2+0),
		SunshineUndone(2+4),
		MallSum(3+0),
		MallUndone(3+4);
		
		private int id;
		private MixType(int id){
			this.id = id;
		}
		public int getId() {
			return id;
		}
	}
}

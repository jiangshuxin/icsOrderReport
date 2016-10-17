package com.handpay.arch.stat.ics.support;

public class MetaData {
	public enum StatType {
		OnlineBatch(0), OfflineBatch(1), SunshineOnline(2), Mall(3);
		
		private int id;
		private StatType(int id){
			this.id = id;
		}
		public int getId() {
			return id;
		}
	}
	
	public enum CountType {
		Sum(0), Undone(4);
		
		private int id;
		private CountType(int id){
			this.id = id;
		}
		public int getId() {
			return id;
		}
	}
	
	public enum MixType {
		OnlineSum(StatType.OnlineBatch.getId() + CountType.Sum.getId()),
		OnlineUndone(StatType.OnlineBatch.getId() + CountType.Undone.getId()),
		OfflineSum(StatType.OfflineBatch.getId() + CountType.Sum.getId()),
		OfflineUndone(StatType.OfflineBatch.getId() + CountType.Undone.getId()),
		SunshineSum(StatType.SunshineOnline.getId() + CountType.Sum.getId()),
		SunshineUndone(StatType.SunshineOnline.getId() + CountType.Undone.getId()),
		MallSum(StatType.Mall.getId() + CountType.Sum.getId()),
		MallUndone(StatType.Mall.getId() + CountType.Undone.getId());
		
		private int id;
		private MixType(int id){
			this.id = id;
		}
		public int getId() {
			return id;
		}
	}
}

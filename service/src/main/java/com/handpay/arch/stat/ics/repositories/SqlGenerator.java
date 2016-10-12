package com.handpay.arch.stat.ics.repositories;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "sql.stat", locations = "classpath:spring/sql.yml")
@Component
public class SqlGenerator {
	private String onlineSum; // 在线批冲-订单总数
	private String onlineUndone; // 在线批冲-未完成
	private String offlineSum; // 离线批冲-订单总数
	private String offlineUndone; // 离线批冲-未完成
	private String sunshineSum; // 阳光-订单总数
	private String sunshineUndone; // 阳光-未完成
	private String mallSum; // 积分商城-订单总数
	private String mallUndone; // 积分商城-未完成
	
	public String getOnlineSum() {
		return onlineSum;
	}
	public void setOnlineSum(String onlineSum) {
		this.onlineSum = onlineSum;
	}
	public String getOnlineUndone() {
		return onlineUndone;
	}
	public void setOnlineUndone(String onlineUndone) {
		this.onlineUndone = onlineUndone;
	}
	public String getOfflineSum() {
		return offlineSum;
	}
	public void setOfflineSum(String offlineSum) {
		this.offlineSum = offlineSum;
	}
	public String getOfflineUndone() {
		return offlineUndone;
	}
	public void setOfflineUndone(String offlineUndone) {
		this.offlineUndone = offlineUndone;
	}
	public String getSunshineSum() {
		return sunshineSum;
	}
	public void setSunshineSum(String sunshineSum) {
		this.sunshineSum = sunshineSum;
	}
	public String getSunshineUndone() {
		return sunshineUndone;
	}
	public void setSunshineUndone(String sunshineUndone) {
		this.sunshineUndone = sunshineUndone;
	}
	public String getMallSum() {
		return mallSum;
	}
	public void setMallSum(String mallSum) {
		this.mallSum = mallSum;
	}
	public String getMallUndone() {
		return mallUndone;
	}
	public void setMallUndone(String mallUndone) {
		this.mallUndone = mallUndone;
	}
}

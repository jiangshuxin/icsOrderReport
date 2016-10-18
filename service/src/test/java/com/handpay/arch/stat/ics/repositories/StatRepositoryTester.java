package com.handpay.arch.stat.ics.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.handpay.arch.stat.ics.support.AppSupport;
import com.handpay.arch.stat.ics.support.MetaData.MixType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatRepositoryTester {
	@Autowired
	private StatRepository statRepository;
	
	@Test
	public void testQueryStat() {
		for (MixType type : MixType.values()) {
			if (AppSupport.isMall(type)) {
				statRepository.queryStat(type.getId(), "20160413-20161013");
			} else {
				statRepository.queryStat(type.getId(), "20161013");
			}
		}
		for (MixType type : MixType.values()) {
			if (AppSupport.isMall(type)) {
				statRepository.queryStat(type.getId(), "20160413-20161013");
			} else {
				statRepository.queryStat(type.getId(), "20161013");
			}
		}
		for (MixType type : MixType.values()) {
			if (AppSupport.isMall(type)) {
				statRepository.queryStat(type.getId(), "20160414-20161014");
			} else {
				statRepository.queryStat(type.getId(), "20161014");
			}
		}
	}
	

	public static void main(String[] args) {
		System.out.println(AppSupport.buildRecentHalfMonthRange("20160303"));
		System.out.println(AppSupport.buildRecentHalfMonthRange("20160216"));
		System.out.println(AppSupport.buildRecentHalfMonthRange("20160110"));
		for(int i=0; i<100; i++) {
			System.out.println("1-------->>"+AppSupport.getToday());
//			System.out.println("2-------->>"+AppSupport.TODAY);
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}

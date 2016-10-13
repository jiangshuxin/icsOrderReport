package com.handpay.arch.stat.ics.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.handpay.arch.stat.ics.domain.MetaData.MixType;
import com.handpay.arch.stat.ics.support.HalfMonthSupport;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatRepositoryTester {
	@Autowired
	private StatRepository statRepository;
	
	@Test
	public void testQueryStat() {
		for(MixType type: MixType.values()) {
			statRepository.queryStat(type.getId(), "20161013");
		}
		for(MixType type: MixType.values()) {
			statRepository.queryStat(type.getId(), "20161013");
		}
		for(MixType type: MixType.values()) {
			statRepository.queryStat(type.getId(), "20161014");
		}
	}
	

	public static void main(String[] args) {
		System.out.println(HalfMonthSupport.getDayRange("20160303"));
		System.out.println(HalfMonthSupport.getDayRange("20160216"));
		System.out.println(HalfMonthSupport.getDayRange("20160110"));
	}
}

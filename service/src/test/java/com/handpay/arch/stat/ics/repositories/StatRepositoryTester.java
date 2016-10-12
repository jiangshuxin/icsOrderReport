package com.handpay.arch.stat.ics.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.handpay.arch.stat.ics.domain.MetaData.MixType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatRepositoryTester {
	@Autowired
	private StatRepository statRepository;
	
	@Test
	public void testQueryStat() {
		for(MixType type: MixType.values()) {
			statRepository.queryStat(type.getId());
		}
	}

}

package com.handpay.arch.stat.ics.repositories;

import java.util.List;

import com.handpay.arch.stat.ics.domain.SimpleOrderStat;

public interface StatRepository {
	List<SimpleOrderStat> queryStat(int type); 
}
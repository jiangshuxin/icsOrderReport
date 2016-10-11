package com.handpay.arch.stat.ics.repositories;

import java.util.List;

import com.handpay.arch.stat.ics.domain.Stat;

public interface StatRepository {
	List<Stat> queryStat(String dateStr, int type); 
}
package com.handpay.arch.stat.ics.service;

import java.util.List;

import com.handpay.arch.stat.ics.domain.Stat;
import com.handpay.arch.stat.ics.support.MetaData.StatType;

/**
 * Created by sxjiang on 16/10/11.
 */
public interface ReportService {

	void makeReport();
	
	List<Stat> embraceSumAndUndone(StatType statType);
	
    int makeSnapshot(Stat... stat);

    String exportExcel(String fromDate,String toDate);
}

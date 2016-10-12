package com.handpay.arch.stat.ics.service;

import java.util.List;

import com.handpay.arch.stat.ics.domain.MetaData.StatType;
import com.handpay.arch.stat.ics.domain.Stat;

/**
 * Created by sxjiang on 16/10/11.
 */
public interface ReportService {

	List<Stat> embraceSumAndUndone(StatType statType);
	
    int makeSnapshot(Stat... stat);

    String exportExcel(String fromDate,String toDate);
}

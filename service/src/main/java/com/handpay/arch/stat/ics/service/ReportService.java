package com.handpay.arch.stat.ics.service;

import com.handpay.arch.stat.ics.domain.MetaData;
import com.handpay.arch.stat.ics.domain.Stat;

import java.util.Date;

/**
 * Created by sxjiang on 16/10/11.
 */
public interface ReportService {

    int makeSnapshot(Stat... stat);

    String exportExcel(MetaData.StatType type,Date fromDate,Date toDate);
}

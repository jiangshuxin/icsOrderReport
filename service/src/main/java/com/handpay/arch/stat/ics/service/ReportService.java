package com.handpay.arch.stat.ics.service;

import com.handpay.arch.stat.ics.domain.Stat;

/**
 * Created by sxjiang on 16/10/11.
 */
public interface ReportService {

    int makeSnapshot(Stat... stat);

    String exportExcel(String fromDate,String toDate);
}

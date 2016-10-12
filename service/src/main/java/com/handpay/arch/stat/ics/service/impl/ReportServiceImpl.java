package com.handpay.arch.stat.ics.service.impl;

import com.handpay.arch.stat.ics.domain.MetaData;
import com.handpay.arch.stat.ics.domain.Stat;
import com.handpay.arch.stat.ics.service.ReportService;
import com.handpay.rache.core.spring.StringRedisTemplateX;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sxjiang on 2016/10/12.
 */
public class ReportServiceImpl implements ReportService {

    @Autowired
    private StringRedisTemplateX stringRedisTemplateX;
    private String datePattern = "yyyyMMdd";

    @Override
    public int makeSnapshot(Stat... stats) {//同一个查询时间
        for(Stat stat : stats){
            MetaData.StatType type = stat.getStatType();
            Date orderDay = null;
            Date queryDay = null;
            try {
                orderDay = DateUtils.parseDate(stat.getOrderDate(),datePattern);
                queryDay = DateUtils.parseDate(stat.getQueryDate(),datePattern);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            double value = Double.parseDouble(String.valueOf(orderDay.getTime()));
            stringRedisTemplateX.boundZSetOps(type.name()).add(stat.getOrderDate(),value);

            stringRedisTemplateX.boundValueOps(StringUtils.join(new String[]{type.name(),stat.getOrderDate(),"orderCount"},"-")).set(String.valueOf(stat.getOrderCount()));

            Long length = stringRedisTemplateX.boundListOps(StringUtils.join(new String[]{type.name(),stat.getOrderDate(),"undoneCount"},"-")).size();
            if(queryDay.compareTo(DateUtils.addDays(orderDay,Integer.parseInt(String.valueOf(length)))) < 0){//订单时间+length < 查询时间表示可以操作
                stringRedisTemplateX.boundListOps(StringUtils.join(new String[]{type.name(),stat.getOrderDate(),"undoneCount"},"-")).rightPush(String.valueOf(stat.getUndoneCount()));
            }
        }

        return 0;
    }

    @Override
    public String exportExcel(MetaData.StatType type, Date fromDate, Date toDate) {
        return null;
    }
}

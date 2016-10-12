package com.handpay.arch.stat.ics.service.impl;

import com.google.common.collect.Maps;
import com.handpay.arch.stat.ics.domain.MetaData;
import com.handpay.arch.stat.ics.domain.Stat;
import com.handpay.arch.stat.ics.domain.StatReport;
import com.handpay.arch.stat.ics.service.ReportService;
import com.handpay.rache.core.spring.StringRedisTemplateX;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private Double extractDateValue(String date){
        Date day = null;
        try {
            day = DateUtils.parseDate(date,datePattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Double.parseDouble(String.valueOf(day.getTime()));
    }

    @Override
    public String exportExcel(String fromDate,String toDate) {
        Double fromValue = extractDateValue(fromDate);
        Double toValue = extractDateValue(toDate);

        Map<String,List<StatReport>> dataMap = Maps.newHashMap();
        for(MetaData.StatType type : MetaData.StatType.values()){
            Set<String> dateSet = stringRedisTemplateX.boundZSetOps(type.name()).rangeByScore(fromValue,toValue);
        }
        return null;
    }
}

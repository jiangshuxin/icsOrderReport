package com.handpay.arch.stat.ics.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.handpay.arch.stat.ics.domain.MetaData;
import com.handpay.arch.stat.ics.domain.MetaData.CountType;
import com.handpay.arch.stat.ics.domain.MetaData.StatType;
import com.handpay.arch.stat.ics.domain.SimpleOrderStat;
import com.handpay.arch.stat.ics.domain.Stat;
import com.handpay.arch.stat.ics.domain.StatReport;
import com.handpay.arch.stat.ics.repositories.StatRepository;
import com.handpay.arch.stat.ics.service.ReportService;
import com.handpay.rache.core.spring.StringRedisTemplateX;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sxjiang on 2016/10/12.
 */
@Service
public class ReportServiceImpl implements ReportService {
    private static Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

	@Autowired
	private StatRepository statRepository;
    @Autowired
    private StringRedisTemplateX stringRedisTemplateX;
    private String datePattern = "yyyyMMdd";
    private String outputPath = "/Users/sxjiang";
    @Override
	public List<Stat> embraceSumAndUndone(StatType statType) {
    	//1. 查询订单总数和未完成订单数
    	List<SimpleOrderStat> sumList = statRepository.queryStat(statType.getId());  
    	List<SimpleOrderStat> undoneList = statRepository.queryStat(statType.getId() + CountType.Undone.getId());
    	
    	//2. 组装成Stat
    	Map<String, Stat> maps = Maps.newHashMap();
    	for(SimpleOrderStat sum: sumList) {
    		Stat e = new Stat(statType.getId());
    		e.setOrderDate(sum.getOrderDate());
    		e.setOrderCount(sum.getOrderNum());
    		maps.put(e.getId(), e);
    	}
    	for(SimpleOrderStat undone: undoneList) {
			String key = statType + "-" + undone.getOrderDate();
    		if (maps.containsKey(key)) {
    			maps.get(key).setUndoneCount(undone.getOrderNum());
    		} else {
    			Stat e = new Stat(statType.getId());
        		e.setOrderDate(undone.getOrderDate());
        		e.setUndoneCount(undone.getOrderNum());
        		maps.put(e.getId(), e);
    		}
    	}
		return Lists.newArrayList(maps.values());
	}

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
            List<StatReport> reportList = Lists.newArrayList();
            for(String date : dateSet){
                StatReport report = new StatReport(type.ordinal());
                report.setOrderDate(date);

                String orderCount = stringRedisTemplateX.boundValueOps(StringUtils.join(new String[]{type.name(),date,"orderCount"},"-")).get();
                report.setOrderCount(Integer.parseInt(orderCount));

                List<String> undoneCountList = stringRedisTemplateX.boundListOps(StringUtils.join(new String[]{type.name(),date,"undoneCount"},"-")).range(0,-1);
                report.setUndoneCountList(undoneCountList);

                List<String> undoneRatioList = Lists.newArrayList();
                for(String count : undoneCountList){
                    BigDecimal ratio = new BigDecimal(count).divide(new BigDecimal(orderCount),2, RoundingMode.HALF_UP);
                    undoneRatioList.add(ratio.toPlainString());
                }
                report.setUndoneRatioList(undoneRatioList);

                reportList.add(report);
            }
            dataMap.put(StringUtils.join(new String[]{"statList",type.name()},"-"),reportList);
        }

        InputStream in = null;
        OutputStream out = null;
        Workbook workbook;
        XLSTransformer transformer = new XLSTransformer();
        try{
            in = new ClassPathResource("template/ICS-undone-template.xls").getInputStream();
            out = new FileOutputStream(outputPath);
            workbook=transformer.transformXLS(in, dataMap);
            workbook.write(out);
            out.flush();

        }catch(Exception e){
            log.error("生成Excel失败！",e);
        }finally{
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            dataMap = null;
            workbook = null;
        }
        return null;
    }
}

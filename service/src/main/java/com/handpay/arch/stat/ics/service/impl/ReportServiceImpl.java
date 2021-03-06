package com.handpay.arch.stat.ics.service.impl;

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

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.handpay.arch.stat.ics.domain.DateRange;
import com.handpay.arch.stat.ics.domain.SimpleOrderStat;
import com.handpay.arch.stat.ics.domain.Stat;
import com.handpay.arch.stat.ics.domain.StatReport;
import com.handpay.arch.stat.ics.repositories.StatRepository;
import com.handpay.arch.stat.ics.service.ReportService;
import com.handpay.arch.stat.ics.support.AppSupport;
import com.handpay.arch.stat.ics.support.Constants;
import com.handpay.arch.stat.ics.support.MetaData;
import com.handpay.arch.stat.ics.support.MetaData.CountType;
import com.handpay.arch.stat.ics.support.MetaData.StatType;
import com.handpay.rache.core.spring.StringRedisTemplateX;

import net.sf.jxls.transformer.XLSTransformer;

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
    @Value("${report.outputPath}")
    private String outputPath = "/Users/sxjiang/";
    
    @Override
    public void makeReport(){
    	for(MetaData.StatType type : MetaData.StatType.values()){
			List<Stat> statList = embraceSumAndUndone(type);
			makeSnapshot(statList.toArray(new Stat[0]));
		}
		DateRange range = AppSupport.buildSixMonthRange(AppSupport.getToday());
		exportExcel(range.getStartDate(), range.getEndDate());
    }
    
    @Override
	public List<Stat> embraceSumAndUndone(StatType statType) {
    	String range = AppSupport.buildRecentHalfMonthRange(AppSupport.getToday()).toString();
    	String queryStr = StatType.Mall.equals(statType) ? range : AppSupport.getToday();
    	
    	//1. 查询订单总数和未完成订单数
    	List<SimpleOrderStat> sumList = statRepository.queryStat(statType.getId(), queryStr);  
    	List<SimpleOrderStat> undoneList = statRepository.queryStat(statType.getId() + CountType.Undone.getId(), queryStr);
    	
    	//2. 组装成Stat
    	Map<String, Stat> maps = Maps.newHashMap();
    	for(SimpleOrderStat sum: sumList) {
    		Stat e = new Stat(statType, sum, range);
    		e.setOrderCount(sum.getOrderNum());
    		maps.put(e.getId(), e);
    	}
    	for(SimpleOrderStat undone: undoneList) {
			String key = statType + Constants.SEPERATOR + undone.getOrderDate();
    		if (maps.containsKey(key)) {
    			Stat e = maps.get(key);
    			e.setUndoneCount(e.getOrderCount() - undone.getOrderNum());
    		} 
    	}
		return Lists.newArrayList(maps.values());
	}
    
    @Override
    public int makeSnapshot(Stat... stats) {//同一个查询时间
        for(Stat stat : stats){
            MetaData.StatType type = stat.getStatType();
            if(type.equals(StatType.Mall)){
                Date orderDay = null;
                try {
                    String[] period = stat.getOrderDate().split(Constants.SEPERATOR);
                    if(period.length < 1)continue;
                    orderDay = DateUtils.parseDate(period[0], Constants.DATE_PATTERN);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                double value = Double.parseDouble(String.valueOf(orderDay.getTime()));
                Set<String> set = stringRedisTemplateX.boundZSetOps(type.name()).rangeByScore(value,value);
                if(set.size() == 0) stringRedisTemplateX.boundZSetOps(type.name()).add(JSON.toJSONString(stat, SerializerFeature.WriteClassName),value);
            }else{
                Date orderDay = null;
                try {
                    orderDay = DateUtils.parseDate(stat.getOrderDate(),Constants.DATE_PATTERN);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                double value = Double.parseDouble(String.valueOf(orderDay.getTime()));
                stringRedisTemplateX.boundZSetOps(type.name()).add(stat.getOrderDate(),value);

                stringRedisTemplateX.boundValueOps(StringUtils.join(new String[]{type.name(),stat.getOrderDate(),"orderCount"},"-")).set(String.valueOf(stat.getOrderCount()));

                //Long length = stringRedisTemplateX.boundListOps(StringUtils.join(new String[]{type.name(),stat.getOrderDate(),"undoneCount"},"-")).size();
                stringRedisTemplateX.boundHashOps(StringUtils.join(new String[]{type.name(),stat.getOrderDate(),"undoneCount"},"-")).put(stat.getQueryDate(),String.valueOf(stat.getUndoneCount()));
            }
        }

        return 0;
    }

    private Double extractDateValue(String date){
        Date day = null;
        try {
            day = DateUtils.parseDate(date,Constants.DATE_PATTERN);
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
            List<StatReport> reportList = Lists.newArrayList();
            if(type.equals(StatType.Mall)){
                Set<String> objSet = stringRedisTemplateX.boundZSetOps(type.name()).reverseRangeByScore(fromValue,toValue);
                for(String objStr : objSet){
					if (NumberUtils.isDigits(objStr))
						continue;
                    Stat stat = JSON.parseObject(objStr,Stat.class);
                    StatReport report = new StatReport(type);
                    BeanUtils.copyProperties(stat,report);
                    if(stat.getOrderCount() == 0){
                        report.setUndoneRatio("0.00%");
                    }else{
                        report.setUndoneRatio(new BigDecimal(stat.getUndoneCount()).divide(new BigDecimal(stat.getOrderCount()),Constants.DEFAULT_SCALE,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).toPlainString()+"%");
                    }
                    reportList.add(report);
                }
            }else{
                Set<String> dateSet = stringRedisTemplateX.boundZSetOps(type.name()).reverseRangeByScore(fromValue,toValue);
                for(String date : dateSet){
                    StatReport report = new StatReport(type);
                    report.setOrderDate(date);

                    String orderCount = stringRedisTemplateX.boundValueOps(StringUtils.join(new String[]{type.name(),date,"orderCount"},Constants.SEPERATOR)).get();
                    report.setOrderCount(Integer.parseInt(orderCount));

                    List<Object> undoneCountList = stringRedisTemplateX.boundHashOps(StringUtils.join(new String[]{type.name(),date,"undoneCount"},Constants.SEPERATOR)).values();
                    report.setUndoneCountList(ListUtils.transformedList(undoneCountList, new Transformer() {
                        @Override
                        public String transform(Object input) {
                            return input.toString();
                        }
                    }));

                    //特殊处理:Offline需展示到T+3  Online T+6 Sunshine T+10
                    int undoneSize = report.getUndoneCountList().size();
                    if(type.equals(StatType.OfflineBatch)){
                        if(undoneSize < 4){
                            for(int i=0;i<4-undoneSize;i++){
                                report.getUndoneCountList().add("#N/A");
                            }
                        }else if(undoneSize > 4){
                            report.setUndoneCountList(report.getUndoneCountList().subList(0,4));
                        }
                    }
                    if(type.equals(StatType.OnlineBatch)){
                        if(undoneSize < 7){
                            for(int i=0;i<7-undoneSize;i++){
                                report.getUndoneCountList().add("#N/A");
                            }
                        }else if(undoneSize > 7){
                            report.setUndoneCountList(report.getUndoneCountList().subList(0,7));
                        }
                    }
                    if(type.equals(StatType.SunshineOnline)){
                        if(undoneSize < 11){
                            for(int i=0;i<11-undoneSize;i++){
                                report.getUndoneCountList().add("#N/A");
                            }
                        }else if(undoneSize > 11){
                            report.setUndoneCountList(report.getUndoneCountList().subList(0,11));
                        }
                    }

                    List<String> undoneRatioList = Lists.newArrayList();
                    for(String count : report.getUndoneCountList()){
                        if(StringUtils.equals(count,"#N/A")){
                            undoneRatioList.add("#N/A");
                        }else{
                            BigDecimal ratio = new BigDecimal(count).divide(new BigDecimal(orderCount),Constants.DEFAULT_SCALE, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                            undoneRatioList.add(ratio.toPlainString()+"%");
                        }
                    }
                    report.setUndoneRatioList(undoneRatioList);

                    reportList.add(report);
                }
            }

            dataMap.put(StringUtils.join(new String[]{"statList",type.name()},""),reportList);
        }

        generateXls(fromDate, toDate, dataMap);
        return null;
    }

    private void generateXls(String fromDate, String toDate, Map<String, List<StatReport>> dataMap) {
        InputStream in = null;
        OutputStream out = null;
        Workbook workbook;
        XLSTransformer transformer = new XLSTransformer();
        try{
            in = new ClassPathResource("template/ICS-undone-template.xls").getInputStream();
            out = new FileOutputStream(outputPath+ StringUtils.join(new String[]{"ICS-undone",fromDate,toDate},"-")+".xls");
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
    }
}

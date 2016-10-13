package com.handpay.arch.stat.ics.service.impl;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

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

/**
 * Created by sxjiang on 2016/10/12.
 */
@Service
public class ReportServiceImpl implements ReportService {
    private static Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);
    private static final String datePattern = "yyyyMMdd";
    static final SimpleDateFormat sdf = new SimpleDateFormat(datePattern);

	@Autowired
	private StatRepository statRepository;
    @Autowired
    private StringRedisTemplateX stringRedisTemplateX;
    private String outputPath = "/Users/sxjiang/";
    @Override
	public List<Stat> embraceSumAndUndone(StatType statType) {
    	//1. 查询订单总数和未完成订单数
    	List<SimpleOrderStat> sumList = statRepository.queryStat(statType.getId(), sdf.format(new Date()));  
    	List<SimpleOrderStat> undoneList = statRepository.queryStat(statType.getId() + CountType.Undone.getId(), sdf.format(new Date()));
    	
    	//2. 组装成Stat
    	Map<String, Stat> maps = Maps.newHashMap();
    	for(SimpleOrderStat sum: sumList) {
    		Stat e = new Stat(statType.getId());
    		e.setOrderDate(sum.getOrderDate());
    		e.setOrderCount(sum.getOrderNum());
            e.setQueryDate(sdf.format(new Date()));
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
            if(type.equals(StatType.Mall)){
                Date orderDay = null;
                try {
                    String[] period = stat.getOrderDate().split("-");
                    if(period.length < 1)continue;
                    orderDay = DateUtils.parseDate(period[0],datePattern);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                double value = Double.parseDouble(String.valueOf(orderDay.getTime()));
                stringRedisTemplateX.boundZSetOps(type.name()).add(JSON.toJSONString(stat, SerializerFeature.WriteClassName),value);
            }else{
                Date orderDay = null;
                try {
                    orderDay = DateUtils.parseDate(stat.getOrderDate(),datePattern);
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
            List<StatReport> reportList = Lists.newArrayList();
            if(type.equals(StatType.Mall)){
                Set<String> objSet = stringRedisTemplateX.boundZSetOps(type.name()).reverseRangeByScore(fromValue,toValue);
                for(String objStr : objSet){
                    Stat stat = JSON.parseObject(objStr,Stat.class);
                    StatReport report = new StatReport(type.ordinal());
                    BeanUtils.copyProperties(stat,report);
                    report.setUndoneRatio(new BigDecimal(stat.getUndoneCount()).divide(new BigDecimal(stat.getOrderCount()),2,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).toPlainString()+"%");
                    reportList.add(report);
                }
            }else{
                Set<String> dateSet = stringRedisTemplateX.boundZSetOps(type.name()).reverseRangeByScore(fromValue,toValue);
                for(String date : dateSet){
                    StatReport report = new StatReport(type.ordinal());
                    report.setOrderDate(date);

                    String orderCount = stringRedisTemplateX.boundValueOps(StringUtils.join(new String[]{type.name(),date,"orderCount"},"-")).get();
                    report.setOrderCount(Integer.parseInt(orderCount));

                    List<Object> undoneCountList = stringRedisTemplateX.boundHashOps(StringUtils.join(new String[]{type.name(),date,"undoneCount"},"-")).values();
                    report.setUndoneCountList(ListUtils.transformedList(undoneCountList, new Transformer() {
                        @Override
                        public String transform(Object input) {
                            return input.toString();
                        }
                    }));

                    //特殊处理:Offline需展示到T+3  Online T+6 Sunshine T+10
                    int undoneSize = report.getUndoneCountList().size();
                    if(type.equals(StatType.OfflineBatch) && undoneSize < 4){
                        for(int i=0;i<4-undoneSize;i++){
                            report.getUndoneCountList().add("#N/A");
                        }
                    }
                    if(type.equals(StatType.OnlineBatch) && undoneSize < 7){
                        for(int i=0;i<7-undoneSize;i++){
                            report.getUndoneCountList().add("#N/A");
                        }
                    }
                    if(type.equals(StatType.SunshineOnline) && undoneSize < 11){
                        for(int i=0;i<11-undoneSize;i++){
                            report.getUndoneCountList().add("#N/A");
                        }
                    }

                    List<String> undoneRatioList = Lists.newArrayList();
                    for(String count : report.getUndoneCountList()){
                        if(StringUtils.equals(count,"#N/A")){
                            undoneRatioList.add("#N/A");
                        }else{
                            BigDecimal ratio = new BigDecimal(count).divide(new BigDecimal(orderCount),2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
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

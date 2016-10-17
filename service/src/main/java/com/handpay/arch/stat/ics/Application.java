package com.handpay.arch.stat.ics;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.handpay.arch.stat.ics.domain.Stat;
import com.handpay.arch.stat.ics.service.ReportService;
import com.handpay.arch.stat.ics.support.Constants;
import com.handpay.arch.stat.ics.support.MetaData;

/**
 * Created by sxjiang on 16/10/11.
 */
@RestController
@SpringBootApplication
@EnableCaching
@EnableScheduling
@ImportResource(Constants.REDIS_XML_LOCATION)
public class Application {
	private static Logger log = LoggerFactory.getLogger(Application.class);

	
	@Autowired
	private ReportService reportService;
	@Value("${report.downloadPath}")
	private String downloadPath;
	
	@Primary 
	@Bean(name=Constants.CACHE_MAMAGER_NAME)
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache(Constants.CACHE_NAME)));
		return cacheManager;
	}
	
	@Scheduled(cron = "0 0,30 * * * *")
	@RequestMapping({"/report/start"})
	public void doSomething() {
		log.info("Job start ......");
		reportService.makeReport();
		log.info("Job End ......");
	}
	
	@RequestMapping({"/report/{period}"})
	  public String report(@PathVariable("period") String period, HttpServletResponse response)
	    throws IOException
	  {
	    MetaData.StatType[] arrayOfStatType;
	    int j = (arrayOfStatType = MetaData.StatType.values()).length;
	    for (int i = 0; i < j; i++)
	    {
	      MetaData.StatType type = arrayOfStatType[i];
	      List<Stat> statList = this.reportService.embraceSumAndUndone(type);
	      this.reportService.makeSnapshot((Stat[])statList.toArray(new Stat[0]));
	    }
	    String[] periods = StringUtils.split(period, Constants.SEPERATOR);
	    if (periods.length < 2) {
	      return "ERROR";
	    }
	    this.reportService.exportExcel(periods[0], periods[1]);
	    response.sendRedirect(this.downloadPath);
	    return "World!";
	  }
	
    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);
	}
}

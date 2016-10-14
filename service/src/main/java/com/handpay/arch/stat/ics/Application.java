package com.handpay.arch.stat.ics;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.handpay.arch.stat.ics.domain.Stat;
import com.handpay.arch.stat.ics.service.ReportService;
import com.handpay.arch.stat.ics.support.Constants;
import com.handpay.arch.stat.ics.support.MetaData;
import com.handpay.rache.core.spring.StringRedisTemplateX;

/**
 * Created by sxjiang on 16/10/11.
 */
@RestController
@SpringBootApplication
@EnableCaching
@EnableScheduling
@ImportResource(Constants.REDIS_XML_LOCATION)
public class Application {

	@Autowired
	private StringRedisTemplateX stringRedisTemplateX;
	@Autowired
	private ReportService reportService;
	
	@Primary 
	@Bean(name=Constants.CACHE_MAMAGER_NAME)
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache(Constants.CACHE_NAME)));
		return cacheManager;
	}
	
	@Scheduled(cron = "0 * * * * *")
	public void doSomething() {
		System.out.println("do something");
		reportService.makeReport();
	}
	
	@RequestMapping("/hello")
	public String home() {
		stringRedisTemplateX.keys("test");
		for(MetaData.StatType type : MetaData.StatType.values()){
			List<Stat> statList = reportService.embraceSumAndUndone(type);
			reportService.makeSnapshot(statList.toArray(new Stat[0]));
		}
		reportService.exportExcel("20160601","20161012");
		return "World!";
	}
	
    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);
	}
}

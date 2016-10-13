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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.handpay.arch.stat.ics.domain.MetaData;
import com.handpay.arch.stat.ics.domain.Stat;
import com.handpay.arch.stat.ics.service.ReportService;
import com.handpay.rache.core.spring.StringRedisTemplateX;

/**
 * Created by sxjiang on 16/10/11.
 */
@RestController
@SpringBootApplication
@EnableCaching
@ImportResource("spring/spring-application.xml")
public class Application {

	@Autowired
	private StringRedisTemplateX stringRedisTemplateX;
	@Autowired
	private ReportService reportService;
	
	@Primary 
	@Bean(name="simpleCache")
	public CacheManager cacheManager() {
		// configure and return an implementation of Spring's CacheManager SPI
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("dataStats")));
		return cacheManager;
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

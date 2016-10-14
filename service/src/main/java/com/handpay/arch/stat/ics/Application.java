package com.handpay.arch.stat.ics;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.handpay.arch.stat.ics.domain.MetaData;
import com.handpay.arch.stat.ics.domain.Stat;
import com.handpay.arch.stat.ics.service.ReportService;
import com.handpay.rache.core.spring.StringRedisTemplateX;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by sxjiang on 16/10/11.
 */
@RestController
@SpringBootApplication
@EnableCaching
@ImportResource("spring/spring-application.xml")
public class Application {

	@Autowired
	private ReportService reportService;
	@Value("${report.downloadPath}")
	private String downloadPath;
	
	@Primary 
	@Bean(name="simpleCache")
	public CacheManager cacheManager() {
		// configure and return an implementation of Spring's CacheManager SPI
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("dataStats")));
		return cacheManager;
	}
	
	@RequestMapping("/report/{period}")
	public String report(@PathVariable("period") String period, HttpServletResponse response) throws IOException {
		for(MetaData.StatType type : MetaData.StatType.values()){
			List<Stat> statList = reportService.embraceSumAndUndone(type);
			reportService.makeSnapshot(statList.toArray(new Stat[0]));
		}
		String[] periods = StringUtils.split(period,"-");
		if(periods.length < 2) return "ERROR";
		reportService.exportExcel(periods[0],periods[1]);
		response.sendRedirect(downloadPath);
		return "World!";
	}
	
    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);
	}
}

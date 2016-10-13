package com.handpay.arch.stat.ics;

import com.handpay.arch.stat.ics.domain.MetaData;
import com.handpay.arch.stat.ics.domain.Stat;
import com.handpay.arch.stat.ics.service.ReportService;
import com.handpay.rache.core.spring.StringRedisTemplateX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by sxjiang on 16/10/11.
 */
@RestController
@SpringBootApplication
@ImportResource("spring/spring-application.xml")
public class Application {

	@Autowired
	private StringRedisTemplateX stringRedisTemplateX;
	@Autowired
	private ReportService reportService;
	
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

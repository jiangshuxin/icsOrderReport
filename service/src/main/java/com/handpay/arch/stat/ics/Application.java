package com.handpay.arch.stat.ics;

import com.handpay.rache.core.spring.StringRedisTemplateX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by sxjiang on 16/10/11.
 */
@RestController
@SpringBootApplication
@ImportResource("spring/spring-application.xml")
public class Application {

	@Autowired
	private StringRedisTemplateX stringRedisTemplateX;
	
	@RequestMapping("/hello")
	public String home() {
		stringRedisTemplateX.keys("test");
		return "World!";
	}
	
    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);
	}
}

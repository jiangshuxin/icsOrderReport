package com.handpay.arch.stat.ics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by sxjiang on 16/10/11.
 */
@RestController
@SpringBootApplication
public class Application {
	
	@RequestMapping("/hello")
	public String home() {
		return "World!";
	}
	
    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);
	}
}

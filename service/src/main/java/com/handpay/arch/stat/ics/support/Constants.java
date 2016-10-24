package com.handpay.arch.stat.ics.support;

public class Constants {
	public static final String SEPERATOR = "-";  //分隔符
	public static final String DATE_PATTERN = "yyyyMMdd";
	
	public static final String CACHE_NAME = "dataStats";
	public static final String CACHE_MAMAGER_NAME = "simpleCache";
	public static final String CACHE_KEY_REGEX = "#dateStr + #id";
	
	public static final String SQL_PREFIX = "sql.stat";
	public static final String SQL_YML_LOCATION = "classpath:spring/sql.yml";
	public static final String REDIS_XML_LOCATION = "classpath:spring/spring-application.xml";

	public static final int DEFAULT_SCALE = 4;
}
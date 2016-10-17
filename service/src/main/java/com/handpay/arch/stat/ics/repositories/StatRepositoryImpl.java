package com.handpay.arch.stat.ics.repositories;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Lists;
import com.handpay.arch.stat.ics.domain.DateRange;
import com.handpay.arch.stat.ics.domain.SimpleOrderStat;
import com.handpay.arch.stat.ics.support.AppSupport;
import com.handpay.arch.stat.ics.support.Constants;
import com.handpay.arch.stat.ics.support.MetaData.MixType;

@Repository
public class StatRepositoryImpl implements StatRepository {
	
	private static Logger log = LoggerFactory.getLogger(StatRepositoryImpl.class);
	static final BeanPropertyRowMapper<SimpleOrderStat> rowMapper = new BeanPropertyRowMapper<SimpleOrderStat>(SimpleOrderStat.class);
	
	@Autowired
	private SqlGenerator sqlGenerator; 
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Cacheable(value = Constants.CACHE_NAME, key = Constants.CACHE_KEY_REGEX)
	@Override
	public List<SimpleOrderStat> queryStat(int id, String dateStr) {
		reload();

		String sql = getSql(id);
		List<SimpleOrderStat> statList = Lists.newArrayList();
		if (AppSupport.isMall(id)) { // 积分商城:参数为区间
			DateRange range = new DateRange(dateStr);
			statList = jdbcTemplate.query(sql, new Object[] { dateStr, range.getStartDate(), range.getEndDate() }, rowMapper);
		} else {
			statList = jdbcTemplate.query(sql, rowMapper);
		}

		log.info("dateStr:: " + dateStr + ",id:: " + id + ",sql:: " + sql);
		log.info("statList::: " + statList);
		return statList;
	}
	
	private String getSql(int id) {
		for (MixType type : MixType.values()) {
			if (id == type.getId()) {
				Method method = ReflectionUtils.findMethod(SqlGenerator.class, "get" + type);
				return ReflectionUtils.invokeMethod(method, sqlGenerator).toString();
			}
		}
		throw new IllegalArgumentException("参数不正确---args::: " + id);
	}
	
	@CacheEvict(value=Constants.CACHE_NAME, allEntries = true) // 清空缓存
	public void reload() {
	}

}

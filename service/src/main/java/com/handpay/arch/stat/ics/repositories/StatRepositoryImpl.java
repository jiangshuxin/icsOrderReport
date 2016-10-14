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

import com.handpay.arch.stat.ics.domain.MetaData;
import com.handpay.arch.stat.ics.domain.MetaData.MixType;
import com.handpay.arch.stat.ics.domain.SimpleOrderStat;

@Repository
public class StatRepositoryImpl implements StatRepository {
	
	private static Logger log = LoggerFactory.getLogger(StatRepositoryImpl.class);
	static final BeanPropertyRowMapper<SimpleOrderStat> rowMapper = new BeanPropertyRowMapper<SimpleOrderStat>(SimpleOrderStat.class);
//	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	@Autowired
	private SqlGenerator sqlGenerator; 
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
//	@Override
//	public List<SimpleOrderStat> queryStat(int id) {
//		System.out.println("------------------->>>" + sdf.format(new Date()));
//		return queryStat(id, sdf.format(new Date()));
//	}
//	
	@Cacheable(value="dataStats", cacheManager = "simpleCache", key="#dateStr + #id")
	@Override
	public List<SimpleOrderStat> queryStat(int id, String dateStr) {
		reload();
		String sql = getSql(id);
		if (isMall(id)) {  //积分商城:参数为区间
			return jdbcTemplate.query(sql, new Object[]{dateStr, dateStr.split("-")[0]+"000000", dateStr.split("-")[1]+"235959"}, rowMapper);
		}
		List<SimpleOrderStat> statList = jdbcTemplate.query(sql, rowMapper);
		log.info("dateStr:: " + dateStr + ",id:: "+id+",sql:: " + sql);
		log.info("statList::: " + statList);
		return statList;
	}
	
	private boolean isMall(int id) {
		return (MetaData.MixType.MallSum.getId() == id) || (MetaData.MixType.MallUndone.getId()  == id);
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
	
	@CacheEvict(value = "dataStats", allEntries = true) // 清空缓存
	public void reload() {
	}

}

package com.handpay.arch.stat.ics.repositories;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import com.handpay.arch.stat.ics.domain.MetaData.MixType;
import com.handpay.arch.stat.ics.domain.SimpleOrderStat;

@Repository
public class StatRepositoryImpl implements StatRepository {
	
	static final BeanPropertyRowMapper<SimpleOrderStat> rowMapper = new BeanPropertyRowMapper<SimpleOrderStat>(SimpleOrderStat.class);
	
	@Autowired
	private SqlGenerator sqlGenerator; 
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<SimpleOrderStat> queryStat(String dateStr, int id) {
		String sql = getSql(id);
		List<SimpleOrderStat> statList = jdbcTemplate.query(sql, rowMapper);
		System.out.println("sql::: " + sql);
		System.out.println("statList::: " + statList);
		return statList;
	}
	
	public String getSql(int id) {
		for (MixType type : MixType.values()) {
			if (id == type.getId()) {
				Method method = ReflectionUtils.findMethod(SqlGenerator.class, "get" + type);
				return ReflectionUtils.invokeMethod(method, sqlGenerator).toString();
			}
		}
		throw new IllegalArgumentException("参数不正确---args::: " + id);
	}

}

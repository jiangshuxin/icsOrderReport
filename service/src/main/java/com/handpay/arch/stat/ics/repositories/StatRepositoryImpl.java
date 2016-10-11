package com.handpay.arch.stat.ics.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.handpay.arch.stat.ics.domain.Stat;

@Repository
public class StatRepositoryImpl implements StatRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	final String sql = "select count(1) from ics.topup_detai_source";
	
	@Override
	public List<Stat> queryStat(String dateStr, int type) {
		int result = jdbcTemplate.queryForObject(sql, Integer.class);
		System.out.println(String.format("result:: s%", result));
		return null;
	}

}

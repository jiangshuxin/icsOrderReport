package com.handpay.arch.stat.ics.domain;

import java.util.List;

import com.handpay.arch.stat.ics.support.MetaData.StatType;

/**
 * Created by sxjiang on 2016/10/12.
 */
public class StatReport extends Stat {
    private List<String> undoneCountList;
    private List<String> undoneRatioList;
    private String undoneRatio;

    public StatReport(StatType type) {
        super(type);
    }

    public List<String> getUndoneCountList() {
        return undoneCountList;
    }

    public void setUndoneCountList(List<String> undoneCountList) {
        this.undoneCountList = undoneCountList;
    }

    public List<String> getUndoneRatioList() {
        return undoneRatioList;
    }

    public void setUndoneRatioList(List<String> undoneRatioList) {
        this.undoneRatioList = undoneRatioList;
    }

	public String getUndoneRatio() {
		return undoneRatio;
	}

	public void setUndoneRatio(String undoneRatio) {
		this.undoneRatio = undoneRatio;
	}
    
}

package com.handpay.arch.stat.ics.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by sxjiang on 2016/10/12.
 */
public class StatReport extends Stat {
    private List<BigDecimal> undoneCountList;
    private List<BigDecimal> undoneRatioList;

    protected StatReport(int ordinal) {
        super(ordinal);
    }

    public List<BigDecimal> getUndoneCountList() {
        return undoneCountList;
    }

    public void setUndoneCountList(List<BigDecimal> undoneCountList) {
        this.undoneCountList = undoneCountList;
    }

    public List<BigDecimal> getUndoneRatioList() {
        return undoneRatioList;
    }

    public void setUndoneRatioList(List<BigDecimal> undoneRatioList) {
        this.undoneRatioList = undoneRatioList;
    }
}

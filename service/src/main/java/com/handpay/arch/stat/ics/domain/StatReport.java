package com.handpay.arch.stat.ics.domain;

import java.math.String;
import java.util.List;

/**
 * Created by sxjiang on 2016/10/12.
 */
public class StatReport extends Stat {
    private List<String> undoneCountList;
    private List<String> undoneRatioList;

    public StatReport(int ordinal) {
        super(ordinal);
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
}

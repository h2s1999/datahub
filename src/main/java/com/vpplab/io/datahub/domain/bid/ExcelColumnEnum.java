package com.vpplab.io.datahub.domain.bid;

public enum ExcelColumnEnum {

    전력거래발전기아이디(0),
    전력거래발전기명(1),
    소규모발전기아이디(2),
    정산여부(3),
    발전원(4),
    설비용량(5),
    ESS발전타입(6),
    ESS발전타입명(7);

    private final int order;

    ExcelColumnEnum(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}

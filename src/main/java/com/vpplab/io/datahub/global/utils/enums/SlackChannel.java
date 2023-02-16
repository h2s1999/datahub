package com.vpplab.io.datahub.global.utils.enums;

public enum SlackChannel {
	JEJU_DONGBOK("#51-1_pj_제주동복풍력"),
	CS_WEB("#92_cs_web"),
	DATA_REPORT("#92_data_report"),
	DEV_NOTI("#99_dev_noti"),
	CS_TEST("C03S0VAS1NU");

	public String code;

	SlackChannel(String code) {
		this.code = code;
	}

}


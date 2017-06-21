package com.wso2telco.dep.common.mediation.quota.limit;

public class CurrentQuotaLimit {
	private Integer apiQuotaLimit;
	private Integer appQuotaLimit;
	private Integer spQuotaLimit;

	public Integer getApiQuotaLimit() {
		return apiQuotaLimit;
	}

	public void setApiQuotaLimit(Integer apiQuotaLimit) {
		this.apiQuotaLimit = apiQuotaLimit;
	}

	public Integer getAppQuotaLimit() {
		return appQuotaLimit;
	}

	public void setAppQuotaLimit(Integer appQuotaLimit) {
		this.appQuotaLimit = appQuotaLimit;
	}

	public Integer getSpQuotaLimit() {
		return spQuotaLimit;
	}

	public void setSpQuotaLimit(Integer spQuotaLimit) {
		this.spQuotaLimit = spQuotaLimit;
	}

}

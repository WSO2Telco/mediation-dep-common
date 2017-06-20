package com.wso2telco.dep.common.mediation.quota.limit;

public class QuotaLimits {

	private int spLimit;
	private int appLimit;
	private int apiLimit;

	public static QuotaLimits getQuotaLimitsObj() {

		return new QuotaLimits();
	}

	public int getSpLimit() {
		return spLimit;
	}

	public void setSpLimit(int spLimit) {
		this.spLimit = spLimit;
	}

	public int getAppLimit() {
		return appLimit;
	}

	public void setAppLimit(int appLimit) {
		this.appLimit = appLimit;
	}

	public int getApiLimit() {
		return apiLimit;
	}

	public void setApiLimit(int apiLimit) {
		this.apiLimit = apiLimit;
	}
}
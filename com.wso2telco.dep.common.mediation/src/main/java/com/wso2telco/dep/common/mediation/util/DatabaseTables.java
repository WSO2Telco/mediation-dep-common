package com.wso2telco.dep.common.mediation.util;

public enum DatabaseTables {

	NOTIFY_URL_ENTRY("notify_url"), 
	VALID_PAYMENT_CATEGORIES("valid_payment_categories");

	private String tableName;

	DatabaseTables(String tableName) {

		this.tableName = tableName;
	}

	public String getTableName() {

		return this.tableName;
	}
}

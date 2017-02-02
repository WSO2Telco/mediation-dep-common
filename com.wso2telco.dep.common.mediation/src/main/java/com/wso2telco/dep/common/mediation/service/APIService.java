package com.wso2telco.dep.common.mediation.service;

import com.wso2telco.dep.common.mediation.dao.APIDAO;

public class APIService {

	APIDAO walletDAO;

	{
		walletDAO = new APIDAO();
	}

	public Integer storeServiceProviderNotifyURLService(String apiName,
			String notifyURL, String serviceProvider) throws Exception {

		Integer newId = 0;

		if (apiName == null || apiName.trim().length() <= 0) {

			// should return exception
		}

		if (notifyURL == null || notifyURL.trim().length() <= 0) {

			// should return exception
		}

		if (serviceProvider == null || serviceProvider.trim().length() <= 0) {

			// should return exception
		}

		try {

			walletDAO.insertServiceProviderNotifyURL(apiName, notifyURL,
					serviceProvider);
		} catch (Exception e) {

			throw e;
		}

		return newId;
	}
}

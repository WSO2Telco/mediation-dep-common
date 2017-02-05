package com.wso2telco.dep.common.mediation.service;

import java.util.List;

import com.wso2telco.dep.common.mediation.dao.APIDAO;

public class APIService {

	APIDAO apiDAO;

	{
		apiDAO = new APIDAO();
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

			apiDAO.insertServiceProviderNotifyURL(apiName, notifyURL,
					serviceProvider);
		} catch (Exception e) {

			throw e;
		}

		return newId;
	}

	public void validatePurchaseCategoryCode(String purchaseCategoryCode)
			throws Exception {

		if (purchaseCategoryCode == null
				|| purchaseCategoryCode.trim().length() <= 0) {

			return;
		}

		try {

			boolean isvalid = true;
			List<String> validCategoris = apiDAO.getValidPayCategories();

			if (validCategoris.size() > 0) {

				isvalid = false;
				for (String category : validCategoris) {

					if (category.equalsIgnoreCase(purchaseCategoryCode)) {

						isvalid = true;
						break;
					}
				}
			}

			if (!isvalid) {
				/*
				 * throw new CustomException("POL0001",
				 * "A policy error occurred. Error code is %1", new String[] {
				 * "Invalid " + "purchaseCategoryCode : " + chargeCategory });
				 */
			}
		} catch (Exception e) {

			throw e;
		}
	}
}

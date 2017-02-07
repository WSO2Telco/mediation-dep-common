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

		try {

			newId = apiDAO.insertServiceProviderNotifyURL(apiName, notifyURL,
					serviceProvider);
		} catch (Exception e) {

			throw e;
		}

		return newId;
	}

	public boolean validatePurchaseCategoryCode(String purchaseCategoryCode)
			throws Exception {

		boolean isvalid = true;

		try {

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
		} catch (Exception e) {

			throw e;
		}

		return isvalid;
	}
}

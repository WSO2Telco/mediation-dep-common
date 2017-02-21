package com.wso2telco.dep.common.mediation.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.wso2telco.dep.common.mediation.dao.APIDAO;

public class APIService {

	APIDAO apiDAO;

	{
		apiDAO = new APIDAO();
	}

	public Integer storeServiceProviderNotifyURLService(String apiName,
			String notifyURL, String serviceProvider, String clientCorrelator)
			throws Exception {

		Integer newId = 0;

		try {

			newId = apiDAO.insertServiceProviderNotifyURL(apiName, notifyURL,
					serviceProvider, clientCorrelator);
		} catch (Exception e) {

			throw e;
		}

		return newId;
	}

	public boolean validatePurchaseCategoryCode(String purchaseCategoryCode)
			throws Exception {

		boolean isvalid = true;

		try {

			List<String> validCategoris = apiDAO.getValidPurchaseCategories();

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

	public Map<String, String> getNotificationURLInformation(int notifyurldid)
			throws Exception {

		Map<String, String> notificationURLInformation = null;

		try {

			notificationURLInformation = apiDAO
					.getNotificationURLInformation(notifyurldid);
		} catch (Exception e) {

			throw e;
		}

		if (notificationURLInformation != null) {

			return notificationURLInformation;
		} else {

			return Collections.emptyMap();
		}
	}

	public void updateNotificationURLInformationStatus(int notifyurldid)
			throws Exception {

		try {

			apiDAO.updateNotificationURLInformationStatus(notifyurldid);
		} catch (Exception e) {

			throw e;
		}
	}
}

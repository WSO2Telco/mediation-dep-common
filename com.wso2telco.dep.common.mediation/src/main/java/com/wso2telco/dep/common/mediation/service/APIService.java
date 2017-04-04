package com.wso2telco.dep.common.mediation.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wso2telco.dep.common.mediation.dao.APIDAO;

public class APIService {

	APIDAO apiDAO;
	
	private final Log log = LogFactory.getLog(APIService.class);

	{
		apiDAO = new APIDAO();
	}

	public Integer storeServiceProviderNotifyURLService(String apiName,
			String notifyURL, String serviceProvider, String clientCorrelator, String operatorName)
			throws Exception {

		Integer newId = 0;

		try {

			newId = apiDAO.insertServiceProviderNotifyURL(apiName, notifyURL,
					serviceProvider, clientCorrelator, operatorName);
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
	
	public String getAttributeValueForCode(String tableName, String operatorName, String attributeGroupCode,
			String attributeCode) throws Exception {
		String attributeValue = null;

		try {
			attributeValue = apiDAO.getAttributeValueForCode(tableName, operatorName, attributeGroupCode,
					attributeCode);
		} catch (Exception ex) {
			log.error("Error while retrieving attribute value", ex);
			throw ex;
		}

		return attributeValue;
	}


	public String getAPIId(String apiPublisher, String apiName, String apiVersion) throws Exception {
			String apiId;
			try {
				apiId = apiDAO.getAPIId(apiPublisher,apiName,apiVersion);
			} catch (Exception ex) {
				log.error("Error while retrieving API Id value", ex);
				throw ex;
			}
			return apiId;
	}


	public boolean isBlackListedNumber(String apiId, String msisdn) throws Exception {
		try {
			List<String> msisdnArrayList = apiDAO.readBlacklistNumbers(apiId);
			if(msisdnArrayList.contains(msisdn) || msisdnArrayList.contains("tel3A+" + msisdn)){
				return true;
			}
		} catch (Exception ex) {
			log.error("Error while checking whether the msisdn :" + msisdn + " is blacklisted", ex);
			throw ex;
		}

		return false;
	}

	public String getSubscriptionID(String apiId, String applicationId)
			throws Exception {
		return String.valueOf(apiDAO.getSubscriptionId(apiId, applicationId));
	}


	public boolean isWhiteListed(String MSISDN, String applicationId, String subscriptionId, String apiId) throws
			Exception{
		MSISDN = "tel3A+" + MSISDN;
		return apiDAO.checkWhiteListed(MSISDN, applicationId, subscriptionId, apiId);
	}

	public List<Map<String, Object>> executeCustomSelectQuery(String selectQuery) throws Exception {
		return apiDAO.executeCustomSelectQuery(selectQuery);
	}
	
	public int executeCustomInsertQueryAndGetGeneratedPrimaryKey (String insertQuery) throws Exception {
		return apiDAO.executeCustomInsertQueryAndGetGeneratedPrimaryKey(insertQuery);
	}
}

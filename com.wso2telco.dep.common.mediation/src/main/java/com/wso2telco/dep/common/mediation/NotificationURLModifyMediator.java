package com.wso2telco.dep.common.mediation;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import com.wso2telco.dep.common.mediation.service.APIService;

public class NotificationURLModifyMediator extends AbstractMediator {

	public boolean mediate(MessageContext mc) {

		try {

			Integer id = 0;
			String generatedNotifyURL = null;
			String apiName = mc.getProperty("API_NAME").toString();
			String spNotifyURL = mc.getProperty("notifyURL").toString();
			String notificationURL = mc.getProperty("NOTIFICATION_URL")
					.toString();
			String serviceProvider = mc.getProperty("USER_ID").toString();

			APIService apiService = new APIService();
			id = apiService.storeServiceProviderNotifyURLService(apiName,
					spNotifyURL, serviceProvider);
			generatedNotifyURL = notificationURL + "/" + id;

			mc.setProperty("generatedNotifyURL", generatedNotifyURL);
			mc.setProperty("notifyURLTableId", id);
		} catch (Exception e) {

			log.error("error in NotificationURLModifyMediator mediate : "
					+ e.getMessage());
			mc.setProperty("INTERNAL_ERROR", "true");
		}

		return true;
	}
}

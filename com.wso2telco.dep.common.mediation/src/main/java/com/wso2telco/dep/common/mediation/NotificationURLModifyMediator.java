package com.wso2telco.dep.common.mediation;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import com.wso2telco.dep.common.mediation.service.APIService;

public class NotificationURLModifyMediator extends AbstractMediator {

	public boolean mediate(MessageContext mc) {

		try {

			Integer id = 0;
			String generatedNotifyURL = null;
			String apiName = mc.getProperty("apiName").toString();
			String spNotifyURL = mc.getProperty("notifyURL").toString();
			String notificationURL = mc.getProperty("notificationURL")
					.toString();
			String serviceProvider = mc.getProperty("serviceProvider")
					.toString();

			APIService walletService = new APIService();
			id = walletService.storeServiceProviderNotifyURLService(apiName,
					spNotifyURL, serviceProvider);
			generatedNotifyURL = notificationURL + "/" + id;

			mc.setProperty("generatedNotifyURL", generatedNotifyURL);
			mc.setProperty("notifyURLTAbleId", id);
		} catch (Exception e) {

			log.error("error in NotificationURLModifyMediator mediate : "
					+ e.getMessage());
		}

		return true;
	}
}

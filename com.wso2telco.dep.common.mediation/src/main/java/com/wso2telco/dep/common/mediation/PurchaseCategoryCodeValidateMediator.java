package com.wso2telco.dep.common.mediation;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.wso2telco.dep.common.mediation.service.APIService;

public class PurchaseCategoryCodeValidateMediator extends AbstractMediator {

	public boolean mediate(MessageContext mc) {

		try {

			String purchaseCategoryCode = mc
					.getProperty("purchaseCategoryCode").toString();

			APIService apiService = new APIService();
			apiService.validatePurchaseCategoryCode(purchaseCategoryCode);
		} catch (Exception e) {

			log.error("error in PurchaseCategoryCodeValidateMediator mediate : "
					+ e.getMessage());
		}
		return true;
	}
}

package com.wso2telco.dep.common.mediation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import com.wso2telco.core.mnc.resolver.MNCQueryClient;
import com.wso2telco.core.msisdnvalidator.MSISDN;
import com.wso2telco.core.msisdnvalidator.MSISDNUtil;
import com.wso2telco.dep.operatorservice.model.OperatorEndPointDTO;
import com.wso2telco.dep.operatorservice.service.OparatorService;

public class EndpointRetrieverMediator extends AbstractMediator {

	/** The operatorEndpoints. */
	private List<OperatorEndPointDTO> operatorEndpoints;

	private Set<String> countryLookUpOnHeader = new HashSet<String>();
	private Set<String> validOperators = new HashSet<String>();

	private void setErrorInContext(MessageContext synContext, String messageId,
			String errorText, String errorVariable, String httpStatusCode,
			String exceptionType) {

		synContext.setProperty("messageId", messageId);
		synContext.setProperty("errorText", errorText);
		synContext.setProperty("errorVariable", errorVariable);
		synContext.setProperty("httpStatusCode", httpStatusCode);
		synContext.setProperty("exceptionType", exceptionType);
	}

	public boolean mediate(MessageContext synContext) {

		MNCQueryClient mncQueryclient = new MNCQueryClient();
		MSISDNUtil phoneUtil = new MSISDNUtil();

		try {

			String operator = null;
			operatorEndpoints = new OparatorService().getOperatorEndpoints();
			String apiName = synContext.getProperty("API_NAME").toString();
			String requestMSISDN = synContext.getProperty("MSISDN").toString();
			String countryCodes = synContext.getProperty(
					"SEARCH_OPERATOR_ON_HEADER").toString();
			String headerOperatorName = synContext.getProperty("OPERATOR")
					.toString();
			String validOperatorList = synContext
					.getProperty("VALID_OPERATORS").toString();
			String resourcePath = synContext.getProperty("RESOURCE_PATH")
					.toString();

			/**
			 * MSISDN provided at JSon body convert into Phone number object.
			 */
			MSISDN numberProto = phoneUtil.parse(requestMSISDN);

			/**
			 * obtain the country code form the phone number object
			 */
			int countryCode = numberProto.getCountryCode();

			loadCountryCodeList(countryCodes);

			/**
			 * if the country code within the header look up context , the
			 * operator taken from the header object
			 */
			if (countryLookUpOnHeader.contains(String.valueOf(countryCode))) {

				if (headerOperatorName != null
						&& headerOperatorName.trim().length() > 0) {

					operator = headerOperatorName;
					log.debug("operator pick from the Header : " + operator);
				} else {

					log.debug("the request doesnot obtain operator from the header");
				}
			}

			/**
			 * build the MSISDN
			 */
			StringBuffer msisdn = new StringBuffer();
			msisdn.append("+").append(numberProto.getCountryCode())
					.append(numberProto.getNationalNumber());

			/**
			 * if the operator still not selected the operator selection logic
			 * goes as previous. ie select from MCC_NUMBER_RANGE
			 */
			if (operator == null) {

				String mcc = null;

				// mcc not known in mediator
				log.debug("unable to obtain operator from the header and check for mcc_number_range table"
						+ operator
						+ " mcc :"
						+ mcc
						+ "msisdn: "
						+ msisdn.toString());
				operator = mncQueryclient.QueryNetwork(mcc, msisdn.toString());
			}

			if (operator == null) {

				setErrorInContext(synContext, "SVC0001",
						"A service error occurred. Error code is %1",
						"No valid operator found for given MSISDN", "400",
						"SERVICE_EXCEPTION");
				synContext.setProperty("ENDPOINT_ERROR", "true");
			}

			loadValidOperatorList(validOperatorList);

			if (!validOperators.contains(String.valueOf(operator))) {

				setErrorInContext(synContext, "SVC0001",
						"A service error occurred. Error code is %1",
						"Requested service is not provisioned", "400",
						"SERVICE_EXCEPTION");
				synContext.setProperty("ENDPOINT_ERROR", "true");
			}

			OperatorEndPointDTO validOperatorendpoint = getValidEndpoints(
					apiName, operator);

			if (validOperatorendpoint == null) {

				setErrorInContext(synContext, "SVC0001",
						"A service error occurred. Error code is %1",
						"Requested service is not provisioned", "400",
						"SERVICE_EXCEPTION");
				synContext.setProperty("ENDPOINT_ERROR", "true");
			}

			String apiEndpoint = validOperatorendpoint.getEndpoint()
					+ resourcePath;
			synContext.setProperty("API_ENDPOINT", apiEndpoint);
			synContext.setProperty("OPERATOR_NAME", operator.toUpperCase());
		} catch (Exception e) {

			log.error("error in EndpointRetrieverMediator mediate : "
					+ e.getMessage());
			setErrorInContext(
					synContext,
					"SVC0001",
					"A service error occurred. Error code is %1",
					"An internal service error has occured. Please try again later.",
					"500", "SERVICE_EXCEPTION");
			synContext.setProperty("INTERNAL_ERROR", "true");
		}

		return true;
	}

	private void loadCountryCodeList(String countries) {

		if (countries != null) {

			// Split the comma separated country codes
			String[] countryArray = countries.split(",");
			for (String country : countryArray) {

				countryLookUpOnHeader.add(country.trim());
			}
		}
	}

	private void loadValidOperatorList(String validOperatorList) {

		if (validOperatorList != null) {

			// Split the comma separated operators
			String[] operatorArray = validOperatorList.split(",");
			for (String operator : operatorArray) {

				validOperators.add(operator.trim());
			}
		}
	}

	/**
	 * Gets the valid endpoints.
	 *
	 * @param api
	 *            the api
	 * @param validoperator
	 *            the validOperator
	 * @return the valid endpoints
	 */
	private OperatorEndPointDTO getValidEndpoints(String api,
			String validOperator) {

		OperatorEndPointDTO validoperendpoint = null;

		for (OperatorEndPointDTO d : operatorEndpoints) {

			if ((d.getApi().equalsIgnoreCase(api))
					&& (validOperator.equalsIgnoreCase(d.getOperatorcode()))) {

				validoperendpoint = d;
				break;
			}
		}

		return validoperendpoint;
	}
}

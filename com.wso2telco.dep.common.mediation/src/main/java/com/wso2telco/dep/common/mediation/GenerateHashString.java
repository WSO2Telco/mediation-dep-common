package com.wso2telco.dep.common.mediation;

import java.security.MessageDigest;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class GenerateHashString extends AbstractMediator { 

	@Override
	public boolean mediate(MessageContext context) { 
		
		String hashText = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hashedBytes = digest.digest(context.getMessageString().getBytes("UTF-8"));
			org.apache.axis2.context.MessageContext a2mc = ((Axis2MessageContext) context)
					.getAxis2MessageContext();
			String jsonPayloadToString = JsonUtil.jsonPayloadToString(a2mc);
			
			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i < hashedBytes.length; i++) {
				stringBuffer.append(Integer.toString(
						(hashedBytes[i] & 0xff) + 0x100, 16).substring(1));
			}

			hashText = stringBuffer.toString();
		} catch (Exception e) {

			log.error("Error in getHashString : " + e.getMessage());
			//throw new Exception("SVC1000");
		}
		return true;

	}
}

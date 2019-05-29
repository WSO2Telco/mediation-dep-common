package com.wso2telco.dep.common.mediation;

import org.apache.axis2.context.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Copyright (c) 2019, WSO2.Telco Inc. (http://www.wso2telco.com) All Rights Reserved.
 * <p>
 * WSO2.Telco Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class UserMaskingMediatorTest {

    @Test
    public void testMediate_whenHeaderisNotNull() {
        try {
            MessageContext mockMessageContext = Mockito.mock(MessageContext.class);
            Axis2MessageContext mockAxis2MessageContext = Mockito.mock(Axis2MessageContext.class);

            String payload = "{\"amountTransaction\":{\"endUserId\":\"tel:+SBcRDj/+M108gFCu1S56zw==\"," +
                    "\"transactionOperationStatus\":\"Charged\",\"clientCorrelator\":\"TES35cctrd25\"," +
                    "\"referenceCode\":\"REF-TEce2dfdwe\",\"paymentAmount\":{\"chargingInformation\":{" +
                    "\"amount\":\"100\",\"description\":\"Alien Invaders Game\",\"currency\":\"USD\"}," +
                    "\"chargingMetaData\":{\"channel\":\"sms\",\"onBehalfOf\":\"Merchant\",\"taxAmount\":\"0\"}}}}";
            InputStream is = new ByteArrayInputStream(payload.getBytes());
            Mockito.when(mockMessageContext.getProperty("org.apache.synapse.commons.json.JsonInputStream")).thenReturn(is);
            Mockito.when(mockAxis2MessageContext.getAxis2MessageContext()).thenReturn(mockMessageContext);
            Mockito.when(mockMessageContext.getProperty(MessageContext.TRANSPORT_HEADERS)).thenReturn(new HashMap<>());

            UserMaskingMediator userMaskingMediator = new UserMaskingMediator();
            boolean result = userMaskingMediator.mediate(mockAxis2MessageContext);
            Assert.assertEquals(true, result);

        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testMediate_whenHeaderisNull() {
        try {
            MessageContext mockMessageContext = Mockito.mock(MessageContext.class);
            Axis2MessageContext mockAxis2MessageContext = Mockito.mock(Axis2MessageContext.class);

            String payload = "{\"amountTransaction\":{\"endUserId\":\"tel:+SBcRDj/+M108gFCu1S56zw==\"," +
                    "\"transactionOperationStatus\":\"Charged\",\"clientCorrelator\":\"TES35cctrd25\"," +
                    "\"referenceCode\":\"REF-TEce2dfdwe\",\"paymentAmount\":{\"chargingInformation\":{" +
                    "\"amount\":\"100\",\"description\":\"Alien Invaders Game\",\"currency\":\"USD\"}," +
                    "\"chargingMetaData\":{\"channel\":\"sms\",\"onBehalfOf\":\"Merchant\",\"taxAmount\":\"0\"}}}}";
            InputStream is = new ByteArrayInputStream(payload.getBytes());
            Mockito.when(mockMessageContext.getProperty("org.apache.synapse.commons.json.JsonInputStream")).thenReturn(is);
            Mockito.when(mockAxis2MessageContext.getAxis2MessageContext()).thenReturn(mockMessageContext);
            Mockito.when(mockMessageContext.getProperty(MessageContext.TRANSPORT_HEADERS)).thenReturn(null);

            UserMaskingMediator userMaskingMediator = new UserMaskingMediator();
            boolean result = userMaskingMediator.mediate(mockAxis2MessageContext);
            Assert.assertEquals(true, result);

        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testMediate_whenHandlerIsNull(){
        try {

            MessageContext mockMessageContext = Mockito.mock(MessageContext.class);
            Axis2MessageContext mockAxis2MessageContext = Mockito.mock(Axis2MessageContext.class);

            String payload = "{\"amountTransaction\":{\"endUserId\":\"tel:+SBcRDj/+M108gFCu1S56zw==\"," +
                    "\"transactionOperationStatus\":\"Charged\",\"clientCorrelator\":\"TES35cctrd25\"," +
                    "\"referenceCode\":\"REF-TEce2dfdwe\",\"paymentAmount\":{\"chargingInformation\":{" +
                    "\"amount\":\"100\",\"description\":\"Alien Invaders Game\",\"currency\":\"USD\"}," +
                    "\"chargingMetaData\":{\"channel\":\"sms\",\"onBehalfOf\":\"Merchant\",\"taxAmount\":\"0\"}}}}";
            InputStream is = new ByteArrayInputStream(payload.getBytes());
            Mockito.when(mockMessageContext.getProperty("org.apache.synapse.commons.json.JsonInputStream")).thenReturn(is);
            Mockito.when(mockAxis2MessageContext.getAxis2MessageContext()).thenReturn(mockMessageContext);
            Mockito.when(mockMessageContext.getProperty(MessageContext.TRANSPORT_HEADERS)).thenReturn(new HashMap<>());
            Mockito.when(mockMessageContext.getProperty("handler")).thenReturn(null);

            UserMaskingMediator userMaskingMediator = new UserMaskingMediator();
            boolean result = userMaskingMediator.mediate(mockAxis2MessageContext);
            Assert.assertEquals(true, result);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testMediate_whenHandlerIsAmountChargeHandler_anonymizeIsTrue() {
        try {

            MessageContext mockMessageContext = Mockito.mock(MessageContext.class);
            Axis2MessageContext mockAxis2MessageContext = Mockito.mock(Axis2MessageContext.class);

            String payload = "{\"amountTransaction\":{\"endUserId\":\"tel:+94777323228\",\"serverReferenceCode\":\"ABC-123\"," +
                    "\"resourceURL\":\"https://gateway1a.mife.sla-mobile.com.my:8243/payment/v1/94777323228/transactions/amount/1558087751564PA6900\"," +
                    "\"transactionOperationStatus\":\"Charged\",\"clientCorrelator\":\"TES35cctrd25\",\"referenceCode\":\"REF-12345\"," +
                    "\"paymentAmount\":{\"totalAmountCharged\":\"12.99\",\"chargingInformation\":{\"amount\":\"10\"," +
                    "\"description\":\"Alien Invaders Game\",\"currency\":\"USD\"},\"chargingMetaData\":{\"purchaseCategoryCode\":\"Game\"," +
                    "\"onBehalfOf\":\"Example Games Inc\",\"channel\":\"WAP\",\"taxAmount\":\"0\"}}}}";
            InputStream is = new ByteArrayInputStream(payload.getBytes());
            Mockito.when(mockMessageContext.getProperty("org.apache.synapse.commons.json.JsonInputStream")).thenReturn(is);
            Mockito.when(mockAxis2MessageContext.getAxis2MessageContext()).thenReturn(mockMessageContext);
            Mockito.when(mockMessageContext.getProperty(MessageContext.TRANSPORT_HEADERS)).thenReturn(new HashMap<>());
            Mockito.when(mockAxis2MessageContext.getProperty("handler")).thenReturn("AmountChargeHandler");
            Mockito.when(mockAxis2MessageContext.getProperty("MASKED_MSISDN_SUFFIX")).thenReturn("SBcRDj/+M108gFCu1S56zw==");
            Mockito.when(mockAxis2MessageContext.getProperty("UserMSISDN")).thenReturn("94777323228");
            Mockito.when(mockAxis2MessageContext.getProperty("anonymize")).thenReturn("true");
            Mockito.when(mockAxis2MessageContext.getProperty("MASKED_MSISDN")).thenReturn("tel:+SBcRDj/+M108gFCu1S56zw==");
            Mockito.when(mockAxis2MessageContext.getProperty("MASKED_RESOURCE")).thenReturn("");
            Mockito.when(mockAxis2MessageContext.getProperty("MSISDN")).thenReturn("tel:+94777323228");

            UserMaskingMediator userMaskingMediator = new UserMaskingMediator();
            boolean result = userMaskingMediator.mediate(mockAxis2MessageContext);
            Assert.assertEquals(true, result);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testMediate_whenHandlerIsAmountChargeHandler_anonymizeIsFalse() {
        try {

            MessageContext mockMessageContext = Mockito.mock(MessageContext.class);
            Axis2MessageContext mockAxis2MessageContext = Mockito.mock(Axis2MessageContext.class);

            String payload = "{\"amountTransaction\":{\"endUserId\":\"tel:+SBcRDj/+M108gFCu1S56zw==\"," +
                    "\"transactionOperationStatus\":\"Charged\",\"clientCorrelator\":\"TES35cctrd25\"," +
                    "\"referenceCode\":\"REF-TEce2dfdwe\",\"paymentAmount\":{\"chargingInformation\":{" +
                    "\"amount\":\"100\",\"description\":\"Alien Invaders Game\",\"currency\":\"USD\"}," +
                    "\"chargingMetaData\":{\"channel\":\"sms\",\"onBehalfOf\":\"Merchant\",\"taxAmount\":\"0\"}}}}";
            InputStream is = new ByteArrayInputStream(payload.getBytes());
            Mockito.when(mockMessageContext.getProperty("org.apache.synapse.commons.json.JsonInputStream")).thenReturn(is);
            Mockito.when(mockAxis2MessageContext.getAxis2MessageContext()).thenReturn(mockMessageContext);
            Mockito.when(mockMessageContext.getProperty(MessageContext.TRANSPORT_HEADERS)).thenReturn(new HashMap<>());
            Mockito.when(mockAxis2MessageContext.getProperty("handler")).thenReturn("AmountChargeHandler");
            Mockito.when(mockAxis2MessageContext.getProperty("MASKED_MSISDN_SUFFIX")).thenReturn("SBcRDj/+M108gFCu1S56zw==");
            Mockito.when(mockAxis2MessageContext.getProperty("UserMSISDN")).thenReturn("94777323228");
            Mockito.when(mockAxis2MessageContext.getProperty("anonymize")).thenReturn("false");
            Mockito.when(mockAxis2MessageContext.getProperty("MASKED_MSISDN")).thenReturn("tel:+SBcRDj/+M108gFCu1S56zw==");
            Mockito.when(mockAxis2MessageContext.getProperty("MASKED_RESOURCE")).thenReturn("");
            Mockito.when(mockAxis2MessageContext.getProperty("MSISDN")).thenReturn("tel:+94777323228");

            UserMaskingMediator userMaskingMediator = new UserMaskingMediator();
            boolean result = userMaskingMediator.mediate(mockAxis2MessageContext);
            Assert.assertEquals(true, result);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testMediate_whenHandlerIsAmountChargeHandler_amountTransactionIsNull() {
        try {

            MessageContext mockMessageContext = Mockito.mock(MessageContext.class);
            Axis2MessageContext mockAxis2MessageContext = Mockito.mock(Axis2MessageContext.class);

            String payload = "{\"amountTransaction\":\"\"}";
            InputStream is = new ByteArrayInputStream(payload.getBytes());
            Mockito.when(mockMessageContext.getProperty("org.apache.synapse.commons.json.JsonInputStream")).thenReturn(is);
            Mockito.when(mockAxis2MessageContext.getAxis2MessageContext()).thenReturn(mockMessageContext);
            Mockito.when(mockMessageContext.getProperty(MessageContext.TRANSPORT_HEADERS)).thenReturn(new HashMap<>());
            Mockito.when(mockAxis2MessageContext.getProperty("handler")).thenReturn("AmountChargeHandler");

            UserMaskingMediator userMaskingMediator = new UserMaskingMediator();
            boolean result = userMaskingMediator.mediate(mockAxis2MessageContext);
            Assert.assertEquals(true, result);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testMediate_whenHandlerIsSendSMSHandler_outboundSMSMessageRequest_anonymizeTrue(){
        MessageContext mockMessageContext = Mockito.mock(MessageContext.class);
        Axis2MessageContext mockAxis2MessageContext = Mockito.mock(Axis2MessageContext.class);

        String payload = "{\"outboundSMSMessageRequest\":{\"senderAddress\":\"tel:+7555\",\"senderName\":\"ACME Inc.\"," +
                "\"address\":[\"tel:+00123456788\"],\"resourceURL\":\"http://example.com/smsmessaging/v1/outbound/tel:+7555/request/1558073724312SM6901\"," +
                "\"receiptRequest\":{\"callbackData\":\"some-data-useful-to-the-requester\"," +
                "\"notifyURL\":\"http://application.example.com/notifications/DeliveryInfoNotification\"}," +
                "\"deliveryInfoList\":{\"deliveryInfo\":[{\"address\":\"tel:+00123456788\",\"deliveryStatus\":\"DeliveredToTerminal\"}]," +
                "\"resourceURL\":\"https://gateway1a.mife.sla-mobile.com.my:8243/smsmessaging/v1/outbound/tel%3A%2B7555/requests/1558073724312SM6901/deliveryInfos\"}," +
                "\"outboundSMSTextMessage\":{\"message\":\"Hello World\"},\"clientCorrelator\":\"scs1211\"}}";
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        Mockito.when(mockMessageContext.getProperty("org.apache.synapse.commons.json.JsonInputStream")).thenReturn(is);
        Mockito.when(mockAxis2MessageContext.getAxis2MessageContext()).thenReturn(mockMessageContext);
        Mockito.when(mockMessageContext.getProperty(MessageContext.TRANSPORT_HEADERS)).thenReturn(new HashMap<>());
        Mockito.when(mockAxis2MessageContext.getProperty("handler")).thenReturn("SendSMSHandler");
        Mockito.when(mockMessageContext.getProperty("anonymize")).thenReturn("true");
        Mockito.when(mockMessageContext.getProperty("MASKED_MSISDN_LIST")).thenReturn(new String[]{"tel:+00123456788"});
        Mockito.when(mockMessageContext.getProperty("SMS_RESOURCE")).thenReturn("");
        Mockito.when(mockMessageContext.getProperty("MASKED_MSISDN_SUFFIX_MAP")).thenReturn(new HashMap<>());

        UserMaskingMediator userMaskingMediator = new UserMaskingMediator();
        boolean result = userMaskingMediator.mediate(mockAxis2MessageContext);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testMediate_whenHandlerIsSendSMSHandler_outboundSMSMessageRequest_anonymizeFalse(){
        MessageContext mockMessageContext = Mockito.mock(MessageContext.class);
        Axis2MessageContext mockAxis2MessageContext = Mockito.mock(Axis2MessageContext.class);

        String payload = "{\"outboundSMSMessageRequest\":{\"senderAddress\":\"tel:+7555\",\"senderName\":\"ACME Inc.\"," +
                "\"address\":[\"tel:+00123456788\"],\"resourceURL\":\"http://example.com/smsmessaging/v1/outbound/tel:+7555/request/1558073724312SM6901\"," +
                "\"receiptRequest\":{\"callbackData\":\"some-data-useful-to-the-requester\"," +
                "\"notifyURL\":\"http://application.example.com/notifications/DeliveryInfoNotification\"}," +
                "\"deliveryInfoList\":{\"deliveryInfo\":[{\"address\":\"tel:+00123456788\",\"deliveryStatus\":\"DeliveredToTerminal\"}]," +
                "\"resourceURL\":\"https://gateway1a.mife.sla-mobile.com.my:8243/smsmessaging/v1/outbound/tel%3A%2B7555/requests/1558073724312SM6901/deliveryInfos\"}," +
                "\"outboundSMSTextMessage\":{\"message\":\"Hello World\"},\"clientCorrelator\":\"scs1211\"}}";
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        Mockito.when(mockMessageContext.getProperty("org.apache.synapse.commons.json.JsonInputStream")).thenReturn(is);
        Mockito.when(mockAxis2MessageContext.getAxis2MessageContext()).thenReturn(mockMessageContext);
        Mockito.when(mockMessageContext.getProperty(MessageContext.TRANSPORT_HEADERS)).thenReturn(new HashMap<>());
        Mockito.when(mockAxis2MessageContext.getProperty("handler")).thenReturn("SendSMSHandler");
        Mockito.when(mockAxis2MessageContext.getProperty("anonymize")).thenReturn("false");
        Mockito.when(mockAxis2MessageContext.getProperty("MSISDN_LIST")).thenReturn(new String[]{"tel:+00123456788"});
        Mockito.when(mockAxis2MessageContext.getProperty("MASKED_MSISDN_SUFFIX_MAP")).thenReturn(new HashMap<>());

        UserMaskingMediator userMaskingMediator = new UserMaskingMediator();
        boolean result = userMaskingMediator.mediate(mockAxis2MessageContext);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testMediate_whenHandlerIsSendSMSHandler_addressIsNull(){
        MessageContext mockMessageContext = Mockito.mock(MessageContext.class);
        Axis2MessageContext mockAxis2MessageContext = Mockito.mock(Axis2MessageContext.class);

        String payload = "{\"outboundSMSMessageRequest\":{\"senderAddress\":\"tel:+7555\",\"senderName\":\"ACME Inc.\"," +
                "\"address\":null,\"resourceURL\":\"http://example.com/smsmessaging/v1/outbound/tel:+7555/request/1558073724312SM6901\"," +
                "\"receiptRequest\":{\"callbackData\":\"some-data-useful-to-the-requester\"," +
                "\"notifyURL\":\"http://application.example.com/notifications/DeliveryInfoNotification\"}," +
                "\"deliveryInfoList\":{\"deliveryInfo\":[{\"address\":\"tel:+00123456788\",\"deliveryStatus\":\"DeliveredToTerminal\"}]," +
                "\"resourceURL\":\"https://gateway1a.mife.sla-mobile.com.my:8243/smsmessaging/v1/outbound/tel%3A%2B7555/requests/1558073724312SM6901/deliveryInfos\"}," +
                "\"outboundSMSTextMessage\":{\"message\":\"Hello World\"},\"clientCorrelator\":\"scs1211\"}}";
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        Mockito.when(mockMessageContext.getProperty("org.apache.synapse.commons.json.JsonInputStream")).thenReturn(is);
        Mockito.when(mockAxis2MessageContext.getAxis2MessageContext()).thenReturn(mockMessageContext);
        Mockito.when(mockMessageContext.getProperty(MessageContext.TRANSPORT_HEADERS)).thenReturn(new HashMap<>());
        Mockito.when(mockAxis2MessageContext.getProperty("handler")).thenReturn("SendSMSHandler");
        Mockito.when(mockAxis2MessageContext.getProperty("anonymize")).thenReturn("true");
        Mockito.when(mockAxis2MessageContext.getProperty("MASKED_MSISDN_LIST")).thenReturn(new String[]{"tel:+00123456788"});
        Mockito.when(mockAxis2MessageContext.getProperty("SMS_RESOURCE")).thenReturn("");
        Mockito.when(mockAxis2MessageContext.getProperty("MASKED_MSISDN_SUFFIX_MAP")).thenReturn(new HashMap<>());

        UserMaskingMediator userMaskingMediator = new UserMaskingMediator();
        boolean result = userMaskingMediator.mediate(mockAxis2MessageContext);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testMediate_whenHandlerIsSendSMSHandler_deliveryInfoListIsNull(){
        MessageContext mockMessageContext = Mockito.mock(MessageContext.class);
        Axis2MessageContext mockAxis2MessageContext = Mockito.mock(Axis2MessageContext.class);

        String payload = "{\"outboundSMSMessageRequest\":{\"senderAddress\":\"tel:+7555\",\"senderName\":\"ACME Inc.\"," +
                "\"address\":[\"tel:+00123456788\"],\"resourceURL\":\"http://example.com/smsmessaging/v1/outbound/tel:+7555/request/1558073724312SM6901\"," +
                "\"receiptRequest\":{\"callbackData\":\"some-data-useful-to-the-requester\"," +
                "\"notifyURL\":\"http://application.example.com/notifications/DeliveryInfoNotification\"}," +
                "\"deliveryInfoList\":null,\"outboundSMSTextMessage\":{\"message\":\"Hello World\"},\"clientCorrelator\":\"scs1211\"}}";
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        Mockito.when(mockMessageContext.getProperty("org.apache.synapse.commons.json.JsonInputStream")).thenReturn(is);
        Mockito.when(mockAxis2MessageContext.getAxis2MessageContext()).thenReturn(mockMessageContext);
        Mockito.when(mockMessageContext.getProperty(MessageContext.TRANSPORT_HEADERS)).thenReturn(new HashMap<>());
        Mockito.when(mockAxis2MessageContext.getProperty("handler")).thenReturn("SendSMSHandler");

        UserMaskingMediator userMaskingMediator = new UserMaskingMediator();
        boolean result = userMaskingMediator.mediate(mockAxis2MessageContext);
        Assert.assertEquals(true, result);
    }


}

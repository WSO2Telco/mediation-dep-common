package com.wso2telco.dep.common.mediation.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wso2telco.core.dbutils.DbUtils;
import com.wso2telco.core.dbutils.util.DataSourceNames;
import com.wso2telco.dep.common.mediation.util.DatabaseTables;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class APIDAO {

	private final Log log = LogFactory.getLog(APIDAO.class);

	public Integer insertServiceProviderNotifyURL(String apiName,
			String notifyURL, String serviceProvider, String clientCorrelator)
			throws SQLException, Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer newId = 0;

		try {

			con = DbUtils.getDbConnection(DataSourceNames.WSO2TELCO_DEP_DB);
			if (con == null) {

				throw new Exception("Connection not found");
			}

			StringBuilder insertQueryString = new StringBuilder("INSERT INTO ");
			insertQueryString.append(DatabaseTables.NOTIFICATION_URLS);
			insertQueryString
					.append(" (apiname, notifyurl, serviceprovider, clientCorrelator) ");
			insertQueryString.append("VALUES (?, ?, ?, ?)");

			ps = con.prepareStatement(insertQueryString.toString(),
					Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, apiName);
			ps.setString(2, notifyURL);
			ps.setString(3, serviceProvider);
			ps.setString(4, clientCorrelator);

			log.debug("sql query in insertServiceProviderNotifyURL : " + ps);

			ps.executeUpdate();

			rs = ps.getGeneratedKeys();

			while (rs.next()) {

				newId = rs.getInt(1);
			}
		} catch (SQLException e) {

			log.error(
					"database operation error in insertServiceProviderNotifyURL : ",
					e);
			throw e;
		} catch (Exception e) {

			log.error("error in insertServiceProviderNotifyURL : ", e);
			throw e;
		} finally {

			DbUtils.closeAllConnections(ps, con, rs);
		}

		return newId;
	}

	public List<String> getValidPurchaseCategories() throws SQLException,
			Exception {

		Connection con = DbUtils
				.getDbConnection(DataSourceNames.WSO2TELCO_DEP_DB);
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> categories = new ArrayList<String>();

		try {

			if (con == null) {

				throw new Exception("Connection not found");
			}

			StringBuilder queryString = new StringBuilder(
					"SELECT id, category ");
			queryString.append("FROM ");
			queryString.append(DatabaseTables.VALID_PAYMENT_CATEGORIES);

			ps = con.prepareStatement(queryString.toString());

			log.debug("sql query in getValidPurchaseCategories : " + ps);

			rs = ps.executeQuery();

			while (rs.next()) {

				categories.add(rs.getString("category"));
			}
		} catch (SQLException e) {

			log.error(
					"database operation error in getValidPurchaseCategories : ",
					e);
			throw e;
		} catch (Exception e) {

			log.error("error in getValidPurchaseCategories : ", e);
			throw e;
		} finally {

			DbUtils.closeAllConnections(ps, con, rs);
		}

		return categories;
	}

	public Map<String, String> getNotificationURLInformation(int notifyurldid)
			throws SQLException, Exception {

		Connection con = DbUtils
				.getDbConnection(DataSourceNames.WSO2TELCO_DEP_DB);
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, String> notificationURLInformation = new HashMap<String, String>();

		try {

			if (con == null) {

				throw new Exception("Connection not found");
			}

			StringBuilder queryString = new StringBuilder(
					"SELECT apiname, notifyurl, serviceprovider, notifystatus, clientCorrelator ");
			queryString.append("FROM ");
			queryString.append(DatabaseTables.NOTIFICATION_URLS);
			queryString.append(" WHERE notifyurldid = ?");

			ps = con.prepareStatement(queryString.toString());

			ps.setInt(1, notifyurldid);

			log.debug("sql query in getNotificationURLInformation : " + ps);

			rs = ps.executeQuery();

			while (rs.next()) {

				notificationURLInformation.put("apiname",
						rs.getString("apiname"));
				notificationURLInformation.put("notifyurl",
						rs.getString("notifyurl"));
				notificationURLInformation.put("serviceprovider",
						rs.getString("serviceprovider"));
				notificationURLInformation.put("notifystatus",
						String.valueOf(rs.getInt("notifystatus")));
				notificationURLInformation.put("clientCorrelator",
						String.valueOf(rs.getInt("clientCorrelator")));
			}
		} catch (SQLException e) {

			log.error(
					"database operation error in getNotificationURLInformation : ",
					e);
			throw e;
		} catch (Exception e) {

			log.error("error in getNotificationURLInformation : ", e);
			throw e;
		} finally {

			DbUtils.closeAllConnections(ps, con, rs);
		}

		return notificationURLInformation;
	}

	public void updateNotificationURLInformationStatus(int notifyurldid)
			throws SQLException, Exception {

		Connection con = DbUtils
				.getDbConnection(DataSourceNames.WSO2TELCO_DEP_DB);
		PreparedStatement ps = null;

		try {

			if (con == null) {

				throw new Exception("Connection not found");
			}

			StringBuilder queryString = new StringBuilder("UPDATE ");
			queryString.append(DatabaseTables.NOTIFICATION_URLS);
			queryString.append(" SET notifystatus = ?");
			queryString.append(" WHERE notifyurldid = ?");

			ps = con.prepareStatement(queryString.toString());

			ps.setInt(1, 1);
			ps.setInt(2, notifyurldid);

			log.debug("sql query in updateNotificationURLInformationStatus : "
					+ ps);

			ps.execute();
		} catch (SQLException e) {

			log.error(
					"database operation error in updateNotificationURLInformationStatus : ",
					e);
			throw e;
		} catch (Exception e) {

			log.error("error in updateNotificationURLInformationStatus : ", e);
			throw e;
		} finally {

			DbUtils.closeAllConnections(ps, con, null);
		}
	}
	
	public String getAttributeValueForCode(String tableName, String operatorName, String attributeGroupCode,
			String attributeCode) throws Exception {

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String attributeValue = null;
		Connection connection = null;

		try {

			connection = DbUtils.getDbConnection(DataSourceNames.WSO2TELCO_DEP_DB);

			StringBuilder queryBuilder = new StringBuilder();

			queryBuilder.append("select map.VALUE as attributeValue from ");
			queryBuilder.append(DatabaseTables.MDXATTRIBGROUP +" attgroup, ");
			queryBuilder.append(DatabaseTables.MDXATTRIBUTE + " attribute, ");
			queryBuilder.append(DatabaseTables.MDTATTRIBUTEMAP + " map, ");
			queryBuilder.append(DatabaseTables.OPERATORS + " operator ");
			queryBuilder.append(" where ");
			queryBuilder.append(" attgroup.GROUPDID = attribute.GROUPDID ");
			queryBuilder.append(" and attribute.ATTRIBUTEDID = map.ATTRIBUTEDID ");
			queryBuilder.append(" and map.OWNERDID = operator.id ");
			queryBuilder.append(" and lower(map.TOBJECT) = ? ");
			queryBuilder.append(" and lower(operator.operatorname) = ? ");
			queryBuilder.append(" and lower(attribute.code) = ? ");
			queryBuilder.append(" and lower(attgroup.CODE) = ? ");

			statement = connection.prepareStatement(queryBuilder.toString());
			statement.setString(1, tableName.toLowerCase());
			statement.setString(2, operatorName.toLowerCase());
			statement.setString(3, attributeCode.toLowerCase());
			statement.setString(4, attributeGroupCode.toLowerCase());

			resultSet = statement.executeQuery();

			if (resultSet.next() && resultSet.getString("attributeValue") != null) {
				attributeValue = resultSet.getString("attributeValue");
			}

		} catch (Exception ex) {
			log.error("database operation error in getAttributeValueForCode :", ex);
			throw ex;
		} finally {
			DbUtils.closeAllConnections(statement, connection, resultSet);
		}

		return attributeValue;
	}



	public String getAPIId(String apiPublisher, String apiName, String apiVersion) throws Exception{

		String apiId = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Connection connection = null;

		try {


			StringBuilder queryBuilder = new StringBuilder();

			queryBuilder.append("select API_ID FROM ");
			queryBuilder.append(DatabaseTables.AM_API + "  ");
			queryBuilder.append(" where ");
			queryBuilder.append(" API_PROVIDER = ? ");
			queryBuilder.append(" AND API_NAME = ? ");
			queryBuilder.append(" AND API_VERSION = ? ");

			connection = DbUtils.getDbConnection(DataSourceNames.WSO2AM_DB);

			statement = connection.prepareStatement(queryBuilder.toString());
			statement.setString(1, apiPublisher);
			statement.setString(2, apiName);
			statement.setString(3, apiVersion);

			resultSet = statement.executeQuery();

			if (resultSet.next() && resultSet.getString("API_ID") != null) {
				apiId = resultSet.getString("API_ID");
			}

		} catch (Exception ex) {
			log.error("database operation error in API_ID :", ex);
			throw ex;
		} finally {
			DbUtils.closeAllConnections(statement, connection, resultSet);
		}

		return apiId;
	}

	/**
	 * Read blacklist numbers.
	 *
	 * @param apiId the api name
	 * @return the list
	 * @throws Exception the all exceptions
	 */
	public List<String> readBlacklistNumbers(String apiId) throws Exception {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<String> msisdnArrayList = new ArrayList<String>();

		try {
			StringBuilder queryBuilder = new StringBuilder();

			queryBuilder.append("select * FROM ");
			queryBuilder.append(DatabaseTables.BLACKLIST_MSISDN + "  ");
			queryBuilder.append(" where ");
			queryBuilder.append(" API_ID = ? ");
			//queryBuilder.append(" AND MSISDN = ? ");

			connection = DbUtils.getDbConnection(DataSourceNames.WSO2AM_STATS_DB);

			preparedStatement = connection.prepareStatement(queryBuilder.toString());
			preparedStatement.setString(1, apiId);

			resultSet = preparedStatement.executeQuery();

			if (resultSet != null) {
				while (resultSet.next()) {
					String msisdnTable = resultSet.getString("MSISDN").replace("tel3A+", "");
					log.debug("msisdn in the table = " + msisdnTable);
					msisdnArrayList.add(msisdnTable);
				}
			}

		} catch (Exception ex) {
			log.error("database operation error in API_ID :", ex);
			throw ex;
		} finally {
			DbUtils.closeAllConnections(preparedStatement, connection, resultSet);
		}
		return msisdnArrayList;
	}

	public int getSubscriptionId(String apiID, String applicationID) throws Exception {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("SELECT SUBS.SUBSCRIPTION_ID AS SUBSCRIPTION_ID FROM ");
		queryBuilder.append(DatabaseTables.AM_SUBSCRIPTION +" SUBS, ");
		queryBuilder.append(DatabaseTables.AM_APPLICATION + " APP, ");
		queryBuilder.append(DatabaseTables.AM_API + " API ");
		queryBuilder.append(" where ");
		queryBuilder.append(" API.API_ID = ?  ");
		queryBuilder.append(" AND APP.APPLICATION_ID = ?  ");
		queryBuilder.append(" AND SUBS.APPLICATION_ID = APP.APPLICATION_ID ");
		queryBuilder.append("  AND API.API_ID = SUBS.API_ID  ");
		queryBuilder.append(" AND SUBS.SUB_STATUS != 'REJECTED'  ");
		queryBuilder.append("  ORDER BY APP.NAME ");

		try {
			connection = DbUtils.getDbConnection(DataSourceNames.WSO2AM_DB);
			preparedStatement = connection.prepareStatement(queryBuilder.toString());
			preparedStatement.setInt(1, Integer.parseInt(apiID));
			preparedStatement.setInt(2, Integer.parseInt(applicationID));

			resultSet = preparedStatement.executeQuery();


			while (resultSet.next()) {
				return resultSet.getInt("SUBSCRIPTION_ID");
			}
		} catch (Exception e) {
			log.error("database operation error in SUBSCRIPTION_ID :", e);
			throw e;
		} finally {
			DbUtils.closeAllConnections(preparedStatement, connection, resultSet);
		}
		return -1;
	}


	public boolean checkWhiteListed(String MSISDN, String applicationId, String subscriptionId, String apiId) throws Exception {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("SELECT * FROM  ");
		queryBuilder.append(DatabaseTables.SUBSCRIPTION_WHITELIST + "  WHERE  ");
		queryBuilder.append("( subscriptionID  = ? AND  msisdn = ? AND  api_id = ? AND  application_id  =  ?) OR ");
		queryBuilder.append("( subscriptionID IS NULL  AND  msisdn = ? AND  api_id = ? AND  application_id  =  ?) OR  ");
		queryBuilder.append("( subscriptionID = ? AND  msisdn = ? AND  api_id IS NULL  AND  application_id IS NULL ) OR  ");
		queryBuilder.append("( subscriptionID IS NULL  AND  msisdn = ? AND  api_id IS NULL  AND  application_id =?) OR   ");
		queryBuilder.append("( subscriptionID IS NULL  AND  msisdn  IS NULL  AND  api_id IS NULL  AND application_id =  ?) OR   ");
		queryBuilder.append("( subscriptionID IS NULL  AND  msisdn IS NULL  AND api_id = ? AND application_id  = ?)  " +
				"LIMIT 0,1    ");

		try {
			connection = DbUtils.getDbConnection(DataSourceNames.WSO2AM_STATS_DB);
			preparedStatement = connection.prepareStatement(queryBuilder.toString());

			//"(`subscriptionID` = ? AND `msisdn` = ? AND `api_id` = ? AND `application_id` =  >) OR \n" +
			preparedStatement.setString(1, subscriptionId);
			preparedStatement.setString(2, MSISDN);
			preparedStatement.setString(3, apiId);
			preparedStatement.setString(4, applicationId);

			//"(`subscriptionID` = null AND `msisdn` = ? AND `api_id` = ? AND `application_id` =  ?) OR\n"
			preparedStatement.setString(5, MSISDN);
			preparedStatement.setString(6, apiId);
			preparedStatement.setString(7, applicationId);

			// "(`subscriptionID` = ? AND `msisdn` = ? AND `api_id` = null AND `application_id` =  null) OR \n" +
			preparedStatement.setString(8, subscriptionId);
			preparedStatement.setString(9, MSISDN);

			// "(`subscriptionID` = null AND `msisdn` = ? AND `api_id` = null AND `application_id` =  ?) OR \n" +
			preparedStatement.setString(10, MSISDN);
			preparedStatement.setString(11, applicationId);

			//"(`subscriptionID` = null AND `msisdn` = null AND `api_id` = null AND `application_id` =  ?) OR \n" +
			preparedStatement.setString(12, applicationId);

			// "(`subscriptionID` = null AND `msisdn` = null AND `api_id` = ? AND `application_id` =  ?)  ";
			preparedStatement.setString(13, apiId);
			preparedStatement.setString(14, applicationId);

			resultSet = preparedStatement.executeQuery();
			if (resultSet != null) {
				while (resultSet.next()) {
					return true;
				}
			}
		} catch (Exception e) {
			log.error("database operation error in subscription whitelist :", e);
			throw e;
		} finally {
			DbUtils.closeAllConnections(preparedStatement, connection, resultSet);
		}

		return false;
	}

	/**
	 * Generic method to execute a select query
	 *
	 * @param selectQuery   Select query to execute
	 *
	 * @return   List of rows selected
	 * @throws Exception
	 */
	public List<Map<String, Object>> executeCustomSelectQuery(String selectQuery) throws Exception {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();
		Map<String, Object> record = null;

		try {
			connection = DbUtils.getDbConnection(DataSourceNames.WSO2TELCO_DEP_DB);
			preparedStatement = connection.prepareStatement(selectQuery);
			resultSet = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = resultSet.getMetaData();

			while (resultSet.next()) {
				record = new HashMap<String, Object>();

				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String columnName = rsmd.getColumnName(i);
					String columnValue = resultSet.getObject(i).toString();
					record.put(columnName, columnValue);
				}

				recordList.add(record);
			}
		} catch (Exception e) {
			log.error("database operation error in executing custom query :", e);
			throw e;
		} finally {
			DbUtils.closeAllConnections(preparedStatement, connection, resultSet);
		}
		return recordList;
	}
	
	/**
	 * Generic method to execute a insert query
	 *
	 * @param insertQuery   Insert query to execute
	 *
	 * @return   Generated ID
	 * @throws Exception
	 */
	public int executeCustomInsertQueryAndGetGeneratedPrimaryKey (String insertQuery) throws Exception {
		int generatedId = 0;
		Connection connection = null;
		PreparedStatement preparedStatement=null;
		ResultSet insertResults = null;
		
		try {
			connection = DbUtils.getDbConnection(DataSourceNames.WSO2TELCO_DEP_DB);
			preparedStatement = connection.prepareStatement(insertQuery);
			
			preparedStatement.executeUpdate();
			
			insertResults = preparedStatement.getGeneratedKeys();
			
			while (insertResults.next()) {
				generatedId = insertResults.getInt(1);
			}
		} catch (Exception ex) {
			log.error("database operation error in executing custom insert query : ", ex);
			throw ex;
		} finally {
			DbUtils.closeAllConnections(preparedStatement, connection, insertResults);
		}
		
		return generatedId;
	}
}

package com.wso2telco.dep.common.mediation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
}

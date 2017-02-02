package com.wso2telco.dep.common.mediation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.wso2telco.core.dbutils.DbUtils;
import com.wso2telco.core.dbutils.util.DataSourceNames;
import com.wso2telco.dep.common.mediation.util.DatabaseTables;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class APIDAO {

	private final Log log = LogFactory.getLog(APIDAO.class);

	public Integer insertServiceProviderNotifyURL(String apiName,
			String notifyURL, String serviceProvider) throws SQLException,
			Exception {

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
			insertQueryString.append(DatabaseTables.NOTIFY_URL_ENTRY
					.getTableName());
			insertQueryString
					.append(" (api_name, notifyurl, service_provider) ");
			insertQueryString.append("VALUES (?, ?, ?)");

			ps = con.prepareStatement(insertQueryString.toString(),
					Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, apiName);
			ps.setString(2, notifyURL);
			ps.setString(3, serviceProvider);

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
}

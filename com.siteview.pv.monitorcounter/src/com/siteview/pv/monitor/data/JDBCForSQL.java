package com.siteview.pv.monitor.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCForSQL {
	private static Connection conn = getConnection();
	public static Connection getConnection() {
		try {
			String filePath = Config
					.getRealPath("\\file\\sqlconfig.propertes");
			String driver = Config.getReturnStr(filePath, "driver");
			Class.forName(driver).newInstance();
			String ip=Config.getReturnStr(filePath, "ip");
			String port=Config.getReturnStr(filePath, "port");
			String dataName=Config.getReturnStr(filePath, "DatabaseName");
			String URL = "jdbc:sqlserver://"+ip+":"+port+";DatabaseName="+dataName;
			String USER = "sa";
			String PASSWORD = "siteview";
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (java.lang.ClassNotFoundException ce) {
			System.out.println("Get Connection error:");
			ce.printStackTrace();
		} catch (java.sql.SQLException se) {
			System.out.println("Get Connection error:");
			se.printStackTrace();
		} catch (Exception e) {
			System.out.println("Get Connection error:");
			e.printStackTrace();
		}
		return conn;
	}

	/*
	 * the Program is to Select the database!!!
	 */
	public static ResultSet sql_ConnectExecute_Select(String query_sql) {
		ResultSet rs = null;
		try {
			if (conn==null) {
				conn = getConnection();
			}
			Statement statement = conn.createStatement();
			rs = statement.executeQuery(query_sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static void execute_Insert(String sql) {

		try {
			if (conn.isClosed()) {
				conn = getConnection();
			}
			Statement statement = conn.createStatement();
			statement.execute(sql);
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testConnection() {
		if (conn == null)
			conn=getConnection();
		try {
			String sql = "SELECT * FROM Ecc";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				System.out.println(rs.getString("RecId"));
			}
		} catch (SQLException e) {
		} 
	}

	public static void main(String[] args) {
		JDBCForSQL jdbc = new JDBCForSQL();
		// SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Timestamp d2=new Timestamp(System.currentTimeMillis());
		// jdbc.savaLog("insert into MonitorLog(RecId,ownerID,MonitorStatus,MonitorName,MonitorId,MonitorMassage,CreatedDateTime)values('ad26c8ee090d47dfaa9a3ee477c1ba90','lili','good','ping131','1','0.01 sec*39**','"+Timestamp.valueOf(f.format(d2))+"')");
		String query_sql = "select * from EccMonitor";
		ResultSet eccrs = JDBCForSQL.sql_ConnectExecute_Select(query_sql);
		ResultSetMetaData metaData;
		try {
			metaData = eccrs.getMetaData();
			int colum = metaData.getColumnCount();
			while (eccrs.next()) {
				for (int i = 1; i < colum; i++) {
					// Get colum name
					String columName = metaData.getColumnName(i);
					String datavalue = eccrs.getString(columName);
					System.out.println("�У�" + columName + " ֵ��" + datavalue);
					if (datavalue == null )
						System.out.println("���У�" + columName);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

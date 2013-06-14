package com.siteview.snmp.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.siteview.snmp.constants.CommonDef;


public class OIDTypeUtils {

	private static String dbName = "DeviceType.db";
	private static String basedir = System.getProperty("user.dir");
	private static String dataDir = basedir + File.separator + "resource\\";
	private static Map<String, String> OidMap = new HashMap<String, String>();
	private static OIDTypeUtils instance = null;
	private OIDTypeUtils(){
		dbName = dataDir + dbName;
		Connection con = null;
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			ResultSet rs = con.createStatement().executeQuery("select * from SYSOBJECTID");
			while(rs.next()){
				OidMap.put(rs.getString("id"), rs.getString("devname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public String getTypeByOid(String oid){
		if(!OidMap.containsKey(oid)){
			return CommonDef.OTHER;
		}
		return OidMap.get(oid);
	}
	public synchronized static OIDTypeUtils getInstance(){
		if(instance == null){
			instance = new OIDTypeUtils();
		}
		return instance;
	}
	public static void main(String[] args) {
		String result = OIDTypeUtils.getInstance().getTypeByOid("1.3.6.1.4.1.9.1.275");
		System.out.println(result);
	}
}

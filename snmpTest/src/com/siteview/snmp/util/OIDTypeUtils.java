package com.siteview.snmp.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.siteview.snmp.constants.CommonDef;
import com.siteview.snmp.pojo.DevicePro;


public class OIDTypeUtils {

	private  String dbName = "DeviceType.db";
	private  String basedir = System.getProperty("user.dir");
	private  String dataDir = basedir + File.separator + "resource\\";
	private static Map<String, DevicePro> OidMap = new HashMap<String, DevicePro>();
	private static OIDTypeUtils instance = null;
	private OIDTypeUtils(){
		dbName = dataDir + dbName;
		Connection con = null;
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			ResultSet rs = con.createStatement().executeQuery("select * from SYSOBJECTID");
			while(rs.next()){
				DevicePro devicePro = new DevicePro();
				devicePro.setDevFac(rs.getString("factory"));
				devicePro.setDevModel(rs.getString("romversion"));
				devicePro.setDevType(rs.getString("devtype"));
				devicePro.setDevTypeName(rs.getString("devname"));
				OidMap.put(rs.getString("id"), devicePro);
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
	public  String getTypeByOid(String oid){
		if(!OidMap.containsKey(oid)){
			return CommonDef.OTHER;
		}
		return OidMap.get(oid).getDevType();
	}
	public  DevicePro getDevicePro(String oid){
		if(!OidMap.containsKey(oid)){
			return null;
		}
		return OidMap.get(oid);
	}
	public boolean containsKey(String oid){
		return OidMap.containsKey(oid);
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

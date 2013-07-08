package com.siteview.snmp.scan;

import java.util.HashMap;
import java.util.Map;

import com.siteview.snmp.devicehandler.CiscoDeviceHandler;
import com.siteview.snmp.devicehandler.IDeviceHandler;
import com.siteview.snmp.devicehandler.UnivDeviceHandler;

public class DeviceFactory {

	private Map<String, IDeviceHandler> callbackMap = new HashMap<String, IDeviceHandler>();
	private static DeviceFactory instance;
	private DeviceFactory(){
		callbackMap.put("?1.3.6.1.4.1.9", new CiscoDeviceHandler());
		callbackMap.put("0.0.0.0.0.0.0", new UnivDeviceHandler());
	}
	public static synchronized DeviceFactory getInstance(){
		if(instance == null){
			instance = new DeviceFactory();
		}
		return instance;
	}
	public boolean unregisterDevice(String sysOid){
		
		return callbackMap.containsKey(sysOid);
	}
	/**
	 * 根据OId生成特定设备扫描器
	 * @param sysOid 设备OID
	 * @return 设备扫描器
	 */
	public IDeviceHandler createDevice(String sysOid){
		if(callbackMap.containsKey(sysOid)){
			return callbackMap.get(sysOid);
		}
		else{
			String[] ss = sysOid.split("\\.");
			if(ss.length >= 7){
				String oid_tmp = ss[0] + "." + ss[1] + "." + ss[2] + "." + ss[3] + "." + ss[4] + "." + ss[5] + "." + ss[6];
				if(oid_tmp.equals("1.3.6.1.4.1.9")) return new CiscoDeviceHandler();
			}
			return callbackMap.get("0.0.0.0.0.0.0");
		}
	}
	public static void main(String[] args) {
		IDeviceHandler handler = getInstance().createDevice("1.3.6.1.4.1.9");
		System.out.println(handler.getClass().getName());
	}
}

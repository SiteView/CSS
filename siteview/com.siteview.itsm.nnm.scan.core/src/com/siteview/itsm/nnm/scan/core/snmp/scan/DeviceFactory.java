package com.siteview.itsm.nnm.scan.core.snmp.scan;

import java.util.HashMap;
import java.util.Map;

import com.siteview.itsm.nnm.scan.core.snmp.devicehandler.CiscoDeviceHandler;
import com.siteview.itsm.nnm.scan.core.snmp.devicehandler.IDeviceHandler;
import com.siteview.itsm.nnm.scan.core.snmp.devicehandler.UnivDeviceHandler;

public class DeviceFactory {

	private Map<String, IDeviceHandler> callbackMap = new HashMap<String, IDeviceHandler>();
	private static DeviceFactory instance;
	private DeviceFactory(){
		callbackMap.put("1.3.6.1.4.1.9", new CiscoDeviceHandler());
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
	 * ����OId�����ض��豸ɨ����
	 * @param sysOid �豸OID
	 * @return �豸ɨ����
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

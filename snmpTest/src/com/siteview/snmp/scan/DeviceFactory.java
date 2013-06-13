package com.siteview.snmp.scan;

public class DeviceFactory {

	private static DeviceFactory instance;
	
	public static synchronized DeviceFactory getInstance(){
		if(instance == null){
			instance = new DeviceFactory();
		}
		return instance;
	}
}

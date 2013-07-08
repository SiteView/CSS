package com.siteview.snmp.scan.icmp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;


public class LocalNetInfo {

	private NetworkInterface device = null;
	public LocalNetInfo(){
		NetworkInterface[] devices=JpcapCaptor.getDeviceList();
		int index = 0;
		if(devices.length >0){index = 1;}
		device =  devices[index];
	}
	
	private InetAddress thisIp = null;
	
	public NetworkInterface getLocalDevice(){
		return this.device;
	}
	public void getThisMac(NetworkInterface device){
		
	}
	public InetAddress getThisIp(){
		for(NetworkInterfaceAddress addr:device.addresses)
			if(addr.address instanceof Inet4Address){
				return addr.address;
			}
		return null;
	}
	public void macToHexString(){
		
	}
}

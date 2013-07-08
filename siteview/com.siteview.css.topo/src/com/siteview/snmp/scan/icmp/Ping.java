package com.siteview.snmp.scan.icmp;

import java.net.InetAddress;

import jpcap.NetworkInterface;

public class Ping implements Runnable{

	private String destinatin;
	short TTL = 30;
	int packets=4;//一次ping 发送包数
	int waitTime = 4000;
	NetworkInterface device = null;
	InetAddress thisIP = null;
	byte []thisMac = null;
	byte []gwMac = null;
	boolean done = false;//线程执行标识，可以实现纯种在执行中停止
	
	@Override
	public void run() {
		try {
			NetworkInterface device = new LocalNetInfo().getLocalDevice();
			
		} catch (Exception e) {
		}
	}

}

package com.siteview.itsm.nnm.scan.core.snmp.scan.icmp;

import java.net.InetAddress;

import jpcap.NetworkInterface;

public class Ping implements Runnable{

	private String destinatin;
	short TTL = 30;
	int packets=4;//һ��ping ���Ͱ���
	int waitTime = 4000;
	NetworkInterface device = null;
	InetAddress thisIP = null;
	byte []thisMac = null;
	byte []gwMac = null;
	boolean done = false;//�߳�ִ�б�ʶ������ʵ�ִ�����ִ����ֹͣ
	
	@Override
	public void run() {
		try {
			NetworkInterface device = new LocalNetInfo().getLocalDevice();
			
		} catch (Exception e) {
		}
	}

}

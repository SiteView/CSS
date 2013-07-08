package com.siteview.snmp.scan;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class PingMaster implements Runnable {

	private Vector<String> aliveIp_list = new Vector<String>();
	private PingHelper pingHelper;
	private Integer sleepTime;
	private HashMap<String, Integer> pingMap;

	public PingMaster(Integer sleepTime, HashMap<String, Integer> pingMap) {
		this.pingHelper = new PingHelper();
		this.sleepTime = sleepTime;
		this.pingMap = pingMap;
	}

	public Vector<String> getAliveIp_list() {
		return aliveIp_list;
	}

	public void setAliveIp_list(Vector<String> aliveIp_list) {
		this.aliveIp_list = aliveIp_list;
	}

	public synchronized void setPingStatus(String pingIP, Integer pingStatus) {
		pingMap.put(pingIP, pingStatus);
	}

	public HashMap<String, Integer> getPingStatus() {
		return this.pingMap;
	}

	public int siglePing(String ip) {
		
		boolean result = false; 
//		try{
//		result= pingHelper.singlePing(ip,300);
//		catch (Exception e) {
//		}
		if(result){
			return 1;
		}else return -1;
	}

	@Override
	public void run() {
		Set<String> pingIPs = pingMap.keySet();
		for (String pingIP : pingIPs) {
			int pingStatus = 0;//pingHelper.ping(pingIP);
			setPingStatus(pingIP, pingStatus);
			if (pingStatus > 0) {
				aliveIp_list.add(pingIP);
			}
		}
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

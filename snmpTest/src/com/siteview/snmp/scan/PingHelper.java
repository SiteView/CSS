package com.siteview.snmp.scan;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class PingHelper {

	private static final String CMD = "CMD /c PING ";
	private Process cmdProcess;
	
	public int ping(String ip){
		try {
			cmdProcess = Runtime.getRuntime().exec(CMD + ip);
			cmdProcess.waitFor();
			return cmdProcess.exitValue();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public static void main(String[] args) throws InterruptedException {
		HashMap<String, Integer> map = new HashMap<String,Integer>();
		map.put("192.168.9.188", 1);
		map.put("192.168.0.248", 1);
		map.put("127.0.0.1", 1);
		PingMaster p = new PingMaster(2000, map);
		Thread t = new Thread(p);
		t.join();
		t.start();
			map = p.getPingStatus();
			Set<String> set = map.keySet();
			String datetime = (new Date()).toLocaleString();
			for(String s : set){
				System.out.println("*-*-*" + datetime + "ping-report *-*-*");
				System.out.println("IP = " + s + "Ping " + " Status:" + map.get(s));
				
				Thread.sleep(1500);
			}
	}
}

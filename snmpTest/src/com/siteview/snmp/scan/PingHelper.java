package com.siteview.snmp.scan;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.siteview.snmp.util.ThreadTaskPool;

public class PingHelper {

	private Vector<String> alive_list = new Vector<String>();
	
	public static boolean isStop = false;

	public Vector<String> getAliveIpList() {
		return alive_list;
	}

	/**
	 * 异步执行ping
	 * 
	 * @param to_ping_ips
	 * @param retrys
	 * @param timeout
	 * @return
	 */
	public boolean multPing(List<String> to_ping_ips, int retrys, int timeout) {
		alive_list.clear();
		if (to_ping_ips.isEmpty()) {
			return true;
		}
		for (String ip : to_ping_ips) {
			if (isStop) {
				return false;
			} else {
				ThreadTaskPool.getInstance().excute(new Task(ip, retrys, timeout));
			}
		}
		return true;
	}
	/**
	 * 使用java自带的icmp服务ping主机
	 * @param ip
	 * @param timeout
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public boolean singlePing(String ip, int timeout)
			throws UnknownHostException, IOException {
		return InetAddress.getByName(ip).isReachable(timeout);
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		// System.out.println(singlePing("192.168.0.248",3000));
		Vector<String> toPingList = new Vector<String>();
		toPingList.add("192.168.9.188");
		toPingList.add("192.168.0.248");
		toPingList.add("192.168.9.254");
		new PingHelper().multPing(toPingList, 10, 2000);

	}

	public static void maian(String[] args) throws InterruptedException {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("192.168.9.188", 1);
		map.put("192.168.0.248", 1);
		map.put("192.168.9.254", 1);
		PingMaster p = new PingMaster(2000, map);
		Thread t = new Thread(p);
		t.join();
		t.start();
		map = p.getPingStatus();
		Set<String> set = map.keySet();
		String datetime = (new Date()).toLocaleString();
		for (String s : set) {
			System.out.println("*-*-*" + datetime + "ping-report *-*-*");
			System.out.println("IP = " + s + "Ping " + " Status:" + map.get(s));
			Thread.sleep(1500);
		}
	}

	class Task implements Runnable {
		private String ip;
		private int retrys;
		private int timeout;

		public Task(String ip, int retrys, int timeout) {
			this.ip = ip;
			this.retrys = retrys;
			this.timeout = timeout;
		}

		@Override
		public void run() {
			try {
				if (InetAddress.getByName(ip).isReachable(timeout)) {
					alive_list.add(ip);
				}
				synchronized (alive_list) {
					endCount++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	public static int endCount = 0;
}

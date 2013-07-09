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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.siteview.snmp.util.ThreadTaskPool;

public class PingHelper {

	/**
	 * 可以ping通的IP地址列表　	
	 */
	private Vector<String> alive_list = new Vector<String>();
	/**
	 * 是否停止的标志
	 */
	public static boolean isStop = false;

	public Vector<String> getAliveIpList() {
		return alive_list;
	}
	//异步执行计数器
	private CountDownLatch multPingLatch;
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
		//初始化计数器
		multPingLatch = new CountDownLatch(to_ping_ips.size());
		for (String ip : to_ping_ips) {
			if (isStop) {
				return false;
			} else {
				ThreadTaskPool.getInstance().excute(new Task(ip, retrys, timeout));
			}
		}
		try {
			multPingLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		boolean result = InetAddress.getByName(ip).isReachable(timeout);
		if(result){
			alive_list.add(ip);
		}
		return result;
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		Vector<String> toPingList = new Vector<String>();
		toPingList.add("192.168.9.188");
		toPingList.add("192.168.0.248");
		toPingList.add("192.168.9.254");
		PingHelper p = new PingHelper();p.multPing(toPingList, 10, 2000);
		System.out.println(p.getAliveIpList().size());

	}
	/**
	 * 执行异步PING操作的任务类
	 * @author haiming.wang
	 *
	 */
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
				singlePing(ip, timeout);
				multPingLatch.countDown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

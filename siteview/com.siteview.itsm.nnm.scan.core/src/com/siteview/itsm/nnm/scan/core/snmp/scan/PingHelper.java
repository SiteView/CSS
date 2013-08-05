package com.siteview.itsm.nnm.scan.core.snmp.scan;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import com.siteview.itsm.nnm.scan.core.snmp.util.ThreadTaskPool;

public class PingHelper {

	/**
	 * ����pingͨ��IP��ַ�б�	
	 */
	private Vector<String> alive_list = new Vector<String>();
	/**
	 * �Ƿ�ֹͣ�ı�־
	 */
	public static boolean isStop = false;

	public Vector<String> getAliveIpList() {
		return alive_list;
	}
	//�첽ִ�м�����
	private CountDownLatch multPingLatch;
	/**
	 * �첽ִ��ping
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
		//��ʼ��������
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
	 * ʹ��java�Դ���icmp����ping����
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
	 * ִ���첽PING������������
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

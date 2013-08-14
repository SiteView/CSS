package com.siteview.itsm.nnm.scan.core.snmp.pojo;

public class DeviceMemInfo {

	/**
	 * IP 地址
	 */
	private String ip;
	/**
	 * 总内存
	 */
	private int memTotal;
	/**
	 * 空闲内存
	 */
	private int memFree;
	
	public int getMemTotal() {
		return memTotal;
	}
	public void setMemTotal(int memTotal) {
		this.memTotal = memTotal;
	}
	public int getMemFree() {
		return memFree;
	}
	public void setMemFree(int memFree) {
		this.memFree = memFree;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}

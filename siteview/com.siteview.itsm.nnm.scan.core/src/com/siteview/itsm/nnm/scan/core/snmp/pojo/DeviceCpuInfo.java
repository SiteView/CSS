package com.siteview.itsm.nnm.scan.core.snmp.pojo;

/**
 * 
 * @author haiming.wang
 *
 */
public class DeviceCpuInfo {

	/**
	 * IP 地址
	 */
	private String ip;
	/**
	 * cpu使用百分比 5秒之内
	 */
	private int usedCpu5S;
	/**
	 * cpu使用百分比1分钟之内
	 */
	private int usedCpu1M;
	/**
	 * cpu使用百分比5分钟之内
	 */
	private int usedCpu5M;
	
	public int getUsedCpu5M() {
		return usedCpu5M;
	}
	public void setUsedCpu5M(int usedCpu5M) {
		this.usedCpu5M = usedCpu5M;
	}
	public int getUsedCpu5S() {
		return usedCpu5S;
	}
	public void setUsedCpu5S(int usedCpu5S) {
		this.usedCpu5S = usedCpu5S;
	}
	public int getUsedCpu1M() {
		return usedCpu1M;
	}
	public void setUsedCpu1M(int usedCpu1M) {
		this.usedCpu1M = usedCpu1M;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
}

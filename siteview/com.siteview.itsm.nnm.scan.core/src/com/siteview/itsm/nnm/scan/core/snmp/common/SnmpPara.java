package com.siteview.itsm.nnm.scan.core.snmp.common;

public class SnmpPara {
	private String ip;
	private String community;
	private int timeout = 300;
	private int retry = 3;
	private String snmpver = "0";

	public SnmpPara() {

	}

	public SnmpPara(String ip, String community, int timeout, int retry,
			String snmpver) {
		this.ip = ip;
		this.community = community;
		this.timeout = timeout;
		this.retry = retry;
		this.snmpver = snmpver;
	}
	public SnmpPara(String ip, String community, int timeout, int retry) {
		this.ip = ip;
		this.community = community;
		this.timeout = timeout;
		this.retry = retry;
		this.snmpver = snmpver;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public String getSnmpver() {
		return snmpver;
	}

	public void setSnmpver(String snmpver) {
		this.snmpver = snmpver;
	}

}

package com.siteview.itsm.nnm.scan.core.snmp.pojo;

/**
 * �豸�ӿ�����
 * @author haiming.wang
 *
 */
public class IfRec {

	private String ifIndex;
	private String ifType;
	private String ifDesc;
	private String ifMac;
	private String ifPort;
	private String ifSpeed;
	public String getIfIndex() {
		return ifIndex;
	}
	public void setIfIndex(String ifIndex) {
		this.ifIndex = ifIndex;
	}
	public String getIfType() {
		return ifType;
	}
	public void setIfType(String ifType) {
		this.ifType = ifType;
	}
	public String getIfDesc() {
		return ifDesc;
	}
	public void setIfDesc(String ifDesc) {
		this.ifDesc = ifDesc;
	}
	public String getIfMac() {
		return ifMac;
	}
	public void setIfMac(String ifMac) {
		this.ifMac = ifMac;
	}
	public String getIfPort() {
		return ifPort;
	}
	public void setIfPort(String ifPort) {
		this.ifPort = ifPort;
	}
	public String getIfSpeed() {
		return ifSpeed;
	}
	public void setIfSpeed(String ifSpeed) {
		this.ifSpeed = ifSpeed;
	}
	
}

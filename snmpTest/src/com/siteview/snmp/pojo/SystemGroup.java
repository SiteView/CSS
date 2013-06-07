package com.siteview.snmp.pojo;

public class SystemGroup {

	/**
	 * 关于该设备或实体的描述，如设备类型、硬件特性、操作系统信息 等
	 */
	private String sysDescr;
	/**
	 * 设备厂商的授权标识符
	 */
	private String sysObjectID;
	/**
	 * 从系统（代理）的网络管理部分最后一次重新初始化以来，经过的 时间量
	 */
	private String sysUpTime;
	/**
	 * 记录其他提供该设备支持的机构和（或）联系人的信息 
	 */
	private String sysContact;
	/**
	 * 设备的名字，可能是官方的主机名或者是分配的管理名字 
	 */
	private String sysName;
	/**
	 * 该设备安装的物理位置 
	 */
	private String sysLocation;
	/**
	 * 该设备提供的服务
	 */
	private int sysServcies;
	public String getSysDescr() {
		return sysDescr;
	}
	public void setSysDescr(String sysDescr) {
		this.sysDescr = sysDescr;
	}
	public String getSysObjectID() {
		return sysObjectID;
	}
	public void setSysObjectID(String sysObjectID) {
		this.sysObjectID = sysObjectID;
	}
	public String getSysUpTime() {
		return sysUpTime;
	}
	public void setSysUpTime(String sysUpTime) {
		this.sysUpTime = sysUpTime;
	}
	public String getSysContact() {
		return sysContact;
	}
	public void setSysContact(String sysContact) {
		this.sysContact = sysContact;
	}
	public String getSysName() {
		return sysName;
	}
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	public String getSysLocation() {
		return sysLocation;
	}
	public void setSysLocation(String sysLocation) {
		this.sysLocation = sysLocation;
	}
	public int getSysServcies() {
		return sysServcies;
	}
	public void setSysServcies(int sysServcies) {
		this.sysServcies = sysServcies;
	}
	
	
}

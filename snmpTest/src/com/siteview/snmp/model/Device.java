package com.siteview.snmp.model;

import java.util.Date;
import java.util.List;

public class Device {
	
	/**
	 * 设备ID
	 */
	private String deviceId;
	/**
	 * 设备名称
	 */
	private String deviceName;
	/**
	 * 设备类型 pc 或者 路由设备
	 */
	private DeviceType deviceType;
	/**
	 * 运行时间
	 */
	private String lastChangeTime;
	/**
	 * 设备位置
	 */
	private String location;
	/**
	 * 资产标签
	 */
	private String assetMark;
	/**
	 * 服务标签
	 */
	private String serviceMark;
	/**
	 * 联系人
	 */
	private String contact;
	/**
	 * 上次扫描时间
	 */
	private Date lastScanTime;
	/**
	 * 上次扫描状态
	 */
	private int lastScanStautus;
	/**
	 * 制造商
	 */
	private String maker;
	/**
	 * 所属部门
	 */
	private String department;
	
	
	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


	public String getDeviceName() {
		return deviceName;
	}


	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}


	public DeviceType getDeviceType() {
		return deviceType;
	}


	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getLastChangeTime() {
		return lastChangeTime;
	}


	public void setLastChangeTime(String lastChangeTime) {
		this.lastChangeTime = lastChangeTime;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getAssetMark() {
		return assetMark;
	}


	public void setAssetMark(String assetMark) {
		this.assetMark = assetMark;
	}


	public String getServiceMark() {
		return serviceMark;
	}


	public void setServiceMark(String serviceMark) {
		this.serviceMark = serviceMark;
	}


	public String getContact() {
		return contact;
	}


	public void setContact(String contact) {
		this.contact = contact;
	}


	public Date getLastScanTime() {
		return lastScanTime;
	}


	public void setLastScanTime(Date lastScanTime) {
		this.lastScanTime = lastScanTime;
	}


	public int getLastScanStautus() {
		return lastScanStautus;
	}


	public void setLastScanStautus(int lastScanStautus) {
		this.lastScanStautus = lastScanStautus;
	}


	public String getMaker() {
		return maker;
	}


	public void setMaker(String maker) {
		this.maker = maker;
	}


	public String getDepartment() {
		return department;
	}


	public void setDepartment(String department) {
		this.department = department;
	}

	
	public enum DeviceType{
		pc,
		route
	}
}

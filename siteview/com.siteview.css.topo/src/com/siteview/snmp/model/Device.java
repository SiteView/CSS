package com.siteview.snmp.model;

import java.util.Date;
import java.util.List;

public class Device {
	
	/**
	 * �豸ID
	 */
	private String deviceId;
	/**
	 * �豸����
	 */
	private String deviceName;
	/**
	 * �豸���� pc ���� ·���豸
	 */
	private DeviceType deviceType;
	/**
	 * ����ʱ��
	 */
	private String lastChangeTime;
	/**
	 * �豸λ��
	 */
	private String location;
	/**
	 * �ʲ���ǩ
	 */
	private String assetMark;
	/**
	 * �����ǩ
	 */
	private String serviceMark;
	/**
	 * ��ϵ��
	 */
	private String contact;
	/**
	 * �ϴ�ɨ��ʱ��
	 */
	private Date lastScanTime;
	/**
	 * �ϴ�ɨ��״̬
	 */
	private int lastScanStautus;
	/**
	 * ������
	 */
	private String maker;
	/**
	 * ��������
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

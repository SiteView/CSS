package com.siteview.snmp.pojo;

/***********************************************************************
 * Module:  AtTable.java
 * Author:  haiming.wang
 * Purpose: Defines the Class AtTable
 ***********************************************************************/


/**
 * 转化表（物理地址与IP地址和商品索引之间的映射表）
 * 
 */
public class AtTable {
	
	private String atPhysAddress;
	private int atIfIndex;
	private String atNetAddress;

	public String getAtPhysAddress() {
		return atPhysAddress;
	}

	/**
	 * @param newAtPhysAddress
	 */
	public void setAtPhysAddress(String newAtPhysAddress) {
		atPhysAddress = newAtPhysAddress;
	}

	public int getAtIfIndex() {
		return atIfIndex;
	}

	/**
	 * @param newAtIfIndex
	 */
	public void setAtIfIndex(int newAtIfIndex) {
		atIfIndex = newAtIfIndex;
	}

	public String getAtNetAddress() {
		return atNetAddress;
	}

	/**
	 * @param newAtNetAddress
	 */
	public void setAtNetAddress(String newAtNetAddress) {
		atNetAddress = newAtNetAddress;
	}

}
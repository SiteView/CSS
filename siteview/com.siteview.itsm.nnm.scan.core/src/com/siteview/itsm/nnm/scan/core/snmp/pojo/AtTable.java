package com.siteview.itsm.nnm.scan.core.snmp.pojo;

/***********************************************************************
 * Module:  AtTable.java
 * Author:  haiming.wang
 * Purpose: Defines the Class AtTable
 ***********************************************************************/


/**
 * ת���������ַ��IP��ַ����Ʒ����֮���ӳ���
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
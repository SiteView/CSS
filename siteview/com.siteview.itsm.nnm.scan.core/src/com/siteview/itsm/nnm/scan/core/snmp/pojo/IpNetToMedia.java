package com.siteview.itsm.nnm.scan.core.snmp.pojo;

/***********************************************************************
 * Module:  IpNetToMedia.java
 * Author:  haiming.wang
 * Purpose: Defines the Class IpNetToMedia
 ***********************************************************************/


/**
 * IP NetToMedia ���� ARP��
 * 
 * @pdOid bf9e1218-94c7-4b44-84c8-99f380925e00
 */
public class IpNetToMedia {
	/**
	 * �ӿڱ��нӿڵ�����������ĵȼ��������Ч��
	 * 
	 * @pdOid c6e86a91-abe3-4515-9535-6e7db27a163f
	 */
	private int ipNetToMediaIfIndex;
	/**
	 * ��ý����ص������ַ
	 * 
	 * @pdOid 0726dc1f-6105-4cb4-aa4a-815854bfc044
	 */
	private String ipNetToMediaPhysAddress;
	/**
	 * �������ַ��Ӧ�� IP ��ַ
	 * 
	 * @pdOid 30ed3a40-cb12-449d-8a1c-c2fb75a72e81
	 */
	private String ipNetToMediaNetAddress;
	/**
	 * ӳ������ other(1), invalid(2), dynamic(3),static(4)
	 * 
	 * @pdOid 0bc2a139-4657-4a87-86a2-511ec1038725
	 */
	private int ipNetToMediaType;

	/** @pdOid 88b49a82-9026-4755-a991-e75448db926c */
	public int getIpNetToMediaIfIndex() {
		return ipNetToMediaIfIndex;
	}

	/**
	 * @param newIpNetToMediaIfIndex
	 * @pdOid 4884174d-f6ba-4520-af00-70cc3669bfed
	 */
	public void setIpNetToMediaIfIndex(int newIpNetToMediaIfIndex) {
		ipNetToMediaIfIndex = newIpNetToMediaIfIndex;
	}

	/** @pdOid 0bf3b68d-e64a-4b25-837b-6ef3ef6e2722 */
	public String getIpNetToMediaPhysAddress() {
		return ipNetToMediaPhysAddress;
	}

	/**
	 * @param newIpNetToMediaPhysAddress
	 * @pdOid 52b0fe88-412b-4c68-97fb-be70e5f0e6d9
	 */
	public void setIpNetToMediaPhysAddress(String newIpNetToMediaPhysAddress) {
		ipNetToMediaPhysAddress = newIpNetToMediaPhysAddress;
	}

	/** @pdOid d874515f-a0a1-43a4-96d3-cbc34017dc26 */
	public String getIpNetToMediaNetAddress() {
		return ipNetToMediaNetAddress;
	}

	/**
	 * @param newIpNetToMediaNetAddress
	 * @pdOid d0f913db-d6a5-41ac-bf44-01292b9b441c
	 */
	public void setIpNetToMediaNetAddress(String newIpNetToMediaNetAddress) {
		ipNetToMediaNetAddress = newIpNetToMediaNetAddress;
	}

	/** @pdOid cfdfe1d9-f879-4357-8d63-b7f964c125ac */
	public int getIpNetToMediaType() {
		return ipNetToMediaType;
	}

	/**
	 * @param newIpNetToMediaType
	 * @pdOid 26452603-ce0a-4756-9833-c1042fc43072
	 */
	public void setIpNetToMediaType(int newIpNetToMediaType) {
		ipNetToMediaType = newIpNetToMediaType;
	}

}
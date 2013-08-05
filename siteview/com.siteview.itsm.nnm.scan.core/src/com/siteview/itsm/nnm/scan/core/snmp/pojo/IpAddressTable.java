package com.siteview.itsm.nnm.scan.core.snmp.pojo;

/***********************************************************************
 * Module:  IpAddressTable.java
 * Author:  haiming.wang
 * Purpose: Defines the Class IpAddressTable
 ***********************************************************************/


/**
 * ip��ַ���Ա�
 * 
 * @pdOid 341980db-dd18-4fe4-b1f0-767017288a29
 */
public class IpAddressTable {
	/**
	 * ���ڴ����Ѱַ��Ϣ�� IP ��ַ
	 * 
	 * @pdOid 388e7159-6c65-46ea-b52a-07a1b9a50d5d
	 */
	private String ipAdEntAddr;
	/**
	 * �ӿڱ��ж�Ӧ�ӿڵ�����
	 * 
	 * @pdOid 4ce2a0a0-7fab-49f4-8d65-214a67342721
	 */
	private int ipAdEntIfIndex;
	/**
	 * �� IP ��ַ��������������
	 * 
	 * @pdOid ac2cd160-93e6-463f-ae4c-df6880b8e789
	 */
	private String ipAdEntNetMask;
	/**
	 * IP �㲥��ַ�����Ҫ��λ��ֵ
	 * 
	 * @pdOid 1de812df-ee1d-424e-8890-23ab55eacb8e
	 */
	private int ipAdEntBcastAddr;
	/**
	 * ��ʵ�����װ����� IP ���ݱ���С
	 * 
	 * @pdOid 3b36e7e5-cc37-4b2b-86cf-1f34d604640d
	 */
	private int ipAdEntReasmMaxSize;

	/** @pdOid 7f8440e0-1479-40cd-80ab-85a525214784 */
	public String getIpAdEntAddr() {
		return ipAdEntAddr;
	}

	/**
	 * @param newIpAdEntAddr
	 * @pdOid dab3f342-2412-406d-b57b-683b5292d19e
	 */
	public void setIpAdEntAddr(String newIpAdEntAddr) {
		ipAdEntAddr = newIpAdEntAddr;
	}

	/** @pdOid 58d2c61d-7f8c-41ee-b9cb-6d84a60ec33b */
	public int getIpAdEntIfIndex() {
		return ipAdEntIfIndex;
	}

	/**
	 * @param newIpAdEntIfIndex
	 * @pdOid d783cd2c-f682-4060-9578-584b0dc0aec7
	 */
	public void setIpAdEntIfIndex(int newIpAdEntIfIndex) {
		ipAdEntIfIndex = newIpAdEntIfIndex;
	}

	/** @pdOid 960bf04f-0c63-44a5-aa1f-a82520f60cc9 */
	public String getIpAdEntNetMask() {
		return ipAdEntNetMask;
	}

	/**
	 * @param newIpAdEntNetMask
	 * @pdOid 4ef12e35-f17e-48b4-bdf9-321ab40fac0f
	 */
	public void setIpAdEntNetMask(String newIpAdEntNetMask) {
		ipAdEntNetMask = newIpAdEntNetMask;
	}

	/** @pdOid f16f1916-59f1-4d7e-8eba-f164327a4ff8 */
	public int getIpAdEntBcastAddr() {
		return ipAdEntBcastAddr;
	}

	/**
	 * @param newIpAdEntBcastAddr
	 * @pdOid 4ab0ebea-925e-4741-9f4e-c5d5679c74ea
	 */
	public void setIpAdEntBcastAddr(int newIpAdEntBcastAddr) {
		ipAdEntBcastAddr = newIpAdEntBcastAddr;
	}

	/** @pdOid afef9a22-5b7c-45e1-85fd-d136dcab4c2c */
	public int getIpAdEntReasmMaxSize() {
		return ipAdEntReasmMaxSize;
	}

	/**
	 * @param newIpAdEntReasmMaxSize
	 * @pdOid 526ba398-3e38-46e1-834c-f28e265bc9fc
	 */
	public void setIpAdEntReasmMaxSize(int newIpAdEntReasmMaxSize) {
		ipAdEntReasmMaxSize = newIpAdEntReasmMaxSize;
	}

}
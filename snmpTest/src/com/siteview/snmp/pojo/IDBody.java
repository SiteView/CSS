package com.siteview.snmp.pojo;

import java.util.Vector;

public class IDBody {

	private String baseMac;//基本Mac地址
	private String snmpflag;//
	private String community_get;
	private String community_set;
	private String devType;//
	private String devFactory;//
	private String devModel;//
	private String devTypeName;//
	private String sysOid;//sysObjectID 1.3.6.1.2.1.1.2
	private String sysSvcs;//sysServices 1.3.6.1.2.1.1.7
	private String sysName;//sysName 1.3.6.1.2.1.1.5 
	private Vector<String> ips = new Vector<String>();//IP地址1.3.6.1.2.1.4.20.1.3
	private Vector<String> infinxs = new Vector<String>();//IP地址对应的索引1.3.6.1.2.1.4.20.1.2
	private Vector<String> msks = new Vector<String>();//MASK地址1.3.6.1.2.1.4.20.1.3
	private Vector<String> macs = new Vector<String>();//MAC地址1.3.6.1.2.1.2.2.1.6
	public String getBaseMac() {
		return baseMac;
	}
	public void setBaseMac(String baseMac) {
		this.baseMac = baseMac;
	}
	public String getSnmpflag() {
		return snmpflag;
	}
	public void setSnmpflag(String snmpflag) {
		this.snmpflag = snmpflag;
	}
	public String getCommunity_get() {
		return community_get;
	}
	public void setCommunity_get(String community_get) {
		this.community_get = community_get;
	}
	public String getCommunity_set() {
		return community_set;
	}
	public void setCommunity_set(String community_set) {
		this.community_set = community_set;
	}
	public String getDevType() {
		return devType;
	}
	public void setDevType(String devType) {
		this.devType = devType;
	}
	public String getDevFactory() {
		return devFactory;
	}
	public void setDevFactory(String devFactory) {
		this.devFactory = devFactory;
	}
	public String getDevModel() {
		return devModel;
	}
	public void setDevModel(String devModel) {
		this.devModel = devModel;
	}
	public String getDevTypeName() {
		return devTypeName;
	}
	public void setDevTypeName(String devTypeName) {
		this.devTypeName = devTypeName;
	}
	public String getSysOid() {
		return sysOid;
	}
	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}
	public String getSysSvcs() {
		return sysSvcs;
	}
	public void setSysSvcs(String sysSvcs) {
		this.sysSvcs = sysSvcs;
	}
	public String getSysName() {
		return sysName;
	}
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	public Vector<String> getIps() {
		return ips;
	}
	public void setIps(Vector<String> ips) {
		this.ips = ips;
	}
	public Vector<String> getInfinxs() {
		return infinxs;
	}
	public void setInfinxs(Vector<String> infinxs) {
		this.infinxs = infinxs;
	}
	public Vector<String> getMsks() {
		return msks;
	}
	public void setMsks(Vector<String> msks) {
		this.msks = msks;
	}
	public Vector<String> getMacs() {
		return macs;
	}
	public void setMacs(Vector<String> macs) {
		this.macs = macs;
	}
	
}

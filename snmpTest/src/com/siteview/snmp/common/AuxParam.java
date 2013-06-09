package com.siteview.snmp.common;

import com.siteview.snmp.model.Pair;

public class AuxParam {

	private String scan_type;		// 是否从设备读数
	private String seed_type;		// 种子扫描方式，是子网向下还是arp向下
	private String ping_type;		// 是否ping
	private String dumb_type;		// 是否创建哑设备
	private String comp_type;		// 使用ARP数据补足AFT数据的操作类型
	private String arp_read_type;	// 是否读取二层交换机的arp数据
	private String nbr_read_type;	// 是否读取邻居表
	private String rt_read_type; 	// 是否读取路由表 恢复路由表
	private String vrrp_read_type; 	// 是否读取VRRP,HSRP
	private String bgp_read_type; 	// 是否读取BGP
	private String snmp_version; 	// 指定特定SNMP Version的设备
	private Pair<String, String> SNMPV_list;
	private String tracert_type;		// 是否执行trace route
	private String filter_type;		// 是否清除扫描范围外的ip
	private String commit_pc;		// 是否提交PC到SVDB
	public String getScan_type() {
		return scan_type;
	}
	public void setScan_type(String scan_type) {
		this.scan_type = scan_type;
	}
	public String getSeed_type() {
		return seed_type;
	}
	public void setSeed_type(String seed_type) {
		this.seed_type = seed_type;
	}
	public String getPing_type() {
		return ping_type;
	}
	public void setPing_type(String ping_type) {
		this.ping_type = ping_type;
	}
	public String getDumb_type() {
		return dumb_type;
	}
	public void setDumb_type(String dumb_type) {
		this.dumb_type = dumb_type;
	}
	public String getComp_type() {
		return comp_type;
	}
	public void setComp_type(String comp_type) {
		this.comp_type = comp_type;
	}
	public String getArp_read_type() {
		return arp_read_type;
	}
	public void setArp_read_type(String arp_read_type) {
		this.arp_read_type = arp_read_type;
	}
	public String getNbr_read_type() {
		return nbr_read_type;
	}
	public void setNbr_read_type(String nbr_read_type) {
		this.nbr_read_type = nbr_read_type;
	}
	public String getRt_read_type() {
		return rt_read_type;
	}
	public void setRt_read_type(String rt_read_type) {
		this.rt_read_type = rt_read_type;
	}
	public String getVrrp_read_type() {
		return vrrp_read_type;
	}
	public void setVrrp_read_type(String vrrp_read_type) {
		this.vrrp_read_type = vrrp_read_type;
	}
	public String getBgp_read_type() {
		return bgp_read_type;
	}
	public void setBgp_read_type(String bgp_read_type) {
		this.bgp_read_type = bgp_read_type;
	}
	public String getSnmp_version() {
		return snmp_version;
	}
	public void setSnmp_version(String snmp_version) {
		this.snmp_version = snmp_version;
	}
	public Pair<String, String> getSNMPV_list() {
		return SNMPV_list;
	}
	public void setSNMPV_list(Pair<String, String> sNMPV_list) {
		SNMPV_list = sNMPV_list;
	}
	public String getTracert_type() {
		return tracert_type;
	}
	public void setTracert_type(String tracert_type) {
		this.tracert_type = tracert_type;
	}
	public String getFilter_type() {
		return filter_type;
	}
	public void setFilter_type(String filter_type) {
		this.filter_type = filter_type;
	}
	public String getCommit_pc() {
		return commit_pc;
	}
	public void setCommit_pc(String commit_pc) {
		this.commit_pc = commit_pc;
	}
	
	
}

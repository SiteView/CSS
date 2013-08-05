package com.siteview.itsm.nnm.scan.core.snmp.pojo;

public class Bgp {

	private String local_ip;
	private String local_port;
	private String peer_ip;
	private String peer_port;
	public String getLocal_ip() {
		return local_ip;
	}
	public void setLocal_ip(String local_ip) {
		this.local_ip = local_ip;
	}
	public String getLocal_port() {
		return local_port;
	}
	public void setLocal_port(String local_port) {
		this.local_port = local_port;
	}
	public String getPeer_ip() {
		return peer_ip;
	}
	public void setPeer_ip(String peer_ip) {
		this.peer_ip = peer_ip;
	}
	public String getPeer_port() {
		return peer_port;
	}
	public void setPeer_port(String peer_port) {
		this.peer_port = peer_port;
	}
	
}

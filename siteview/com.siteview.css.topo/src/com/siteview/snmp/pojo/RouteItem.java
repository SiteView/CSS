package com.siteview.snmp.pojo;

/**
 * 路由条目
 * @author haiming.wang
 *
 */
public class RouteItem {

	private String dest_net;//目标网络
	private String next_hop;//下一跳IP
	private String dest_msk;//目标网络的掩码
	public String getDest_net() {
		return dest_net;
	}
	public void setDest_net(String dest_net) {
		this.dest_net = dest_net;
	}
	public String getNext_hop() {
		return next_hop;
	}
	public void setNext_hop(String next_hop) {
		this.next_hop = next_hop;
	}
	public String getDest_msk() {
		return dest_msk;
	}
	public void setDest_msk(String dest_msk) {
		this.dest_msk = dest_msk;
	}
	
}

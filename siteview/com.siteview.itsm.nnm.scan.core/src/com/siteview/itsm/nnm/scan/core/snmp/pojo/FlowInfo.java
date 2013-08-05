package com.siteview.itsm.nnm.scan.core.snmp.pojo;

public class FlowInfo {

	private long time;
	/**
	 * 源节点IP
	 */
	private String srcLift;
	/**
	 * 目标节点IP
	 */
	private String desRight;
	/**
	 * 端口输入流量
	 */
	private long inFlow;
	/**
	 * 端口输出流量
	 */
	private long outFlow;
	/**
	 * 端口索引
	 */
	private int ifIndex;
	/**
	 * 端口mac地址
	 */
	private String ifMac;
	
	public String getSrcLift() {
		return srcLift;
	}
	public void setSrcLift(String srcLift) {
		this.srcLift = srcLift;
	}
	public String getDesRight() {
		return desRight;
	}
	public void setDesRight(String desRight) {
		this.desRight = desRight;
	}
	public long getInFlow() {
		return inFlow;
	}
	public void setInFlow(long inFlow) {
		this.inFlow = inFlow;
	}
	public long getOutFlow() {
		return outFlow;
	}
	public void setOutFlow(long outFlow) {
		this.outFlow = outFlow;
	}
	public int getIfIndex() {
		return ifIndex;
	}
	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}
	public String getIfMac() {
		return ifMac;
	}
	public void setIfMac(String ifMac) {
		this.ifMac = ifMac;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}

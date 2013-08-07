package com.siteview.itsm.nnm.scan.core.snmp.pojo;

import java.util.Comparator;

import com.siteview.itsm.nnm.scan.core.snmp.util.ScanUtils;

/**
 * 子网
 * @author haiming.wang
 *
 */
public class SubnetInfo{
	//
	private String subnetNum;
	private String mask;
	private int count;
	public String getSubnetNum() {
		return subnetNum;
	}
	public void setSubnetNum(String subnetNum) {
		this.subnetNum = subnetNum;
	}
	public String getMask() {
		return mask;
	}
	public void setMask(String mask) {
		this.mask = mask;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public boolean equals(Object obj) {
		SubnetInfo info = (SubnetInfo)obj;
		return info.getSubnetNum().equals(this.getSubnetNum());
	}
	@Override
	public int hashCode() {
		return this.getSubnetNum().split("/")[0].hashCode();
	}
	
}

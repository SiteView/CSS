package com.siteview.snmp.pojo;

import java.util.Vector;

public class RouterStandbyItem {

	private Vector<String> virtualIps = new Vector<String>();
    private Vector<String> virtualMacs = new Vector<String>();
	public Vector<String> getVirtualIps() {
		return virtualIps;
	}
	public void setVirtualIps(Vector<String> virtualIps) {
		this.virtualIps = virtualIps;
	}
	public Vector<String> getVirtualMacs() {
		return virtualMacs;
	}
	public void setVirtualMacs(Vector<String> virtualMacs) {
		this.virtualMacs = virtualMacs;
	}
    
}

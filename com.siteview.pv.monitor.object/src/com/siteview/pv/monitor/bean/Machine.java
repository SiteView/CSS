package com.siteview.pv.monitor.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Machine implements Serializable{
	private static final long serialVersionUID = 1L;
	private String machineType;
	private String machineId;
	private String machineIp;
	private String machineTitle;
	private String machineUrl;
	public String getMachineUrl() {
		return machineUrl;
	}
	public void setMachineUrl(String machineUrl) {
		this.machineUrl = machineUrl;
	}
	private List<Monitor> monitors=new ArrayList<Monitor>();
	public String getMachineType() {
		return machineType;
	}
	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	public String getMachineIp() {
		return machineIp;
	}
	public void setMachineIp(String machineIp) {
		this.machineIp = machineIp;
	}
	public String getMachineTitle() {
		return machineTitle;
	}
	public void setMachineTitle(String machineTitle) {
		this.machineTitle = machineTitle;
	}
	public List<Monitor> getMonitors() {
		return monitors;
	}
	public void setMonitors(List<Monitor> monitors) {
		this.monitors = monitors;
	}
	
}

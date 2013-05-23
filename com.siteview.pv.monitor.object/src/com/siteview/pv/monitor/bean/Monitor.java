package com.siteview.pv.monitor.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Monitor implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String monitorId;
	private String monitorType;
	private String monitorMachine;
	private String monitorTitle;
	private String groupId;
	private String monitorUrl;
	private String importCounter;
	private List<String> counter=new ArrayList<String>();
	public List<String> getCounter() {
		return counter;
	}
	public void setCounter(List<String> counter) {
		this.counter = counter;
	}
	public String getMonitorId() {
		return monitorId;
	}
	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}
	public String getMonitorType() {
		return monitorType;
	}
	public void setMonitorType(String monitorType) {
		this.monitorType = monitorType;
	}
	public String getMonitorMachine() {
		return monitorMachine;
	}
	public void setMonitorMachine(String monitorMachine) {
		this.monitorMachine = monitorMachine;
	}
	public String getMonitorTitle() {
		return monitorTitle;
	}
	public void setMonitorTitle(String monitorTitle) {
		this.monitorTitle = monitorTitle;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getMonitorUrl() {
		return monitorUrl;
	}
	public void setMonitorUrl(String monitorUrl) {
		this.monitorUrl = monitorUrl;
	}
	public String getImportCounter() {
		return importCounter;
	}
	public void setImportCounter(String importCounter) {
		this.importCounter = importCounter;
	}
	
}

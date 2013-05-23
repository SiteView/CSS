package com.siteview.pv.monitor.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String groupName;
	private String groupId;
	private String parentGroupId;
	private String groupUrl;
	private List<Group> subGroup=new ArrayList<Group>();
	private List<Monitor> monitors=new ArrayList<Monitor>();
	private List<Machine> machines=new ArrayList<Machine>();
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getParentGroupId() {
		return parentGroupId;
	}
	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}
	public List<Group> getSubGroup() {
		return subGroup;
	}
	public void setSubGroup(List<Group> subGroup) {
		this.subGroup = subGroup;
	}
	public List<Monitor> getMonitors() {
		return monitors;
	}
	public void setMonitors(List<Monitor> monitors) {
		this.monitors = monitors;
	}
	public String getGroupUrl() {
		return groupUrl;
	}
	public void setGroupUrl(String groupUrl) {
		this.groupUrl = groupUrl;
	}
	public List<Machine> getMachines() {
		return machines;
	}
	public void setMachines(List<Machine> machines) {
		this.machines = machines;
	}
	
}

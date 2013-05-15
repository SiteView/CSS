package com.siteview.pv.monitor.api;

import java.util.ArrayList;
import java.util.List;

import system.Collections.ICollection;
import system.Collections.IEnumerator;

import Siteview.Api.BusinessObject;

import com.siteview.pv.monitor.bean.FileTools;
import com.siteview.pv.monitor.bean.Group;
import com.siteview.pv.monitor.bean.Monitor;

public class CssMonitorIpm implements CssMonitorInterface{
	
	public List<Group> getMonitors() {
		List<Group> groups=new ArrayList<Group>();
		ICollection ico=FileTools.getBussCollection("ParentGroupId", "", "EccGroup");
		IEnumerator ien=ico.GetEnumerator();
		while(ien.MoveNext()){
			BusinessObject bo=(BusinessObject) ien.get_Current();
			Group group=new Group();
			String name=bo.GetField("GroupName").get_NativeValue().toString();
			group.setGroupUrl(name);
			group.setGroupName(name);
			group.setGroupId(bo.get_RecId());
			group.setGroupId("");
			group.getSubGroup().addAll(getSubGroup(bo.get_RecId(), group.getGroupUrl()));
			group.getMonitors().addAll(getMoniotor(bo.get_RecId(), group.getGroupUrl()));
			groups.add(group);
		}
		return groups;
	}
	
	public List<Group> getSubGroup(String parentId,String parentUrl){
		List<Group> groups=new ArrayList<Group>();
		ICollection ico=FileTools.getBussCollection("ParentGroupId", parentId, "EccGroup");
		IEnumerator ien=ico.GetEnumerator();
		while(ien.MoveNext()){
			BusinessObject bo=(BusinessObject) ien.get_Current();
			Group group=new Group();
			String name=bo.GetField("GroupName").get_NativeValue().toString();
			group.setGroupUrl(parentUrl+"\\"+name);
			group.setGroupName(name);
			group.setGroupId(bo.get_RecId());
			group.setGroupId(parentId);
			group.getSubGroup().addAll(getSubGroup(parentId, group.getGroupUrl()));
			group.getMonitors().addAll(getMoniotor(bo.get_RecId(), group.getGroupUrl()));
			groups.add(group);
		}
		return groups;
	}
	
	public List<Monitor> getMoniotor(String groupId,String groupUrl){
		List<Monitor> monitors=new ArrayList<Monitor>();
		ICollection ico=FileTools.getBussCollection("Groups", groupId, "EccMonitor");
		IEnumerator ien=ico.GetEnumerator();
		while(ien.MoveNext()){
			BusinessObject bo=(BusinessObject) ien.get_Current();
			Monitor monitor=new Monitor();
			String name=bo.GetField("Title").get_NativeValue().toString();
			monitor.setMonitorUrl(groupUrl+"\\"+name);
			monitor.setMonitorTitle(name);
			monitor.setGroupId(groupId);
			monitor.setMonitorId(bo.get_RecId());
			monitor.setMonitorType(bo.get_Name());
			name=bo.GetField("Machine").get_NativeValue().toString();
			monitor.setMonitorMachine(name==null?"":name);
			monitors.add(monitor);
		}
		return monitors;
	}
	
}

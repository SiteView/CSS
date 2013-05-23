package com.siteview.pv.monitor.api;

import java.util.ArrayList;
import java.util.List;

import system.Collections.ICollection;
import system.Collections.IEnumerator;

import Siteview.Api.BusinessObject;

import com.siteview.pv.monitor.bean.FileTools;
import com.siteview.pv.monitor.bean.Group;
import com.siteview.pv.monitor.bean.Machine;
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
			group.getMachines().addAll(getMachine(bo.get_RecId),group.getGroupUrl());
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
			if(bo.GetField("Machine").get_NativeValue().toString()!=null&&!"".equals(bo.GetField("Machine").get_NativeValue().toString())){
				Monitor monitor=new Monitor();
				String name=bo.GetField("Title").get_NativeValue().toString();
				monitor.setMonitorUrl(groupUrl+"\\"+name);
				monitor.setMonitorTitle(name);
				monitor.setGroupId(groupId);
				monitor.setMonitorId(bo.get_RecId());
				monitor.setMonitorType(bo.get_Name());
				monitor.setCounter(getCounter(bo));
				monitor.setMonitorMachine("");
				monitors.add(monitor);
			}
		}
		return monitors;
	}
	
	public List<String> getCounter(BusinessObject bo){
		List<String> counter=new ArrayList<String>();
		String type=bo.GetField("EccMonitor").get_NativeValue().toString();
		String s=bo.GetRelationship(type+"ContainsAlarm").get_Name();
		ICollection ico=FileTools.getBussCollection("Item."+s);
		IEnumerator ien=ico.GetEnumerator();
		while(ien.MoveNext()){
			BusinessObject item=(BusinessObject) ien.get_Current();
			counter.add(item.GetField("SaveItem").get_NativeValue().toString());
		}
		ico=bo.GetRelationship(type+"ContainsAlarm").GetObjects();
		ien=ico.GetEnumerator();
		while(ien.MoveNext()){
			BusinessObject alarm=(BusinessObject) ien.get_Current();
			String value=alarm.GetField("ReturnValue").get_NativeValue().toString();
			if(!counter.contains(value))
				counter.add(value);
		}
		return counter;
	}

	public List<String> getMachine(String groupId,String groupUrl){
		List<Machine> machines=new ArrayList<Machine>();
		ICollection ico=FileTools.getBussCollection("Groups", groupId, "RemoteMachine");
		IEnumerator ien=ico.GetEnumerator();
		while(ien.MoveNext()){
			BusinessObject bo=(BusinessObject) ien.get_Current();
			Machine m=new Machine();
			m.setMachineId(bo.get_RecId());
			m.setMachineIp(bo.GetField("ServerAddress").get_NativeValue().toString());
			m.setMachineTitle(bo.GetField("Title").get_NativeValue().toString());
			m.setMachineType(bo.get_name);
			m.setMachineUrl(groupUrl+"\\"+bo.GetField("Title").get_NativeValue().toString());
			m.getMonitors().addAll(getMoniotorforMachine(groupId,bo.get_RecId, m.getMachineUrl()));
			machines.add(m);
		}
	}
	
	public List<Monitor> getMoniotorforMachine(String groupid,String machineid,String machineURL){
		List<Monitor> monitors=new ArrayList<Monitor>();
		ICollection ico=FileTools.getBussCollection("Machine", machineid, "EccMonitor");
		IEnumerator ien=ico.GetEnumerator();
		while(ien.MoveNext()){
			BusinessObject bo=(BusinessObject) ien.get_Current();
				Monitor monitor=new Monitor();
				String name=bo.GetField("Title").get_NativeValue().toString();
				monitor.setMonitorUrl(machineURL+"\\"+name);
				monitor.setMonitorTitle(name);
				monitor.setGroupId(groupid);
				monitor.setMonitorId(bo.get_RecId());
				monitor.setMonitorType(bo.get_Name());
				monitor.setCounter(getCounter(bo));
				name=bo.GetField("Machine").get_NativeValue().toString();
				monitor.setMonitorMachine(name==null?"":name);
				monitors.add(monitor);
		}
		return monitors;
	}
}

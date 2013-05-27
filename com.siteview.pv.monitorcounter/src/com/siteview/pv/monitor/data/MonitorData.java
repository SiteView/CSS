package com.siteview.pv.monitor.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.ecf.monitor.ECfClient;
import com.siteview.pv.monitor.bean.Group;
import com.siteview.pv.monitor.bean.Machine;
import com.siteview.pv.monitor.bean.Monitor;

public class MonitorData {
	public static List<Group> groups;
	public static Map<String,Monitor>monitorsMap=new HashMap<String,Monitor>();
	public static Map<String,Group>groupsMap=new HashMap<String,Group>();
	public static List<Group> getMonitorMessage(){
//		monitorsMap.clear();
//		groupsMap.clear();
//		groups=ECfClient.ecfapi.getMonitors();
//		for(Group group:groups){
//			groupsMap.put(group.getGroupId(), group);
//			getMonitorsforSubgroup(group);
//			List<Monitor> list=group.getMonitors();
//			for(Monitor monitor:list){
//				monitorsMap.put(monitor.getMonitorId(), monitor);
//			}
//		}
		return groups;
	}
	
	public static void getMonitorsforSubgroup(Group groups){
		List<Group> subs=groups.getSubGroup();
		for(Group group:subs){
			groupsMap.put(group.getGroupId(), group);
			getMonitorsforSubgroup(group);
			List<Monitor> list=group.getMonitors();
			for(Monitor monitor:list){
				monitorsMap.put(monitor.getMonitorId(), monitor);
			}
		}
	}
	
	//test
	static{
		groups=new ArrayList<Group>();
		String s="test";
		for(int i=0;i<5;i++){
			Group g=new Group();
			g.setGroupId(i*i+"");
			g.setGroupName(s+i);
			Group sub=new Group();
			sub.setGroupId(i*i+1+"");
			sub.setGroupName(s+i+i);
			g.getSubGroup().add(sub);
			Machine m=new Machine();
			m.setMachineId(i+"machineid");
			m.setMachineTitle(i+"monitortitle");
			g.getMachines().add(m);
			groups.add(g);
		}
	}
}

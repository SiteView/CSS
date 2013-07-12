package com.siteview.css.topo.common;

import java.util.ArrayList;
import java.util.List;

import com.siteview.snmp.pojo.Edge;

public class TopoData {

	public static boolean isInit = false;
	public static List<Edge> edgeList = new ArrayList<Edge>();
	
	public boolean isTrue(){
		return isInit;
	}
	public List getList(){
		return edgeList;
	}
}

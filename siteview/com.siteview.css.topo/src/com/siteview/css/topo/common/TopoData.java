package com.siteview.css.topo.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.snmp.pojo.Edge;
import com.siteview.snmp.pojo.IDBody;

public class TopoData {

	
	public static boolean isInit = false;
	/**
	 * 边信息
	 */
	public static List<Edge> edgeList = new ArrayList<Edge>();
	/**
	 * 设备信息
	 */
	public static Map<String, IDBody> deviceList = new HashMap<String, IDBody>();
}

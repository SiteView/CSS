package com.siteview.itsm.nnm.scan.core.snmp.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.itsm.nnm.scan.core.snmp.common.ScanParam;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Edge;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.SubnetInfo;


public class GlobalData {
	public static boolean isInit = false;
	/**
	 * 边的集合
	 */
	public static List<Edge> edgeList = new ArrayList<Edge>();
	/**
	 * 设备集合
	 */
	public static Map<String, IDBody> deviceList = new HashMap<String, IDBody>();
	/**
	 * 判断是否用配置向导进行了扫描参数配置
	 */
	public static boolean isConfiged = false;
	/**
	 * 全局的扫描参数
	 */
	public static ScanParam scanParam = new ScanParam();
	/**
	 * 子网列表
	 */
	public static Map<SubnetInfo,Map<String,IDBody>> subnetDeviceMap = new HashMap<SubnetInfo,Map<String,IDBody>>();
	
}

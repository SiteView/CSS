package com.siteview.snmp.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.Directitem;
import com.siteview.snmp.pojo.Edge;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.pojo.IfRec;
import com.siteview.snmp.pojo.RouteItem;
import com.siteview.snmp.pojo.RouterStandbyItem;

/**
 * 一些扫描用到的常量
 * @author haiming.wang
 *
 */
public class CommonDef {

	/**
	 * 设备类型
	 */
	public static final String ROUTE_SWITCH = "0";	//三层交换
	public static final String SWITCH = "1";		//二层
	public static final String ROUTER = "2";		//路由器
	public static final String FIREWALL = "3";		//防火墙
	public static final String SERVER = "4";		//服务器 开启了snmp服务的pc也当成服务器处理。
	public static final String PC = "5";			//pc
	public static final String OTHER = "6";			//其它
	
	
	public static final String ERR_AFT_LOG = "Err_Aft_log.txt";
	public static final String ERR_ARP_LOG = "Err_Arp_log.txt";
	public static final String ERR_OID_LOG = "Err_Oid_log.txt";
	public static final String ERR_INF_LOG = "Err_Inf_log.txt";
	public static final String ERR_NBR_LOG = "Err_Nbr_log.txt";
	public static final String ERR_RT_LOG  = "Err_Rt_log.txt";
	public static final String ERR_BGP_LOG = "Err_Bgp_log.txt";
	public static final String ERR_VRRP_LOG = "Err_Vrrp_log.txt";
	public static final String ERR_DRC_LOG = "Err_DRC_log.txt";
	
	//特殊设备的oid集合 [{厂商oid,[{指标代码,指标oid}]}] 
	public static Map<String, Map<String, String>> SPECIAL_OID_LIST = new HashMap<String, Map<String,String>>();
	
	public static Map<String, RouterStandbyItem> RouterStandby_LIST = new ConcurrentHashMap<String, RouterStandbyItem>(); //vrrp,hsrp等

	//设备基本信息列表{devIP,(TYPE,SNMP,[IP],[MAC],[MASK],SYSOID,sysname)}
	public static Map<String, IDBody> DEVID_LIST = new ConcurrentHashMap<String, IDBody>();
	//设备接口属性列表 {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)])}
	public static Map<String, Pair<String, List<IfRec>>> IFPROP_LIST = new ConcurrentHashMap<String, Pair<String, List<IfRec>>>();
	//设备AFT数据列表 {sourceIP,{port,[MAC]}}
	public static Map<String, Map<String, List<String>>> AFT_LIST = new ConcurrentHashMap<String, Map<String, List<String>>>();
	//设备ARP数据列表 {sourceIP,{infInx,[(MAC,destIP)]}}
	public static Map<String, Map<String, List<Pair<String,String>>>> ARP_LIST = new ConcurrentHashMap<String, Map<String, List<Pair<String,String>>>>();
	//设备OSPF邻居列表 {sourceIP,{infInx,[destIP]}}
	public static Map<String, Map<String, List<String>>> OSPFNBR_LIST = new ConcurrentHashMap<String, Map<String, List<String>>>();
	//设备路由表 {sourceIP,{infInx,[nextIP]}}
	public static Map<String, Map<String, List<RouteItem>>> ROUTE_LIST = new ConcurrentHashMap<String, Map<String, List<RouteItem>>>();

	//typedef map<string, VRRPITEM> VRRP_LIST;

	public static List<Bgp> BGP_LIST = new ArrayList<Bgp>();
	//设备路由表 {sourceIP,{infInx,[nextIP]}}
	public static Map<String, Map<String, List<String>>> ROUTE_LIST_FRM = new ConcurrentHashMap<String, Map<String, List<String>>>();
	//规范化后的设备AFT或ARP数据 {sourceIP,{infInx,[destIP]}}
	public static Map<String, Map<String, List<String>>> FRM_AFTARP_LIST = new ConcurrentHashMap<String, Map<String, List<String>>>();

	public static Map<String, List<Directitem>> DIRECTDATA_LIST = new ConcurrentHashMap<String, List<Directitem>>();

	//设备stp列表
	public static Map<String,List<String>> STP_LIST = new ConcurrentHashMap<String,List<String>>();  
	//边列表
	public static List<Edge> EDGE_LIST;

	public static List<Pair<String,String>> SCALE_LIST = new ArrayList<Pair<String,String>>();//[ip0,ip1]

	//路由跟踪路径表 [[path1],[path2],...,[pathn]]
	public static List<List<String>> ROUTEPATH_LIST = new ArrayList<List<String>>();
	

}

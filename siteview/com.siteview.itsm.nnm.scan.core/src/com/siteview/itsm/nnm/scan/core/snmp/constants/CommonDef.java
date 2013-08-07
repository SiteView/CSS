package com.siteview.itsm.nnm.scan.core.snmp.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Bgp;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Directitem;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Edge;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IfRec;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.RouteItem;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.RouterStandbyItem;

/**
 * 一些扫锟斤拷锟矫碉拷锟侥筹拷锟斤拷
 * @author haiming.wang
 *
 */
public class CommonDef {

	/**
	 * 锟借备锟斤拷锟斤拷
	 */
	public static final String ROUTE_SWITCH = "0";	//三层交换
	public static final String SWITCH = "1";		//二层交换
	public static final String ROUTER = "2";		//路由器
	public static final String FIREWALL = "3";		//防火墙
	public static final String SERVER = "4";		//服务器，开启了snmp服务的设备
	public static final String PC = "5";			//pc
	public static final String OTHER = "6";			//其它
	
	public static String getDeviceTypeByTypeId(String typeid){
		if(typeid.equals(ROUTE_SWITCH)){
			return "三层交换";
		}
		if(typeid.equals(SWITCH)){
			return "二层交换";
		}
		if(typeid.equals(ROUTER)){
			return "路由器";
		}
		if(typeid.equals(FIREWALL)){
			return "防火墙";
		}
		if(typeid.equals(SERVER)){
			return "服务器";
		}
		if(typeid.equals(PC)){
			return "PC";
		}
		return "其它设备";
	}
	
	public static final String ERR_AFT_LOG = "Err_Aft_log.txt";
	public static final String ERR_ARP_LOG = "Err_Arp_log.txt";
	public static final String ERR_OID_LOG = "Err_Oid_log.txt";
	public static final String ERR_INF_LOG = "Err_Inf_log.txt";
	public static final String ERR_NBR_LOG = "Err_Nbr_log.txt";
	public static final String ERR_RT_LOG  = "Err_Rt_log.txt";
	public static final String ERR_BGP_LOG = "Err_Bgp_log.txt";
	public static final String ERR_VRRP_LOG = "Err_Vrrp_log.txt";
	public static final String ERR_DRC_LOG = "Err_DRC_log.txt";
	
	//锟斤拷锟斤拷锟借备锟斤拷oid锟斤拷锟斤拷 [{锟斤拷锟斤拷oid,[{指锟斤拷锟斤拷锟�指锟斤拷oid}]}] 
	public static Map<String, Map<String, String>> SPECIAL_OID_LIST = new HashMap<String, Map<String,String>>();
	
	public static Map<String, RouterStandbyItem> RouterStandby_LIST = new ConcurrentHashMap<String, RouterStandbyItem>(); //vrrp,hsrp锟斤拷

	//锟借备锟斤拷锟斤拷锟斤拷息锟叫憋拷{devIP,(TYPE,SNMP,[IP],[MAC],[MASK],SYSOID,sysname)}
	public static Map<String, IDBody> DEVID_LIST = new ConcurrentHashMap<String, IDBody>();
	//锟借备锟接匡拷锟斤拷锟斤拷锟叫憋拷 {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)])}
	public static Map<String, Pair<String, List<IfRec>>> IFPROP_LIST = new ConcurrentHashMap<String, Pair<String, List<IfRec>>>();
	//锟借备AFT锟斤拷锟斤拷锟叫憋拷 {sourceIP,{port,[MAC]}}
	public static Map<String, Map<String, List<String>>> AFT_LIST = new ConcurrentHashMap<String, Map<String, List<String>>>();
	//锟借备ARP锟斤拷锟斤拷锟叫憋拷 {sourceIP,{infInx,[(MAC,destIP)]}}
	public static Map<String, Map<String, List<Pair<String,String>>>> ARP_LIST = new ConcurrentHashMap<String, Map<String, List<Pair<String,String>>>>();
	//锟借备OSPF锟节撅拷锟叫憋拷 {sourceIP,{infInx,[destIP]}}
	public static Map<String, Map<String, List<String>>> OSPFNBR_LIST = new ConcurrentHashMap<String, Map<String, List<String>>>();
	//锟借备路锟缴憋拷 {sourceIP,{infInx,[nextIP]}}
	public static Map<String, Map<String, List<RouteItem>>> ROUTE_LIST = new ConcurrentHashMap<String, Map<String, List<RouteItem>>>();

	//typedef map<string, VRRPITEM> VRRP_LIST;

	public static List<Bgp> BGP_LIST = new ArrayList<Bgp>();
	//锟借备路锟缴憋拷 {sourceIP,{infInx,[nextIP]}}
	public static Map<String, Map<String, List<String>>> ROUTE_LIST_FRM = new ConcurrentHashMap<String, Map<String, List<String>>>();
	//锟芥范锟斤拷锟斤拷锟斤拷璞窤FT锟斤拷ARP锟斤拷锟斤拷 {sourceIP,{infInx,[destIP]}}
	public static Map<String, Map<String, List<String>>> FRM_AFTARP_LIST = new ConcurrentHashMap<String, Map<String, List<String>>>();

	public static Map<String, List<Directitem>> DIRECTDATA_LIST = new ConcurrentHashMap<String, List<Directitem>>();

	//锟借备stp锟叫憋拷
	public static Map<String,List<String>> STP_LIST = new ConcurrentHashMap<String,List<String>>();  
	//锟斤拷锟叫憋拷
	public static List<Edge> EDGE_LIST;

	public static List<Pair<String,String>> SCALE_LIST = new ArrayList<Pair<String,String>>();//[ip0,ip1]

	//路锟缴革拷锟斤拷路锟斤拷锟斤拷 [[path1],[path2],...,[pathn]]
	public static List<List<String>> ROUTEPATH_LIST = new ArrayList<List<String>>();
	

}

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
 * һЩɨ���õ��ĳ���
 * @author haiming.wang
 *
 */
public class CommonDef {

	/**
	 * �豸����
	 */
	public static final String ROUTE_SWITCH = "0";	//三层交换
	public static final String SWITCH = "1";		//二层交换
	public static final String ROUTER = "2";		//路由器
	public static final String FIREWALL = "3";		//防火墙
	public static final String SERVER = "4";		//服务器设备或者开启了snmp服务的pc
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
	
	//�����豸��oid���� [{����oid,[{ָ�����,ָ��oid}]}] 
	public static Map<String, Map<String, String>> SPECIAL_OID_LIST = new HashMap<String, Map<String,String>>();
	
	public static Map<String, RouterStandbyItem> RouterStandby_LIST = new ConcurrentHashMap<String, RouterStandbyItem>(); //vrrp,hsrp��

	//�豸������Ϣ�б�{devIP,(TYPE,SNMP,[IP],[MAC],[MASK],SYSOID,sysname)}
	public static Map<String, IDBody> DEVID_LIST = new ConcurrentHashMap<String, IDBody>();
	//�豸�ӿ������б� {devIP,(ifAmount,[(ifindex,ifType,ifDescr,ifMac,ifPort,ifSpeed)])}
	public static Map<String, Pair<String, List<IfRec>>> IFPROP_LIST = new ConcurrentHashMap<String, Pair<String, List<IfRec>>>();
	//�豸AFT�����б� {sourceIP,{port,[MAC]}}
	public static Map<String, Map<String, List<String>>> AFT_LIST = new ConcurrentHashMap<String, Map<String, List<String>>>();
	//�豸ARP�����б� {sourceIP,{infInx,[(MAC,destIP)]}}
	public static Map<String, Map<String, List<Pair<String,String>>>> ARP_LIST = new ConcurrentHashMap<String, Map<String, List<Pair<String,String>>>>();
	//�豸OSPF�ھ��б� {sourceIP,{infInx,[destIP]}}
	public static Map<String, Map<String, List<String>>> OSPFNBR_LIST = new ConcurrentHashMap<String, Map<String, List<String>>>();
	//�豸·�ɱ� {sourceIP,{infInx,[nextIP]}}
	public static Map<String, Map<String, List<RouteItem>>> ROUTE_LIST = new ConcurrentHashMap<String, Map<String, List<RouteItem>>>();

	//typedef map<string, VRRPITEM> VRRP_LIST;

	public static List<Bgp> BGP_LIST = new ArrayList<Bgp>();
	//�豸·�ɱ� {sourceIP,{infInx,[nextIP]}}
	public static Map<String, Map<String, List<String>>> ROUTE_LIST_FRM = new ConcurrentHashMap<String, Map<String, List<String>>>();
	//�淶������豸AFT��ARP���� {sourceIP,{infInx,[destIP]}}
	public static Map<String, Map<String, List<String>>> FRM_AFTARP_LIST = new ConcurrentHashMap<String, Map<String, List<String>>>();

	public static Map<String, List<Directitem>> DIRECTDATA_LIST = new ConcurrentHashMap<String, List<Directitem>>();

	//�豸stp�б�
	public static Map<String,List<String>> STP_LIST = new ConcurrentHashMap<String,List<String>>();  
	//���б�
	public static List<Edge> EDGE_LIST;

	public static List<Pair<String,String>> SCALE_LIST = new ArrayList<Pair<String,String>>();//[ip0,ip1]

	//·�ɸ���·���� [[path1],[path2],...,[pathn]]
	public static List<List<String>> ROUTEPATH_LIST = new ArrayList<List<String>>();
	

}

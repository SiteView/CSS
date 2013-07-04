package com.siteview.snmp.devicehandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Bgp;
import com.siteview.snmp.pojo.Directitem;
import com.siteview.snmp.pojo.IfRec;
import com.siteview.snmp.pojo.RouteItem;
import com.siteview.snmp.pojo.RouterStandbyItem;
import com.siteview.snmp.scan.MibScan;

public interface  IDeviceHandler {

	public static final String DEVICEMAC_MACRO 		= "15010"; // 设备MAC地址
															// "1.3.6.1.2.1.17.1.1"

	public static final String INF_AMOUNT_MACRO 	= "15011"; // 接口表：端口总数
															// "1.3.6.1.2.1.2.1"
	public static final String INF_TYPE_MACRO 		= "16036"; // 接口表：接口类型
															// "1.3.6.1.2.1.2.2.1.3"
	public static final String INF_DCR_MACRO 		= "16041"; // 接口表：端口描述
														// "1.3.6.1.2.1.2.2.1.2"
	public static final String INF_INDEXPORT_MACRO 	= "15012"; // 接口表：物理端口和逻辑索引号对应关系
																// "1.3.6.1.2.1.17.4.3.1.2"
	public static final String INF_MAC_MACRO 		= "16057"; // 接口表：接口MAC地址
														// "1.3.6.1.2.1.2.2.1.6"
	public static final String INF_SPEED_MACRO 		= "15013"; // 接口表：接口速度
															// "1.3.6.1.2.1.2.2.1.5"

	public static final String IPADDR_MASK_MACRO 	= "16075"; // IP地址表：与该实体的ip地址相关联的子网掩码
															// "1.3.6.1.2.1.4.20.1.3"
	public static final String IPADDR_INFINDEX 		= "16074"; // IP地址表：唯一标示该实体所对应的接口的索引值
															// "1.3.6.1.2.1.4.20.1.2"

	public static final String ARP_INFINDEX_MACRO 	= "16028"; // ARP表：端口索引号
																// "1.3.6.1.2.1.4.22.1.1"
	public static final String ARP_MAC_MACRO 		= "16029";// ARP表：介质依赖的物理地址
														// "1.3.6.1.2.1.4.22.1.2"
	public static final String ARP_IP_MACRO 		= "16030";// ARP表：与介质依赖的物理地址相对应的ip地址
														// "1.3.6.1.2.1.4.22.1.3"

	public static final String AFT_MAC_DTP_PORT 	= "15021";// AFT表：端口索引号
															// "1.3.6.1.2.1.17.4.3.1.2"
	public static final String AFT_MAC_DTP_MACRO 	= "16032"; // AFT表：MAC地址(DTP)
															// "1.3.6.1.2.1.17.4.3.1.1"
	public static final String AFT_MAC_QTP_MACRO 	= "15014"; // AFT表：MAC地址(QTP)
															// "1.3.6.1.2.1.17.7.1.2.2.1.2"

	public static final String ROUTE_INFINDEX_MACRO  = "16017"; // 路由表：连接目的网络的接口索引号
																// "1.3.6.1.2.1.4.21.1.2"
	public static final String ROUTE_NEXTHOPIP_MACRO = "16022"; // 路由表：目的网络的下一跳IP地址
																// "1.3.6.1.2.1.4.21.1.7"
	public static final String ROUTE_ROUTETYPE_MACRO = "16023"; // 路由表：路由类型
																// "1.3.6.1.2.1.4.21.1.8"
	public static final String ROUTE_ROUTEMASK_MACRO = "16026"; // 路由表：路由目的ip地址的子网掩码
																// "1.3.6.1.2.1.4.21.1.11"

	public static final String OSPF_INFINDEX_MACRO 		= "15015"; // OSPF表：邻居IP所在的接口索引
																// "1.3.6.1.2.1.14.10.1.2"

	public static final String BGP_LOCALADDR_MACRO 		= "15016"; // BGP表：本端IP地址
																// "1.3.6.1.2.1.15.3.1.5"
	public static final String BGP_LOCALPORT_MACRO 		= "15017"; // BGP表：本端端口
																// "1.3.6.1.2.1.15.3.1.6"
	public static final String BGP_REMOTEPORT_MACRO 	= "15018";// BGP表：对端端口
																// "1.3.6.1.2.1.15.3.1.8"

	public static final String VRRP_VIRTUALMAC_MACRO 	= "15019"; // VRRP表：虚拟MAC
																// "1.3.6.1.2.1.68.1.3.1.2"
	public static final String VRRP_VIRTUALIP_MACRO 	= "15020";// VRRP表：虚拟IP
																// "1.3.6.1.2.1.17.7.1.2.2.1.2"
	public static Map<String, Pair<String, List<IfRec>>> ifprop_List = new ConcurrentHashMap<String, Pair<String, List<IfRec>>>();

	public abstract Map<String, List<Directitem>> getDirectData(MibScan snmp,
			SnmpPara spr);

	public abstract Map<String, Map<String, List<String>>> getAftData(MibScan snmp,
			SnmpPara spr, Map<String, String> oidIndexList);

	public abstract Map<String, Map<String, List<Pair<String, String>>>> getArpData(
			MibScan snmp, SnmpPara spr, Map<String, String> oidIndexList);

	public abstract Map<String, Map<String, List<String>>> getOspfNbrData(
			MibScan snmp, SnmpPara spr);

	public abstract Map<String, Map<String, List<RouteItem>>> getRouteData(
			MibScan snmp, SnmpPara spr, Map<String, String> oidIndexList);

	public abstract List<Bgp> getBgpData(MibScan snmp, SnmpPara spr);

	public abstract Map<String, Pair<String, List<IfRec>>> getInfProp(
			MibScan snmp, SnmpPara spr, Map<String, String> oidIndexList,
			boolean bRouter);

	public abstract void getInfFlow(MibScan snmp, SnmpPara spr);

	public abstract Map<String, RouterStandbyItem> getVrrpData(MibScan snmp,
			SnmpPara spr);

	public abstract Map<String, List<String>> getStpData(MibScan snmp,
			SnmpPara spr);

}

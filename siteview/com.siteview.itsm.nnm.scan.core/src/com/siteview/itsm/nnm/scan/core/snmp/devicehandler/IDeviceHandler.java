package com.siteview.itsm.nnm.scan.core.snmp.devicehandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.siteview.itsm.nnm.scan.core.snmp.common.SnmpPara;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Bgp;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.DeviceCpuInfo;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.DeviceMemInfo;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Directitem;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IfRec;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.RouteItem;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.RouterStandbyItem;
import com.siteview.itsm.nnm.scan.core.snmp.scan.MibScan;

public interface  IDeviceHandler {

	public static final String DEVICEMAC_MACRO 		= "15010"; // �豸MAC��ַ
															// "1.3.6.1.2.1.17.1.1"

	public static final String INF_AMOUNT_MACRO 	= "15011"; // �ӿڱ��˿�����
															// "1.3.6.1.2.1.2.1"
	public static final String INF_TYPE_MACRO 		= "16036"; // �ӿڱ��ӿ�����
															// "1.3.6.1.2.1.2.2.1.3"
	public static final String INF_DCR_MACRO 		= "16041"; // �ӿڱ��˿�����
														// "1.3.6.1.2.1.2.2.1.2"
	public static final String INF_INDEXPORT_MACRO 	= "15012"; // �ӿڱ�����˿ں��߼������Ŷ�Ӧ��ϵ
																// "1.3.6.1.2.1.17.4.3.1.2"
	public static final String INF_MAC_MACRO 		= "16057"; // �ӿڱ��ӿ�MAC��ַ
														// "1.3.6.1.2.1.2.2.1.6"
	public static final String INF_SPEED_MACRO 		= "15013"; // �ӿڱ��ӿ��ٶ�
															// "1.3.6.1.2.1.2.2.1.5"

	public static final String IPADDR_MASK_MACRO 	= "16075"; // IP��ַ�����ʵ���ip��ַ���������������
															// "1.3.6.1.2.1.4.20.1.3"
	public static final String IPADDR_INFINDEX 		= "16074"; // IP��ַ��Ψһ��ʾ��ʵ������Ӧ�Ľӿڵ�����ֵ
															// "1.3.6.1.2.1.4.20.1.2"

	public static final String ARP_INFINDEX_MACRO 	= "16028"; // ARP���˿�������
																// "1.3.6.1.2.1.4.22.1.1"
	public static final String ARP_MAC_MACRO 		= "16029";// ARP�����������������ַ
														// "1.3.6.1.2.1.4.22.1.2"
	public static final String ARP_IP_MACRO 		= "16030";// ARP������������������ַ���Ӧ��ip��ַ
														// "1.3.6.1.2.1.4.22.1.3"

	public static final String AFT_MAC_DTP_PORT 	= "15021";// AFT���˿�������
															// "1.3.6.1.2.1.17.4.3.1.2"
	public static final String AFT_MAC_DTP_MACRO 	= "16032"; // AFT��MAC��ַ(DTP)
															// "1.3.6.1.2.1.17.4.3.1.1"
	public static final String AFT_MAC_QTP_MACRO 	= "15014"; // AFT��MAC��ַ(QTP)
															// "1.3.6.1.2.1.17.7.1.2.2.1.2"

	public static final String ROUTE_INFINDEX_MACRO  = "16017"; // ·�ɱ�����Ŀ������Ľӿ�������
																// "1.3.6.1.2.1.4.21.1.2"
	public static final String ROUTE_NEXTHOPIP_MACRO = "16022"; // ·�ɱ�Ŀ���������һ��IP��ַ
																// "1.3.6.1.2.1.4.21.1.7"
	public static final String ROUTE_ROUTETYPE_MACRO = "16023"; // ·�ɱ�·������
																// "1.3.6.1.2.1.4.21.1.8"
	public static final String ROUTE_ROUTEMASK_MACRO = "16026"; // ·�ɱ�·��Ŀ��ip��ַ����������
																// "1.3.6.1.2.1.4.21.1.11"

	public static final String OSPF_INFINDEX_MACRO 		= "15015"; // OSPF���ھ�IP���ڵĽӿ�����
																// "1.3.6.1.2.1.14.10.1.2"

	public static final String BGP_LOCALADDR_MACRO 		= "15016"; // BGP������IP��ַ
																// "1.3.6.1.2.1.15.3.1.5"
	public static final String BGP_LOCALPORT_MACRO 		= "15017"; // BGP�����˶˿�
																// "1.3.6.1.2.1.15.3.1.6"
	public static final String BGP_REMOTEPORT_MACRO 	= "15018";// BGP���Զ˶˿�
																// "1.3.6.1.2.1.15.3.1.8"

	public static final String VRRP_VIRTUALMAC_MACRO 	= "15019"; // VRRP������MAC
																// "1.3.6.1.2.1.68.1.3.1.2"
	public static final String VRRP_VIRTUALIP_MACRO 	= "15020";// VRRP������IP
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
	
	public abstract DeviceCpuInfo getDeviceCpuInfo(MibScan snmp,SnmpPara spr);
	
	public abstract DeviceMemInfo getdeviceMemInfo(MibScan snmp,SnmpPara spr);

}

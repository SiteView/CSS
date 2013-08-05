package com.siteview.itsm.nnm.scan.core.snmp.constants;

import org.snmp4j.smi.OID;

public class OIDConstants {
	
	/**
	 * 锟接口憋拷
	 */
	public static final OID INTERFACE_ifTable = new OID("1.3.6.1.2.1.2.2.0");
	/**
	 * 锟斤拷锟斤拷一锟斤拷锟接口讹拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry = new OID("1.3.6.1.2.1.2.2.1");
	/**
	 * 锟接匡拷锟斤拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_Index = new OID("1.3.6.1.2.1.2.2.1.1.0");
	/**
	 * 锟接匡拷实锟斤拷锟斤拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_Descr = new OID("1.3.6.1.2.1.2.2.1.2.0");
	/**
	 * 锟接匡拷锟斤拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_Type = new OID("1.3.6.1.2.1.2.2.1.3.0");
	/**
	 * 锟接口匡拷锟皆凤拷锟酵碉拷锟斤拷锟斤拷锟斤拷荼锟�
	 */
	public static final OID INTERFACE_ifEntry_Mtu = new OID("1.3.6.1.2.1.2.2.1.4.0");
	/**
	 * 锟接口端匡拷锟劫讹拷
	 */
	public static final OID INTERFACE_ifEntry_Speed = new OID("1.3.6.1.2.1.2.2.1.5.0");
	/**
	 * 锟接匡拷锟斤拷锟斤拷锟街�
	 */
	public static final OID INTERFACE_ifEntry_PhysAddress = new OID("1.3.6.1.2.1.2.2.1.6.0");
	/**
	 * 锟接匡拷锟斤拷锟斤拷锟斤拷状态
	 * 锟缴讹拷写
	 * 锟斤拷锟斤拷状态up(1) down(2) testing(3)
	 */
	public static final OID INTERFACE_ifEntry_AdminStatus = new OID("1.3.6.1.2.1.2.2.1.7.0");
	/**
	 * 锟接口碉拷前锟斤拷状态
	 * 锟斤拷锟斤拷状态up(1) down(2) testing(3)
	 * mgmt/mib-2/interfaces/ifTable/ifEntry/ifOperStatus
	 * 锟斤拷值为Up(1)锟斤拷Downd(2)锟斤拷Testing(3)锟斤拷只锟斤拷枚锟斤拷锟酵ｏ拷锟斤拷锟斤拷锟斤拷锟剿接口的碉拷前状态锟斤拷
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷校锟斤拷硕锟斤拷锟斤拷锟皆和接口憋拷锟斤拷惟一锟侥匡拷写锟斤拷锟斤拷ifAdminStatus锟斤拷锟斤拷锟揭伙拷锟饺凤拷锟斤拷涌诘牡锟角白刺拷锟�
	 * ifOperStatus	ifAdminStatus	锟斤拷锟斤拷
		Up(1)		Up(1)			锟斤拷锟斤拷锟斤拷锟斤拷
		Down(2)		Up(1)			失锟斤拷
		Down(2)		Down(2)			Down锟斤拷锟截闭ｏ拷
		Testing(3)	Testing(3)		Testing锟斤拷锟斤拷锟皆ｏ拷

	 */
	public static final OID INTERFACE_ifEntry_OperStatus = new OID("1.3.6.1.2.1.2.2.1.8.0");
	/**
	 * 锟接口接匡拷锟斤拷锟斤拷锟侥ｏ拷锟接口刚匡拷始锟斤拷始锟斤拷时锟斤拷值为0
	 */
	public static final OID INTERFACE_ifEntry_LastChange = new OID("1.3.6.1.2.1.2.2.1.9.0");
	/**
	 * 锟接口斤拷锟秸碉拷锟斤拷锟街斤拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_InOctets = new OID("1.3.6.1.2.1.2.2.1.10.0");
	/**
	 * 锟接口碉拷锟斤拷锟姐播锟斤拷锟斤拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_InUcastPkts = new OID("1.3.6.1.2.1.2.2.1.11.0");
	/**
	 * 锟接口非碉拷锟斤拷锟姐播锟斤拷锟斤拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_InNUcastPkts = new OID("1.3.6.1.2.1.2.2.1.12.0");
	/**
	 * 锟接口讹拷锟斤拷锟斤拷锟斤拷锟捷帮拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_InDiscards = new OID("1.3.6.1.2.1.2.2.1.13.0");
	/**
	 * 锟接口帮拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷莅锟�
	 */
	public static final OID INTERFACE_ifEntry_InErrors = new OID("1.3.6.1.2.1.2.2.1.14.0");
	/**
	 * 锟接匡拷末知锟斤拷支锟街碉拷协锟斤拷锟斤拷锟斤拷莅锟斤拷锟斤拷锟�
	 */
	public static final OID INTERFACE_ifEntry_InUnknownProtos = new OID("1.3.6.1.2.1.2.2.1.15.0");
	/**
	 * 锟接口凤拷锟酵碉拷锟斤拷锟街斤拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_OutOctets = new OID("1.3.6.1.2.1.2.2.1.16.0");
	/**
	 * 锟接口凤拷锟酵的碉拷锟斤拷锟姐播锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_OutUcastPkts = new OID("1.3.6.1.2.1.2.2.1.17.0");
	/**
	 * 锟接口凤拷锟酵的非碉拷锟斤拷锟姐播锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_OutNUcastPkts = new OID("1.3.6.1.2.1.2.2.1.18.0");
	/**
	 * 锟接口凤拷锟酵的憋拷锟斤拷锟斤拷锟斤拷锟斤拷锟捷帮拷锟斤拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_OutDiscards = new OID("1.3.6.1.2.1.2.2.1.19.0");
	/**
	 * 锟接口凤拷锟酵的达拷锟斤拷锟斤拷锟斤拷锟�
	 */
	public static final OID INTERFACE_ifEntry_OutErrors = new OID("1.3.6.1.2.1.2.2.1.20.0");
	/**
	 * 锟接口凤拷锟酵碉拷锟斤拷锟捷帮拷锟斤拷锟叫筹拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_OutQLen = new OID("1.3.6.1.2.1.2.2.1.21.0");
	/**
	 * 锟接匡拷实锟斤拷锟斤拷锟斤拷
	 */
	public static final OID INTERFACE_ifEntry_Specific = new OID("1.3.6.1.2.1.2.2.1.22.0");
	
	
	
	
	public static final String sysPrefix = "1.3.6.1.2.1.1";
	/**
	 * 系统锟斤拷锟斤拷
	 */
	public static final OID sysDescr = new OID(sysPrefix+".1.0");
	
	/**
	 * 系统OID
	 */
	public static final OID sysObjectId = new OID(sysPrefix+".2.0");
	/**
	 * 系统锟斤拷锟斤拷时锟斤拷
	 */
	public static final OID sysUptime = new OID(sysPrefix+".3.0");
	/**
	 * 系统锟斤拷锟斤拷锟斤拷系锟斤拷
	 */
	public static final OID sysContact = new OID(sysPrefix+".4.0");
	/**
	 * 系统锟斤拷锟斤拷
	 */
	public static final OID sysName = new OID(sysPrefix+".5.0");
	/**
	 * 锟借备锟斤拷锟斤拷位锟斤拷
	 */
	public static final OID sysLocation = new OID(sysPrefix+".6.0");
	/**
	 *锟斤拷锟借备锟结供锟侥凤拷锟斤拷
	 */
	public static final OID sysServices = new OID(sysPrefix+".7.0");
	/**
	 * system锟斤拷OID锟斤拷锟斤拷
	 */
	public static final OID[] sysOIDs = new OID[]{
		sysDescr,
		sysObjectId,
		sysUptime,
		sysContact,
		sysName,
		sysLocation,
		sysServices
	};
	/**
	 * 锟接口憋拷oid锟街达拷前缀
	 */
	public static final String ifPrefix = "1.3.6.1.2.1.2";
	/**
	 * 锟接匡拷锟斤拷锟斤拷
	 */
	public static final OID ifNumber = new OID(ifPrefix + ".1.0");
	/**
	 * 锟接口憋拷OID
	 */
	public static final OID ifTable = new OID(ifPrefix + ".2.1");
	/**
	 * ip锟斤拷oid锟街达拷前缀
	 */
	public static final String ipTablePrefix = "1.3.6.1.2.1.4";
	/**
	 * ip锟斤拷OID
	 */
	public static final OID ipTable = new OID(ipTablePrefix);
	/**
	 * at锟斤拷锟街达拷前缀
	 */
	public static final String atTablePrefix = "1.3.6.1.2.1.3";
	/**
	 * at锟斤拷OID
	 */
	public static final OID atTable = new OID(atTablePrefix + ".1.1");//1.3.6.1.2.1.3.1.1 
	/**
	 * ip路锟缴憋拷oid
	 */
	public static final OID ipRouteTable = new OID(ipTablePrefix + ".21.1.1");//1.3.6.1.2.1.4.21.1
	/**
	 * ip路锟缴憋拷oid
	 */
	public static final OID ipAddressTable = new OID(ipTablePrefix + ".20.1");//1.3.6.1.2.1.4.21.1
	/**
	 * ip路锟缴憋拷oid
	 */
	public static final OID ipNetToMediaTable = new OID(ipTablePrefix + ".22.1");//1.3.6.1.2.1.4.21.1
	
	
	public static final OID ipForwarding 		= new OID(ipTablePrefix + ".1.0");
	public static final OID ipDefaultTTL 		= new OID(ipTablePrefix + ".2.0");
	public static final OID ipInReceives 		= new OID(ipTablePrefix + ".3.0");
	public static final OID ipInHdrErrors 		= new OID(ipTablePrefix + ".4.0");
	public static final OID ipInAddrErrors 		= new OID(ipTablePrefix + ".5.0");
	public static final OID ipForwDatagrams 	= new OID(ipTablePrefix + ".6.0");
	public static final OID ipInUnknownProtos 	= new OID(ipTablePrefix + ".7.0");
	public static final OID ipInDiscards 		= new OID(ipTablePrefix + ".8.0");
	public static final OID ipInDelivers 		= new OID(ipTablePrefix + ".9.0");
	public static final OID ipOutRequests 		= new OID(ipTablePrefix + ".10.0");
	public static final OID ipOutDiscards 		= new OID(ipTablePrefix + ".11.0");
	public static final OID ipOutNoRoutes 		= new OID(ipTablePrefix + ".12.0");
	public static final OID ipReasmTimeout 		= new OID(ipTablePrefix + ".13.0");
	public static final OID ipReasmReqds 		= new OID(ipTablePrefix + ".14.0");
	public static final OID ipReasmOKs 			= new OID(ipTablePrefix + ".15.0");
	public static final OID ipReasmFails 		= new OID(ipTablePrefix + ".16.0");
	public static final OID ipFragOKs 			= new OID(ipTablePrefix + ".17.0");
	public static final OID ipFragFails 		= new OID(ipTablePrefix + ".18.0");
	public static final OID ipFragCreates 		= new OID(ipTablePrefix + ".19.0");
	public static final OID ipRoutingDiscards 	= new OID(ipTablePrefix + ".23.0");
	
	public static final OID[] ipBaseinfoOIDs = new OID[]{
		ipForwarding,
		ipDefaultTTL,
		ipInReceives,
		ipInHdrErrors,
		ipInAddrErrors,
		ipForwDatagrams,
		ipInUnknownProtos,
		ipInDiscards,
		ipInDelivers,
		ipOutRequests,
		ipOutDiscards,
		ipOutNoRoutes,
		ipReasmTimeout,
		ipReasmReqds,
		ipReasmOKs,
		ipReasmFails,
		ipFragOKs,
		ipFragFails,
		ipFragCreates,
		ipRoutingDiscards,
	};
}

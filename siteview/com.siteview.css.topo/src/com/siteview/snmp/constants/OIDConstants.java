package com.siteview.snmp.constants;

import org.snmp4j.smi.OID;

public class OIDConstants {
	
	/**
	 * 接口表
	 */
	public static final OID INTERFACE_ifTable = new OID("1.3.6.1.2.1.2.2.0");
	/**
	 * 代表一个接口对象
	 */
	public static final OID INTERFACE_ifEntry = new OID("1.3.6.1.2.1.2.2.1");
	/**
	 * 接口索引
	 */
	public static final OID INTERFACE_ifEntry_Index = new OID("1.3.6.1.2.1.2.2.1.1.0");
	/**
	 * 接口实体描述
	 */
	public static final OID INTERFACE_ifEntry_Descr = new OID("1.3.6.1.2.1.2.2.1.2.0");
	/**
	 * 接口类型
	 */
	public static final OID INTERFACE_ifEntry_Type = new OID("1.3.6.1.2.1.2.2.1.3.0");
	/**
	 * 接口可以发送的最大数据报
	 */
	public static final OID INTERFACE_ifEntry_Mtu = new OID("1.3.6.1.2.1.2.2.1.4.0");
	/**
	 * 接口端口速度
	 */
	public static final OID INTERFACE_ifEntry_Speed = new OID("1.3.6.1.2.1.2.2.1.5.0");
	/**
	 * 接口物理地址
	 */
	public static final OID INTERFACE_ifEntry_PhysAddress = new OID("1.3.6.1.2.1.2.2.1.6.0");
	/**
	 * 接口期望的状态
	 * 可读写
	 * 三种状态up(1) down(2) testing(3)
	 */
	public static final OID INTERFACE_ifEntry_AdminStatus = new OID("1.3.6.1.2.1.2.2.1.7.0");
	/**
	 * 接口当前的状态
	 * 三种状态up(1) down(2) testing(3)
	 * mgmt/mib-2/interfaces/ifTable/ifEntry/ifOperStatus
	 * 其值为Up(1)、Downd(2)、Testing(3)的只读枚举型，它描述了接口的当前状态。
	 * 在网络管理中，此对象可以和接口表中惟一的可写对象ifAdminStatus结合在一起，确定接口的当前状态。
	 * ifOperStatus	ifAdminStatus	含义
		Up(1)		Up(1)			正常运行
		Down(2)		Up(1)			失败
		Down(2)		Down(2)			Down（关闭）
		Testing(3)	Testing(3)		Testing（测试）

	 */
	public static final OID INTERFACE_ifEntry_OperStatus = new OID("1.3.6.1.2.1.2.2.1.8.0");
	/**
	 * 接口接口启动的，接口刚开始初始发时，值为0
	 */
	public static final OID INTERFACE_ifEntry_LastChange = new OID("1.3.6.1.2.1.2.2.1.9.0");
	/**
	 * 接口接收的总字节数
	 */
	public static final OID INTERFACE_ifEntry_InOctets = new OID("1.3.6.1.2.1.2.2.1.10.0");
	/**
	 * 接口单播广播包数量
	 */
	public static final OID INTERFACE_ifEntry_InUcastPkts = new OID("1.3.6.1.2.1.2.2.1.11.0");
	/**
	 * 接口非单播广播包数量
	 */
	public static final OID INTERFACE_ifEntry_InNUcastPkts = new OID("1.3.6.1.2.1.2.2.1.12.0");
	/**
	 * 接口丢弃的数据包数
	 */
	public static final OID INTERFACE_ifEntry_InDiscards = new OID("1.3.6.1.2.1.2.2.1.13.0");
	/**
	 * 接口包含错误的数据包
	 */
	public static final OID INTERFACE_ifEntry_InErrors = new OID("1.3.6.1.2.1.2.2.1.14.0");
	/**
	 * 接口末知或不支持的协议的数据包数量
	 */
	public static final OID INTERFACE_ifEntry_InUnknownProtos = new OID("1.3.6.1.2.1.2.2.1.15.0");
	/**
	 * 接口发送的总字节数
	 */
	public static final OID INTERFACE_ifEntry_OutOctets = new OID("1.3.6.1.2.1.2.2.1.16.0");
	/**
	 * 接口发送的单播广播数
	 */
	public static final OID INTERFACE_ifEntry_OutUcastPkts = new OID("1.3.6.1.2.1.2.2.1.17.0");
	/**
	 * 接口发送的非单播广播数
	 */
	public static final OID INTERFACE_ifEntry_OutNUcastPkts = new OID("1.3.6.1.2.1.2.2.1.18.0");
	/**
	 * 接口发送的被丢弃的数据包数量
	 */
	public static final OID INTERFACE_ifEntry_OutDiscards = new OID("1.3.6.1.2.1.2.2.1.19.0");
	/**
	 * 接口发送的错误包数量
	 */
	public static final OID INTERFACE_ifEntry_OutErrors = new OID("1.3.6.1.2.1.2.2.1.20.0");
	/**
	 * 接口发送的数据包队列长度
	 */
	public static final OID INTERFACE_ifEntry_OutQLen = new OID("1.3.6.1.2.1.2.2.1.21.0");
	/**
	 * 接口实体描述
	 */
	public static final OID INTERFACE_ifEntry_Specific = new OID("1.3.6.1.2.1.2.2.1.22.0");
	
	
	
	
	public static final String sysPrefix = "1.3.6.1.2.1.1";
	/**
	 * 系统描述
	 */
	public static final OID sysDescr = new OID(sysPrefix+".1.0");
	
	/**
	 * 系统OID
	 */
	public static final OID sysObjectId = new OID(sysPrefix+".2.0");
	/**
	 * 系统开启时间
	 */
	public static final OID sysUptime = new OID(sysPrefix+".3.0");
	/**
	 * 系统厂商联系人
	 */
	public static final OID sysContact = new OID(sysPrefix+".4.0");
	/**
	 * 系统名称
	 */
	public static final OID sysName = new OID(sysPrefix+".5.0");
	/**
	 * 设备物理位置
	 */
	public static final OID sysLocation = new OID(sysPrefix+".6.0");
	/**
	 *该设备提供的服务
	 */
	public static final OID sysServices = new OID(sysPrefix+".7.0");
	/**
	 * system组OID集合
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
	 * 接口表oid字串前缀
	 */
	public static final String ifPrefix = "1.3.6.1.2.1.2";
	/**
	 * 接口总数
	 */
	public static final OID ifNumber = new OID(ifPrefix + ".1.0");
	/**
	 * 接口表OID
	 */
	public static final OID ifTable = new OID(ifPrefix + ".2.1");
	/**
	 * ip表oid字串前缀
	 */
	public static final String ipTablePrefix = "1.3.6.1.2.1.4";
	/**
	 * ip表OID
	 */
	public static final OID ipTable = new OID(ipTablePrefix);
	/**
	 * at表字串前缀
	 */
	public static final String atTablePrefix = "1.3.6.1.2.1.3";
	/**
	 * at表OID
	 */
	public static final OID atTable = new OID(atTablePrefix + ".1.1");//1.3.6.1.2.1.3.1.1 
	/**
	 * ip路由表oid
	 */
	public static final OID ipRouteTable = new OID(ipTablePrefix + ".21.1.1");//1.3.6.1.2.1.4.21.1
	/**
	 * ip路由表oid
	 */
	public static final OID ipAddressTable = new OID(ipTablePrefix + ".20.1");//1.3.6.1.2.1.4.21.1
	/**
	 * ip路由表oid
	 */
	public static final OID ipNetToMediaTable = new OID(ipTablePrefix + ".22.1");//1.3.6.1.2.1.4.21.1
	
	
	public static final OID ipForwarding = new OID(ipTablePrefix +".1.0");
	public static final OID ipDefaultTTL = new OID(ipTablePrefix +".2.0");
	public static final OID ipInReceives = new OID(ipTablePrefix +".3.0");
	public static final OID ipInHdrErrors = new OID(ipTablePrefix +".4.0");
	public static final OID ipInAddrErrors = new OID(ipTablePrefix +".5.0");
	public static final OID ipForwDatagrams = new OID(ipTablePrefix +".6.0");
	public static final OID ipInUnknownProtos = new OID(ipTablePrefix +".7.0");
	public static final OID ipInDiscards = new OID(ipTablePrefix +".8.0");
	public static final OID ipInDelivers = new OID(ipTablePrefix +".9.0");
	public static final OID ipOutRequests = new OID(ipTablePrefix +".10.0");
	public static final OID ipOutDiscards = new OID(ipTablePrefix +".11.0");
	public static final OID ipOutNoRoutes = new OID(ipTablePrefix +".12.0");
	public static final OID ipReasmTimeout = new OID(ipTablePrefix +".13.0");
	public static final OID ipReasmReqds = new OID(ipTablePrefix +".14.0");
	public static final OID ipReasmOKs = new OID(ipTablePrefix +".15.0");
	public static final OID ipReasmFails = new OID(ipTablePrefix +".16.0");
	public static final OID ipFragOKs = new OID(ipTablePrefix +".17.0");
	public static final OID ipFragFails = new OID(ipTablePrefix +".18.0");
	public static final OID ipFragCreates = new OID(ipTablePrefix +".19.0");
	public static final OID ipRoutingDiscards = new OID(ipTablePrefix +".23.0");
	
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

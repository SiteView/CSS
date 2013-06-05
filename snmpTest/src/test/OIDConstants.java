package test;

import org.snmp4j.smi.OID;

public class OIDConstants {
	/**interface 组 1.3.6.1.2.1.2
	 *	mgmt/mib-2/interfaces/ifNumber  表示一个设备上有的接口数
	 */
	public static final OID INTERFACE_ifNumber = new OID("1.3.6.1.2.1.2.1.0");
	/**
	 * mgmt/mib-2/interfaces/ifTable/ifEntry/ifDescr 
	 *这是接口的文本描述符，为只读显示串，它描述了接口的厂商名、
	 *产品名和硬件接口的版本号。
	 */ 
	public static final OID INTERFACE_ifDescr = new OID("1.3.6.1.2.1.2.2.1.2.0");
	/**
	 * mgmt/mib-2/interfaces/ifTable/ifEntry/ifInOctets
	 * 为只读的计数器（Counter），它定义在接口上收到的字节总数（包括帧格式）
	 */
	public static final OID INTERFACE_ifInOctets = new OID("1.3.6.1.2.1.2.2.1.10.0");
	/**
	 * mgmt/mib-2/interfaces/ifTable/ifEntry/ifOutOctets
	 * 为只读的计数器（Counter），它显示在接口上输出的字节总数（包括帧格式）
	 */
	public static final OID INTERFACE_ifOutOctets = new OID("1.3.6.1.2.1.2.2.1.16.0");
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
	
	public static OID getInterfaceIfnumber() {
		return INTERFACE_ifNumber;
	}
	public static OID getInterfaceIfdescr() {
		return INTERFACE_ifDescr;
	}
	public static OID getInterfaceIfinoctets() {
		return INTERFACE_ifInOctets;
	}
	public static OID getInterfaceIfoutoctets() {
		return INTERFACE_ifOutOctets;
	}
	public static OID getInterfaceIftable() {
		return INTERFACE_ifTable;
	}
	public static OID getInterfaceIfentry() {
		return INTERFACE_ifEntry;
	}
	public static OID getInterfaceIfentryIndex() {
		return INTERFACE_ifEntry_Index;
	}
	public static OID getInterfaceIfentryDescr() {
		return INTERFACE_ifEntry_Descr;
	}
	public static OID getInterfaceIfentryType() {
		return INTERFACE_ifEntry_Type;
	}
	public static OID getInterfaceIfentryMtu() {
		return INTERFACE_ifEntry_Mtu;
	}
	public static OID getInterfaceIfentrySpeed() {
		return INTERFACE_ifEntry_Speed;
	}
	public static OID getInterfaceIfentryPhysaddress() {
		return INTERFACE_ifEntry_PhysAddress;
	}
	public static OID getInterfaceIfentryAdminstatus() {
		return INTERFACE_ifEntry_AdminStatus;
	}
	public static OID getInterfaceIfentryOperstatus() {
		return INTERFACE_ifEntry_OperStatus;
	}
	public static OID getInterfaceIfentryLastchange() {
		return INTERFACE_ifEntry_LastChange;
	}
	public static OID getInterfaceIfentryInoctets() {
		return INTERFACE_ifEntry_InOctets;
	}
	public static OID getInterfaceIfentryInucastpkts() {
		return INTERFACE_ifEntry_InUcastPkts;
	}
	public static OID getInterfaceIfentryInnucastpkts() {
		return INTERFACE_ifEntry_InNUcastPkts;
	}
	public static OID getInterfaceIfentryIndiscards() {
		return INTERFACE_ifEntry_InDiscards;
	}
	public static OID getInterfaceIfentryInerrors() {
		return INTERFACE_ifEntry_InErrors;
	}
	public static OID getInterfaceIfentryInunknownprotos() {
		return INTERFACE_ifEntry_InUnknownProtos;
	}
	public static OID getInterfaceIfentryOutoctets() {
		return INTERFACE_ifEntry_OutOctets;
	}
	public static OID getInterfaceIfentryOutucastpkts() {
		return INTERFACE_ifEntry_OutUcastPkts;
	}
	public static OID getInterfaceIfentryOutnucastpkts() {
		return INTERFACE_ifEntry_OutNUcastPkts;
	}
	public static OID getInterfaceIfentryOutdiscards() {
		return INTERFACE_ifEntry_OutDiscards;
	}
	public static OID getInterfaceIfentryOuterrors() {
		return INTERFACE_ifEntry_OutErrors;
	}
	public static OID getInterfaceIfentryOutqlen() {
		return INTERFACE_ifEntry_OutQLen;
	}
	public static OID getInterfaceIfentrySpecific() {
		return INTERFACE_ifEntry_Specific;
	}
}

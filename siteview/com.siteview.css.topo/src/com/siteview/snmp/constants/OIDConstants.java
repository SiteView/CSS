package com.siteview.snmp.constants;

import org.snmp4j.smi.OID;

public class OIDConstants {
	
	/**
	 * �ӿڱ�
	 */
	public static final OID INTERFACE_ifTable = new OID("1.3.6.1.2.1.2.2.0");
	/**
	 * ����һ���ӿڶ���
	 */
	public static final OID INTERFACE_ifEntry = new OID("1.3.6.1.2.1.2.2.1");
	/**
	 * �ӿ�����
	 */
	public static final OID INTERFACE_ifEntry_Index = new OID("1.3.6.1.2.1.2.2.1.1.0");
	/**
	 * �ӿ�ʵ������
	 */
	public static final OID INTERFACE_ifEntry_Descr = new OID("1.3.6.1.2.1.2.2.1.2.0");
	/**
	 * �ӿ�����
	 */
	public static final OID INTERFACE_ifEntry_Type = new OID("1.3.6.1.2.1.2.2.1.3.0");
	/**
	 * �ӿڿ��Է��͵�������ݱ�
	 */
	public static final OID INTERFACE_ifEntry_Mtu = new OID("1.3.6.1.2.1.2.2.1.4.0");
	/**
	 * �ӿڶ˿��ٶ�
	 */
	public static final OID INTERFACE_ifEntry_Speed = new OID("1.3.6.1.2.1.2.2.1.5.0");
	/**
	 * �ӿ������ַ
	 */
	public static final OID INTERFACE_ifEntry_PhysAddress = new OID("1.3.6.1.2.1.2.2.1.6.0");
	/**
	 * �ӿ�������״̬
	 * �ɶ�д
	 * ����״̬up(1) down(2) testing(3)
	 */
	public static final OID INTERFACE_ifEntry_AdminStatus = new OID("1.3.6.1.2.1.2.2.1.7.0");
	/**
	 * �ӿڵ�ǰ��״̬
	 * ����״̬up(1) down(2) testing(3)
	 * mgmt/mib-2/interfaces/ifTable/ifEntry/ifOperStatus
	 * ��ֵΪUp(1)��Downd(2)��Testing(3)��ֻ��ö���ͣ��������˽ӿڵĵ�ǰ״̬��
	 * ����������У��˶�����Ժͽӿڱ���Ωһ�Ŀ�д����ifAdminStatus�����һ��ȷ���ӿڵĵ�ǰ״̬��
	 * ifOperStatus	ifAdminStatus	����
		Up(1)		Up(1)			��������
		Down(2)		Up(1)			ʧ��
		Down(2)		Down(2)			Down���رգ�
		Testing(3)	Testing(3)		Testing�����ԣ�

	 */
	public static final OID INTERFACE_ifEntry_OperStatus = new OID("1.3.6.1.2.1.2.2.1.8.0");
	/**
	 * �ӿڽӿ������ģ��ӿڸտ�ʼ��ʼ��ʱ��ֵΪ0
	 */
	public static final OID INTERFACE_ifEntry_LastChange = new OID("1.3.6.1.2.1.2.2.1.9.0");
	/**
	 * �ӿڽ��յ����ֽ���
	 */
	public static final OID INTERFACE_ifEntry_InOctets = new OID("1.3.6.1.2.1.2.2.1.10.0");
	/**
	 * �ӿڵ����㲥������
	 */
	public static final OID INTERFACE_ifEntry_InUcastPkts = new OID("1.3.6.1.2.1.2.2.1.11.0");
	/**
	 * �ӿڷǵ����㲥������
	 */
	public static final OID INTERFACE_ifEntry_InNUcastPkts = new OID("1.3.6.1.2.1.2.2.1.12.0");
	/**
	 * �ӿڶ��������ݰ���
	 */
	public static final OID INTERFACE_ifEntry_InDiscards = new OID("1.3.6.1.2.1.2.2.1.13.0");
	/**
	 * �ӿڰ�����������ݰ�
	 */
	public static final OID INTERFACE_ifEntry_InErrors = new OID("1.3.6.1.2.1.2.2.1.14.0");
	/**
	 * �ӿ�ĩ֪��֧�ֵ�Э������ݰ�����
	 */
	public static final OID INTERFACE_ifEntry_InUnknownProtos = new OID("1.3.6.1.2.1.2.2.1.15.0");
	/**
	 * �ӿڷ��͵����ֽ���
	 */
	public static final OID INTERFACE_ifEntry_OutOctets = new OID("1.3.6.1.2.1.2.2.1.16.0");
	/**
	 * �ӿڷ��͵ĵ����㲥��
	 */
	public static final OID INTERFACE_ifEntry_OutUcastPkts = new OID("1.3.6.1.2.1.2.2.1.17.0");
	/**
	 * �ӿڷ��͵ķǵ����㲥��
	 */
	public static final OID INTERFACE_ifEntry_OutNUcastPkts = new OID("1.3.6.1.2.1.2.2.1.18.0");
	/**
	 * �ӿڷ��͵ı����������ݰ�����
	 */
	public static final OID INTERFACE_ifEntry_OutDiscards = new OID("1.3.6.1.2.1.2.2.1.19.0");
	/**
	 * �ӿڷ��͵Ĵ��������
	 */
	public static final OID INTERFACE_ifEntry_OutErrors = new OID("1.3.6.1.2.1.2.2.1.20.0");
	/**
	 * �ӿڷ��͵����ݰ����г���
	 */
	public static final OID INTERFACE_ifEntry_OutQLen = new OID("1.3.6.1.2.1.2.2.1.21.0");
	/**
	 * �ӿ�ʵ������
	 */
	public static final OID INTERFACE_ifEntry_Specific = new OID("1.3.6.1.2.1.2.2.1.22.0");
	
	
	
	
	public static final String sysPrefix = "1.3.6.1.2.1.1";
	/**
	 * ϵͳ����
	 */
	public static final OID sysDescr = new OID(sysPrefix+".1.0");
	
	/**
	 * ϵͳOID
	 */
	public static final OID sysObjectId = new OID(sysPrefix+".2.0");
	/**
	 * ϵͳ����ʱ��
	 */
	public static final OID sysUptime = new OID(sysPrefix+".3.0");
	/**
	 * ϵͳ������ϵ��
	 */
	public static final OID sysContact = new OID(sysPrefix+".4.0");
	/**
	 * ϵͳ����
	 */
	public static final OID sysName = new OID(sysPrefix+".5.0");
	/**
	 * �豸����λ��
	 */
	public static final OID sysLocation = new OID(sysPrefix+".6.0");
	/**
	 *���豸�ṩ�ķ���
	 */
	public static final OID sysServices = new OID(sysPrefix+".7.0");
	/**
	 * system��OID����
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
	 * �ӿڱ�oid�ִ�ǰ׺
	 */
	public static final String ifPrefix = "1.3.6.1.2.1.2";
	/**
	 * �ӿ�����
	 */
	public static final OID ifNumber = new OID(ifPrefix + ".1.0");
	/**
	 * �ӿڱ�OID
	 */
	public static final OID ifTable = new OID(ifPrefix + ".2.1");
	/**
	 * ip��oid�ִ�ǰ׺
	 */
	public static final String ipTablePrefix = "1.3.6.1.2.1.4";
	/**
	 * ip��OID
	 */
	public static final OID ipTable = new OID(ipTablePrefix);
	/**
	 * at���ִ�ǰ׺
	 */
	public static final String atTablePrefix = "1.3.6.1.2.1.3";
	/**
	 * at��OID
	 */
	public static final OID atTable = new OID(atTablePrefix + ".1.1");//1.3.6.1.2.1.3.1.1 
	/**
	 * ip·�ɱ�oid
	 */
	public static final OID ipRouteTable = new OID(ipTablePrefix + ".21.1.1");//1.3.6.1.2.1.4.21.1
	/**
	 * ip·�ɱ�oid
	 */
	public static final OID ipAddressTable = new OID(ipTablePrefix + ".20.1");//1.3.6.1.2.1.4.21.1
	/**
	 * ip·�ɱ�oid
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

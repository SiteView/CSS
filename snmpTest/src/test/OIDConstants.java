package test;

import org.snmp4j.smi.OID;

public class OIDConstants {
	/**interface �� 1.3.6.1.2.1.2
	 *	mgmt/mib-2/interfaces/ifNumber  ��ʾһ���豸���еĽӿ���
	 */
	public static final OID INTERFACE_ifNumber = new OID("1.3.6.1.2.1.2.1.0");
	/**
	 * mgmt/mib-2/interfaces/ifTable/ifEntry/ifDescr 
	 *���ǽӿڵ��ı���������Ϊֻ����ʾ�����������˽ӿڵĳ�������
	 *��Ʒ����Ӳ���ӿڵİ汾�š�
	 */ 
	public static final OID INTERFACE_ifDescr = new OID("1.3.6.1.2.1.2.2.1.2.0");
	/**
	 * mgmt/mib-2/interfaces/ifTable/ifEntry/ifInOctets
	 * Ϊֻ���ļ�������Counter�����������ڽӿ����յ����ֽ�����������֡��ʽ��
	 */
	public static final OID INTERFACE_ifInOctets = new OID("1.3.6.1.2.1.2.2.1.10.0");
	/**
	 * mgmt/mib-2/interfaces/ifTable/ifEntry/ifOutOctets
	 * Ϊֻ���ļ�������Counter��������ʾ�ڽӿ���������ֽ�����������֡��ʽ��
	 */
	public static final OID INTERFACE_ifOutOctets = new OID("1.3.6.1.2.1.2.2.1.16.0");
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

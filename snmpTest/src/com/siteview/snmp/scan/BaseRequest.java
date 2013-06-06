package com.siteview.snmp.scan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableUtils;

import test.TableUtilsTest;
import test.Utils;

public class BaseRequest {

	public Snmp snmp = null;
	private PDU request = new PDU();
	private static final String COMMUNITY248 = "siteview";
	private String community = COMMUNITY248;
	private String ip = "192.168.0.248";
	private int port = 161;
	private int trapPort = 162;
	private int version = SnmpConstants.version2c;
	boolean inited = false;
	

	public Snmp getSnmp() {
		return snmp;
	}

	public void setSnmp(Snmp snmp) {
		this.snmp = snmp;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTrapPort() {
		return trapPort;
	}

	public void setTrapPort(int trapPort) {
		this.trapPort = trapPort;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public OID buildOid(String oidStr) {
		if (Utils.isEmptyOrBlank(oidStr)) {
			throw new NullPointerException("oid String is null");
		}
		return new OID(oidStr);
	}

	public CommunityTarget buildGetPduCommunityTarget() {
		CommunityTarget target = new CommunityTarget();
		target.setAddress(GenericAddress.parse("udp:" + ip +"/" + port));
		target.setCommunity(new OctetString(community));
		target.setRetries(2);
		target.setTimeout(200);
		target.setVersion(version);
		return target;
	}

	protected void init() throws IOException {
		if (snmp == null) {
			// �趨��ȡ��Э��--SNMP
			TransportMapping transport1 = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport1);
			snmp.listen();
		}
	}

	protected ResponseEvent send(PDU pdu, Target target) throws IOException {
		if (snmp == null)
			init();
		return snmp.send(pdu, target);
	}

	// public void sendSynchronismGet(String ip,String type,String community){
	// try{
	// ResponseEvent responseEvent = send(buildPDU(),
	// buildGetCommunityTarget(ip,type,community));
	// // �����ؽ����Ϊ�գ����豸֧��SNMPЭ��
	// PDU response=responseEvent.getResponse();
	//
	// if(response != null){
	// Vector<? extends VariableBinding> a = response.getVariableBindings();
	// for(VariableBinding vb : a){
	// System.out.println(vb.getOid()+" = "+vb.getVariable());
	// }
	// }else{
	// System.out.println("���ؽ��Ϊnull!");
	// }
	// }catch (Exception e) {
	// e.printStackTrace();
	// }
	// finally{
	// if(snmp!=null) {
	// try {
	// snmp.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
}

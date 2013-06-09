package com.siteview.snmp.scan;

import java.io.IOException;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import com.siteview.snmp.base.BaseRequest;
import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.pojo.IpGroupTable;
import com.siteview.snmp.pojo.SystemGroup;
import com.siteview.snmp.util.OIDTypeUtils;

public class SystemGroupScan extends BaseRequest {
	
	public SystemGroup resolute(Vector<? extends VariableBinding> vbs){
		SystemGroup group = new SystemGroup();
		for(VariableBinding vb : vbs){
			if(vb.getOid().equals(OIDConstants.sysDescr)){
				group.setSysDescr(vb.getVariable().toString());
			}else if(vb.getOid().equals(OIDConstants.sysObjectId)){
				group.setSysObjectID(vb.getVariable().toString());
			}else if(vb.getOid().equals(OIDConstants.sysName)){
				group.setSysName(vb.getVariable().toString());
			}else if(vb.getOid().equals(OIDConstants.sysUptime)){
				group.setSysUpTime(vb.getVariable().toString());
			}else if(vb.getOid().equals(OIDConstants.sysLocation)){
				group.setSysLocation(vb.getVariable().toString());
			}else if(vb.getOid().equals(OIDConstants.sysContact)){
				group.setSysContact(vb.getVariable().toString());
			}else if(vb.getOid().equals(OIDConstants.sysServices)){
				group.setSysServcies(vb.getVariable().toInt());
			}
		}
		return group;
	}
	public SystemGroup getBaseInfo() throws IOException{
		PDU pdu = new PDU();
		pdu.setType(PDU.GET);
		for(OID oid:OIDConstants.sysOIDs){
			pdu.add(new VariableBinding(oid));
		}
		return resolute(send(pdu, buildGetPduCommunityTarget()).getResponse().getVariableBindings());
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Aaa a1 = new SystemGroupScan().new Aaa("192.168.0.248");
		a1.setName("a1");
		a1.start();
		Aaa a2 = new SystemGroupScan().new Aaa("192.168.0.251");
		a2.setName("a2");
		a2.start();
		Thread.sleep(5000);
	}
	class Aaa extends Thread{
		String IP = "";
		public Aaa(String ip){
			this.IP = ip;
		}
		@Override
		public void run() {
			SystemGroupScan scan = new SystemGroupScan();
			scan.setIp(IP);
			SystemGroup group = null;
			try {
				CommunityTarget target = new CommunityTarget();
				group = scan.getBaseInfo();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + "\t" +OIDTypeUtils.getInstance().getTypeByOid(group.getSysObjectID()));
		}
	}
}

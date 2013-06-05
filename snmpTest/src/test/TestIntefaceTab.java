package test;


import java.io.IOException;

import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

public class TestIntefaceTab extends BaseRequest {

	
	public void buildOid() {
		super.buildSystemOid();
		super.oids.add(OIDConstants.INTERFACE_ifDescr);
		super.oids.add(OIDConstants.INTERFACE_ifInOctets);
		super.oids.add(OIDConstants.INTERFACE_ifNumber);
		super.oids.add(OIDConstants.INTERFACE_ifEntry_OperStatus);
		super.oids.add(OIDConstants.INTERFACE_ifOutOctets);
		super.oids.add(OIDConstants.INTERFACE_ifDescr);
		super.oids.add(OIDConstants.INTERFACE_ifDescr);
		
	}

	@Override
	public PDU buildPDU() {
//		buildOid();
//		super.oids.add(OIDConstants.INTERFACE_ifNumber);
//		super.oids.add(OIDConstants.INTERFACE_ifEntryIndex);
//		super.oids.add(OIDConstants.INTERFACE_ifEntryDescr);
		super.oids.add(OIDConstants.INTERFACE_ifEntry_InOctets);
//		super.oids.add(OIDConstants.INTERFACE_ifEntry);
		PDU request = new PDU();
		for(OID o:super.oids){
			request.add(new VariableBinding(o));
		}
		request.setType(PDU.GETNEXT);
		return request;
	}

	public static void main(String[] args) {
		TestIntefaceTab tab = new TestIntefaceTab();
		try {
			ResponseEvent resEvent = tab.send(tab.buildPDU(), tab.buildGetCommunityTarget("192.168.0.248", "udp", "siteview"));
			PDU res = resEvent.getResponse();
//			Variable entryCount = res.getVariable(OIDConstants.INTERFACE_ifNumber);
//			Variable entryIndex = res.getVariable(OIDConstants.INTERFACE_ifEntryIndex);
//			Variable entryDescr = res.getVariable(OIDConstants.INTERFACE_ifEntryDescr);
//			Variable ifTable = res.getVariable(OIDConstants.INTERFACE_ifTable);
//			Variable ifEntry = res.getVariable(OIDConstants.INTERFACE_ifEntry);
			if(res!=null){
//				System.out.println("count = " + entryCount.toInt());
//				System.out.println("index = " + entryIndex.toInt());
//				System.out.println("descr = " + entryDescr.toString());
				System.out.println("res size is " + res.size());
//				System.out.println("count is " + entryCount);
				for(int i=0;i<res.size();i++){
					VariableBinding vb = res.get(i);
					Variable v = vb.getVariable();
					System.out.println(v);
				}
			}else{
				System.out.println("res is null");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//		tab.sendSynchronismGet("192.168.0.248","udp","siteview");
	}

	
}

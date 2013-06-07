package com.siteview.snmp.scan;

import java.io.IOException;
import java.util.Vector;

import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import com.siteview.snmp.base.BaseRequest;
import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.pojo.IpGroupTable;
import com.siteview.snmp.pojo.SystemGroup;

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
			System.out.println(vb);
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
	
	public static void main(String[] args) throws IOException {
		new SystemGroupScan().getBaseInfo();
	}
}

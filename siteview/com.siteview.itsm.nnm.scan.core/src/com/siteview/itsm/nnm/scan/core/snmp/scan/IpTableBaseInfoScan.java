package com.siteview.itsm.nnm.scan.core.snmp.scan;

import java.io.IOException;
import java.util.Vector;

import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;


import com.siteview.itsm.nnm.scan.core.snmp.base.BaseRequest;
import com.siteview.itsm.nnm.scan.core.snmp.constants.OIDConstants;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IpGroupTable;

public class IpTableBaseInfoScan extends BaseRequest{

	public IpGroupTable resolute(Vector<? extends VariableBinding> vbs) {
		IpGroupTable table = new IpGroupTable();
		for(VariableBinding vb : vbs){
			if(vb.getOid().equals(OIDConstants.ipForwarding)){
				table.setIpForwarding(vb.getVariable().toInt());
			}else if(vb.getOid().equals(OIDConstants.ipDefaultTTL)){
				table.setIpDefaultTTL(vb.getVariable().toInt());
			}else if(vb.getOid().equals(OIDConstants.ipForwDatagrams)){
				table.setIpForwDatagrams(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipFragCreates)){
				table.setIpFragCreates(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipFragFails)){
				table.setIpFragFails(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipFragOKs)){
				table.setIpFragOKs(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipInAddrErrors)){
				table.setIpInAddrErrors(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipInDelivers)){
				table.setIpInDelivers(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipInDiscards)){
				table.setIpInDiscards(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipInHdrErrors)){
				table.setIpInHdrErrors(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipInReceives)){
				table.setIpInHdrErrors(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipInUnknownProtos)){
				table.setIpInUnknownProtos(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipOutDiscards)){
				table.setIpOutDiscards(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipOutNoRoutes)){
				table.setIpOutDiscards(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipOutRequests)){
				table.setIpOutRequests(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipReasmFails)){
				table.setIpReasmFails(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipReasmOKs)){
				table.setIpReasmOKs(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipReasmReqds)){
				table.setIpReasmReqds(vb.getVariable().toLong());
			}else if(vb.getOid().equals(OIDConstants.ipReasmTimeout)){
				table.setIpReasmTimeout(vb.getVariable().toInt());
			}else if(vb.getOid().equals(OIDConstants.ipRoutingDiscards)){
				table.setIpRoutingDiscards(vb.getVariable().toLong());
			}
		}
		return table;
	}
	
	public IpGroupTable getBaseInfo() throws IOException{
		PDU pdu = new PDU();
		pdu.setType(PDU.GET);
		for(OID oid:OIDConstants.ipBaseinfoOIDs){
			pdu.add(new VariableBinding(oid));
		}
		return resolute(send(pdu, buildGetPduCommunityTarget()).getResponse().getVariableBindings());
		
	}
	public static void main(String[] args) throws IOException {
		new IpTableBaseInfoScan().getBaseInfo();
		
	}
	
}

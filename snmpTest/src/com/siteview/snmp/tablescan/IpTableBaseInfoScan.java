package com.siteview.snmp.tablescan;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.snmp4j.PDU;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.util.TableEvent;


import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.scan.BaseRequest;
import com.siteview.snmp.scan.BaseTableRequest;

public class IpTableBaseInfoScan extends BaseTableRequest{


	public static final OID defaulEndOID = new OID("23");
	@Override
	public Map<String, Object> resolute(List<TableEvent> tEvent) {
		for(TableEvent event : tEvent){
			System.out.println(event.getColumns()[0] + "\t");
		}
		return null;
	}
	@Override
	protected ResponseEvent send(PDU pdu, Target target) throws IOException {
		// TODO Auto-generated method stub
		return super.send(pdu, target);
	}
	public static void main(String[] args) {
		PDU request = new PDU();
		request.setType(PDU.GET);
		IpTableBaseInfoScan scan = new IpTableBaseInfoScan();
		scan.setEnd(defaulEndOID);
		scan.getTablePojos(OIDConstants.ipBaseinfoOIDs);
		
	}
	
}

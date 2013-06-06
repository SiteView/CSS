package com.siteview.snmp.tablescan;

import java.util.List;
import java.util.Map;

import org.snmp4j.smi.OID;
import org.snmp4j.util.TableEvent;

import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.scan.BaseTableRequest;

public class IpRoutTableScan extends BaseTableRequest {

	public static final OID defaultEndOID = new OID("13");
	
	public static OID _OID = OIDConstants.ipRouteTable;
	@Override
	public Map<String, Object> resolute(List<TableEvent> tEvent) {
		for(TableEvent evne:tEvent){
			System.out.println(evne.toString());
		}
		return null;
	}
	public static void main(String[] args) {
		IpRoutTableScan scan = new IpRoutTableScan();
		scan.setEnd(defaultEndOID);
		scan.getTablePojos(scan._OID);
		
	}

}

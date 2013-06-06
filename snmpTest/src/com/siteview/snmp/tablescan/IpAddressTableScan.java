package com.siteview.snmp.tablescan;

import java.util.List;
import java.util.Map;

import org.snmp4j.smi.OID;
import org.snmp4j.util.TableEvent;

import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.scan.BaseTableRequest;

public class IpAddressTableScan extends BaseTableRequest {

	public static final OID defaultEndOID = new OID("5");
	public static OID _OID = OIDConstants.ipAddressTable;
	@Override
	public Map<String, Object> resolute(List<TableEvent> tEvent) {
		for(TableEvent event : tEvent){
			System.out.println(event.getColumns()[0] + "\t");
		}
		return null;
	}
	public static void main(String[] args) {
		IpAddressTableScan scan = new IpAddressTableScan();
		scan.setEnd(defaultEndOID);
		scan.getTablePojos(_OID);
	}
}

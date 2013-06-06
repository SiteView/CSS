package com.siteview.snmp.tablescan;

import java.util.List;
import java.util.Map;

import org.snmp4j.smi.OID;
import org.snmp4j.util.TableEvent;

import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.scan.BaseTableRequest;

public class IPNetToMediaTableScan extends BaseTableRequest {

	public static final OID defaultEndOID = new OID("4");
	@Override
	public Map<String, Object> resolute(List<TableEvent> tEvent) {
		int count = 0;
		for(TableEvent event : tEvent){
			System.out.println(event.getColumns()[0]);
			count++;
		}
		System.out.println("********************"+count);
		return null;
	}

	public static void main(String[] args) {
		
		IPNetToMediaTableScan scan = new IPNetToMediaTableScan();
		scan.setEnd(defaultEndOID);
		scan.getTablePojos(OIDConstants.ipNetToMediaTable);
		
	}

	
}

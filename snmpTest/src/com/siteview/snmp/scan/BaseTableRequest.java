package com.siteview.snmp.scan;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.snmp4j.CommunityTarget;
import org.snmp4j.smi.OID;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;



public abstract class BaseTableRequest extends BaseRequest {

	
	private OID start = new OID("1");
	private OID end = new OID("10");
	
	public OID getStart() {
		return start;
	}
	public void setStart(OID start) {
		this.start = start;
	}
	public OID getEnd() {
		return end;
	}
	public void setEnd(OID end) {
		this.end = end;
	}
	public Map<String,Object> getTablePojos(OID oid){
		CommunityTarget target = buildGetPduCommunityTarget();
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		TableUtils utils = new TableUtils(snmp,new DefaultPDUFactory());
		utils.setMaxNumRowsPerPDU(5);
		OID[] columnOIDs = new OID[]{
				oid
		};
		return resolute(utils.getTable(target, columnOIDs, getStart(), getEnd()));
	}
	public Map<String,Object> getTablePojos(OID[] oids){
		CommunityTarget target = buildGetPduCommunityTarget();
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		TableUtils utils = new TableUtils(snmp,new DefaultPDUFactory());
		utils.setMaxNumRowsPerPDU(23);
		return resolute(utils.getTable(target, oids, getStart(), getEnd()));
	}
	public abstract Map<String, Object> resolute(List<TableEvent> tEvent);
	
	
}

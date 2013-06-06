package com.siteview.snmp.tablescan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import test.Utils;

import com.siteview.snmp.constants.OIDConstants;
import com.siteview.snmp.pojo.InterfaceTable;
import com.siteview.snmp.scan.BaseTableRequest;

public class InterfaceTableScan extends BaseTableRequest {
	
	public static final OID defaultEndOID = new OID("22");

	public static OID _OID = OIDConstants.ifTable; 
	@Override
	public Map<String, Object> resolute(List<TableEvent> l) {
		Map<String, Object> ifTables = new HashMap<String, Object>();
		for (TableEvent t : l) {
			VariableBinding[] vbs = t.getColumns();
			String varible = t.getColumns()[0].toString();
			String[] varibleSplit = varible.split("=");
			String[] oids = varibleSplit[0].trim().split("\\.");
			String value = Utils.isEmptyOrBlank(varibleSplit[1]) ? null
					: varibleSplit[1].trim();
			String index = oids[oids.length - 1];
			String type = oids[oids.length - 2];
			InterfaceTable iftable = null;
			if (ifTables.containsKey(index)) {
				iftable = (InterfaceTable)ifTables.get(index);
			} else {
				iftable = new InterfaceTable();
				iftable.setIfIndex(Utils.getIntValueFromNotNullString(index));
				ifTables.put(index, iftable);
			}
			if ("2".equals(type)) {
				iftable.setIfDescr(value);
			} else if ("3".equals(type)) {
				iftable.setIfType(value);
			} else if ("4".equals(type)) {
				iftable.setIfMtu(Utils.getDoubleValueFromString(value));
			} else if ("5".equals(type)) {
				iftable.setIfSpeed(Utils.getDoubleValueFromString(value));
			} else if ("6".equals(type)) {
				iftable.setIfPhysAddress(value);
			} else if ("7".equals(type)) {
				iftable.setIfAdminStatus(Utils.getIntValueFromNotNullString(value));
			} else if ("8".equals(type)) {
				iftable.setIfOperStatus(Utils
						.getIntValueFromNotNullString(value));
			} else if ("9".equals(type)) {
				iftable.setIfLastChange(value);
			} else if ("10".equals(type)) {
				iftable.setIfInoctets(Utils.getDoubleValueFromString(value));
			} else if ("11".equals(type)) {
				iftable.setIfinUcastPkts(Utils.getDoubleValueFromString(value));
			} else if ("12".equals(type)) {
				iftable.setIfInNUcastPkts(Utils.getDoubleValueFromString(value));
			} else if ("13".equals(type)) {
				iftable.setIfInDiscards(Utils.getDoubleValueFromString(value));
			} else if ("14".equals(type)) {
				iftable.setIfInErrors(Utils.getDoubleValueFromString(value));
			} else if ("15".equals(type)) {
				iftable.setIfInUnknownProtos(Utils
						.getIntValueFromAllowNullString(value));
			} else if ("16".equals(type)) {
				iftable.setIfOutOctets(Utils.getDoubleValueFromString(value));
			} else if ("17".equals(type)) {
				iftable.setIfOutUcastPkts(Utils.getDoubleValueFromString(value));
			} else if ("18".equals(type)) {
				iftable.setIfOutNUcastPkts(Utils
						.getDoubleValueFromString(value));
			} else if ("19".equals(type)) {
				iftable.setIfOutDiscards(Utils.getDoubleValueFromString(value));
			} else if ("20".equals(type)) {
				iftable.setIfOutErrors(Utils.getDoubleValueFromString(value));
			} else if ("21".equals(type)) {
				iftable.setIfOutQLen(Utils.getDoubleValueFromString(value));
			} else if ("22".equals(type)) {
				iftable.setIfspecific(value);
			}
		}
		return ifTables;
	}
	public static void main(String[] args) {
		InterfaceTableScan scan = new InterfaceTableScan();
		Map<String, Object> tabs = scan.getTablePojos(_OID);
		scan.setEnd(defaultEndOID);
		Set<Entry<String, Object>> sets = tabs.entrySet();
		for(Entry<String, Object> obj : sets){
			InterfaceTable table = (InterfaceTable)obj.getValue();
			String status = "";
			if(table.isUsed()){
				status = "正常";
			}else if(table.isDown()){
				status = "关闭";
			}else if(table.isTesting()){
				status = "测试...";
			}else if(table.isError()){
				status = "错误";
			}else {
				status = "其它";
			}
			System.out.println("index 为" + obj.getKey() +"状态为：" + status);
		}
	}
}

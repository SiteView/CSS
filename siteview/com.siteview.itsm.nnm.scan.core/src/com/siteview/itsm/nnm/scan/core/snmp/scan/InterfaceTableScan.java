package com.siteview.itsm.nnm.scan.core.snmp.scan;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.OID;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;


import com.siteview.itsm.nnm.scan.core.snmp.base.BaseTableRequest;
import com.siteview.itsm.nnm.scan.core.snmp.common.ScanParam;
import com.siteview.itsm.nnm.scan.core.snmp.common.SnmpPara;
import com.siteview.itsm.nnm.scan.core.snmp.constants.OIDConstants;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.InterfaceTable;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IpAddressTable;
import com.siteview.itsm.nnm.scan.core.snmp.util.ScanUtils;
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;

public class InterfaceTableScan {
	
	public static final OID defaultEndOID = new OID("23");

	public static OID _OID = OIDConstants.ifTable; 
	/**
	 * 获取ipaddress表数据
	 * @param spr
	 * @return
	 */
	public Map<String,InterfaceTable> getInterfaceables(SnmpPara spr){
		Map<String,InterfaceTable> result = new HashMap<String, InterfaceTable>();
		CommunityTarget target = ScanUtils.buildGetPduCommunityTarget(spr.getIp(), 161, spr.getCommunity(), spr.getTimeout(), spr.getRetry(), Integer.parseInt(spr.getSnmpver()));
		Snmp snmp = new Snmp();
		TransportMapping transport1 = null;
		try {
			transport1 = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport1);
			snmp.listen();
			TableUtils utils = new TableUtils(snmp,new DefaultPDUFactory());
			utils.setMaxNumRowsPerPDU(5);
			OID[] columnOIDs = new OID[]{
					_OID
			};
			result =  resoluteTable(utils.getTable(target, columnOIDs, new OID("1"), defaultEndOID)); 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(snmp != null){
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	public Map<String, InterfaceTable> resoluteTable(List<TableEvent> l) {
		Map<String, InterfaceTable> ifTables = new HashMap<String, InterfaceTable>();
		for (TableEvent t : l) {
			if(t==null || t.getColumns().length == 0|| t.getColumns()[0] == null){
				continue;
			}
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
				iftable.setIfMtu(Long.valueOf(value));
			} else if ("5".equals(type)) {
				iftable.setIfSpeed(Long.valueOf(value));
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
				iftable.setIfInoctets(Long.valueOf(value));
			} else if ("11".equals(type)) {
				iftable.setIfinUcastPkts(Long.valueOf(value));
			} else if ("12".equals(type)) {
				iftable.setIfInNUcastPkts(Long.valueOf(value));
			} else if ("13".equals(type)) {
				iftable.setIfInDiscards(Long.valueOf(value));
			} else if ("14".equals(type)) {
				iftable.setIfInErrors(Long.valueOf(value));
			} else if ("15".equals(type)) {
				iftable.setIfInUnknownProtos(Utils
						.getIntValueFromAllowNullString(value));
			} else if ("16".equals(type)) {
				iftable.setIfOutOctets(Long.valueOf(value));
			} else if ("17".equals(type)) {
				iftable.setIfOutUcastPkts(Long.valueOf(value));
			} else if ("18".equals(type)) {
				iftable.setIfOutNUcastPkts(Long.valueOf(value));
			} else if ("19".equals(type)) {
				iftable.setIfOutDiscards(Long.valueOf(value));
			} else if ("20".equals(type)) {
				iftable.setIfOutErrors(Long.valueOf(value));
			} else if ("21".equals(type)) {
				iftable.setIfOutQLen(Long.valueOf(value));
			} else if ("22".equals(type)) {
				iftable.setIfspecific(value);
			}
		}
		return ifTables;
	}
	public static void main(String[] args) {
		InterfaceTableScan scan = new InterfaceTableScan();
		
		SnmpPara spr = new SnmpPara();
		spr.setCommunity("public");
		
		spr.setIp("192.168.0.248");
		spr.setRetry(2);
		spr.setTimeout(200);
		
		Map<String, InterfaceTable> tabs = scan.getInterfaceables(spr);
		
		for(Entry<String, InterfaceTable> obj : tabs.entrySet()){
			InterfaceTable table = (InterfaceTable)obj.getValue();
			String status = "";
			if(table.isUsed()){
				status = "使用中...";
				System.out.println("index " + obj.getKey() +"状态：" + status);
			}else if(table.isDown()){
				status = "未使用";
			}else if(table.isTesting()){
				status = "测试中...";
			}else if(table.isError()){
				status = "错误";
			}else {
				status = "其它";
			}
			
		}
	}
	
}

package com.siteview.snmp.flowmonitor;

import java.io.IOException;
import java.util.Map;

import org.ietf.jgss.Oid;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.OID;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableUtils;

import com.siteview.snmp.common.SnmpPara;
import com.siteview.snmp.pojo.InterfaceTable;
import com.siteview.snmp.scan.InterfaceTableScan;
import com.siteview.snmp.util.ScanUtils;

/**
 * 监控流量的线程任务
 * @author haiming.wang
 *
 */
public class Monitor{

	/**
	 * 流量采集的时间间隔
	 */
	public static final int interval = 1000;
	
	/**
	 * 流量监控主函数
	 * @param ip
	 */
	public Map<String,InterfaceTable> monitor(SnmpPara spr){
		InterfaceTableScan iftable = new InterfaceTableScan();
		return iftable.getInterfaceables(spr);
		
	}

}

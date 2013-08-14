package com.siteview.itsm.nnm.scan.core.snmp.flowmonitor;

import java.util.Map;

import com.siteview.itsm.nnm.scan.core.snmp.common.SnmpPara;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.InterfaceTable;
import com.siteview.itsm.nnm.scan.core.snmp.scan.InterfaceTableScan;

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
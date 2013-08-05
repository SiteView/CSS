package com.siteview.css.topo.pv;

import org.csstudio.utility.pv.simu.DynamicValue;

import com.siteview.itsm.nnm.scan.core.snmp.flowmonitor.MonitorControler;


public class FlowValue extends DynamicValue {

	private String ip;
	private int ifindex;
	private final MonitorControler mc = new MonitorControler();
	public FlowValue(String name) {
		super(name);
	}
	public FlowValue(String name,String ip,int ifindex){
		super(name);
		this.ip = ip;
		this.ifindex = ifindex;
		//设置刷新时间为5秒
		update_period = 5000;
	}

	@Override
	protected void update() {
		setValue((double)mc.getFlowMap(ip, ifindex));
	}

}

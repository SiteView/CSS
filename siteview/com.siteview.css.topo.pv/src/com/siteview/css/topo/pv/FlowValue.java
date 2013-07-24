package com.siteview.css.topo.pv;

import org.csstudio.utility.pv.simu.DynamicValue;

import com.siteview.snmp.flowmonitor.MonitorControler;

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
	}

	@Override
	protected void update() {
		System.out.print(MonitorControler.flowMap.size());
		System.out.println(MonitorControler.getFlowMap(ip, ifindex));
		setValue((double)mc.getFlowMap(ip, ifindex));
	}

}

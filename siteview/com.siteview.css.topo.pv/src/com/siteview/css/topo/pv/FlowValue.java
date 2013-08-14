package com.siteview.css.topo.pv;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.utility.pv.simu.DynamicValue;

import com.siteview.itsm.nnm.scan.core.snmp.flowmonitor.MonitorControler;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;


public class FlowValue extends DynamicValue {

	private String ip;
	private int ifindex;
	private final MonitorControler mc = new MonitorControler();
	Map<Pair<String,Integer>, Double> preMap = new ConcurrentHashMap<Pair<String,Integer>, Double>();
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
		
		Pair<String,Integer> key = new Pair<String, Integer>(ip, ifindex);
		double currentValue = (double)mc.getFlowMap(ip, ifindex);
		if(currentValue == 0d){
			if(preMap.containsKey(key)){
				currentValue = preMap.get(key);
			}else{
				preMap.put(key, 0d);
			}
		}else{
			preMap.put(key, currentValue);
		}
		
		setValue(currentValue);
	}

}

package com.siteview.pv.monitor;

import org.csstudio.utility.pv.simu.DynamicValue;


public class MonitorCounter extends DynamicValue{
	public String recId="";
	public String counter="";
	public MonitorCounter(String name) {
		super(name);
		recId=name.substring(0,name.indexOf("="));
		counter=name.substring(name.indexOf("=")+1);
	}
	@Override
	protected void update() {
		String s=System.currentTimeMillis()+"";
		  final double count =Double.parseDouble((String) s.substring(9, 11));
		  setValue((double )count);
	}

}

package com.siteview.css.topo.pv;

import org.csstudio.utility.pv.simu.DynamicValue;

public class TopologyValue extends DynamicValue {

	private static int data = 1;
	public TopologyValue(String name) {
		super(name);
	}

	@Override
	protected void update() {
		if(data >= 100) data = 1;
		data+=10;
		setValue(data);
	}

	
}

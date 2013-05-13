package com.siteview.pv.monitor;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.simu.DynamicValue;
import org.csstudio.utility.pv.simu.SimulatedPV;
import org.csstudio.utility.pv.simu.StaticPV;
import org.csstudio.utility.pv.simu.Value;

public class MonitorPVFactory implements IPVFactory{
	 public static final String PREFIX = "mon";
	 private static Map<String, Value> values =new HashMap<String, Value>();
	public static int getValueCount(){
		 return values.size();
    }
	 
	public PV createPV(String name) throws Exception {
		 Value value = values.get(name);
	        if (value == null)
	        {
	          value=new MonitorCounter(name);
	          values.put(name, value);
	        }
	        if (value instanceof DynamicValue)
	            return new SimulatedPV(PREFIX, (DynamicValue) value);
	        
	        return new StaticPV(PREFIX, value);
	}

}

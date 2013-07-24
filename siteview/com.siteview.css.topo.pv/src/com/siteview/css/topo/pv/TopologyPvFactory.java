package com.siteview.css.topo.pv;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.simu.DynamicValue;
import org.csstudio.utility.pv.simu.SimulatedPV;
import org.csstudio.utility.pv.simu.StaticPV;
import org.csstudio.utility.pv.simu.Value;

public class TopologyPvFactory implements IPVFactory {

	 /** PV type prefix */
    public static final String PREFIX = "sv";
    
    private static Map<String, Value> values =
            new HashMap<String, Value>();
    
	@Override
	public PV createPV(String name) throws Exception {
		Value value = values.get(name);
		if(value == null){
			if(name.equals("topo")){
				value = new TopologyValue(name);
			}
			if (value instanceof DynamicValue)
				return new SimulatedPV(PREFIX, (DynamicValue) value);
			values.put(name, value);
		}
        return new StaticPV(PREFIX, value);
	}

}

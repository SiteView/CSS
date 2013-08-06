package core.businessobject.pv.search;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.simu.DynamicValue;
import org.csstudio.utility.pv.simu.SimulatedPV;
import org.csstudio.utility.pv.simu.Value;

public class BusObPVSearch implements IPVFactory {

	public static final String PREFIX = "bo";
	
	private static Map<String, Value> values =new HashMap<String, Value>();
	
	@Override
	public PV createPV(String name) {
		try {
			if(name.trim().length()!=0){
				String[] params1 = name.split("\\?");
				String busobname = params1[0];
				String[] params2 = params1[1].split("@");
				String[] expressions = params2[0].split("&&");
				String findfieldname = params2[1];
				Map<String,String> expressionMap = new HashMap<String,String>();
				for(String expression:expressions){
					String[] params3 = expression.split("=");
					expressionMap.put(params3[0].trim(),params3[1].trim());
				}
				MonitorValue value = new MonitorValue(name);
				values.put(name,value);
				return new SimulatedPV(PREFIX, (DynamicValue) value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

}

package core.businessobject.pv.search;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.simu.DynamicValue;
import org.csstudio.utility.pv.simu.SimulatedPV;
import org.csstudio.utility.pv.simu.StaticPV;
import org.csstudio.utility.pv.simu.TextValue;
import org.csstudio.utility.pv.simu.Value;

public class BusObPVSearch implements IPVFactory {

	public static final String PREFIX = "bo";
	
	private static Map<String, Value> values =new HashMap<String, Value>();
	
	@Override
	public PV createPV(String name) {
		try {
			if(name.trim().length()!=0){
				MonitorValue value = new MonitorValue(name);
				values.put(name,value);
				return new SimulatedPV(PREFIX, (DynamicValue) value);
			}
			else{
				throw new Exception("ȱ�ٲ���!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Value value = new TextValue(name,e.getMessage());
			values.put(name,value);
			return new StaticPV(PREFIX,value);
		}
	}

}

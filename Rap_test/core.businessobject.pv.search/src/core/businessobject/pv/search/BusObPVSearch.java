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
import org.eclipse.swt.widgets.Display;

import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.thread.IPrincipal;

public class BusObPVSearch implements IPVFactory {

	public static final String PREFIX = "bo";
	
	private static Map<String, Value> values =new HashMap<String, Value>();
	
	@Override
	public PV createPV(String name) {
		try {
			if(name.trim().length()!=0){
				Display dis=Display.getDefault();
				ISiteviewApi siteviewapi=ConnectionBroker.get_SiteviewApi();
				IPrincipal iprincipal = Siteview.thread.Thread.get_CurrentPrincipal();
				GetApi value = new GetApi(name,dis,siteviewapi,iprincipal);
				values.put(name,value);
				return new SimulatedPV(PREFIX, (DynamicValue) value);
			}
			else{
				throw new Exception("È±ÉÙ²ÎÊý!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Value value = new TextValue(name,e.getMessage());
			values.put(name,value);
			return new StaticPV(PREFIX,value);
		}
	}

}

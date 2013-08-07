package core.businessobject.pv.search;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.utility.pv.simu.DynamicValue;

import Siteview.Convert;
import Siteview.SiteviewException;
import Siteview.thread.IPrincipal;

public class MonitorValue extends DynamicValue{

	private BusObSearch busobsearch;
	private String busobname;
	private String findfieldname;
	private Map<String,String> expressionMap;
	
	public MonitorValue(String name) {
		super(name);
		busobsearch = new BusObSearch();
		String[] params1 = name.split("\\?");
		busobname = params1[0];
		String[] params2 = params1[1].split("@");
		String[] expressions = params2[0].split("&&");
		findfieldname = params2[1];
		expressionMap = new HashMap<String,String>();
		for(String expression:expressions){
			String[] params3 = expression.split("=");
			expressionMap.put(params3[0].trim(),params3[1].trim());
		}
	}
	
	@Override
	protected void update() {
		try {
			final ISeverity severity = ValueFactory.createOKSeverity();
			Map<Class<?>,Object> valueMap = busobsearch.searchBusOb(busobname, findfieldname, expressionMap,BusObConnection.getPrincipal());
			if(valueMap!=null){
				Class<?> currentClass = valueMap.keySet().iterator().next();
				if(currentClass!=null){
					if(currentClass == String.class){
						setValue(ValueFactory.createStringValue(TimestampFactory.now(), severity, severity.toString(), Quality.Original,new String[]{valueMap.get(currentClass).toString()}));
					}
					else{
						setValue(Convert.ToDouble(valueMap.get(currentClass)));
					}
				}
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}

}

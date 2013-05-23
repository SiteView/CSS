package org.csstudio.opibuilder.monitor;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

public class MonitorValueProperty extends AbstractWidgetProperty{

	public MonitorValueProperty(String prop_id, String description,
			WidgetPropertyCategory category, Object defaultValue) {
		super(prop_id, prop_id, null, defaultValue);
		setVisibleInPropSheet(false);
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		IValue acceptableValue = null;
		if(value instanceof IValue)
			acceptableValue = (IValue) value;
		else if(value instanceof Double || value instanceof Float){
	        final ISeverity severity = ValueFactory.createOKSeverity();
			acceptableValue = ValueFactory.createDoubleValue(
					TimestampFactory.now(), severity, severity.toString(),
					null, Quality.Original, new double[]{
						(value instanceof Double? (Double)value : (Float)value)});
		}else if(value instanceof String){
	        final ISeverity severity = ValueFactory.createOKSeverity();
			acceptableValue = ValueFactory.createStringValue(
					TimestampFactory.now(), severity, severity.toString(),
					Quality.Original, new String[]{(String)value});
		}else if(value instanceof Long || value instanceof Integer || value instanceof Short 
				|| value instanceof Boolean
				|| value instanceof Byte || value instanceof Character){
	        final ISeverity severity = ValueFactory.createOKSeverity();
			long r = 0;
			if(value instanceof Long)
				r = (Long)value;
			else if(value instanceof Integer)
				r = (Integer)value;
			else if(value instanceof Short)
				r = (Short)value;
			else if(value instanceof Boolean)
				r= ((Boolean)value)?1:0;
			else if(value instanceof Byte)
				r=(Byte)value;
			else if(value instanceof Character)
				r=(Character)value;			
	        
	        acceptableValue = ValueFactory.createLongValue(
					TimestampFactory.now(), severity, severity.toString(),
					null, Quality.Original, new long[]{r});
		}

		return acceptableValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeToXML(Element propElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object readValueFromXML(Element propElement) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean configurableByRule() {
		return true;
	}
	
	@Override
	public boolean onlyAcceptExpressionInRule() {
		return true;
	}
}

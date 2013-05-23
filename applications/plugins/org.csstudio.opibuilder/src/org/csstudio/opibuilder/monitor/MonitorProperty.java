package org.csstudio.opibuilder.monitor;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

public class MonitorProperty  extends AbstractWidgetProperty{
	private boolean showHookOption;
	private String MONITOR_ID="monitorid";
	private String TYPE="type";
	
	public MonitorProperty(String prop_id, String description,
			WidgetPropertyCategory category, Object defaultValue) {
		super(prop_id, description, category, new MonitorInput());
		showHookOption=true;
	}
	public MonitorProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean showHookOption) {
		super(prop_id, description, category, new MonitorInput());
		this.showHookOption = showHookOption;
	}
	
	@Override
	public Object checkValue(Object value) {
		// TODO Auto-generated method stub
		if(value == null)
			return null;
		MonitorInput acceptableValue = null;
		if(value instanceof MonitorInput){
			((MonitorInput) value).setWidgetModel(widgetModel);
			acceptableValue = (MonitorInput)value;
		}
		
		return acceptableValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		// TODO Auto-generated method stub
		if(PropertySSHelper.getIMPL() == null)
			return null;
		return PropertySSHelper.getIMPL().getMonitorPropertyDescriptor(
				prop_id, description, showHookOption);
	}

	@Override
	public void writeToXML(Element propElement) {
		// TODO Auto-generated method stub
		MonitorInput actionsInput = (MonitorInput)getPropertyValue();
		propElement.setText(actionsInput.getMonitorUrl());
//		propElement.setAttribute(MONITOR_ID, actionsInput.getMonitorId()==null?"":actionsInput.getMonitorId());
//		propElement.setAttribute(TYPE,actionsInput.getType()==null?"":actionsInput.getType());
	}

	@Override
	public Object readValueFromXML(Element propElement) throws Exception {
		// TODO Auto-generated method stub
		MonitorInput result = new MonitorInput();
		String monitorUrl=propElement.getText();
		result.setMonitorId(monitorUrl);
		return result;
	}
	
	public void setWidgetModel(AbstractWidgetModel widgetModel) {
		super.setWidgetModel(widgetModel);
		((MonitorInput)getPropertyValue()).setWidgetModel(widgetModel);
	}

}

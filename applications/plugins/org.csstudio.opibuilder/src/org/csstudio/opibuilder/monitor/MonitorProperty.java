package org.csstudio.opibuilder.monitor;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

public class MonitorProperty  extends AbstractWidgetProperty{

	/**
	 * XML ELEMENT name <code>Monitor Counter</code>.
	 */
	public static final String XML_ELEMENT_MONITOR = "monitor"; //$NON-NLS-1$

	/**
	 * XML ATTRIBUTE name <code>PATHSTRING</code>.
	 */
	public static final String XML_ATTRIBUTE_ACTION_TYPE = "type"; //$NON-NLS-1$

	/**
	 * XML ATTRIBUTE name <code>HOOK</code>.
	 */
	public static final String XML_ATTRIBUTE_HOOK_FIRST = "hook"; //$NON-NLS-1$
	
	public static final String XML_ATTRIBUTE_HOOK_ALL = "hook_all"; //$NON-NLS-1$

	private boolean showHookOption;
	
	public MonitorProperty(String prop_id, String description,
			WidgetPropertyCategory category, Object defaultValue) {
		super(prop_id, description, category, new MonitorInput());
		showHookOption=true;
	}
	public MonitorProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean showHookOption) {
		super(prop_id, description, category, new ActionsInput());
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
		
	}

	@Override
	public Object readValueFromXML(Element propElement) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

public class ButtonProperty extends AbstractWidgetProperty{

	public ButtonProperty(String prop_id, String description,
			WidgetPropertyCategory category, Object defaultValue) {
		super(prop_id, description, category, defaultValue);
	}

	@Override
	public Object checkValue(Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		// TODO Auto-generated method stub
		if(PropertySSHelper.getIMPL() == null)
			return null;
		return PropertySSHelper.getIMPL().getButtonPropertyDescriptor(prop_id, description);
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

package com.siteview.css.topo.models;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class DeviceModel extends AbstractPVWidgetModel {

	private void addBaseStringProperty(String proName,String proValue){
		if(proValue == null){
			proValue = "";
		}
		this.addProperty(new StringProperty(proName, proName, WidgetPropertyCategory.Basic, proValue));
	}
	public void addMacProperty(String proValue){
		addBaseStringProperty("mac", proValue);
	}
	public void addCommunityProperty(String proValue){
		addBaseStringProperty("community", proValue);
	}
	@Override
	protected void configureProperties() {

	}

	@Override
	public String getTypeID() {
		return null;
	}

}

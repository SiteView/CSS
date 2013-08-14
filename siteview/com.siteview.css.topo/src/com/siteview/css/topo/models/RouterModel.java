package com.siteview.css.topo.models;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgets.model.AbstractMarkedWidgetModel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class RouterModel extends AbstractPVWidgetModel{
	public static final String ID = "com.siteview.css.topo.router";

	public static final String PROP_MIN = "min";
	public static final String PROP_MAX = "max";
	public static final int WIDTH = 32;
	public static final int HIGHT = 32;
	private double min = 0;
	private double max = 100;
	public static final int MINIMUM_SIZE = 10;

	public RouterModel() {
		setSize(WIDTH, HIGHT);
	}

	@Override
	protected void configureProperties() {
		addProperty(new DoubleProperty(PROP_MAX, "Max",
				WidgetPropertyCategory.Behavior, 100));
		addProperty(new DoubleProperty(PROP_MIN, "Min",
				WidgetPropertyCategory.Behavior, 0));
	}

	@Override
	public String getTypeID() {
		return ID;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public static void main(String[] args) {
		AbstractContainerModel container = new DisplayModel();
		container.addChild(new DumbModel());
		System.out.println(XMLUtil.widgetToXMLString(container, true));
	}
}
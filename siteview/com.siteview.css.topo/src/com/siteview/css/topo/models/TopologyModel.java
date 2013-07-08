package com.siteview.css.topo.models;


import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class TopologyModel extends AbstractPVWidgetModel {

public static final String ID = "com.siteview.css.topo.topology";
	
	public static final String PROP_MIN = "min";
	public static final String PROP_MAX = "max";
	public static final int WIDTH = 50;
	public static final int HIGHT = 50;
	private double min = 0;
	private double max = 100;
	public static final int MINIMUM_SIZE = 10;
	public TopologyModel(){
		setSize(WIDTH, HIGHT);
	}
	@Override
	protected void configureProperties() {
		addProperty(new DoubleProperty(PROP_MAX, "Max", WidgetPropertyCategory.Behavior, 100));
		addProperty(new DoubleProperty(PROP_MIN, "Min", WidgetPropertyCategory.Behavior, 0));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	public double getMin(){
		return min;
	}
	public double getMax(){
		return max;
	}
	
	public static void main(String[] args) {
		AbstractContainerModel container = new DisplayModel();
		container.addChild(new TopologyModel());
		System.out.println(XMLUtil.widgetToXMLString(container,true));
	}
}

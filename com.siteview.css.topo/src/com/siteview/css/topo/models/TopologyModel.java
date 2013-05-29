package com.siteview.css.topo.models;


import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class TopologyModel extends AbstractPVWidgetModel {

public static final String ID = "com.siteview.css.topo.topology";
	private List sourceConnection=new ArrayList();
	private List targetConnection=new ArrayList();
	public static final String P_SOURCE_CONNECTION="_source_connection";
	public static final String P_TARGET_CONNECTION="_target_connection";	

	public static final String PROP_MIN = "min";
	public static final String PROP_MAX = "max";
	public static final int WIDTH = 175;
	public static final int HIGHT = 175;
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
	
	//连接模型

	public void addSourceConnection(Object connx){
		sourceConnection.add(connx);
		firePropertyChange(P_SOURCE_CONNECTION,null,null); 
	}
	public void addTargetConnection(Object connx){
		targetConnection.add(connx);
		firePropertyChange(P_TARGET_CONNECTION,null,null);
	}
	public List getModelSourceConnections(){
		return sourceConnection;
	}
	public List getModelTargetConnections(){
		return targetConnection;
	}
	public void removeSourceConnection(Object connx){
		sourceConnection.remove(connx);
		firePropertyChange(P_SOURCE_CONNECTION,null,null);
	}
	public void removeTargetConnection(Object connx){
		targetConnection.remove(connx);
		firePropertyChange(P_TARGET_CONNECTION,null,null);
	}
}

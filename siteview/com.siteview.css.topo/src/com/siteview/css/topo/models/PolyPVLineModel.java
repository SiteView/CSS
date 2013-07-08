package com.siteview.css.topo.models;

import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.widgets.model.PolyLineModel;

public class PolyPVLineModel extends PolyLineModel implements IPVWidgetModel {

	public static final String PROP_MIN = "min";
	public static final String PROP_MAX = "max";
	public static final int WIDTH = 175;
	public static final int HIGHT = 175;
	private double min = 0;
	private double max = 100;
	public static final int MINIMUM_SIZE = 10;
	public PolyPVLineModel(){
		super();
	}
	public PolyPVLineModel(int x,int y,int size){
		
	}
	@Override
	protected void configureProperties() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTypeID() {
		// TODO Auto-generated method stub
		return null;
	}

}

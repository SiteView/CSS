package com.siteview.css.topo.models;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;

import com.siteview.css.topo.pojo.DataFromVisio;
import com.siteview.css.topo.pojo.RouterClientData;

public class RouterModel extends AbstractPVWidgetModel {
	
	public static final String ID = "com.siteview.css.topo.router";
	
	public static final String PROP_MIN = "min";
	public static final String PROP_MAX = "max";
	public static final int WIDTH = 0;
	public static final int HIGHT = 0;
	public static final int MINIMUM_SIZE = 10;
	RouterClientData rcd = new RouterClientData(); ;


	public RouterModel() {
		DataFromVisio dfv = new DataFromVisio();
		String filename = "D://panel//1.3.6.1.4.1.9.1.516.vdx";
		rcd = dfv.getModelSize(filename);
		setSize((int)rcd.getWidth(), (int)rcd.getHeigth());
		setTooltip("Router Model");
	}

	@Override
	protected void configureProperties() {
	}

	@Override
	public String getTypeID() {
		return ID;
	}

}

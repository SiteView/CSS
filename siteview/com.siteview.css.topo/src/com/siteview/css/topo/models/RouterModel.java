package com.siteview.css.topo.models;

import java.io.File;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;

import com.siteview.css.topo.actions.OpenRouterOPIHandler;
import com.siteview.css.topo.pojo.DataFromVisio;
import com.siteview.css.topo.pojo.RouterClientData;
import com.siteview.itsm.nnm.scan.core.snmp.util.IoUtils;

public class RouterModel extends AbstractPVWidgetModel {
	
	public static final String ID = "com.siteview.css.topo.router";
	public static String SYSOID = ""; 
	public static final String PROP_MIN = "min";
	public static final String PROP_MAX = "max";
	public static final int WIDTH = 0;
	public static final int HIGHT = 0;
	public static final int MINIMUM_SIZE = 10;
	RouterClientData rcd = new RouterClientData();

	public RouterModel() {	
		DataFromVisio dfv = new DataFromVisio();
		//String filename = "D://panel//1.3.6.1.4.1.3320.1.116.0.vdx";
		String filename = IoUtils.getProductPath() + "panel" + File.separator + SYSOID +".vdx";
//		String filename = IoUtils.getPlatformPath()+"panel/" + SYSOID + ".vdx";		
		if (!new File(filename).exists()) {
			throw new RuntimeException("visio文件不存在！");
		}
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

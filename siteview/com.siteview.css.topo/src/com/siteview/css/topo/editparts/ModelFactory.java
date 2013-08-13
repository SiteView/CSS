package com.siteview.css.topo.editparts;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;

import com.siteview.css.topo.models.DumbModel;
import com.siteview.css.topo.models.RouterModel;
import com.siteview.css.topo.models.SwitchModel;
import com.siteview.css.topo.models.SwitchRouterModel;
import com.siteview.css.topo.models.TopologyModel;


public class ModelFactory {
	/**PC终端*/
	public static final String TOPOLOGYMODEL = "topologyModel";
	/**亚设备*/
	public static final String DUMBMODEL = "dumbModel";
	/**三层交换*/
	public static final String SWITCH_ROUTER = "switchrouter";
	/**路由器*/
	public static final String ROUTER = "router";
	/**二层交换*/
	public static final String SWITCH = "switch";
	public static AbstractPVWidgetModel getWidgetModel(String mode,String ip){
		if (mode.equals(TOPOLOGYMODEL)) {
			TopologyModel topologyModel = new TopologyModel();
			topologyModel.setName(ip);
			return topologyModel;
		}else if (mode.equals(DUMBMODEL)) {
			DumbModel dumbModel = new DumbModel();
			dumbModel.setName(ip);
			return dumbModel;
		}else if (mode.equals(SWITCH_ROUTER)) {
			SwitchRouterModel switchModel = new SwitchRouterModel();
			switchModel.setName(ip);
			return switchModel;
		}else if (mode.equals(ROUTER)) {
			RouterModel routerModel = new RouterModel();
			routerModel.setName(ip);
			return routerModel;
		}else if (mode.equals(SWITCH)) {
			SwitchModel switchModel = new SwitchModel();
			switchModel.setName(ip);
			return switchModel;
		}
		return null;
	}
}

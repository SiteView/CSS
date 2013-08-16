package com.siteview.css.topo.editparts;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;

import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;
import com.siteview.itsm.nnm.scan.core.snmp.util.ScanUtils;
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;
import com.siteview.itsm.nnm.widgets.model.DeviceModel;
import com.siteview.itsm.nnm.widgets.model.DumbModel;
import com.siteview.itsm.nnm.widgets.model.RouterModel;
import com.siteview.itsm.nnm.widgets.model.SwitchModel;
import com.siteview.itsm.nnm.widgets.model.SwitchRouterModel;
import com.siteview.itsm.nnm.widgets.model.TopologyModel;


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
	public static AbstractPVWidgetModel getWidgetModel(String mode,Pair<String, IDBody> body){
		DeviceModel model = null;
		if (mode.equals(TOPOLOGYMODEL)) {
			model = new TopologyModel();
		}else if (mode.equals(DUMBMODEL)) {
			model = new DumbModel();
		}else if (mode.equals(SWITCH_ROUTER)) {
			model = new SwitchRouterModel();
		}else if (mode.equals(ROUTER)) {
			model = new RouterModel();
		}else if (mode.equals(SWITCH)) {
			model = new SwitchModel();
		}
		model.addCommunityProperty(body.getSecond().getCommunity_get());
		String mac = body.getSecond().getBaseMac();
		if(Utils.isEmptyOrBlank(mac)){
			mac = "";
		}else{
			mac = Utils.formatMac(mac, " ");
		}
		model.addMacProperty(mac);
		model.setName(body.getFirst());
		return model;
	}
}

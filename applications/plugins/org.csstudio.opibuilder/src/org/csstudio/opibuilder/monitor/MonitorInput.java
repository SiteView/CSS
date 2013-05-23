package org.csstudio.opibuilder.monitor;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;

public class MonitorInput {
	private String monitorUrl;
	private String monitorId="";
	private String type="";
	private boolean hookUpFirstActionToWidget = false;
	
	private boolean hookUpAllActionsToWidget = false;
	
	private AbstractWidgetModel widgetModel;
	
	public MonitorInput(String monitorUrl,String monitorId,String type) {
		this.monitorUrl = monitorUrl;
		this.monitorId=monitorId;
		this.type=type;
	}
	
	public MonitorInput(AbstractWidgetModel widgetModel) {
		monitorUrl ="no select monitor";
		monitorId="";
		this.type="";
		this.widgetModel=widgetModel;
	}
	
	public MonitorInput() {
		monitorUrl ="no select monitor";
		monitorId="";
		this.type="";
	}

	/**
	 * @param hookWithWidget the hookWithWidget to set
	 */
	public void setHookUpFirstActionToWidget(boolean hookWithWidget) {
		this.hookUpFirstActionToWidget = hookWithWidget;
	}

	/**
	 * @return the hookWithWidget true if the first action is hooked with the widget's click,
	 * which means click on the widget will activate the first action in the list.
	 */
	public boolean isFirstActionHookedUpToWidget() {
		return hookUpFirstActionToWidget;
	}
	
	@Override
	public String toString() {
		return monitorUrl;
	}

	public void addAction(AbstractWidgetAction action){
		action.setWidgetModel(widgetModel);
	}
	
	/**
	 * @return a total contents copy of this ScriptsInput.
	 */
	public MonitorInput getCopy(){
		MonitorInput copy = new MonitorInput();
		copy.setWidgetModel(widgetModel);
		copy.setHookUpFirstActionToWidget(hookUpFirstActionToWidget);
		copy.setHookUpAllActionsToWidget(hookUpAllActionsToWidget);
		return copy;
	}
	

	/**
	 * @return the widgetModel
	 */
	public AbstractWidgetModel getWidgetModel() {
		return widgetModel;
	}

	public boolean isHookUpAllActionsToWidget() {
		return hookUpAllActionsToWidget;
	}

	public void setHookUpAllActionsToWidget(boolean hookUpAllActionsToWidget) {
		this.hookUpAllActionsToWidget = hookUpAllActionsToWidget;
	}

	public String getMonitorUrl() {
		return monitorUrl;
	}

	public void setMonitorUrl(String monitorUrl) {
		this.monitorUrl = monitorUrl;
	}

	public void setWidgetModel(AbstractWidgetModel widgetModel2) {
		// TODO Auto-generated method stub
		widgetModel=widgetModel2;
	}

	public String getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}

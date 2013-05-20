package org.csstudio.opibuilder.monitor;

import java.util.LinkedList;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;

public class MonitorInput {
private LinkedList<AbstractWidgetAction> actionsList;
	
	private boolean hookUpFirstActionToWidget = false;
	
	private boolean hookUpAllActionsToWidget = false;
	
	private AbstractWidgetModel widgetModel;
	
	public MonitorInput(LinkedList<AbstractWidgetAction> actionsList) {
		this.actionsList = actionsList;
	}
	
	public MonitorInput() {
		actionsList = new LinkedList<AbstractWidgetAction>();
	}

	/**
	 * @return the scriptList
	 */
	public LinkedList<AbstractWidgetAction> getActionsList() {
		return actionsList;
	}
	
	public void addAction(AbstractWidgetAction action){
		actionsList.add(action);
		action.setWidgetModel(widgetModel);
	}
	
	/**
	 * @return a total contents copy of this ScriptsInput.
	 */
	public MonitorInput getCopy(){
		MonitorInput copy = new MonitorInput();
		for(AbstractWidgetAction data : actionsList){
			copy.getActionsList().add(data.getCopy());
		}
		copy.setWidgetModel(widgetModel);
		copy.setHookUpFirstActionToWidget(hookUpFirstActionToWidget);
		copy.setHookUpAllActionsToWidget(hookUpAllActionsToWidget);
		return copy;
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
		if(actionsList.size() ==0){
			return "no monitor_counter";
		}
		if(actionsList.size() == 1){
			return actionsList.get(0).getDescription();
		}
		return actionsList.size() + "monitor_counter";
	}

	/**
	 * @param widgetModel the widgetModel to set
	 */
	public void setWidgetModel(AbstractWidgetModel widgetModel) {
		this.widgetModel = widgetModel;
		for(AbstractWidgetAction action : actionsList)
			action.setWidgetModel(widgetModel);
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
}

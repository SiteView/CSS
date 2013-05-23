package org.csstudio.opibuilder.monitor;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.model.PVWidgetModelDelegate;
import org.csstudio.opibuilder.visualparts.AbstractDialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class MonitorCellEditor  extends AbstractDialogCellEditor{
	private MonitorInput actionsInput;
	private boolean showHookOption;
	public MonitorCellEditor(Composite parent, String title,boolean showHookOption) {
		super(parent, title);
		this.showHookOption = showHookOption;
	}

	
	
	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
		MonitorCounterDialog mo=new MonitorCounterDialog(parentShell, actionsInput, dialogTitle, showHookOption);
		if(mo.open() == Window.OK){
			actionsInput = mo.getOutput();
			actionsInput.getWidgetModel().setPropertyValue(AbstractWidgetModel.ECC_ID, actionsInput.getMonitorId());
			actionsInput.getWidgetModel().setPropertyValue(AbstractWidgetModel.ECC_URL, actionsInput.getMonitorUrl());
			actionsInput.getWidgetModel().setPropertyValue(AbstractWidgetModel.ECC_TYPE, actionsInput.getType());
			if(!(actionsInput.getWidgetModel() instanceof DisplayModel)){
				actionsInput.getWidgetModel().setPropertyValue(PVWidgetModelDelegate.PROP_PVNAME, "mon://"+actionsInput.getMonitorId());
			}
		}
	}
	@Override
	protected boolean shouldFireChanges() {
		return actionsInput != null;
	}

	@Override
	protected Object doGetValue() {
		return actionsInput;
	}

	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof MonitorInput))
			actionsInput = new MonitorInput();
		else
			actionsInput = (MonitorInput)value;
	}

}

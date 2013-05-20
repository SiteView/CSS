package org.csstudio.opibuilder.monitor;

import org.csstudio.opibuilder.visualparts.AbstractDialogCellEditor;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
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
		// TODO Auto-generated method stub
//		parentShell.setLayout(new FillLayout());
//		Button b=new Button(parentShell, SWT.NONE);
//		b.setText(dialogTitle);
		MonitorCounterDialog mo=new MonitorCounterDialog(parentShell, actionsInput, dialogTitle, showHookOption);
		mo.open();
		
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
		if(value == null || !(value instanceof ActionsInput))
			actionsInput = new MonitorInput();
		else
			actionsInput = (MonitorInput)value;
	}

}

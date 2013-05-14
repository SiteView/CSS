package org.csstudio.opibuilder.properties.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ButtonCellEditor extends CellEditor{
	
	public ButtonCellEditor(Composite parent){
		
	}

	@Override
	protected Control createControl(Composite parent) {
		// TODO Auto-generated method stub
		return new Button(parent,SWT.NONE);
	}

	@Override
	protected Object doGetValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doSetFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doSetValue(Object value) {
		// TODO Auto-generated method stub
		
	}

}

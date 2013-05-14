package org.csstudio.opibuilder.properties.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public final class ButtonPropertyDescriptor extends PropertyDescriptor{

	public ButtonPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new ButtonCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}

package org.csstudio.opibuilder.properties.monitor;

import org.csstudio.opibuilder.monitor.MonitorCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class MonitorPropertyDescriptor extends TextPropertyDescriptor{
	private boolean showHookOption;
	public MonitorPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		this.showHookOption = showHookOption;
	}
	public MonitorPropertyDescriptor(final Object id, 
			final String displayName, final boolean showHookOption) {
		super(id, displayName);
		this.showHookOption = showHookOption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new MonitorCellEditor(parent, "Set Monitor", showHookOption);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
}

package com.siteview.css.topo.actions;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.siteview.css.topo.editparts.TopologyEdit;

public class ShowDeviceInfoAction extends AbstractWidgetTargetAction {

	private IWorkbenchPart targetPart;
	@Override
	public void run(IAction action) {
		System.out.println("asdf");
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	protected final TopologyEdit getSelectTopologyEdit(){
		return (TopologyEdit)selection.getFirstElement();
	}
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

}

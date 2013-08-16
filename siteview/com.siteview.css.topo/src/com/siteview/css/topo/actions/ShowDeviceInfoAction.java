package com.siteview.css.topo.actions;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.siteview.itsm.nnm.widgets.editpart.TopologyEdit;


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

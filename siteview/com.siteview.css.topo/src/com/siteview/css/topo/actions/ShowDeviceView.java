package com.siteview.css.topo.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.siteview.css.topo.editparts.DeviceEditrInput;
import com.siteview.css.topo.editparts.ShowDeviceEditor;
import com.siteview.css.topo.viewpart.DeviceDetailInfoViewPart;

public class ShowDeviceView implements IWorkbenchWindowActionDelegate{

	public static final String ID = "";
	private IWorkbenchWindow window;
	@Override
	public void run(IAction action) {
		try {
			window.getActivePage().openEditor(new DeviceEditrInput(),
					ShowDeviceEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
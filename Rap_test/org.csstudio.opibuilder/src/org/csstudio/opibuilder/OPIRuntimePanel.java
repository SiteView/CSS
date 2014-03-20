package org.csstudio.opibuilder;

import java.io.File;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIRuntimeDelegate;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class OPIRuntimePanel implements IOPIRuntime{

	private OPIRuntimeDelegate opiruntimedelegate;
	private IEditorInput runnerinput;
	private IWorkbenchPartSite site; 
	
	public OPIRuntimePanel(){
		
	}
	
	public OPIRuntimePanel(Composite parent,String realpath) throws Exception{
		String fullpath = PreferencesHelper.getOPIRepository()+File.separator+realpath;
		this.site = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();
		opiruntimedelegate = new OPIRuntimeDelegate(this);
		try {
			IPath path = new Path(fullpath);
			runnerinput = new RunnerInput(path,null,null);
			opiruntimedelegate.init(site,runnerinput);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		opiruntimedelegate.createGUI(parent);
	}

	@Override
	public void addPropertyListener(IPropertyListener listener) {
		System.out.println("1");
	}

	@Override
	public void createPartControl(Composite parent) {
		System.out.println("2");
	}

	@Override
	public void dispose() {
		System.out.println("3");
	}

	@Override
	public IWorkbenchPartSite getSite() {
		return site;
	}

	@Override
	public String getTitle() {
		System.out.println("5");
		return null;
	}

	@Override
	public Image getTitleImage() {
		return null;
	}

	@Override
	public String getTitleToolTip() {
		System.out.println("6");
		return null;
	}

	@Override
	public void removePropertyListener(IPropertyListener listener) {
		System.out.println("7");
	}

	@Override
	public Object getAdapter(Class adapter) {
		Object obj = opiruntimedelegate.getAdapter(adapter);
		if(obj != null)
			return obj;
		else
			return null;
	}

	@Override
	public void setWorkbenchPartName(String name) {
		
	}

	@Override
	public void setOPIInput(IEditorInput input) throws PartInitException {
		this.runnerinput=input;
	}

	@Override
	public IEditorInput getOPIInput() {
		return runnerinput;
	}

	@Override
	public DisplayModel getDisplayModel() {
		return opiruntimedelegate.getDisplayModel();
	}

	@Override
	public void setFocus() {
		
	}
	
}

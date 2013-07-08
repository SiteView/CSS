package com.siteview.css.topo.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import com.siteview.css.topo.feedBack.TopologyFeedBackFactory;
import com.siteview.css.topo.models.TopologyModel;

public class NewTopoFileWizard extends Wizard implements INewWizard {

	private NewTopoFilePage topoFilePage;

	private IStructuredSelection selection;

	private IWorkbench workbench;


	@Override
	public void addPages() {
		topoFilePage =new NewTopoFilePage("TOPOFilePage", selection); //$NON-NLS-1$
		addPage(topoFilePage);
	}


	@Override
	public boolean performFinish() {
		IFile file = topoFilePage.createNewFile();
		
		if (file == null) {
			return false;
		}
		InputStream is = null;
		try {
			is = file.getContents();
		} catch (CoreException e2) {
			e2.printStackTrace();
		}
		
//		List<AbstractWidgetModel> list = dm.getChildren();
//		for(AbstractWidgetModel m:list){
//			System.out.println(m.getName());
//		}
		
		try {
			workbench.getActiveWorkbenchWindow().getActivePage().openEditor(
					new FileEditorInput(file), "com.siteview.css.topo.editparts.TOPOEdit");//"org.csstudio.opibuilder.OPIEditor");//$NON-NLS-1$
		} catch (PartInitException e) {
			MessageDialog.openError(null, "Open TOPO File error",
					"Failed to open the newly created TOPO File. \n" + e.getMessage());
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "TOPOEditor activation error", e);
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

}

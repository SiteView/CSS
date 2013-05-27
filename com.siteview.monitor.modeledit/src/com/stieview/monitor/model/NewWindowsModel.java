package com.stieview.monitor.model;

import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

public class NewWindowsModel extends Wizard implements INewWizard {
	private NewWindowsModelPage newWindowsModelPage;
	private SelectMachinePage selectMachinePage;

	private IStructuredSelection selection;

	private IWorkbench workbench;

	
	public NewWindowsModel() {
	}
	
	@Override
	public void addPages() {
		selectMachinePage=new SelectMachinePage("Select Machine",selection);
		addPage(selectMachinePage);
		newWindowsModelPage =new NewWindowsModelPage("WindowsModelPage", selection); //$NON-NLS-1$
		addPage(newWindowsModelPage);
	}
	
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		IFile file = newWindowsModelPage.createNewFile();

		if (file == null) {
			return false;
		}
		try {
			workbench.getActiveWorkbenchWindow().getActivePage().openEditor(
					new FileEditorInput(file), "com.siteview.monitor.modeledit.editor.windows");//$NON-NLS-1$
		} catch (PartInitException e) {
			MessageDialog.openError(null, "Open OPI File error",
					"Failed to open the newly created OPI File. \n" + e.getMessage());
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "OPIEditor activation error", e);
		}

		return true;
	}

}

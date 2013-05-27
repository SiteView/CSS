package com.stieview.monitor.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.SchemaService;
import org.csstudio.ui.util.wizards.WizardNewFileCreationPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;

public class NewWindowsModelPage extends WizardNewFileCreationPage{
	IWizardPage selectMachinePage=null;
	private IStructuredSelection selection;
	public NewWindowsModelPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle("Create a Windows Model File");
		setDescription("Create a new WindowsModel file in the selected project or folder.");
		this.selection=selection;
	}

	@Override
	protected InputStream getInitialContents() {
		DisplayModel displayModel = new DisplayModel();
		SchemaService.getInstance().applySchema(displayModel);
		String s = XMLUtil.widgetToXMLString(displayModel, true);
		InputStream result = new ByteArrayInputStream(s.getBytes());
		return result;
	}
	
	
	@Override
	protected String getNewFileLabel() {
		return "WindoesModel Name:";
	}
	
	@Override
	public String getFileExtension() {
		return OPIBuilderPlugin.OPI_FILE_EXTENSION;
	}
	
//	 public IWizardPage getNextPage() {
//	        if (selectMachinePage == null) {
//	        	selectMachinePage=new SelectMachinePage("«Î—°‘Ò…Ë±∏", selection);
//				return null;
//			}
//	        return selectMachinePage.getNextPage();
//	    }
}

package com.siteview.css.topo.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.SchemaService;
import org.csstudio.ui.util.wizards.WizardNewFileCreationPage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;

public class NewTopoFilePage extends WizardNewFileCreationPage {

	public NewTopoFilePage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle("Create a new OPI File");
		setDescription("Create a new OPI file in the selected project or folder.");
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
		return "TOPOLOGY File Name:";
	}
	
	@Override
	public String getFileExtension() {
		return OPIBuilderPlugin.OPI_FILE_EXTENSION;
	}	
	public void createFiles(IFile file,InputStream is) throws CoreException{
		super.createFile(file, is);
	}
}
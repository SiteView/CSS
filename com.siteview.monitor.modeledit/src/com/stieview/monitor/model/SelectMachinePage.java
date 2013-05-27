package com.stieview.monitor.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.SchemaService;
import org.eclipse.jface.fieldassist.FieldAssistColors;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;

import com.siteview.pv.monitor.bean.*;
import com.siteview.pv.monitor.data.MonitorData;

public class SelectMachinePage extends WizardPage implements Listener{
	IStructuredSelection selection;
	private List<Group> groups;
	private String machineid="";
	private String machineurl="";
	private String machinetitle="";
	 private IWizard wizard = null;
	
	Text _fullPathLabel,_resourceNameField;
	/**
	 * @wbp.parser.constructor
	 */
	protected SelectMachinePage(String pageName) {
		super(pageName);
	}

	public SelectMachinePage(String pageName, IStructuredSelection selection) {
		super(pageName);
		this.selection=selection;
		wizard=this.getWizard();
		setTitle("Create a Windows Model File");
		setDescription("Create a new WindowsModel file in the selected project or folder.");
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		initializeDialogUnits(parent);
		SashForm sa=new SashForm(parent, SWT.VERTICAL);
		sa.setLayout(new FillLayout());
		Composite topLevel = new Composite(sa, SWT.VIRTUAL);
		topLevel.setLayout(new FillLayout());
		topLevel.setFont(parent.getFont());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(topLevel,
				IIDEHelpContextIds.NEW_FILE_WIZARD_PAGE);
		// Show description on opening
		groups=MonitorData.getMonitorMessage();

		Tree tree=new Tree(topLevel,SWT.NONE);
		tree.setLayout(new FillLayout());
		for(Group g:groups){
			TreeItem treeItem=new TreeItem(tree,SWT.NONE);
			treeItem.setText(g.getGroupName());
			treeItem.setData(g);
			createSubTreeItem(treeItem,g);
			List<Machine> machines=g.getMachines();
			for(Machine machine:machines){
				TreeItem treeItem1=new TreeItem(treeItem,SWT.NONE);
				treeItem1.setText(machine.getMachineTitle());
				treeItem1.setData(machine);
			}
		}
		
		tree.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Object o=e.item.getData();
				if(o instanceof Group){
					return;
				}else if(o instanceof Machine){
					Machine machine=(Machine) o;
					machineid=machine.getMachineId();
//					machineurl=machine.getMachineUrl();
					machinetitle=machine.getMachineTitle();
					_fullPathLabel.setText(machineid);
					_resourceNameField.setText(machinetitle);
				}
					
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		// resource name group
		Composite nameGroup = new Composite(sa, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		nameGroup.setLayout(layout);
		nameGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL));
//		nameGroup.setFont(font);

		Label label = new Label(nameGroup, SWT.NONE);
		label.setText("Machine:");
//		label.setFont(font);

		// resource name entry field 
		 _resourceNameField = new Text(nameGroup, SWT.BORDER);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		data.horizontalAlignment = SWT.LEFT;
		data.widthHint = 491;
		_resourceNameField.setLayoutData(data);
//		_resourceNameField.setFont(font);
		_resourceNameField.setBackground(FieldAssistColors.getRequiredFieldBackgroundColor(_resourceNameField));
		_resourceNameField.setEnabled(false);
		_resourceNameField.addListener(SWT.Modify, this);
		// full path
		label = new Label(nameGroup, SWT.NONE);
		label.setText("Machine Id:");
		_fullPathLabel = new Text(nameGroup, SWT.BORDER | SWT.WRAP);
		GridData gd__fullPathLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd__fullPathLabel.widthHint = 491;
		_fullPathLabel.setLayoutData(gd__fullPathLabel);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		data.widthHint = 250;
		_fullPathLabel.setEnabled(false);
		_fullPathLabel.setBackground(FieldAssistColors
				.getRequiredFieldBackgroundColor(_fullPathLabel));
		sa.setWeights(new int[] {211, 57});
		setErrorMessage(null);
		setMessage(null);
		setControl(parent);
		
	}

	private void createSubTreeItem(TreeItem treeItem, Group groups) {
		for(Group g:groups.getSubGroup()){
			TreeItem treeItem0=new TreeItem(treeItem,SWT.NONE);
			treeItem0.setText(g.getGroupName());
			treeItem0.setData(g);
			createSubTreeItem(treeItem0,g);
			List<Machine> machines=g.getMachines();
			for(Machine machine:machines){
				TreeItem treeItem1=new TreeItem(treeItem0,SWT.NONE);
				treeItem1.setText(machine.getMachineTitle());
				treeItem1.setData(machine);
			}
		}
	}

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		setPageComplete(validatePage());
	}
	protected InputStream getInitialContents() {
		DisplayModel displayModel = new DisplayModel();
		SchemaService.getInstance().applySchema(displayModel);
		String s = XMLUtil.widgetToXMLString(displayModel, true);
		InputStream result = new ByteArrayInputStream(s.getBytes());
		return result;
	}
	
	
	protected boolean validatePage() {
		boolean valid = true;
		return valid;
	}
	
	 public boolean canFlipToNextPage() {
	        return !machineid.equals("");
	    }
	  private boolean isPageComplete = true;
	 public void setPageComplete(boolean complete) {
	        isPageComplete = complete;
	        if (isCurrentPage()) {
				getContainer().updateButtons();
			}
	    }
	 
	 public IWizardPage getNextPage() {
	        if (wizard == null) {
	        	return null;
			}
	        return wizard.getNextPage(this);
	 }
	 
	 public IWizardPage getNextPage(SelectMachinePage wizard){
		 IWizardPage newWindowsModelPage =new NewWindowsModelPage("WindowsModelPage", wizard.selection);
		 return newWindowsModelPage;
	 }

	public IStructuredSelection getSelection() {
		return selection;
	}

	public void setSelection(IStructuredSelection selection) {
		this.selection = selection;
	}

	public IWizard getWizard() {
		return wizard;
	}

	public void setWizard(IWizard wizard) {
		this.wizard = wizard;
	}
	 
}

package com.siteview.itsm.nnm.common.editor.ipResourceMgr;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.siteview.itsm.nnm.scan.core.snmp.data.GlobalData;

public class SubNetMgrEditor extends EditorPart {

	public static final String ID = "com.siteview.nnm.main.editor.subnetMrgEdit";
	TableViewer tv;
	Table table;
	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		tv = new TableViewer(parent);
		tv.setContentProvider(new SubnetMgrContentProvider());
		tv.setLabelProvider(new SubnetMgrLabelProvider());
		tv.setInput(GlobalData.deviceList);
		createTable();
		SubNetMgrInput input = (SubNetMgrInput)getEditorInput();
		String subnetIp = input.getSubnetIp();
		int index = -1;
		tv.setInput(input.getInputData());
	}
	private void createTable(){
		table = tv.getTable();
		TableColumn ipColumn = new TableColumn(table, SWT.LEFT);
		ipColumn.setText("IP地址");
		ipColumn.setWidth(120);
		
		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("设备名称");
		nameColumn.setWidth(120);
		
		TableColumn typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText("设备类型");
		typeColumn.setWidth(120);
		
		TableColumn macColumn = new TableColumn(table, SWT.LEFT);
		macColumn.setText("设备MAC地址");
		macColumn.setWidth(120);
		
		TableColumn modelColumn = new TableColumn(table, SWT.LEFT);
		modelColumn.setText("设备型号");
		modelColumn.setWidth(120);

		TableColumn factoryColumn = new TableColumn(table, SWT.LEFT);
		factoryColumn.setText("设备厂商");
		factoryColumn.setWidth(120);
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	@Override
	public void setFocus() {

	}

}

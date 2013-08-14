package com.siteview.itsm.nnm.common.editor.ipResourceMgr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

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
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;

public class IpMacBaseDataEditor extends EditorPart {
	private TableViewer tv;
	private Table table;

	public static final String ID = "com.siteview.itsm.nnm.common.ipMacBaseEdtior";
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
		createTable(parent);
		tv.setContentProvider(new IpMacBaseDataContentProvider());
		tv.setLabelProvider(new IpMacBaseDataLabelProvider());
		List<Pair<String, IDBody>> list = new ArrayList<Pair<String, IDBody>>();
		for(Entry<String, IDBody> entry : GlobalData.deviceList.entrySet()){
			if(!entry.getKey().startsWith("DUMB"))
				list.add(new Pair<String, IDBody>(entry.getKey(), entry.getValue()));
		}
		tv.setInput(list);
	}

	@Override
	public void setFocus() {

	}

	private void createTable(Composite parent) {
		table = tv.getTable();
		TableColumn ipColumn = new TableColumn(table, SWT.LEFT);
		ipColumn.setText("IP地址");
		ipColumn.setWidth(120);

		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("MAC地址");
		nameColumn.setWidth(120);

		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

}

package com.siteview.css.topo.editparts;


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

import com.siteview.css.topo.common.TopoData;
import com.siteview.css.topo.provider.DeviceContentProvider;
import com.siteview.css.topo.provider.DeviceLabelProvider;
import com.siteview.snmp.constants.CommonDef;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.IDBody;

public class ShowDeviceEditor extends EditorPart {

	public static final String ID = "com.siteview.css.topo.ShowDeviceEditor";
	public static  int type;
	private TableViewer tv ;
	private Table table;
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
		createTable(parent);
		tv.setContentProvider(new DeviceContentProvider());
		tv.setLabelProvider(new DeviceLabelProvider());
		List<Pair<String, IDBody>> dataList = new ArrayList<Pair<String, IDBody>>();
		//根据点击的菜单类型添加数据
		if (!TopoData.deviceList.isEmpty()) {
			if (type != 0) {
				// 添加二层交换设备
				if (type == 300011) {
					for (Entry<String, IDBody> entry : TopoData.deviceList
							.entrySet()) {
						if (entry.getValue().getDevType()
								.equals(CommonDef.SWITCH))
							dataList.add(new Pair<String, IDBody>(entry
									.getKey(), entry.getValue()));
					}
				} else if (type == 300012) {// 三层交换
					for (Entry<String, IDBody> entry : TopoData.deviceList
							.entrySet()) {
						if (entry.getValue().getDevType()
								.equals(CommonDef.ROUTE_SWITCH))
							dataList.add(new Pair<String, IDBody>(entry
									.getKey(), entry.getValue()));
					}
				} else if (type == 300013) {// 路由器
					for (Entry<String, IDBody> entry : TopoData.deviceList
							.entrySet()) {
						if (entry.getValue().getDevType()
								.equals(CommonDef.ROUTER))
							dataList.add(new Pair<String, IDBody>(entry
									.getKey(), entry.getValue()));
					}
				} else if (type == 300014) {// 防火墙
					for (Entry<String, IDBody> entry : TopoData.deviceList
							.entrySet()) {
						if (entry.getValue().getDevType()
								.equals(CommonDef.FIREWALL))
							dataList.add(new Pair<String, IDBody>(entry
									.getKey(), entry.getValue()));
					}
				} else if (type == 300015) {
					for (Entry<String, IDBody> entry : TopoData.deviceList
							.entrySet()) {
						if (entry.getValue().getDevType()
								.equals(CommonDef.SERVER))
							dataList.add(new Pair<String, IDBody>(entry
									.getKey(), entry.getValue()));
					}
				} else if (type == 300016) {
					for (Entry<String, IDBody> entry : TopoData.deviceList
							.entrySet()) {
						if (entry.getValue().getDevType().equals(CommonDef.PC))
							dataList.add(new Pair<String, IDBody>(entry
									.getKey(), entry.getValue()));
					}
				} else if (type == 300017) {
					for (Entry<String, IDBody> entry : TopoData.deviceList
							.entrySet()) {
						if (entry.getValue().getDevType()
								.equals(CommonDef.OTHER))
							dataList.add(new Pair<String, IDBody>(entry
									.getKey(), entry.getValue()));
					}
				}
			}else{
				for (Entry<String, IDBody> entry : TopoData.deviceList
						.entrySet()) {
						dataList.add(new Pair<String, IDBody>(entry
								.getKey(), entry.getValue()));
				}
			}
		}
		tv.setInput(dataList);
		
//		getSite().setSelectionProvider(tv);
//		GridData gridData = new GridData();
//		gridData.verticalAlignment = GridData.FILL;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.horizontalAlignment = gridData.FILL_HORIZONTAL;
//		tv.getControl().setLayoutData(gridData);
	}
	
	private void createTable(Composite parent){
		tv = new TableViewer(parent);//MULTI 
		table = tv.getTable();
		TableColumn ipColumn = new TableColumn(table, SWT.LEFT);
		ipColumn.setText("IP地址");
		ipColumn.setWidth(120);
		
		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("设备名称");
		nameColumn.setWidth(120);
		
		TableColumn mid1Column = new TableColumn(table, SWT.LEFT);
		mid1Column.setWidth(120);
		//如果是pc终端类型
		if(type == 300016){
			mid1Column.setText("连接设备IP");
		}else{
			mid1Column.setText("设备型号");
		}
		TableColumn macColumn = new TableColumn(table, SWT.LEFT);
		macColumn.setText("MAC地址");
		macColumn.setWidth(140);
		
		TableColumn endColumn = new TableColumn(table, SWT.LEFT);
		endColumn.setWidth(120);
		//如果是pc终端类型
		if(type == 300016){
			endColumn.setText("连接设备端口");
			
		}else{
			endColumn.setText("设备厂商");
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	@Override
	public void setFocus() {
		
	}

}

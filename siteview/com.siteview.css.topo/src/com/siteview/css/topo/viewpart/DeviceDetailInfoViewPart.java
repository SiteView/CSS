package com.siteview.css.topo.viewpart;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import com.siteview.css.topo.common.TopoData;
import com.siteview.css.topo.provider.DeviceContentProvider;
import com.siteview.css.topo.provider.DeviceLabelProvider;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.util.IoUtils;

public class DeviceDetailInfoViewPart extends ViewPart{
	public static final String ID = "com.siteview.css.topo.devicelistview";

	private DeviceLabelProvider labelProvider;
	
	private TableViewer viewer;
	
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1,false);
		parent.setLayout(layout);
		createViewer(parent);
		Map<String,IDBody> bodys = new HashMap<String, IDBody>();
		if(TopoData.deviceList.isEmpty())
			IoUtils.readIdBodyData(bodys, "");
		else{
			bodys = TopoData.deviceList;
		}
		if(bodys.isEmpty()){
			MessageBox msgDialog = new MessageBox(parent.getShell(), SWT.ICON_WARNING);
			msgDialog.setMessage("提示");
			msgDialog.setText("没有扫描信息可以显示，请重新扫描！");
			return;
		}
		List<IDBody> dlist = new ArrayList<IDBody>();
		dlist.addAll(bodys.values());
		viewer.setInput(dlist);
		getSite().setSelectionProvider(viewer);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = gridData.FILL;
		viewer.getControl().setLayoutData(gridData);
	}
	private void createViewer(Composite parent){
		viewer = new TableViewer(parent,SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL|SWT.FULL_SELECTION|SWT.BORDER);
		createColumns(viewer);
		viewer.setContentProvider(new DeviceContentProvider());
		labelProvider = new DeviceLabelProvider();
		viewer.setLabelProvider(labelProvider);
	}
	private void createColumns(final TableViewer viewer){
		Table table = viewer.getTable();
		String[] titles = {"设备名称","OID","Mac地址","设备类型"};
		int[] bounds = {50,100,100,50};
		for(int i = 0;i<titles.length;i++){
			final int index = i;
			final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn column = viewerColumn.getColumn();
			column.setText(titles[i]);
			column.setWidth(bounds[i]);
			column.setResizable(true);
			column.setMoveable(true);
			viewer.refresh();
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	@Override
	public void setFocus() {
		
	}
}

package org.csstudio.opibuilder.monitor;

import java.util.ArrayList;
import java.util.LinkedList;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.visualparts.HelpTrayDialog;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

//import com.siteview.pv.monitor.bean.Group;
//import com.siteview.pv.monitor.bean.Monitor;
//import com.siteview.pv.monitor.data.MonitorData;

//import com.siteview.ecf.monitor.ECFConnection;


public class MonitorCounterDialog extends HelpTrayDialog{
	private String returnvalue="";
	private String monitorid="";
	private String type="";
	
	private TreeItem itemtree;
	public String listValue;
	private MonitorInput monitorInput;
	public static java.util.List<String> lists=new ArrayList<String>();
	private boolean hookedUpFirstActionToWidget;
	private boolean hookedUpAllActionsToWidget;
	List list;
	private Tree tree;
	private boolean showHookOption = true;
	private String title;
	private Button hookFirstCheckBox;	
	public MonitorCounterDialog(Shell parentShell, 
			MonitorInput actionsInput, String dialogTitle, boolean showHookOption) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.monitorInput=actionsInput;
		hookedUpFirstActionToWidget = actionsInput.isFirstActionHookedUpToWidget();
		hookedUpAllActionsToWidget = actionsInput.isHookUpAllActionsToWidget();
		title = dialogTitle;
		this.showHookOption = showHookOption;
	}
	
//	@Override
//	protected void setShellStyle(int newShellStyle) {
//		// TODO Auto-generated method stub
//		super.setShellStyle(newShellStyle);
////		this.set
//	}

	@Override
	protected String getHelpResourcePath() {
		// TODO Auto-generated method stub
		return "/" + OPIBuilderPlugin.PLUGIN_ID + "/html/Actions.html"; //$NON-NLS-1$; //$NON-NLS-2$
	}
	
	protected Control createDialogArea(Composite parent) {
		final Composite parent_Composite = (Composite) super.createDialogArea(parent);
		parent_Composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		tree = new Tree(parent_Composite, SWT.BORDER);
		if(monitorInput.getWidgetModel() instanceof DisplayModel){
			TreeItem treeItem=new TreeItem(tree,SWT.NONE);
			treeItem.setText("Test0");
	//		treeItem.setImage();
			TreeItem treeItem_1=new TreeItem(treeItem,SWT.NONE);
			treeItem_1.setText("pingmonitor_Ping131");
			
			TreeItem treeItem1=new TreeItem(tree,SWT.NONE);
			treeItem1.setText("Test1");
	//		treeItem.setImage();
			TreeItem treeItem1_1=new TreeItem(treeItem1,SWT.NONE);
			treeItem1_1.setText("URLContent_11:1433");
			
			
			TreeItem treeItem2=new TreeItem(tree,SWT.NONE);
			treeItem2.setText("Test2");
	//		treeItem.setImage();
			TreeItem treeItem2_1=new TreeItem(treeItem2,SWT.NONE);
			treeItem2_1.setText("¶Ë¿Ú_11");
			
//			java.util.List<Group> groups=MonitorData.groups;
//			for(Group group:groups){
//				TreeItem treeItem0=new TreeItem(tree,SWT.NONE);
//				treeItem0.setText(group.getGroupName());
//				treeItem0.setData(group);
//				getSubGroupItem(group,treeItem0);
//				getMonitorItem(group, treeItem0);
//			}
		}else{
			DisplayModel display=(DisplayModel) monitorInput.getWidgetModel().getParent();
			String parentType=display.getMonitorInput().getType();
			String Id=display.getMonitorInput().getMonitorId();
			if(parentType.equals("Group")){
				for(int i=0;i<5;i++){
					TreeItem treeItem=new TreeItem(tree,SWT.NONE);
					treeItem.setText(Id+"_monitor_"+i);
					treeItem.setData(Id+"_monitor_"+i);
				}
//				Group g=MonitorData.groupsMap.get(Id);
//				java.util.List<Monitor> monitors=g.getMonitors();
//				for(Monitor monitor:monitors){
//					TreeItem treeItem=new TreeItem(tree,SWT.NONE);
//					treeItem.setText(monitor.getMonitorTitle());
//					treeItem.setData(monitor);
//				}
			}else if(parentType.equals("Monitor")){
				for(int i=0;i<5;i++){
					TreeItem treeItem=new TreeItem(tree,SWT.NONE);
					treeItem.setText(Id+"_counter_"+i);
					treeItem.setData(Id+"_counter_"+i);
				}
//				Monitor m=MonitorData.monitorsMap.get(Id);
//				java.util.List<String> counters=m.getCounter();
//				for(String s:counters){
//					TreeItem treeItem=new TreeItem(tree,SWT.NONE);
//					treeItem.setText(s);
//					treeItem.setData(s);
//				}
			}
		}
//			if(treeItem.getData() instanceof Group){
//				list.removeAll();
//			}else if(treeItem.getData() instanceof Monitor){
//				list.removeAll();
//				Monitor m=(Monitor) treeItem.getData();
//				returnvalue=m.getMonitorUrl();
//				monitorid=((Monitor)itemtree.getData()).getMonitorId()
//				for(String s:m.getCounter()){
//					list.add(s);
//				}
//			}	
		tree.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				TreeItem treeItem=(TreeItem) e.item;
				itemtree=treeItem;
				returnvalue=itemtree.getText();
				monitorid=itemtree.getText();
				if(itemtree.getText().contains("Test")){
					type="Group";
				}else{
					type="Monitor";
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return parent_Composite;
	}

	public MonitorInput getOutput() {
		MonitorInput actionsInput = new MonitorInput(returnvalue,monitorid,type);
		actionsInput.setWidgetModel(monitorInput.getWidgetModel());
		return actionsInput;
	}

//	private void getSubGroupItem(Group group, TreeItem treeItem0) {
//		java.util.List<Group> groups=group.getSubGroup();
//		for(Group subgroup:groups){
//			TreeItem treeItem=new TreeItem(treeItem0,SWT.NONE);
//			treeItem.setText(subgroup.getGroupName());
//			treeItem.setData(subgroup);
//			getSubGroupItem(subgroup, treeItem);
//			getMonitorItem(subgroup,treeItem);
//		}
//	}
//
//	private void getMonitorItem(Group group, TreeItem treeItem0) {
//		java.util.List<Monitor> monitors=group.getMonitors();
//		for(Monitor monitor:monitors){
//			TreeItem treeItem=new TreeItem(treeItem0,SWT.NONE);
//			treeItem.setText(monitor.getMonitorTitle());
//			treeItem.setData(monitor);
//		}
//	}
	protected void configureShell(Shell newShell) {
		newShell.setSize(650, 600);
		newShell.setLocation(280, 100);
		super.configureShell(newShell);
	}
}

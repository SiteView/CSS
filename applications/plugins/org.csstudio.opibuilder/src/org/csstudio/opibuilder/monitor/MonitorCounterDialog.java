package org.csstudio.opibuilder.monitor;

import java.util.ArrayList;
import java.util.LinkedList;

import org.csstudio.opibuilder.OPIBuilderPlugin;
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

public class MonitorCounterDialog extends HelpTrayDialog{
	private String returnvalue="";
	private TreeItem itemtree;
	public String listValue;
	private LinkedList<AbstractWidgetAction> actionsList;
	public static java.util.List<String> lists=new ArrayList<String>();
	private boolean hookedUpFirstActionToWidget;
	private boolean hookedUpAllActionsToWidget;
	List list;
	private Tree tree;
	private boolean showHookOption = true;
	private MonitorInput actionsInput;
	private String title;
	private Button hookFirstCheckBox;	
	public MonitorCounterDialog(Shell parentShell, 
			MonitorInput actionsInput, String dialogTitle, boolean showHookOption) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		this.actionsInput = actionsInput.getCopy();
		this.actionsList = this.actionsInput.getActionsList();
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
		
		SashForm sashForm = new SashForm(parent_Composite, SWT.NONE);
		
		tree = new Tree(sashForm, SWT.BORDER);
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
		treeItem2_1.setText("端口_11");
		
		tree.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
			TreeItem treeItem=(TreeItem) e.item;
			itemtree=treeItem;
			returnvalue=itemtree.getText();
				if(treeItem.getText().contains("Test")){
					list.removeAll();
				}else if(treeItem.getText().contains("pingmonitor_Ping131")){
					list.removeAll();
					list.add("往返时间");
					list.add("包成功率");
				}else if(treeItem.getText().contains("URLContent_11")){
					list.removeAll();
					list.add("状态");
					list.add("往返时间");
				}else if(treeItem.getText().contains("端口_11")){
					list.removeAll();
					list.add("往返时间");
					list.add("包成功率");
				}
				
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		list = new List(sashForm, SWT.BORDER|SWT.CHECK);
//		list.addListener(SWT.MouseDown,new Listener() {
//			public void handleEvent(Event event) {
//				Point point=new Point(event.x,event.y);
//				listvalue=list.geti
//				System.out.println(event);
//			}
//		});
		list.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				listValue=list.getSelection()[0];
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
			
		sashForm.setWeights(new int[] {1, 1});
		return parent_Composite;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		// TODO Auto-generated method stub
		super.buttonPressed(buttonId);
		returnvalue+="="+listValue;
	}

}

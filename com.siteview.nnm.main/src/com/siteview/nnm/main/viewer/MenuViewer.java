package com.siteview.nnm.main.viewer;

import java.awt.MenuItem;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;



import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import com.siteview.nnm.main.mib.DwSnmpMibOutputHandler;
import com.siteview.nnm.main.mib.MenuTreeRecord;
import com.siteview.nnm.main.mib.DwSnmpMibTreeBuilder;
import com.siteview.nnm.main.pojo.MenuNode;

public class MenuViewer extends ViewPart {

	public final static String ID = "com.siteview.nnm.main.treeview";

	private TreeViewer tv;

	private MenuNode root;
	DwSnmpMibOutputHandler output = new DwSnmpMibOutputHandler();
	private Window window;
	private Composite parent;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		parent.setLayout(fillLayout);
		tv = new TreeViewer(parent);
		tv.setContentProvider(new TreeContentProvide());
		tv.setLabelProvider(new TreeLabelProvide());
		root = new MenuNode();
//		initNode();
		//构造拓扑展示菜单
		{
			
			MenuTreeRecord record = new MenuTreeRecord();
			record.name = "拓扑图管理";
			record.number=1000;
			record.parent = "root";
			record.imgUri = "拓扑图管理.ico";
			
			MenuNode topoMenu = new MenuNode(record);
			root.add(topoMenu);
			
			MenuTreeRecord recordscan = new MenuTreeRecord();
			recordscan.name = "扫描配置";
			recordscan.number=1001;
			recordscan.parent = "root";
			MenuNode topoScanMenu = new MenuNode(recordscan);
			topoMenu.add(topoScanMenu);
			
			MenuTreeRecord recordsetting = new MenuTreeRecord();
			recordsetting.name = "拓扑扫描";
			recordsetting.number=1002;
			recordsetting.parent = "root";
			MenuNode topoSettingMenu = new MenuNode(recordsetting);
			topoMenu.add(topoSettingMenu);
		}
		{//设备管理菜单
			MenuTreeRecord deviceRecord = new MenuTreeRecord();
			deviceRecord.name = "设备管理";
			deviceRecord.number = 3000;
			deviceRecord.parent = "root";
			deviceRecord.imgUri = "设备管理.ico";
			MenuNode deviceMgr = new MenuNode(deviceRecord);
			root.add(deviceMgr);
			
			MenuTreeRecord deviceType = new MenuTreeRecord();
			deviceType.name = "设备类型";
			deviceType.imgUri = "设备类型.ico";
			deviceType.number = 3000;
			deviceType.parent = "设备管理";
			MenuNode deviceTypeNode = new MenuNode(deviceType);
			
			deviceMgr.add(deviceTypeNode);
			
			
		}
		//构造mib结构菜单
		{
			MenuTreeRecord recordMibBrowser = new MenuTreeRecord();
			recordMibBrowser.name = "MIB浏览";
			recordMibBrowser.type = "mib";
			recordMibBrowser.number=2000;
			recordMibBrowser.parent = "root";
			recordMibBrowser.imgUri = "mib.ico";
			MenuNode mibBrowserMenu = new MenuNode(recordMibBrowser);
			//将mib浏览节点加到菜单
			root.add(mibBrowserMenu);
			
			
			DwSnmpMibTreeBuilder treeSupport = new DwSnmpMibTreeBuilder(mibBrowserMenu);
			output = new DwSnmpMibOutputHandler();
			treeSupport.setOutput(output);
			String projectdir = getProductPath();
			if (projectdir == null) {
				projectdir = ".";
			}
			if (treeSupport.addDirectory(projectdir + "mibs/") == false) {
				// 日志
			}
			try {
				treeSupport.buildTree();
//				sortMibTree(mibBrowserMenu);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		{
			//ip资源管理
			MenuTreeRecord ipMgr = new MenuTreeRecord();
			ipMgr.name = "IP资源管理";
			ipMgr.type = "ipMgr";
			ipMgr.number = 4000;
			ipMgr.imgUri = "pcmgr.bmp";
			MenuNode ipMgrNode = new MenuNode(ipMgr);
			root.add(ipMgrNode);
			
			MenuTreeRecord subnetRecord = new MenuTreeRecord();
			subnetRecord.name = "子网";
			subnetRecord.type = "ipMgr";
			subnetRecord.number = 40001;
			subnetRecord.imgUri = "pcmgr.bmp";
			MenuNode subnetNode = new MenuNode(subnetRecord);
			ipMgrNode.add(subnetNode);
			
			MenuTreeRecord ipmac1 = new MenuTreeRecord();
			ipmac1.name = "IP-MAC基准数据";
			ipmac1.type = "ipMgr";
			ipmac1.number = 40002;
			ipmac1.imgUri = "pcmgr.bmp";
			MenuNode ipmac1Node = new MenuNode(ipmac1);
			ipMgrNode.add(ipmac1Node);
			
			MenuTreeRecord ipmac2 = new MenuTreeRecord();
			ipmac2.name = "IP-MAC异动查询";
			ipmac2.type = "ipMgr";
			ipmac2.number = 40003;
			ipmac2.imgUri = "pcmgr.bmp";
			MenuNode ipmac2Node = new MenuNode(ipmac2);
			ipMgrNode.add(ipmac2Node);
			
			
		}
		{
			//告警管理
			MenuTreeRecord dragRecord = new MenuTreeRecord();
			dragRecord.name = "告警管理";
			dragRecord.type = "dragMgr";
			dragRecord.number = 5000;
			dragRecord.imgUri = "pcmgr.bmp";
			MenuNode dragRecordNode = new MenuNode(dragRecord);
			root.add(dragRecordNode);
			
			MenuTreeRecord dragtypeRecord = new MenuTreeRecord();
			dragtypeRecord.name = "告警方式";
			dragtypeRecord.type = "logMgr";
			dragtypeRecord.number = 50001;
			dragtypeRecord.imgUri = "pcmgr.bmp";
			MenuNode dragtypeNode = new MenuNode(dragtypeRecord);
			dragRecordNode.add(dragtypeNode);
			
			MenuTreeRecord dragset = new MenuTreeRecord();
			dragset.name = "告警设置";
			dragset.type = "dragMgr";
			dragset.number = 50002;
			dragset.imgUri = "pcmgr.bmp";
			MenuNode dragsetNode = new MenuNode(dragset);
			dragRecordNode.add(dragsetNode);
			
			MenuTreeRecord dragReportRecord = new MenuTreeRecord();
			dragReportRecord.name = "告警记录";
			dragReportRecord.type = "dragMgr";
			dragReportRecord.number = 50003;
			dragReportRecord.imgUri = "pcmgr.bmp";
			MenuNode dragReportNode = new MenuNode(dragReportRecord);
			dragRecordNode.add(dragReportNode);
			{
				//告警记录
				MenuTreeRecord dragcurrent = new MenuTreeRecord();
				dragcurrent.name = "当前告警";
				dragcurrent.type = "dragMgr";
				dragcurrent.number = 500031;
				dragcurrent.imgUri = "pcmgr.bmp";
				MenuNode dragcurrentNode = new MenuNode(dragcurrent);
				dragReportNode.add(dragcurrentNode);
				
				MenuTreeRecord draghis = new MenuTreeRecord();
				draghis.name = "历史告警";
				draghis.type = "dragMgr";
				draghis.number = 500032;
				draghis.imgUri = "pcmgr.bmp";
				MenuNode draghisNode = new MenuNode(draghis);
				dragReportNode.add(draghisNode);
			}
			
			
		}
		{
			//	监测报表
			buildMonitorReportMenu();
		}
		{
			//日志菜单
			buildLogMenu();
		}
		{
			//系统设置
			buildSysMenu();
		}
		tv.setInput(root);
		// 自定义的方法，实现双击打开相应的编辑器的功能
	    hookDoubleClickAction();
	}
	/**
	 * 系统设置菜单 number 8000
	 */
	private void buildSysMenu() {
		// 系统设置
		MenuTreeRecord sysRecord = new MenuTreeRecord();
		sysRecord.name = "系统设置";
		sysRecord.type = "sysset";
		sysRecord.number = 8000;
		sysRecord.imgUri = "pcmgr.bmp";
		MenuNode sysRecordNode = new MenuNode(sysRecord);
		root.add(sysRecordNode);

		MenuTreeRecord sysRecord1 = new MenuTreeRecord();
		sysRecord1.name = "用户管理";
		sysRecord1.type = "sysset";
		sysRecord1.number = 80001;
		sysRecord1.imgUri = "pcmgr.bmp";
		MenuNode sysRecordNode1 = new MenuNode(sysRecord1);
		sysRecordNode.add(sysRecordNode1);
		
		{
			//用户管理子菜单
			MenuTreeRecord sysRecord11 = new MenuTreeRecord();
			sysRecord11.name = "用户";
			sysRecord11.type = "sysset";
			sysRecord11.number = 800011;
			sysRecord11.imgUri = "pcmgr.bmp";
			MenuNode sysRecordNode11 = new MenuNode(sysRecord11);
			sysRecordNode1.add(sysRecordNode11);
			
			MenuTreeRecord sysRecord12 = new MenuTreeRecord();
			sysRecord12.name = "用户组";
			sysRecord12.type = "sysset";
			sysRecord12.number = 800012;
			sysRecord12.imgUri = "pcmgr.bmp";
			MenuNode sysRecordNode12 = new MenuNode(sysRecord12);
			sysRecordNode1.add(sysRecordNode12);
			
			MenuTreeRecord sysRecord13 = new MenuTreeRecord();
			sysRecord13.name = "设备组";
			sysRecord13.type = "sysset";
			sysRecord13.number = 800013;
			sysRecord13.imgUri = "pcmgr.bmp";
			MenuNode sysRecordNode13 = new MenuNode(sysRecord13);
			sysRecordNode1.add(sysRecordNode13);
			{
				//设备管理子菜单
			}
		}

		MenuTreeRecord logRecord2 = new MenuTreeRecord();
		logRecord2.name = "数据管理";
		logRecord2.type = "sysset";
		logRecord2.number = 80002;
		logRecord2.imgUri = "pcmgr.bmp";
		MenuNode sysRecordNode2 = new MenuNode(logRecord2);
		sysRecordNode.add(sysRecordNode2);
		{
			//数据管理子菜单
			MenuTreeRecord logRecord21 = new MenuTreeRecord();
			logRecord21.name = "采集管理";
			logRecord21.type = "sysset";
			logRecord21.number = 800021;
			logRecord21.imgUri = "pcmgr.bmp";
			MenuNode sysRecordNode21 = new MenuNode(logRecord21);
			sysRecordNode2.add(sysRecordNode21);
			
			MenuTreeRecord logRecord22 = new MenuTreeRecord();
			logRecord22.name = "数据管理";
			logRecord22.type = "sysset";
			logRecord22.number = 800021;
			logRecord22.imgUri = "pcmgr.bmp";
			MenuNode sysRecordNode22 = new MenuNode(logRecord22);
			sysRecordNode2.add(sysRecordNode22);
		}
		
		MenuTreeRecord logRecord3 = new MenuTreeRecord();
		logRecord3.name = "SysLog配置";
		logRecord3.type = "sysset";
		logRecord3.number = 80003;
		logRecord3.imgUri = "pcmgr.bmp";
		MenuNode sysRecordNode3 = new MenuNode(logRecord3);
		sysRecordNode.add(sysRecordNode3);

		MenuTreeRecord logRecord4 = new MenuTreeRecord();
		logRecord4.name = "报表生成配置";
		logRecord4.type = "sysset";
		logRecord4.number = 80004;
		logRecord4.imgUri = "pcmgr.bmp";
		MenuNode sysRecordNode4 = new MenuNode(logRecord4);
		sysRecordNode.add(sysRecordNode4);
	}
	/**
	 * 构建日志菜单 number 7000
	 */
	private void buildLogMenu(){
		//日志
		MenuTreeRecord logRecord = new MenuTreeRecord();
		logRecord.name = "日志管理";
		logRecord.type = "logMgr";
		logRecord.number = 7000;
		logRecord.imgUri = "pcmgr.bmp";
		MenuNode logRecordNode = new MenuNode(logRecord);
		root.add(logRecordNode);
		
		MenuTreeRecord logRecord1 = new MenuTreeRecord();
		logRecord1.name = "操作日志";
		logRecord1.type = "logMgr";
		logRecord1.number = 70001;
		logRecord1.imgUri = "pcmgr.bmp";
		MenuNode dragtypeNode1 = new MenuNode(logRecord1);
		logRecordNode.add(dragtypeNode1);
		
		MenuTreeRecord logRecord2 = new MenuTreeRecord();
		logRecord2.name = "扫描日志";
		logRecord2.type = "logMgr";
		logRecord2.number = 70002;
		logRecord2.imgUri = "pcmgr.bmp";
		MenuNode dragsetNode2 = new MenuNode(logRecord2);
		logRecordNode.add(dragsetNode2);
		
	}
	/**
	 * 监测报表菜单 number = 6000
	 */
	private void buildMonitorReportMenu(){
		MenuTreeRecord monitorReportRecord = new MenuTreeRecord();
		monitorReportRecord.name = "监测报表";
		monitorReportRecord.type = "monitorReportMgr";
		monitorReportRecord.number = 6000;
		monitorReportRecord.imgUri = "pcmgr.bmp";
		MenuNode monitorReportNode = new MenuNode(monitorReportRecord);
		root.add(monitorReportNode);
		
		MenuTreeRecord monitorReportRecord1 = new MenuTreeRecord();
		monitorReportRecord1.name = "设备状态实时分析";
		monitorReportRecord1.type = "monitorReportMgr";
		monitorReportRecord1.number = 60001;
		monitorReportRecord1.imgUri = "pcmgr.bmp";
		MenuNode monitortypeNode1 = new MenuNode(monitorReportRecord1);
		monitorReportNode.add(monitortypeNode1);
		
		{
			//设备状态实时分析子菜单
			MenuTreeRecord monitorReportRecord11 = new MenuTreeRecord();
			monitorReportRecord11.name = "设备商品状态实时分析";
			monitorReportRecord11.type = "monitorReportMgr";
			monitorReportRecord11.number = 600011;
			monitorReportRecord11.imgUri = "pcmgr.bmp";
			MenuNode monitorReportRecord11Node = new MenuNode(monitorReportRecord11);
			monitortypeNode1.add(monitorReportRecord11Node);
			
			MenuTreeRecord monitorReportRecord12 = new MenuTreeRecord();
			monitorReportRecord12.name = "CPU&MEM实时分析";
			monitorReportRecord12.type = "monitorReportMgr";
			monitorReportRecord12.number = 600011;
			monitorReportRecord12.imgUri = "pcmgr.bmp";
			MenuNode monitorReportRecord12Node = new MenuNode(monitorReportRecord12);
			monitortypeNode1.add(monitorReportRecord12Node);
		} 
		
		MenuTreeRecord monitorReportRecord2 = new MenuTreeRecord();
		monitorReportRecord2.name = "历史监测查询";
		monitorReportRecord2.type = "monitorReportMgr";
		monitorReportRecord2.number = 60002;
		monitorReportRecord2.imgUri = "pcmgr.bmp";
		MenuNode monitorsetNode2 = new MenuNode(monitorReportRecord2);
		monitorReportNode.add(monitorsetNode2);
		
		{
			//设备状态实时分析子菜单
			MenuTreeRecord monitorReportRecord21 = new MenuTreeRecord();
			monitorReportRecord21.name = "端口历史数据查询";
			monitorReportRecord21.type = "monitorReportMgr";
			monitorReportRecord21.number = 600021;
			monitorReportRecord21.imgUri = "pcmgr.bmp";
			MenuNode monitorReportRecord12Node = new MenuNode(monitorReportRecord21);
			monitorsetNode2.add(monitorReportRecord12Node);
			
			MenuTreeRecord monitorReportRecord22 = new MenuTreeRecord();
			monitorReportRecord22.name = "CPU&MEM历史数据查询";
			monitorReportRecord22.type = "monitorReportMgr";
			monitorReportRecord22.number = 600021;
			monitorReportRecord22.imgUri = "pcmgr.bmp";
			MenuNode monitorReportRecord22Node = new MenuNode(monitorReportRecord22);
			monitorsetNode2.add(monitorReportRecord22Node);
		} 
		MenuTreeRecord monitorReportRecord3 = new MenuTreeRecord();
		monitorReportRecord3.name = "网络设备监测查询";
		monitorReportRecord3.type = "monitorReportMgr";
		monitorReportRecord3.number = 60003;
		monitorReportRecord3.imgUri = "pcmgr.bmp";
		MenuNode monitorReportNode3 = new MenuNode(monitorReportRecord3);
		monitorReportNode.add(monitorReportNode3);
		
		MenuTreeRecord monitorReportRecord4 = new MenuTreeRecord();
		monitorReportRecord4.name = "设备性能分析报表";
		monitorReportRecord4.type = "monitorReportMgr";
		monitorReportRecord4.number = 60004;
		monitorReportRecord4.imgUri = "pcmgr.bmp";
		MenuNode monitorReportNode4 = new MenuNode(monitorReportRecord4);
		monitorReportNode.add(monitorReportNode4);
		
		MenuTreeRecord monitorReportRecord5 = new MenuTreeRecord();
		monitorReportRecord5.name = "设备端口使用率";
		monitorReportRecord5.type = "monitorReportMgr";
		monitorReportRecord5.number = 60005;
		monitorReportRecord5.imgUri = "pcmgr.bmp";
		MenuNode monitorReportNode5 = new MenuNode(monitorReportRecord5);
		monitorReportNode.add(monitorReportNode5);
		
		MenuTreeRecord monitorReportRecord6 = new MenuTreeRecord();
		monitorReportRecord6.name = "网络整体性能分析";
		monitorReportRecord6.type = "monitorReportMgr";
		monitorReportRecord6.number = 60006;
		monitorReportRecord6.imgUri = "pcmgr.bmp";
		MenuNode monitorReportNode6 = new MenuNode(monitorReportRecord6);
		monitorReportNode.add(monitorReportNode6);
		
		MenuTreeRecord monitorReportRecord7 = new MenuTreeRecord();
		monitorReportRecord7.name = "设备故障趋势分析";
		monitorReportRecord7.type = "monitorReportMgr";
		monitorReportRecord7.number = 60007;
		monitorReportRecord7.imgUri = "pcmgr.bmp";
		MenuNode monitorReportNode7 = new MenuNode(monitorReportRecord7);
		monitorReportNode.add(monitorReportNode7);
		
		MenuTreeRecord monitorReportRecord8 = new MenuTreeRecord();
		monitorReportRecord8.name = "SysLog查询";
		monitorReportRecord8.type = "monitorReportMgr";
		monitorReportRecord8.number = 60008;
		monitorReportRecord8.imgUri = "pcmgr.bmp";
		MenuNode monitorReportNode8 = new MenuNode(monitorReportRecord8);
		monitorReportNode.add(monitorReportNode8);
		
		MenuTreeRecord monitorReportRecord9 = new MenuTreeRecord();
		monitorReportRecord9.name = "自动生成报表";
		monitorReportRecord9.type = "monitorReportMgr";
		monitorReportRecord9.number = 60009;
		monitorReportRecord9.imgUri = "pcmgr.bmp";
		MenuNode monitorReportNode9 = new MenuNode(monitorReportRecord9);
		monitorReportNode.add(monitorReportNode9);
		
	}
	void showtree(MenuNode mibBrowserMenu) {
		MenuTreeRecord record = (MenuTreeRecord) mibBrowserMenu.getUserObject();
		System.out.println(record);
		Enumeration<MenuNode> nodes = mibBrowserMenu.children();
		while (nodes.hasMoreElements()) {
			showtree(nodes.nextElement());
		}
	}
	public void sortMibTree(MenuNode mibBrowserMenu) {
		if (mibBrowserMenu.isLeaf()) {
			return;
		}
		
		if (mibBrowserMenu.getChildCount() > 0) {
			Collections.sort(mibBrowserMenu.getChildrenVector());
			for (MenuNode node : mibBrowserMenu.getChildrenVector()) {
				sortMibTree(node);
			}
		}
	}
	private void hookDoubleClickAction(){
		tv.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = tv.getSelection();
		        // 得到选中的项，注意方法是将得到的选项转换成 IStructuredSelection，再调用 getFirstElement 方法
		        Object object = ((IStructuredSelection) selection).getFirstElement();
		        MenuNode item = (MenuNode)object;
				showError("", ((MenuTreeRecord)item.getUserObject()).syntax);
			}
		});
	}
	private void showError(String title,String conent){
		MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_ERROR);
		dialog.setMessage(conent);
		dialog.setText(title);
		dialog.open();
	}
	public String getProductPath(){
		Location location = Platform.getConfigurationLocation();
		URL url = location.getURL();
		return url.getPath();
	}
	public void initNode() {
		String path = getProductPath() + File.separator + "menu.xml";
		URL xmlUrl = Platform.getBundle("com.siteview.nnm.main").getEntry(
				"menu.xml");
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path));
			String xpath = "/menu/node";
			List list = doc.selectNodes(xpath);
			Iterator i = list.iterator();
			while (i.hasNext()) {
				Element e = (Element)i.next();
//				buildNode(e,root,node);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void setFocus() {

	}
	/**
	 * treeview内容提供者
	 * @author haiming.wang
	 *
	 */
	class TreeContentProvide implements ITreeContentProvider {

		@Override
		public void dispose() {
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			Vector v = ((MenuNode)inputElement).getChildrenVector();
			Collections.sort(v);
			return v.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			Vector v = ((MenuNode)parentElement).getChildrenVector();
			Collections.sort(v);
			return v.toArray();
		}

		@Override
		public Object getParent(Object element) {
			return ((MenuNode)element).getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			return ((MenuNode) element).children().hasMoreElements();
		}

	}
	/**
	 * treeviewlalel提供者
	 * @author haiming.wang
	 *
	 */
	class TreeLabelProvide implements ILabelProvider {

		private Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>(
				20);

		@Override
		public void addListener(ILabelProviderListener listener) {
			
		}

		@Override
		public void dispose() {

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {

		}

		@Override
		public Image getImage(Object element) {
			MenuNode item = (MenuNode)element;
			MenuTreeRecord record = (MenuTreeRecord)item.getUserObject();
			String imgUrl = "";
			if(record.imgUri.equals("")){
				MenuNode parantNode = (MenuNode)item.getParent();
					if("".equals(record.syntax)){
						if(item.isLeaf()){
							imgUrl = "2.bmp";
						}else
							imgUrl = "node.bmp";
					}else if(record.syntax.indexOf("SEQUENCE")>=0){
						imgUrl = "51.bmp";
					}else if((record.syntax.indexOf("SEQUENCE")<0) && (record.syntax.indexOf("Entry") > 0)){
						imgUrl = "6.ico";
					}else{	
						MenuTreeRecord pRecord = (MenuTreeRecord)parantNode.getUserObject();
						if(pRecord.syntax.indexOf("Entry") > -1){
							imgUrl = "51.bmp";
						}else{
							imgUrl = "2.bmp";
						}
					}
			}else{
				imgUrl = record.imgUri;
			}
			final Bundle bundle = Platform.getBundle("com.siteview.nnm.main");
			System.out.println(imgUrl);
			URL url = bundle.getEntry("icons/NNM_NewIcon/NodeIcon/"+imgUrl);
			System.out.println(url.toString());
			return ImageDescriptor.createFromURL(url).createImage();
		}
		@Override
		public String getText(Object element) {
			MenuNode item = (MenuNode)element;
			
			MenuTreeRecord record = (MenuTreeRecord)item.getUserObject();
			String Text = record.name;
			if(record.number < 1000 && !record.name.equals("root") && !record.name.equals("Variables/Textual Conventions") &&!record.name.equals("Orphans")){
				String recoString = record.name;
				Text = Text + "[" + record.number + "]";
				
			}
			return(Text);
		}

	}
	public static void main(String[] args) {
		
		MenuTreeRecord record = new MenuTreeRecord();
		record.name="1";
		record.number = 1;
		MenuNode rootNode = new MenuNode(record);
		
		MenuTreeRecord c1 = new MenuTreeRecord();
		c1.name="c1";
		c1.number = 1;
		MenuNode node1 = new MenuNode(c1);
		
		MenuTreeRecord c3 = new MenuTreeRecord();
		c3.name="c3";
		c3.number = 3;
		MenuNode node3 = new MenuNode(c3);
		
		MenuTreeRecord c2 = new MenuTreeRecord();
		c2.name="c2";
		c2.number = 2;
		MenuNode node2 = new MenuNode(c2);
		
		rootNode.addMenuNode(node2);
		rootNode.addMenuNode(node3);
		rootNode.addMenuNode(node1);
		
		MenuViewer v = new MenuViewer();
		v.showtree(rootNode);
		v.sortMibTree(rootNode);
		System.out.println("\t\t end");
		v.showtree(rootNode);
	}
}

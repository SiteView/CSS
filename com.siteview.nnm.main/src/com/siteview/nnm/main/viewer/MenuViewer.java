package com.siteview.nnm.main.viewer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import com.siteview.css.topo.editparts.DeviceEditrInput;
import com.siteview.css.topo.editparts.ShowDeviceEditor;
import com.siteview.css.topo.wizard.scan.ScanDialog;
import com.siteview.css.topo.wizard.scan.SomeWizard;
import com.siteview.itsm.nnm.common.editor.ipResourceMgr.IpMacBaseDataEditor;
import com.siteview.itsm.nnm.common.editor.ipResourceMgr.IpMacBaseDataInput;
import com.siteview.itsm.nnm.common.editor.ipResourceMgr.SubNetMgrEditor;
import com.siteview.itsm.nnm.common.editor.ipResourceMgr.SubNetMgrInput;
import com.siteview.itsm.nnm.scan.core.StartScan;
import com.siteview.itsm.nnm.scan.core.snmp.data.GlobalData;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.SubnetInfo;
import com.siteview.itsm.nnm.scan.core.snmp.scan.MibScan;
import com.siteview.nnm.main.edit.MibEditorInput;
import com.siteview.nnm.main.edit.MibEditorPart;
import com.siteview.nnm.main.mib.DwSnmpMibOutputHandler;
import com.siteview.nnm.main.mib.MenuTreeRecord;
import com.siteview.nnm.main.mib.DwSnmpMibTreeBuilder;
import com.siteview.nnm.main.pojo.MenuNode;
import com.siteview.nnm.main.utils.BaseUtils;
import com.siteview.nnm.main.utils.DrawTopo;

public class MenuViewer extends ViewPart {

	public final static String ID = "com.siteview.nnm.main.treeview";

	MibScan mibScan;
	public static int type;
	MibEditorInput input = new MibEditorInput();
	private TreeViewer tv;
	private MenuNode root;
	DwSnmpMibOutputHandler output = new DwSnmpMibOutputHandler();
	private Composite parent;
	private IWorkbenchWindow window;
	DwSnmpMibTreeBuilder treeSupport = null;

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		this.window = site.getWorkbenchWindow();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		parent.setLayout(fillLayout);
		tv = new TreeViewer(parent);
		tv.setContentProvider(new TreeContentProvide());
		tv.setLabelProvider(new TreeLabelProvide());
		root = new MenuNode();
		// initNode();
		// ��������չʾ�˵�
		{

			MenuTreeRecord record = new MenuTreeRecord();
			record.name = "����ͼ����";
			record.number = 1000;
			record.type = "topoMgr";
			record.parent = "root";
			record.imgUri = "����ͼ����.ico";

			MenuNode topoMenu = new MenuNode(record);
			root.add(topoMenu);

			MenuTreeRecord recordscan = new MenuTreeRecord();
			recordscan.name = "ɨ������";
			recordscan.number = 10001;
			recordscan.type = "topoMgr";
			recordscan.parent = "root";
			MenuNode topoScanMenu = new MenuNode(recordscan);
			topoMenu.add(topoScanMenu);

			MenuTreeRecord recordsetting = new MenuTreeRecord();
			recordsetting.name = "����ɨ��";
			recordsetting.number = 10002;
			recordsetting.type = "topoMgr";
			recordsetting.parent = "root";
			MenuNode topoSettingMenu = new MenuNode(recordsetting);
			topoMenu.add(topoSettingMenu);
		}
		{// �豸����˵�
			MenuTreeRecord deviceRecord = new MenuTreeRecord();
			deviceRecord.name = "�豸����";
			deviceRecord.number = 3000;
			deviceRecord.type = "deviceMgr";
			deviceRecord.parent = "root";
			deviceRecord.imgUri = "�豸����.ico";
			MenuNode deviceMgr = new MenuNode(deviceRecord);
			root.add(deviceMgr);

			MenuTreeRecord deviceType = new MenuTreeRecord();
			deviceType.name = "�豸����";
			deviceType.imgUri = "�豸����.ico";
			deviceType.number = 30001;
			deviceType.type = "deviceMgr";
			deviceType.parent = "�豸����";
			MenuNode deviceTypeNode = new MenuNode(deviceType);
			deviceMgr.add(deviceTypeNode);
			{
				MenuTreeRecord deviceType1 = new MenuTreeRecord();
				deviceType1.name = "���㽻����";
				deviceType1.imgUri = "�豸����.ico";
				deviceType1.number = 300011;
				deviceType1.type = "deviceMgr";
				deviceType1.parent = "�豸����";
				MenuNode deviceType1Node = new MenuNode(deviceType1);
				deviceTypeNode.add(deviceType1Node);

				MenuTreeRecord deviceType2 = new MenuTreeRecord();
				deviceType2.name = "���㽻����";
				deviceType2.imgUri = "�豸����.ico";
				deviceType2.number = 300012;
				deviceType2.type = "deviceMgr";
				deviceType2.parent = "�豸����";
				MenuNode deviceType2Node = new MenuNode(deviceType2);
				deviceTypeNode.add(deviceType2Node);

				MenuTreeRecord deviceType3 = new MenuTreeRecord();
				deviceType3.name = "·����";
				deviceType3.imgUri = "�豸����.ico";
				deviceType3.number = 300013;
				deviceType3.type = "deviceMgr";
				deviceType3.parent = "�豸����";
				MenuNode deviceType3Node = new MenuNode(deviceType3);
				deviceTypeNode.add(deviceType3Node);

				MenuTreeRecord deviceType4 = new MenuTreeRecord();
				deviceType4.name = "����ǽ";
				deviceType4.imgUri = "�豸����.ico";
				deviceType4.number = 300014;
				deviceType4.type = "deviceMgr";
				deviceType4.parent = "�豸����";
				MenuNode deviceType4Node = new MenuNode(deviceType4);
				deviceTypeNode.add(deviceType4Node);

				MenuTreeRecord deviceType5 = new MenuTreeRecord();
				deviceType5.name = "������";
				deviceType5.imgUri = "�豸����.ico";
				deviceType5.number = 300015;
				deviceType5.type = "deviceMgr";
				deviceType5.parent = "�豸����";
				MenuNode deviceType5Node = new MenuNode(deviceType5);
				deviceTypeNode.add(deviceType5Node);

				MenuTreeRecord deviceType6 = new MenuTreeRecord();
				deviceType6.name = "PC�ն�";
				deviceType6.imgUri = "�豸����.ico";
				deviceType6.number = 300016;
				deviceType6.type = "deviceMgr";
				deviceType6.parent = "�豸����";
				MenuNode deviceType6Node = new MenuNode(deviceType6);
				deviceTypeNode.add(deviceType6Node);

				MenuTreeRecord deviceType7 = new MenuTreeRecord();
				deviceType7.name = "����";
				deviceType7.imgUri = "�豸����.ico";
				deviceType7.number = 300017;
				deviceType7.type = "deviceMgr";
				deviceType7.parent = "�豸����";
				MenuNode deviceType7Node = new MenuNode(deviceType7);
				deviceTypeNode.add(deviceType7Node);
			}

		}
		// ����mib�ṹ�˵�
		{
			MenuTreeRecord recordMibBrowser = new MenuTreeRecord();
			recordMibBrowser.name = "MIB���";
			recordMibBrowser.type = "mib";
			recordMibBrowser.number = 2000;
			recordMibBrowser.parent = "root";
			recordMibBrowser.imgUri = "mib.ico";
			MenuNode mibBrowserMenu = new MenuNode(recordMibBrowser);
			// ��mib����ڵ�ӵ��˵�
			root.add(mibBrowserMenu);

			treeSupport = new DwSnmpMibTreeBuilder(mibBrowserMenu);
			output = new DwSnmpMibOutputHandler();
			treeSupport.setOutput(output);
			String projectdir = getProductPath();
			if (projectdir == null) {
				projectdir = ".";
			}
			if (treeSupport.addDirectory(projectdir + "mibs/") == false) {
				// ��־
			}
			try {
				treeSupport.buildTree();
				// sortMibTree(mibBrowserMenu);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		{
			// ip��Դ����
			MenuTreeRecord ipMgr = new MenuTreeRecord();
			ipMgr.name = "IP��Դ����";
			ipMgr.type = "ipMgr";
			ipMgr.number = 4000;
			ipMgr.imgUri = "PC_Blue.ico";
			MenuNode ipMgrNode = new MenuNode(ipMgr);
			root.add(ipMgrNode);

			MenuTreeRecord subnetRecord = new MenuTreeRecord();
			subnetRecord.name = "����";
			subnetRecord.type = "ipMgr";
			subnetRecord.number = 40001;
			subnetRecord.imgUri = "����16.ico";
			MenuNode subnetNode = new MenuNode(subnetRecord);
			ipMgrNode.add(subnetNode);

			MenuTreeRecord ipmac1 = new MenuTreeRecord();
			ipmac1.name = "IP-MAC��׼����";
			ipmac1.type = "ipMgr";
			ipmac1.number = 40002;
			ipmac1.imgUri = "PC_Blue.ico";
			MenuNode ipmac1Node = new MenuNode(ipmac1);
			ipMgrNode.add(ipmac1Node);

			MenuTreeRecord ipmac2 = new MenuTreeRecord();
			ipmac2.name = "IP-MAC�춯��ѯ";
			ipmac2.type = "ipMgr";
			ipmac2.number = 40003;
			ipmac2.imgUri = "PC_Blue.ico";
			MenuNode ipmac2Node = new MenuNode(ipmac2);
			ipMgrNode.add(ipmac2Node);

		}
		{
			// �澯����
			MenuTreeRecord dragRecord = new MenuTreeRecord();
			dragRecord.name = "�澯����";
			dragRecord.type = "dragMgr";
			dragRecord.number = 5000;
			dragRecord.imgUri = "�澯����.ico";
			MenuNode dragRecordNode = new MenuNode(dragRecord);
			root.add(dragRecordNode);

			MenuTreeRecord dragtypeRecord = new MenuTreeRecord();
			dragtypeRecord.name = "�澯��ʽ";
			dragtypeRecord.type = "logMgr";
			dragtypeRecord.number = 50001;
			dragtypeRecord.imgUri = "�澯��ʽ.ico";
			MenuNode dragtypeNode = new MenuNode(dragtypeRecord);
			dragRecordNode.add(dragtypeNode);

			MenuTreeRecord dragset = new MenuTreeRecord();
			dragset.name = "�澯����";
			dragset.type = "dragMgr";
			dragset.number = 50002;
			dragset.imgUri = "�澯����.ico";
			MenuNode dragsetNode = new MenuNode(dragset);
			dragRecordNode.add(dragsetNode);

			MenuTreeRecord dragReportRecord = new MenuTreeRecord();
			dragReportRecord.name = "�澯��¼";
			dragReportRecord.type = "dragMgr";
			dragReportRecord.number = 50003;
			dragReportRecord.imgUri = "�澯��¼.ico";
			MenuNode dragReportNode = new MenuNode(dragReportRecord);
			dragRecordNode.add(dragReportNode);
			{
				// �澯��¼
				MenuTreeRecord dragcurrent = new MenuTreeRecord();
				dragcurrent.name = "��ǰ�澯";
				dragcurrent.type = "dragMgr";
				dragcurrent.number = 500031;
				dragcurrent.imgUri = "��ǰ�澯.ico";
				MenuNode dragcurrentNode = new MenuNode(dragcurrent);
				dragReportNode.add(dragcurrentNode);
				{
					MenuTreeRecord ipmacbound = new MenuTreeRecord();
					ipmacbound.name = "ipmac��";
					ipmacbound.type = "dragMgr";
					ipmacbound.number = 5000311;
					ipmacbound.imgUri = "�澯����.ico";
					MenuNode ipmacboundNode = new MenuNode(ipmacbound);
					dragcurrentNode.add(ipmacboundNode);
				}

				MenuTreeRecord draghis = new MenuTreeRecord();
				draghis.name = "��ʷ�澯";
				draghis.type = "dragMgr";
				draghis.number = 500032;
				draghis.imgUri = "��ʷ�澯.ico";
				MenuNode draghisNode = new MenuNode(draghis);
				dragReportNode.add(draghisNode);
			}

		}
		{
			// ��ⱨ��
			buildMonitorReportMenu();
		}
		{
			// ��־�˵�
			buildLogMenu();
		}
		{
			// ϵͳ����
			buildSysMenu();
		}
		tv.setInput(root);
		// �Զ���ķ�����ʵ��˫������Ӧ�ı༭���Ĺ���
		hookDoubleClickAction();
	}

	/**
	 * ϵͳ���ò˵� number 8000
	 */
	private void buildSysMenu() {
		// ϵͳ����
		MenuTreeRecord sysRecord = new MenuTreeRecord();
		sysRecord.name = "ϵͳ����";
		sysRecord.type = "sysset";
		sysRecord.number = 8000;
		sysRecord.imgUri = "PC_Blue.ico";
		MenuNode sysRecordNode = new MenuNode(sysRecord);
		root.add(sysRecordNode);

		MenuTreeRecord sysRecord1 = new MenuTreeRecord();
		sysRecord1.name = "�û�����";
		sysRecord1.type = "sysset";
		sysRecord1.number = 80001;
		sysRecord1.imgUri = "�û�����.ico";
		MenuNode sysRecordNode1 = new MenuNode(sysRecord1);
		sysRecordNode.add(sysRecordNode1);

		{
			// �û������Ӳ˵�
			MenuTreeRecord sysRecord11 = new MenuTreeRecord();
			sysRecord11.name = "�û�";
			sysRecord11.type = "sysset";
			sysRecord11.number = 800011;
			sysRecord11.imgUri = "�û�.ico";
			MenuNode sysRecordNode11 = new MenuNode(sysRecord11);
			sysRecordNode1.add(sysRecordNode11);

			MenuTreeRecord sysRecord12 = new MenuTreeRecord();
			sysRecord12.name = "�û���";
			sysRecord12.type = "sysset";
			sysRecord12.number = 800012;
			sysRecord12.imgUri = "�û���.ico";
			MenuNode sysRecordNode12 = new MenuNode(sysRecord12);
			sysRecordNode1.add(sysRecordNode12);

			MenuTreeRecord sysRecord13 = new MenuTreeRecord();
			sysRecord13.name = "�豸��";
			sysRecord13.type = "sysset";
			sysRecord13.number = 800013;
			sysRecord13.imgUri = "�豸��.ico";
			MenuNode sysRecordNode13 = new MenuNode(sysRecord13);
			sysRecordNode1.add(sysRecordNode13);
			{
				// �豸�����Ӳ˵�
			}
		}

		MenuTreeRecord logRecord2 = new MenuTreeRecord();
		logRecord2.name = "���ݹ���";
		logRecord2.type = "sysset";
		logRecord2.number = 80002;
		logRecord2.imgUri = "PC_Blue.ico";
		MenuNode sysRecordNode2 = new MenuNode(logRecord2);
		sysRecordNode.add(sysRecordNode2);
		{
			// ���ݹ����Ӳ˵�
			MenuTreeRecord logRecord21 = new MenuTreeRecord();
			logRecord21.name = "�ɼ�����";
			logRecord21.type = "sysset";
			logRecord21.number = 800021;
			logRecord21.imgUri = "PC_Blue.ico";
			MenuNode sysRecordNode21 = new MenuNode(logRecord21);
			sysRecordNode2.add(sysRecordNode21);

			MenuTreeRecord logRecord22 = new MenuTreeRecord();
			logRecord22.name = "���ݹ���";
			logRecord22.type = "sysset";
			logRecord22.number = 800021;
			logRecord22.imgUri = "PC_Blue.ico";
			MenuNode sysRecordNode22 = new MenuNode(logRecord22);
			sysRecordNode2.add(sysRecordNode22);
		}

		MenuTreeRecord logRecord3 = new MenuTreeRecord();
		logRecord3.name = "SysLog����";
		logRecord3.type = "sysset";
		logRecord3.number = 80003;
		logRecord3.imgUri = "PC_Blue.ico";
		MenuNode sysRecordNode3 = new MenuNode(logRecord3);
		sysRecordNode.add(sysRecordNode3);

		MenuTreeRecord logRecord4 = new MenuTreeRecord();
		logRecord4.name = "������������";
		logRecord4.type = "sysset";
		logRecord4.number = 80004;
		logRecord4.imgUri = "PC_Blue.ico";
		MenuNode sysRecordNode4 = new MenuNode(logRecord4);
		sysRecordNode.add(sysRecordNode4);
	}

	/**
	 * ������־�˵� number 7000
	 */
	private void buildLogMenu() {
		// ��־
		MenuTreeRecord logRecord = new MenuTreeRecord();
		logRecord.name = "��־����";
		logRecord.type = "logMgr";
		logRecord.number = 7000;
		logRecord.imgUri = "��־.ico";
		MenuNode logRecordNode = new MenuNode(logRecord);
		root.add(logRecordNode);

		MenuTreeRecord logRecord1 = new MenuTreeRecord();
		logRecord1.name = "������־";
		logRecord1.type = "logMgr";
		logRecord1.number = 70001;
		logRecord1.imgUri = "������־.ico";
		MenuNode dragtypeNode1 = new MenuNode(logRecord1);
		logRecordNode.add(dragtypeNode1);

		MenuTreeRecord logRecord2 = new MenuTreeRecord();
		logRecord2.name = "ɨ����־";
		logRecord2.type = "logMgr";
		logRecord2.number = 70002;
		logRecord2.imgUri = "ɨ����־.ico";
		MenuNode dragsetNode2 = new MenuNode(logRecord2);
		logRecordNode.add(dragsetNode2);

	}

	/**
	 * ��ⱨ��˵� number = 6000
	 */
	private void buildMonitorReportMenu() {
		MenuTreeRecord monitorReportRecord = new MenuTreeRecord();
		monitorReportRecord.name = "��ⱨ��";
		monitorReportRecord.type = "monitorReportMgr";
		monitorReportRecord.number = 6000;
		monitorReportRecord.imgUri = "��ⱨ��.ico";
		MenuNode monitorReportNode = new MenuNode(monitorReportRecord);
		root.add(monitorReportNode);

		MenuTreeRecord monitorReportRecord1 = new MenuTreeRecord();
		monitorReportRecord1.name = "�豸״̬ʵʱ����";
		monitorReportRecord1.type = "monitorReportMgr";
		monitorReportRecord1.number = 60001;
		monitorReportRecord1.imgUri = "PC_Blue.ico";
		MenuNode monitortypeNode1 = new MenuNode(monitorReportRecord1);
		monitorReportNode.add(monitortypeNode1);

		{
			// �豸״̬ʵʱ�����Ӳ˵�
			MenuTreeRecord monitorReportRecord11 = new MenuTreeRecord();
			monitorReportRecord11.name = "�豸��Ʒ״̬ʵʱ����";
			monitorReportRecord11.type = "monitorReportMgr";
			monitorReportRecord11.number = 600011;
			monitorReportRecord11.imgUri = "�豸�˿�״̬ʵʱ����.ico";
			MenuNode monitorReportRecord11Node = new MenuNode(
					monitorReportRecord11);
			monitortypeNode1.add(monitorReportRecord11Node);

			MenuTreeRecord monitorReportRecord12 = new MenuTreeRecord();
			monitorReportRecord12.name = "CPU&MEMʵʱ����";
			monitorReportRecord12.type = "monitorReportMgr";
			monitorReportRecord12.number = 600011;
			monitorReportRecord12.imgUri = "CPU&MEMʵʱ����.ico";
			MenuNode monitorReportRecord12Node = new MenuNode(
					monitorReportRecord12);
			monitortypeNode1.add(monitorReportRecord12Node);
		}

		MenuTreeRecord monitorReportRecord2 = new MenuTreeRecord();
		monitorReportRecord2.name = "��ʷ����ѯ";
		monitorReportRecord2.type = "monitorReportMgr";
		monitorReportRecord2.number = 60002;
		monitorReportRecord2.imgUri = "��ʷ����ѯ.ico";
		MenuNode monitorsetNode2 = new MenuNode(monitorReportRecord2);
		monitorReportNode.add(monitorsetNode2);

		{
			// �豸״̬ʵʱ�����Ӳ˵�
			MenuTreeRecord monitorReportRecord21 = new MenuTreeRecord();
			monitorReportRecord21.name = "�˿���ʷ���ݲ�ѯ";
			monitorReportRecord21.type = "monitorReportMgr";
			monitorReportRecord21.number = 600021;
			monitorReportRecord21.imgUri = "�˿���ʷ���ݲ�ѯ.ico";
			MenuNode monitorReportRecord12Node = new MenuNode(
					monitorReportRecord21);
			monitorsetNode2.add(monitorReportRecord12Node);

			MenuTreeRecord monitorReportRecord22 = new MenuTreeRecord();
			monitorReportRecord22.name = "CPU&MEM��ʷ���ݲ�ѯ";
			monitorReportRecord22.type = "monitorReportMgr";
			monitorReportRecord22.number = 600021;
			monitorReportRecord22.imgUri = "CPU&MEM��ʷ���ݲ�ѯ.ico";
			MenuNode monitorReportRecord22Node = new MenuNode(
					monitorReportRecord22);
			monitorsetNode2.add(monitorReportRecord22Node);
		}
		MenuTreeRecord monitorReportRecord3 = new MenuTreeRecord();
		monitorReportRecord3.name = "�����豸����ѯ";
		monitorReportRecord3.type = "monitorReportMgr";
		monitorReportRecord3.number = 60003;
		monitorReportRecord3.imgUri = "�����豸����ѯ.ico";
		MenuNode monitorReportNode3 = new MenuNode(monitorReportRecord3);
		monitorReportNode.add(monitorReportNode3);

		MenuTreeRecord monitorReportRecord4 = new MenuTreeRecord();
		monitorReportRecord4.name = "�豸���ܷ�������";
		monitorReportRecord4.type = "monitorReportMgr";
		monitorReportRecord4.number = 60004;
		monitorReportRecord4.imgUri = "�豸���ܷ�������.ico";
		MenuNode monitorReportNode4 = new MenuNode(monitorReportRecord4);
		monitorReportNode.add(monitorReportNode4);

		MenuTreeRecord monitorReportRecord5 = new MenuTreeRecord();
		monitorReportRecord5.name = "�豸�˿�ʹ����";
		monitorReportRecord5.type = "monitorReportMgr";
		monitorReportRecord5.number = 60005;
		monitorReportRecord5.imgUri = "CPU����_�ڴ�ռ��.ico";
		MenuNode monitorReportNode5 = new MenuNode(monitorReportRecord5);
		monitorReportNode.add(monitorReportNode5);

		MenuTreeRecord monitorReportRecord6 = new MenuTreeRecord();
		monitorReportRecord6.name = "�����������ܷ���";
		monitorReportRecord6.type = "monitorReportMgr";
		monitorReportRecord6.number = 60006;
		monitorReportRecord6.imgUri = "�����������ܷ���.ico";
		MenuNode monitorReportNode6 = new MenuNode(monitorReportRecord6);
		monitorReportNode.add(monitorReportNode6);

		MenuTreeRecord monitorReportRecord7 = new MenuTreeRecord();
		monitorReportRecord7.name = "�豸�������Ʒ���";
		monitorReportRecord7.type = "monitorReportMgr";
		monitorReportRecord7.number = 60007;
		monitorReportRecord7.imgUri = "����������Ʒ���.ico";
		MenuNode monitorReportNode7 = new MenuNode(monitorReportRecord7);
		monitorReportNode.add(monitorReportNode7);

		MenuTreeRecord monitorReportRecord8 = new MenuTreeRecord();
		monitorReportRecord8.name = "SysLog��ѯ";
		monitorReportRecord8.type = "monitorReportMgr";
		monitorReportRecord8.number = 60008;
		monitorReportRecord8.imgUri = "PC_Blue.ico";
		MenuNode monitorReportNode8 = new MenuNode(monitorReportRecord8);
		monitorReportNode.add(monitorReportNode8);

		MenuTreeRecord monitorReportRecord9 = new MenuTreeRecord();
		monitorReportRecord9.name = "�Զ����ɱ���";
		monitorReportRecord9.type = "monitorReportMgr";
		monitorReportRecord9.number = 60009;
		monitorReportRecord9.imgUri = "PC_Blue.ico";
		MenuNode monitorReportNode9 = new MenuNode(monitorReportRecord9);
		monitorReportNode.add(monitorReportNode9);

	}

	private void doListener(EventObject event) {
		ISelection selection = tv.getSelection();
		// �õ�ѡ�е��ע�ⷽ���ǽ��õ���ѡ��ת���� IStructuredSelection���ٵ��� getFirstElement ����
		Object object = ((IStructuredSelection) selection).getFirstElement();
		MenuNode node = (MenuNode) object;
		MenuTreeRecord record = (MenuTreeRecord) node.getUserObject();
		ShowDeviceEditor.type = record.number;
		// ����ͼ����˵�
		if (record.type.equals("topoMgr")) {
			// ��ɨ��������
			if (record.number == 10001) {
				ScanDialog dialog = new ScanDialog(Display.getCurrent()
						.getActiveShell(), new SomeWizard());
				dialog.open();
			}
			// ����ɨ��
			if (record.number == 10002) {
				if (GlobalData.isConfiged)
					StartScan.getInstance(parent).scanTopo();
				DrawTopo.getInstance(parent).showTopo();
			}
		} else if (record.type.equals("mib")) {
			try {
				IEditorPart editorPart = window.getActivePage().findEditor(
						input);
				if (editorPart == null) {
					window.getActivePage().openEditor(input, MibEditorPart.ID);
				}
				MibEditorPart part = (MibEditorPart) editorPart;
				if (record.number != 2000 && record.number != 20001) {
					String oid = treeSupport.oidSupport.getNodeOid(node);
					MibEditorPart.oidText.setText(oid);
				}

			} catch (PartInitException e) {
				e.printStackTrace();
			}

		} else if (record.type.equals("deviceMgr")) {
			try {
				PlatformUI
						.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.openEditor(new DeviceEditrInput(), ShowDeviceEditor.ID);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		} else if (record.type.equals("ipMgr")) {// ip����
			if (record.number == 40001) {
				Set<SubnetInfo> subSet = GlobalData.subnetDeviceMap.keySet();
				List<String> subNum = new ArrayList<String>();
				for(SubnetInfo i : subSet){
					subNum.add(i.getSubnetNum());
				}
				Collections.sort(subNum);
				for (int i = 0; i < subNum.size(); i++) {
					int number = 400011;
					MenuTreeRecord tmpR = new MenuTreeRecord();
					tmpR.number = number;
					tmpR.type="ipMgr";
					tmpR.name = subNum.get(i);
					tmpR.imgUri = record.imgUri;
					MenuNode rmpN = new MenuNode(tmpR);
					node.add(rmpN);
				}
				tv.refresh();
			}
			if (record.number == 40002) {// ip��׼����չʾ
				try {
					PlatformUI
							.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.openEditor(new IpMacBaseDataInput(),
									IpMacBaseDataEditor.ID);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
			if (record.number == 400011) {
				try {
					SubNetMgrInput input = new SubNetMgrInput();
					input.setSubnetIp(record.name);
					SubnetInfo info = new SubnetInfo();
					info.setSubnetNum(record.name);
					input.setInputData(GlobalData.subnetDeviceMap.get(info));
					PlatformUI
							.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.openEditor(input,
									SubNetMgrEditor.ID);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
		// showError("", ((MenuTreeRecord)item.getUserObject()).syntax);

	}

	private void hookDoubleClickAction() {
		// tv.addOpenListener(new IOpenListener() {
		//
		// @Override
		// public void open(OpenEvent event) {
		// doListener(event);
		// }
		// });
		tv.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				doListener(event);
			}
		});
	}

	public String getProductPath() {
		Location location = Platform.getConfigurationLocation();
		URL url = location.getURL();
		return url.getPath();
	}

	@Override
	public void setFocus() {

	}

	/**
	 * treeview�����ṩ��
	 * 
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

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Object[] getElements(Object inputElement) {
			Vector v = ((MenuNode) inputElement).getChildrenVector();
			Collections.sort(v);
			return v.toArray();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Object[] getChildren(Object parentElement) {
			Vector v = ((MenuNode) parentElement).getChildrenVector();
			Collections.sort(v);
			return v.toArray();
		}

		@Override
		public Object getParent(Object element) {
			return ((MenuNode) element).getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			return ((MenuNode) element).children().hasMoreElements();
		}

	}

	/**
	 * treeviewlalel�ṩ��
	 * 
	 * @author haiming.wang
	 * 
	 */
	class TreeLabelProvide implements ILabelProvider {

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
			MenuNode item = (MenuNode) element;
			MenuTreeRecord record = (MenuTreeRecord) item.getUserObject();
			String imgUrl = "";
			if (record.imgUri.equals("")) {
				MenuNode parantNode = (MenuNode) item.getParent();
				if ("".equals(record.syntax)) {
					if (item.isLeaf()) {
						imgUrl = "2.ico";
					} else
						imgUrl = "01.ico";
				} else if (record.syntax.indexOf("SEQUENCE") >= 0) {
					imgUrl = "4.ico";
				} else if ((record.syntax.indexOf("SEQUENCE") < 0)
						&& (record.syntax.indexOf("Entry") > 0)) {
					imgUrl = "6.ico";
				} else {
					MenuTreeRecord pRecord = (MenuTreeRecord) parantNode
							.getUserObject();
					if (pRecord.syntax.indexOf("Entry") > -1) {
						imgUrl = "4.ico";
					} else {
						imgUrl = "2.ico";
					}
				}
			} else {
				imgUrl = record.imgUri;
			}
			final Bundle bundle = Platform.getBundle("com.siteview.nnm.main");
			System.out.println(imgUrl);
			URL url = bundle.getEntry("icons/NNM_NewIcon/NodeIcon/" + imgUrl);
			return ImageDescriptor.createFromURL(url).createImage();
		}

		@Override
		public String getText(Object element) {
			MenuNode item = (MenuNode) element;

			MenuTreeRecord record = (MenuTreeRecord) item.getUserObject();
			String Text = record.name;
			if (record.number < 1000 && !record.name.equals("root")
					&& !record.name.equals("Variables/Textual Conventions")
					&& !record.name.equals("Orphans")) {
				Text = Text + "[" + record.number + "]";

			}
			return (Text);
		}

	}
}

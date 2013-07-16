package com.siteview.css.topo.wizard.scan;


import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.siteview.css.topo.wizard.common.GlobalData;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.util.IoUtils;
import com.siteview.snmp.util.ScanUtils;
import com.siteview.snmp.util.Utils;

/**
 * ɨ����
 * @author zhangxinnan
 * 
 */

public class SomeWizard extends Wizard {
	private ScanParameterWizard paramPage = new ScanParameterWizard();
	private ScanScopeWizard scopePage = new ScanScopeWizard();
	private OutScopeWizard filterPage = new OutScopeWizard();
	private CommunitySetupWizard communityPage = new CommunitySetupWizard();
	private ScanSeedsWizard seedsPage = new ScanSeedsWizard();
	Table tab1 = null;
	public SomeWizard() {
		//���ȶ�ȡ�ϴα����ɨ�����
		ScanParam sp = IoUtils.readScanParam();
		if(sp != null){
			//��ʼ������ɨ�����
			paramPage.setDepth(sp.getDepth());
			paramPage.setRetry(sp.getRetrytimes());
			paramPage.setThreadCount(sp.getThreadCount());
			paramPage.setTimeOut(sp.getTimeout());
			//��ʼ��ɨ�跶Χ
			scopePage.setScaleList(sp.getScan_scales());
			//��ʼ��ɨ����˷�Χ�б�
			filterPage.setFilterList(sp.getFilter_scales());
			//��ʼ��ɨ������
			seedsPage.setSeedList(sp.getScan_seeds());
			//��ʼ����д��ͬ��
			communityPage.setCommunityGet(sp.getCommunity_get_dft());
			communityPage.setCommunitySet(sp.getCommunity_set_dft());
		}
		setWindowTitle("ɨ��");
		this.setDialogSettings(new DialogSettings("���빤��"));
		addPage(paramPage);
		addPage(scopePage);
		addPage(filterPage);
		addPage(communityPage);
		addPage(seedsPage);
		
	}

	/**
	 * ɨ�����
	 */
	public boolean performFinish() {
		int searchDepth = 0;
		int parallelThreads = 0;
		int retryCount = 0;
		int timeOut = 0;
		Control[] groups = paramPage.getGroup().getChildren();
		//��ȡ����ɨ����Ϣ
		for(int i=0;i<groups.length;i++){
			Control c = groups[i];
			if(c instanceof Spinner){
				Spinner spi = (Spinner)c;
				if(i == 1){
					searchDepth = spi.getSelection();
				}
				if(i == 3){
					parallelThreads = spi.getSelection();
				}
				if(i == 5){
					retryCount = spi.getSelection();
				}
				if(i == 7){
					timeOut = spi.getSelection();
				}
			}
			
		}
		//���������
		GlobalData.scanParam = new ScanParam();
		// ����ɨ�����
		GlobalData.scanParam.setDepth(searchDepth);
		GlobalData.scanParam.setThreadCount(parallelThreads);
		GlobalData.scanParam.setRetrytimes(retryCount);
		GlobalData.scanParam.setTimeout(timeOut);
		TableItem[] items = scopePage.getTable().getItems();
		/**
		 * ��ʼ��ɨ��IP��Χ
		 */
		for(int i=0;i<items.length;i++){
			TableItem tmp = items[i];
			String beginIp = tmp.getText(0);
			String endIp = tmp.getText(1);
			if(Utils.isIp(beginIp) && Utils.isIp(endIp)){
				GlobalData.scanParam.getScan_scales().add(new Pair<String, String>(beginIp, endIp));
				GlobalData.scanParam.getScan_scales_num().add(new Pair<Long, Long>(ScanUtils.ipToLong(beginIp), ScanUtils.ipToLong(endIp)));
			}
		}
		/**
		 * ��ʼ��ɨ�����IP
		 */
		TableItem[] filterItems = filterPage.getTable().getItems();
		for(int i=0;i<filterItems.length;i++){
			String beginIp = filterItems[i].getText(0);
			String endIp = filterItems[i].getText(1);
			if(Utils.isIp(beginIp) && Utils.isIp(endIp)){
				GlobalData.scanParam.getFilter_scales().add(new Pair<String, String>(beginIp, endIp));
				GlobalData.scanParam.getFilter_scales_num().add(new Pair<Long, Long>(ScanUtils.ipToLong(beginIp), ScanUtils.ipToLong(endIp)));
			}
		}
		String getCommunity = communityPage.getGetCommu().getText();
		String setCommunity = communityPage.getSetCommu().getText();
		if(!Utils.isEmptyOrBlank(getCommunity)){
			GlobalData.scanParam.setCommunity_get_dft(getCommunity);
			GlobalData.scanParam.setCommunity_set_dft(setCommunity);
		}
		
		/**
		 * ��ʼ��ɨ������
		 */
		TableItem[] seedsItems = seedsPage.getSeedsTable().getItems();
		for(int i=0;i<seedsItems.length;i++){
			TableItem item = seedsItems[i];
			if(!Utils.isEmptyOrBlank(item.getText(0)) && Utils.isIp(item.getText(0))){
				GlobalData.scanParam.getScan_seeds().add(item.getText(0));
			}
		}
		if(GlobalData.scanParam.getScan_scales().isEmpty() && GlobalData.scanParam.getScan_seeds().isEmpty()){
			//���ɨ�����Ӻ�ɨ�跶Χ��Ϊ�ղ��ܵ��finish��ť
			seedsPage.setErrorMessage("ɨ�����Ӻ�ɨ�跶Χ��������һ�");
			return false;
		}
		GlobalData.isConfiged = true;
		//����ɨ�����
		IoUtils.saveScanParam(GlobalData.scanParam);
		// ����ȡ�����ݽ�����һ������
		System.out.println("�������" + searchDepth + "�����߳���" + parallelThreads
				+ "���Դ���" + retryCount + "��ʱʱ��" + timeOut);
		return true;
	}
	public boolean canFinish() {
		   //������ǰҳ��Ϊ��лҳ��ʱ�Ž�����ɡ���ť��Ϊ����״̬
		   if (this.getContainer().getCurrentPage() == seedsPage )
		    return true;
		   else
		    return false;
	}
	
}

package com.siteview.css.topo.wizard.scan;


import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.siteview.css.topo.wizard.common.GlobalData;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.util.IoUtils;
import com.siteview.snmp.util.ScanUtils;
import com.siteview.snmp.util.Utils;

/**
 * 扫描向导
 * @author zhangxinnan
 * 
 */

public class SomeWizard extends Wizard {
	private ScanParameterWizard paramPage = new ScanParameterWizard();
	private ScanScopeWizard scopePage = new ScanScopeWizard();
	private OutScopeWizard filterPage = new OutScopeWizard();
	private CommunitySetupWizard communityPage = new CommunitySetupWizard();
	private ScanSeedsWizard seedsPage = new ScanSeedsWizard();
	private Table tab;
	protected void setTable(Table tab){
		this.tab = tab;
	}
	Table tab1 = null;
	public SomeWizard() {
		setWindowTitle("扫描");
		this.setDialogSettings(new DialogSettings("导入工程"));
		addPage(paramPage);
		addPage(scopePage);
		addPage(filterPage);
		addPage(communityPage);
		addPage(seedsPage);
		
	}

	/**
	 * 扫描完成
	 */
	public boolean performFinish() {
		String retryValue = paramPage.getRetryCount().getText();
		IDialogSettings dialogSettings2 = this.getDialogSettings();
		int searchDepth = 0;
		int parallelThreads = 0;
		int retryCount = 0;
		int timeOut = 0;
		String[] array = dialogSettings2.getArray("Array");
		Object value1 = paramPage.getGroup().getText();
		Control[] groups = paramPage.getGroup().getChildren();
		//获取基本扫描信息
		for(int i=0;i<groups.length;i++){
			Control c = groups[i];
			if(c instanceof Spinner){
				@SuppressWarnings("unused")
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
		// 构造扫描参数
		GlobalData.scanParam.setDepth(searchDepth);
		GlobalData.scanParam.setThreadCount(parallelThreads);
		GlobalData.scanParam.setRetrytimes(retryCount);
		GlobalData.scanParam.setTimeout(timeOut);
		TableItem[] items = scopePage.getTable().getItems();
		/**
		 * 初始化扫描IP范围
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
		 * 初始化扫描过滤IP
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
		 * 初始化扫描种子
		 */
		TableItem[] seedsItems = seedsPage.getSeedsTable().getItems();
		for(int i=0;i<seedsItems.length;i++){
			TableItem item = seedsItems[i];
			if(!Utils.isEmptyOrBlank(item.getText(0)) && Utils.isIp(item.getText(0))){
				GlobalData.scanParam.getScan_seeds().add(item.getText(0));
			}
		}
		GlobalData.isConfiged = true;
		//缓存扫描参数
		IoUtils.saveScanParam(GlobalData.scanParam);
		// 将获取的数据进行下一步操作
		System.out.println("收索深度" + searchDepth + "并行线程数" + parallelThreads
				+ "重试次数" + retryCount + "超时时间" + timeOut);
		return true;
	}
}

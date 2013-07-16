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
	Table tab1 = null;
	public SomeWizard() {
		//首先读取上次保存的扫描参数
		ScanParam sp = IoUtils.readScanParam();
		if(sp != null){
			//初始化基本扫描参数
			paramPage.setDepth(sp.getDepth());
			paramPage.setRetry(sp.getRetrytimes());
			paramPage.setThreadCount(sp.getThreadCount());
			paramPage.setTimeOut(sp.getTimeout());
			//初始化扫描范围
			scopePage.setScaleList(sp.getScan_scales());
			//初始化扫描过滤范围列表
			filterPage.setFilterList(sp.getFilter_scales());
			//初始化扫描种子
			seedsPage.setSeedList(sp.getScan_seeds());
			//初始化读写共同体
			communityPage.setCommunityGet(sp.getCommunity_get_dft());
			communityPage.setCommunitySet(sp.getCommunity_set_dft());
		}
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
		int searchDepth = 0;
		int parallelThreads = 0;
		int retryCount = 0;
		int timeOut = 0;
		Control[] groups = paramPage.getGroup().getChildren();
		//获取基本扫描信息
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
		//先清空数据
		GlobalData.scanParam = new ScanParam();
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
		if(GlobalData.scanParam.getScan_scales().isEmpty() && GlobalData.scanParam.getScan_seeds().isEmpty()){
			//如果扫描种子和扫描范围都为空不能点击finish按钮
			seedsPage.setErrorMessage("扫描种子和扫描范围必须配置一项！");
			return false;
		}
		GlobalData.isConfiged = true;
		//缓存扫描参数
		IoUtils.saveScanParam(GlobalData.scanParam);
		// 将获取的数据进行下一步操作
		System.out.println("收索深度" + searchDepth + "并行线程数" + parallelThreads
				+ "重试次数" + retryCount + "超时时间" + timeOut);
		return true;
	}
	public boolean canFinish() {
		   //仅当当前页面为感谢页面时才将“完成”按钮置为可用状态
		   if (this.getContainer().getCurrentPage() == seedsPage )
		    return true;
		   else
		    return false;
	}
	
}

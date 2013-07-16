package com.siteview.css.topo.wizard.scan;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import com.siteview.snmp.util.Utils;

/**
 * 点击扫描时打开dialog
 * @author zhangxinnan
 * 
 */
public class ScanDialog extends WizardDialog{
	public ScanDialog(Shell parentShell,IWizard wizard) {
		super(parentShell,wizard);
	}
	/**
	 * 点击下一页时对输入的数据进行校验
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		System.out.println();
		if (buttonId == IDialogConstants.NEXT_ID) {
			IWizardPage currentPage = getWizard().getContainer()
					.getCurrentPage();
			//校验IP地址范围的数据格式
			if (currentPage instanceof ScanScopeWizard) {
				ScanScopeWizard scopePage = (ScanScopeWizard) currentPage;
				TableItem[] items = scopePage.getTable().getItems();
				for (int i = 0;i<items.length;i++) {
					if(Utils.isEmptyOrBlank(items[i].getText(0)) && Utils.isEmptyOrBlank(items[i].getText(1))){
						continue;
					}
					if((Utils.isEmptyOrBlank(items[i].getText(0)) && !Utils.isEmptyOrBlank(items[i].getText(1)))
							||(!Utils.isEmptyOrBlank(items[i].getText(1)) && Utils.isEmptyOrBlank(items[i].getText(0)))){
						((ScanScopeWizard) currentPage)
							.setErrorMessage("起始IP和结束IP不能只填一个");
						return;
					}
					if (!Utils.isIp(items[i].getText(0)) || !Utils.isIp(items[i].getText(1))) {
						((ScanScopeWizard) currentPage)
								.setErrorMessage("IP地址格式不正确(必须为点分十进制格式)！");
						return;
					}
				}
				//校验IP过滤范围的数据格式
			}else if(currentPage instanceof OutScopeWizard){
				OutScopeWizard outPage = (OutScopeWizard) currentPage;
				TableItem[] items = outPage.getTable().getItems();
				for (int i = 0;i<items.length;i++) {
					if(Utils.isEmptyOrBlank(items[i].getText(0)) && Utils.isEmptyOrBlank(items[i].getText(1))){
						continue;
					}
					if((Utils.isEmptyOrBlank(items[i].getText(0)) && !Utils.isEmptyOrBlank(items[i].getText(1)))
							||(!Utils.isEmptyOrBlank(items[i].getText(1)) && Utils.isEmptyOrBlank(items[i].getText(0)))){
						((ScanScopeWizard) currentPage)
							.setErrorMessage("起始IP和结束IP不能只填一个");
						return;
					}
					if (!Utils.isIp(items[i].getText(0)) || !Utils.isIp(items[i].getText(1))) {
						((ScanScopeWizard) currentPage)
								.setErrorMessage("IP地址格式不正确(必须为点分十进制格式)！");
						return;
					}
				}
				//校验种子IP格式
			}else if(currentPage instanceof ScanSeedsWizard){
				ScanSeedsWizard seedPage = (ScanSeedsWizard) currentPage;
				TableItem[] items = seedPage.getSeedsTable().getItems();
				for (int i = 0;i<items.length;i++) {
					if(Utils.isEmptyOrBlank(items[i].getText())){
						continue;
					}
					if (!Utils.isIp(items[i].getText())) {
						((ScanScopeWizard) currentPage)
								.setErrorMessage("IP地址格式不正确(必须为点分十进制格式)！");
						return;
					}
				}
			}
		}
		super.buttonPressed(buttonId);
	}
}

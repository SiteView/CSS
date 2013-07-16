package com.siteview.css.topo.wizard.scan;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import com.siteview.snmp.util.Utils;

/**
 * ���ɨ��ʱ��dialog
 * @author zhangxinnan
 * 
 */
public class ScanDialog extends WizardDialog{
	public ScanDialog(Shell parentShell,IWizard wizard) {
		super(parentShell,wizard);
	}
	/**
	 * �����һҳʱ����������ݽ���У��
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		System.out.println();
		if (buttonId == IDialogConstants.NEXT_ID) {
			IWizardPage currentPage = getWizard().getContainer()
					.getCurrentPage();
			//У��IP��ַ��Χ�����ݸ�ʽ
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
							.setErrorMessage("��ʼIP�ͽ���IP����ֻ��һ��");
						return;
					}
					if (!Utils.isIp(items[i].getText(0)) || !Utils.isIp(items[i].getText(1))) {
						((ScanScopeWizard) currentPage)
								.setErrorMessage("IP��ַ��ʽ����ȷ(����Ϊ���ʮ���Ƹ�ʽ)��");
						return;
					}
				}
				//У��IP���˷�Χ�����ݸ�ʽ
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
							.setErrorMessage("��ʼIP�ͽ���IP����ֻ��һ��");
						return;
					}
					if (!Utils.isIp(items[i].getText(0)) || !Utils.isIp(items[i].getText(1))) {
						((ScanScopeWizard) currentPage)
								.setErrorMessage("IP��ַ��ʽ����ȷ(����Ϊ���ʮ���Ƹ�ʽ)��");
						return;
					}
				}
				//У������IP��ʽ
			}else if(currentPage instanceof ScanSeedsWizard){
				ScanSeedsWizard seedPage = (ScanSeedsWizard) currentPage;
				TableItem[] items = seedPage.getSeedsTable().getItems();
				for (int i = 0;i<items.length;i++) {
					if(Utils.isEmptyOrBlank(items[i].getText())){
						continue;
					}
					if (!Utils.isIp(items[i].getText())) {
						((ScanScopeWizard) currentPage)
								.setErrorMessage("IP��ַ��ʽ����ȷ(����Ϊ���ʮ���Ƹ�ʽ)��");
						return;
					}
				}
			}
		}
		super.buttonPressed(buttonId);
	}
}

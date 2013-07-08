package com.siteview.css.topo.wizard.scan;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;

/**
 * 扫描向导
 * 
 * @author zhangxinnan
 * 
 */
public class SomeWizard extends Wizard {

	public SomeWizard() {
		setWindowTitle("扫描");
		this.setDialogSettings(new DialogSettings("导入工程"));
		addPage(new ScanParameterWizard());
		addPage(new ScanScopeWizard());
		addPage(new OutScopeWizard());
		addPage(new CommunitySetupWizard());
		addPage(new ScanSeedsWizard());
	}

	/**
	 * 扫描完成
	 */
	public boolean performFinish() {
		IDialogSettings dialogSettings2 = this.getDialogSettings();
		String searchDepth = dialogSettings2.get("收索深度");
		String parallelThreads = dialogSettings2.get("并行线程数");
		String retryCount = dialogSettings2.get("重试次数");
		String timeOut = dialogSettings2.get("超时时间");
		String startID = dialogSettings2.get("起始ID");
		String overID = dialogSettings2.get("终止ID");
		String[] array = dialogSettings2.getArray("Array");
		// 如果没有进行操作
		if (null == searchDepth) {
			searchDepth = "5";
		}
		if (null == parallelThreads) {
			parallelThreads = "50";
		}
		if (null == retryCount) {
			retryCount = "3";
		}
		if (null == timeOut) {
			timeOut = "99";
		}
		if (null == array) {
			array = new String[2];
			array[0] = "1";

		}
		// 将获取的数据进行下一步操作
		System.out.println("收索深度" + searchDepth + "并行线程数" + parallelThreads
				+ "重试次数" + retryCount + "超时时间" + timeOut);
		// System.out.println(startID + "-----" + overID);
		// for (int i = 0; i < array.length; i++) {
		// System.out.println(array[i].toString());
		// }
		return true;
	}
}

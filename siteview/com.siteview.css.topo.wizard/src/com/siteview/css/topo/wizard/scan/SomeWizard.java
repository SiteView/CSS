package com.siteview.css.topo.wizard.scan;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;

/**
 * ɨ����
 * 
 * @author zhangxinnan
 * 
 */
public class SomeWizard extends Wizard {

	public SomeWizard() {
		setWindowTitle("ɨ��");
		this.setDialogSettings(new DialogSettings("���빤��"));
		addPage(new ScanParameterWizard());
		addPage(new ScanScopeWizard());
		addPage(new OutScopeWizard());
		addPage(new CommunitySetupWizard());
		addPage(new ScanSeedsWizard());
	}

	/**
	 * ɨ�����
	 */
	public boolean performFinish() {
		IDialogSettings dialogSettings2 = this.getDialogSettings();
		String searchDepth = dialogSettings2.get("�������");
		String parallelThreads = dialogSettings2.get("�����߳���");
		String retryCount = dialogSettings2.get("���Դ���");
		String timeOut = dialogSettings2.get("��ʱʱ��");
		String startID = dialogSettings2.get("��ʼID");
		String overID = dialogSettings2.get("��ֹID");
		String[] array = dialogSettings2.getArray("Array");
		// ���û�н��в���
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
		// ����ȡ�����ݽ�����һ������
		System.out.println("�������" + searchDepth + "�����߳���" + parallelThreads
				+ "���Դ���" + retryCount + "��ʱʱ��" + timeOut);
		// System.out.println(startID + "-----" + overID);
		// for (int i = 0; i < array.length; i++) {
		// System.out.println(array[i].toString());
		// }
		return true;
	}
}

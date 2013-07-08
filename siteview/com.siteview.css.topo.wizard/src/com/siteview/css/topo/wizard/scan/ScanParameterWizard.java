package com.siteview.css.topo.wizard.scan;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * ɨ�������
 * 
 * @author zhangxinnan
 * 
 */
public class ScanParameterWizard extends WizardPage {
	private Label parallelThreads;
	private Label retryCount;
	private Label timeOut;
	/** ��ɫ */
	private Color COLOR_SYSTEM_RED = null;
	private Display display = null;

	protected ScanParameterWizard() {
		super("ScanParameterWizard");
		setTitle("ɨ����� -->" + "ɨ�跶Χ -->" + "�ų���Χ -->" + "��ͬ������ -->" + "ɨ������");
		setMessage("ɨ�����ð���ɨ�������ɨ�跶Χ������");
	}

	/**
	 * ������ͼ
	 */
	public void createControl(Composite parent) {
		// ������ɫ
		display = parent.getDisplay();
		COLOR_SYSTEM_RED = display.getSystemColor(SWT.COLOR_RED);
		// Ⱥ��
		Group group = new Group(parent, SWT.NONE);
		group.setText("ɨ�����");
		group.setLayout(new GridLayout(2, true));
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);

		// �������
		Label searchDepth = new Label(group, SWT.NONE);
		searchDepth.setText("\t\t������ȣ�");
		GridData layoutD = new GridData();
		layoutD.verticalIndent = 15;
		creatSpinner(group, 5, "�������");// ��������
		// �����߳���
		parallelThreads = new Label(group, SWT.NONE);
		parallelThreads.setText("\t\t�����߳�����");
		creatSpinner(group, 50, "�����߳���");

		// ���Դ���
		retryCount = new Label(group, SWT.NONE);
		retryCount.setText("\t\t���Դ�����");
		creatSpinner(group, 3, "���Դ���");

		// ��ʱ
		timeOut = new Label(group, SWT.NONE);
		timeOut.setText("\t\t��ʱʱ�䣺");
		creatSpinner(group, 100, "��ʱʱ��");
		/*
		 * ��ʾ�� Spinner spinner4 = new Spinner (group, SWT.BORDER);
		 * spinner4.setMinimum(0); spinner4.setMaximum(1000);
		 * spinner4.setSelection(5); spinner4.setIncrement(1);
		 * spinner4.setPageIncrement(100); spinner4.setLayoutData(new
		 * GridData(120, SWT.DEFAULT)); Rectangle clientArea4 =
		 * group.getClientArea(); spinner4.setLocation(clientArea4.x,
		 * clientArea4.y); spinner4.pack();
		 */
		setControl(group);
	}

	/**
	 * ��������ѡ�������
	 * 
	 * @param group
	 */

	public void creatSpinner(Composite group, int init, final String key) {

		final Spinner spinner = new Spinner(group, SWT.BORDER);
		spinner.setValues(init, 0, 99, 0, 1, 10);// ��ʼֵ,��Сֵ,���ֵ,С���c,�c�����ӵĶ���,����
		spinner.setLayoutData(new GridData(120, SWT.DEFAULT));
		spinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String string = ((Spinner) e.getSource()).getText();
				String message = null;
				try {
					int value = Integer.parseInt(string);
					// ������ͳһ����wizard�����ͨ�ô洢��DialogSettings����ֵ��
					ScanParameterWizard.this.getWizard().getDialogSettings()
							.put(key, value);
					int maximum = spinner.getMaximum();
					int minimum = spinner.getMinimum();
					if (value > maximum) {
						message = "Current input is greater than the maximum limit ("
								+ maximum + ")";
					} else if (value < minimum) {
						message = "Current input is less than the minimum limit ("
								+ minimum + ")";
					}
				} catch (Exception ex) {
					message = "Current input is not numeric";
				}
				if (message != null) {// ����д�����Ϣ
					spinner.setForeground(COLOR_SYSTEM_RED);// ����������ʡ��
					Rectangle rect = spinner.getBounds();
					GC gc = new GC(spinner);
					Point pt = gc.textExtent(string);
					gc.dispose();
				} else {
					spinner.setForeground(null);
				}
			}
		});
	}
}
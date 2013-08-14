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
	private Label timeOutLabel;
	private Group group = null; 
	
	private int depth = 5;
	private int timeOut = 200;
	private int retry = 2;
	private int threadCount = 50;
	
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public int getRetry() {
		return retry;
	}
	public void setRetry(int retry) {
		this.retry = retry;
	}
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	public Group getGroup(){
		return group;
	}
	public Label getParallelThreads() {
		return parallelThreads;
	}

	public void setParallelThreads(Label parallelThreads) {
		this.parallelThreads = parallelThreads;
	}

	public Label getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Label retryCount) {
		this.retryCount = retryCount;
	}

	public Label getTimeOutLabel() {
		return timeOutLabel;
	}

	public void setTimeOutLabel(Label timeOut) {
		this.timeOutLabel = timeOut;
	}

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
		group = new Group(parent, SWT.NONE);
		
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
		creatSpinner(group, depth, "�������");// ��������
		// �����߳���
		parallelThreads = new Label(group, SWT.NONE);
		parallelThreads.setText("\t\t�����߳�����");
		creatSpinner(group, threadCount, "�����߳���");

		// ���Դ���
		retryCount = new Label(group, SWT.NONE);
		retryCount.setText("\t\t���Դ�����");
		creatSpinner(group, retry, "���Դ���");

		// ��ʱ
		timeOutLabel = new Label(group, SWT.NONE);
		timeOutLabel.setText("\t\t��ʱʱ�䣺");
		creatSpinnerUsefull(group, timeOut, "��ʱʱ��",200,1000);
		setControl(group);
	}
	/**
	 * ��������ѡ�������
	 * 
	 * @param group
	 */

	public void creatSpinnerUsefull(Composite group, int init, final String key,int min,int max) {

		final Spinner spinner = new Spinner(group, SWT.BORDER);
		spinner.setValues(init, min, max, 0, 100, 10);// ��ʼֵ,��Сֵ,���ֵ,С���c,�c�����ӵĶ���,����
		spinner.setLayoutData(new GridData(120, SWT.DEFAULT));
		spinner.addModifyListener(new ModifyListener() {
			@SuppressWarnings("unused")
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
			@SuppressWarnings("unused")
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
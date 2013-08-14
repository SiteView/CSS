package com.siteview.css.topo.wizard.scan;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * 扫描参数向导
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

	/** 红色 */
	private Color COLOR_SYSTEM_RED = null;
	private Display display = null;

	protected ScanParameterWizard() {
		super("ScanParameterWizard");
		setTitle("扫描参数 -->" + "扫描范围 -->" + "排除范围 -->" + "共同体设置 -->" + "扫描种子");
		setMessage("扫描配置包括扫描参数和扫描范围两部分");
		
	}

	/**
	 * 创建视图
	 */
	public void createControl(Composite parent) {
		// 设置颜色
		display = parent.getDisplay();
		COLOR_SYSTEM_RED = display.getSystemColor(SWT.COLOR_RED);
		// 群组
		group = new Group(parent, SWT.NONE);
		
		group.setText("扫描参数");
		group.setLayout(new GridLayout(2, true));
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);

		// 收索深度
		Label searchDepth = new Label(group, SWT.NONE);
		searchDepth.setText("\t\t搜索深度：");
		GridData layoutD = new GridData();
		layoutD.verticalIndent = 15;
		creatSpinner(group, depth, "搜索深度");// 下拉调整
		// 并行线程数
		parallelThreads = new Label(group, SWT.NONE);
		parallelThreads.setText("\t\t并行线程数：");
		creatSpinner(group, threadCount, "并行线程数");

		// 重试次数
		retryCount = new Label(group, SWT.NONE);
		retryCount.setText("\t\t重试次数：");
		creatSpinner(group, retry, "重试次数");

		// 超时
		timeOutLabel = new Label(group, SWT.NONE);
		timeOutLabel.setText("\t\t超时时间：");
		creatSpinnerUsefull(group, timeOut, "超时时间",200,1000);
		setControl(group);
	}
	/**
	 * 创建下拉选项调试器
	 * 
	 * @param group
	 */

	public void creatSpinnerUsefull(Composite group, int init, final String key,int min,int max) {

		final Spinner spinner = new Spinner(group, SWT.BORDER);
		spinner.setValues(init, min, max, 0, 100, 10);// 初始值,最小值,最大值,小數點,點擊增加的多少,。。
		spinner.setLayoutData(new GridData(120, SWT.DEFAULT));
		spinner.addModifyListener(new ModifyListener() {
			@SuppressWarnings("unused")
			public void modifyText(ModifyEvent e) {
				String string = ((Spinner) e.getSource()).getText();
				String message = null;
				try {
					int value = Integer.parseInt(string);
					// 把数据统一交给wizard里面的通用存储器DialogSettings来存值储
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
				if (message != null) {// 如果有错误消息
					spinner.setForeground(COLOR_SYSTEM_RED);// 下面代码可以省略
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
	 * 创建下拉选项调试器
	 * 
	 * @param group
	 */

	public void creatSpinner(Composite group, int init, final String key) {

		final Spinner spinner = new Spinner(group, SWT.BORDER);
		spinner.setValues(init, 0, 99, 0, 1, 10);// 初始值,最小值,最大值,小數點,點擊增加的多少,。。
		spinner.setLayoutData(new GridData(120, SWT.DEFAULT));
		spinner.addModifyListener(new ModifyListener() {
			@SuppressWarnings("unused")
			public void modifyText(ModifyEvent e) {
				String string = ((Spinner) e.getSource()).getText();
				String message = null;
				try {
					int value = Integer.parseInt(string);
					// 把数据统一交给wizard里面的通用存储器DialogSettings来存值储
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
				if (message != null) {// 如果有错误消息
					spinner.setForeground(COLOR_SYSTEM_RED);// 下面代码可以省略
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
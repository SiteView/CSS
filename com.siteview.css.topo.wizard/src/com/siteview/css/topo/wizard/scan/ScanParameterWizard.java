package com.xinnan.patientims.dialogs;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * 扫描参数向导
 * @author zhangxinnan
 * 
 */
public class ScanParameterWizard extends WizardPage {
	private Label parallelThreads;
	private Label retryCount;
	private Label timeOut;
	/** 红色 */
	private Color COLOR_SYSTEM_RED = null;
	private Display display = null;

	protected ScanParameterWizard() {
		super("Some wizard Page");
		setTitle("扫描参数 -->" + "扫描范围 -->" + "排除范围 -->" + "共同体设置 -->" + "扫描种子");
		setMessage("扫描配置包括扫描参数和扫描范围两部分");
	}

	public void createControl(Composite parent) {
		// 设置颜色
		display = parent.getDisplay();
		COLOR_SYSTEM_RED = display.getSystemColor(SWT.COLOR_RED);
		// 群组
		Group group = new Group(parent, SWT.NONE);
		group.setText("扫描参数");
		group.setLayout(new GridLayout(2, true));
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);

		// 收索深度
		Label searchDepth = new Label(group, SWT.NONE);
		searchDepth.setText("\t\t收索深度：");
		GridData layoutD = new GridData();
		layoutD.verticalIndent = 15;
		creatSpinner(group);// 下拉调整

		// 并行线程数
		parallelThreads = new Label(group, SWT.NONE);
		parallelThreads.setText("\t\t并行线程数：");
		creatSpinner(group);

		// 重试次数
		retryCount = new Label(group, SWT.NONE);
		retryCount.setText("\t\t重试次数：");
		creatSpinner(group);

		// 超时
		timeOut = new Label(group, SWT.NONE);
		timeOut.setText("\t\t超时时间：");
		creatSpinner(group);
		/*
		 * 简单示例 Spinner spinner4 = new Spinner (group, SWT.BORDER);
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
	 * 创建下拉选项调试器
	 * 
	 * @param group
	 */
	public void creatSpinner(Composite group) {
		final Spinner spinner = new Spinner(group, SWT.BORDER);
		spinner.setValues(5, 0, 100, 0, 1, 10);// 初始值,最小值,最大值,小數點,點擊增加的多少,。。
		spinner.setLayoutData(new GridData(120, SWT.DEFAULT));
		final ToolTip toolTip = new ToolTip(new Shell(), SWT.BALLOON
				| SWT.ICON_WARNING);
		spinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String string = spinner.getText();
				// String string2 =
				// String.valueOf(spinner.getSelection());//test
				String message = null;
				try {
					int value = Integer.parseInt(string);
					System.out.println(value);
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
					toolTip.setLocation(display.map(new Shell(), null, rect.x
							+ pt.x, rect.y + rect.height));
					toolTip.setMessage(message);
					toolTip.setVisible(true);
				} else {
					toolTip.setVisible(false);
					spinner.setForeground(null);
				}
			}
		});
	}
}
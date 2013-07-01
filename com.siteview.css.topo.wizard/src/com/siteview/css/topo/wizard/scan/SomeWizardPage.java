package com.siteview.css.topo.wizard.scan;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.*;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * 界面1
 * @author zhangxinnan
 *
 */
public class SomeWizardPage extends WizardPage {
	private Label parallelThreads;
	private Label retryCount;
	private Label timeOut;
    /** 红色*/
    private Color COLOR_SYSTEM_RED = null;
	private Display display = null;
	protected SomeWizardPage() {
		super("Some wizard Page");
		setTitle("WizardPage Title");
		setMessage("WizardPage Message");
	}
	
public void createControl(Composite parent) {
    //设置颜色
    display = parent.getDisplay();
    COLOR_SYSTEM_RED=display.getSystemColor(SWT.COLOR_RED);
//	Label label = new Label(parent, SWT.NONE);
//		setControl(label);
    //群组
	Group group = new Group(parent, SWT.NONE);
	group.setText("\t\t扫描参数");
	group.setLayout(new GridLayout(2, true));
	GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
	layoutData.verticalIndent = 15;
	group.setLayoutData(layoutData);
	
	//收索深度
	Label retryCount2 = new Label(group, SWT.NONE);
	retryCount2.setText("\t\t收索深度：");
	GridData layoutD = new GridData();
	layoutD.verticalIndent = 15;
	//retryCount.setLayoutData(layoutD);
	
	final Spinner spinner = new Spinner (group, SWT.BORDER);
	spinner.setValues(0, 0, 100, 0, 1, 10);
	spinner.setLayoutData(new GridData(120, SWT.DEFAULT));
	final ToolTip toolTip = new ToolTip(new Shell(), SWT.BALLOON | SWT.ICON_WARNING);
	spinner.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			String string = spinner.getText();
			String message = null;
			try {
				int value = Integer.parseInt(string);
				int maximum = spinner.getMaximum();
				int minimum = spinner.getMinimum();
				if (value > maximum) {
					message = "Current input is greater than the maximum limit ("+maximum+")";
				} else if (value < minimum) {
					message = "Current input is less than the minimum limit ("+minimum+")";
				}
			} catch (Exception ex) {
				message = "Current input is not numeric";
			}
			if (message != null) {
				spinner.setForeground(display.getSystemColor(SWT.COLOR_RED));
				Rectangle rect = spinner.getBounds();
				GC gc = new GC(spinner);
				Point pt = gc.textExtent(string);
				gc.dispose();
				toolTip.setLocation(display.map(new Shell(), null, rect.x + pt.x, rect.y + rect.height));
				toolTip.setMessage(message);
				toolTip.setVisible(true);
			} else {
				toolTip.setVisible(false);
				spinner.setForeground(null);
			}
		}
	});
	
	//并行线程数
	parallelThreads = new Label(group, SWT.NONE);
	parallelThreads.setText("\t\t并行线程数：");
	//parallelThreads.setLayoutData(layoutD);	
	
	final Spinner spinner2 = new Spinner (group, SWT.BORDER);
	spinner2.setValues(0, 0, 100, 0, 1, 10);
	spinner2.setLayoutData(new GridData(120, SWT.DEFAULT));
	final ToolTip toolTip2 = new ToolTip(new Shell(), SWT.BALLOON | SWT.ICON_WARNING);
	spinner2.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			String string = spinner2.getText();
			String message = null;
			try {
				int value = Integer.parseInt(string);
				int maximum = spinner2.getMaximum();
				int minimum = spinner2.getMinimum();
				if (value > maximum) {
					message = "Current input is greater than the maximum limit ("+maximum+")";
				} else if (value < minimum) {
					message = "Current input is less than the minimum limit ("+minimum+")";
				}
			} catch (Exception ex) {
				message = "Current input is not numeric";
			}
			if (message != null) {
				spinner2.setForeground(display.getSystemColor(SWT.COLOR_RED));
				Rectangle rect = spinner2.getBounds();
				GC gc = new GC(spinner2);
				Point pt = gc.textExtent(string);
				gc.dispose();
				toolTip2.setLocation(display.map(new Shell(), null, rect.x + pt.x, rect.y + rect.height));
				toolTip2.setMessage(message);
				toolTip2.setVisible(true);
			} else {
				toolTip2.setVisible(false);
				spinner2.setForeground(null);
			}
		}
	});
	
	//重试次数
	retryCount = new Label(group, SWT.NONE);
	retryCount.setText("\t\t重试次数：");
	//retryCount.setLayoutData(layoutD);
	
	final Spinner spinner3 = new Spinner (group, SWT.BORDER);
	spinner3.setValues(0, 0, 100, 0, 1, 10);
	spinner3.setLayoutData(new GridData(120, SWT.DEFAULT));
	final ToolTip toolTip3 = new ToolTip(new Shell(), SWT.BALLOON | SWT.ICON_WARNING);
	spinner3.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			String string = spinner3.getText();
			String message = null;
			try {
				int value = Integer.parseInt(string);
				int maximum = spinner3.getMaximum();
				int minimum = spinner3.getMinimum();
				if (value > maximum) {
					message = "Current input is greater than the maximum limit ("+maximum+")";
				} else if (value < minimum) {
					message = "Current input is less than the minimum limit ("+minimum+")";
				}
			} catch (Exception ex) {
				message = "Current input is not numeric";
			}
			if (message != null) {
				spinner3.setForeground(display.getSystemColor(SWT.COLOR_RED));
				Rectangle rect = spinner3.getBounds();
				GC gc = new GC(spinner3);
				Point pt = gc.textExtent(string);
				gc.dispose();
				toolTip3.setLocation(display.map(new Shell(), null, rect.x + pt.x, rect.y + rect.height));
				toolTip3.setMessage(message);
				toolTip3.setVisible(true);
			} else {
				toolTip3.setVisible(false);
				spinner3.setForeground(null);
			}
		}
	});		
	
	//超时
	timeOut = new Label(group, SWT.NONE);
	timeOut.setText("\t\t超时时间：");
	//timeOut.setLayoutData(layoutD);
	
	final Spinner spinner4 = new Spinner (group, SWT.BORDER);
	spinner4.setValues(0, 0, 100, 0, 1, 10);
	spinner4.setLayoutData(new GridData(120, SWT.DEFAULT));
	final ToolTip toolTip4 = new ToolTip(new Shell(), SWT.BALLOON | SWT.ICON_WARNING);
	spinner4.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			String string = spinner4.getText();
			String message = null;
			try {
				int value = Integer.parseInt(string);
				int maximum = spinner4.getMaximum();
				int minimum = spinner4.getMinimum();
				if (value > maximum) {
					message = "Current input is greater than the maximum limit ("+maximum+")";
				} else if (value < minimum) {
					message = "Current input is less than the minimum limit ("+minimum+")";
				}
			} catch (Exception ex) {
				message = "Current input is not numeric";
			}
			if (message != null) {
				spinner4.setForeground(display.getSystemColor(SWT.COLOR_RED));
				Rectangle rect = spinner4.getBounds();
				GC gc = new GC(spinner4);
				Point pt = gc.textExtent(string);
				gc.dispose();
				toolTip4.setLocation(display.map(new Shell(), null, rect.x + pt.x, rect.y + rect.height));
				toolTip4.setMessage(message);
				toolTip4.setVisible(true);
			} else {
				toolTip4.setVisible(false);
				spinner4.setForeground(null);
			}
		}
	});
/*简单示例
 * 	Spinner spinner4 = new Spinner (group, SWT.BORDER);
	spinner4.setMinimum(0);
	spinner4.setMaximum(1000);
	spinner4.setSelection(5);
	spinner4.setIncrement(1);
	spinner4.setPageIncrement(100);
	spinner4.setLayoutData(new GridData(120, SWT.DEFAULT));
	Rectangle clientArea4 = group.getClientArea();
	spinner4.setLocation(clientArea4.x, clientArea4.y);
	spinner4.pack();*/
	
	setControl(group);
	}
}
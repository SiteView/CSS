package com.siteview.css.topo.wizard.scan;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * 界面3
 * @author zhangxinnan
 *
 */
public class SomeWizardPage3 extends WizardPage {
	private Label parallelThreads;
	private Label retryCount;
	private Label timeOut;
	protected SomeWizardPage3() {
		super("Some wizard Page");
		setTitle("WizardPage Title");
		setMessage("WizardPage Message");
	}
	
public void createControl(Composite parent) {
	Group group = new Group(parent, SWT.NONE);
	group.setText("\t\t排除范围");
	group.setLayout(new GridLayout(2, true));
	GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
	layoutData.verticalIndent = 15;
	group.setLayoutData(layoutData);
	
	
	Label retryCount2 = new Label(group, SWT.NONE);
	retryCount2.setText("\t\t收索深度：");
	GridData layoutD = new GridData();
	layoutD.verticalIndent = 15;
	//retryCount.setLayoutData(layoutD);
	
	Spinner spinner = new Spinner (group, SWT.BORDER);
	spinner.setMinimum(0);
	spinner.setMaximum(1000);
	spinner.setSelection(5);
	spinner.setIncrement(1);
	spinner.setPageIncrement(100);
	spinner.setSize(200, 100);
	Rectangle clientArea = group.getClientArea();
	spinner.setLocation(clientArea.x, clientArea.y);
	spinner.pack();
	
	parallelThreads = new Label(group, SWT.NONE);
	parallelThreads.setText("\t\t并行线程数：");
	//parallelThreads.setLayoutData(layoutD);	
	
	Spinner spinner2 = new Spinner (group, SWT.BORDER);
	spinner2.setMinimum(0);
	spinner2.setMaximum(1000);
	spinner2.setSelection(5);
	spinner2.setIncrement(1);
	spinner2.setPageIncrement(100);
	spinner2.setSize(200, 100);
	Rectangle clientArea2 = group.getClientArea();
	spinner2.setLocation(clientArea2.x, clientArea2.y);
	spinner2.pack();
	
	//重试次数
	retryCount = new Label(group, SWT.NONE);
	retryCount.setText("\t\t重试次数：");
	//retryCount.setLayoutData(layoutD);
	
	Spinner spinner3 = new Spinner (group, SWT.BORDER);
	spinner3.setMinimum(0);
	spinner3.setMaximum(1000);
	spinner3.setSelection(5);
	spinner3.setIncrement(1);
	spinner3.setPageIncrement(100);
	spinner3.setSize(200, 100);
	Rectangle clientArea3 = group.getClientArea();
	spinner3.setLocation(clientArea3.x, clientArea3.y);
	spinner3.pack();		
	
//	//超时
	timeOut = new Label(group, SWT.NONE);
	timeOut.setText("\t\t超时时间：");
	//timeOut.setLayoutData(layoutD);
	
	Spinner spinner4 = new Spinner (group, SWT.BORDER);
	spinner4.setMinimum(0);
	spinner4.setMaximum(1000);
	spinner4.setSelection(5);
	spinner4.setIncrement(1);
	spinner4.setPageIncrement(100);
	spinner4.setSize(200, 100);
	Rectangle clientArea4 = group.getClientArea();
	spinner4.setLocation(clientArea4.x, clientArea4.y);
	spinner4.pack();
	
	setControl(group);
	}
}
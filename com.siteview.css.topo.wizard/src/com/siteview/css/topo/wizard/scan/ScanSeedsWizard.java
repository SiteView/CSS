package com.xinnan.patientims.dialogs;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * 扫描种子向导
 * 
 * @author zhangxinnan
 * 
 */
public class ScanSeedsWizard extends WizardPage {
	private Label parallelThreads;
	private Label retryCount;
	private Label timeOut;

	protected ScanSeedsWizard() {
		super("Some wizard Page");
		setTitle("扫描参数 -->" + "扫描范围 -->" + "排除范围 -->" + "共同体设置 -->" + "扫描种子");
		setMessage("扫描的种子地址一般用核心设备的地址。");
	}

	public void createControl(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText("\t\t扫描种子");
		group.setLayout(new GridLayout(2, true));
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);

		Label retryCount2 = new Label(group, SWT.NONE);
		retryCount2.setText("\t\t收索深度：");
		GridData layoutD = new GridData();
		layoutD.verticalIndent = 15;
		// retryCount.setLayoutData(layoutD);

		setControl(group);
	}
}
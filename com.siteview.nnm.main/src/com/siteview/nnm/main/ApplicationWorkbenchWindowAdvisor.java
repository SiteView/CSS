package com.siteview.nnm.main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.siteview.css.topo.wizard.scan.ScanDialog;
import com.siteview.css.topo.wizard.scan.SomeWizard;
import com.siteview.itsm.nnm.scan.core.StartScan;


public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(true);
		configurer.setShowPerspectiveBar(true);
		configurer.setShowStatusLine(true);
		configurer.setTitle("NNM");
		
	}
	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		//设置打开时最大化窗口
		getWindowConfigurer().getWindow().getShell().setMaximized(true);
		StartScan scan = StartScan.getInstance(getWindowConfigurer().getWindow().getShell());
		if(scan.theFirstOpen()){
			MessageBox initDialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION);
			initDialog.setMessage("第一次使用本软件请先配置扫描相关参数");
			initDialog.setText("初始界面");
			initDialog.open();
			ScanDialog dialog = new ScanDialog(Display.getCurrent().getActiveShell(),new SomeWizard());
			dialog.open();
		}
		if(!scan.scaned)
			scan.scanTopo();
	}
}

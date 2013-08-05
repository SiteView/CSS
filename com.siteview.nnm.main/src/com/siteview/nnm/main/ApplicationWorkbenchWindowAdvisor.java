package com.siteview.nnm.main;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.siteview.nnm.main.utils.StartScan;

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
		if(!scan.scaned)
			StartScan.getInstance(getWindowConfigurer().getWindow().getShell()).scanTopo();
	}
}

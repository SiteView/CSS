package com.siteview.css.topo.wizard.scan;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * 点击扫描时打开dialog
 * 
 * @author zhangxinnan
 * 
 */
public class ScanDialog extends TitleAreaDialog {

	public ScanDialog(Shell parentShell) {
		super(parentShell);
		WizardDialog wizardDialog = new WizardDialog(parentShell,
				new SomeWizard());
		wizardDialog.open();
	}

}

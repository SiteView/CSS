package com.xinnan.patientims.dialogs;

import org.eclipse.jface.wizard.Wizard;

/**
 * ɨ����
 * 
 * @author zhangxinnan
 * 
 */
public class SomeWizard extends Wizard {

	public SomeWizard() {
		setWindowTitle("ɨ��");
		addPage(new ScanParameterWizard());
		addPage(new ScanScopeWizard());
		addPage(new OutScopeWizard());
		addPage(new CommunitySetupWizard());
		addPage(new ScanSeedsWizard());
	}

	public boolean performFinish() {
		return true;
	}
}

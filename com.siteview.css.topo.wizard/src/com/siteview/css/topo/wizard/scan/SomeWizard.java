package com.xinnan.patientims.dialogs;

import org.eclipse.jface.wizard.Wizard;

/**
 * …®√ËœÚµº
 * 
 * @author zhangxinnan
 * 
 */
public class SomeWizard extends Wizard {

	public SomeWizard() {
		setWindowTitle("…®√Ë");
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

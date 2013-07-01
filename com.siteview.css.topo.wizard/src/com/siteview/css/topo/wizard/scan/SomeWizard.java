package com.siteview.css.topo.wizard.scan;

import org.eclipse.jface.wizard.Wizard;
/**
 * …®√ËœÚµº
 * @author zhangxinnan
 *
 */
public class SomeWizard extends Wizard {
	
	public SomeWizard() {
		setWindowTitle("Window Title");
		addPage(new SomeWizardPage());
		addPage(new SomeWizardPage2());
		addPage(new SomeWizardPage3());
		addPage(new SomeWizardPage4());
		addPage(new SomeWizardPage5());
	}
	
	public boolean performFinish() {
		return true;
	}
}

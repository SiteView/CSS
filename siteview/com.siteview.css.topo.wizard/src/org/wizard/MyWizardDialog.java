package org.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class MyWizardDialog extends WizardDialog {

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        // ���windowTitle�ᱻmyWizard.setWindowTitle("���빤��2")����
        newShell.setText("���빤��1");
        newShell.setSize(400, 300);
        newShell.setMinimumSize(300, 270);
    }

    public MyWizardDialog(Shell parentShell, IWizard newWizard) {
        super(parentShell, newWizard);
        ((Wizard) newWizard).setNeedsProgressMonitor(true);
    }
}
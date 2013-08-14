package org.wizard;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
public class App extends ApplicationWindow {

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("应用程序");
        shell.setSize(400, 300);
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite contents = (Composite) super.createContents(parent);
        contents.setLayout(new GridLayout(1, false));

        Button button = new Button(contents, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                false));
        button.setText("click");

        button.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                Wizard myWizard = new MyWizard();
                WizardDialog wdialog = new MyWizardDialog(App.this.getShell(),
                        myWizard);
                myWizard.setWindowTitle("导入工程2");
                wdialog.open();
            }
        });

        return contents;
    }

    public App() {
        super(null);
    }

    public void run() {
        this.setBlockOnOpen(true);
        this.open();

        // 注意，这里调用dispose很重要，因为open中没有进行dispose操作。getCurrent是OK的。
        Display.getCurrent().dispose();
    }

    public static void main(String[] args) {
        new App().run();
    }

}
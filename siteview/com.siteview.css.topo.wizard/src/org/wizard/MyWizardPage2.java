package org.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MyWizardPage2 extends WizardPage {
    private String city = "";
    private String address = "";

    protected MyWizardPage2() {
        super("MyWizardPage2");
    }

    @Override
    public void createControl(Composite parent) {
        // 在生成UI之前，先设为未完成
        // this.setPageComplete(false);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(2, false));

        Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                false));
        label.setText("城  市:");

        Text text = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                city = ((Text) e.getSource()).getText();
                MyWizardPage2.this.getWizard().getDialogSettings().put("城  市",
                        ((Text) e.getSource()).getText());
                // 因为模型改变了，所以要及时更改界面
                MyWizardPage2.this.getContainer().updateButtons();
            }
        });

        label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                false));
        label.setText("地  址:");

        text = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                address = ((Text) e.getSource()).getText();
                MyWizardPage2.this.getWizard().getDialogSettings().put("地  址",
                        ((Text) e.getSource()).getText());
                // 因为模型改变了，所以要及时更改界面
                MyWizardPage2.this.getContainer().updateButtons();
            }
        });

        this.setTitle("导入TOS工程");
        this.setMessage("请输入用户名和密码，进行工程导入操作");

        this.setControl(composite);
    }

    @Override
    public boolean isPageComplete() {
        // TODO Auto-generated method stub
        return city.length() > 0 && address.length() > 0;
    }

}
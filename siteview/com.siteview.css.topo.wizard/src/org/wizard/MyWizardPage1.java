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

public class MyWizardPage1 extends WizardPage {
    private String username = "";
    private String password = "";

    protected MyWizardPage1() {
        super("MyWizardPage1");
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
        label.setText("用户名:");

        Text text = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                // 把数据存在自己手里，对外提供get API
                username = ((Text) e.getSource()).getText();
                // 把数据统一交给wizard里面的通用存储器DialogSettings来存值储
                MyWizardPage1.this.getWizard().getDialogSettings().put("用户名",
                        ((Text) e.getSource()).getText());
                // 因为模型改变了，所以要及时更改界面
                MyWizardPage1.this.getContainer().updateButtons();
            }
        });

        label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                false));
        label.setText("密  码:");

        text = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                password = ((Text) e.getSource()).getText();
                System.out.println("密码="+password);
                MyWizardPage1.this.getWizard().getDialogSettings().put("密  码",
                        ((Text) e.getSource()).getText());
                // 因为模型改变了，所以要及时更改界面
                MyWizardPage1.this.getContainer().updateButtons();
            }
        });

        this.setTitle("导入TOS工程");
        this.setMessage("请输入用户名和密码，进行工程导入操作");

        this.setControl(composite);
    }

    @Override
    // 重写这个方法，不再使用以前的flag
    public boolean isPageComplete() {
        return username.length() > 0 && password.length() > 0;
    }

}
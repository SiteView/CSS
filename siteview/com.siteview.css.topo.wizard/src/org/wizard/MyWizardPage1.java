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
        // ������UI֮ǰ������Ϊδ���
        // this.setPageComplete(false);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(2, false));

        Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                false));
        label.setText("�û���:");

        Text text = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                // �����ݴ����Լ���������ṩget API
                username = ((Text) e.getSource()).getText();
                // ������ͳһ����wizard�����ͨ�ô洢��DialogSettings����ֵ��
                MyWizardPage1.this.getWizard().getDialogSettings().put("�û���",
                        ((Text) e.getSource()).getText());
                // ��Ϊģ�͸ı��ˣ�����Ҫ��ʱ���Ľ���
                MyWizardPage1.this.getContainer().updateButtons();
            }
        });

        label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                false));
        label.setText("��  ��:");

        text = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                password = ((Text) e.getSource()).getText();
                System.out.println("����="+password);
                MyWizardPage1.this.getWizard().getDialogSettings().put("��  ��",
                        ((Text) e.getSource()).getText());
                // ��Ϊģ�͸ı��ˣ�����Ҫ��ʱ���Ľ���
                MyWizardPage1.this.getContainer().updateButtons();
            }
        });

        this.setTitle("����TOS����");
        this.setMessage("�������û��������룬���й��̵������");

        this.setControl(composite);
    }

    @Override
    // ��д�������������ʹ����ǰ��flag
    public boolean isPageComplete() {
        return username.length() > 0 && password.length() > 0;
    }

}
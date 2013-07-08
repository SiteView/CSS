package org.csstudio.ui.menu.app;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

public class PreferencePage1 extends PreferencePage implements
		IWorkbenchPreferencePage, ModifyListener {
	private Label userLabel;
	private Label passWordLabel;
	private Text userName;
	private Text passWord;

	public PreferencePage1(String title, ImageDescriptor image) {
		super(title, image);
	}

	public PreferencePage1() {
		setDescription("对用户的登录密码进行修改首选项页");

	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		// 设置面板采用网格式布局
		composite.setLayout(new GridLayout(2, false));
		Group group = new Group(composite, SWT.NONE);
		group.setText("修改用户密码");
		group.setLayout(new GridLayout(2, false));
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);
		userLabel = new Label(group, SWT.NONE);
		userLabel.setText("用户名：");
		GridData layoutD = new GridData();
		layoutD.verticalIndent = 15;
		userLabel.setLayoutData(layoutD);
		userName = new Text(group, SWT.BORDER);
		userName.setLayoutData(layoutData);
		passWordLabel = new Label(group, SWT.NONE);
		passWordLabel.setText("密  码：");
		passWordLabel.setLayoutData(layoutD);
		passWord = new Text(group, SWT.BORDER);
		passWord.setEchoChar('*');
		passWord.setLayoutData(layoutData);
		// 添加监听器
		userName.addModifyListener(this);
		passWord.addModifyListener(this);
		// 将首选项的"恢复"和"应用"按钮隐藏
		noDefaultAndApplyButton();
		setValid(false);
		return composite;
	}

	// 设置确定按钮
	public boolean performOk() {
		// 修改密码操作
		//DataBaseOperate.UpdateUserPassWordInfor(userName, passWord);
		return true;
	}

	// 监听文本输入的合法性
	public void modifyText(ModifyEvent e) {
		String un = userName.getText();
		String pw = passWord.getText();
		if (un == null || un.equals("")) {
			setErrorMessage("用户名不能为空");
			setValid(false);
		} else if (pw == null || pw.equals("")) {
			setErrorMessage("密码不能为空");
			setValid(false);
		} else
			setValid(true);

	}

	public void init(IWorkbench workbench) {

	}
}

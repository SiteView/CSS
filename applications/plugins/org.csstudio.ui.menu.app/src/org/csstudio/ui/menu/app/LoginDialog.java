package org.csstudio.ui.menu.app;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class LoginDialog extends TitleAreaDialog {

	private Text userName;
	private Text passWord;
	//private Doctor doctor;

	public LoginDialog(Shell parentshell) {
		super(parentshell);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// 设置对话框标题
		newShell.setText("用户登录");
		// 设置对话大小
		newShell.setSize(500, 200);
		// 设置对话框位置
		newShell.setLocation(260, 260);
	}

	// 建立对话框上的标题及信息提示
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		// 设置标题
		setTitle("添加登录信息");
		// 设置信息提示
		setMessage("注意文本框信息不能为空", IMessageProvider.INFORMATION);
		return contents;
	}

	// 设置对话框区域内容
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(layoutData);
		new Label(composite, SWT.NONE).setText("用户名：");
		userName = new Text(composite, SWT.BORDER);
		userName.setLayoutData(layoutData);
		new Label(composite, SWT.NONE).setText("密  码：");
		passWord = new Text(composite, SWT.BORDER);
		passWord.setEchoChar('*');
		passWord.setLayoutData(layoutData);
		return composite;
	}

	// 单击按钮执行的操作
	protected void buttonPressed(int buttonId) {

		// 单击OK按钮执行以下操作
		if (IDialogConstants.OK_ID == buttonId) {
			// 验证输入信息的合法性，
			if (!checkValue())
				return;
			//getValue(doctor);
		}
		okPressed();
		// 单击Cancel按钮执行以下操作
		if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	// 对输入数据进行验证，不合法返回false
	private boolean checkValue() {
		String un = userName.getText();
		String pw = passWord.getText();

		if (pw == null || pw.equals("") || un == null || pw.equals("")) {
			setErrorMessage("用户名和密码不能为空");
			return false;
		}
		return true;
	}

//	// 获得对话框中输入的信息，将信息植入Doctor类属性中
//	private void getValue(Doctor doctor) {
//		String un = userName.getText();
//		doctor.setUserName(un);
//		String pw = passWord.getText();
//		doctor.setPassWord(pw);
//	}
//
//	public Doctor getDoctor() {
//		return doctor;
//	}
//
//	public void setDoctor(Doctor doctor) {
//		this.doctor = doctor;
//	}

	/**
	 * 用SWT.RESIZE、SWT.MAX、SWT.MIN分别设置窗口为可以变大小、窗口可最 大化、最小化
	 */

	/**
	 * protected int getShellStyle() { return super.getShellStyle() | SWT.RESIZE |
	 * SWT.MAX | SWT.MIN; }
	 */
	/***************************************************************************
	 * 建立按钮——确认、取消按钮 /
	 */
	/*
	 * protected void createButtonsForButtonBar(Composite parent) {
	 * createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
	 * true); createButton(parent, IDialogConstants.CANCEL_ID,
	 * IDialogConstants.CANCEL_LABEL, true); }
	 */
	/**
	 * 设置最大化最小化
	 */
	protected int getShellStyle(){
		return super.getShellStyle()|SWT.RESIZE|SWT.MAX|SWT.MIN;
	}
}

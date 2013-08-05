package com.siteview.nnm.main.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;

public class BaseUtils {
	public static void showError(Composite parent,String title,String conent){
		MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_ERROR);
		dialog.setMessage(conent);
		dialog.setText(title);
		dialog.open();
	}
}

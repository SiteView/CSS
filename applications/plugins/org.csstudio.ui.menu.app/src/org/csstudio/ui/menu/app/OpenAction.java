package org.csstudio.ui.menu.app;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.*;

public class OpenAction extends Action{
	private IWorkbenchWindow window;
	public OpenAction(IWorkbenchWindow window){
		this.window = window;
		//设置菜单项文本及快捷键绑定
		this.setText("&Navigator@Ctrl+Alt+N");
		//工具条栏上提示性标签
		setToolTipText("Open View Action");
		//设置菜单项及工具栏按钮图标
		ImageDescriptor imgDes = WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_LCL_PIN_VIEW);
		this.setImageDescriptor(imgDes);
	}
	public void run(){
		MessageDialog.openInformation(window.getShell(), "Myrcp Plug-in", "Hello,Eclipse world");
	}
	
	   //判断用户名和密码是否已经存在。
		public boolean preOpen() {
			// 调用登录对话框
			LoginDialog ld = new LoginDialog(null);
			//定义Dcctor对象
//	    	Doctor doctor = new Doctor();
//			ld.setDoctor(doctor);
			//打开登录对话框
			if (ld.open() == IDialogConstants.OK_ID) {
				//到数据库中去验证信息
				//doctor = DataBaseOperate.getLoginInfor(doctor);
//				if (doctor == null) {
//					MessageDialog.openInformation(null, null, "请正确填写信息！！！！");
//					return false;
//				}
//				if (doctor != null) {
//					return true;
//				}
			}
			return false;
		}
}

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
		//���ò˵����ı�����ݼ���
		this.setText("&Navigator@Ctrl+Alt+N");
		//������������ʾ�Ա�ǩ
		setToolTipText("Open View Action");
		//���ò˵����������ťͼ��
		ImageDescriptor imgDes = WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_LCL_PIN_VIEW);
		this.setImageDescriptor(imgDes);
	}
	public void run(){
		MessageDialog.openInformation(window.getShell(), "Myrcp Plug-in", "Hello,Eclipse world");
	}
	
	   //�ж��û����������Ƿ��Ѿ����ڡ�
		public boolean preOpen() {
			// ���õ�¼�Ի���
			LoginDialog ld = new LoginDialog(null);
			//����Dcctor����
//	    	Doctor doctor = new Doctor();
//			ld.setDoctor(doctor);
			//�򿪵�¼�Ի���
			if (ld.open() == IDialogConstants.OK_ID) {
				//�����ݿ���ȥ��֤��Ϣ
				//doctor = DataBaseOperate.getLoginInfor(doctor);
//				if (doctor == null) {
//					MessageDialog.openInformation(null, null, "����ȷ��д��Ϣ��������");
//					return false;
//				}
//				if (doctor != null) {
//					return true;
//				}
			}
			return false;
		}
}

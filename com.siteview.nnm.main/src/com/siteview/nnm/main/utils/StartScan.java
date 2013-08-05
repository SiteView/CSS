package com.siteview.nnm.main.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.siteview.css.topo.common.TopoData;
import com.siteview.css.topo.wizard.common.GlobalData;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.scan.NetScan;
import com.siteview.snmp.util.IoUtils;
/**
 * ��ʾtopoͼ��
 * @author haiming.wang
 *
 */
public class StartScan {
	
	@SuppressWarnings("unused")
	public boolean scaned = false;
	
	//����ɨ�蹤��
	public NetScan scan;

	private Composite parent;
	private static StartScan instance ;
	private CountDownLatch scanCound;
	
	private StartScan(Composite parent){
		this.parent = parent;
	}
	public static synchronized StartScan getInstance(Composite parent){
		if(instance == null){
			instance = new StartScan(parent);
		}
		return instance;
	}
	ScanParam scanParam;
	public void scanTopo(){
		//����û��ӽ���������ɨ������������õĲ�����Ϣɨ��
		if(GlobalData.isConfiged){
			scanParam = GlobalData.scanParam;
		}else{
			//������ϴα����������Ϣɨ��
			scanParam = IoUtils.readScanParam();
		}
		if(scanParam == null || (scanParam.getScan_scales().isEmpty() && scanParam.getScan_seeds().isEmpty())){
			BaseUtils.showError(parent,"ɨ�������ʼ��ʧ��", "��������ɨ�����ӻ���ɨ��IP��Χ��");
			return;
		}
		// ɨ�������
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		scanCound = new CountDownLatch(1);
		try {
			pmd.run(true, false, new WithProgress());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			scanCound.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	class WithProgress implements IRunnableWithProgress{
		@Override
		public void run(IProgressMonitor monitor){
			monitor.beginTask("����ɨ������ṹ..." + "", IProgressMonitor.UNKNOWN);  
			/**
			 * ִ�е�����
			 */
			Map<String, Map<String, String>> special_oid_list = new ConcurrentHashMap<String, Map<String, String>>();
			scan = new NetScan(null, special_oid_list, scanParam);
			try{
				scan.scan();
				monitor.worked(50);
				//���������
				TopoData.isInit = true;
				TopoData.edgeList = scan.getTopo_edge_list();
				TopoData.deviceList = scan.getDevid_list();
				monitor.worked(100);
				scaned = true;
				GlobalData.isConfiged = false;//�������ñ�������Ϊflase,����������Ϊtrueÿ�ε��ɨ�趼����������ɨ��
			}catch (Exception e) {
				TopoData.isInit = false;
				scaned = false;
				BaseUtils.showError(parent,"ɨ��ʧ��", e.getMessage());
			}finally{
				monitor.done();
				scanCound.countDown();
			}
		}
		
	}
}

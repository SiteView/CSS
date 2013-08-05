package com.siteview.itsm.nnm.scan.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.siteview.itsm.nnm.scan.core.snmp.common.ScanParam;
import com.siteview.itsm.nnm.scan.core.snmp.data.GlobalData;
import com.siteview.itsm.nnm.scan.core.snmp.scan.NetScan;
import com.siteview.itsm.nnm.scan.core.snmp.util.IoUtils;
/**
 * 显示topo图型
 * @author haiming.wang
 *
 */
public class StartScan {
	
	@SuppressWarnings("unused")
	public boolean scaned = false;
	
	//拓扑扫描工具
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
		//如果用户从界面配置了扫描参数，以配置的参数信息扫描
		if(GlobalData.isConfiged){
			scanParam = GlobalData.scanParam;
		}else{
			//否则从上次保存的配置信息扫描
			scanParam = IoUtils.readScanParam();
		}
		if(scanParam == null || (scanParam.getScan_scales().isEmpty() && scanParam.getScan_seeds().isEmpty())){
			throw new RuntimeException("必须配置扫描种子或者扫描IP范围！");
		}
		// 扫描进度条
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
			monitor.beginTask("正在扫描网络结构..." + "", IProgressMonitor.UNKNOWN);  
			/**
			 * 执行的任务
			 */
			Map<String, Map<String, String>> special_oid_list = new ConcurrentHashMap<String, Map<String, String>>();
			scan = new NetScan(null, special_oid_list, scanParam);
			try{
				scan.scan();
				monitor.worked(50);
				//缓存边数据
				GlobalData.isInit = true;
				GlobalData.edgeList = scan.getTopo_edge_list();
				GlobalData.deviceList = scan.getDevid_list();
				monitor.worked(100);
				scaned = true;
				GlobalData.isConfiged = false;//将己配置变量设置为flase,如果这个变量为true每次点击扫描都会重新启动扫描
			}catch (Exception e) {
				GlobalData.isInit = false;
				scaned = false;
				throw new RuntimeException(e);
			}finally{
				monitor.done();
				scanCound.countDown();
			}
		}
		
	}
}

package com.siteview.css.topo.wizard.actions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.siteview.css.topo.wizard.common.GlobalData;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.scan.NetScan;
/**
 * ¿ªÊ¼ÍØÆËÉ¨ÃèµÄaction
 * @author haiming.wang
 *
 */
public class StartScanAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		ScanParam scanParam = null;
		if(GlobalData.isConfiged){
			scanParam = GlobalData.scanParam;
		}
//		s.setCommunity_get_dft("public");
//		s.setDepth(5);
//		s.getScan_seeds().add("192.168.0.248");
//		s.getScan_seeds().add("192.168.0.251");
			
		Map<String, Map<String, String>> special_oid_list = new ConcurrentHashMap<String, Map<String, String>>();
		NetScan scan = new NetScan(null, special_oid_list, scanParam);
		Thread thread = new Thread(scan);
		thread.start();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {

	}

}

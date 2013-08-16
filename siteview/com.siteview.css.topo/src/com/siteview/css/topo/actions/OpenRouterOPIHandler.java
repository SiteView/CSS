package com.siteview.css.topo.actions;

import java.io.File;
import java.util.LinkedHashMap;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective.Position;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.siteview.css.topo.figure.RouterFigure;
import com.siteview.css.topo.models.RouterModel;
import com.siteview.itsm.nnm.scan.core.snmp.constants.CommonDef;
import com.siteview.itsm.nnm.scan.core.snmp.data.GlobalData;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;
import com.siteview.itsm.nnm.scan.core.snmp.util.IoUtils;

public class OpenRouterOPIHandler extends AbstractHandler{
	private static final String MACRO_NAME = "probe_pv";
	Shell shell;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection =
			HandlerUtil.getActiveMenuSelection(event);
		final ProcessVariable[] pvs =
			AdapterUtil.convert(selection, ProcessVariable.class);
		
		shell = HandlerUtil.getActiveShell(event);
		
		String ipStr = selection.toString();
		
		//截取查看设备所属的IP
		String ip = ipStr.substring(1, ipStr.length()-1);
		
		String  reg = "((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))";
		
		if (!ip.matches(reg)) {
			showError("获取设备出错", "不支持该设备！");
			return null;
		}

		IDBody device = new IDBody();
		System.out.println(GlobalData.deviceList.size());
		
		device = GlobalData.deviceList.get(ip);
		//通过设备IP查设备信息，看是不是所属路由器设备,是否支持查看面板图
		String type = device.getDevType();
		
		//如果设备类型不是 路由器类型，给出提示信息
		if (!type.equals(CommonDef.ROUTER)&&!type.equals(CommonDef.ROUTE_SWITCH)&&!type.equals(CommonDef.SWITCH)) {
			showError("获取路由器设备出错", "面板图查看只支持路由器设备类型查看！");
			return null;
		}
		
		String sysOid = device.getSysOid();
		RouterModel.SYSOID = sysOid;
		RouterFigure.SYSOID = sysOid;
		
//		String filepath = IoUtils.getPlatformPath()+"opi";
//		File filename = new File(filepath + File.separator + sysOid + ".opi");
		String filepath = IoUtils.getPlatformPath()+"opi" + File.separator + sysOid + ".opi";
		File filename = new File(filepath);
		//如果该路由器设备没有对应的OPI文件，给出提示信息
		if (!filename.exists()) {
			showError("获取设备面板图失败", "没有找到匹配的设备面板图,该设备不支持显示！");
			return null;
		}
		
		IPath probeOPIPath = PreferencesHelper.getProbeOPIPath();
		if(probeOPIPath == null || probeOPIPath.isEmpty()){
			//1.3.6.1.4.1.9.1.516.opi
//			IoUtils.getProductPath() + opi + File.separator + sysOid +".opi"
//			probeOPIPath = ResourceUtil.getPathFromString("platform:/plugin/com.siteview.css.topo/opi/"+sysOid+".opi");
			probeOPIPath = ResourceUtil.getPathFromString(filepath);
		
		}
		
		LinkedHashMap<String, String> macros = new LinkedHashMap<String, String>();
		if(pvs.length >0)
			macros.put(MACRO_NAME, pvs[0].getName());

		int i=0;
		for(ProcessVariable pv : pvs){
			macros.put(MACRO_NAME + "_" + Integer.toString(i), pv.getName()); //$NON-NLS-1$
			i++;
		}

		MacrosInput macrosInput = new MacrosInput(macros, true);
		
		// Errors in here will show in dialog and error log
		RunModeService.runOPIInView(probeOPIPath, null,macrosInput, Position.DETACHED);
		
		return null;
	}
	
	/**
	 * 
	 * @param title
	 * @param conent
	 */
	private void showError(String title,String conent){
		MessageBox dialog = new MessageBox(shell, SWT.ICON_INFORMATION);
		dialog.setMessage(conent);
		dialog.setText(title);
		dialog.open();
	}

}

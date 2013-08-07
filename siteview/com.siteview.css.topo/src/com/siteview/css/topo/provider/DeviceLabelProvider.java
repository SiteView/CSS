package com.siteview.css.topo.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.siteview.css.topo.editparts.ShowDeviceEditor;
import com.siteview.itsm.nnm.scan.core.snmp.data.GlobalData;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Edge;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;

public class DeviceLabelProvider implements ITableLabelProvider {

	private Color systemColor;
	@Override
	public void addListener(ILabelProviderListener listener) {
		systemColor = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Pair<String, IDBody> pair = (Pair<String, IDBody>)element;
		switch (columnIndex) {
		case 0:
			
			return pair.getFirst();//ip
		case 1:
			return pair.getSecond().getSysName();//设备名称
		case 2:
			//如果是点击的PC此列显示连接设备的ip 否则 显示设备型号
			if(ShowDeviceEditor.type == 300016){
				return getEdge(pair.getFirst()).getIp_left();
			}
			return pair.getSecond().getDevModel();
		case 3: //设备类型
			return Utils.formatMac(pair.getSecond().getBaseMac()," ");
		case 4:
			//如果是点击的PC此列显示连接设备的ip 否则 显示设备型号
			if(ShowDeviceEditor.type == 300016){
				return getEdge(pair.getFirst()).getInf_left();
			}
			return pair.getSecond().getDevFactory();
		default:
			break;
		}
		return null;
	}
	
	private Edge getEdge(String rightIp){
		for(Edge edge : GlobalData.edgeList){
			if(edge.getIp_right().equals(rightIp)){
				if(edge.getIp_left().startsWith("DUMB")){
					for(Edge dumpEdge : GlobalData.edgeList){
						if(dumpEdge.getIp_right().equals(edge.getIp_left())){
							return dumpEdge;
						}
					}
				}
				return edge;
			}
		}
		return null;
	}
}

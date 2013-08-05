package com.siteview.css.topo.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.siteview.css.topo.common.TopoData;
import com.siteview.css.topo.editparts.ShowDeviceEditor;
import com.siteview.snmp.constants.CommonDef;
import com.siteview.snmp.model.Pair;
import com.siteview.snmp.pojo.Edge;
import com.siteview.snmp.pojo.IDBody;

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
			return formatMac(pair.getSecond().getBaseMac());
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
	private String formatMac(String mac){
		int start = 0;
		int tmpL = 2;
		StringBuffer sb = new StringBuffer("");
		if(mac.length() == 12){
			while(tmpL <=12){
				sb.append(mac.substring(start,tmpL)).append(" ");
				start = tmpL;
				tmpL += 2;
			}
			return sb.toString();
		}
		return mac;
	}
	private Edge getEdge(String rightIp){
		for(Edge edge : TopoData.edgeList){
			if(edge.getIp_right().equals(rightIp)){
				if(edge.getIp_left().startsWith("DUMB")){
					for(Edge dumpEdge : TopoData.edgeList){
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

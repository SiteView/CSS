package com.siteview.css.topo.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.siteview.snmp.constants.CommonDef;
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
		IDBody body = (IDBody)element;
		switch (columnIndex) {
		case 0:
			
			return body.getSysName();//设备名称
		case 1:
			return body.getSysOid();//设备OID
		case 2:
			return body.getBaseMac();//mac地址
		case 3: //设备类型
			if(body.getDevType().equals(CommonDef.SWITCH)){
				return "二层交换";
			}else if(body.getDevType().equals(CommonDef.ROUTE_SWITCH)){
				return "三层交换";
			}else if(body.getDevType().equals(CommonDef.SERVER)){
				return "服务器设备";
			}else if(body.getDevType().equals(CommonDef.ROUTER)){
				return "路由器";
			}else if(body.getDevType().equals(CommonDef.FIREWALL)){
				return "防火墙";
			}else if(body.getDevType().equals(CommonDef.PC)){
				return "pc";
			}else{
				return "其它";
			}
		default:
			break;
		}
		return null;
	}

}

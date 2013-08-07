package com.siteview.itsm.nnm.common.editor.ipResourceMgr;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;

public class IpMacBaseDataLabelProvider implements ITableLabelProvider{

	@Override
	public void addListener(ILabelProviderListener listener) {
		
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
		Pair<String, IDBody> ele = (Pair<String, IDBody>)element;
		switch (columnIndex) {
		case 0:
			return ele.getFirst();
		case 1:
			return Utils.formatMac(ele.getSecond().getBaseMac(), " ");
		default:
			break;
		}
		return null;
	}

}

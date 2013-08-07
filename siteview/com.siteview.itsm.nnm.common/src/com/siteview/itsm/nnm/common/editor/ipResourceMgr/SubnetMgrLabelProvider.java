package com.siteview.itsm.nnm.common.editor.ipResourceMgr;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.siteview.itsm.nnm.scan.core.snmp.constants.CommonDef;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;
import com.siteview.itsm.nnm.scan.core.snmp.util.ScanUtils;
import com.siteview.itsm.nnm.scan.core.snmp.util.Utils;

public class SubnetMgrLabelProvider implements ITableLabelProvider {

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
		Pair<String, IDBody> body = (Pair<String, IDBody>)element;
		switch (columnIndex) {
		case 0:
			
			return body.getFirst();
		case 1:
			return body.getSecond().getSysName();
		case 2:
			return CommonDef.getDeviceTypeByTypeId(body.getSecond().getDevType());
		case 3:
			return Utils.formatMac(body.getSecond().getBaseMac()," ");
		case 4:
			return body.getSecond().getDevModel();
		case 5:
			return body.getSecond().getDevFactory();
		default:
			break;
		}
		return null;
	}

}

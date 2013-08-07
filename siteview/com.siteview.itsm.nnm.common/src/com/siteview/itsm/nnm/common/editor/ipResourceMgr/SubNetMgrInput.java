package com.siteview.itsm.nnm.common.editor.ipResourceMgr;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;

public class SubNetMgrInput implements IEditorInput {

	private String nameMid = "";
	
	private String subnetIp = "";
	
	private Map<String, IDBody> inputData = new HashMap<String, IDBody>();
	
	public Map<String, IDBody> getInputData() {
		return inputData;
	}

	public void setInputData(Map<String, IDBody> inputData) {
		this.inputData = inputData;
	}

	public String getSubnetIp() {
		return subnetIp;
	}

	public void setSubnetIp(String subnetIp) {
		this.subnetIp = subnetIp;
	}

	public String getNameMid() {
		return nameMid;
	}

	public void setNameMid(String nameMid) {
		this.nameMid = nameMid;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return "子网(" + subnetIp + ")IP列表";
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "IP资源管理/子网(" + subnetIp + ")IP列表";
	}

}

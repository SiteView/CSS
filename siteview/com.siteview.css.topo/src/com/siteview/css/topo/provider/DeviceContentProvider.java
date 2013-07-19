package com.siteview.css.topo.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.siteview.snmp.pojo.IDBody;

public class DeviceContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<IDBody> list = (List<IDBody>)inputElement;
		System.out.println(list.size());
		return list.toArray();
	}

}

package com.siteview.itsm.nnm.common.editor.ipResourceMgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;

public class SubnetMgrContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		Map<String,IDBody> bodys = (Map<String,IDBody>)inputElement;
		List<Pair<String, IDBody>> list = new ArrayList<>();
		for(Entry<String, IDBody> entry : bodys.entrySet()){
			if(!entry.getKey().startsWith("DUMB"))
			list.add(new Pair<String,IDBody>(entry.getKey(),entry.getValue()));
		}
		Collections.sort(list);
		return list.toArray();
	}

}

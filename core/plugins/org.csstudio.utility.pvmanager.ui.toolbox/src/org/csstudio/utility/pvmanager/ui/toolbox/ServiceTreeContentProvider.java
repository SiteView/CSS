/**
 * 
 */
package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceMethod;

/**
 * @author shroffk
 * 
 */
public class ServiceTreeContentProvider implements ITreeContentProvider {

    private List<Service> services;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
     * .viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	this.services = (List<Service>) newInput;
    }

    @Override
    public Object[] getElements(Object inputElement) {
	return services.toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
	if (parentElement instanceof Service) {
	    return ((Service) parentElement).getServiceMethods().values()
		    .toArray();
	} else if (parentElement instanceof ServiceMethod) {
	    SortedMap<String, String> map = new TreeMap<String, String>();
	    map.putAll(((ServiceMethod) parentElement)
		    .getArgumentDescriptions());
	    map.putAll(((ServiceMethod) parentElement).getResultDescriptions());
	    return map.entrySet().toArray();
	}
	return null;
    }

    @Override
    public Object getParent(Object element) {
	return null;
    }

    @Override
    public boolean hasChildren(Object element) {
	if (element instanceof Service) {
	    return !((Service) element).getServiceMethods().values().isEmpty();
	} else if (element instanceof ServiceMethod) {
	    return !((ServiceMethod) element).getArgumentDescriptions()
		    .isEmpty()
		    || !((ServiceMethod) element).getResultDescriptions()
			    .isEmpty();
	}
	return false;
    }
}

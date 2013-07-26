package com.siteview.nnm.main.viewer;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import com.siteview.nnm.main.pojo.MenuItem;

public class MenuViewer extends ViewPart {

	public final static String ID = "com.siteview.nnm.main.treeview";

	private TreeViewer tv;

	private MenuItem root;

	@Override
	public void createPartControl(Composite parent) {
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		parent.setLayout(fillLayout);
		tv = new TreeViewer(parent);
		tv.setContentProvider(new TreeContentProvide());
		tv.setLabelProvider(new TreeLabelProvide());
		root = new MenuItem();
		initNode();
		tv.setInput(root);
	}

	public String getProductPath(){
		Location location = Platform.getConfigurationLocation();
		URL url = location.getURL();
		return url.getPath();
	}
	public void initNode() {
		String path = getProductPath() + File.separator + "menu.xml";
		URL xmlUrl = Platform.getBundle("com.siteview.nnm.main").getEntry(
				"menu.xml");
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(path));
			String xpath = "/menu/node";
			List list = doc.selectNodes(xpath);
			Iterator i = list.iterator();
			while (i.hasNext()) {
				Element e = (Element)i.next();
				MenuItem node = new MenuItem();
				buildNode(e,root,node);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 从menu.xml文件组装菜单项
	 * @param e 节点元素对象
	 * @param parent 父节点对象
	 * @param thisNode 当前节点对象
	 */
	public void buildNode(Element e,MenuItem parent,MenuItem thisNode){
		List names = e.selectNodes("./name");
		Element ename = (Element)names.get(0);
		thisNode.setName(ename.getText());
		
		List imageNames = e.selectNodes("./imageName");
		if(imageNames.size() > 0){
			Element imageName = (Element)imageNames.get(0);
			thisNode.setImageName(imageName.getText());
		}
		
		thisNode.setParent(parent);
		parent.addChild(thisNode);
		
		List nodes = e.selectNodes("./node");
		if(nodes.size() > 0){
			Iterator i = nodes.iterator();
			while(i.hasNext()){
				Element elem = (Element)i.next();
				MenuItem node = new MenuItem();
				buildNode(elem,thisNode,node);
			}
		}
		return;
	}
	@Override
	public void setFocus() {

	}
	/**
	 * treeview内容提供者
	 * @author haiming.wang
	 *
	 */
	class TreeContentProvide implements ITreeContentProvider {

		@Override
		public void dispose() {

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return ((MenuItem) inputElement).getChildren().toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return ((MenuItem) parentElement).getChildren().toArray();
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return ((MenuItem) element).hasChild();
		}

	}
	/**
	 * treeviewlalel提供着
	 * @author haiming.wang
	 *
	 */
	class TreeLabelProvide implements ILabelProvider {

		private Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>(
				20);

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
		public Image getImage(Object element) {
			MenuItem item = (MenuItem)element;
			final Bundle bundle = Platform.getBundle("com.siteview.nnm.main");
			URL url = bundle.getEntry("icons/"+item.getImageName());
			return ImageDescriptor.createFromURL(url).createImage();
		}

		@Override
		public String getText(Object element) {
			return ((MenuItem) element).getName();
		}

	}

}

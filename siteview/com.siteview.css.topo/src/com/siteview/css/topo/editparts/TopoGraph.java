package com.siteview.css.topo.editparts;

import java.util.UUID;
import org.csstudio.opibuilder.model.DisplayModel;
import ILOG.Diagrammer.GraphicContainer;
import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.Link;

public class TopoGraph extends GraphicContainer {

	public TopoGraph(DisplayModel diplayModel) {
		super(diplayModel);
	}

	// 把ip地址换成对应的模型
	public void addLink(String mac1, String mac2) {

		GraphicObject obj1 = null;
		GraphicObject obj2 = null;
		for (GraphicObject go : nodes) {// 所有节点
			if (go.isNode()) {
				String sIP = ((TopoNode) go).getId();

				if (mac1.equals(sIP)) {
					obj1 = go;
				}
				if (mac2.equals(sIP)) {
					obj2 = go;
				}
			}
		}

		if (obj1 != null && obj2 != null) {
			//System.out.println("添加了数据" + obj1 + "-" + obj2);
			Link lnkLink = new Link(this, UUID.randomUUID().toString());
			lnkLink.connect(obj1, obj2);
			nodes.add(lnkLink);
		}
	}
}

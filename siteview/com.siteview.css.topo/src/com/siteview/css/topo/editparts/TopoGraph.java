package com.siteview.css.topo.editparts;

import java.util.UUID;
import org.csstudio.opibuilder.model.DisplayModel;
import ILOG.Diagrammer.GraphicContainer;
import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.Link;

public class TopoGraph extends GraphicContainer {
	DisplayModel displayModel;

	public TopoGraph(DisplayModel diplayModel) {
		super(diplayModel);
		this.displayModel = diplayModel;
	}

	/**
	 * 1 添加节点模型
	 * 
	 * @param node
	 */
	public void addNode(GraphicObject node) {
		// System.out.println("--------------------"+nodes.size());
		// for (int i = 0; i < nodes.size() - 1; i++) {
		// if (nodes.get(i).getId().equals(node.getId()))
		// return;
		// }
		nodes.add(node);// 添加所有节点
		displayModel.addChild(node.getMode());
	}

	/**
	 * 2 添加连接模型 把ip地址换成对应的模型
	 * 
	 * @param mac1
	 *            起点
	 * @param mac2
	 *            终点
	 */
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
			// System.out.println("添加了数据" + obj1 + "-" + obj2);
			Link lnkLink = new Link(this, UUID.randomUUID().toString());// UUID.randomUUID().toString()生成唯一的id
			lnkLink.connect(obj1, obj2);

			// lnkLink.setStartAnchor(obj1.);
			// lnkLink.setEndAnchor(anchor);
			nodes.add(lnkLink);// 添加所有边
		}
	}

}

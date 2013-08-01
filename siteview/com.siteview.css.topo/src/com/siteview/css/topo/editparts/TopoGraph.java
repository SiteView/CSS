package com.siteview.css.topo.editparts;

import javax.xml.crypto.NodeSetData;

import org.csstudio.opibuilder.model.DisplayModel;

import ILOG.Diagrammer.GraphicContainer;
import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.Link;

public class TopoGraph extends GraphicContainer{

	public TopoGraph(DisplayModel diplayModel) {
		super(diplayModel);
	}
	
	public void addLink(String mac1,String mac2){
		GraphicObject obj1 = null;
		GraphicObject obj2 = null;
		
		for(GraphicObject go:nodes){
			if (go.isNode()){
				if (((TopoNode)go).getTopoData().getBaseMac().equals(mac1)){
					obj1 = go;
				}
				if (((TopoNode)go).getTopoData().getBaseMac().equals(mac2)){
					obj2 = go;
				}
			}
		}
		
		if (obj1!=null && obj2 !=null){
			Link lnkLink = new Link(this);
			lnkLink.SetSource(obj1);
			lnkLink.setTarget(obj2);
			nodes.add(lnkLink);
		}
	}

}

package com.siteview.css.topo.editparts;

import org.csstudio.opibuilder.model.AbstractWidgetModel;

import com.siteview.snmp.pojo.IDBody;

import ILOG.Diagrammer.GraphicContainer;
import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.Rectangle2D;

public class TopoNode extends GraphicObject {

	private IDBody topoData;
	protected AbstractWidgetModel model;

	public TopoNode(GraphicContainer parent, String id,
			AbstractWidgetModel nodeModel, IDBody data) {
		super(parent, id);
		model = nodeModel;
		setTopoData(data);
	}

	public void set_Bounds(Rectangle2D boundingBox) {
		model.setBounds((int) boundingBox.get_X(), (int) boundingBox.get_Y(),
				(int) boundingBox.get_Width(), (int) boundingBox.get_Height());
	}

	public AbstractWidgetModel getMode() {
		return model;
	}

	public void transform(float x, float y) {
		model.setLocation((int) x, (int) y);
	}

	/**
	 * 如果用到的是这个模型为true 否则false
	 */
	@Override
	public boolean isNode() {
		return true;
	}

	@Override
	public boolean isLink() {
		return false;
	}

	public IDBody getTopoData() {
		return topoData;
	}

	private void setTopoData(IDBody topoData) {
		this.topoData = topoData;
	}

}

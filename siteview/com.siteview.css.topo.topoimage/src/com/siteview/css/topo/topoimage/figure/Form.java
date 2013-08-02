package com.siteview.css.topo.topoimage.figure;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import ILOG.Diagrammer.GraphicContainer;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayout;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayoutReport;

public class Form extends OPIEditor {
	ForceDirectedLayout fLayout = new ForceDirectedLayout();

	public TopologyFigure createNode(Composite parent, String text, int x, int y) {
		TopologyFigure tFigure = new TopologyFigure();
		// TopologyFigure tFigure = new TopologyFigure(new Rectangle(x, y, 60,
		// 30));
		Rectangle rectangle = new Rectangle(x, y, 60, 30);
		return tFigure;
	}

	public void createLink(Composite parent, TopologyFigure startNode,
			TopologyFigure endNode) {

	}

	public void CreateGraph(Composite parent) {
		// 先对数据进行排版。然后在界面上面显示
		// 获取对应模型 的横纵坐标
		DisplayModel displayModel = getDisplayModel();
		ImageModel model1 = new ImageModel();
		ImageModel model2 = new ImageModel();
		model2.setX(model1.getX() + model1.getWidth() + 20);

		ConnectionModel cModel = new ConnectionModel(displayModel);
		cModel.connect(model1, "TOP", model2, "BOTTOM");
		displayModel.addChild(model1);
		displayModel.addChild(model2);

		GraphicContainer grapher = null;
		fLayout.SetLayoutReport(new ForceDirectedLayoutReport());
		fLayout.set_PreferredLinksLength(1);
		fLayout.set_RespectNodeSizes(true);

		fLayout.Attach(displayModel);// GraphicContainer grapher
		fLayout.Layout();

	}
}
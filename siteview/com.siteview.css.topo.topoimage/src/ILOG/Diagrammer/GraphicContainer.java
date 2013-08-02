package ILOG.Diagrammer;

import java.util.ArrayList;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.DisplayModel;

/**
 * 用数组保存了节点与边
 * 
 * @author Administrator
 * 
 */
public class GraphicContainer {
	DisplayModel diplayModel;

	protected ArrayList<GraphicObject> nodes;

	public ArrayList<GraphicObject> getNodes() {
		return nodes;
	}

	public GraphicContainer(DisplayModel diplayModel) {
		super();
		this.diplayModel = diplayModel;
		nodes = new ArrayList<GraphicObject>();
	}

	public void addNode(GraphicObject node) {
		for (int i = 0; i < nodes.size() - 1; i++) {
			if (nodes.get(i).getId().equals(node.getId()))
				return;
		}
		nodes.add(node);
		diplayModel.addChild(node.getMode());
	}

	public DisplayModel getDisplayModel() {
		return diplayModel;
	}

}
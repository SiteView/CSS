package ILOG.Diagrammer;

import org.csstudio.opibuilder.model.AbstractWidgetModel;

public abstract class GraphicObject {

	private String id;
	private GraphicContainer parent;

	public GraphicObject(GraphicContainer container, String id) {

		this.parent = container;
		this.id = id;
	}

	public void set_Bounds(Rectangle2D boundingBox) {

	}

	public abstract void transform(float x, float y);

	public abstract boolean isNode();

	public abstract boolean isLink();

	public AbstractWidgetModel getMode() {
		return parent.getDisplayModel();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
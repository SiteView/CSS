package ILOG.Diagrammer.GraphLayout.Internal;

import system.Collections.ArrayList;
import system.Collections.ICollection;
import ILOG.Diagrammer.Point2D;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.GraphLayout.GraphModelContentsChangedEventHandler;
import ILOG.Diagrammer.GraphLayout.IGraphModel;
import ILOG.Diagrammer.GraphLayout.ReshapeLinkMode;
import ILOG.Diagrammer.GraphLayout.ReshapeLinkStyle;

public abstract class InternalFlatBaseGraphModel implements IGraphModel {
	private ILOG.Diagrammer.GraphLayout.GraphLayout attachedLayout;

	private Boolean disposed = false;

	/* TODO: Event Declare */
	public java.util.ArrayList<GraphModelContentsChangedEventHandler> ContentsChanged = new java.util.ArrayList<GraphModelContentsChangedEventHandler>();

	public InternalFlatBaseGraphModel() {
	}

	public void AfterLayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {

	}

	public void BeforeLayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {

	}

	public abstract Rectangle2D BoundingBox(java.lang.Object nodeOrLink);

	private void DetachLayouts() {
		if ((this.attachedLayout != null)
				&& (this.attachedLayout.GetGraphModel() == this)) {
			this.attachedLayout.Detach();
		}

	}

	public void Dispose() {
		this.disposed = true;
		this.DetachLayouts();
		this.attachedLayout = null;

	}

	public abstract java.lang.Object GetFrom(java.lang.Object link);

	public IGraphModel GetGraphModel(java.lang.Object subgraph) {
		throw (new system.Exception("Not needed"));

	}

	public abstract Point2D[] GetLinkPoints(java.lang.Object link);

	public abstract ICollection GetLinksFrom(java.lang.Object node);

	public abstract ICollection GetLinksTo(java.lang.Object node);

	public float GetLinkWidth(java.lang.Object link) {

		return 1f;

	}

	public abstract java.lang.Object GetProperty(String key);

	public abstract java.lang.Object GetProperty(java.lang.Object nodeOrLink,
			String key);

	public abstract java.lang.Object GetTo(java.lang.Object link);

	public Boolean HasMoveableConnectionPoint(java.lang.Object link,
			Boolean origin) {

		return true;

	}

	public Boolean HasPinnedConnectionPoint(java.lang.Object link,
			Boolean origin) {

		return true;

	}

	public Boolean IsDisposed() {

		return this.disposed;

	}

	public Boolean IsInterGraphLink(java.lang.Object obj) {

		return false;

	}

	public Boolean IsLayoutNeeded(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {

		return true;

	}

	public abstract Boolean IsLink(java.lang.Object obj);

	public abstract Boolean IsNode(java.lang.Object obj);

	public Boolean IsSubgraph(java.lang.Object obj) {

		return false;

	}

	public abstract void MoveNode(java.lang.Object node, float x, float y);

	public void OnAttach(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if (this.attachedLayout != null) {
			throw (new system.Exception("Have already attached layouts."));
		}
		this.attachedLayout = layout;

	}

	public void OnDetach(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if (this.attachedLayout != null) {
			this.attachedLayout = null;
		}

	}

	public abstract void ReshapeLink(java.lang.Object link, int style,
			Point2D fromPoint, int fromPointMode, Point2D[] points,
			Integer startIndex, Integer length, Point2D toPoint, int toPointMode);

	public abstract void SetProperty(String key, java.lang.Object val);

	public abstract void SetProperty(java.lang.Object nodeOrLink, String key,
			java.lang.Object val);

	public ICollection get_InterGraphLinks() {

		return new ArrayList();
	}

	public abstract ICollection get_Links();

	public abstract ICollection get_Nodes();

	public IGraphModel get_Parent() {
		throw (new system.Exception("Not needed"));
	}

	public IGraphModel get_Root() {

		return this;
	}

	public ICollection get_Subgraphs() {

		return new ArrayList();
	}

}
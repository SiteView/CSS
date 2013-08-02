package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.*;
import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.Util.*;
import system.*;
import system.Collections.*;
import system.ComponentModel.*;

public final class ConnectedComponentAdapter extends InternalFlatBaseGraphModel {
	private IGraphModel _graphModel;

	private ArrayList _links;

	private HashSet _linksSet;

	private ArrayList _nodes;

	private HashSet _nodesSet;

	private IPropertyContainer _properties;

	public ConnectedComponentAdapter(IGraphModel graphModel) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		this._graphModel = graphModel;
		this._nodes = new ArrayList(300);
		this._links = new ArrayList(300);
		this._nodesSet = new HashSet();
		this._linksSet = new HashSet();
	}

	public void AddLink(java.lang.Object link) {

		if (this._linksSet.Contains(link)) {
			throw (new ArgumentException(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] { "link ", link,
									" is already contained in adapter ", this })));
		}
		this._links.Add(link);
		this._linksSet.Add(link);

	}

	public void AddNode(java.lang.Object node) {

		if (this._nodesSet.Contains(node)) {
			throw (new ArgumentException(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] { "node ", node,
									" is already contained in adapter ", this })));
		}
		this._nodes.Add(node);
		this._nodesSet.Add(node);

	}

	@Override
	public Rectangle2D BoundingBox(java.lang.Object nodeOrLink) {

		return this._graphModel.BoundingBox(nodeOrLink);

	}

	// @Override
	public void Dispose() {
		// super.Dispose();
		this._nodes = null;
		this._links = null;
		this._nodesSet = null;
		this._linksSet = null;
		this._properties = null;

	}

	@Override
	public java.lang.Object GetFrom(java.lang.Object link) {

		return this._graphModel.GetFrom(link);

	}

	@Override
	public Point2D[] GetLinkPoints(java.lang.Object link) {

		return this._graphModel.GetLinkPoints(link);

	}

	@Override
	public ICollection GetLinksFrom(java.lang.Object node) {

		return this._graphModel.GetLinksFrom(node);

	}

	@Override
	public ICollection GetLinksTo(java.lang.Object node) {

		return this._graphModel.GetLinksTo(node);

	}

	@Override
	public float GetLinkWidth(java.lang.Object link) {

		return this._graphModel.GetLinkWidth(link);

	}

	@Override
	public java.lang.Object GetProperty(String key) {
		if (this._properties == null) {

			return null;
		}

		return this._properties.GetProperty(key);

	}

	@Override
	public java.lang.Object GetProperty(java.lang.Object nodeOrLink, String key) {

		return this._graphModel.GetProperty(nodeOrLink, key);

	}

	@Override
	public java.lang.Object GetTo(java.lang.Object link) {

		return this._graphModel.GetTo(link);

	}

	@Override
	public Boolean HasMoveableConnectionPoint(java.lang.Object link,
			Boolean origin) {

		return this._graphModel.HasMoveableConnectionPoint(link, origin);

	}

	@Override
	public Boolean HasPinnedConnectionPoint(java.lang.Object link,
			Boolean origin) {

		return this._graphModel.HasPinnedConnectionPoint(link, origin);

	}

	@Override
	public Boolean IsLink(java.lang.Object obj) {

		return (this._graphModel.IsLink(obj) && this._linksSet.Contains(obj));

	}

	@Override
	public Boolean IsNode(java.lang.Object obj) {

		return (this._graphModel.IsNode(obj) && this._nodesSet.Contains(obj));

	}

	@Override
	public void MoveNode(java.lang.Object node, float x, float y) {
		this._graphModel.MoveNode(node, x, y);

	}

	@Override
	public void ReshapeLink(java.lang.Object link, int style,
			Point2D fromPoint, int fromPointMode, Point2D[] points,
			Integer startIndex, Integer length, Point2D toPoint, int toPointMode) {
		this._graphModel.ReshapeLink(link, style, fromPoint, fromPointMode,
				points, startIndex, length, toPoint, toPointMode);

	}

	@Override
	public void SetProperty(String key, java.lang.Object val) {
		if (this._properties == null) {
			this._properties = new DefaultPropertyContainer();
		}
		this._properties.SetProperty(key, val);

	}

	@Override
	public void SetProperty(java.lang.Object nodeOrLink, String key,
			java.lang.Object val) {
		this._graphModel.SetProperty(nodeOrLink, key, val);

	}

	public ICollection get_Links() {

		return this._links;
	}

	public ICollection get_Nodes() {

		return this._nodes;
	}

}
package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.*;
import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.Util.*;
import system.*;
import system.Collections.*;
import system.ComponentModel.*;

public final class ConnectedComponentCollectionAdapter extends
		InternalFlatBaseGraphModel {
	private ArrayList _nodes = new ArrayList(300);

	private HashSet _nodesSet = new HashSet();

	private IPropertyContainer _properties;

	public ConnectedComponentCollectionAdapter() {
	}

	public void AddNode(ConnectedComponentAdapter node) {

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

		return GraphModelUtil
				.BoundingBoxAsRectangle2D((ConnectedComponentAdapter) nodeOrLink);

	}

	@Override
	public void Dispose() {
		super.Dispose();
		if (this._nodes != null) {
			IEnumerator it1 = this._nodes.GetEnumerator();
			while (it1.MoveNext()) {
				java.lang.Object obj2 = (java.lang.Object) it1.get_Current();
				((IGraphModel) obj2).Dispose();
			}
		}
		this._nodes = null;
		this._nodesSet = null;

	}

	@Override
	public java.lang.Object GetFrom(java.lang.Object link) {
		throw (new system.Exception("Not a link in this graph model"));

	}

	@Override
	public Point2D[] GetLinkPoints(java.lang.Object link) {
		throw (new system.Exception("Not a link in this graph model"));

	}

	@Override
	public ICollection GetLinksFrom(java.lang.Object node) {

		return new ArrayList();

	}

	@Override
	public ICollection GetLinksTo(java.lang.Object node) {

		return new ArrayList();

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

		return ((ConnectedComponentAdapter) nodeOrLink).GetProperty(key);

	}

	@Override
	public java.lang.Object GetTo(java.lang.Object link) {
		throw (new system.Exception("Not a link in this graph model"));

	}

	@Override
	public Boolean IsLink(java.lang.Object obj) {

		return false;

	}

	@Override
	public Boolean IsNode(java.lang.Object obj) {

		return this._nodesSet.Contains(obj);

	}

	@Override
	public void MoveNode(java.lang.Object node, float x, float y) {
		GraphModelUtil.Move((ConnectedComponentAdapter) node, x, y);

	}

	@Override
	public void ReshapeLink(java.lang.Object link, int style,
			Point2D fromPoint, int fromPointMode, Point2D[] points,
			Integer startIndex, Integer length, Point2D toPoint, int toPointMode) {
		throw (new system.Exception("Not a link in this graph model"));

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
		((ConnectedComponentAdapter) nodeOrLink).SetProperty(key, val);

	}

	public ICollection get_Links() {

		return new ArrayList();
	}

	public ICollection get_Nodes() {

		return this._nodes;
	}

}
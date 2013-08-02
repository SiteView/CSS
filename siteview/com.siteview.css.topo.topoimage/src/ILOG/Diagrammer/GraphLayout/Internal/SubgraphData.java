package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.*;
import ILOG.Diagrammer.GraphLayout.*;
import system.*;
import system.Collections.*;
import system.ComponentModel.*;

public final class SubgraphData extends InternalFlatBaseGraphModel {
	private Boolean collectModelDone = false;

	private CoordinatesMode coordMode = CoordinatesMode.ViewCoordinates;

	private IGraphModel graphModel;

	// private Transform inverseReferenceTransformer;

	private LinkData links;

	private ArrayList linksCache;

	private NodeData nodes;

	private ArrayList nodesCache;

	private Boolean normalLinksAreFixed = true;

	// private Transform referenceTransformer;

	private NodeData root = new NodeData();

	private IGraphModel rootModel;

	public SubgraphData() {
		this.nodes = this.root;
	}

	private LinkData AddLink(java.lang.Object linkObj, Point2D[] points,
			float width, Boolean ig, NodeData parent, NodeData from, NodeData to) {
		LinkData data = new LinkData();
		data.next = this.links;
		data.obj = linkObj;
		data.points = points;
		data.width = width;
		data.isInterGraphLink = ig;
		data.parent = parent;
		data.from = from;
		data.to = to;
		data.nextOutgoing = from.outgoing;
		data.nextIncoming = to.incoming;
		from.outgoing = data;
		to.incoming = data;
		this.links = data;

		return data;

	}

	private NodeData AddNode(java.lang.Object nodeObj, NodeData parent) {
		NodeData data = new NodeData();
		data.next = this.nodes;
		data.obj = nodeObj;
		data.parent = parent;
		data.nextSibling = parent.children;
		parent.children = data;
		this.nodes = data;

		return data;

	}

	private void AfterCollect(IGraphModel model) {
		if (model != null) {
			if (model instanceof GraphicContainerAdapter) {
				((GraphicContainerAdapter) model)
						.RestoreAndDisableViewCoordinates();
			}
			IEnumerator it1 = model.get_Subgraphs().GetEnumerator();
			while (it1.MoveNext()) {
				java.lang.Object obj2 = (java.lang.Object) it1.get_Current();
				this.AfterCollect(this.rootModel.GetGraphModel(obj2));
			}
		}

	}

	private void AfterCreation(LinkData link) {
		// if(this.coordMode != CoordinatesMode.ViewCoordinates){
		//
		// link.bbox =
		// this.inverseReferenceTransformer.TransformRectangle(link.bbox);
		// for( Integer i = 0;
		// i < link.points.length;i++){
		//
		// link.points[i] =
		// this.inverseReferenceTransformer.TransformPoint(link.points[i]);
		// }
		// float num2 = Math.Min(this.referenceTransformer.get_ScaleX(),
		// this.referenceTransformer.get_ScaleY());
		// if(num2 == 0f){
		// throw(new
		// system.Exception("Zoom factor is not allowed to be zero. Transformer on reference view: "
		// + this.referenceTransformer));
		// }
		// link.width /= num2;
		// }

	}

	private void AfterCreation(NodeData node) {

		node.bbox = this.ConvertCoordinateSystem(node.bbox);

	}

	private void BeforeCollect(IGraphModel model) {
		// if(model != null){
		// if(model instanceof GraphicContainerAdapter){
		// GraphicContainerAdapter adapter = (GraphicContainerAdapter)model;
		// adapter.SaveAndEnableViewCoordinates(this.graphModel);
		// if(model == this.graphModel){
		// this.referenceTransformer = adapter.get_ReferenceTransform();
		//
		// this.inverseReferenceTransformer =
		// this.referenceTransformer.Inverse();
		// }
		// }
		// IEnumerator it1 = model.get_Subgraphs().GetEnumerator();
		// while(it1.MoveNext()){
		// java.lang.Object obj2 = (java.lang.Object)it1.get_Current();
		// this.BeforeCollect(this.rootModel.GetGraphModel(obj2));
		// }
		// }

	}

	@Override
	public Rectangle2D BoundingBox(java.lang.Object nodeOrLink) {

		return ((NodeOrLinkData) nodeOrLink).bbox;

	}

	public void Clean() {

	}

	public void CollectData(IGraphModel model) {
		if (model instanceof GraphicContainerAdapter) {
			this.coordMode = ((GraphicContainerAdapter) model)
					.get_CoordinatesMode();
		}
		this.collectModelDone = false;
		this.graphModel = model;
		this.rootModel = model.get_Root();
		if (this.rootModel == null) {
			this.rootModel = model;
		}
		Hashtable nodeTable = new Hashtable(500);
		this.BeforeCollect(model);
		this.CollectNodes(model, this.root, nodeTable);
		this.CollectLinks(model, this.root, nodeTable);
		this.AfterCollect(model);
		this.collectModelDone = true;
		nodeTable = null;

	}

	private void CollectLinks(IGraphModel model, NodeData parent,
			Hashtable nodeTable) {
		LinkData data = null;
		NodeData data2 = null;
		NodeData data3 = null;
		Point2D[] points = null;
		IEnumerator it1 = model.get_Links().GetEnumerator();
		while (it1.MoveNext()) {
			java.lang.Object obj2 = (java.lang.Object) it1.get_Current();
			data2 = (NodeData) nodeTable.get_Item(model.GetFrom(obj2));
			data3 = (NodeData) nodeTable.get_Item(model.GetTo(obj2));
			if ((data2 != null) && (data3 != null)) {

				points = model.GetLinkPoints(obj2);

				data = this.AddLink(obj2, points, model.GetLinkWidth(obj2),
						false, parent, data2, data3);

				data.bbox = model.BoundingBox(obj2);
				this.AfterCreation(data);
				data.model = model;
			}
		}
		IEnumerator it2 = model.get_InterGraphLinks().GetEnumerator();
		while (it2.MoveNext()) {
			java.lang.Object obj3 = (java.lang.Object) it2.get_Current();
			data2 = (NodeData) nodeTable.get_Item(model.GetFrom(obj3));
			data3 = (NodeData) nodeTable.get_Item(model.GetTo(obj3));
			if ((data2 != null) && (data3 != null)) {

				points = model.GetLinkPoints(obj3);

				data = this.AddLink(obj3, points, model.GetLinkWidth(obj3),
						true, parent, data2, data3);

				data.bbox = model.BoundingBox(obj3);
				this.AfterCreation(data);
				data.model = model;
			}
		}
		IEnumerator it3 = model.get_Subgraphs().GetEnumerator();
		while (it3.MoveNext()) {
			java.lang.Object obj4 = (java.lang.Object) it3.get_Current();
			NodeData data4 = (NodeData) nodeTable.get_Item(obj4);
			IGraphModel graphModel = this.rootModel.GetGraphModel(obj4);
			this.CollectLinks(graphModel, data4, nodeTable);
		}

	}

	private void CollectNodes(IGraphModel model, NodeData parent,
			Hashtable nodeTable) {
		NodeData data = null;
		IEnumerator it1 = model.get_Nodes().GetEnumerator();
		while (it1.MoveNext()) {
			java.lang.Object obj2 = (java.lang.Object) it1.get_Current();

			data = this.AddNode(obj2, parent);

			data.bbox = model.BoundingBox(obj2);
			data.model = model;
			this.AfterCreation(data);
			nodeTable.set_Item(obj2, data);
		}
		IEnumerator it2 = model.get_Subgraphs().GetEnumerator();
		while (it2.MoveNext()) {
			java.lang.Object obj3 = (java.lang.Object) it2.get_Current();
			data = (NodeData) nodeTable.get_Item(obj3);
			IGraphModel graphModel = this.rootModel.GetGraphModel(obj3);
			this.CollectNodes(graphModel, data, nodeTable);
		}

	}

	public Rectangle2D ConvertCoordinateSystem(Rectangle2D rect) {
		// if(this.coordMode != CoordinatesMode.ViewCoordinates){
		//
		//
		// return
		// this.get_inverseReferenceTransformer().TransformRectangle(rect);
		// }

		return rect;

	}

	@Override
	public void Dispose() {
		this.graphModel = null;
		this.rootModel = null;
		this.root = null;
		this.nodes = null;
		this.links = null;
		this.nodesCache = null;
		this.linksCache = null;
		super.Dispose();

	}

	public Boolean FromEndsInside(java.lang.Object link, java.lang.Object node) {

		return this.Inside(((LinkData) link).from, (NodeData) node);

	}

	public ICollection GetChildren(java.lang.Object node) {
		NodeData data = (NodeData) node;
		Boolean collectModelDone = this.collectModelDone;
		if (collectModelDone && (data.childrenCache != null)) {

			return data.childrenCache;
		}
		ArrayList list = new ArrayList();
		for (NodeData data2 = data.children; data2 != null; data2 = data2.nextSibling) {
			list.Add(data2);
		}
		if (collectModelDone) {
			data.childrenCache = list;
		}

		return list;

	}

	@Override
	public java.lang.Object GetFrom(java.lang.Object link) {

		return ((LinkData) link).from;

	}

	@Override
	public Point2D[] GetLinkPoints(java.lang.Object link) {
		LinkData data = (LinkData) link;

		return data.points;

	}

	@Override
	public ICollection GetLinksFrom(java.lang.Object node) {
		NodeData data = (NodeData) node;
		Boolean collectModelDone = this.collectModelDone;
		if (collectModelDone && (data.outgoingCache != null)) {

			return data.outgoingCache;
		}
		ArrayList list = new ArrayList();
		for (LinkData data2 = data.outgoing; data2 != null; data2 = data2.nextOutgoing) {
			list.Add(data2);
		}
		if (collectModelDone) {
			data.outgoingCache = list;
		}

		return list;

	}

	@Override
	public ICollection GetLinksTo(java.lang.Object node) {
		NodeData data = (NodeData) node;
		Boolean collectModelDone = this.collectModelDone;
		if (collectModelDone && (data.incomingCache != null)) {

			return data.incomingCache;
		}
		ArrayList list = new ArrayList();
		for (LinkData data2 = data.incoming; data2 != null; data2 = data2.nextIncoming) {
			list.Add(data2);
		}
		if (collectModelDone) {
			data.incomingCache = list;
		}

		return list;

	}

	@Override
	public float GetLinkWidth(java.lang.Object link) {

		return ((LinkData) link).width;

	}

	public Rectangle2D GetNodeBox(INodeBoxProvider ifc, java.lang.Object node) {
		java.lang.Object original = this.GetOriginal(node);
		if (original == null) {

			return ifc.GetBox(this.graphModel, node);
		}
		IGraphModel originalGraphModel = this.GetOriginalGraphModel(node);
		if (originalGraphModel instanceof GraphicContainerAdapter) {
			((GraphicContainerAdapter) originalGraphModel)
					.SaveAndEnableViewCoordinates(this.graphModel);
		}
		Rectangle2D box = ifc.GetBox(originalGraphModel, original);
		if (originalGraphModel instanceof GraphicContainerAdapter) {
			((GraphicContainerAdapter) originalGraphModel)
					.RestoreAndDisableViewCoordinates();
		}

		return this.ConvertCoordinateSystem(box);

	}

	public java.lang.Object GetOriginal(java.lang.Object obj) {
		if (obj instanceof NodeOrLinkData) {

			return ((NodeOrLinkData) obj).obj;
		}

		return null;

	}

	public IGraphModel GetOriginalGraphModel(java.lang.Object obj) {
		if (obj instanceof NodeOrLinkData) {

			return ((NodeOrLinkData) obj).model;
		}

		return this.graphModel;

	}

	public IGraphModel GetOriginalModel() {

		return this.graphModel;

	}

	public java.lang.Object GetParent(java.lang.Object obj) {
		if (obj instanceof NodeOrLinkData) {

			return ((NodeOrLinkData) obj).parent;
		}

		return null;

	}

	@Override
	public java.lang.Object GetProperty(String key) {

		return this.graphModel.GetProperty(key);

	}

	@Override
	public java.lang.Object GetProperty(java.lang.Object nodeOrLink, String key) {
		NodeOrLinkData data = (NodeOrLinkData) nodeOrLink;
		if (data.model == null) {
			throw (new system.Exception(
					"model is null; the method should be called only after collectData is called"));
		}

		return data.model.GetProperty(data.obj, key);

	}

	// public float GetTangentialOffset(ILinkConnectionBoxProvider ifc,
	// java.lang.Object node, int side)
	// {
	//
	//
	// return this.GetTangentialOffset(ifc,node,(int)side);
	//
	// }

	public float GetTangentialOffset(ILinkConnectionBoxProvider ifc,
			java.lang.Object node, int side) {
		java.lang.Object original = this.GetOriginal(node);
		if (original == null) {

			return ifc.GetTangentialOffset(this.graphModel, node, side);
		}
		IGraphModel originalGraphModel = this.GetOriginalGraphModel(node);
		if (originalGraphModel instanceof GraphicContainerAdapter) {
			((GraphicContainerAdapter) originalGraphModel)
					.SaveAndEnableViewCoordinates(this.graphModel);
		}
		float num = ifc.GetTangentialOffset(originalGraphModel, original, side);
		if (originalGraphModel instanceof GraphicContainerAdapter) {
			((GraphicContainerAdapter) originalGraphModel)
					.RestoreAndDisableViewCoordinates();
		}
		if (this.coordMode == CoordinatesMode.ViewCoordinates) {

			return num;
		}
		// if(side == NodeSide.Top || side == NodeSide.Bottom){
		//
		// return (num / this.get_referenceTransformer().get_ScaleX());
		// }
		//
		// return (num / this.referenceTransformer.get_ScaleY());

		return num;
	}

	@Override
	public java.lang.Object GetTo(java.lang.Object link) {

		return ((LinkData) link).to;

	}

	@Override
	public Boolean HasMoveableConnectionPoint(java.lang.Object link,
			Boolean origin) {
		LinkData data = (LinkData) link;

		return data.model.HasMoveableConnectionPoint(data.obj, origin);

	}

	@Override
	public Boolean HasPinnedConnectionPoint(java.lang.Object link,
			Boolean origin) {
		LinkData data = (LinkData) link;

		return data.model.HasPinnedConnectionPoint(data.obj, origin);

	}

	private Boolean Inside(NodeData node, NodeData graph) {
		if (node == null) {

			return false;
		}

		return ((node.parent == graph) || this.Inside(node.parent, graph));

	}

	public Boolean IsFixedLink(java.lang.Object link) {
		LinkData data = (LinkData) link;

		return ((this.normalLinksAreFixed && !data.isInterGraphLink) || (data.model != this.graphModel));

	}

	@Override
	public Boolean IsLink(java.lang.Object obj) {

		return (obj instanceof LinkData);

	}

	@Override
	public Boolean IsNode(java.lang.Object obj) {

		return (obj instanceof NodeData);

	}

	@Override
	public void MoveNode(java.lang.Object node, float x, float y) {
		throw (new system.Exception("Link routing should not move nodes"));

	}

	public Boolean NodeInsideGraph(java.lang.Object node, java.lang.Object graph) {

		return this.Inside((NodeData) node, (NodeData) graph);

	}

	@Override
	public void ReshapeLink(java.lang.Object link, int style,
			Point2D fromPoint, int fromPointMode, Point2D[] points,
			Integer startIndex, Integer length, Point2D toPoint, int toPointMode) {
		LinkData data = (LinkData) link;
		data.model.ReshapeLink(data.obj, style, fromPoint, fromPointMode,
				points, startIndex, length, toPoint, toPointMode);

	}

	public void SetNormalLinksFixed(Boolean f) {
		this.normalLinksAreFixed = f;

	}

	@Override
	public void SetProperty(String key, java.lang.Object val) {
		this.graphModel.SetProperty(key, val);

	}

	@Override
	public void SetProperty(java.lang.Object nodeOrLink, String key,
			java.lang.Object val) {
		NodeOrLinkData data = (NodeOrLinkData) nodeOrLink;
		if (data.model == null) {
			throw (new system.Exception(
					"model is null; the method should be called only after collectData is called"));
		}
		data.model.SetProperty(data.obj, key, val);

	}

	public Boolean ToEndsInside(java.lang.Object link, java.lang.Object node) {

		return this.Inside(((LinkData) link).to, (NodeData) node);

	}

	public ICollection get_Links() {
		Boolean collectModelDone = this.collectModelDone;
		if (collectModelDone && (this.linksCache != null)) {

			return this.linksCache;
		}
		ArrayList list = new ArrayList();
		for (LinkData data = this.links; data != null; data = data.next) {
			list.Add(data);
		}
		if (collectModelDone) {
			this.linksCache = list;
		}

		return list;
	}

	public ICollection get_Nodes() {
		Boolean collectModelDone = this.collectModelDone;
		if (collectModelDone && (this.nodesCache != null)) {

			return this.nodesCache;
		}
		ArrayList list = new ArrayList();
		NodeData nodes = this.nodes;
		if (nodes == this.root) {
			nodes = nodes.next;
		}
		while (nodes != null) {
			list.Add(nodes);
			nodes = nodes.next;
			if (nodes == this.root) {
				nodes = nodes.next;
			}
		}
		if (collectModelDone) {
			this.nodesCache = list;
		}

		return list;
	}

	private final class LinkData extends SubgraphData.NodeOrLinkData {
		public SubgraphData.NodeData from;

		public Boolean isInterGraphLink = false;

		public SubgraphData.LinkData next;

		public SubgraphData.LinkData nextIncoming;

		public SubgraphData.LinkData nextOutgoing;

		public Point2D[] points;

		public SubgraphData.NodeData to;

		public float width;

	}

	private final class NodeData extends SubgraphData.NodeOrLinkData {
		public SubgraphData.NodeData children;

		public ArrayList childrenCache;

		public SubgraphData.LinkData incoming;

		public ArrayList incomingCache;

		public SubgraphData.NodeData next;

		public SubgraphData.NodeData nextSibling;

		public SubgraphData.LinkData outgoing;

		public ArrayList outgoingCache;

	}

	private class NodeOrLinkData {
		public Rectangle2D bbox;

		public IGraphModel model;

		public java.lang.Object obj;

		public SubgraphData.NodeData parent;

	}
}
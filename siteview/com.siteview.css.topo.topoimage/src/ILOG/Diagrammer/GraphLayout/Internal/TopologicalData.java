package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;
import system.Collections.*;

public final class TopologicalData {
	private IGraphModel _graphModel;

	private ILOG.Diagrammer.GraphLayout.GraphLayout _layout;

	private GraphLayoutData _layoutData;

	private Hashtable _neighbors = new Hashtable(500);

	private Boolean _noMoveableNode = false;

	private Integer _numberOfLinks;

	private Integer _numberOfNodes;

	private java.lang.Object[] _vectLinks;

	private ListOfIntegers[] _vectNeighbours;

	private java.lang.Object[] _vectNodes;

	private KeyDataInt _whatLinkBetweenTwoNodes;

	public TopologicalData(ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			GraphLayoutData layoutData, Boolean oneConnectedComp) {
		this._layout = layout;
		this._layoutData = layoutData;
		this.Initialize(oneConnectedComp);
	}

	public static Integer ComputeUniqueIndex(Integer nodeId1, Integer nodeId2,
			Integer numberOfNodes) {
		if (nodeId1 < nodeId2) {

			return (nodeId1 + (nodeId2 * numberOfNodes));
		}

		return (nodeId2 + (nodeId1 * numberOfNodes));

	}

	private void ComputeVectNodesAndLinks(Boolean oneConnectedComp) {
		if (oneConnectedComp) {
			this.ComputeVectNodesAndLinksOneConnectedComp();
		} else {
			this.ComputeVectNodesAndLinksGlobal();
		}

	}

	private void ComputeVectNodesAndLinksGlobal() {
		this.CountNodesAndLinks();
		this._vectNodes = new java.lang.Object[this._numberOfNodes];
		this._vectLinks = new java.lang.Object[this._numberOfLinks];
		Integer num = 0;
		Integer num2 = 0;
		Boolean flag = this._layout.SupportsPreserveFixedNodes()
				&& this._layout.get_PreserveFixedNodes();
		Boolean noMoveableNode = true;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._graphModel.get_Nodes());

		while (enumerator.HasMoreElements()) {
			java.lang.Object nodeOrLink = enumerator.NextElement();

			if ((flag && noMoveableNode) && !this._layout.GetFixed(nodeOrLink)) {
				noMoveableNode = false;
			}
			if (num >= this._numberOfNodes) {
				throw (new system.Exception(
						"There are more nodes in the graph than expected!"));
			}
			this._vectNodes[num++] = nodeOrLink;
		}
		if (flag) {
			this.SetNoMoveableNode(noMoveableNode);
		} else {
			this.SetNoMoveableNode(false);
		}
		IJavaStyleEnumerator enumerator2 = TranslateUtil
				.Collection2JavaStyleEnum(this._graphModel.get_Links());

		while (enumerator2.HasMoreElements()) {
			java.lang.Object obj3 = enumerator2.NextElement();
			if (num2 >= this._numberOfLinks) {
				throw (new system.Exception(
						"There are more links in the graph than expected!"));
			}
			this._vectLinks[num2++] = obj3;
		}

	}

	private void ComputeVectNodesAndLinksOneConnectedComp() {

	}

	public void ComputeWhatLinkBetweenTwoNodes(Boolean memorySavings) {
		this._whatLinkBetweenTwoNodes = new KeyDataInt(this._numberOfNodes
				* this._numberOfNodes, false);
		for (Integer i = 0; i < this._numberOfLinks; i++) {
			java.lang.Object link = this._vectLinks[i];
			java.lang.Object from = this._graphModel.GetFrom(link);
			java.lang.Object to = this._graphModel.GetTo(link);
			Integer intIdentifier = this._layoutData.GetIntIdentifier(from);
			Integer num2 = this._layoutData.GetIntIdentifier(to);
			this._whatLinkBetweenTwoNodes
					.Put(ComputeUniqueIndex(intIdentifier, num2,
							this._numberOfNodes), i);
		}

	}

	private void CountNodesAndLinks() {
		this._numberOfNodes = 0;
		this._numberOfLinks = 0;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._graphModel.get_Nodes());

		while (enumerator.HasMoreElements()) {
			java.lang.Object nodeOrLink = enumerator.NextElement();
			this._layoutData
					.SetIntIdentifier(nodeOrLink, this._numberOfNodes++);
		}
		IJavaStyleEnumerator enumerator2 = TranslateUtil
				.Collection2JavaStyleEnum(this._graphModel.get_Links());

		while (enumerator2.HasMoreElements()) {
			java.lang.Object obj3 = enumerator2.NextElement();
			this._layoutData.SetIntIdentifier(obj3, this._numberOfLinks++);
		}

	}

	public java.lang.Object GetLink(Integer linkId) {

		return this._vectLinks[linkId];

	}

	public Integer GetLinkId(Integer nodeId1, Integer nodeId2) {

		return this._whatLinkBetweenTwoNodes.Get(ComputeUniqueIndex(nodeId1,
				nodeId2, this._numberOfNodes));

	}

	public InternalRect GetModifiedLayoutRegion(InternalRect layoutRegion) {
		float width = 0f;
		float height = 0f;
		Integer numberOfNodes = this.GetNumberOfNodes();
		for (Integer i = 0; i < numberOfNodes; i++) {
			java.lang.Object node = this.GetNode(i);
			InternalRect rect = GraphModelUtil.BoundingBox(this._graphModel,
					node);
			if (rect.Width > width) {
				width = rect.Width;
			}
			if (rect.Height > height) {
				height = rect.Height;
			}
		}

		return new InternalRect(layoutRegion.X + (width * 0.5f), layoutRegion.Y
				+ (height * 0.5f), layoutRegion.Width - width,
				layoutRegion.Height - height);

	}

	public Integer GetNeighbour(Integer nodeId, Integer neighbourId) {

		return this._vectNeighbours[nodeId].Get(neighbourId);

	}

	public java.lang.Object GetNode(Integer nodeId) {

		return this._vectNodes[nodeId];

	}

	public java.lang.Object[] GetNodes() {

		return this._vectNodes;

	}

	public Integer GetNumberOfLinks() {

		return this._numberOfLinks;

	}

	public Integer GetNumberOfNeighbours(Integer nodeId) {

		return this._vectNeighbours[nodeId].Length();

	}

	public Integer GetNumberOfNodes() {

		return this._numberOfNodes;

	}

	public void Initialize(Boolean oneConnectedComp) {

		this._graphModel = this._layout.GetGraphModel();
		this.ComputeVectNodesAndLinks(oneConnectedComp);

	}

	public void InitializeNeighbours() {
		if ((this._vectNeighbours == null)
				|| (this._vectNeighbours.length != this._numberOfNodes)) {
			this._vectNeighbours = new ListOfIntegers[this._numberOfNodes];
		}
		for (Integer i = 0; i < this._numberOfNodes; i++) {
			java.lang.Object node = this.GetNode(i);
			if (node != null) {
				Integer nodeDegree = GraphModelUtil.GetNodeDegree(
						this._graphModel, node);
				if (nodeDegree >= 0) {
					this._vectNeighbours[i] = new ListOfIntegers(nodeDegree);
					this._neighbors.Clear();
					IJavaStyleEnumerator enumerator = TranslateUtil
							.Collection2JavaStyleEnum(GraphModelUtil
									.GetNeighbors(this._graphModel, node));

					while (enumerator.HasMoreElements()) {
						java.lang.Object key = enumerator.NextElement();

						if (((key != null) && (key != node))
								&& !this._neighbors.ContainsKey(key)) {
							this._vectNeighbours[i].Append(this._layoutData
									.GetIntIdentifier(key));
							this._neighbors.set_Item(key, key);
						}
					}
				}
			}
		}

	}

	public Boolean IsLinkBetween(Integer nodeId1, Integer nodeId2) {

		return (this._whatLinkBetweenTwoNodes.Get(ComputeUniqueIndex(nodeId1,
				nodeId2, this._numberOfNodes)) >= 0);

	}

	public Boolean IsNoMoveableNode() {

		return this._noMoveableNode;

	}

	private void SetNoMoveableNode(Boolean noMoveableNode) {
		this._noMoveableNode = noMoveableNode;

	}

	public final class ConnectedComponentTraversal extends GraphModelDFS {
		public TopologicalData __outerThis;

		private Integer _nLinks;

		private Integer _nNodes;

		private ArrayList _vectLinks2;

		private ArrayList _vectNodes2;

		public ConnectedComponentTraversal(TopologicalData input__outerThis,
				IGraphModel graphModel, String temporaryPropName) {
			super(graphModel, temporaryPropName);
			this.__outerThis = input__outerThis;
		}

		@Override
		public void Clean() {
			if (super._graphModel != null) {
				Integer count = this._vectNodes2.get_Count();
				for (Integer i = 0; i < count; i++) {
					super._graphModel.SetProperty(this._vectNodes2.get_Item(i),
							super._propName, null);
				}
				count = this._vectLinks2.get_Count();
				for (Integer j = 0; j < count; j++) {
					super._graphModel.SetProperty(this._vectLinks2.get_Item(j),
							super._propName, null);
				}
			}

		}

		public void Execute(java.lang.Object startNode, ArrayList vectNodes,
				ArrayList vectLinks) {
			this._vectNodes2 = vectNodes;
			this._vectLinks2 = vectLinks;
			this._nNodes = 0;
			this._nLinks = 0;
			super.Execute(startNode, true);

		}

		@Override
		public void MarkLink(java.lang.Object link) {
			super.MarkLink(link);
			this._vectLinks2.Add(link);
			this.__outerThis._layoutData.SetIntIdentifier(link, this._nLinks++);

		}

		@Override
		public void MarkNode(java.lang.Object node) {
			super.MarkNode(node);
			this._vectNodes2.Add(node);
			this.__outerThis._layoutData.SetIntIdentifier(node, this._nNodes++);

		}

	}
}
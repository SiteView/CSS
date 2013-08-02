package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;

public final class CrossRedSwimLaneAlgorithm extends CrossingReductionAlgorithm {
	public HSwimLane[] _dummySwimLanes;

	private Integer _numNodesForPropagation;

	private double _sumMarkersForPropagation;

	public CrossRedSwimLaneAlgorithm(HGraph graph) {
		super(graph);
		super._checkBestPositions = false;
		super._numberOfSweeps = 1;
	}

	private void AllocateDummySwimLanes() {
		Integer numberOfSwimLanes = super.GetGraph().GetNumberOfSwimLanes();
		this._dummySwimLanes = new HSwimLane[numberOfSwimLanes + 1];

	}

	private void CalcAveragePositionOfSwimLanes(HLevel level) {
		HSwimLane swimLane = null;
		HGraph graph = super.GetGraph();
		IJavaStyleEnumerator swimLanes = graph.GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			swimLane = (HSwimLane) swimLanes.NextElement();
			swimLane.SetAvgPositionActLevel(0f);
			swimLane.SetMarker(0);
		}
		HNodeIterator nodes = level.GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();

			swimLane = node.GetSwimLane();
			if (swimLane != null) {
				swimLane.AddMarker(1);
				swimLane.AddAvgPositionActLevel((float) node
						.GetPositionInLevel());
			}
		}

		swimLanes = graph.GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			swimLane = (HSwimLane) swimLanes.NextElement();
			if (swimLane.GetMarker() > 0) {
				swimLane.SetAvgPositionActLevel(swimLane
						.GetAvgPositionActLevel()
						/ ((float) swimLane.GetMarker()));
			} else {
				swimLane.SetAvgPositionActLevel(-1f);
			}
		}

	}

	private void CalcOrderingNumbersFromCrossings() {
		HMANode orderingNode = null;
		HGraph graph = super.GetGraph();
		HMAGraph graph2 = new HMAGraph();
		HMANode[] nodeArray = new HMANode[graph.GetNumberOfSwimLanes()];
		Integer index = 0;
		IJavaStyleEnumerator swimLanes = graph.GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			HSwimLane lane = (HSwimLane) swimLanes.NextElement();
			nodeArray[index] = new HMANode();
			lane.SetOrderingNode(nodeArray[index]);
			graph2.AddNode(nodeArray[index]);
			index++;
		}
		HLinkIterator links = graph.GetLinks();

		while (links.HasNext()) {
			HLink link = links.Next();

			orderingNode = link.GetFrom().GetSwimLane().GetOrderingNode();
			HMANode target = link.GetTo().GetSwimLane().GetOrderingNode();
			if (orderingNode != target) {
				HMAEdge edge = (HMAEdge) graph2.GetEdgeBetween(orderingNode,
						target, false);
				if (edge != null) {
					edge.SetPriority(edge.GetPriority() + 1f);
				} else {
					graph2.AddEdge(new HMAEdge(1f), orderingNode, target);
				}
			}
		}
		index = 0;
		while (index < nodeArray.length) {
			nodeArray[index].SetOrderingNumber(index);
			index++;
		}
		Boolean flag = true;
		Integer num2 = nodeArray.length + 2;
		while (flag && (num2 > 0)) {
			num2--;
			flag = false;
			index = 0;
			while (index < (nodeArray.length - 1)) {

				if (this.NeedsSwapFromCrossings(nodeArray[index],
						nodeArray[index + 1])) {
					orderingNode = nodeArray[index];
					nodeArray[index] = nodeArray[index + 1];
					nodeArray[index + 1] = orderingNode;
					nodeArray[index].SetOrderingNumber(index);
					nodeArray[index + 1].SetOrderingNumber(index + 1);
					flag = true;
				}
				index++;
			}
			if (!flag) {
				for (index = 0; index < (nodeArray.length - 2); index++) {
					if (this.NeedsRotateFromCrossings(nodeArray[index],
							nodeArray[index + 1], nodeArray[index + 2]) == -1) {
						orderingNode = nodeArray[index];
						nodeArray[index] = nodeArray[index + 1];
						nodeArray[index + 1] = nodeArray[index + 2];
						nodeArray[index + 2] = orderingNode;
						nodeArray[index].SetOrderingNumber(index);
						nodeArray[index + 1].SetOrderingNumber(index + 1);
						nodeArray[index + 2].SetOrderingNumber(index + 2);
						flag = true;
						// NOTICE: break ignore!!!
					} else if (this.NeedsRotateFromCrossings(nodeArray[index],
							nodeArray[index + 1], nodeArray[index + 2]) == 1) {
						orderingNode = nodeArray[index];
						nodeArray[index] = nodeArray[index + 2];
						nodeArray[index + 2] = nodeArray[index + 1];
						nodeArray[index + 1] = orderingNode;
						nodeArray[index].SetOrderingNumber(index);
						nodeArray[index + 1].SetOrderingNumber(index + 1);
						nodeArray[index + 2].SetOrderingNumber(index + 2);
						flag = true;
						// NOTICE: break ignore!!!
					}
				}
			}
		}
		this.TransferOrderingNumbersFromOrderingGraph();

	}

	private void CalcOrderingNumbersFromNeighboring() {
		HSwimLane swimLane = null;
		HGraph graph = super.GetGraph();
		HMAGraph graph2 = new HMAGraph();
		IJavaStyleEnumerator swimLanes = graph.GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			swimLane = (HSwimLane) swimLanes.NextElement();
			swimLane.SetOrderingNode(new HMANode());
			graph2.AddNode(swimLane.GetOrderingNode());
		}
		HLevelIterator levels = graph.GetLevels();

		while (levels.HasNext()) {
			HNodeIterator nodes = levels.Next().GetNodes();
			HSwimLane lane2 = null;

			while (nodes.HasNext()) {

				swimLane = nodes.Next().GetSwimLane();
				if (swimLane != null) {
					if ((swimLane != lane2) && (lane2 != null)) {
						graph2.AddEdge(new HMAEdge(1f),
								lane2.GetOrderingNode(),
								swimLane.GetOrderingNode());
					}
					lane2 = swimLane;
				}
			}
		}
		Algorithm algorithm = new Algorithm(graph2, super.GetGraph()
				.GetLayout(), null);
		algorithm.SetTopologicalOrdering(true);
		algorithm.Run();
		this.TransferOrderingNumbersFromOrderingGraph();

	}

	private void CalcPropagationSwimLaneMarker(HNode node) {
		if (node.GetMarker() == -1) {
			node.SetMarker(0);
			HSegmentIterator segments = node.GetSegments();

			while (segments.HasNext()) {
				HNode opposite = segments.Next().GetOpposite(node);
				if (opposite.GetMarker() == -1) {
					this.CalcPropagationSwimLaneMarker(opposite);
				} else if (opposite.GetMarker() > 0) {
					this._sumMarkersForPropagation += opposite.GetMarker();
					this._numNodesForPropagation++;
				}
			}
		}

	}

	private void CalcSwimLaneOrder() {

		if (this.HasFullPositionNumbers()) {
			this.SetOrderingNumberFromPositionNumbers();
		} else if (this.HasSwimLanesAtAllNodes()) {
			this.CalcOrderingNumbersFromCrossings();
			this.VerifySpecifiedLanePositionIndex();
		} else {
			this.CalcOrderingNumbersFromNeighboring();
			this.VerifySpecifiedLanePositionIndex();
		}
		this.SetFinalOrderingNumbers();

	}

	private void CalcSwimLanesForFreeNodes() {
		HNode node = null;
		HGraph graph = super.GetGraph();
		HNodeIterator nodes = graph.GetNodes();

		while (nodes.HasNext()) {

			node = nodes.Next();
			if (node.GetSwimLane() != null) {
				node.SetMarker(node.GetSwimLane().GetOrderingNumber());
			} else {
				node.SetMarker(-1);
			}
		}

		nodes = graph.GetNodes();

		while (nodes.HasNext()) {

			node = nodes.Next();
			if (node.GetMarker() == -1) {
				this.PropagateMarker(node,
						this.GetPropagationSwimLaneMarker(node));
			}
		}
		this.OptimizeSwimLanesForFreeNodes();

		nodes = graph.GetNodes();

		while (nodes.HasNext()) {

			node = nodes.Next();
			if (node.GetSwimLane() == null) {
				if (node.GetMarker() == -1) {
					node.SetMarker(1);
				} else if (node.IsDummyNode()) {
					Integer marker = node.GetOwnerLink().GetTo().GetMarker();
					if ((marker % 2) == 1) {
						node.SetMarker(marker);
						continue;
					}

					marker = node.GetOwnerLink().GetFrom().GetMarker();
					if ((marker % 2) == 1) {
						node.SetMarker(marker);
					}
				}
			}
		}

		nodes = graph.GetNodes();

		while (nodes.HasNext()) {

			node = nodes.Next();
			if (node.GetSwimLane() == null) {
				node.SetSwimLane(this.GetDummySwimLane(node.GetMarker()));
			}
		}

	}

	private void CalcSwimLaneSortValue(HLevel level) {
		Integer num = level.GetNodesCount() + 5;
		HNodeIterator nodes = level.GetNodes();

		while (nodes.HasNext()) {
			Integer orderingNumber = null;
			HNode node = nodes.Next();
			HSwimLane swimLane = node.GetSwimLane();
			if (swimLane != null) {

				orderingNumber = swimLane.GetOrderingNumber();
			} else {

				orderingNumber = this.GetFreeNodeOrderingValue(node);
				node.SetSwimLane(this.GetDummySwimLane(orderingNumber));
			}
			node.SetSortValue((float) ((num * orderingNumber) + node
					.GetPositionInLevel()));
		}

	}

	@Override
	public void Clean() {
		super.Clean();

	}

	private HSwimLane GetDummySwimLane(Integer i) {
		if (i < 2) {
			i = 0;
		}
		if (((i - 1) / 2) >= this._dummySwimLanes.length) {
			i = this._dummySwimLanes.length - 1;
		} else {
			i = (i - 1) / 2;
		}
		if (this._dummySwimLanes[i] == null) {

			this._dummySwimLanes[i] = super.GetGraph().AddDummySwimLane();
			this._dummySwimLanes[i].SetOrderingNumber((2 * i) + 1);
		}

		return this._dummySwimLanes[i];

	}

	private Integer GetFreeNodeOrderingValue(HNode node) {
		Integer num = 0;
		Integer num2 = 0;
		Integer num3 = 0;
		HSwimLane swimLane = null;
		float positionInLevel = node.GetPositionInLevel();
		HSegmentIterator segments = node.GetSegments();

		while (segments.HasNext()) {
			HNode opposite = segments.Next().GetOpposite(node);
			if (opposite.GetSwimLane() != null) {

				num2 += opposite.GetSwimLane().GetOrderingNumber();
				num3++;

				swimLane = opposite.GetSwimLane();
			}
		}
		if (num3 > 0) {
			num = (int) ((num2 + 1E-06f) / ((float) num3));
			if ((((num % 2) == 0) && ((num * num3) == num2))
					&& ((swimLane != null) && (swimLane.GetOrderingNumber() == num))) {
				if (swimLane.GetAvgPositionActLevel() != -1f) {
					if (swimLane.GetAvgPositionActLevel() < positionInLevel) {

						return (swimLane.GetOrderingNumber() + 1);
					}

					return (swimLane.GetOrderingNumber() - 1);
				}
				if (node.GetSegmentsToCount() <= 0) {

					return num;
				}
				if (swimLane.GetAvgPositionPrevLevel() < node.GetSortValue()) {

					return (swimLane.GetOrderingNumber() + 1);
				}

				return (swimLane.GetOrderingNumber() - 1);
			}
			if ((num % 2) == 0) {

				return (num + 1);
			}

			return num;
		}
		num = 1;
		float avgPositionActLevel = -1f;
		IJavaStyleEnumerator swimLanes = super.GetGraph().GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			swimLane = (HSwimLane) swimLanes.NextElement();
			if ((swimLane.GetAvgPositionActLevel() < positionInLevel)
					&& (swimLane.GetAvgPositionActLevel() > avgPositionActLevel)) {
				num = swimLane.GetOrderingNumber() + 1;

				avgPositionActLevel = swimLane.GetAvgPositionActLevel();
			}
		}

		return num;

	}

	private Integer GetPropagationSwimLaneMarker(HNode node) {
		this._numNodesForPropagation = 0;
		this._sumMarkersForPropagation = 0.0;
		this.CalcPropagationSwimLaneMarker(node);
		if (this._numNodesForPropagation == 0) {

			return -1;
		}
		Integer num = (int) ((this._sumMarkersForPropagation + 0.0001) / ((double) this._numNodesForPropagation));
		if ((num % 2) == 0) {
			num++;
		}

		return num;

	}

	private Integer GetSwimLaneMarker(HNode node) {
		float num = 0f;
		Integer num2 = 0;
		HSegmentIterator segments = node.GetSegments();

		while (segments.HasNext()) {
			HNode opposite = segments.Next().GetOpposite(node);
			if (opposite.GetMarker() != -1) {

				num += opposite.GetMarker();
				num2++;
			}
		}
		if (num2 == 0) {

			return -1;
		}
		Integer num3 = (int) ((num + 0.0001f) / ((float) num2));
		if ((num3 % 2) == 0) {
			num3++;
		}

		return num3;

	}

	private Boolean HasFullPositionNumbers() {
		IJavaStyleEnumerator swimLanes = super.GetGraph().GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			HSwimLane lane = (HSwimLane) swimLanes.NextElement();
			if (lane.GetSpecPositionIndex() < 0) {

				return false;
			}
		}

		return true;

	}

	private Boolean HasSwimLanesAtAllNodes() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			HNodeIterator nodes = levels.Next().GetNodes();

			while (nodes.HasNext()) {
				if (nodes.Next().GetSwimLane() == null) {

					return false;
				}
			}
		}

		return true;

	}

	@Override
	public void InitBackwardFirstLevel(HLevel firstLevel) {
		this.VerifySwimLanes(firstLevel);
		firstLevel.StoreLevelPositionsInNodes(1, false);

	}

	@Override
	public void InitForwardFirstLevel(HLevel firstLevel) {
		this.VerifySwimLanes(firstLevel);
		firstLevel.StoreLevelPositionsInNodes(1, false);

	}

	private Integer NeedsRotateFromCrossings(HMANode node1, HMANode node2,
			HMANode node3) {
		HMAEdge edge = null;
		HMANode hMAOpposite = null;
		float num = 0f;
		float num2 = 0f;
		Integer orderingNumber = node1.GetOrderingNumber();
		HMAEdgeIterator hMAEdges = node1.GetHMAEdges();

		while (hMAEdges.HasNext()) {

			edge = hMAEdges.Next();

			hMAOpposite = edge.GetHMAOpposite(node1);
			if (hMAOpposite == node2) {

				num2 += edge.GetPriority();
			} else {
				if (hMAOpposite == node3) {

					num -= edge.GetPriority();

					num2 -= edge.GetPriority();
					continue;
				}
				if (hMAOpposite.GetOrderingNumber() < orderingNumber) {

					num += edge.GetPriority();
					num2 += 2f * edge.GetPriority();
					continue;
				}

				num -= edge.GetPriority();
				num2 -= 2f * edge.GetPriority();
			}
		}

		orderingNumber = node2.GetOrderingNumber();

		hMAEdges = node2.GetHMAEdges();

		while (hMAEdges.HasNext()) {

			edge = hMAEdges.Next();

			hMAOpposite = edge.GetHMAOpposite(node2);
			if (hMAOpposite == node3) {

				num += edge.GetPriority();
			} else if (hMAOpposite != node1) {
				if (hMAOpposite.GetOrderingNumber() < orderingNumber) {

					num += edge.GetPriority();

					num2 -= edge.GetPriority();
					continue;
				}

				num -= edge.GetPriority();

				num2 += edge.GetPriority();
			}
		}

		orderingNumber = node3.GetOrderingNumber();

		hMAEdges = node3.GetHMAEdges();

		while (hMAEdges.HasNext()) {

			edge = hMAEdges.Next();

			hMAOpposite = edge.GetHMAOpposite(node3);
			if ((hMAOpposite != node1) && (hMAOpposite != node2)) {
				if (hMAOpposite.GetOrderingNumber() < orderingNumber) {
					num -= 2f * edge.GetPriority();

					num2 -= edge.GetPriority();
				} else {
					num += 2f * edge.GetPriority();

					num2 += edge.GetPriority();
				}
			}
		}
		if (num < num2) {
			if (num < 0f) {

				return 1;
			}
		} else if (num2 < 0f) {

			return -1;
		}

		return 0;

	}

	private Boolean NeedsSwapFromCrossings(HMANode node1, HMANode node2) {
		HMAEdge edge = null;
		HMANode hMAOpposite = null;
		float num = 0f;
		Integer orderingNumber = node1.GetOrderingNumber();
		HMAEdgeIterator hMAEdges = node1.GetHMAEdges();

		while (hMAEdges.HasNext()) {

			edge = hMAEdges.Next();

			hMAOpposite = edge.GetHMAOpposite(node1);
			if (hMAOpposite != node2) {
				if (hMAOpposite.GetOrderingNumber() < orderingNumber) {

					num += edge.GetPriority();
				} else {

					num -= edge.GetPriority();
				}
			}
		}

		orderingNumber = node2.GetOrderingNumber();

		hMAEdges = node2.GetHMAEdges();

		while (hMAEdges.HasNext()) {

			edge = hMAEdges.Next();

			hMAOpposite = edge.GetHMAOpposite(node2);
			if (hMAOpposite != node1) {
				if (hMAOpposite.GetOrderingNumber() < orderingNumber) {

					num -= edge.GetPriority();
				} else {

					num += edge.GetPriority();
				}
			}
		}

		return (num < 0f);

	}

	private void OptimizeSwimLanesForFreeNodes() {
		HGraph graph = super.GetGraph();
		Boolean flag = true;
		Integer num = 10;
		while (flag && (num > 0)) {
			flag = false;
			num--;
			HNodeIterator nodes = graph.GetNodes();

			while (nodes.HasNext()) {
				HNode node = nodes.Next();
				if (node.GetSwimLane() == null) {
					Integer marker = node.GetMarker();
					Integer swimLaneMarker = this.GetSwimLaneMarker(node);
					if (marker != swimLaneMarker) {
						node.SetMarker(swimLaneMarker);
						flag = true;
					}
				}
			}
		}

	}

	private void PropagateMarker(HNode node, Integer marker) {
		if ((marker > 0) && (node.GetMarker() <= 0)) {
			node.SetMarker(marker);
			HSegmentIterator segments = node.GetSegments();

			while (segments.HasNext()) {
				this.PropagateMarker(segments.Next().GetOpposite(node), marker);
			}
		}

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();
		Integer pointsOfStep = (3 * (graph.GetNumberOfLevels() - 1)) + 2;
		super.GetPercController().StartStep(graph._percForCrossReduction,
				pointsOfStep);
		this.CalcSwimLaneOrder();
		this.AllocateDummySwimLanes();
		this.CalcSwimLanesForFreeNodes();
		super.UpdateInfoInLevels();
		super._usePortBaryCenter = false;
		if (graph.GetNumberOfLevels() == 1) {
			this.TreatForwardLevel(null, graph.GetFirstLevel());
		} else {
			this.SweepForward();
			this.SweepBackward();
			this.SweepForward();
		}
		this.CalcCrossings(true);
		super.StoreLevelPositionsInNodes();

	}

	private void SetFinalOrderingNumbers() {
		HGraph graph = super.GetGraph();
		HSwimLane[] sortedSwimLanes = graph.GetSortedSwimLanes();
		Integer numberOfSwimLanes = graph.GetNumberOfSwimLanes();
		for (Integer i = 0; i < numberOfSwimLanes; i++) {
			sortedSwimLanes[i].SetOrderingNumber(2 + (2 * i));
			sortedSwimLanes[i].SetCalcPositionIndex(i);
		}

	}

	private void SetOrderingNumberFromPositionNumbers() {
		IJavaStyleEnumerator swimLanes = super.GetGraph().GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			HSwimLane lane = (HSwimLane) swimLanes.NextElement();
			lane.SetOrderingNumber(lane.GetSpecPositionIndex());
		}

	}

	private void TransferOrderingNumbersFromOrderingGraph() {
		IJavaStyleEnumerator swimLanes = super.GetGraph().GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			HSwimLane lane = (HSwimLane) swimLanes.NextElement();
			lane.SetOrderingNumber(lane.GetOrderingNode().GetOrderingNumber());
			lane.SetOrderingNode(null);
		}

	}

	@Override
	public void TreatBackwardLevel(HLevel prevLevel, HLevel level) {
		super.TreatBackwardLevel(prevLevel, level);
		this.VerifySwimLanes(level);
		level.StoreLevelPositionsInNodes(1, false);

	}

	@Override
	public void TreatForwardLevel(HLevel prevLevel, HLevel level) {
		super.TreatForwardLevel(prevLevel, level);
		this.VerifySwimLanes(level);
		level.StoreLevelPositionsInNodes(1, false);

	}

	private void VerifySpecifiedLanePositionIndex() {
		HGraph graph = super.GetGraph();
		IJavaStyleEnumerator swimLanes = graph.GetSwimLanes();

		while (swimLanes.HasMoreElements()) {
			HSwimLane lane = (HSwimLane) swimLanes.NextElement();
			Integer specPositionIndex = lane.GetSpecPositionIndex();
			Integer orderingNumber = lane.GetOrderingNumber();
			if ((orderingNumber != specPositionIndex)
					&& (specPositionIndex >= 0)) {
				IJavaStyleEnumerator enumerator2 = graph.GetSwimLanes();

				while (enumerator2.HasMoreElements()) {
					HSwimLane lane2 = (HSwimLane) enumerator2.NextElement();
					if (lane2.GetOrderingNumber() == specPositionIndex) {
						lane2.SetOrderingNumber(orderingNumber);
						break;
					}
				}
				lane.SetOrderingNumber(specPositionIndex);
			}
		}

	}

	private void VerifySwimLanes(HLevel level) {
		if (level.GetNodesField() != null) {
			this.CalcAveragePositionOfSwimLanes(level);
			this.CalcSwimLaneSortValue(level);
			super._nodeSortAlg.Sort(level.GetNodesField());
			IJavaStyleEnumerator swimLanes = super.GetGraph().GetSwimLanes();

			while (swimLanes.HasMoreElements()) {
				((HSwimLane) swimLanes.NextElement())
						.MoveToNextLevelDuringCrossingReduction();
			}
		}

	}

}
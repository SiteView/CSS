package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning.*;
import system.*;
import system.Collections.*;

public class CrossingReductionAlgorithm extends HLevelSweepAlgorithm {
	private Integer _bestNumberOfCrossings;

	public Boolean _checkBestPositions = false;

	public HNodeSort _nodeSortAlg;

	public Integer _numberOfSweeps;

	private HRPSolving _solveAlg;

	private Integer _traversalNumber;

	private Boolean _useMedianCrossingValue = false;

	public Boolean _usePortBaryCenter = false;

	private ILOG.Diagrammer.GraphLayout.Internal.HLayout.CalcCrossings calcCrossAlg = new ILOG.Diagrammer.GraphLayout.Internal.HLayout.CalcCrossings();

	public CrossingReductionAlgorithm(HGraph graph) {
		super.Init(graph);
		HierarchicalLayout layout = graph.GetLayout();
		this._nodeSortAlg = new HNodeSort();
		this._solveAlg = new HRPSolving(null, layout, graph.GetPercController());
		this._numberOfSweeps = layout.get_NumberOfLinkCrossingSweeps();
		this._checkBestPositions = layout
				.get_BacktrackCrossingReductionEnabled();
		this._useMedianCrossingValue = layout.get_MedianCrossingValueEnabled();
	}

	public Integer CalcCrossings(Boolean storeInLevels) {
		HLevelIterator levels = super.GetGraph().GetLevels();
		HLevel upperLevel = null;
		Integer num = 0;

		while (levels.HasNext()) {
			HLevel lowerLevel = levels.Next();
			if (upperLevel != null) {
				Integer numberOfCrossings = this.calcCrossAlg
						.GetNumberOfCrossings(upperLevel, lowerLevel);
				num += numberOfCrossings;
				if (storeInLevels) {
					upperLevel.SetNumberOfCrossings(numberOfCrossings);
				}
			}
			upperLevel = lowerLevel;
		}

		return num;

	}

	private void CalcPredBaryCenter(HLevel level) {
		Integer nodesCount = level.GetNodesCount();
		HNodeIterator nodes = level.GetNodes();
		if (!this._usePortBaryCenter) {

			while (nodes.HasNext()) {
				nodes.Next().CalcPredBaryCenter(nodesCount);
			}
		} else {

			while (nodes.HasNext()) {
				nodes.Next().CalcPredBaryCenterForPorts();
			}
		}
		if (this._useMedianCrossingValue) {
			if (nodesCount > 0) {
				double num1 = 1E-05 / ((double) nodesCount);
			}

			nodes = level.GetNodes();

			while (nodes.HasNext()) {
				nodes.Next().AddPredMedian((double) nodesCount);
			}
		}
		if (level != super.GetGraph().GetFirstLevel()) {
			HNode node = null;

			nodes = level.GetNodes();
			float sortValue = 0f;

			while (nodes.HasNext()) {

				node = nodes.Next();
				if (node.GetSegmentsToCount() == 0) {
					node.SetSortValue(sortValue);
				} else {

					sortValue = node.GetSortValue();
				}
			}

			nodes = level.GetNodesBackward();

			while (nodes.HasNext()) {

				node = nodes.Next();
				if (node.GetSegmentsToCount() == 0) {
					node.SetSortValue((sortValue + node.GetSortValue()) / 2f);
				} else {

					sortValue = node.GetSortValue();
				}
			}
		}

	}

	private void CalcSuccBaryCenter(HLevel level) {
		Integer nodesCount = level.GetNodesCount();
		HNodeIterator nodes = level.GetNodes();
		if (!this._usePortBaryCenter) {

			while (nodes.HasNext()) {
				nodes.Next().CalcSuccBaryCenter(nodesCount);
			}
		} else {

			while (nodes.HasNext()) {
				nodes.Next().CalcSuccBaryCenterForPorts();
			}
		}
		if (this._useMedianCrossingValue) {
			if (nodesCount > 0) {
				double num1 = 1E-05 / ((double) nodesCount);
			}

			nodes = level.GetNodes();

			while (nodes.HasNext()) {
				nodes.Next().AddSuccMedian((double) nodesCount);
			}
		}
		if (level != super.GetGraph().GetLastLevel()) {
			HNode node = null;

			nodes = level.GetNodes();
			float sortValue = 0f;

			while (nodes.HasNext()) {

				node = nodes.Next();
				if (node.GetSegmentsFromCount() == 0) {
					node.SetSortValue(sortValue);
				} else {

					sortValue = node.GetSortValue();
				}
			}

			nodes = level.GetNodesBackward();

			while (nodes.HasNext()) {

				node = nodes.Next();
				if (node.GetSegmentsFromCount() == 0) {
					node.SetSortValue((sortValue + node.GetSortValue()) / 2f);
				} else {

					sortValue = node.GetSortValue();
				}
			}
		}

	}

	private void CheckForBestPositions() {
		if (this._checkBestPositions) {
			Integer num = this.CalcCrossings(false);
			if (num < this._bestNumberOfCrossings) {
				this.SaveBestPositions();
				this._bestNumberOfCrossings = num;
			}
		}

	}

	@Override
	public void Clean() {
		super.Clean();
		this._nodeSortAlg = null;

	}

	@Override
	public void InitBackwardFirstLevel(HLevel firstLevel) {
		if (firstLevel.GetConstraintNetwork() != null) {
			this.StorePositionAsSortValue(firstLevel);
			this.ProcessConstraints(firstLevel);
			if (firstLevel.GetNodesField() != null) {
				this._nodeSortAlg.Sort(firstLevel.GetNodesField());
			}
		}
		this.VerifySpecifiedPositionIndex(firstLevel);
		firstLevel.StoreLevelPositionsInNodes(1, false);

	}

	@Override
	public void InitForwardFirstLevel(HLevel firstLevel) {
		if (firstLevel.GetConstraintNetwork() != null) {
			this.StorePositionAsSortValue(firstLevel);
			this.ProcessConstraints(firstLevel);
			if (firstLevel.GetNodesField() != null) {
				this._nodeSortAlg.Sort(firstLevel.GetNodesField());
			}
		}
		this.VerifySpecifiedPositionIndex(firstLevel);
		firstLevel.StoreLevelPositionsInNodes(1, false);

	}

	private Boolean IsOptimal() {

		return (this._checkBestPositions && (this._bestNumberOfCrossings == 0));

	}

	private void ProcessConstraints(HLevel level) {
		HGraph graph = super.GetGraph();
		HRPGraph constraintNetwork = level.GetConstraintNetwork();
		if (constraintNetwork != null) {
			HNode node = null;
			this._solveAlg.Init(constraintNetwork);
			HNodeIterator nodes = level.GetNodes();

			while (nodes.HasNext()) {

				node = nodes.Next();
				graph.GetConstraintNode(node).SetBarycenter(
						node.GetSortValue(), 1f);
			}
			this._solveAlg.Run();

			nodes = level.GetNodes();

			while (nodes.HasNext()) {

				node = nodes.Next();
				node.SetSortValue((float) graph.GetConstraintNode(node)
						.GetPositionNumber());
			}
		}

	}

	private void RestoreBestPositions() {
		if (this._checkBestPositions) {
			HNodeIterator nodes = super.GetGraph().GetNodes();

			while (nodes.HasNext()) {
				nodes.Next().RestoreBestPosition();
			}
			HLevelIterator levels = super.GetGraph().GetLevels();

			while (levels.HasNext()) {
				HLevel level = levels.Next();
				if (level.GetNodesField() != null) {
					this._nodeSortAlg.Sort(level.GetNodesField());
				}
				level.StoreLevelPositionsInNodes(1, false);
			}
		}

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();
		Integer pointsOfStep = ((this._numberOfSweeps + 3) * (graph
				.GetNumberOfLevels() - 1)) + 2;
		if (graph.HasPorts() || graph.HasFixedLinkSides()) {
			pointsOfStep += 2 * (graph.GetNumberOfLevels() - 1);
		}
		super.GetPercController().StartStep(graph._percForCrossReduction,
				pointsOfStep);
		this.UnwindDisconnectedParts();
		this.UpdateInfoInLevels();
		this._bestNumberOfCrossings = 0x7fffffff;
		Boolean flag = false;
		if (graph.HasPorts() || graph.HasFixedLinkSides()) {
			this._usePortBaryCenter = true;
			this.SweepForward();
			flag = true;

			if (!this.IsOptimal()) {
				this.SweepBackward();
			}
		}
		this._usePortBaryCenter = false;
		if (graph.GetNumberOfLevels() == 1) {
			this.TreatForwardLevel(null, graph.GetFirstLevel());
		} else {
			for (Integer i = 0; i < this._numberOfSweeps; i++) {
				if (flag && this.IsOptimal()) {
					break;
				}
				if ((i % 2) == 0) {
					this.SweepForward();
				} else {
					this.SweepBackward();
				}
				flag = true;
			}
		}
		this.RestoreBestPositions();
		this.CalcCrossings(true);
		this.StoreLevelPositionsInNodes();

	}

	private void SaveBestPositions() {
		HNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().SaveBestPosition();
		}

	}

	public void StoreLevelPositionsInNodes() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			levels.Next().StoreLevelPositionsInNodes(0, false);
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void StorePositionAsSortValue(HLevel level) {
		HNodeIterator nodes = level.GetNodes();
		Integer num = 1;

		while (nodes.HasNext()) {
			nodes.Next().SetSortValue((float) num++);
		}

	}

	@Override
	public void SweepBackward() {
		super.SweepBackward();
		this.CheckForBestPositions();

	}

	@Override
	public void SweepForward() {
		super.SweepForward();
		this.CheckForBestPositions();

	}

	private void TraverseConnectedComponent(HNode node) {
		Stack stack = new Stack();
		node.SetSortValue((float) this._traversalNumber++);
		stack.Push(node);
		while (stack.get_Count() != 0) {
			HNode to = null;
			node = (HNode) stack.Pop();
			HSegmentIterator segmentsFrom = node.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {

				to = segmentsFrom.Next().GetTo();
				if (to.GetSortValue() < 0f) {
					to.SetSortValue((float) this._traversalNumber++);
					stack.Push(to);
				}
			}

			segmentsFrom = node.GetSegmentsTo();

			while (segmentsFrom.HasNext()) {

				to = segmentsFrom.Next().GetFrom();
				if (to.GetSortValue() < 0f) {
					to.SetSortValue((float) this._traversalNumber++);
					stack.Push(to);
				}
			}
		}

	}

	@Override
	public void TreatBackwardLevel(HLevel prevLevel, HLevel level) {
		this.CalcSuccBaryCenter(level);
		this.ProcessConstraints(level);
		if (level.GetNodesField() != null) {
			this._nodeSortAlg.Sort(level.GetNodesField());
		}
		this.VerifySpecifiedPositionIndex(level);
		level.StoreLevelPositionsInNodes(1, false);

	}

	@Override
	public void TreatForwardLevel(HLevel prevLevel, HLevel level) {
		this.CalcPredBaryCenter(level);
		this.ProcessConstraints(level);
		if (level.GetNodesField() != null) {
			this._nodeSortAlg.Sort(level.GetNodesField());
		}
		this.VerifySpecifiedPositionIndex(level);
		level.StoreLevelPositionsInNodes(1, false);

	}

	private void UnwindDisconnectedParts() {
		this._traversalNumber = 1;
		HGraph graph = super.GetGraph();
		HNodeIterator nodes = graph.GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().SetSortValue(-1f);
		}

		nodes = graph.GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			if (node.GetSortValue() < 0f) {
				this.TraverseConnectedComponent(node);
			}
		}
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			HLevel level = levels.Next();
			if (level.GetNodesField() != null) {
				this._nodeSortAlg.Sort(level.GetNodesField());
			}
			super.LayoutStepPerformed();
		}

	}

	public void UpdateInfoInLevels() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			levels.Next().UpdateInfo();
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void VerifySpecifiedPositionIndex(HLevel level) {
		Integer nodesCount = level.GetNodesCount();
		if ((nodesCount > 0) && level.HasNodesWithSpecPos()) {
			HNodeIterator nodes = level.GetNodes();
			Integer index = 0;
			Integer num5 = level.GetNodesCount() + 1;

			while (nodes.HasNext()) {
				HNode node = nodes.Next();
				Integer specPositionInLevel = node.GetSpecPositionInLevel();
				Integer num4 = num5;
				while (((specPositionInLevel != index) && (specPositionInLevel >= 0))
						&& ((specPositionInLevel < nodesCount) && (num4 > 0))) {
					num4--;
					HNode node2 = level.GetNodesField()[specPositionInLevel];
					level.GetNodesField()[specPositionInLevel] = node;
					level.GetNodesField()[index] = node2;

					specPositionInLevel = node2.GetSpecPositionInLevel();
				}
				index++;
			}
		}

	}

}
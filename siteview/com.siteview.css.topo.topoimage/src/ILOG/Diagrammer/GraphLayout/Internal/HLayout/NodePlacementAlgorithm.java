package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;
import system.Math;

public final class NodePlacementAlgorithm extends HGraphAlgorithm {
	public Integer _actRegionNumberNormNodes;

	public Integer _actRegionNumberPrioNodes;

	public float _actRegionSumNormVectors;

	public float _actRegionSumPrioVectors;

	private HBooleanNodeMarker _backwardHighPriorityNodeMarker;

	private Integer _edgeFlowDirection;

	private HBooleanNodeMarker _forwardHighPriorityNodeMarker;

	private float _fracEdges = 1f;

	private float _fracOldPos;

	private Boolean _incrementalMode = false;

	private float _incrRange;

	private float _initialCenter;

	public HNode _lastRegionEndMarker;

	public Integer _lastRegionNumberNormNodes;

	public Integer _lastRegionNumberPrioNodes;

	public float _lastRegionSumNormVectors;

	public float _lastRegionSumPrioVectors;

	private Integer _levelFlowDirection;

	private float _maxApertureAngle;

	private double _maxApertureAngleInRadians;

	public StraightenChainAlgorithm _straightenAlg;

	public SwimLanePlacementAlgorithm _swimLaneAlg;

	public NodePlacementAlgorithm(HGraph graph,
			SwimLanePlacementAlgorithm swimLaneAlg) {
		super.Init(graph);
		this._swimLaneAlg = swimLaneAlg;
		this._straightenAlg = new StraightenChainAlgorithm(graph, false);
		this._forwardHighPriorityNodeMarker = new HBooleanNodeMarker(graph);
		this._backwardHighPriorityNodeMarker = new HBooleanNodeMarker(graph);

		this._levelFlowDirection = graph.GetLevelFlow();

		this._edgeFlowDirection = graph.GetEdgeFlow();
		HierarchicalLayout layout = graph.GetLayout();

		if ((graph.IsIncrementalMode() && !graph
				.IsCrossingReductionDuringIncremental())
				&& graph.IsIncrementalAbsoluteLevelPositioning()) {
			this._incrementalMode = true;
			this._fracOldPos = layout
					.get_IncrementalAbsoluteLevelPositionTendency() / 100f;
			this._fracEdges = 1f - this._fracOldPos;
			this._incrRange = layout
					.get_IncrementalAbsoluteLevelPositionRange();
		} else {
			this._incrementalMode = false;
			this._fracOldPos = 0f;
			this._fracEdges = 1f;
			this._incrRange = 0f;
		}
		this._maxApertureAngle = layout.get_MaxInterLevelApertureAngle();

		this._maxApertureAngleInRadians = Math
				.Abs((double) ((((double) this._maxApertureAngle) / 180.0) * 3.1415926535897931));
	}

	private void AddToActRegionBackward(HNode node) {
		this._actRegionSumNormVectors += node.GetMoveVector();
		;
		this._actRegionNumberNormNodes++;

		if (this.HasBackwardHighPriority(node)) {
			this._actRegionSumPrioVectors += node.GetMoveVector();
			;
			this._actRegionNumberPrioNodes++;
		}

	}

	private void AddToActRegionForward(HNode node) {
		this._actRegionSumNormVectors += node.GetMoveVector();
		this._actRegionNumberNormNodes++;

		if (this.HasForwardHighPriority(node)) {
			this._actRegionSumPrioVectors += node.GetMoveVector();
			this._actRegionNumberPrioNodes++;
		}

	}

	private float CalcBackwardMoveVectors(HLevel level) {
		float num = 0f;
		HNodeIterator nodes = level.GetNodes();
		Boolean flag = false;

		while (nodes.HasNext()) {
			float num2 = nodes.Next().CalcBackwardMoveVector(
					this._levelFlowDirection, this._fracEdges,
					this._fracOldPos, this._incrRange);
			if (num2 == Float.MAX_VALUE) {
				flag = true;
			} else {
				num += num2;
			}
		}
		if (flag) {
			this.CorrectInvalidMoveVectors(level);
		}

		return num;

	}

	private float CalcForwardMoveVectors(HLevel level) {
		float num = 0f;
		HNodeIterator nodes = level.GetNodes();
		Boolean flag = false;

		while (nodes.HasNext()) {
			float num2 = nodes.Next().CalcForwardMoveVector(
					this._levelFlowDirection, this._fracEdges,
					this._fracOldPos, this._incrRange);
			if (num2 == Float.MAX_VALUE) {
				flag = true;
			} else {
				num += num2;
			}
		}
		if (flag) {
			this.CorrectInvalidMoveVectors(level);
		}

		return num;

	}

	private void CalcInitialCenter() {
		HNodeIterator nodes = super.GetGraph().GetNodes();
		float maxValue = Float.MAX_VALUE;
		float minValue = Float.MIN_VALUE;
		Boolean flag = false;

		while (nodes.HasNext()) {
			float oldCoordInLevelFlow = nodes.Next().GetOldCoordInLevelFlow();
			if (oldCoordInLevelFlow != Float.MAX_VALUE) {
				if (oldCoordInLevelFlow < maxValue) {
					maxValue = oldCoordInLevelFlow;
				}
				if (oldCoordInLevelFlow > minValue) {
					minValue = oldCoordInLevelFlow;
				}
				flag = true;
			}
		}
		if (flag) {
			this._initialCenter = 0.5f * (maxValue + minValue);
		} else {
			this._initialCenter = 0f;
		}
		if (((this._initialCenter == Float.NaN) || (this._initialCenter == Float.POSITIVE_INFINITY))
				|| (this._initialCenter == Float.NEGATIVE_INFINITY)) {
			this._initialCenter = 0f;
		}

	}

	private float CalcLastPassBackwardMoveVectors(HLevel level) {
		float num = 0f;
		HNodeIterator nodes = level.GetNodes();
		Boolean flag = false;

		while (nodes.HasNext()) {
			float num2 = nodes.Next().CalcBackwardSpecialMoveVector(
					this._levelFlowDirection, this._fracEdges,
					this._fracOldPos, this._incrRange);
			if (num2 == Float.MAX_VALUE) {
				flag = true;
			} else {
				num += num2;
			}
		}
		if (flag) {
			this.CorrectInvalidMoveVectors(level);
		}

		return num;

	}

	private float CalcLastPassForwardMoveVectors(HLevel level) {
		float num = 0f;
		HNodeIterator nodes = level.GetNodes();
		Boolean flag = false;

		while (nodes.HasNext()) {
			float num2 = nodes.Next().CalcForwardSpecialMoveVector(
					this._levelFlowDirection, this._fracEdges,
					this._fracOldPos, this._incrRange);
			if (num2 == Float.MAX_VALUE) {
				flag = true;
			} else {
				num += num2;
			}
		}
		if (flag) {
			this.CorrectInvalidMoveVectors(level);
		}

		return num;

	}

	private void CalcOffsetForMinDist() {
		HGraph graph = super.GetGraph();
		Integer levelFlow = graph.GetLevelFlow();
		float offset = 0.1f * graph.GetMinDistBetweenNodes(levelFlow);
		if (offset > 5f) {
			offset = 5f;
		}
		graph.SetOffsetForMinDist(levelFlow, offset);

	}

	@Override
	public void Clean() {
		super.Clean();
		this._lastRegionEndMarker = null;
		if (this._forwardHighPriorityNodeMarker != null) {
			this._forwardHighPriorityNodeMarker.Clean();
		}
		this._forwardHighPriorityNodeMarker = null;
		if (this._backwardHighPriorityNodeMarker != null) {
			this._backwardHighPriorityNodeMarker.Clean();
		}
		this._backwardHighPriorityNodeMarker = null;

	}

	private void CondenseLevels(HLevel level1, HLevel level2, float diff) {
		Integer index = this._edgeFlowDirection;
		Integer levelJustification = super.GetGraph().GetLevelJustification();
		Integer num3 = 1;
		HLevelIterator levels = super.GetGraph().GetLevels();
		levels.Init(level1);
		levels.Next();

		while (levels.HasNext()) {
			HLevel level = levels.Next();
			if (level == level2) {

				return;
			}
			float minCoord = level.GetCoord(index) + (num3 * diff);
			float maxCoord = minCoord + level.GetSize(index);
			level.SetCoord(index, minCoord, maxCoord, levelJustification);
			num3++;
		}

	}

	private void CondenseNodes(HLevel level, HNode node1, HNode node2,
			float diff) {
		Integer num = 1;
		HNodeIterator nodes = level.GetNodes();
		nodes.Init(node1);
		nodes.Next();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			if (node == node2) {

				return;
			}
			node.ShiftCoord(this._levelFlowDirection, num * diff);
			num++;
		}

	}

	private void CorrectInvalidMoveVectors(HLevel level) {
		HNode node = null;
		HNode node2 = null;
		HNodeIterator nodes = level.GetNodes();
		for (node2 = null; nodes.HasNext(); node2 = node) {

			node = nodes.Next();
			node.SetInvalidMoveVectorMarker(-1);
			if (node.GetMoveVector() == Float.MAX_VALUE) {
				if (this.Touching(node2, node)
						&& (node2.GetMoveVector() >= 0.0)) {
					node.SetMoveVector(node2.GetMoveVector());
					node.SetInvalidMoveVectorMarker(1);
				} else {
					node.SetMoveVector(0f);
					node.SetInvalidMoveVectorMarker(0);
				}
			}
		}
		node2 = null;

		nodes = level.GetNodesBackward();

		while (nodes.HasNext()) {

			node = nodes.Next();
			if (((node.GetInvalidMoveVectorMarker() != -1) && this.Touching(
					node2, node)) && (node2.GetMoveVector() <= 0.0)) {
				node.SetMoveVector((node.GetMoveVector() + node2
						.GetMoveVector())
						/ ((float) (1 + node.GetInvalidMoveVectorMarker())));
			}
			node2 = node;
		}

	}

	private void CreateInialRegions(HLevel level) {
		HNodeIterator nodes = level.GetNodes();
		HNode node2 = null;

		while (nodes.HasNext()) {
			HNode node = nodes.Next();

			if (node.IsFixedForIncremental(this._levelFlowDirection)) {
				node.SetRegionEndMarker(2);
				if (node2 != null) {
					node2.SetRegionEndMarker(2);
				}
			} else {
				node.SetRegionEndMarker(0);
				if (node2 != null) {

					if (this.Touching(node2, node)) {

						if ((node2.GetMoveVector() < node.GetMoveVector())
								&& !node2
										.IsFixedForIncremental(this._levelFlowDirection)) {
							node2.SetRegionEndMarker(1);
						}
					} else {
						node2.SetRegionEndMarker(2);
					}
				}
			}
			node2 = node;
		}
		if (node2 != null) {
			node2.SetRegionEndMarker(2);
		}

	}

	private void EvaluateMoveVectors(HLevel level, float dir) {
		if (level.GetNodesCount() > 0) {
			if (dir <= 0.0) {
				this.EvaluateNegativeMoveVectors(level);
			} else {
				this.EvaluatePositiveMoveVectors(level);
			}
			if (dir <= 0.0) {
				this.EvaluatePositiveMoveVectors(level);
			} else {
				this.EvaluateNegativeMoveVectors(level);
			}
		}

	}

	private void EvaluateNegativeMoveVectors(HLevel level) {
		HNode node2 = null;
		float minValue = Float.MIN_VALUE;
		HNodeIterator nodes = level.GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			float moveVector = node.GetMoveVector();
			float coord = node.GetCoord(this._levelFlowDirection);
			if (node2 != null) {

				minValue += this.GetMinDist(node2, node);
			}
			if (moveVector < 0f) {
				float num3 = Math.Max(coord + moveVector, minValue);
				if (num3 < coord) {
					node.SetCoord(this._levelFlowDirection, num3);
					coord = num3;
				}
			}
			minValue = coord + node.GetSize(this._levelFlowDirection);
			node2 = node;
		}

	}

	private void EvaluatePositiveMoveVectors(HLevel level) {
		HNode node2 = null;
		float maxValue = Float.MAX_VALUE;
		HNodeIterator nodesBackward = level.GetNodesBackward();

		while (nodesBackward.HasNext()) {
			HNode node = nodesBackward.Next();
			float moveVector = node.GetMoveVector();
			float coord = node.GetCoord(this._levelFlowDirection);
			if (node2 != null) {
				maxValue -= this.GetMinDist(node2, node)
						+ node.GetSize(this._levelFlowDirection);
			}
			if (moveVector > 0f) {
				float num3 = Math.Min(coord + moveVector, maxValue);
				if (num3 > coord) {
					node.SetCoord(this._levelFlowDirection, num3);
					coord = num3;
				}
			}
			maxValue = coord;
			node2 = node;
		}

	}

	private float GetActRegionMoveVector() {
		if (this._actRegionNumberPrioNodes > 0) {

			return (this._actRegionSumPrioVectors / ((float) this._actRegionNumberPrioNodes));
		}

		return (this._actRegionSumNormVectors / ((float) this._actRegionNumberNormNodes));

	}

	private float GetDistToEnsureApertureAngle(HSegment seg) {
		HNode from = seg.GetFrom();
		HNode to = seg.GetTo();
		float coord = from.GetCoord(this._levelFlowDirection);
		float num2 = to.GetCoord(this._levelFlowDirection);
		float num3 = Math.Abs((float) (coord - num2));
		if (num3 <= 0.1) {

			return 0f;
		}

		return (float) (((double) num3) / Math
				.Tan(this._maxApertureAngleInRadians));

	}

	private float GetLastRegionMoveVector() {
		if (this._lastRegionNumberPrioNodes > 0) {

			return (this._lastRegionSumPrioVectors / ((float) this._lastRegionNumberPrioNodes));
		}

		return (this._lastRegionSumNormVectors / ((float) this._lastRegionNumberNormNodes));

	}

	private float GetMinDist(HNode node1, HNode node2) {

		return super.GetGraph().GetMinDist(this._levelFlowDirection, node1,
				node2);

	}

	private float GetMinDistToNextLevel(HLevel level) {
		Integer direction = this._edgeFlowDirection;
		float minDistBetweenNodes = super.GetGraph().GetMinDistBetweenNodes(
				direction);
		HSegmentIterator segmentsFrom = level.GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {
			HSegment seg = segmentsFrom.Next();

			minDistBetweenNodes = Math.Max(minDistBetweenNodes,
					this.GetDistToEnsureApertureAngle(seg));
		}

		return minDistBetweenNodes;

	}

	private float GetMinDistToPrevLevel(HLevel level) {
		Integer direction = this._edgeFlowDirection;
		float minDistBetweenNodes = super.GetGraph().GetMinDistBetweenNodes(
				direction);
		HSegmentIterator segmentsTo = level.GetSegmentsTo();

		while (segmentsTo.HasNext()) {
			HSegment seg = segmentsTo.Next();

			minDistBetweenNodes = Math.Max(minDistBetweenNodes,
					this.GetDistToEnsureApertureAngle(seg));
		}

		return minDistBetweenNodes;

	}

	private Boolean HasBackwardHighPriority(HNode node) {

		return this._backwardHighPriorityNodeMarker.Get(node);

	}

	private Boolean HasForwardHighPriority(HNode node) {

		return this._forwardHighPriorityNodeMarker.Get(node);

	}

	private Boolean HasSwimLanes() {

		return super.GetGraph().HasSwimLanes();

	}

	private Boolean HighPriorityAllowed(HNode node) {

		return (!this._incrementalMode || (node.GetOldCoordInLevelFlow() == Float.MAX_VALUE));

	}

	private static Boolean IsBackwardHighPriorityCandidate(HNode node) {
		if (node.GetSegmentsFromCount() != 1) {

			return false;
		}
		HNode to = node.GetFirstSegmentFrom().GetTo();
		if (node.GetSwimLane() != to.GetSwimLane()) {

			return false;
		}

		return (to.GetSegmentsToCount() == 1);

	}

	private Boolean IsForwardHighPriorityCandidate(HNode node) {
		if (node.GetSegmentsToCount() != 1) {

			return false;
		}
		HNode from = node.GetFirstSegmentTo().GetFrom();
		if (node.GetSwimLane() != from.GetSwimLane()) {

			return false;
		}

		return (from.GetSegmentsFromCount() == 1);

	}

	private void MarkHighPriorityNodes() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			this.MarkHighPriorityNodes(levels.Next());
		}

	}

	private void MarkHighPriorityNodes(HLevel level) {
		if (level.GetNodesCount() != 0) {
			HNode node = null;
			HNodeIterator nodes = level.GetNodes();

			while (nodes.HasNext()) {

				node = nodes.Next();

				if (this.HighPriorityAllowed(node)) {

					if (this.IsForwardHighPriorityCandidate(node)) {
						this._forwardHighPriorityNodeMarker.Set(node, true);
					}

					if (IsBackwardHighPriorityCandidate(node)) {
						this._backwardHighPriorityNodeMarker.Set(node, true);
					}
				}
			}

			nodes = level.GetNodes();
			Boolean flag = true;
			Boolean flag2 = true;

			while (nodes.HasNext()) {

				node = nodes.Next();
				if (flag) {

					if (this.IsForwardHighPriorityCandidate(node)) {
						this._forwardHighPriorityNodeMarker.Set(node, false);
					} else {
						flag = false;
					}
				}
				if (flag2) {

					if (IsBackwardHighPriorityCandidate(node)) {
						this._backwardHighPriorityNodeMarker.Set(node, false);
					} else {
						flag2 = false;
					}
				}
				if (!flag && !flag2) {
					break;
				}
			}

			nodes = level.GetNodesBackward();
			flag = true;
			flag2 = true;

			while (nodes.HasNext()) {

				node = nodes.Next();
				if (flag) {

					if (this.IsForwardHighPriorityCandidate(node)) {
						this._forwardHighPriorityNodeMarker.Set(node, false);
					} else {
						flag = false;
					}
				}
				if (flag2) {

					if (IsBackwardHighPriorityCandidate(node)) {
						this._backwardHighPriorityNodeMarker.Set(node, false);
					} else {
						flag2 = false;
					}
				}
				if (!flag && !flag2) {
					break;
				}
			}
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void PendulumSweepBackward() {
		HGraph graph = super.GetGraph();
		if (graph.GetNumberOfLevels() > 1) {
			HLevelIterator levelsBackward = graph.GetLevelsBackward();
			float num3 = 0f;
			levelsBackward.Prev();

			while (levelsBackward.HasPrev()) {
				HLevel level = levelsBackward.Prev();
				float dir = this.CalcBackwardMoveVectors(level);
				float num2 = (dir >= 0f) ? dir : -dir;
				num3 = -1f;
				for (Integer i = 0; (i == 0)
						|| (((i < 100) && (num2 > 1f)) && (num2 < num3)); i++) {
					this.UnifyBackwardRegions(level);
					this.EvaluateMoveVectors(level, dir);

					dir = this.CalcBackwardMoveVectors(level);
					num3 = num2;
					num2 = (dir >= 0f) ? dir : -dir;
				}
				super.GetPercController().AddPoints(1);
				super.LayoutStepPerformed();
			}
		}

	}

	private void PendulumSweepForward() {
		HGraph graph = super.GetGraph();
		if (graph.GetNumberOfLevels() > 1) {
			HLevelIterator levels = graph.GetLevels();
			float num3 = 0f;
			levels.Next();

			while (levels.HasNext()) {
				HLevel level = levels.Next();
				float dir = this.CalcForwardMoveVectors(level);
				float num2 = (dir >= 0f) ? dir : -dir;
				for (Integer i = 0; (i == 0)
						|| (((i < 100) && (num2 > 1f)) && (num2 < num3)); i++) {
					this.UnifyForwardRegions(level);
					this.EvaluateMoveVectors(level, dir);

					dir = this.CalcForwardMoveVectors(level);
					num3 = num2;
					num2 = (dir >= 0f) ? dir : -dir;
				}
				super.GetPercController().AddPoints(1);
				super.LayoutStepPerformed();
			}
		}

	}

	private void PendulumSweepWidestLevel() {
		HGraph graph = super.GetGraph();
		if (graph.GetNumberOfLevels() > 1) {
			HLevel level = null;
			float num5 = 0;
			float num6 = 0;
			Integer num8 = null;
			HLevelIterator levels = graph.GetLevels();
			HLevel level2 = null;
			float num = -1f;
			float num7 = 0f;

			while (levels.HasNext()) {
				float num2 = 0;

				level = levels.Next();
				if (level.GetNodesCount() > 0) {
					HNode node = level.GetNode(0);
					HNode node2 = level.GetNode(level.GetNodesCount() - 1);
					float center = node.GetCenter(this._levelFlowDirection);
					num2 = node2.GetCenter(this._levelFlowDirection) - center;
				} else {
					num2 = 0f;
				}
				if (num2 > num) {
					level2 = level;
					num = num2;
				}
				super.GetPercController().AddPoints(1);
				super.LayoutStepPerformed();
			}
			levels.Init(level2);

			if (levels.HasNext()) {
				levels.Next();
			}

			while (levels.HasNext()) {

				level = levels.Next();

				num5 = this.CalcLastPassForwardMoveVectors(level);
				num6 = (num5 >= 0f) ? num5 : -num5;
				num8 = 0;
				while ((num8 == 0)
						|| (((num8 < 100) && (num6 > 1f)) && (num6 < num7))) {
					this.UnifyForwardRegions(level);
					this.EvaluateMoveVectors(level, num5);

					num5 = this.CalcLastPassForwardMoveVectors(level);
					num7 = num6;
					num6 = (num5 >= 0f) ? num5 : -num5;
					num8++;
				}
				super.GetPercController().AddPoints(1);
				super.LayoutStepPerformed();
			}
			levels.Init(level2);

			if (levels.HasPrev()) {
				levels.Prev();
			}

			while (levels.HasPrev()) {

				level = levels.Prev();

				num5 = this.CalcLastPassBackwardMoveVectors(level);
				num6 = (num5 >= 0f) ? num5 : -num5;
				for (num8 = 0; (num8 == 0)
						|| (((num8 < 100) && (num6 > 1f)) && (num6 < num7)); num8++) {
					this.UnifyBackwardRegions(level);
					this.EvaluateMoveVectors(level, num5);

					num5 = this.CalcLastPassBackwardMoveVectors(level);
					num7 = num6;
					num6 = (num5 >= 0f) ? num5 : -num5;
				}
				super.GetPercController().AddPoints(1);
				super.LayoutStepPerformed();
			}
		}

	}

	private void PlaceInEdgeFlow() {
		Integer direction = this._edgeFlowDirection;
		HLevelIterator levels = super.GetGraph().GetLevels();
		HLevel level2 = null;
		HLevel level3 = null;
		float minCoord = 0f;
		float minDistBetweenNodes = super.GetGraph().GetMinDistBetweenNodes(
				direction);
		Integer levelJustification = super.GetGraph().GetLevelJustification();
		Integer num7 = 0;

		while (levels.HasNext()) {
			float largestSize = 0;
			HLevel fixedLevel = levels.Next();
			fixedLevel.UpdateInfo();

			if (fixedLevel.HasFixedNodes(direction)) {
				fixedLevel.UpdateBounds(direction, true);
				float delta = fixedLevel.GetCoord(direction) - minCoord;

				minCoord = fixedLevel.GetCoord(direction);

				largestSize = fixedLevel.GetLargestSize(direction);
				if (largestSize < fixedLevel.GetSize(direction)) {

					largestSize = fixedLevel.GetSize(direction);
				}
				fixedLevel.SetCoord(direction, minCoord,
						minCoord + largestSize, levelJustification);
				if (level2 != level3) {
					if (level3 == null) {
						this.ShiftLevels(fixedLevel, delta);
					} else {
						this.CondenseLevels(level3, fixedLevel, delta
								/ ((float) (num7 + 1)));
					}
				}
				level3 = fixedLevel;
				num7 = 0;
			} else {

				largestSize = fixedLevel.GetLargestSize(direction);
				fixedLevel.SetCoord(direction, minCoord,
						minCoord + largestSize, levelJustification);
				num7++;
			}
			minCoord = fixedLevel.GetCoord(direction)
					+ fixedLevel.GetSize(direction);
			minCoord += minDistBetweenNodes;
			level2 = fixedLevel;
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void PlaceInEdgeFlowWithApertureAngle() {
		if (this._maxApertureAngle < 89f) {
			HLevel level = null;
			float largestSize = 0;
			float minDistToNextLevel = 0;
			Integer index = this._edgeFlowDirection;
			HLevelIterator levels = super.GetGraph().GetLevels();
			HLevel level2 = null;
			HLevel level3 = null;
			HLevel level4 = null;
			Integer levelJustification = super.GetGraph()
					.GetLevelJustification();

			while (levels.HasNext()) {

				level = levels.Next();
				level2 = level;
				level.UpdateInfo();

				if (level.HasFixedNodes(index)) {
					level4 = level;
					if (level3 == null) {
						level3 = level;
					}
				}
			}
			if (level3 == null) {
				level3 = level4 = level2;
			}

			levels = super.GetGraph().GetLevels();
			Boolean flag = false;
			float minCoord = 0f;

			while (levels.HasNext()) {

				level = levels.Next();
				if (flag) {

					largestSize = level.GetLargestSize(index);
					level.SetCoord(index, minCoord, minCoord + largestSize,
							levelJustification);
				}
				if (level == level4) {
					flag = true;
				}
				if (flag) {

					minDistToNextLevel = this.GetMinDistToNextLevel(level);
					minCoord = (level.GetCoord(index) + level.GetSize(index))
							+ minDistToNextLevel;
				}
			}

			levels = super.GetGraph().GetLevelsBackward();
			flag = false;

			while (levels.HasPrev()) {

				level = levels.Prev();
				if (flag) {

					largestSize = level.GetLargestSize(index);
					level.SetCoord(index, minCoord - largestSize, minCoord,
							levelJustification);
				}
				if (level == level3) {
					flag = true;
				}
				if (flag) {

					minDistToNextLevel = this.GetMinDistToPrevLevel(level);
					minCoord = level.GetCoord(index) - minDistToNextLevel;
				}
			}
		}

	}

	private void PlaceInitial() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			this.PlaceInitial(levels.Next());
		}

	}

	private void PlaceInitial(HLevel level) {
		if (level.GetNodesCount() >= 0) {
			HNodeIterator nodes = level.GetNodes();
			HNode node2 = null;
			HNode node3 = null;
			float coord = 0f;
			Integer num2 = 0;

			while (nodes.HasNext()) {
				HNode node = nodes.Next();
				if (node2 != null) {

					coord += this.GetMinDist(node2, node);
				}

				if (node.IsFixedForIncremental(this._levelFlowDirection)) {
					float delta = node.GetCoord(this._levelFlowDirection)
							- coord;
					if (node2 != node3) {
						if (node3 == null) {
							this.ShiftNodes(level, node, delta);
						} else {
							this.CondenseNodes(level, node3, node, delta
									/ ((float) (num2 + 1)));
						}
					}
					node3 = node;
					num2 = 0;
				} else {
					node.SetCoord(this._levelFlowDirection, coord);
					num2++;
				}
				coord = node.GetCoord(this._levelFlowDirection)
						+ node.GetSize(this._levelFlowDirection);
				node2 = node;
			}
		}
		level.UpdateBounds(this._levelFlowDirection, false);
		super.GetPercController().AddPoints(1);
		super.LayoutStepPerformed();

	}

	private void PlaceInLevelFlow() {
		HGraph graph = super.GetGraph();
		Integer levelFlow = graph.GetLevelFlow();
		this.MarkHighPriorityNodes();
		if (this._incrementalMode) {
			this.CalcInitialCenter();
		} else {
			this._initialCenter = 0f;
		}
		if (graph.GetLayout().get_LinkStraighteningEnabled()) {
			this.CalcOffsetForMinDist();
		}
		this.PlaceInitial();
		this.ShiftToCenter();
		for (Integer i = 0; i < 2; i++) {
			this.PendulumSweepForward();
			this.VerifySwimLanesForwardSweep();
			this.PendulumSweepBackward();
			this.VerifySwimLanesBackwardSweep();
		}
		this.PendulumSweepWidestLevel();
		this._straightenAlg.Run();

		if (!this.HasSwimLanes()) {
			graph.SetOffsetForMinDist(levelFlow, 0f);
		}
		this._swimLaneAlg.ShiftTogether(false);
		this.VerifySwimLanesAtEnd();
		this.UpdateBounds();

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();
		Integer pointsOfStep = (10 * graph.GetNumberOfLevels()) - 5;
		super.GetPercController().StartStep(graph._percForNodePlacement,
				pointsOfStep);
		this.PlaceInEdgeFlow();
		this.PlaceInLevelFlow();
		this.PlaceInEdgeFlowWithApertureAngle();

	}

	private void SetBackwardRegionMoveVector(HLevel level) {
		HNode node = null;
		HNodeIterator nodes = level.GetNodes();
		this.StartActRegion();

		while (nodes.HasNext()) {

			node = nodes.Next();
			this.AddToActRegionBackward(node);
			if (node.GetRegionEndMarker() != 0) {
				node.SetMoveVector(this.GetActRegionMoveVector());
				this.StartActRegion();
			}
		}

		nodes = level.GetNodesBackward();
		float moveVector = 0f;

		while (nodes.HasNext()) {

			node = nodes.Next();
			if (node.GetRegionEndMarker() != 0) {

				moveVector = node.GetMoveVector();
			} else {
				node.SetMoveVector(moveVector);
			}
		}

	}

	private void SetForwardRegionMoveVector(HLevel level) {
		HNode node = null;
		HNodeIterator nodes = level.GetNodes();
		this.StartActRegion();

		while (nodes.HasNext()) {

			node = nodes.Next();
			this.AddToActRegionForward(node);
			if (node.GetRegionEndMarker() != 0) {
				node.SetMoveVector(this.GetActRegionMoveVector());
				this.StartActRegion();
			}
		}

		nodes = level.GetNodesBackward();
		float moveVector = 0f;

		while (nodes.HasNext()) {

			node = nodes.Next();
			if (node.GetRegionEndMarker() != 0) {

				moveVector = node.GetMoveVector();
			} else {
				node.SetMoveVector(moveVector);
			}
		}

	}

	private void ShiftLevels(HLevel fixedLevel, float delta) {
		Integer index = this._edgeFlowDirection;
		Integer levelJustification = super.GetGraph().GetLevelJustification();
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			HLevel level = levels.Next();
			if (level == fixedLevel) {

				return;
			}
			float minCoord = level.GetCoord(index) + delta;
			float maxCoord = minCoord + level.GetSize(index);
			level.SetCoord(index, minCoord, maxCoord, levelJustification);
		}

	}

	private void ShiftNodes(HLevel level, HNode fixedNode, float delta) {
		HNodeIterator nodes = level.GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			if (node == fixedNode) {

				return;
			}
			node.ShiftCoord(this._levelFlowDirection, delta);
		}

	}

	private void ShiftToCenter() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			this.ShiftToCenter(levels.Next());
		}

	}

	private void ShiftToCenter(HLevel level) {
		level.UpdateInfo();

		if ((level.GetNodesCount() > 0)
				&& !level.HasFixedNodes(this._levelFlowDirection)) {
			HNodeIterator nodes = level.GetNodes();
			float coord = level.GetCoord(this._levelFlowDirection);
			float num2 = coord + level.GetSize(this._levelFlowDirection);
			float num3 = this._initialCenter - (0.5f * (coord + num2));

			while (nodes.HasNext()) {
				nodes.Next().ShiftCoord(this._levelFlowDirection, num3);
			}
			level.SetCoord(this._levelFlowDirection, coord + num3);
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void StartActRegion() {
		this._actRegionSumNormVectors = 0f;
		this._actRegionSumPrioVectors = 0f;
		this._actRegionNumberNormNodes = 0;
		this._actRegionNumberPrioNodes = 0;

	}

	private void StartNewRegion(HNode node) {
		if (node.GetRegionEndMarker() == 1) {
			this._lastRegionEndMarker = node;
			this._lastRegionSumNormVectors = this._actRegionSumNormVectors;
			this._lastRegionSumPrioVectors = this._actRegionSumPrioVectors;
			this._lastRegionNumberNormNodes = this._actRegionNumberNormNodes;
			this._lastRegionNumberPrioNodes = this._actRegionNumberPrioNodes;
		} else {
			this._lastRegionEndMarker = null;
		}
		this.StartActRegion();

	}

	private Boolean Touching(HNode node1, HNode node2) {
		Integer index = this._levelFlowDirection;

		return (((node1 != null) && (node2 != null)) && ((((node1
				.GetCoord(index) + node1.GetSize(index)) + this.GetMinDist(
				node1, node2)) + 0.01f) >= node2.GetCoord(index)));

	}

	private Boolean TryUnifyRegion(HNode node) {
		if (this._lastRegionEndMarker != null) {
			float lastRegionMoveVector = this.GetLastRegionMoveVector();
			float actRegionMoveVector = this.GetActRegionMoveVector();
			if (lastRegionMoveVector >= actRegionMoveVector) {
				this._lastRegionEndMarker.SetRegionEndMarker(0);
				this._actRegionSumNormVectors += this._lastRegionSumNormVectors;
				this._actRegionSumPrioVectors += this._lastRegionSumPrioVectors;
				this._actRegionNumberNormNodes += this._lastRegionNumberNormNodes;
				this._actRegionNumberPrioNodes += this._lastRegionNumberPrioNodes;

				return true;
			}
		}

		return false;

	}

	private void UnifyBackwardRegions(HLevel level) {
		this.CreateInialRegions(level);
		Boolean flag = true;
		while (flag) {
			flag = false;
			this._lastRegionEndMarker = null;
			this.StartActRegion();
			HNodeIterator nodes = level.GetNodes();

			while (nodes.HasNext()) {
				HNode node = nodes.Next();
				this.AddToActRegionBackward(node);
				if (node.GetRegionEndMarker() != 0) {

					flag |= this.TryUnifyRegion(node);
					this.StartNewRegion(node);
				}
			}
		}
		this.SetBackwardRegionMoveVector(level);

	}

	private void UnifyForwardRegions(HLevel level) {
		this.CreateInialRegions(level);
		Boolean flag = true;
		while (flag) {
			flag = false;
			this._lastRegionEndMarker = null;
			this.StartActRegion();
			HNodeIterator nodes = level.GetNodes();

			while (nodes.HasNext()) {
				HNode node = nodes.Next();
				this.AddToActRegionForward(node);
				if (node.GetRegionEndMarker() != 0) {

					flag |= this.TryUnifyRegion(node);
					this.StartNewRegion(node);
				}
			}
		}
		this.SetForwardRegionMoveVector(level);

	}

	private void UpdateBounds() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			levels.Next().UpdateBounds(this._levelFlowDirection, false);
		}

	}

	private void VerifySwimLanesAtEnd() {

		if (this.HasSwimLanes()) {
			HGraph graph = super.GetGraph();
			this._swimLaneAlg.CalcSwimLaneBorders(false);
			graph.ToggleLevelsSwimLaneMode();
			this.PendulumSweepWidestLevel();
			graph.SetOffsetForMinDist(this._levelFlowDirection, 0f);
			this._straightenAlg.Run();
			this._swimLaneAlg.ShiftTogether(true);
			graph.ToggleLevelsSwimLaneMode();
			this._swimLaneAlg.CalcSwimLaneBounds();
		}

	}

	private void VerifySwimLanesBackwardSweep() {

		if (this.HasSwimLanes()) {
			HGraph graph = super.GetGraph();
			this._swimLaneAlg.CalcSwimLaneBorders(false);
			graph.ToggleLevelsSwimLaneMode();
			this.PendulumSweepBackward();
			graph.ToggleLevelsSwimLaneMode();
		}

	}

	private void VerifySwimLanesForwardSweep() {

		if (this.HasSwimLanes()) {
			HGraph graph = super.GetGraph();
			this._swimLaneAlg.CalcSwimLaneBorders(false);
			graph.ToggleLevelsSwimLaneMode();
			this.PendulumSweepForward();
			graph.ToggleLevelsSwimLaneMode();
		}

	}

}
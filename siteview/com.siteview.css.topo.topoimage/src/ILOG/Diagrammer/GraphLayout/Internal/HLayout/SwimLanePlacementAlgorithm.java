package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;
import system.Collections.*;

public final class SwimLanePlacementAlgorithm extends HGraphAlgorithm {
	public Integer _actSwimLaneOrderingNumber;

	private float _additionalSwimLaneBorderOffset;

	public Boolean _fixedNodesInShiftTogether = false;

	private Integer _levelFlowDirection;

	public Integer _maxComponentLevelIndex;

	public Integer _maxSwimLaneOrderingNumber;

	public Integer _minComponentLevelIndex;

	public Integer _minSwimLaneOrderingNumber;

	private HIntNodeMarker _nodeShiftMarker;

	private HSwimLane[] _swimLanes;

	public Boolean _useConnectionPoints = false;

	private Integer[] _visitedNodeIndexPhase1;

	private Integer[] _visitedNodeIndexPhase2;

	public SwimLanePlacementAlgorithm(HGraph graph) {
		super.Init(graph);
		this._swimLanes = null;

		if (graph.HasSwimLanes()) {
			this.PreprocessSwimLanes();
		}
		this._nodeShiftMarker = new HIntNodeMarker(graph);
		this._visitedNodeIndexPhase1 = new Integer[graph.GetNumberOfLevels()];
		this._visitedNodeIndexPhase2 = new Integer[graph.GetNumberOfLevels()];

		this._levelFlowDirection = graph.GetLevelFlow();
	}

	public void CalcFinalSwimLaneBounds() {
		if (this._swimLanes != null) {
			float relativeSize = 0;
			this.CalcSwimLaneBorders(true);
			this.CalcSwimLaneBounds();
			Integer index = this._levelFlowDirection;
			float num3 = 0f;
			float num5 = 0f;
			Integer num = 0;
			while (num < this._swimLanes.length) {
				this._swimLanes[num].SetShiftValue(0f);

				relativeSize = this._swimLanes[num].GetRelativeSize();
				if (relativeSize > 0f) {
					num5 = this._swimLanes[num].GetSize(index) / relativeSize;
					if (num5 > num3) {
						num3 = num5;
					}
				}
				num++;
			}
			if (num3 != 0f) {
				float num6 = 0;
				float size = 0;
				float coord = this._swimLanes[0].GetCoord(index);
				num = 0;
				while (num < this._swimLanes.length) {

					relativeSize = this._swimLanes[num].GetRelativeSize();
					if (relativeSize > 0f) {
						size = num3 * relativeSize;
					} else {

						size = this._swimLanes[num].GetSize(index);
					}

					num6 = this._swimLanes[num].GetCoord(index);
					float val = (coord - num6)
							+ (0.5f * (size - this._swimLanes[num]
									.GetSize(index)));

					if (this._swimLanes[num].IsFreeToRight()) {
						this._swimLanes[num].AddShiftValue(val);
						this._swimLanes[num].SetCoord(index, coord);
						this._swimLanes[num].SetSize(index, size);
						this._swimLanes[num].SetBorderCoord(
								index,
								(coord + size)
										- this._swimLanes[num].GetMinMargin());
					}
					coord = this._swimLanes[num].GetCoord(index)
							+ this._swimLanes[num].GetSize(index);
					num++;
				}
				num = this._swimLanes.length - 1;
				num6 = this._swimLanes[num].GetCoord(index)
						+ this._swimLanes[num].GetSize(index);
				for (num = this._swimLanes.length - 1; num >= 0; num--) {

					relativeSize = this._swimLanes[num].GetRelativeSize();
					if (relativeSize > 0f) {
						size = num3 * relativeSize;
					} else {

						size = this._swimLanes[num].GetSize(index);
					}
					num6 -= size;

					coord = this._swimLanes[num].GetCoord(index);
					float num10 = (num6 - coord)
							+ (0.5f * (size - this._swimLanes[num]
									.GetSize(index)));

					if (this._swimLanes[num].IsFreeToLeft()) {
						this._swimLanes[num].AddShiftValue(num10);
						this._swimLanes[num].SetCoord(index, num6);
						this._swimLanes[num].SetSize(index, size);
						this._swimLanes[num].SetBorderCoord(
								index,
								(num6 + size)
										- this._swimLanes[num].GetMinMargin());
					}

					num6 = this._swimLanes[num].GetCoord(index);
				}
				this.EvaluateShiftValues();
			}
		}

	}

	public void CalcSwimLaneBorders(Boolean finalrun) {
		if (this._swimLanes != null) {
			if (finalrun) {
				this._additionalSwimLaneBorderOffset = 0f;
			} else {
				this._additionalSwimLaneBorderOffset = 3f * super.GetGraph()
						.GetMinDistBetweenNodes(this._levelFlowDirection);
			}
			this.CalcSwimLaneMinMaxNodePositions();
			for (Integer i = 0; i < this._swimLanes.length; i++) {

				if (this._swimLanes[i].IsFreeToRight()) {
					this.HandleFreeToRightSwimLane(i);
				} else if (this._swimLanes[i].IsFreeToLeft()) {
					this.HandleFreeToLeftSwimLane(i);
				} else {
					this.HandleFixedSwimLane(i);
				}
			}
			this.EvaluateShiftValues();
		}

	}

	public void CalcSwimLaneBounds() {
		if (this._swimLanes != null) {
			HNode borderNode = null;
			Integer index = this._levelFlowDirection;
			float maxValue = Float.MAX_VALUE;
			float minValue = Float.MIN_VALUE;
			HLevelIterator levels = super.GetGraph().GetLevels();

			while (levels.HasNext()) {
				HLevel level = levels.Next();
				if (level.GetNodesCount() > 0) {
					float coord = level.GetNode(0).GetCoord(index);
					if (coord < maxValue) {
						maxValue = coord;
					}
				}
			}
			Integer num5 = 0;
			while (num5 < (this._swimLanes.length - 1)) {

				borderNode = this._swimLanes[num5].GetBorderNode(0);

				maxValue -= this._swimLanes[num5].GetMinMargin();
				minValue = borderNode.GetCoord(index)
						+ this._swimLanes[num5].GetMinMargin();
				this._swimLanes[num5].SetCoord(index, maxValue);
				this._swimLanes[num5].SetSize(index, minValue - maxValue);
				maxValue = borderNode.GetCoord(index)
						+ borderNode.GetSize(index);
				num5++;
			}
			num5 = this._swimLanes.length - 1;

			borderNode = this._swimLanes[num5].GetBorderNode(0);

			maxValue -= this._swimLanes[num5].GetMinMargin();
			minValue = borderNode.GetCoord(index)
					+ this._swimLanes[num5].GetMinMargin();
			this._swimLanes[num5].SetCoord(index, maxValue);
			this._swimLanes[num5].SetSize(index, minValue - maxValue);
		}

	}

	private void CalcSwimLaneMarkers() {
		Integer num2 = null;
		HGraph graph = super.GetGraph();

		this._swimLanes = graph.GetSortedSwimLanes();
		Integer length = this._swimLanes.length;

		this._minSwimLaneOrderingNumber = this._swimLanes[0]
				.GetOrderingNumber();

		this._maxSwimLaneOrderingNumber = this._swimLanes[length - 1]
				.GetOrderingNumber();
		for (num2 = 0; num2 < length; num2++) {
			float minMargin = this._swimLanes[num2].GetMinMargin();
			if ((num2 + 1) < this._swimLanes.length) {

				minMargin += this._swimLanes[num2 + 1].GetMinMargin();
			}
			this._swimLanes[num2].SetMarker(num2);
			this._swimLanes[num2].SetFreeToLeft(true);
			this._swimLanes[num2].SetFreeToRight(true);
			this._swimLanes[num2].SetMinFixed(Float.MAX_VALUE);
			this._swimLanes[num2].SetMaxFixed(Float.MIN_VALUE);
			this._swimLanes[num2].SetMinInfluencedFromFixed(Float.MAX_VALUE);
			this._swimLanes[num2].SetMaxInfluencedFromFixed(Float.MIN_VALUE);
			this._swimLanes[num2].AllocateBorderNodes(graph, minMargin);
		}
		HLevelIterator levels = graph.GetLevels();

		while (levels.HasNext()) {
			this.PreprocessSwimLanes(levels.Next());
		}
		Boolean flag = true;
		for (num2 = 0; num2 < length; num2++) {
			this._swimLanes[num2].SetFreeToLeft(flag);
			flag &= this._swimLanes[num2].GetMaxFixed() == Float.MIN_VALUE;
		}
		flag = true;
		for (num2 = length - 1; num2 >= 0; num2--) {
			this._swimLanes[num2].SetFreeToRight(flag);
			flag &= this._swimLanes[num2].GetMinFixed() == Float.MAX_VALUE;
		}

	}

	private void CalcSwimLaneMinMaxNodePositions() {
		for (Integer i = 0; i < this._swimLanes.length; i++) {
			this._swimLanes[i].SetMinNodePosition(Float.MAX_VALUE);
			this._swimLanes[i].SetMaxNodePosition(Float.MIN_VALUE);
			this._swimLanes[i].SetShiftValue(0f);
		}
		Integer index = this._levelFlowDirection;
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			HNodeIterator nodes = levels.Next().GetNodes();

			while (nodes.HasNext()) {
				HNode node = nodes.Next();
				HSwimLane swimLane = node.GetSwimLane();
				float coord = node.GetCoord(index);
				if (coord < swimLane.GetMinNodePosition()) {
					swimLane.SetMinNodePosition(coord);
				}

				coord += node.GetSize(index);
				if (coord > swimLane.GetMaxNodePosition()) {
					swimLane.SetMaxNodePosition(coord);
				}
			}
		}

	}

	@Override
	public void Clean() {
		super.Clean();
		if (this._nodeShiftMarker != null) {
			this._nodeShiftMarker.Clean();
		}
		this._nodeShiftMarker = null;
		this._visitedNodeIndexPhase1 = null;
		this._visitedNodeIndexPhase2 = null;
		this._swimLanes = null;

	}

	private void EvaluateShiftValues() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			HNodeIterator nodes = levels.Next().GetNodes();

			while (nodes.HasNext()) {
				HNode node = nodes.Next();
				this.MoveNodeBy(node, node.GetSwimLane().GetShiftValue());
			}
		}

	}

	private float GetMinDist(HNode node1, HNode node2) {

		return super.GetGraph().GetMinDist(this._levelFlowDirection, node1,
				node2);

	}

	private HNode GetNodeWithSmallestShiftDistToLeft(Integer minLevelIndex,
			Integer maxLevelIndex, Boolean preferLeftestNode,
			Integer swimLaneIndex) {
		HNode node2 = null;
		float maxValue = Float.MAX_VALUE;
		for (Integer i = minLevelIndex; i <= maxLevelIndex; i++) {
			HLevel level = super.GetGraph().GetLevel(i);
			Integer index = this._visitedNodeIndexPhase2[i] + 1;
			if (index < level.GetNodesCount()) {
				HNode node = level.GetNode(index);
				if ((swimLaneIndex < 0)
						|| ((node.GetSwimLane() != null) && (node.GetSwimLane()
								.GetOrderingNumber() == swimLaneIndex))) {
					float shiftDistToLeft = 0;
					if ((index == 0) && !preferLeftestNode) {
						shiftDistToLeft = Float.MAX_VALUE;
					} else {

						shiftDistToLeft = this.GetShiftDistToLeft(node);
					}
					if (shiftDistToLeft < maxValue) {
						node2 = node;
						maxValue = shiftDistToLeft;
					}
				}
			}
		}

		return node2;

	}

	private HNode GetNodeWithSmallestShiftDistToRight(Integer minLevelIndex,
			Integer maxLevelIndex, Boolean preferRightestNode,
			Integer swimLaneIndex) {
		HNode node2 = null;
		float maxValue = Float.MAX_VALUE;
		for (Integer i = minLevelIndex; i <= maxLevelIndex; i++) {
			HLevel level = super.GetGraph().GetLevel(i);
			Integer index = this._visitedNodeIndexPhase2[i] - 1;
			if (index >= 0) {
				HNode node = level.GetNode(index);
				if ((swimLaneIndex < 0)
						|| ((node.GetSwimLane() != null) && (node.GetSwimLane()
								.GetOrderingNumber() == swimLaneIndex))) {
					float shiftDistToRight = 0;
					if ((index == (level.GetNodesCount() - 1))
							&& !preferRightestNode) {
						shiftDistToRight = Float.MAX_VALUE;
					} else {

						shiftDistToRight = this.GetShiftDistToRight(node);
					}
					if (shiftDistToRight < maxValue) {
						node2 = node;
						maxValue = shiftDistToRight;
					}
				}
			}
		}

		return node2;

	}

	private HNode GetRootNodeForShiftToLeft() {
		HNode node = this.GetNodeWithSmallestShiftDistToLeft(0,
				this._visitedNodeIndexPhase1.length - 1, true,
				this._actSwimLaneOrderingNumber);
		if ((node == null) && (this._actSwimLaneOrderingNumber >= 0)) {
			while (this._actSwimLaneOrderingNumber < this._maxSwimLaneOrderingNumber) {
				this.MarkSwimLaneBorderNodesVisited(
						this._actSwimLaneOrderingNumber, true);
				this._actSwimLaneOrderingNumber++;

				node = this.GetNodeWithSmallestShiftDistToLeft(0,
						this._visitedNodeIndexPhase1.length - 1, true,
						this._actSwimLaneOrderingNumber);
				if (node != null) {

					return node;
				}
			}

			return null;
		}

		return node;

	}

	private HNode GetRootNodeForShiftToRight() {
		HNode node = this.GetNodeWithSmallestShiftDistToRight(0,
				this._visitedNodeIndexPhase1.length - 1, true,
				this._actSwimLaneOrderingNumber);
		if ((node == null) && (this._actSwimLaneOrderingNumber >= 0)) {
			while (this._actSwimLaneOrderingNumber > this._minSwimLaneOrderingNumber) {
				this.MarkSwimLaneBorderNodesVisited(
						this._actSwimLaneOrderingNumber - 1, false);
				this._actSwimLaneOrderingNumber--;

				node = this.GetNodeWithSmallestShiftDistToRight(0,
						this._visitedNodeIndexPhase1.length - 1, true,
						this._actSwimLaneOrderingNumber);
				if (node != null) {

					return node;
				}
			}

			return null;
		}

		return node;

	}

	private float GetShiftDistForShiftToLeft(HNode node) {
		this._minComponentLevelIndex = this._visitedNodeIndexPhase1.length;
		this._maxComponentLevelIndex = -1;
		this._fixedNodesInShiftTogether = false;
		this.VisitForCalcLevelRangeToLeft(node);
		if (!this._fixedNodesInShiftTogether) {
			HNode node2 = this.GetNodeWithSmallestShiftDistToLeft(
					this._minComponentLevelIndex, this._maxComponentLevelIndex,
					false, -1);
			if (node2 == null) {

				return 0f;
			}
			float shiftDistToLeft = this.GetShiftDistToLeft(node2);
			if (shiftDistToLeft >= 0f) {

				return shiftDistToLeft;
			}
		}

		return 0f;

	}

	private float GetShiftDistForShiftToRight(HNode node) {
		this._minComponentLevelIndex = this._visitedNodeIndexPhase1.length;
		this._maxComponentLevelIndex = -1;
		this._fixedNodesInShiftTogether = false;
		this.VisitForCalcLevelRangeToRight(node);
		if (!this._fixedNodesInShiftTogether) {
			HNode node2 = this.GetNodeWithSmallestShiftDistToRight(
					this._minComponentLevelIndex, this._maxComponentLevelIndex,
					false, -1);
			if (node2 == null) {

				return 0f;
			}
			float shiftDistToRight = this.GetShiftDistToRight(node2);
			if (shiftDistToRight >= 0f) {

				return shiftDistToRight;
			}
		}

		return 0f;

	}

	private float GetShiftDistToLeft(HNode node) {
		HLevel level = node.GetLevel();
		Integer positionInLevel = node.GetPositionInLevel();
		if (positionInLevel <= 0) {

			return -1f;
		}
		HNode node2 = level.GetNode(positionInLevel - 1);
		float num2 = ((node.GetCoord(this._levelFlowDirection) - node2
				.GetCoord(this._levelFlowDirection)) - node2
				.GetSize(this._levelFlowDirection))
				- this.GetMinDist(node2, node);
		if (num2 < 0f) {
			num2 = 0f;
		}

		return num2;

	}

	private float GetShiftDistToRight(HNode node) {
		HLevel level = node.GetLevel();
		Integer positionInLevel = node.GetPositionInLevel();
		if (positionInLevel >= (level.GetNodesCount() - 1)) {

			return -1f;
		}
		HNode node2 = level.GetNode(positionInLevel + 1);
		float num2 = ((node2.GetCoord(this._levelFlowDirection) - node
				.GetCoord(this._levelFlowDirection)) - node
				.GetSize(this._levelFlowDirection))
				- this.GetMinDist(node, node2);
		if (num2 < 0f) {
			num2 = 0f;
		}

		return num2;

	}

	private void HandleFixedSwimLane(Integer actSwimLaneOrderingNumber) {

	}

	private void HandleFreeToLeftSwimLane(Integer actSwimLaneOrderingNumber) {
		if (actSwimLaneOrderingNumber != 0) {
			Integer index = this._levelFlowDirection;
			HSwimLane lane = this._swimLanes[actSwimLaneOrderingNumber];
			HSwimLane lane2 = this._swimLanes[actSwimLaneOrderingNumber - 1];
			float coord = (lane.GetMinNodePosition() + lane.GetShiftValue())
					- this._additionalSwimLaneBorderOffset;

			coord -= lane2.GetBorderNode(0).GetSize(index);
			lane2.SetBorderCoord(index, coord);
			float num3 = (lane2.GetMaxNodePosition() + lane2.GetShiftValue())
					+ this._additionalSwimLaneBorderOffset;
			float num4 = num3 - coord;
			for (Integer i = actSwimLaneOrderingNumber - 1; i >= 0; i--) {
				if (num4 > 0f) {
					this._swimLanes[i].AddShiftValue(-num4);
				} else if (this._swimLanes[i].IsFreeToRight()) {
					this._swimLanes[i].AddShiftValue(-num4);
				}
			}
		}

	}

	private void HandleFreeToRightSwimLane(Integer actSwimLaneOrderingNumber) {
		Integer dir = this._levelFlowDirection;
		HSwimLane lane = this._swimLanes[actSwimLaneOrderingNumber];
		float coord = (lane.GetMaxNodePosition() + lane.GetShiftValue())
				+ this._additionalSwimLaneBorderOffset;
		lane.SetBorderCoord(dir, coord);

		coord += lane.GetBorderNode(0).GetSize(dir);
		if (actSwimLaneOrderingNumber < (this._swimLanes.length - 1)) {
			HSwimLane lane2 = this._swimLanes[actSwimLaneOrderingNumber + 1];
			float num3 = (lane2.GetMinNodePosition() + lane2.GetShiftValue())
					- this._additionalSwimLaneBorderOffset;
			float val = coord - num3;
			for (Integer i = actSwimLaneOrderingNumber + 1; i < this._swimLanes.length; i++) {
				if (val > 0f) {
					this._swimLanes[i].AddShiftValue(val);
				} else if (this._swimLanes[i].IsFreeToLeft()) {
					this._swimLanes[i].AddShiftValue(val);
				}
			}
		}

	}

	private void MarkSwimLaneBorderNodesVisited(Integer orderingNumber,
			Boolean toLeft) {
		HSwimLane lane = null;
		Integer num = null;
		for (num = 0; num < this._swimLanes.length; num++) {
			if (this._swimLanes[num].GetOrderingNumber() == orderingNumber) {
				lane = this._swimLanes[num];
				break;
			}
		}
		if (lane != null) {
			for (num = 0; num < this._visitedNodeIndexPhase1.length; num++) {
				Integer positionInLevel = lane.GetBorderNode(num)
						.GetPositionInLevel();
				if (toLeft) {
					if (this._visitedNodeIndexPhase1[num] < positionInLevel) {
						this._visitedNodeIndexPhase1[num] = positionInLevel;
					}
					if (this._visitedNodeIndexPhase2[num] < positionInLevel) {
						this._visitedNodeIndexPhase2[num] = positionInLevel;
					}
				} else {
					if (this._visitedNodeIndexPhase1[num] > positionInLevel) {
						this._visitedNodeIndexPhase1[num] = positionInLevel;
					}
					if (this._visitedNodeIndexPhase2[num] > positionInLevel) {
						this._visitedNodeIndexPhase2[num] = positionInLevel;
					}
				}
			}
		}

	}

	private void MoveNodeBy(HNode node, float offset) {
		if (offset != 0f) {
			float coord = node.GetCoord(this._levelFlowDirection) + offset;
			if (this._useConnectionPoints) {
				node.SetCoordWithLinkUpdate(this._levelFlowDirection, coord);
			} else {
				node.SetCoord(this._levelFlowDirection, coord);
			}
		}

	}

	private void PreprocessSwimLanes() {
		this.ProcessSegmentsForSwimLanes();
		this.CalcSwimLaneMarkers();
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			levels.Next().BuildNodesFieldForSwimLanes(this._swimLanes);
		}

	}

	private void PreprocessSwimLanes(HLevel level) {
		HNode node = null;
		HSwimLane swimLane = null;
		float num2 = 0;
		Integer index = this._levelFlowDirection;
		HNodeIterator nodes = level.GetNodes();
		HNode node2 = null;
		float minValue = Float.MIN_VALUE;

		while (nodes.HasNext()) {

			node = nodes.Next();

			swimLane = node.GetSwimLane();

			if (node.IsFixedForIncremental(index)) {
				swimLane.UpdateMinFixed(node.GetCoord(index)
						- swimLane.GetMinMargin());
				num2 = (node.GetCoord(index) + node.GetSize(index))
						+ swimLane.GetMinMargin();
				swimLane.UpdateMaxFixed(num2);
				swimLane.UpdateMaxInfluencedFromFixed(num2);
				minValue = node.GetCoord(index) + node.GetSize(index);
			} else if (minValue != Float.MIN_VALUE) {
				if (node2 != null) {

					minValue += this.GetMinDist(node2, node);
				}

				minValue += node.GetSize(index);
				num2 = minValue + swimLane.GetMinMargin();
				swimLane.UpdateMaxInfluencedFromFixed(num2);
			}
			node2 = node;
		}
		node2 = null;

		nodes = level.GetNodesBackward();
		minValue = Float.MAX_VALUE;

		while (nodes.HasNext()) {

			node = nodes.Next();

			swimLane = node.GetSwimLane();

			if (node.IsFixedForIncremental(index)) {
				num2 = node.GetCoord(index) - swimLane.GetMinMargin();
				swimLane.UpdateMinInfluencedFromFixed(num2);

				minValue = node.GetCoord(index);
			} else if (minValue != Float.MAX_VALUE) {
				if (node2 != null) {

					minValue -= this.GetMinDist(node, node2);
				}

				minValue -= node.GetSize(index);
				num2 = minValue - swimLane.GetMinMargin();
				swimLane.UpdateMinInfluencedFromFixed(num2);
			}
			node2 = node;
		}

	}

	private void ProcessSegmentsForSwimLanes() {
		HSegmentIterator segments = super.GetGraph().GetSegments();

		while (segments.HasNext()) {
			HSegment segment = segments.Next();
			if (segment.GetFrom().GetSwimLane() != segment.GetTo()
					.GetSwimLane()) {
				segment.SetPriority(0f);
			}
		}

	}

	private void PushOnStack(HNode node, Stack stack, Integer desired) {
		if (this._nodeShiftMarker.Get(node) < desired) {
			this._nodeShiftMarker.Set(node, desired);
			stack.Push(node);
		}

	}

	@Override
	public void Run() {
		this.CalcSwimLaneBorders(true);
		this.CalcSwimLaneBounds();

	}

	public void SetUseConnectionPoints(Boolean flag) {
		this._useConnectionPoints = flag;

	}

	public void ShiftTogether(Boolean hasSwimLaneBorderNodes) {
		Boolean flag = true;
		Integer num = 0;
		while (flag) {
			float shiftDistForShiftToLeft = 0;
			flag = false;
			num++;
			if (num > 5) {

				return;
			}
			for (Integer i = 0; i < this._visitedNodeIndexPhase1.length; i++) {
				this._visitedNodeIndexPhase1[i] = -1;
				this._visitedNodeIndexPhase2[i] = -1;
			}
			this._nodeShiftMarker.SetAll(0);
			if (hasSwimLaneBorderNodes) {
				this._actSwimLaneOrderingNumber = this._minSwimLaneOrderingNumber;
			} else {
				this._actSwimLaneOrderingNumber = -1;
			}
			HNode rootNodeForShiftToLeft = this.GetRootNodeForShiftToLeft();
			while (rootNodeForShiftToLeft != null) {

				shiftDistForShiftToLeft = this
						.GetShiftDistForShiftToLeft(rootNodeForShiftToLeft);
				if (shiftDistForShiftToLeft > 0.0) {
					flag = true;
				}
				this.ShiftToLeft(rootNodeForShiftToLeft,
						shiftDistForShiftToLeft);

				rootNodeForShiftToLeft = this.GetRootNodeForShiftToLeft();
			}
			for (Integer j = 0; j < this._visitedNodeIndexPhase1.length; j++) {

				this._visitedNodeIndexPhase1[j] = super.GetGraph().GetLevel(j)
						.GetNodesCount();

				this._visitedNodeIndexPhase2[j] = super.GetGraph().GetLevel(j)
						.GetNodesCount();
			}
			this._nodeShiftMarker.SetAll(0);
			if (hasSwimLaneBorderNodes) {
				this._actSwimLaneOrderingNumber = this._maxSwimLaneOrderingNumber;
			} else {
				this._actSwimLaneOrderingNumber = -1;
			}
			for (rootNodeForShiftToLeft = this.GetRootNodeForShiftToRight(); rootNodeForShiftToLeft != null; rootNodeForShiftToLeft = this
					.GetRootNodeForShiftToRight()) {

				shiftDistForShiftToLeft = this
						.GetShiftDistForShiftToRight(rootNodeForShiftToLeft);
				if (shiftDistForShiftToLeft > 0.0) {
					flag = true;
				}
				this.ShiftToRight(rootNodeForShiftToLeft,
						shiftDistForShiftToLeft);
			}
		}

	}

	private void ShiftToLeft(HNode node, float shiftValue) {
		if (this._nodeShiftMarker.Get(node) < 2) {
			Stack stack = new Stack();
			stack.Push(node);
			this._nodeShiftMarker.Set(node, 2);
			while (stack.get_Count() != 0) {
				node = (HNode) stack.Pop();
				if (shiftValue != 0f) {
					node.ShiftCoord(this._levelFlowDirection, -shiftValue);
				}
				HLevel level = node.GetLevel();
				Integer levelNumber = node.GetLevelNumber();
				Integer positionInLevel = node.GetPositionInLevel();
				Integer num3 = this._visitedNodeIndexPhase2[levelNumber];
				if (num3 < positionInLevel) {
					this._visitedNodeIndexPhase2[levelNumber] = positionInLevel;
				}
				for (Integer i = num3 + 1; i < positionInLevel; i++) {
					this.PushOnStack(level.GetNode(i), stack, 2);
				}
				this.TreatParentsAndChildren(node, stack, 2);
			}
		}

	}

	private void ShiftToRight(HNode node, float shiftValue) {
		if (this._nodeShiftMarker.Get(node) < 2) {
			Stack stack = new Stack();
			stack.Push(node);
			this._nodeShiftMarker.Set(node, 2);
			while (stack.get_Count() != 0) {
				node = (HNode) stack.Pop();
				if (shiftValue != 0f) {
					node.ShiftCoord(this._levelFlowDirection, shiftValue);
				}
				HLevel level = node.GetLevel();
				Integer levelNumber = node.GetLevelNumber();
				Integer positionInLevel = node.GetPositionInLevel();
				Integer num3 = this._visitedNodeIndexPhase2[levelNumber];
				if (num3 > positionInLevel) {
					this._visitedNodeIndexPhase2[levelNumber] = positionInLevel;
				}
				for (Integer i = positionInLevel + 1; i < num3; i++) {
					this.PushOnStack(level.GetNode(i), stack, 2);
				}
				this.TreatParentsAndChildren(node, stack, 2);
			}
		}

	}

	private void TreatParentsAndChildren(HNode node, Stack stack,
			Integer desired) {
		HSegmentIterator segments = node.GetSegments();

		while (segments.HasNext()) {
			HSegment segment = segments.Next();
			if (segment.GetPriority() != 0f) {
				this.PushOnStack(segment.GetOpposite(node), stack, desired);
			}
		}

	}

	private void VisitForCalcLevelRangeToLeft(HNode node) {
		if (this._nodeShiftMarker.Get(node) < 1) {
			Stack stack = new Stack();
			stack.Push(node);
			this._nodeShiftMarker.Set(node, 1);
			while (stack.get_Count() != 0) {
				node = (HNode) stack.Pop();

				if (node.IsFixedForIncremental(this._levelFlowDirection)) {
					this._fixedNodesInShiftTogether = true;
				}
				HLevel level = node.GetLevel();
				Integer levelNumber = node.GetLevelNumber();
				Integer positionInLevel = node.GetPositionInLevel();
				Integer num3 = this._visitedNodeIndexPhase1[levelNumber];
				if (levelNumber < this._minComponentLevelIndex) {
					this._minComponentLevelIndex = levelNumber;
				}
				if (levelNumber > this._maxComponentLevelIndex) {
					this._maxComponentLevelIndex = levelNumber;
				}
				if (num3 < positionInLevel) {
					this._visitedNodeIndexPhase1[levelNumber] = positionInLevel;
				}
				for (Integer i = num3 + 1; i < positionInLevel; i++) {
					this.PushOnStack(level.GetNode(i), stack, 1);
				}
				this.TreatParentsAndChildren(node, stack, 1);
			}
		}

	}

	private void VisitForCalcLevelRangeToRight(HNode node) {
		if (this._nodeShiftMarker.Get(node) < 1) {
			Stack stack = new Stack();
			stack.Push(node);
			this._nodeShiftMarker.Set(node, 1);
			while (stack.get_Count() != 0) {
				node = (HNode) stack.Pop();

				if (node.IsFixedForIncremental(this._levelFlowDirection)) {
					this._fixedNodesInShiftTogether = true;
				}
				HLevel level = node.GetLevel();
				Integer levelNumber = node.GetLevelNumber();
				Integer positionInLevel = node.GetPositionInLevel();
				Integer num3 = this._visitedNodeIndexPhase1[levelNumber];
				if (levelNumber < this._minComponentLevelIndex) {
					this._minComponentLevelIndex = levelNumber;
				}
				if (levelNumber > this._maxComponentLevelIndex) {
					this._maxComponentLevelIndex = levelNumber;
				}
				if (num3 > positionInLevel) {
					this._visitedNodeIndexPhase1[levelNumber] = positionInLevel;
				}
				for (Integer i = positionInLevel + 1; i < num3; i++) {
					this.PushOnStack(level.GetNode(i), stack, 1);
				}
				this.TreatParentsAndChildren(node, stack, 1);
			}
		}

	}

}
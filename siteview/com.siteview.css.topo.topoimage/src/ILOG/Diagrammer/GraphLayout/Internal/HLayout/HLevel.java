package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning.*;
import system.*;

public final class HLevel extends HGraphMember {
	private HRPGraph _constraintNetwork;

	private float[] _coord;

	private HNode _eastExtremeNode;

	private Integer _firstFreeNodeSlot;

	private Boolean _flagsUpToDate = false;

	private float _fromForkCoord;

	private Integer _generalIntValue;

	private Boolean[] _hasFixedNodes;

	private Boolean _hasNodesWithSpecPos = false;

	private HNode[] _nodes;

	private HNode[] _nodesWhenSwimLanesAreUsed;

	private Integer _numberOfCrossings;

	private float[] _size;

	private float _toForkCoord;

	private HNode _westExtremeNode;

	public HLevel() {
		this.Init();
	}

	public void Add(HNode node) {
		this._nodes[this._firstFreeNodeSlot++] = node;
		this.MarkInfoOutOfDate();

	}

	public void BuildNodesFieldForSwimLanes(HSwimLane[] orderedSwimLanes) {
		this.TrimNodesField();
		Integer index = (this._nodes.length + orderedSwimLanes.length) - 1;
		this._nodesWhenSwimLanesAreUsed = new HNode[index];
		Integer num2 = 0;
		Integer num3 = 0;
		HSwimLane lane = orderedSwimLanes[num3];
		for (index = 0; index < this._nodes.length; index++) {
			while (this._nodes[index].GetSwimLane() != lane) {

				this._nodesWhenSwimLanesAreUsed[num2++] = lane
						.GetBorderNode(super.GetNumID());
				num3++;
				lane = orderedSwimLanes[num3];
			}
			this._nodesWhenSwimLanesAreUsed[num2++] = this._nodes[index];
		}
		while (num3 < (orderedSwimLanes.length - 1)) {

			this._nodesWhenSwimLanesAreUsed[num2++] = lane.GetBorderNode(super
					.GetNumID());
			num3++;
			lane = orderedSwimLanes[num3];
		}

	}

	public void CleanupConstraintNetwork() {
		this._westExtremeNode = null;
		this._eastExtremeNode = null;
		this._constraintNetwork = null;

	}

	public HRPGraph GetConstraintNetwork() {

		return this._constraintNetwork;

	}

	public float GetCoord(Integer index) {

		return this._coord[index];

	}

	public HNode GetEastExtremeNode() {

		return this._eastExtremeNode;

	}

	public float GetFromForkCoord() {

		return this._fromForkCoord;

	}

	public float GetLargestSize(Integer index) {
		float num = 0f;
		HNodeIterator nodes = this.GetNodes();

		while (nodes.HasNext()) {
			float size = nodes.Next().GetSize(index);
			if (size > num) {
				num = size;
			}
		}

		return num;

	}

	public HNode GetNode(Integer index) {

		return this._nodes[index];

	}

	public HNodeIterator GetNodes() {
		if (this._nodes != null) {

			return new AnonClass_1(this);
		}

		return new AnonClass_2();

	}

	public HNodeIterator GetNodesBackward() {
		if (this._nodes != null) {

			return new AnonClass_3(this);
		}

		return new AnonClass_4();

	}

	public Integer GetNodesCount() {
		if (this._firstFreeNodeSlot <= 0) {

			return 0;
		}

		return this._firstFreeNodeSlot;

	}

	public HNode[] GetNodesField() {

		return this._nodes;

	}

	public Integer GetNumberOfCrossings() {

		return this._numberOfCrossings;

	}

	public HRPGraph GetOrAllocateConstraintNetwork() {
		if (this._constraintNetwork == null) {
			HGraph ownerGraph = this.GetOwnerGraph();
			ownerGraph.UpdateNodeIDs();
			this._constraintNetwork = new HRPGraph();
			HNodeIterator nodes = this.GetNodes();

			while (nodes.HasNext()) {
				ownerGraph.AllocConstraintNode(nodes.Next(),
						this._constraintNetwork);
			}
			HNode westExtremeNode = this.GetWestExtremeNode();
			HNode eastExtremeNode = this.GetEastExtremeNode();
			this._constraintNetwork.Init(
					(westExtremeNode == null) ? null : ownerGraph
							.GetConstraintNode(westExtremeNode),
					(eastExtremeNode == null) ? null : ownerGraph
							.GetConstraintNode(eastExtremeNode));
		}

		return this._constraintNetwork;

	}

	public float GetReferenceCoord() {
		double num = 0.0;
		Integer num2 = 0;
		HNodeIterator nodes = this.GetNodes();
		Integer edgeFlow = this.GetOwnerGraph().GetEdgeFlow();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();

			if (!node.IsDummyNode() && !node.IsMarkedForIncremental()) {

				num += node.GetCenter(edgeFlow);
				num2++;
			}
		}
		if (num2 > 0) {

			return (float) (num / ((double) num2));
		}

		return Float.MAX_VALUE;

	}

	public Integer GetReferenceIndex() {

		return this._generalIntValue;

	}

	public HSegmentIterator GetSegmentsFrom() {
		if (this._nodes != null) {

			return new AnonClass_5(this);
		}

		return new AnonClass_6();

	}

	public HSegmentIterator GetSegmentsTo() {
		if (this._nodes != null) {

			return new AnonClass_7(this);
		}

		return new AnonClass_8();

	}

	public float GetSize(Integer index) {

		return this._size[index];

	}

	public float GetToForkCoord() {

		return this._toForkCoord;

	}

	public HNode GetWestExtremeNode() {

		return this._westExtremeNode;

	}

	public void HandleDummyNodesForIncremental() {
		HNode node = null;
		float referenceCoord = this.GetReferenceCoord();
		HNodeIterator nodes = this.GetNodes();
		if (referenceCoord == Float.MAX_VALUE) {

			while (nodes.HasNext()) {

				node = nodes.Next();

				if (node.IsDummyNode()) {
					node.MarkForIncremental();
				}
			}
		} else {
			HGraph ownerGraph = this.GetOwnerGraph();
			Integer levelFlow = ownerGraph.GetLevelFlow();
			Integer edgeFlow = ownerGraph.GetEdgeFlow();

			while (nodes.HasNext()) {

				node = nodes.Next();

				if (node.IsDummyNode()) {
					node.MarkToMoveFreeOnIncremental();
					HLink ownerLink = node.GetOwnerLink();
					if (ownerLink.GetFrom().IsMarkedForIncremental()
							|| ownerLink.GetTo().IsMarkedForIncremental()) {
						node.MarkForIncremental();
					}

					else if (!ownerLink.IsCrossingAt(referenceCoord)) {
						node.MarkForIncremental();
					} else {
						node.SetCoord(levelFlow,
								ownerLink.GetCrossCoordAt(referenceCoord));
						node.SetCoord(edgeFlow, referenceCoord);
					}
				}
			}
		}

	}

	public Boolean HasFixedNodes(Integer index) {

		return this._hasFixedNodes[index];

	}

	public Boolean HasNodesWithSpecPos() {

		return this._hasNodesWithSpecPos;

	}

	private void Init() {
		this._nodes = null;
		this._firstFreeNodeSlot = -1;
		this._coord = new float[] { 0f, 0f };
		this._size = new float[] { 0f, 0f };
		this._fromForkCoord = 0f;
		this._toForkCoord = 0f;
		this._westExtremeNode = null;
		this._eastExtremeNode = null;
		this._constraintNetwork = null;
		this._flagsUpToDate = true;
		this._hasNodesWithSpecPos = false;
		this._hasFixedNodes = new Boolean[] { false, false };

	}

	public void MarkInfoOutOfDate() {
		this._flagsUpToDate = false;

	}

	public void Remove(HNode node) {
		for (Integer i = 0; i < this._firstFreeNodeSlot; i++) {
			if (this._nodes[i] == node) {
				clr.System.ArrayStaticWrapper.Copy(this._nodes, i + 1,
						this._nodes, i, (this._firstFreeNodeSlot - i) - 1);
				this._firstFreeNodeSlot--;
				break;
			}
		}
		this.MarkInfoOutOfDate();

	}

	public void SetCapacity(Integer numberOfNodes) {
		if (this._nodes == null) {
			if (numberOfNodes > 0) {
				this._nodes = new HNode[numberOfNodes];
				this._firstFreeNodeSlot = 0;
			}
		} else if (numberOfNodes > this._nodes.length) {
			HNode[] nodeArray = this._nodes;
			this._nodes = new HNode[numberOfNodes];
			for (Integer i = 0; i < this._firstFreeNodeSlot; i++) {
				this._nodes[i] = nodeArray[i];
			}
		}

	}

	public void SetCoord(Integer index, float coord) {
		this._coord[index] = coord;

	}

	public void SetCoord(Integer index, float minCoord, float maxCoord,
			Integer justification) {
		HNodeIterator nodes = this.GetNodes();
		float coord = minCoord;
		float num3 = maxCoord;

		while (nodes.HasNext()) {
			float num = 0;
			HNode node = nodes.Next();

			if (!node.IsFixedForIncremental(index)) {

				if (node.IsDummyNode()) {

					if (node.IsUpperBypassNode()) {
						node.SetCoord(index, maxCoord - node.GetSize(index));
					} else if (node.IsLowerBypassNode()) {
						node.SetCoord(index, minCoord);
					} else {
						node.SetCoord(index,
								0.5f * ((minCoord + maxCoord) - node
										.GetSize(index)));
					}
				} else {
					if (justification == -1) {
						node.SetCoord(index, minCoord);
						num = node.GetCoord(index);
						if (num < coord) {
							coord = num;
						}
						num = node.GetCoord(index) + node.GetSize(index);
						if (num > num3) {
							num3 = num;
						}
						continue;
					} else if (justification == 0) {
						node.SetCoord(index,
								0.5f * ((minCoord + maxCoord) - node
										.GetSize(index)));
						num = node.GetCoord(index);
						if (num < coord) {
							coord = num;
						}
						num = node.GetCoord(index) + node.GetSize(index);
						if (num > num3) {
							num3 = num;
						}
						continue;
					} else if (justification == 1) {
						node.SetCoord(index, maxCoord - node.GetSize(index));
						num = node.GetCoord(index);
						if (num < coord) {
							coord = num;
						}
						num = node.GetCoord(index) + node.GetSize(index);
						if (num > num3) {
							num3 = num;
						}
						continue;
					}
				}
			}
			Label_00C1: num = node.GetCoord(index);
			if (num < coord) {
				coord = num;
			}
			num = node.GetCoord(index) + node.GetSize(index);
			if (num > num3) {
				num3 = num;
			}
		}
		this.SetCoord(index, coord);
		this.SetSize(index, num3 - coord);

	}

	public void SetCoordWithLinkUpdate(Integer index, float minCoord,
			float maxCoord, Integer justification) {
		HNodeIterator nodes = this.GetNodes();
		float coord = minCoord;
		float num3 = maxCoord;

		while (nodes.HasNext()) {
			float num = 0;
			HNode node = nodes.Next();

			if (node.IsDummyNode()) {
				node.SetCoord(index,
						0.5f * ((minCoord + maxCoord) - node.GetSize(index)));
			} else {
				if (justification == -1) {
					node.SetCoordWithLinkUpdate(index, minCoord);

				} else if (justification == 0) {
					node.SetCoordWithLinkUpdate(
							index,
							0.5f * ((minCoord + maxCoord) - node.GetSize(index)));

				} else if (justification == 1) {
					node.SetCoordWithLinkUpdate(index,
							maxCoord - node.GetSize(index));

				}
			}
			Label_0089: num = node.GetCoord(index);
			if (num < coord) {
				coord = num;
			}
			num = node.GetCoord(index) + node.GetSize(index);
			if (num > num3) {
				num3 = num;
			}
		}
		this.SetCoord(index, coord);
		this.SetSize(index, num3 - coord);

	}

	public void SetEastExtremeNode(HNode node) {
		if ((node != this._westExtremeNode) || (node == null)) {
			this._eastExtremeNode = node;
		}

	}

	public void SetFromForkCoord(float coord) {
		this._fromForkCoord = coord;

	}

	public void SetNodesField(HNode[] nodes) {
		this._nodes = nodes;
		this._firstFreeNodeSlot = nodes.length;
		this.MarkInfoOutOfDate();

	}

	public void SetNumberOfCrossings(Integer number) {
		this._numberOfCrossings = number;

	}

	public void SetReferenceIndex(Integer index) {
		this._generalIntValue = index;

	}

	public void SetSize(Integer index, float size) {
		this._size[index] = size;

	}

	public void SetToForkCoord(float coord) {
		this._toForkCoord = coord;

	}

	public void SetWestExtremeNode(HNode node) {
		if ((node != this._eastExtremeNode) || (node == null)) {
			this._westExtremeNode = node;
		}

	}

	public void StoreLevelPositionsInNodes(Integer startIndex,
			Boolean ignoreEastWestNodes) {
		HNodeIterator nodes = this.GetNodes();
		Integer positionInLevel = startIndex;

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			node.SetPositionInLevel(positionInLevel);

			if (!ignoreEastWestNodes || !node.IsEastWestPortAuxNode()) {
				positionInLevel++;
			}
		}

	}

	public void SwapNodesFieldForSwimLanes() {
		HNode[] nodeArray = this._nodesWhenSwimLanesAreUsed;
		this._nodesWhenSwimLanesAreUsed = this._nodes;
		this._nodes = nodeArray;
		this._firstFreeNodeSlot = this._nodes.length;
		this._flagsUpToDate = false;
		this.UpdateInfo();

	}

	public void TrimNodesField() {
		if (this._nodes.length != this._firstFreeNodeSlot) {
			HNode[] nodeArray = new HNode[this._firstFreeNodeSlot];
			for (Integer i = 0; i < this._firstFreeNodeSlot; i++) {
				nodeArray[i] = this._nodes[i];
			}
			this._nodes = nodeArray;
		}

	}

	public void UpdateBounds(Integer index, Boolean fixedOnly) {
		if (this.GetNodesCount() == 0) {
			this.SetCoord(index, 0f);
			this.SetSize(index, 0f);
		} else {
			HNodeIterator nodes = this.GetNodes();
			float maxValue = Float.MAX_VALUE;
			float minValue = Float.MIN_VALUE;

			while (nodes.HasNext()) {
				HNode node = nodes.Next();
				if (!fixedOnly || node.IsFixedForIncremental(index)) {
					float coord = node.GetCoord(index);
					if (coord < maxValue) {
						maxValue = coord;
					}
					coord = node.GetCoord(index) + node.GetSize(index);
					if (coord > minValue) {
						minValue = coord;
					}
				}
			}
			this.SetCoord(index, maxValue);
			this.SetSize(index, minValue - maxValue);
		}

	}

	public void UpdateInfo() {
		if (!this._flagsUpToDate) {
			this._hasNodesWithSpecPos = false;
			this._hasFixedNodes[0] = false;
			this._hasFixedNodes[1] = false;
			for (Integer i = 0; i < this._firstFreeNodeSlot; i++) {

				if (this._nodes[i].IsFixedForIncremental(0)) {
					this._hasFixedNodes[0] = true;
				}

				if (this._nodes[i].IsFixedForIncremental(1)) {
					this._hasFixedNodes[1] = true;
				}
				if (this._nodes[i].GetSpecPositionInLevel() >= 0) {
					this._hasNodesWithSpecPos = true;
				}
				this._nodes[i].SetPositionInLevel(i);
			}
			this._flagsUpToDate = true;
		}

	}

	private class AnonClass_1 implements HNodeIterator {
		private HLevel __outerThis;

		public Integer count;

		public AnonClass_1(HLevel input__outerThis) {
			this.__outerThis = input__outerThis;
			this.count = 0;
		}

		public Boolean HasNext() {

			return (this.count < this.__outerThis._firstFreeNodeSlot);

		}

		public void Init(HNode node) {
			if (node != null) {

				this.count = node.GetPositionInLevel();
			}

		}

		public HNode Next() {

			return this.__outerThis._nodes[this.count++];

		}

	}

	private class AnonClass_2 implements HNodeIterator {
		public AnonClass_2() {
		}

		public Boolean HasNext() {

			return false;

		}

		public void Init(HNode node) {

		}

		public HNode Next() {

			return null;

		}

	}

	private class AnonClass_3 implements HNodeIterator {
		private HLevel __outerThis;

		public Integer count;

		public AnonClass_3(HLevel input__outerThis) {
			this.__outerThis = input__outerThis;
			this.count = this.__outerThis._firstFreeNodeSlot - 1;
		}

		public Boolean HasNext() {

			return (this.count >= 0);

		}

		public void Init(HNode node) {
			if (node != null) {

				this.count = node.GetPositionInLevel();
			}

		}

		public HNode Next() {

			return this.__outerThis._nodes[this.count--];

		}

	}

	private class AnonClass_4 implements HNodeIterator {
		public AnonClass_4() {
		}

		public Boolean HasNext() {

			return false;

		}

		public void Init(HNode node) {

		}

		public HNode Next() {

			return null;

		}

	}

	private class AnonClass_5 implements HSegmentIterator {
		private HLevel __outerThis;

		public Integer count;

		public HSegmentIterator iter;

		public AnonClass_5(HLevel input__outerThis) {
			this.__outerThis = input__outerThis;
			this.count = 0;

			this.iter = this.MoveToNextIter();
		}

		public Boolean HasNext() {

			return (this.iter != null);

		}

		private HSegmentIterator MoveToNextIter() {
			while (this.count < this.__outerThis._firstFreeNodeSlot) {
				if (this.__outerThis._nodes[this.count++]
						.GetSegmentsFromCount() > 0) {

					return this.__outerThis._nodes[this.count - 1]
							.GetSegmentsFrom();
				}
			}

			return null;

		}

		public HSegment Next() {
			HSegment segment = this.iter.Next();

			if (!this.iter.HasNext()) {

				this.iter = this.MoveToNextIter();
			}

			return segment;

		}

		public void Remove() {
			throw (new system.Exception(
					"HSegmentIterator.remove() in Level Segment Iterator"));

		}

	}

	private class AnonClass_6 implements HSegmentIterator {
		public AnonClass_6() {
		}

		public Boolean HasNext() {

			return false;

		}

		public HSegment Next() {

			return null;

		}

		public void Remove() {

		}

	}

	private class AnonClass_7 implements HSegmentIterator {
		private HLevel __outerThis;

		public Integer count;

		public HSegmentIterator iter;

		public AnonClass_7(HLevel input__outerThis) {
			this.__outerThis = input__outerThis;
			this.count = 0;

			this.iter = this.MoveToNextIter();
		}

		public Boolean HasNext() {

			return (this.iter != null);

		}

		private HSegmentIterator MoveToNextIter() {
			while (this.count < this.__outerThis._firstFreeNodeSlot) {
				if (this.__outerThis._nodes[this.count++].GetSegmentsToCount() > 0) {

					return this.__outerThis._nodes[this.count - 1]
							.GetSegmentsTo();
				}
			}

			return null;

		}

		public HSegment Next() {
			HSegment segment = this.iter.Next();

			if (!this.iter.HasNext()) {

				this.iter = this.MoveToNextIter();
			}

			return segment;

		}

		public void Remove() {
			throw (new system.Exception(
					"HSegmentIterator.remove() in Level Segment Iterator"));

		}

	}

	private class AnonClass_8 implements HSegmentIterator {
		public AnonClass_8() {
		}

		public Boolean HasNext() {

			return false;

		}

		public HSegment Next() {

			return null;

		}

		public void Remove() {

		}

	}
}
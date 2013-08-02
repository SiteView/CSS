package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class HNode extends HGraphMember {
	private float[] _coord;

	private HSegment _firstFromSegment;

	private HSegment _firstToSegment;

	private Boolean[] _fixedForIncremental;

	private float _generalFloatValue;

	private Integer _generalIntValue;

	private Integer _indegree;

	private Integer _levelNumber;

	private Boolean _markedForIncremental = false;

	private float _oldCoordInLevelFlow;

	private float _oldSizeInEdgeFlow;

	private Integer _outdegree;

	private HPortCache _portCache;

	private Integer _positionInLevel;

	private float[] _realBounds;

	private float[] _size;

	private Integer _specLevelNumber;

	private Integer _specPositionInLevel;

	private HSwimLane _swimLane;

	public HNode() {
		this.Init();
	}

	public void ActAfterAddToGraph() {

		if (!this.IsDummyNode()) {
			this._portCache = new HPortCache();
		} else {
			this._portCache = null;
		}

	}

	public void ActBeforeRemoveFromGraph() {

		if (!this.IsDummyNode()) {
			HLink ownerLink = null;
			Integer num = null;
			HGraph ownerGraph = this.GetOwnerGraph();
			HSegment[] segmentsFromArray = this.GetSegmentsFromArray();
			HSegment[] segmentsToArray = this.GetSegmentsToArray();
			this._firstFromSegment = null;
			this._firstToSegment = null;
			for (num = 0; num < segmentsFromArray.length; num++) {

				ownerLink = segmentsFromArray[num].GetOwnerLink();
				if ((ownerLink != null)
						&& (ownerLink.GetOwnerGraph() == ownerGraph)) {
					ownerGraph.RemoveLink(ownerLink);
				}
			}
			for (num = 0; num < segmentsToArray.length; num++) {

				ownerLink = segmentsToArray[num].GetOwnerLink();
				if ((ownerLink != null)
						&& (ownerLink.GetOwnerGraph() == ownerGraph)) {
					ownerGraph.RemoveLink(ownerLink);
				}
			}
		}
		if (this.GetLevel() != null) {
			this.GetLevel().Remove(this);
		}

	}

	public void AddPredMedian(double factor) {
		double crossRedFromPosition = 0;
		Integer segmentsToCount = this.GetSegmentsToCount();
		if (segmentsToCount == 0) {
			crossRedFromPosition = 0.0;
			// NOTICE: break ignore!!!
		} else if (segmentsToCount == 1) {

			crossRedFromPosition = this.GetFirstSegmentTo()
					.GetCrossRedFromPosition();
			// NOTICE: break ignore!!!
		} else if (segmentsToCount == 2) {
			HSegment firstSegmentTo = this.GetFirstSegmentTo();
			double num3 = firstSegmentTo.GetCrossRedFromPosition();
			double num4 = firstSegmentTo.GetNextTo().GetCrossRedFromPosition();
			crossRedFromPosition = (num3 < num4) ? num3 : num4;

		} else {
			double[] array = new double[segmentsToCount];
			List<Double> l = new Vector<Double>();

			HSegmentIterator segmentsTo = this.GetSegmentsTo();
			Integer num5 = 0;

			while (segmentsTo.HasNext()) {
				l.add(segmentsTo.Next().GetCrossRedFromPosition());
				array[num5++] = segmentsTo.Next().GetCrossRedFromPosition();
			}
			Collections.sort(l);
			// Array.Sort(array);
			crossRedFromPosition = l.get(segmentsToCount / 2);// array[segmentsToCount
																// / 2];

		}
		this.SetSortValue(this.GetSortValue()
				+ ((float) (factor * crossRedFromPosition)));

	}

	public void AddSuccMedian(double factor) {
		double crossRedToPosition = 0;
		Integer segmentsFromCount = this.GetSegmentsFromCount();
		if (segmentsFromCount == 0) {
			crossRedToPosition = 0.0;
			// NOTICE: break ignore!!!
		} else if (segmentsFromCount == 1) {

			crossRedToPosition = this.GetFirstSegmentFrom()
					.GetCrossRedToPosition();
			// NOTICE: break ignore!!!
		} else if (segmentsFromCount == 2) {
			HSegment firstSegmentFrom = this.GetFirstSegmentFrom();
			double num3 = firstSegmentFrom.GetCrossRedToPosition();
			double num4 = firstSegmentFrom.GetNextFrom()
					.GetCrossRedToPosition();
			crossRedToPosition = (num3 < num4) ? num3 : num4;

		} else {
			double[] array = new double[segmentsFromCount];
			HSegmentIterator segmentsFrom = this.GetSegmentsFrom();
			Integer num5 = 0;
			List<Double> l = new Vector<Double>();
			while (segmentsFrom.HasNext()) {
				l.add(segmentsFrom.Next().GetCrossRedFromPosition());
				array[num5++] = segmentsFrom.Next().GetCrossRedToPosition();
			}
			Collections.sort(l);
			// Array.Sort(array);
			crossRedToPosition = l.get(segmentsFromCount / 2);// array[segmentsFromCount
																// / 2];

		}
		this.SetSortValue(this.GetSortValue()
				+ ((float) (factor * crossRedToPosition)));

	}

	public float CalcBackwardMoveVector(Integer dir, float fracEdges,
			float fracOldPos, float incrRange) {
		float maxValue = 0;

		if (this.IsFixedForIncremental(dir)) {
			this.SetMoveVector(0f);

			return 0f;
		}
		float num = 0f;
		float num2 = 0f;
		float center = this.GetCenter(dir);
		float oldCoordInLevelFlow = this.GetOldCoordInLevelFlow();
		HSegmentIterator segmentsFrom = this.GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {
			HSegment segment = segmentsFrom.Next();
			float priority = segment.GetPriority();
			HNode to = segment.GetTo();
			num += priority;
			num2 += priority * to.GetCenter(dir);
		}
		if (num != 0f) {
			float num7 = num2 / num;
			if (oldCoordInLevelFlow == Float.MAX_VALUE) {
				maxValue = num7 - center;
			} else {
				float num8 = (num7 > oldCoordInLevelFlow) ? (num7 - oldCoordInLevelFlow)
						: (oldCoordInLevelFlow - num7);
				if (num8 < incrRange) {
					maxValue = num7 - center;
				} else {
					maxValue = (fracEdges * (num7 - center))
							+ (fracOldPos * (oldCoordInLevelFlow - center));
				}
			}
		} else if ((oldCoordInLevelFlow == Float.MAX_VALUE)
				|| (fracOldPos == 0f)) {
			maxValue = Float.MAX_VALUE;
		} else {
			maxValue = oldCoordInLevelFlow - center;
		}
		this.SetMoveVector(maxValue);

		return maxValue;

	}

	public float CalcBackwardSpecialMoveVector(Integer dir, float fracEdges,
			float fracOldPos, float incrRange) {
		if ((this.GetSegmentsFromCount() == 1)
				&& (this.GetSegmentsToCount() > 1)) {

			return this.CalcForwardMoveVector(dir, fracEdges, fracOldPos,
					incrRange);
		}

		return this.CalcBackwardMoveVector(dir, fracEdges, fracOldPos,
				incrRange);

	}

	public float CalcForwardMoveVector(Integer dir, float fracEdges,
			float fracOldPos, float incrRange) {
		float maxValue = 0;

		if (this.IsFixedForIncremental(dir)) {
			this.SetMoveVector(0f);

			return 0f;
		}
		float num = 0f;
		float num2 = 0f;
		float center = this.GetCenter(dir);
		float oldCoordInLevelFlow = this.GetOldCoordInLevelFlow();
		HSegmentIterator segmentsTo = this.GetSegmentsTo();

		while (segmentsTo.HasNext()) {
			HSegment segment = segmentsTo.Next();
			float priority = segment.GetPriority();
			HNode from = segment.GetFrom();
			num += priority;
			num2 += priority * from.GetCenter(dir);
		}
		if (num != 0f) {
			float num7 = num2 / num;
			if (oldCoordInLevelFlow == Float.MAX_VALUE) {
				maxValue = num7 - center;
			} else {
				float num8 = (num7 > oldCoordInLevelFlow) ? (num7 - oldCoordInLevelFlow)
						: (oldCoordInLevelFlow - num7);
				if (num8 < incrRange) {
					maxValue = num7 - center;
				} else {
					maxValue = (fracEdges * (num7 - center))
							+ (fracOldPos * (oldCoordInLevelFlow - center));
				}
			}
		} else if ((oldCoordInLevelFlow == Float.MAX_VALUE)
				|| (fracOldPos == 0f)) {
			maxValue = Float.MAX_VALUE;
		} else {
			maxValue = oldCoordInLevelFlow - center;
		}
		this.SetMoveVector(maxValue);

		return maxValue;

	}

	public float CalcForwardSpecialMoveVector(Integer dir, float fracEdges,
			float fracOldPos, float incrRange) {
		if ((this.GetSegmentsToCount() == 1)
				&& (this.GetSegmentsFromCount() > 1)) {

			return this.CalcBackwardMoveVector(dir, fracEdges, fracOldPos,
					incrRange);
		}

		return this
				.CalcForwardMoveVector(dir, fracEdges, fracOldPos, incrRange);

	}

	public void CalcPredBaryCenter(Integer numNodes) {
		double num = 0.0;
		double num2 = 0.0;
		float sortValue = 0f;
		if (numNodes > 0) {
			num2 = (1E-05 * this.GetPositionInLevel()) / ((double) numNodes);
		}
		HSegmentIterator segmentsTo = this.GetSegmentsTo();

		while (segmentsTo.HasNext()) {
			HSegment segment = segmentsTo.Next();
			double priority = segment.GetPriority();
			double crossRedFromPosition = segment.GetCrossRedFromPosition();
			if (priority == 0.0) {
				sortValue = (float) crossRedFromPosition;
			}
			num += priority;
			num2 += priority * crossRedFromPosition;
		}
		if (num != 0.0) {
			this.SetSortValue((float) (num2 / num));
		} else {
			this.SetSortValue(sortValue);
		}

	}

	public void CalcPredBaryCenterForPorts() {
		double num = 0.0;
		double num2 = 0.0;
		HSegmentIterator segmentsTo = this.GetSegmentsTo();

		while (segmentsTo.HasNext()) {
			double num3 = 0;
			HSegment segment = segmentsTo.Next();
			if (segment.GetFromPortSide() == 3
					|| segment.GetFromPortSide() == 1) {
				num3 = 1000.0;
				// NOTICE: break ignore!!!
			} else {
				if (segment.GetFromPortNumber() != -1) {
					num3 = 50.0;
				} else if (segment.IsFromSideFixed()) {
					num3 = 50.0;
				} else {
					num3 = 1E-05;
				}
				// NOTICE: break ignore!!!
			}
			double crossRedFromPosition = segment.GetCrossRedFromPosition();
			num += num3;
			num2 += num3 * crossRedFromPosition;
		}
		if (num != 0.0) {
			this.SetSortValue((float) (num2 / num));
		} else {
			this.SetSortValue(0f);
		}

	}

	public void CalcSuccBaryCenter(Integer numNodes) {
		double num = 0.0;
		double num2 = 0.0;
		float sortValue = 0f;
		if (numNodes > 0) {
			num2 = (1E-05 * this.GetPositionInLevel()) / ((double) numNodes);
		}
		HSegmentIterator segmentsFrom = this.GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {
			HSegment segment = segmentsFrom.Next();
			double priority = segment.GetPriority();
			double crossRedToPosition = segment.GetCrossRedToPosition();
			if (priority == 0.0) {
				sortValue = (float) crossRedToPosition;
			}
			num += priority;
			num2 += priority * crossRedToPosition;
		}
		if (num != 0.0) {
			this.SetSortValue((float) (num2 / num));
		} else {
			this.SetSortValue(sortValue);
		}

	}

	public void CalcSuccBaryCenterForPorts() {
		double num = 0.0;
		double num2 = 0.0;
		HSegmentIterator segmentsFrom = this.GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {
			double num3 = 0;
			HSegment segment = segmentsFrom.Next();
			if (segment.GetToPortSide() == 3 || segment.GetToPortSide() == 1) {
				num3 = 1000.0;
				// NOTICE: break ignore!!!
			} else {
				if (segment.GetToPortNumber() != -1) {
					num3 = 50.0;
				} else if (segment.IsToSideFixed()) {
					num3 = 50.0;
				} else {
					num3 = 1E-05;
				}
				// NOTICE: break ignore!!!
			}
			double crossRedToPosition = segment.GetCrossRedToPosition();
			num += num3;
			num2 += num3 * crossRedToPosition;
		}
		if (num != 0.0) {
			this.SetSortValue((float) (num2 / num));
		} else {
			this.SetSortValue(0f);
		}

	}

	public Boolean ContainsFromSegment(HSegment segment) {

		return ((segment != null) && segment
				.IsContainedInFromSegments(this._firstFromSegment));

	}

	public Boolean ContainsToSegment(HSegment segment) {

		return ((segment != null) && segment
				.IsContainedInToSegments(this._firstToSegment));

	}

	public void CorrectEastWestAuxSize() {
		HSegment segment = null;
		float thickness = 0;
		HGraph ownerGraph = this.GetOwnerGraph();
		Integer levelFlow = ownerGraph.GetLevelFlow();
		float minDistBetweenLinks = ownerGraph
				.GetMinDistBetweenLinks(levelFlow);
		float size = minDistBetweenLinks;
		float num4 = minDistBetweenLinks;
		Boolean flag = false;
		HSegmentIterator segmentsFrom = this.GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();
			if (segment.GetFromPortNumber() >= 0) {
				flag = true;
			}

			thickness = segment.GetOwnerLink().GetThickness();
			size += thickness + minDistBetweenLinks;
		}

		segmentsFrom = this.GetSegmentsTo();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();
			if (segment.GetToPortNumber() >= 0) {
				flag = true;
			}

			thickness = segment.GetOwnerLink().GetThickness();
			num4 += thickness + minDistBetweenLinks;
		}
		if (flag) {
			if (((size + num4) - minDistBetweenLinks) > this.GetSize(levelFlow)) {
				this.SetSize(levelFlow, (size + num4) - minDistBetweenLinks);
			}
		} else if (size > num4) {
			if (size > this.GetSize(levelFlow)) {
				this.SetSize(levelFlow, size);
			}
		} else if (num4 > this.GetSize(levelFlow)) {
			this.SetSize(levelFlow, num4);
		}

	}

	public void CorrectPortNumbers() {
		if (this._portCache != null) {
			HSegment segment = null;
			Integer fromPortSide = null;
			Integer num2 = null;
			HSegmentIterator segmentsFrom = this.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();

				fromPortSide = segment.GetFromPortSide();
				num2 = segment.GetFromPortNumber() + 1;
				if (this._portCache.GetNumberOfPorts(fromPortSide) < num2) {
					this._portCache.SetNumberOfPorts(fromPortSide, num2);
				}
			}

			segmentsFrom = this.GetSegmentsTo();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();

				fromPortSide = segment.GetToPortSide();
				num2 = segment.GetToPortNumber() + 1;
				if (this._portCache.GetNumberOfPorts(fromPortSide) < num2) {
					this._portCache.SetNumberOfPorts(fromPortSide, num2);
				}
			}
		}

	}

	public HNode CreateEastPortAuxNode() {
		if (this._portCache == null) {

			return null;
		}
		if (this._portCache.GetEastPortAuxNode() == null) {
			HGraph ownerGraph = this.GetOwnerGraph();
			Integer edgeFlow = ownerGraph.GetEdgeFlow();
			Integer levelFlow = ownerGraph.GetLevelFlow();
			HNode node = ownerGraph.NewHNode();

			if (this.IsFixedForIncremental(edgeFlow)) {
				node.SetCoord(0, this.GetCoord(0));
				node.SetCoord(1, this.GetCoord(1));
				node.SetFixedForIncremental(edgeFlow);
			}
			this._portCache.SetEastPortAuxNode(node);
			node.GetPortCache().MarkEastWestAuxNode();
			node.SetSwimLane(this.GetSwimLane());
			node.SetLevelNumber(this.GetLevelNumber());
			node.SetSortValue(this.GetSortValue() + 0.1f);
			node.SetSize(edgeFlow, this.GetSize(edgeFlow));
			node.SetSize(levelFlow,
					ownerGraph.GetMinDistBetweenLinks(levelFlow));
			float size = 1f + ownerGraph.GetMinDistBetweenLinks(edgeFlow);
			if (node.GetSize(edgeFlow) < size) {
				node.SetSize(edgeFlow, size);
			}
		}

		return this._portCache.GetEastPortAuxNode();

	}

	public HNode CreateWestPortAuxNode() {
		if (this._portCache == null) {

			return null;
		}
		if (this._portCache.GetWestPortAuxNode() == null) {
			HGraph ownerGraph = this.GetOwnerGraph();
			Integer edgeFlow = ownerGraph.GetEdgeFlow();
			Integer levelFlow = ownerGraph.GetLevelFlow();
			HNode node = ownerGraph.NewHNode();

			if (this.IsFixedForIncremental(edgeFlow)) {
				node.SetCoord(0, this.GetCoord(0));
				node.SetCoord(1, this.GetCoord(1));
				node.SetFixedForIncremental(edgeFlow);
			}
			this._portCache.SetWestPortAuxNode(node);
			node.GetPortCache().MarkEastWestAuxNode();
			node.SetSwimLane(this.GetSwimLane());
			node.SetLevelNumber(this.GetLevelNumber());
			node.SetSortValue(this.GetSortValue() - 0.1f);
			node.SetSize(edgeFlow, this.GetSize(edgeFlow));
			node.SetSize(levelFlow,
					ownerGraph.GetMinDistBetweenLinks(levelFlow));
			float size = 1f + ownerGraph.GetMinDistBetweenLinks(edgeFlow);
			if (node.GetSize(edgeFlow) < size) {
				node.SetSize(edgeFlow, size);
			}
		}

		return this._portCache.GetWestPortAuxNode();

	}

	public void FromAdd(HSegment segment, HSegment location) {
		if (location == null) {
			segment.SetNextFrom(this._firstFromSegment);
			this._firstFromSegment = segment;
		} else {
			segment.SetNextFrom(location.GetNextFrom());
			location.SetNextFrom(segment);
		}
		this._outdegree++;
		if (this._portCache != null) {
			this._portCache.InvalidateFrom();
		}

	}

	public HSegment FromRemove(HSegment segment) {
		HSegment segment2 = null;
		if (this._firstFromSegment == segment) {

			this._firstFromSegment = segment.GetNextFrom();
		} else if (this._firstFromSegment != null) {
			HSegment segment3 = this._firstFromSegment;
			HSegment nextFrom = segment3.GetNextFrom();
			while ((nextFrom != null) && (nextFrom != segment)) {
				segment3 = nextFrom;

				nextFrom = nextFrom.GetNextFrom();
			}
			if (nextFrom == segment) {
				segment3.SetNextFrom(segment.GetNextFrom());
				segment2 = segment3;
			}
		}
		this._outdegree--;
		if (this._portCache != null) {
			this._portCache.InvalidateFrom();
		}

		return segment2;

	}

	public float GetCenter(Integer index) {

		return (this._coord[index] + (0.5f * this._size[index]));

	}

	public float GetCoord(Integer index) {

		return this._coord[index];

	}

	public float[] GetCoordField() {

		return this._coord;

	}

	public HNode GetEastPortAuxNode() {
		if (this._portCache == null) {

			return null;
		}

		return this._portCache.GetEastPortAuxNode();

	}

	public HNode GetEastWestPortAuxOwner() {
		HNode prevNeighbor = this.GetPrevNeighbor();
		if ((prevNeighbor != null)
				&& (prevNeighbor.GetEastPortAuxNode() == this)) {

			return prevNeighbor;
		}

		prevNeighbor = this.GetNextNeighbor();
		if ((prevNeighbor != null)
				&& (prevNeighbor.GetWestPortAuxNode() == this)) {

			return prevNeighbor;
		}
		HNodeIterator nodes = this.GetOwnerGraph().GetNodes();

		while (nodes.HasNext()) {

			prevNeighbor = nodes.Next();
			if (prevNeighbor.GetEastPortAuxNode() == this) {

				return prevNeighbor;
			}
			if (prevNeighbor.GetWestPortAuxNode() == this) {

				return prevNeighbor;
			}
		}

		return null;

	}

	public HSegment GetFirstSegmentFrom() {

		return this._firstFromSegment;

	}

	public HSegment GetFirstSegmentTo() {

		return this._firstToSegment;

	}

	public float GetHeight() {

		return this._size[1];

	}

	public Integer GetInDegreeWithoutSameLevelPortSideLinks() {
		Integer num = 0;
		HSegmentIterator segmentsTo = this.GetSegmentsTo();

		while (segmentsTo.HasNext()) {
			HSegment segment = segmentsTo.Next();
			Integer fromPortSide = segment.GetFromPortSide();
			Integer toPortSide = segment.GetToPortSide();
			if ((((fromPortSide != 0) || (toPortSide != 0)) && ((fromPortSide != 2) || (toPortSide != 2)))
					&& (((fromPortSide != 1) || (toPortSide != 3)) && ((fromPortSide != 3) || (toPortSide != 1)))) {
				num++;
			}
		}

		return num;

	}

	public Integer GetInvalidMoveVectorMarker() {

		return this._generalIntValue;

	}

	public HLevel GetLevel() {
		if (this._levelNumber < 0) {

			return null;
		}

		return this.GetOwnerGraph().GetLevel(this._levelNumber);

	}

	public Integer GetLevelNumber() {

		return this._levelNumber;

	}

	public Integer GetMarker() {

		return this._generalIntValue;

	}

	public float GetMoveVector() {

		return this._generalFloatValue;

	}

	public HNode GetNextNeighbor() {
		HLevel level = this.GetLevel();
		Integer index = this.GetPositionInLevel() + 1;
		if (index < level.GetNodesCount()) {

			return level.GetNode(index);
		}

		return null;

	}

	public Integer GetNumberOfPorts(Integer side) {
		if (this._portCache == null) {

			return 0;
		}

		return this._portCache.GetNumberOfPorts(side);

	}

	public float GetOffsetToRealTopLeft(Integer index) {
		if (this._realBounds == null) {

			return 0f;
		}

		return this._realBounds[index];

	}

	public float GetOldCoordInLevelFlow() {

		return this._oldCoordInLevelFlow;

	}

	public float GetOldSizeInEdgeFlow() {

		return this._oldSizeInEdgeFlow;

	}

	public HSegment GetOpposite(HSegment segment) {
		if (this._firstFromSegment != null) {
			if (this._firstFromSegment != segment) {

				return this._firstFromSegment;
			}
			if (this._firstFromSegment.GetNextFrom() != null) {

				return this._firstFromSegment.GetNextFrom();
			}
		}
		if (this._firstToSegment != null) {
			if (this._firstToSegment != segment) {

				return this._firstToSegment;
			}
			if (this._firstToSegment.GetNextTo() != null) {

				return this._firstToSegment.GetNextTo();
			}
		}

		return null;

	}

	@Override
	public HGraph GetOwnerGraph() {

		if (this.IsDummyNode()) {

			return this.GetOwnerLink().GetOwnerGraph();
		}

		return (HGraph) super.GetOwner();

	}

	public HLink GetOwnerLink() {

		if (this.IsDummyNode()) {

			return (HLink) super.GetOwner();
		}

		return null;

	}

	public HPortCache GetPortCache() {

		return this._portCache;

	}

	public Integer GetPositionInLevel() {

		return this._positionInLevel;

	}

	public HNode GetPrevNeighbor() {
		HLevel level = this.GetLevel();
		Integer index = this.GetPositionInLevel() - 1;
		if (index >= 0) {

			return level.GetNode(index);
		}

		return null;

	}

	public float GetRealMinCoord(Integer index) {
		if (this._realBounds == null) {

			return this._coord[index];
		}

		return (this._coord[index] + this._realBounds[index]);

	}

	public float GetRealSize(Integer index) {
		if (this._realBounds == null) {

			return this.GetSize(index);
		}

		return this._realBounds[2 + index];

	}

	public Integer GetRegionEndMarker() {

		return this._generalIntValue;

	}

	public HSegmentIterator GetSegments() {

		return new AnonClass_5(this);

	}

	public HSegmentIterator GetSegmentsFrom() {

		return new AnonClass_1(this);

	}

	private HSegment[] GetSegmentsFromArray() {
		HSegment[] segmentArray = new HSegment[this.GetSegmentsFromCount()];
		HSegmentIterator segmentsFrom = this.GetSegmentsFrom();
		Integer num = 0;

		while (segmentsFrom.HasNext()) {

			segmentArray[num++] = segmentsFrom.Next();
		}

		return segmentArray;

	}

	public Integer GetSegmentsFromCount() {

		return this._outdegree;

	}

	public HSegmentIterator GetSegmentsFromInReverseOrder() {

		return new AnonClass_2(this);

	}

	public HSegmentIterator GetSegmentsTo() {

		return new AnonClass_3(this);

	}

	private HSegment[] GetSegmentsToArray() {
		HSegment[] segmentArray = new HSegment[this.GetSegmentsToCount()];
		HSegmentIterator segmentsTo = this.GetSegmentsTo();
		Integer num = 0;

		while (segmentsTo.HasNext()) {

			segmentArray[num++] = segmentsTo.Next();
		}

		return segmentArray;

	}

	public Integer GetSegmentsToCount() {

		return this._indegree;

	}

	public HSegmentIterator GetSegmentsToInReverseOrder() {

		return new AnonClass_4(this);

	}

	public float GetSize(Integer index) {

		return this._size[index];

	}

	public float GetSortValue() {

		return this._generalFloatValue;

	}

	public Integer GetSpecLevelNumber() {

		return this._specLevelNumber;

	}

	public Integer GetSpecPositionInLevel() {

		return this._specPositionInLevel;

	}

	public HSwimLane GetSwimLane() {

		return this._swimLane;

	}

	public HNode GetWestPortAuxNode() {
		if (this._portCache == null) {

			return null;
		}

		return this._portCache.GetWestPortAuxNode();

	}

	public float GetWidth() {

		return this._size[0];

	}

	public float GetX() {

		return this._coord[0];

	}

	public float GetY() {

		return this._coord[1];

	}

	private void Init() {
		this._firstFromSegment = null;
		this._firstToSegment = null;
		this._outdegree = 0;
		this._indegree = 0;
		this._coord = new float[] { 0f, 0f };
		this._size = new float[] { 0f, 0f };
		this._realBounds = null;
		this._specLevelNumber = -1;
		this._specPositionInLevel = -1;
		this._levelNumber = -1;
		this._positionInLevel = -1;
		this._oldCoordInLevelFlow = Float.MAX_VALUE;
		this._oldSizeInEdgeFlow = 0f;
		this._markedForIncremental = false;
		this._fixedForIncremental = new Boolean[] { false, false };
		this._portCache = null;
		this._swimLane = null;

	}

	public Boolean IsBypassNode() {

		if (!this.IsDummyNode()) {

			return false;
		}

		return (((this.GetSegmentsFromCount() <= 0) && (this
				.GetSegmentsToCount() == 2)) || ((this.GetSegmentsFromCount() == 2) && (this
				.GetSegmentsToCount() <= 0)));

	}

	public Boolean IsDummyNode() {

		return (super.GetOwner() instanceof HLink);

	}

	public Boolean IsEastWestPortAuxNode() {
		if (this._portCache == null) {

			return false;
		}

		return this._portCache.IsEastWestAuxNode();

	}

	public Boolean IsFixedForIncremental(Integer index) {

		return this._fixedForIncremental[index];

	}

	public Boolean IsInvalid() {

		return ((this._coord[0] == Float.MAX_VALUE) && (this._coord[1] == Float.MAX_VALUE));

	}

	public Boolean IsLowerBypassNode() {

		return ((this.IsDummyNode() && (this.GetSegmentsFromCount() <= 0)) && (this
				.GetSegmentsToCount() == 2));

	}

	public Boolean IsMarkedForIncremental() {

		return this._markedForIncremental;

	}

	public Boolean IsUpperBypassNode() {

		return ((this.IsDummyNode() && (this.GetSegmentsFromCount() == 2)) && (this
				.GetSegmentsToCount() <= 0));

	}

	public void MarkForIncremental() {
		this._markedForIncremental = true;
		this.MarkToMoveFreeOnIncremental();

	}

	public void MarkInvalid() {
		this._coord[0] = Float.MAX_VALUE;
		this._coord[1] = Float.MAX_VALUE;

	}

	public void MarkToMoveFreeOnIncremental() {
		this._oldCoordInLevelFlow = Float.MAX_VALUE;

	}

	public void Mirror(Integer coordinateIndex) {

		if (!this.IsInvalid()) {
			this._coord[coordinateIndex] = -this._coord[coordinateIndex]
					- this._size[coordinateIndex];
			Integer levelFlow = this.GetOwnerGraph().GetLevelFlow();
			if ((coordinateIndex == levelFlow)
					&& (this._oldCoordInLevelFlow != Float.MAX_VALUE)) {
				this._oldCoordInLevelFlow = -this._oldCoordInLevelFlow;
			}
		}

	}

	public void Remove(HSegment segment) {
		if (segment.GetFrom() == this) {
			this.FromRemove(segment);
		}
		if (segment.GetTo() == this) {
			this.ToRemove(segment);
		}

	}

	public void ResolveNorthOrSouth() {

		if (!this.IsDummyNode()) {
			HSegment segment = null;
			HLink ownerLink = null;
			HSegmentIterator segmentsFrom = this.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();

				ownerLink = segment.GetOwnerLink();

				if (segment.IsReversed()) {
					ownerLink.SetToPortSide(this.ResolveNorthOrSouth(
							ownerLink.GetToPortSide(), segment, 2));
				} else {
					ownerLink.SetFromPortSide(this.ResolveNorthOrSouth(
							ownerLink.GetFromPortSide(), segment, 2));
				}
			}

			segmentsFrom = this.GetSegmentsTo();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();

				ownerLink = segment.GetOwnerLink();

				if (segment.IsReversed()) {
					ownerLink.SetFromPortSide(this.ResolveNorthOrSouth(
							ownerLink.GetFromPortSide(), segment, 0));
				} else {
					ownerLink.SetToPortSide(this.ResolveNorthOrSouth(
							ownerLink.GetToPortSide(), segment, 0));
				}
			}
		}

	}

	private Integer ResolveNorthOrSouth(Integer actSide, HSegment segment,
			Integer defSide) {
		if (actSide == -2) {

			return defSide;
		} else if (actSide == -1) {
			if (segment.GetSpan() != 0) {

				return defSide;
			}

			return -3;
		}

		return actSide;

	}

	public void RestoreBestPosition() {
		this.SetSortValue((float) this._generalIntValue);

	}

	public void SaveBestPosition() {

		this._generalIntValue = this.GetPositionInLevel();

	}

	public void SetBoundingBoxes(InternalRect origBBox, InternalRect realBBox) {

		if (!realBBox.equals(origBBox)) {
			this._realBounds = new float[] { realBBox.X - origBBox.X,
					realBBox.Y - origBBox.Y, realBBox.Width, realBBox.Height };
		}
		this.SetOldSizeInEdgeFlow(origBBox);
		if (origBBox.Width >= 0f) {
			this._coord[0] = origBBox.X;
			this._size[0] = origBBox.Width;
		} else {
			this._coord[0] = origBBox.X + origBBox.Width;
			this._size[0] = -origBBox.Width;
		}
		if (origBBox.Height >= 0f) {
			this._coord[1] = origBBox.Y;
			this._size[1] = origBBox.Height;
		} else {
			this._coord[1] = origBBox.Y + origBBox.Height;
			this._size[1] = -origBBox.Height;
		}
		this.GetOwnerGraph().GetFlowDirection();
		Integer levelFlow = this.GetOwnerGraph().GetLevelFlow();

		this._oldCoordInLevelFlow = this.GetCenter(levelFlow);

	}

	public void SetCoord(Integer index, float coord) {
		this._coord[index] = coord;

	}

	public void SetCoordWithLinkUpdate(Integer index, float coord) {
		CalcConnectors connectorAlgorithm = this.GetOwnerGraph()
				.GetConnectorAlgorithm();
		Integer connectorStyle = this.GetOwnerGraph().GetConnectorStyle();
		Integer levelFlow = this.GetOwnerGraph().GetLevelFlow();
		float num3 = coord - this._coord[index];
		this._coord[index] = coord;

		if (!this.IsDummyNode()) {
			HSegment segment = null;
			HSegmentIterator segmentsFrom = this.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();

				if ((!this.IsEastWestPortAuxNode() && (connectorStyle == 100))
						&& !segment.GetOwnerLink().IsOrthogonal()) {
					connectorAlgorithm.CalcClippedConnectors(segment
							.GetOwnerLink());
				} else {

					coord = segment.GetFromCoord(index);
					segment.GetFromPoint()[index] = coord + num3;
					if (index == levelFlow) {
						segment.SetFromForkPoint(segment
								.GetFromForkPoint(index) + num3);
					}
				}
			}

			segmentsFrom = this.GetSegmentsTo();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();

				if ((!this.IsEastWestPortAuxNode() && (connectorStyle == 100))
						&& !segment.GetOwnerLink().IsOrthogonal()) {
					connectorAlgorithm.CalcClippedConnectors(segment
							.GetOwnerLink());
				} else {

					coord = segment.GetToCoord(index);
					segment.GetToPoint()[index] = coord + num3;
					if (index == levelFlow) {
						segment.SetToForkPoint(segment.GetToForkPoint(index)
								+ num3);
					}
				}
			}
		} else if (connectorStyle == 100) {
			HSegment firstSegmentFrom = this.GetFirstSegmentFrom();
			if (firstSegmentFrom == null) {

				firstSegmentFrom = this.GetFirstSegmentTo();
			}
			HNode opposite = firstSegmentFrom.GetOpposite(this);

			if ((!opposite.IsDummyNode() && !opposite.IsEastWestPortAuxNode())
					&& !firstSegmentFrom.GetOwnerLink().IsOrthogonal()) {
				connectorAlgorithm.CalcClippedConnectors(firstSegmentFrom
						.GetOwnerLink());
			}

			firstSegmentFrom = this.GetOpposite(firstSegmentFrom);

			opposite = firstSegmentFrom.GetOpposite(this);

			if ((!opposite.IsDummyNode() && !opposite.IsEastWestPortAuxNode())
					&& !firstSegmentFrom.GetOwnerLink().IsOrthogonal()) {
				connectorAlgorithm.CalcClippedConnectors(firstSegmentFrom
						.GetOwnerLink());
			}
		}

	}

	public void SetFixedForIncremental(Integer index) {
		this._fixedForIncremental[index] = true;

	}

	public void SetInvalidMoveVectorMarker(Integer marker) {
		this._generalIntValue = marker;

	}

	public void SetLevelNumber(Integer levelNumber) {
		this._levelNumber = levelNumber;

	}

	public void SetMarker(Integer marker) {
		this._generalIntValue = marker;

	}

	public void SetMoveVector(float moveVector) {
		this._generalFloatValue = moveVector;

	}

	public void SetNumberOfPorts(Integer[] numPorts) {
		if (this._portCache != null) {
			this._portCache.SetNumberOfPorts(numPorts);
		}

	}

	public void SetNumberOfPorts(Integer side, Integer numPorts) {
		if (this._portCache != null) {
			this._portCache.SetNumberOfPorts(side, numPorts);
		}

	}

	public void SetOldSizeInEdgeFlow(InternalRect rect) {
		if (rect != null) {
			if (this.GetOwnerGraph().GetEdgeFlow() == 0) {
				this._oldSizeInEdgeFlow = rect.Width;
			} else {
				this._oldSizeInEdgeFlow = rect.Height;
			}
		}

	}

	public void SetPositionInLevel(Integer positionInLevel) {
		this._positionInLevel = positionInLevel;

	}

	public void SetRegionEndMarker(Integer marker) {
		this._generalIntValue = marker;

	}

	private void SetSegmentsFrom(HSegment[] array) {
		this._outdegree = array.length;
		if (this._outdegree > 0) {
			this._firstFromSegment = array[0];
			array[this._outdegree - 1].SetNextFrom(null);
		} else {
			this._firstFromSegment = null;
		}
		for (Integer i = 1; i < this._outdegree; i++) {
			array[i - 1].SetNextFrom(array[i]);
		}

	}

	private void SetSegmentsTo(HSegment[] array) {
		this._indegree = array.length;
		if (this._indegree > 0) {
			this._firstToSegment = array[0];
			array[this._indegree - 1].SetNextTo(null);
		} else {
			this._firstToSegment = null;
		}
		for (Integer i = 1; i < this._indegree; i++) {
			array[i - 1].SetNextTo(array[i]);
		}

	}

	public void SetSize(Integer index, float size) {
		this._size[index] = size;

	}

	public void SetSortValue(float sortValue) {
		this._generalFloatValue = sortValue;

	}

	public void SetSpecLevelNumber(Integer levelNumber) {
		this._specLevelNumber = levelNumber;

	}

	public void SetSpecPositionInLevel(Integer posInLevel) {
		this._specPositionInLevel = posInLevel;

	}

	public void SetSwimLane(HSwimLane swimLane) {
		this._swimLane = swimLane;

	}

	public void ShiftBy(float dx, float dy) {

		if (!this.IsInvalid()) {
			this._coord[0] += dx;
			this._coord[1] += dy;
		}

	}

	public void ShiftCoord(Integer index, float coord) {
		this._coord[index] += coord;

	}

	public void SortFromSegments(ArrayStableSort segmentSortAlg) {
		if (this._firstFromSegment != null) {
			HSegment[] segmentsFromArray = this.GetSegmentsFromArray();
			segmentSortAlg.Sort(segmentsFromArray);
			this.SetSegmentsFrom(segmentsFromArray);
		}

	}

	public void SortToSegments(ArrayStableSort segmentSortAlg) {
		if (this._firstToSegment != null) {
			HSegment[] segmentsToArray = this.GetSegmentsToArray();
			segmentSortAlg.Sort(segmentsToArray);
			this.SetSegmentsTo(segmentsToArray);
		}

	}

	public void ToAdd(HSegment segment, HSegment location) {
		if (location == null) {
			segment.SetNextTo(this._firstToSegment);
			this._firstToSegment = segment;
		} else {
			segment.SetNextTo(location.GetNextTo());
			location.SetNextTo(segment);
		}
		this._indegree++;
		if (this._portCache != null) {
			this._portCache.InvalidateTo();
		}

	}

	public HSegment ToRemove(HSegment segment) {
		HSegment segment2 = null;
		if (this._firstToSegment == segment) {

			this._firstToSegment = segment.GetNextTo();
		} else if (this._firstToSegment != null) {
			HSegment segment3 = this._firstToSegment;
			HSegment nextTo = segment3.GetNextTo();
			while ((nextTo != null) && (nextTo != segment)) {
				segment3 = nextTo;

				nextTo = nextTo.GetNextTo();
			}
			if (nextTo == segment) {
				segment3.SetNextTo(segment.GetNextTo());
				segment2 = segment3;
			}
		}
		this._indegree--;
		if (this._portCache != null) {
			this._portCache.InvalidateTo();
		}

		return segment2;

	}

	private class AnonClass_1 implements HSegmentIterator {
		private HNode __outerThis;

		public HSegment _next;

		public AnonClass_1(HNode input__outerThis) {
			this.__outerThis = input__outerThis;
			this._next = this.__outerThis._firstFromSegment;
		}

		public Boolean HasNext() {

			return (this._next != null);

		}

		public HSegment Next() {
			HSegment segment = this._next;

			this._next = this._next.GetNextFrom();

			return segment;

		}

	}

	private class AnonClass_2 implements HSegmentIterator {
		private HNode __outerThis;

		public HSegment[] _array;

		public Integer _count;

		public AnonClass_2(HNode input__outerThis) {
			this.__outerThis = input__outerThis;

			this._array = this.__outerThis.GetSegmentsFromArray();
			this._count = this._array.length - 1;
		}

		public Boolean HasNext() {

			return (this._count >= 0);

		}

		public HSegment Next() {

			return this._array[this._count--];

		}

	}

	private class AnonClass_3 implements HSegmentIterator {
		private HNode __outerThis;

		public HSegment _next;

		public AnonClass_3(HNode input__outerThis) {
			this.__outerThis = input__outerThis;
			this._next = this.__outerThis._firstToSegment;
		}

		public Boolean HasNext() {

			return (this._next != null);

		}

		public HSegment Next() {
			HSegment segment = this._next;

			this._next = this._next.GetNextTo();

			return segment;

		}

	}

	private class AnonClass_4 implements HSegmentIterator {
		private HNode __outerThis;

		public HSegment[] _array;

		public Integer _count;

		public AnonClass_4(HNode input__outerThis) {
			this.__outerThis = input__outerThis;

			this._array = this.__outerThis.GetSegmentsToArray();
			this._count = this._array.length - 1;
		}

		public Boolean HasNext() {

			return (this._count >= 0);

		}

		public HSegment Next() {

			return this._array[this._count--];

		}

	}

	private class AnonClass_5 implements HSegmentIterator {
		private HNode __outerThis;

		public HSegment _nextFrom;

		public HSegment _nextTo;

		public AnonClass_5(HNode input__outerThis) {
			this.__outerThis = input__outerThis;
			this._nextFrom = this.__outerThis._firstFromSegment;
			this._nextTo = this.__outerThis._firstToSegment;
		}

		public Boolean HasNext() {
			if (this._nextFrom == null) {

				return (this._nextTo != null);
			}

			return true;

		}

		public HSegment Next() {
			if (this._nextFrom != null) {
				HSegment segment = this._nextFrom;

				this._nextFrom = this._nextFrom.GetNextFrom();

				return segment;
			}
			HSegment segment2 = this._nextTo;

			this._nextTo = this._nextTo.GetNextTo();

			return segment2;

		}

	}
}
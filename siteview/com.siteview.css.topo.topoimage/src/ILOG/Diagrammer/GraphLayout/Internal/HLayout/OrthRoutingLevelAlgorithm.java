package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;
import system.Collections.*;

public final class OrthRoutingLevelAlgorithm extends HLevelAlgorithm {
	private SegmentInterval[] _endIntervals;

	private float _halfMinDelta;

	private SegmentInterval[] _intervals;

	private Boolean _lowerLevelHasForks = false;

	private float _maxLayerNumber = 0f;

	private Integer _numberOfBends = 0;

	private HSegmentSetMarker _segmentMarker;

	private SegmentInterval[] _startIntervals;

	private Boolean _upperLevelHasForks = false;

	public Integer HH = 3;

	public Integer HL = 0;

	public Integer LH = 1;

	public Integer LL = 2;

	public OrthRoutingLevelAlgorithm(HGraph graph) {
		this._segmentMarker = new HSegmentSetMarker(graph, false);
		this._intervals = new SegmentInterval[graph.GetNumberOfSegments()];
		this._halfMinDelta = 0.5f * graph.GetMinDistBetweenLinks(graph
				.GetLevelFlow());
	}

	private void CalcFinalLayerNumbers() {
		SegmentInterval interval = null;
		Stack stack = new Stack();
		this._maxLayerNumber = 1f;
		for (Integer i = 0; i < this._startIntervals.length; i++) {
			interval = this._startIntervals[i];
			interval.SetLayerNumber(1f);
			if (interval.nrSmallerIntervals == 0) {
				stack.Push(interval);
			}
		}
		while (stack.get_Count() != 0) {
			interval = (SegmentInterval) stack.Pop();
			float layerNumber = interval.GetLayerNumber() + 1f;
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(interval.largerIntervals);

			while (enumerator.HasMoreElements()) {
				SegmentInterval interval2 = (SegmentInterval) enumerator
						.NextElement();
				interval2.SetLayerNumberAtLeastTo(layerNumber);
				interval2.nrSmallerIntervals--;
				if (layerNumber > this._maxLayerNumber) {
					this._maxLayerNumber = layerNumber;
				}
				if (interval2.nrSmallerIntervals == 0) {
					stack.Push(interval2);
				}
			}
		}
		stack = null;

	}

	private void CalcIntervalGraph() {
		this.CreateSegmentIntervals();
		new SegmentIntervalLowerBoundSort().Sort(this._startIntervals);
		new SegmentIntervalHigherBoundSort().Sort(this._endIntervals);
		Integer index = 0;
		Integer num2 = 0;
		Integer length = this._startIntervals.length;
		while ((index < length) || (num2 < length)) {
			SegmentInterval interval = null;
			if ((index < length) && (num2 < length)) {
				if (this._startIntervals[index].GetLowerDeltaBound() <= this._endIntervals[num2]
						.GetHigherDeltaBound()) {
					interval = this._startIntervals[index++];
					this.ResolveConflicts(interval);
					this.MakeActive(interval);
				} else {
					interval = this._endIntervals[num2++];
					this.MakeInactive(interval);
				}
			} else if (index < length) {
				interval = this._startIntervals[index++];
				this.ResolveConflicts(interval);
				this.MakeActive(interval);
			} else {
				interval = this._endIntervals[num2++];
				this.MakeInactive(interval);
			}
		}

	}

	private void CheckForkNodes() {
		Integer levelFlowDir = super.GetLevelFlowDir();
		this._upperLevelHasForks = false;
		this._lowerLevelHasForks = false;
		if (super.GetGraph().GetLayout().get_FromFork()
				|| super.GetGraph().GetLayout().get_ToFork()) {
			HSegment segment = null;
			HSegmentIterator segmentsFrom = super.GetUpperLevel()
					.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();
				if (segment.GetFromForkPoint(levelFlowDir) != segment
						.GetFromCoord(levelFlowDir)) {
					this._upperLevelHasForks = true;
					break;
				}
			}

			segmentsFrom = super.GetLowerLevel().GetSegmentsTo();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();
				if (segment.GetToForkPoint(levelFlowDir) != segment
						.GetToCoord(levelFlowDir)) {
					this._lowerLevelHasForks = true;

					return;
				}
			}
		}

	}

	@Override
	public void Clean() {
		super.Clean();
		if (this._segmentMarker != null) {
			this._segmentMarker.Clean();
		}
		this._segmentMarker = null;
		this._intervals = null;
		this._startIntervals = null;
		this._endIntervals = null;

	}

	private void CleanSegmentIntervals() {
		for (Integer i = 0; i < this._startIntervals.length; i++) {
			this._intervals[this._startIntervals[i].firstSegment.GetNumID()] = null;
			this._startIntervals[i].Clean();
			this._startIntervals[i] = null;
			this._endIntervals[i] = null;
		}

	}

	private void CreateSegmentIntervals() {
		HSegment segment = null;
		Integer levelFlowDir = super.GetLevelFlowDir();
		HSegmentIterator segmentsFrom = super.GetUpperLevel().GetSegmentsFrom();
		Integer index = 0;

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();

			if (segment.GetFrom().IsBypassNode()) {

				if (segment.IsReversed()) {
					index++;
				}
			} else {

				if (segment.GetTo().IsBypassNode()) {

					if (segment.IsReversed()) {
						index++;
					}
					continue;
				}
				if ((segment.GetFromCoord(levelFlowDir) != segment
						.GetToCoord(levelFlowDir))
						&& segment.GetOwnerLink().IsOrthogonal()) {
					index++;
				}
			}
		}
		this._startIntervals = new SegmentInterval[index];
		this._endIntervals = new SegmentInterval[index];

		segmentsFrom = super.GetUpperLevel().GetSegmentsFrom();
		index = 0;

		while (segmentsFrom.HasNext()) {
			SegmentInterval interval = null;

			segment = segmentsFrom.Next();

			if (segment.GetFrom().IsBypassNode()) {

				if (segment.IsReversed()) {
					interval = new SegmentInterval(this, segment.GetFrom());
					this._startIntervals[index] = interval;
					this._endIntervals[index] = interval;
					index++;
				}
			} else {

				if (segment.GetTo().IsBypassNode()) {

					if (segment.IsReversed()) {
						interval = new SegmentInterval(this, segment.GetTo());
						this._startIntervals[index] = interval;
						this._endIntervals[index] = interval;
						index++;
					}
					continue;
				}
				if ((segment.GetFromCoord(levelFlowDir) != segment
						.GetToCoord(levelFlowDir))
						&& segment.GetOwnerLink().IsOrthogonal()) {
					interval = new SegmentInterval(this, segment);
					this._startIntervals[index] = interval;
					this._endIntervals[index] = interval;
					index++;
					continue;
				}
				segment.SetOrthogonalBend(0f);
			}
		}

	}

	private IntervalIterator GetActiveIntervals() {

		return new AnonClass_1(this);

	}

	public Integer GetNumberOfBends() {

		return this._numberOfBends;

	}

	private Boolean HaveConflict(SegmentInterval i1, SegmentInterval i2) {

		return ((((i1.intervalType == 0) || (i1.intervalType == 2)) && ((i2.intervalType == 3) || (i2.intervalType == 0))) || ((((i1.intervalType == 3) || (i1.intervalType == 1)) && ((i2.intervalType == 1) || (i2.intervalType == 2))) || Overlaps(
				i1, i2)));

	}

	@Override
	public void Init(HLevel upperLevel, HLevel lowerLevel) {
		super.Init(upperLevel, lowerLevel);
		this._maxLayerNumber = 0f;

	}

	private Boolean IsSmaller(SegmentInterval i1, SegmentInterval i2) {
		if (i1.intervalType != i2.intervalType) {
			if (i1.intervalType == 3) {

				return true;
			}
			if (i2.intervalType != 3) {
				if (i1.intervalType == 2) {

					return false;
				}
				if (i2.intervalType == 2) {

					return true;
				}
				if (i1.intervalType == 0) {

					return true;
				}
			}

			return false;
		}
		if (i1.intervalType == 3) {
			if (i1.higherCoord < i2.higherCoord) {

				return true;
			}
			if (i1.higherCoord > i2.higherCoord) {

				return false;
			}
			if (i1.lowerCoord > i2.lowerCoord) {

				return true;
			}
			if (i1.lowerCoord < i2.lowerCoord) {

				return false;
			}

			return (i1.firstSegment.GetNumID() < i2.firstSegment.GetNumID());
		}
		if (i1.intervalType == 2) {
			if (i1.lowerCoord < i2.lowerCoord) {

				return true;
			}
			if (i1.lowerCoord > i2.lowerCoord) {

				return false;
			}
			if (i1.higherCoord > i2.higherCoord) {

				return true;
			}
			if (i1.higherCoord < i2.higherCoord) {

				return false;
			}

			return (i1.firstSegment.GetNumID() < i2.firstSegment.GetNumID());
		}
		if (i1.intervalType == 1) {
			if ((i2.higherCoord <= i1.higherCoord)
					&& ((i2.higherCoord != i1.higherCoord) || (i2.lowerCoord <= i1.lowerCoord))) {

				return false;
			}

			return true;
		}
		if ((i2.lowerCoord >= i1.lowerCoord)
				&& ((i2.lowerCoord != i1.lowerCoord) || (i2.higherCoord >= i1.higherCoord))) {

			return false;
		}

		return true;

	}

	private void MakeActive(SegmentInterval i) {
		this._segmentMarker.Insert(i.firstSegment);

	}

	private void MakeInactive(SegmentInterval i) {
		this._segmentMarker.Discard(i.firstSegment);

	}

	private void OptimizeBypassDummyNodes() {
		HNode node = null;
		HSegment firstSegmentFrom = null;
		HNodeIterator nodes = super.GetUpperLevel().GetNodes();
		Integer edgeFlowDir = super.GetEdgeFlowDir();

		while (nodes.HasNext()) {

			node = nodes.Next();

			if (node.IsUpperBypassNode()) {

				firstSegmentFrom = node.GetFirstSegmentFrom();
				if (firstSegmentFrom.GetOrthogonalBend() != Float.MAX_VALUE) {
					node.SetCoord(edgeFlowDir,
							firstSegmentFrom.GetOrthogonalBend());
				}
			}
		}

		nodes = super.GetLowerLevel().GetNodes();

		while (nodes.HasNext()) {

			node = nodes.Next();

			if (node.IsLowerBypassNode()) {

				firstSegmentFrom = node.GetFirstSegmentTo();
				if (firstSegmentFrom.GetOrthogonalBend() != Float.MAX_VALUE) {
					node.SetCoord(edgeFlowDir,
							firstSegmentFrom.GetOrthogonalBend());
				}
			}
		}

	}

	private static Boolean Overlaps(SegmentInterval a, SegmentInterval b) {

		return ((a.lowerCoord <= b.higherCoord) && (a.higherCoord >= b.lowerCoord));

	}

	private void ResolveConflicts(SegmentInterval inputInterval) {
		IntervalIterator activeIntervals = this.GetActiveIntervals();

		while (activeIntervals.HasNext()) {
			SegmentInterval interval = activeIntervals.Next();

			if (this.HaveConflict(interval, inputInterval)) {

				if (this.IsSmaller(interval, inputInterval)) {
					interval.largerIntervals.Add(inputInterval);
					inputInterval.nrSmallerIntervals++;
				} else {
					inputInterval.largerIntervals.Add(interval);
					interval.nrSmallerIntervals++;
				}
			}
		}

	}

	@Override
	public void Run() {
		super.GetGraph().UpdateSegmentIDs();
		this.CheckForkNodes();
		this.CalcIntervalGraph();
		this.CalcFinalLayerNumbers();
		this.CleanSegmentIntervals();
		this.ShiftLowerLevelAndSetBendCoord();
		this.OptimizeBypassDummyNodes();

	}

	private void ShiftLowerLevelAndSetBendCoord() {
		float num4 = 0;
		HSegment segment = null;
		float orthogonalBend = 0;
		HGraph graph = super.GetGraph();
		HLevel upperLevel = super.GetUpperLevel();
		HLevel lowerLevel = super.GetLowerLevel();
		Integer edgeFlowDir = super.GetEdgeFlowDir();
		float num2 = upperLevel.GetCoord(edgeFlowDir)
				+ upperLevel.GetSize(edgeFlowDir);
		float coord = lowerLevel.GetCoord(edgeFlowDir);
		float minDistBetweenNodes = graph.GetMinDistBetweenNodes(edgeFlowDir);
		float num6 = 0f;
		float minDistBetweenNodeAndLink = graph
				.GetMinDistBetweenNodeAndLink(edgeFlowDir);
		float minDistBetweenLinks = graph.GetMinDistBetweenLinks(edgeFlowDir);
		Integer levelJustification = graph.GetLevelJustification();
		Boolean flag = graph.HasFixedNodesInLevels(edgeFlowDir);
		HSegmentIterator segmentsFrom = upperLevel.GetSegmentsFrom();
		float num12 = 0f;
		float num13 = 0f;

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();
			float thickness = segment.GetOwnerLink().GetThickness();
			if (num12 < thickness) {
				num12 = thickness;
			}

			orthogonalBend = segment.GetOrthogonalBend();
			if (((orthogonalBend == 1f) || (orthogonalBend == this._maxLayerNumber))
					&& (num13 < thickness)) {
				num13 = thickness;
			}
		}
		minDistBetweenNodeAndLink += 0.5f * num13;
		minDistBetweenLinks += num12;
		float num14 = minDistBetweenNodeAndLink;
		float num15 = minDistBetweenNodeAndLink;
		float minForkSegmentLength = graph.GetLayout()
				.get_MinForkSegmentLength();
		if (this._upperLevelHasForks) {

			num14 = Math.Max(num14, minForkSegmentLength + minDistBetweenLinks);
		}
		if (this._lowerLevelHasForks) {

			num15 = Math.Max(num15, minForkSegmentLength + minDistBetweenLinks);
		}
		if (this._maxLayerNumber > 0f) {
			num6 = (((this._maxLayerNumber - 1f) * minDistBetweenLinks) + num14)
					+ num15;
		}
		if (flag) {
			num4 = num2 + num6;
			if (num4 <= coord) {
				num14 += 0.5f * (coord - num4);
			} else {
				if (coord < num2) {
					coord = num2;

					num2 = lowerLevel.GetCoord(edgeFlowDir);
				}
				float num17 = this._maxLayerNumber + 1f;
				if (this._upperLevelHasForks) {
					num17++;
				}
				if (this._lowerLevelHasForks) {
					num17++;
				}
				minDistBetweenLinks = (coord - num2) / num17;
				if (this._upperLevelHasForks) {
					num14 = 2f * minDistBetweenLinks;
				} else {
					num14 = minDistBetweenLinks;
				}
			}
		} else {
			float size = lowerLevel.GetSize(edgeFlowDir);
			num4 = num2 + Math.Max(minDistBetweenNodes, num6);
			lowerLevel.SetCoordWithLinkUpdate(edgeFlowDir, num4, num4 + size,
					levelJustification);
		}
		upperLevel.SetFromForkCoord((num2 + num14) - minDistBetweenLinks);
		lowerLevel.SetToForkCoord((num2 + num14)
				+ (this._maxLayerNumber * minDistBetweenLinks));

		segmentsFrom = upperLevel.GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();

			orthogonalBend = segment.GetOrthogonalBend();
			if (orthogonalBend > 0f) {
				segment.SetOrthogonalBend((num2 + num14)
						+ ((orthogonalBend - 1f) * minDistBetweenLinks));
				this._numberOfBends += 2;
			} else {
				segment.SetOrthogonalBend(Float.MAX_VALUE);
			}
		}

	}

	private class AnonClass_1 implements
			OrthRoutingLevelAlgorithm.IntervalIterator {
		private OrthRoutingLevelAlgorithm __outerThis;

		public HSegmentIterator i;

		public AnonClass_1(OrthRoutingLevelAlgorithm input__outerThis) {
			this.__outerThis = input__outerThis;

			this.i = this.__outerThis._segmentMarker.GetElements();
		}

		public Boolean HasNext() {

			return this.i.HasNext();

		}

		public OrthRoutingLevelAlgorithm.SegmentInterval Next() {
			HSegment segment = this.i.Next();

			return this.__outerThis._intervals[segment.GetNumID()];

		}

	}

	private interface IntervalIterator {
		Boolean HasNext();

		OrthRoutingLevelAlgorithm.SegmentInterval Next();

	}

	private final class SegmentInterval {
		public OrthRoutingLevelAlgorithm __outerThis;

		public HSegment firstSegment;

		public float higherCoord;

		public Integer intervalType;

		public ArrayList largerIntervals;

		public float lowerCoord;

		public Integer nrSmallerIntervals;

		public HSegment secondSegment;

		public SegmentInterval(OrthRoutingLevelAlgorithm input__outerThis,
				HNode node) {
			float fromCoord = 0;
			float toCoord = 0;
			this.__outerThis = input__outerThis;
			Integer levelFlowDir = this.__outerThis.GetLevelFlowDir();

			if (node.IsUpperBypassNode()) {
				this.intervalType = 2;

				this.firstSegment = node.GetFirstSegmentFrom();
			} else {
				this.intervalType = 3;

				this.firstSegment = node.GetFirstSegmentTo();
			}

			this.secondSegment = node.GetOpposite(this.firstSegment);
			if (this.intervalType == 3) {

				fromCoord = this.firstSegment.GetFromCoord(levelFlowDir);

				toCoord = this.secondSegment.GetFromCoord(levelFlowDir);
			} else {

				fromCoord = this.firstSegment.GetToCoord(levelFlowDir);

				toCoord = this.secondSegment.GetToCoord(levelFlowDir);
			}
			if (fromCoord < toCoord) {
				this.lowerCoord = fromCoord;
				this.higherCoord = toCoord;
			} else {
				this.lowerCoord = toCoord;
				this.higherCoord = fromCoord;
			}
			this.__outerThis._intervals[this.firstSegment.GetNumID()] = this;
			this.nrSmallerIntervals = 0;
			this.largerIntervals = new ArrayList(2);
		}

		public SegmentInterval(OrthRoutingLevelAlgorithm input__outerThis,
				HSegment segment) {
			this.__outerThis = input__outerThis;
			Integer levelFlowDir = this.__outerThis.GetLevelFlowDir();
			this.firstSegment = segment;
			this.secondSegment = null;
			float fromCoord = segment.GetFromCoord(levelFlowDir);
			float toCoord = segment.GetToCoord(levelFlowDir);
			float thickness = segment.GetOwnerLink().GetThickness();
			if (fromCoord < toCoord) {
				this.lowerCoord = fromCoord - (0.5f * thickness);
				this.higherCoord = toCoord + (0.5f * thickness);
				this.intervalType = 0;
			} else {
				this.lowerCoord = toCoord - (0.5f * thickness);
				this.higherCoord = fromCoord + (0.5f * thickness);
				this.intervalType = 1;
			}
			this.__outerThis._intervals[segment.GetNumID()] = this;
			this.nrSmallerIntervals = 0;
			this.largerIntervals = new ArrayList(2);
		}

		public void Clean() {
			this.firstSegment = null;
			this.secondSegment = null;
			this.largerIntervals = null;

		}

		public float GetHigherDeltaBound() {

			return (this.higherCoord + this.__outerThis._halfMinDelta);

		}

		public float GetLayerNumber() {

			return this.firstSegment.GetOrthogonalBend();

		}

		public float GetLowerDeltaBound() {

			return (this.lowerCoord - this.__outerThis._halfMinDelta);

		}

		public void SetLayerNumber(float layerNumber) {
			this.firstSegment.SetOrthogonalBend(layerNumber);
			if (this.secondSegment != null) {
				this.secondSegment.SetOrthogonalBend(layerNumber);
			}

		}

		public void SetLayerNumberAtLeastTo(float layerNumber) {
			if (layerNumber > this.firstSegment.GetOrthogonalBend()) {
				this.SetLayerNumber(layerNumber);
			}

		}

	}

	public final class SegmentIntervalHigherBoundSort extends ArrayStableSort {
		public SegmentIntervalHigherBoundSort() {
		}

		@Override
		public Boolean Compare(java.lang.Object o1, java.lang.Object o2) {

			return (((OrthRoutingLevelAlgorithm.SegmentInterval) o1)
					.GetHigherDeltaBound() <= ((OrthRoutingLevelAlgorithm.SegmentInterval) o2)
					.GetHigherDeltaBound());

		}

	}

	public final class SegmentIntervalLowerBoundSort extends ArrayStableSort {
		public SegmentIntervalLowerBoundSort() {
		}

		@Override
		public Boolean Compare(java.lang.Object o1, java.lang.Object o2) {

			return (((OrthRoutingLevelAlgorithm.SegmentInterval) o1)
					.GetLowerDeltaBound() <= ((OrthRoutingLevelAlgorithm.SegmentInterval) o2)
					.GetLowerDeltaBound());

		}

	}
}
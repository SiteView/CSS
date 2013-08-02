package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;
import system.Collections.*;

public final class ForkPointAlgorithm extends HLevelSweepAlgorithm {
	private ArrayList _fromForks = new ArrayList();

	private Integer _levelFlowDirection;

	private float _linkDist;

	private float _prefForkAxisLength;

	private ArrayStableSort _sortAlg = new HSegmentSort();

	private ArrayList _toForks = new ArrayList();

	private Boolean _useFromFork = false;

	private Boolean _useToFork = false;

	public ForkPointAlgorithm(HGraph graph) {
		super.Init(graph);

		this._levelFlowDirection = graph.GetLevelFlow();
		this._useFromFork = graph.GetLayout().get_FromFork();
		this._useToFork = graph.GetLayout().get_ToFork();
		this._prefForkAxisLength = graph.GetLayout()
				.get_PreferredForkAxisLength();

		this._linkDist = graph.GetMinDistBetweenLinks(this._levelFlowDirection);
	}

	private void CalcInitialForkPoints(HLevel fromLevel, HLevel toLevel) {
		this.CalcInitialForkPoints(fromLevel, this._fromForks, true);
		this.CalcInitialForkPoints(toLevel, this._toForks, false);

	}

	private void CalcInitialForkPoints(HLevel level, ArrayList datas,
			Boolean fromSide) {
		ForkData data = null;
		float num = 0;
		float num4 = 0;
		this.CollectForkData(level, datas, fromSide);
		this.SortForkData(datas, fromSide);
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(datas);

		while (enumerator.HasMoreElements()) {
			data = (ForkData) enumerator.NextElement();
			num4 = 0f;
			if (data.leftLimit < data.forkCoordinate) {
				num4 = this._linkDist;
				while ((data.leftLimit + (1.5f * num4)) >= data.forkCoordinate) {
					num4 = 0.5f * num4;
				}
			}
			data.leftLimit += num4;
			num4 = 0f;
			if (data.rightLimit > data.forkCoordinate) {
				num4 = this._linkDist;
				while ((data.rightLimit - (1.5f * num4)) <= data.forkCoordinate) {
					num4 = 0.5f * num4;
				}
			}
			data.rightLimit -= num4;
			num = (data.segments.get_Count() - 1) * this._prefForkAxisLength;
			data.leftUsed = data.forkCoordinate - (0.5f * num);
			data.rightUsed = data.forkCoordinate + (0.5f * num);
			if (data.leftUsed < data.leftLimit) {
				data.leftUsed = data.leftLimit;
			}
			if (data.rightUsed > data.rightLimit) {
				data.rightUsed = data.rightLimit;
			}
		}
		ForkData data2 = null;

		enumerator = TranslateUtil.Collection2JavaStyleEnum(datas);

		while (enumerator.HasMoreElements()) {
			data = (ForkData) enumerator.NextElement();
			if (data2 != null) {
				num4 = (data.forkCoordinate - data2.forkCoordinate) / 5f;
				if (num4 > this._linkDist) {
					num4 = this._linkDist;
				}
				if ((data2.rightUsed + num4) >= data.leftUsed) {
					float num5 = data2.rightUsed - data2.forkCoordinate;
					float num6 = data.forkCoordinate - data.leftUsed;
					if ((num5 + num6) > 0f) {
						num = (data.forkCoordinate - data2.forkCoordinate)
								- num4;
						data2.rightUsed = data2.forkCoordinate
								+ ((num * num5) / (num5 + num6));
						data.leftUsed = data.forkCoordinate
								- ((num * num6) / (num5 + num6));
					}
				}
			}
			data2 = data;
		}

		enumerator = TranslateUtil.Collection2JavaStyleEnum(datas);

		while (enumerator.HasMoreElements()) {
			data = (ForkData) enumerator.NextElement();
			num = this._prefForkAxisLength * data.segments.get_Count();
			float num2 = 0.5f * num;
			if ((data.forkCoordinate - num2) < data.leftUsed) {
				num2 = data.forkCoordinate - data.leftUsed;
			}
			if (((data.forkCoordinate - num2) + num) > data.rightUsed) {
				num = (data.rightUsed - data.forkCoordinate) + num2;
			}
			Integer num7 = data.segments.get_Count() - 1;
			Integer num8 = 0;
			data.delta = num / ((float) num7);
			IJavaStyleEnumerator enumerator2 = TranslateUtil
					.Collection2JavaStyleEnum(data.segments);

			while (enumerator2.HasMoreElements()) {
				HSegment segment = (HSegment) enumerator2.NextElement();
				float val = (data.forkCoordinate - num2) + (num8 * data.delta);
				this.SetForkPoint(segment, fromSide, val);
				num8++;
			}
		}

	}

	@Override
	public void Clean() {
		super.Clean();

	}

	private void CollectForkData(HLevel level, ArrayList datas, Boolean fromSide) {
		HSegmentIterator segmentsFrom = null;
		HSegment segment2 = null;
		float minValue = Float.MIN_VALUE;
		float num3 = Float.MIN_VALUE;
		ForkData data = null;
		datas.Clear();
		if (fromSide) {

			segmentsFrom = level.GetSegmentsFrom();
		} else {

			segmentsFrom = level.GetSegmentsTo();
		}

		while (segmentsFrom.HasNext()) {
			float fromCoord = 0;
			HSegment segment = segmentsFrom.Next();
			if (fromSide) {

				fromCoord = segment.GetFromCoord(this._levelFlowDirection);
				segment.SetFromForkPoint(fromCoord);
			} else {

				fromCoord = segment.GetToCoord(this._levelFlowDirection);
				segment.SetToForkPoint(fromCoord);
			}
			if ((fromCoord == minValue) && this.ForkAllowed(segment, fromSide)) {
				if (data == null) {
					data = new ForkData();
					data.forkCoordinate = minValue;
					data.leftLimit = num3;
					data.rightLimit = Float.MAX_VALUE;
					data.segments = new ArrayList();
					if (segment2 != null) {
						data.segments.Add(segment2);
					}
					segment2 = null;
					datas.Add(data);
				}
				data.segments.Add(segment);
			} else {
				num3 = minValue;
				minValue = fromCoord;
				segment2 = segment;
				if (data != null) {
					data.rightLimit = fromCoord;
				}
				data = null;
			}
		}

	}

	private Boolean CorrectForkPoints() {

		return (this.CorrectForkPoints(this._fromForks, true) | this
				.CorrectForkPoints(this._toForks, false));

	}

	private Boolean CorrectForkPoints(ArrayList datas, Boolean fromSide) {
		Boolean flag = false;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(datas);

		while (enumerator.HasMoreElements()) {
			HSegment segment = null;
			float num = 0;
			float num2 = 0;
			float forkPoint = 0;
			ForkData data = (ForkData) enumerator.NextElement();
			float leftLimit = data.leftLimit;
			Integer num5 = 0;
			while (num5 < data.segments.get_Count()) {
				if (num5 == (data.segments.get_Count() - 1)) {

					leftLimit = Math.Max(leftLimit, data.forkCoordinate);
				}
				segment = (HSegment) data.segments.get_Item(num5);
				num = num2 = this.GetForkPoint(segment, fromSide);

				forkPoint = this.GetForkPoint(segment, !fromSide);
				if ((num >= forkPoint) && (forkPoint >= leftLimit)) {
					this.SetForkPoint(segment, fromSide, forkPoint);

					num2 = this.GetForkPoint(segment, fromSide);
					if (num != num2) {
						flag = true;
					}
				}
				leftLimit = num2 + data.delta;
				num5++;
			}
			leftLimit = data.rightLimit;
			for (num5 = data.segments.get_Count() - 1; num5 >= 0; num5--) {
				if (num5 == 0) {

					leftLimit = Math.Min(leftLimit, data.forkCoordinate);
				}
				segment = (HSegment) data.segments.get_Item(num5);
				num = num2 = this.GetForkPoint(segment, fromSide);

				forkPoint = this.GetForkPoint(segment, !fromSide);
				if ((num <= forkPoint) && (forkPoint <= leftLimit)) {
					this.SetForkPoint(segment, fromSide, forkPoint);

					num2 = this.GetForkPoint(segment, fromSide);
					if (num != num2) {
						flag = true;
					}
				}
				leftLimit = num2 - data.delta;
			}
		}

		return flag;

	}

	public void CreateForks() {
		if (this._useFromFork || this._useToFork) {
			HLevelIterator levels = super.GetGraph().GetLevels();

			while (levels.HasNext()) {
				this.CreateForks(levels.Next());
			}
		}

	}

	private void CreateForks(HLevel level) {
		float fromCoord = 0;
		float fromForkPoint = 0;
		HSegment segment = null;
		HLink ownerLink = null;
		HNode node = null;
		Boolean flag = null;
		float fromForkCoord = level.GetFromForkCoord();
		HSegmentIterator segmentsFrom = level.GetSegmentsFrom();
		Integer index = this._levelFlowDirection;
		Integer num5 = 1 - index;

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();

			ownerLink = segment.GetOwnerLink();

			if (ownerLink.IsOrthogonal()) {

				flag = segment.IsReversed();

				fromCoord = segment.GetFromCoord(index);

				fromForkPoint = segment.GetFromForkPoint(index);
				if (fromCoord != fromForkPoint) {

					node = ownerLink.AddDummyNode(segment);
					node.SetCoord(index, flag ? fromCoord : fromForkPoint);
					node.SetCoord(num5, fromForkCoord);

					node = ownerLink.AddDummyNode(segment);
					node.SetCoord(index, flag ? fromForkPoint : fromCoord);
					node.SetCoord(num5, fromForkCoord);
				}
			}
		}

		fromForkCoord = level.GetToForkCoord();

		segmentsFrom = level.GetSegmentsTo();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();

			ownerLink = segment.GetOwnerLink();

			if (ownerLink.IsOrthogonal()) {

				flag = segment.IsReversed();

				fromCoord = segment.GetToCoord(index);

				fromForkPoint = segment.GetToForkPoint(index);
				if (fromCoord != fromForkPoint) {

					node = ownerLink.AddDummyNode(segment);
					node.SetCoord(index, flag ? fromForkPoint : fromCoord);
					node.SetCoord(num5, fromForkCoord);

					node = ownerLink.AddDummyNode(segment);
					node.SetCoord(index, flag ? fromCoord : fromForkPoint);
					node.SetCoord(num5, fromForkCoord);
				}
			}
		}

	}

	private Boolean ForkAllowed(HSegment segment, Boolean fromSide) {
		if (fromSide) {

			if (segment.GetFrom().IsDummyNode()) {

				return false;
			}

			if (segment.IsReversed()) {

				return this._useToFork;
			}

			return this._useFromFork;
		}

		if (segment.GetTo().IsDummyNode()) {

			return false;
		}

		if (segment.IsReversed()) {

			return this._useFromFork;
		}

		return this._useToFork;

	}

	private float GetForkPoint(HSegment segment, Boolean fromSide) {
		if (fromSide) {

			return segment.GetFromForkPoint(this._levelFlowDirection);
		}

		return segment.GetToForkPoint(this._levelFlowDirection);

	}

	@Override
	public void Run() {
		if (this._useFromFork || this._useToFork) {
			this.SweepForward();
		}

	}

	private void SetForkPoint(HSegment segment, Boolean fromSide, float val) {
		if (fromSide) {
			segment.SetFromForkPoint(val);
		} else {
			segment.SetToForkPoint(val);
		}

	}

	private void SortForkData(ForkData data, Boolean fromSide) {
		Integer num = null;
		java.lang.Object[] a = data.segments.ToArray();
		for (num = 0; num < a.length; num++) {
			HSegment segment = (HSegment) a[num];
			if (fromSide) {
				segment.SetSortValue(segment
						.GetToCoord(this._levelFlowDirection));
			} else {
				segment.SetSortValue(segment
						.GetFromCoord(this._levelFlowDirection));
			}
		}
		this._sortAlg.Sort(a);
		data.segments.Clear();
		for (num = 0; num < a.length; num++) {
			data.segments.Add(a[num]);
		}

	}

	private void SortForkData(ArrayList datas, Boolean fromSide) {
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(datas);

		while (enumerator.HasMoreElements()) {
			this.SortForkData((ForkData) enumerator.NextElement(), fromSide);
		}

	}

	public void SwapForkAndEndPoints() {
		if (this._useFromFork || this._useToFork) {
			HLinkIterator links = super.GetGraph().GetLinks();

			while (links.HasNext()) {
				links.Next().SwapForkAndEndPoints(this._levelFlowDirection);
			}
		}

	}

	@Override
	public void TreatBackwardLevel(HLevel prevLevel, HLevel level) {

	}

	@Override
	public void TreatForwardLevel(HLevel prevLevel, HLevel level) {
		this.CalcInitialForkPoints(prevLevel, level);
		Boolean flag = true;
		Integer num = 0;
		while (flag && (num < 20)) {
			num++;

			flag = this.CorrectForkPoints();
		}

	}

	public final class ForkData {
		public float delta;

		public float forkCoordinate;

		public float leftLimit;

		public float leftUsed;

		public float rightLimit;

		public float rightUsed;

		public ArrayList segments;

	}
}
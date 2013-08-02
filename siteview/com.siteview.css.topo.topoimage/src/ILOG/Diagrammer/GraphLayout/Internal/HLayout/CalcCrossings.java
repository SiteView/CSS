package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class CalcCrossings extends HLevelAlgorithm {
	public InSegmentSort _inSegmentSort = new InSegmentSort();

	public Integer _numCrossings;

	public OutSegmentSort _outSegmentSort = new OutSegmentSort();

	public ListCell _segmentList;

	public CalcCrossings() {
	}

	@Override
	public void Clean() {
		super.Clean();
		this._segmentList = null;

	}

	private void CountCrossings(HSegment[] segments) {
		this._numCrossings = 0;
		for (Integer i = 0; i < segments.length; i++) {
			this._numCrossings += this.GetCrossingsAndRemove(segments[i]);
		}

	}

	private Integer GetCrossingsAndRemove(HSegment segment) {
		ListCell cell = null;
		ListCell next = this._segmentList;
		Integer num = 0;
		while (next.segment != segment) {
			cell = next;
			next = next.next;
			num++;
		}
		if (cell != null) {
			cell.next = next.next;

			return num;
		}
		this._segmentList = next.next;

		return num;

	}

	public Integer GetNumberOfCrossings(HLevel upperLevel, HLevel lowerLevel) {
		this.Init(upperLevel, lowerLevel);
		this.Run();

		return this._numCrossings;

	}

	@Override
	public void Init(HLevel upperLevel, HLevel lowerLevel) {
		super.Init(upperLevel, lowerLevel);
		this._segmentList = null;

	}

	private void MakeSegmentList(HSegment[] segments) {
		if (segments.length == 0) {
			this._segmentList = null;
		} else {
			ListCell next = this._segmentList = new ListCell();
			next.segment = segments[0];
			for (Integer i = 1; i < segments.length; i++) {
				next.next = new ListCell();
				next = next.next;
				next.segment = segments[i];
			}
		}

	}

	@Override
	public void Run() {
		super.GetUpperLevel().UpdateInfo();
		super.GetLowerLevel().UpdateInfo();
		Integer num = 0;
		HSegmentIterator segmentsFrom = super.GetUpperLevel().GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {
			segmentsFrom.Next();
			num++;
		}
		if (num == 0) {
			this._numCrossings = 0;
		} else {
			HSegment[] a = new HSegment[num];
			num = 0;

			segmentsFrom = super.GetUpperLevel().GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {

				a[num++] = segmentsFrom.Next();
			}
			this._outSegmentSort.Sort(a);
			this.MakeSegmentList(a);
			this._inSegmentSort.Sort(a);
			this.CountCrossings(a);
		}

	}

	public final class InSegmentSort extends ArrayStableSort {
		public InSegmentSort() {
		}

		@Override
		public Boolean Compare(java.lang.Object o1, java.lang.Object o2) {
			HSegment segment = (HSegment) o1;
			HSegment segment2 = (HSegment) o2;
			double crossRedToPosition = segment.GetCrossRedToPosition();
			double num2 = segment2.GetCrossRedToPosition();
			if (crossRedToPosition < num2) {

				return true;
			}
			if (crossRedToPosition > num2) {

				return false;
			}

			return (segment.GetCrossRedFromPosition() <= segment2
					.GetCrossRedFromPosition());

		}

	}

	public final class ListCell {
		public CalcCrossings.ListCell next;

		public HSegment segment;

	}

	public final class OutSegmentSort extends ArrayStableSort {
		public OutSegmentSort() {
		}

		@Override
		public Boolean Compare(java.lang.Object o1, java.lang.Object o2) {
			HSegment segment = (HSegment) o1;
			HSegment segment2 = (HSegment) o2;
			double crossRedFromPosition = segment.GetCrossRedFromPosition();
			double num2 = segment2.GetCrossRedFromPosition();
			if (crossRedFromPosition < num2) {

				return true;
			}
			if (crossRedFromPosition > num2) {

				return false;
			}

			return (segment.GetCrossRedToPosition() <= segment2
					.GetCrossRedToPosition());

		}

	}
}
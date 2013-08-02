package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class HSegmentSetMarker {
	private IndexSet _indexSet;

	private HSegment[] _segments;

	public HSegmentSetMarker(HGraph graph, Boolean full) {
		this._indexSet = new IndexSet(graph.GetNumberOfSegments());
		graph.UpdateSegmentIDs();
		if (full) {
			this._indexSet.InitFull();
		} else {
			this._indexSet.InitEmpty();
		}
		this._segments = new HSegment[graph.GetNumberOfSegments()];
		Integer num = 0;
		HSegmentIterator segments = graph.GetSegments();

		while (segments.HasNext()) {

			this._segments[num++] = segments.Next();
		}
	}

	public void Clean() {
		if (this._indexSet != null) {
			this._indexSet.Clean();
		}
		this._indexSet = null;
		this._segments = null;

	}

	public void Discard(HSegment segment) {
		this._indexSet.Discard(segment.GetNumID());

	}

	public HSegmentIterator GetElements() {

		return new AnonClass_1(this);

	}

	public void Insert(HSegment segment) {
		this._indexSet.Insert(segment.GetNumID());

	}

	private class AnonClass_1 implements HSegmentIterator {
		private HSegmentSetMarker __outerThis;

		public IntEnumeration e;

		public AnonClass_1(HSegmentSetMarker input__outerThis) {
			this.__outerThis = input__outerThis;

			this.e = this.__outerThis._indexSet.Elements();
		}

		public Boolean HasNext() {

			return this.e.HasMoreElements();

		}

		public HSegment Next() {

			return this.__outerThis._segments[this.e.NextElement()];

		}

		public void Remove() {
			throw (new system.Exception(
					"HSegmentIterator.remove() in HSegmentSetMarker"));

		}

	}
}
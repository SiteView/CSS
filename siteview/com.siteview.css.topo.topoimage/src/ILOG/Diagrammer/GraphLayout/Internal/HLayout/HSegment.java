package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class HSegment extends HGraphMember {
	private HNode _from;

	private float _generalFloatValue;

	private HSegment _nextFromSegment;

	private HSegment _nextToSegment;

	private float _priority;

	private Boolean _reversed = false;

	private HNode _to;

	public HSegment(HNode from, HNode to) {
		this._from = from;
		this._to = to;
		this._priority = 1f;
	}

	public void ActAfterAddToGraph() {

		if (!this._from.IsDummyNode()) {
			this._from.FromAdd(this, null);
		}

		if (!this._to.IsDummyNode()) {
			this._to.ToAdd(this, null);
		}

	}

	public void ActBeforeRemoveFromGraph() {

		if (!this._from.IsDummyNode() && this._from.ContainsFromSegment(this)) {
			this._from.FromRemove(this);
		}

		if (!this._to.IsDummyNode() && this._to.ContainsToSegment(this)) {
			this._to.ToRemove(this);
		}

	}

	public void CopyProperties(HSegment segment) {

		this._priority = segment.GetPriority();

		this._reversed = segment.IsReversed();

	}

	private double GetCrossRedFromPortCorrection() {
		Integer fromPortNumber = null;
		Integer num11 = null;
		double num12 = 0;
		HNode from = this.GetFrom();
		Integer levelFlow = this.GetOwnerGraph().GetLevelFlow();
		Integer index = 1 - levelFlow;
		float size = from.GetSize(index);
		float num4 = size;
		float num5 = from.GetSize(levelFlow);
		float num6 = (num4 + size) + num5;
		if (num6 == 0.0) {

			return 0.0;
		}

		if (!this.IsFromSideFixed()) {
			HPortCache portCache = from.GetPortCache();
			if (portCache == null) {

				return 0.5;
			}

			fromPortNumber = this.GetFromPortNumber();
			Integer num9 = portCache.GetMaxEastFromPortNumber(from) + 2;
			Integer num10 = portCache.GetMaxWestFromPortNumber(from) + 2;
			num11 = portCache.GetMaxNorthSouthFromPortNumber(from) + 2;
			if (this.GetFromPortSide() == 1) {
				if (fromPortNumber != -1) {
					num12 = ((double) (fromPortNumber + 1)) / ((double) num9);
				} else {
					num12 = 0.5;
				}

				return (1.0 - ((num12 * num4) / ((double) num6)));
			} else if (this.GetFromPortSide() == 2) {
				if (fromPortNumber == -1) {
					num12 = 0.5;
				} else {
					num12 = ((double) (fromPortNumber + 1)) / ((double) num11);
				}

				return ((size + (num12 * num5)) / ((double) num6));
			} else if (this.GetFromPortSide() == 3) {
				if (fromPortNumber != -1) {
					num12 = ((double) (fromPortNumber + 1)) / ((double) num10);
				} else {
					num12 = 0.5;
				}

				return ((num12 * size) / ((double) num6));
			}
		} else {
			double offsetToFixedFromPoint = this.GetOwnerLink()
					.GetOffsetToFixedFromPoint();

			if (this.IsReversed()) {

				offsetToFixedFromPoint = this.GetOwnerLink()
						.GetOffsetToFixedToPoint();
			}
			if (this.GetFromPortSide() == 1) {

				return (1.0 - (offsetToFixedFromPoint / ((double) num6)));
			} else if (this.GetFromPortSide() == 3) {

				return (offsetToFixedFromPoint / ((double) num6));
			}

			return ((size + offsetToFixedFromPoint) / ((double) num6));
		}
		Label_0173: if (fromPortNumber == -1) {
			num12 = 0.5;
		} else {
			num12 = ((double) (fromPortNumber + 1)) / ((double) num11);
		}

		return ((size + (num12 * num5)) / ((double) num6));

	}

	public Integer GetCrossRedFromPortNumber() {
		HNode from = this.GetFrom();
		HPortCache portCache = from.GetPortCache();
		if (portCache == null) {

			return 0;
		}
		Integer fromPortNumber = this.GetFromPortNumber();
		Integer num2 = portCache.GetMaxEastFromPortNumber(from) + 1;
		Integer num3 = portCache.GetMaxWestFromPortNumber(from) + 1;
		Integer num4 = portCache.GetMaxNorthSouthFromPortNumber(from) + 1;
		if (this.GetFromPortSide() == 1) {
			if (fromPortNumber == -1) {
				fromPortNumber = num2 / 2;
			}

			return (((num2 + num3) + num4) - fromPortNumber);
		} else if (this.GetFromPortSide() == 3) {
			if (fromPortNumber == -1) {
				fromPortNumber = num3 / 2;
			}

			return fromPortNumber;
		}
		if (fromPortNumber == -1) {
			fromPortNumber = num4 / 2;
		}

		return (num3 + fromPortNumber);

	}

	public double GetCrossRedFromPosition() {

		return (this.GetFrom().GetPositionInLevel() + (0.0002 * this
				.GetCrossRedFromPortCorrection()));

	}

	private double GetCrossRedToPortCorrection() {
		Integer toPortNumber = null;
		Integer num11 = null;
		double num12 = 0;
		HNode to = this.GetTo();
		Integer levelFlow = this.GetOwnerGraph().GetLevelFlow();
		Integer index = 1 - levelFlow;
		float size = to.GetSize(index);
		float num4 = size;
		float num5 = to.GetSize(levelFlow);
		float num6 = (num4 + size) + num5;
		if (num6 == 0.0) {

			return 0.0;
		}

		if (!this.IsToSideFixed()) {
			HPortCache portCache = to.GetPortCache();
			if (portCache == null) {

				return 0.5;
			}

			toPortNumber = this.GetToPortNumber();
			Integer num9 = portCache.GetMaxEastToPortNumber(to) + 2;
			Integer num10 = portCache.GetMaxWestToPortNumber(to) + 2;
			num11 = portCache.GetMaxNorthSouthToPortNumber(to) + 2;
			if (this.GetToPortSide() == 1) {
				if (toPortNumber != -1) {
					num12 = ((double) (toPortNumber + 1)) / ((double) num9);
				} else {
					num12 = 0.5;
				}

				return (((size + num5) + (num12 * num4)) / ((double) num6));
			} else if (this.GetToPortSide() == 2) {
				if (toPortNumber == -1) {
					num12 = 0.5;
				} else {
					num12 = ((double) (toPortNumber + 1)) / ((double) num11);
				}

				return ((size + (num12 * num5)) / ((double) num6));
			} else if (this.GetToPortSide() == 3) {
				if (toPortNumber != -1) {
					num12 = ((double) (toPortNumber + 1)) / ((double) num10);
				} else {
					num12 = 0.5;
				}

				return ((size - (num12 * size)) / ((double) num6));
			}
		} else {
			double offsetToFixedToPoint = this.GetOwnerLink()
					.GetOffsetToFixedToPoint();

			if (this.IsReversed()) {

				offsetToFixedToPoint = this.GetOwnerLink()
						.GetOffsetToFixedFromPoint();
			}
			if (this.GetToPortSide() == 1) {

				return (((size + num5) + offsetToFixedToPoint) / ((double) num6));
			} else if (this.GetToPortSide() == 3) {

				return ((size - offsetToFixedToPoint) / ((double) num6));
			}

			return ((size + offsetToFixedToPoint) / ((double) num6));
		}
		Label_0171: if (toPortNumber == -1) {
			num12 = 0.5;
		} else {
			num12 = ((double) (toPortNumber + 1)) / ((double) num11);
		}

		return ((size + (num12 * num5)) / ((double) num6));

	}

	public Integer GetCrossRedToPortNumber() {
		HNode to = this.GetTo();
		HPortCache portCache = to.GetPortCache();
		if (portCache == null) {

			return 0;
		}
		Integer toPortNumber = this.GetToPortNumber();
		Integer num2 = portCache.GetMaxEastToPortNumber(to) + 1;
		Integer num3 = portCache.GetMaxWestToPortNumber(to) + 1;
		Integer maxNorthSouthToPortNumber = portCache
				.GetMaxNorthSouthToPortNumber(to);
		if (this.GetToPortSide() == 1) {
			if (toPortNumber == -1) {
				toPortNumber = num2 / 2;
			}

			return ((num3 + maxNorthSouthToPortNumber) + toPortNumber);
		} else if (this.GetToPortSide() == 3) {
			if (toPortNumber == -1) {
				toPortNumber = num3 / 2;
			}

			return (num3 - toPortNumber);
		}
		if (toPortNumber == -1) {
			toPortNumber = maxNorthSouthToPortNumber / 2;
		}

		return (num3 + toPortNumber);

	}

	public double GetCrossRedToPosition() {

		return (this.GetTo().GetPositionInLevel() + (0.0002 * this
				.GetCrossRedToPortCorrection()));

	}

	public HNode GetFrom() {

		return this._from;

	}

	public float GetFromCoord(Integer index) {
		HNode from = this.GetFrom();

		if (from.IsDummyNode()) {
			float width = from.GetWidth();
			float height = from.GetHeight();
			if ((width != 0f) || (height != 0f)) {
				HNode to = this.GetTo();
				HNode opposite = from.GetOpposite(this).GetOpposite(from);
				if (to.GetCoord(index) < opposite.GetCoord(index)) {

					return from.GetCoord(index);
				}
				if (to.GetCoord(index) > opposite.GetCoord(index)) {

					return (from.GetCoord(index) + from.GetSize(index));
				}

				if (this.IsReversed()) {

					return (from.GetCoord(index) + from.GetSize(index));
				}
			}

			return from.GetCoord(index);
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().GetToCoord(index);
		}

		return this.GetOwnerLink().GetFromCoord(index);

	}

	public float GetFromForkPoint(Integer levelDir) {

		if (this.GetFrom().IsDummyNode()) {

			return this.GetFromCoord(levelDir);
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().GetToForkPoint();
		}

		return this.GetOwnerLink().GetFromForkPoint();

	}

	public float[] GetFromPoint() {
		HNode from = this.GetFrom();
		float width = from.GetWidth();
		float height = from.GetHeight();

		if (from.IsDummyNode()) {
			if ((width == 0f) && (height == 0f)) {

				return from.GetCoordField();
			}

			return new float[] { this.GetFromCoord(0), this.GetFromCoord(1) };
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().GetToCoordField();
		}

		return this.GetOwnerLink().GetFromCoordField();

	}

	public Integer GetFromPortNumber() {

		if (this._from.IsDummyNode()) {

			return -1;
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().GetToPortNumber();
		}

		return this.GetOwnerLink().GetFromPortNumber();

	}

	public Integer GetFromPortSide() {

		if (this._from.IsDummyNode()) {

			return -2;
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().GetToPortSide();
		}

		return this.GetOwnerLink().GetFromPortSide();

	}

	public HSegment GetNextFrom() {

		return this._nextFromSegment;

	}

	public HSegment GetNextTo() {

		return this._nextToSegment;

	}

	public HNode GetOpposite(HNode oneEndNode) {
		if (oneEndNode == this._from) {

			return this._to;
		}

		return this._from;

	}

	public float GetOrthogonalBend() {

		return this._generalFloatValue;

	}

	@Override
	public HGraph GetOwnerGraph() {

		return this.GetOwnerLink().GetOwnerGraph();

	}

	public HLink GetOwnerLink() {

		return (HLink) super.GetOwner();

	}

	public float GetPriority() {

		return this._priority;

	}

	public float GetSortValue() {

		return this._generalFloatValue;

	}

	public Integer GetSpan() {

		return (this._to.GetLevelNumber() - this._from.GetLevelNumber());

	}

	public HNode GetTo() {

		return this._to;

	}

	public float GetToCoord(Integer index) {
		HNode to = this.GetTo();

		if (to.IsDummyNode()) {
			float width = to.GetWidth();
			float height = to.GetHeight();
			if ((width != 0f) || (height != 0f)) {
				HNode from = this.GetFrom();
				HNode opposite = to.GetOpposite(this).GetOpposite(to);
				if (from.GetCoord(index) < opposite.GetCoord(index)) {

					return to.GetCoord(index);
				}
				if (from.GetCoord(index) > opposite.GetCoord(index)) {

					return (to.GetCoord(index) + to.GetSize(index));
				}

				if (this.IsReversed()) {

					return (to.GetCoord(index) + to.GetSize(index));
				}
			}

			return to.GetCoord(index);
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().GetFromCoord(index);
		}

		return this.GetOwnerLink().GetToCoord(index);

	}

	public float GetToForkPoint(Integer levelDir) {

		if (this.GetTo().IsDummyNode()) {

			return this.GetToCoord(levelDir);
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().GetFromForkPoint();
		}

		return this.GetOwnerLink().GetToForkPoint();

	}

	public float[] GetToPoint() {
		HNode to = this.GetTo();
		float width = to.GetWidth();
		float height = to.GetHeight();

		if (to.IsDummyNode()) {
			if ((width == 0f) && (height == 0f)) {

				return to.GetCoordField();
			}

			return new float[] { this.GetToCoord(0), this.GetToCoord(1) };
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().GetFromCoordField();
		}

		return this.GetOwnerLink().GetToCoordField();

	}

	public Integer GetToPortNumber() {

		if (this._to.IsDummyNode()) {

			return -1;
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().GetFromPortNumber();
		}

		return this.GetOwnerLink().GetToPortNumber();

	}

	public Integer GetToPortSide() {

		if (this._to.IsDummyNode()) {

			return -2;
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().GetFromPortSide();
		}

		return this.GetOwnerLink().GetToPortSide();

	}

	public float GetWantedAnchorCoord() {

		return this._generalFloatValue;

	}

	public Boolean IsContainedInFromSegments(HSegment list) {
		while (list != null) {
			if (list == this) {

				return true;
			}
			list = list._nextFromSegment;
		}

		return false;

	}

	public Boolean IsContainedInToSegments(HSegment list) {
		while (list != null) {
			if (list == this) {

				return true;
			}
			list = list._nextToSegment;
		}

		return false;

	}

	public Boolean IsFromSideFixed() {

		if (this.GetFrom().IsDummyNode()) {

			return false;
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().IsToSideFixed();
		}

		return this.GetOwnerLink().IsFromSideFixed();

	}

	public Boolean IsGradientZeroInFlow() {
		Integer levelFlow = this.GetOwnerGraph().GetLevelFlow();

		return (this.GetFromCoord(levelFlow) == this.GetToCoord(levelFlow));

	}

	public Boolean IsReversed() {

		return this._reversed;

	}

	public Boolean IsToSideFixed() {

		if (this.GetTo().IsDummyNode()) {

			return false;
		}

		if (this.IsReversed()) {

			return this.GetOwnerLink().IsFromSideFixed();
		}

		return this.GetOwnerLink().IsToSideFixed();

	}

	public void Reverse() {
		if (this._from != this._to) {
			this._from.FromRemove(this);
			this._to.ToRemove(this);
			this.ReverseWithoutUpdate();
			this._from.FromAdd(this, null);
			this._to.ToAdd(this, null);
		} else {
			this.ReverseWithoutUpdate();
		}

	}

	public void ReverseWithoutUpdate() {
		HNode node = this._from;
		this._from = this._to;
		this._to = node;
		this._reversed = !this._reversed;

	}

	public void SetFrom(HNode from) {
		this._from = from;

	}

	public void SetFromCoordWhenFixed() {

		if (this.IsReversed()) {
			this.GetOwnerLink().SetToCoordWhenFixed();
		} else {
			this.GetOwnerLink().SetFromCoordWhenFixed();
		}

	}

	public void SetFromForkPoint(float val) {

		if (!this.GetFrom().IsDummyNode()) {

			if (this.IsReversed()) {
				this.GetOwnerLink().SetToForkPoint(val);
			} else {
				this.GetOwnerLink().SetFromForkPoint(val);
			}
		}

	}

	public void SetFromPortSide(Integer side) {

		if (!this._from.IsDummyNode()) {

			if (this.IsReversed()) {
				this.GetOwnerLink().SetToPortSide(side);
			} else {
				this.GetOwnerLink().SetFromPortSide(side);
			}
		}

	}

	public void SetNextFrom(HSegment segment) {
		this._nextFromSegment = segment;

	}

	public void SetNextTo(HSegment segment) {
		this._nextToSegment = segment;

	}

	public void SetOrthogonalBend(float coord) {
		this._generalFloatValue = coord;

	}

	public void SetPriority(float prio) {
		this._priority = prio;

	}

	public void SetSortValue(float sortValue) {
		this._generalFloatValue = sortValue;

	}

	public void SetTo(HNode to) {
		this._to = to;

	}

	public void SetToCoordWhenFixed() {

		if (this.IsReversed()) {
			this.GetOwnerLink().SetFromCoordWhenFixed();
		} else {
			this.GetOwnerLink().SetToCoordWhenFixed();
		}

	}

	public void SetToForkPoint(float val) {

		if (!this.GetTo().IsDummyNode()) {

			if (this.IsReversed()) {
				this.GetOwnerLink().SetFromForkPoint(val);
			} else {
				this.GetOwnerLink().SetToForkPoint(val);
			}
		}

	}

	public void SetToPortSide(Integer side) {

		if (!this._to.IsDummyNode()) {

			if (this.IsReversed()) {
				this.GetOwnerLink().SetFromPortSide(side);
			} else {
				this.GetOwnerLink().SetToPortSide(side);
			}
		}

	}

	public void SetWantedAnchorCoord(float coord) {
		this._generalFloatValue = coord;

	}

}
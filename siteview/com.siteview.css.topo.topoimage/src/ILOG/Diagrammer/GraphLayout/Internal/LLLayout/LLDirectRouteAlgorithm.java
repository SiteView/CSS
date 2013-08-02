package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Collections.*;

public final class LLDirectRouteAlgorithm {
	public Integer _firstFreeOccupiedMarkerSlot;

	private LongLinkLayout _layout;

	private java.lang.Object _node;

	public Integer[] _occupiedMarker;

	public LLDirectRouteAlgorithm(LongLinkLayout layout, LLGrid grid) {
		this._layout = layout;
		this._node = null;
		this._occupiedMarker = null;
		this._firstFreeOccupiedMarkerSlot = -1;
	}

	private void CalcLinksAtSide(Integer side, ArrayList links) {
		LLLink link = null;
		ArrayList coll = new ArrayList(3);
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(links);

		while (enumerator.HasMoreElements()) {
			link = (LLLink) enumerator.NextElement();
			if (link.GetPreferedSide() == side) {
				coll.Add(link);
			}
		}
		if (coll.get_Count() != 0) {
			double correction = 0.0;
			Integer dir = 1;
			Integer num3 = -1;
			Integer num4 = 0;
			Integer num5 = -1;
			Integer num6 = 0;
			if (side == 1) {
				correction = -3.1415926535897931;
				dir = 1;
				num3 = 1;
				num4 = 1;
				num5 = 0;
				num6 = 1;
				// NOTICE: break ignore!!!
			} else if (side == 2) {
				correction = -1.5707963267948966;
				dir = 0;
				num3 = 1;
				num4 = 1;
				num5 = 0;
				num6 = 1;
				// NOTICE: break ignore!!!
			} else if (side == 3) {
				correction = -4.71238898038469;
				dir = 0;
				num3 = -1;
				num4 = 0;
				num5 = -1;
				num6 = 0;
				// NOTICE: break ignore!!!
			}

			enumerator = TranslateUtil.Collection2JavaStyleEnum(coll);

			while (enumerator.HasMoreElements()) {
				link = (LLLink) enumerator.NextElement();
				this.CalcRadialSortValue(link, correction);
			}
			LinkSort sort = new LinkSort(this, coll);
			sort.Sort();
			sort.Dispose();
			this.ClearOccupied(coll.get_Count());
			Integer num7 = (coll.get_Count() - num4) / 2;
			link = (LLLink) coll.get_Item(num7);
			this.SetTerminationPoint(link, 0);
			this.MarkOccupied(num3
					* this.GetTerminationPoints(link)[0].GetIndex(dir));
			Integer num8 = num5 + num6;
			Integer num9 = num3
					* this.GetTerminationPoints(link)[0].GetIndex(dir);
			Integer num10 = num3
					* this.GetTerminationPoints(link)[0].GetIndex(dir);
			for (Integer i = 1; i < coll.get_Count(); i++) {
				Integer num11 = null;
				Integer num14 = null;
				link = (LLLink) coll.get_Item(num7 + num8);
				LLTerminationPoint[] terminationPoints = this
						.GetTerminationPoints(link);
				Integer num12 = 0;
				Integer index = 0;
				Boolean flag = true;
				if (num8 > 0) {
					for (Integer j = 0; j < terminationPoints.length; j++) {
						if (terminationPoints[j].GetSide() != side) {
							continue;
						}
						num14 = num3 * terminationPoints[j].GetIndex(dir);
						num11 = num14 - num10;

						if (this.IsOccupied(num14)) {
							if (flag) {
								if (num11 >= 0) {
									if ((num12 <= 0) || (num11 < num12)) {
										num12 = num11;
										index = j;
									}
								} else if ((num12 <= 0) && (num11 > num12)) {
									num12 = num11;
									index = j;
								}
							}
							continue;
						}
						if (flag) {
							num12 = num11;
							index = j;
							flag = false;
						}
						Label_01F1: if (num11 >= 0) {
							if ((num12 <= 0) || (num11 < num12)) {
								num12 = num11;
								index = j;
							}
						} else if ((num12 <= 0) && (num11 > num12)) {
							num12 = num11;
							index = j;
						}
					}
					num8 = num5 - num8;
				} else {
					for (Integer k = 0; k < terminationPoints.length; k++) {
						if (terminationPoints[k].GetSide() != side) {
							continue;
						}
						num14 = num3 * terminationPoints[k].GetIndex(dir);
						num11 = num9 - num14;

						if (this.IsOccupied(num14)) {
							if (flag) {
								if (num11 >= 0) {
									if ((num12 <= 0) || (num11 < num12)) {
										num12 = num11;
										index = k;
									}
								} else if ((num12 <= 0) && (num11 > num12)) {
									num12 = num11;
									index = k;
								}
							}
							continue;
						}
						if (flag) {
							num12 = num11;
							index = k;
							flag = false;
						}
						Label_0281: if (num11 >= 0) {
							if ((num12 <= 0) || (num11 < num12)) {
								num12 = num11;
								index = k;
							}
						} else if ((num12 <= 0) && (num11 > num12)) {
							num12 = num11;
							index = k;
						}
					}
					num8 = num6 - num8;
				}
				num14 = num3 * terminationPoints[index].GetIndex(dir);
				this.SetTerminationPoint(link, index);
				this.MarkOccupied(num14);
				if (num14 > num10) {
					num10 = num14;
				}
				if (num14 < num9) {
					num9 = num14;
				}
			}
		}

	}

	private void CalcRadialSortValue(LLLink link, double correction) {
		float x = 0;
		float y = 0;
		float num3 = 0;
		float num4 = 0;
		InternalRect fromRect = null;
		if (link.GetFromNode() == this.GetNode()) {

			fromRect = link.GetFromRect();
			x = fromRect.X + (0.5f * fromRect.Width);
			y = fromRect.Y + (0.5f * fromRect.Height);

			fromRect = link.GetToRect();
			num3 = fromRect.X + (0.5f * fromRect.Width);
			num4 = fromRect.Y + (0.5f * fromRect.Height);
			if (link.GetFromPoint() != null) {
				x = link.GetFromPoint().X;
				y = link.GetFromPoint().Y;
			}
			if (link.GetToPoint() != null) {
				num3 = link.GetToPoint().X;
				num4 = link.GetToPoint().Y;
			}
		} else {

			fromRect = link.GetToRect();
			x = fromRect.X + (0.5f * fromRect.Width);
			y = fromRect.Y + (0.5f * fromRect.Height);

			fromRect = link.GetFromRect();
			num3 = fromRect.X + (0.5f * fromRect.Width);
			num4 = fromRect.Y + (0.5f * fromRect.Height);
			if (link.GetToPoint() != null) {
				x = link.GetToPoint().X;
				y = link.GetToPoint().Y;
			}
			if (link.GetFromPoint() != null) {
				num3 = link.GetFromPoint().X;
				num4 = link.GetFromPoint().Y;
			}
		}
		double angle = LayoutUtil.Angle(x, y, num3, num4) + correction;

		angle = LayoutUtil.CorrectAngle(angle);
		link.SetSortValue(angle);

	}

	public void ClearOccupied(Integer numberOfMarkers) {
		this._occupiedMarker = new Integer[numberOfMarkers];
		this._firstFreeOccupiedMarkerSlot = 0;

	}

	public void Dispose() {
		this._occupiedMarker = null;
		this._layout = null;
		this._node = null;

	}

	private LongLinkLayout GetLayout() {

		return this._layout;

	}

	private Integer GetLinkSide(LLLink link) {
		LLTerminationPoint[] startPoints = null;
		Integer num3 = null;
		Integer num4 = null;
		java.lang.Object node = this.GetNode();
		InternalRect fromRect = link.GetFromRect();
		InternalRect toRect = link.GetToRect();
		LongLinkLayout layout = this.GetLayout();
		float minStartSegmentLength = layout.get_MinStartSegmentLength();
		float minEndSegmentLength = layout.get_MinEndSegmentLength();
		Integer[] startPref = new Integer[4];
		Integer[] endPref = new Integer[4];
		startPref[3] = num3 = 0;
		startPref[2] = num4 = num3;
		startPref[0] = startPref[1] = num4;
		if (link.GetFromNode() == node) {

			startPoints = link.GetStartPoints();
			if (link.GetFromPoint() != null) {
				LLTerminationCandAlgorithm.CalcSidePrefInternal(fromRect.X
						- minStartSegmentLength, (fromRect.X + fromRect.Width)
						+ minStartSegmentLength,
						toRect.X - minEndSegmentLength,
						(toRect.X + toRect.Width) + minEndSegmentLength, 1, 3,
						startPref, endPref);
				LLTerminationCandAlgorithm.CalcSidePrefInternal(fromRect.Y
						- minStartSegmentLength, (fromRect.Y + fromRect.Height)
						+ minStartSegmentLength,
						toRect.Y - minEndSegmentLength,
						(toRect.Y + toRect.Height) + minEndSegmentLength, 0, 2,
						startPref, endPref);
			}
		} else {

			startPoints = link.GetEndPoints();
			if (link.GetToPoint() != null) {
				LLTerminationCandAlgorithm.CalcSidePrefInternal(fromRect.X
						- minStartSegmentLength, (fromRect.X + fromRect.Width)
						+ minStartSegmentLength,
						toRect.X - minEndSegmentLength,
						(toRect.X + toRect.Width) + minEndSegmentLength, 1, 3,
						endPref, startPref);
				LLTerminationCandAlgorithm.CalcSidePrefInternal(fromRect.Y
						- minStartSegmentLength, (fromRect.Y + fromRect.Height)
						+ minStartSegmentLength,
						toRect.Y - minEndSegmentLength,
						(toRect.Y + toRect.Height) + minEndSegmentLength, 0, 2,
						endPref, startPref);
			}
		}
		if (startPoints.length > 1) {
			startPoints[0].Sort(startPoints, startPref);
		}

		return startPoints[0].GetSide();

	}

	private java.lang.Object GetNode() {

		return this._node;

	}

	private LLTerminationPoint[] GetTerminationPoints(LLLink link) {
		if (link.GetFromNode() == this.GetNode()) {

			return link.GetStartPoints();
		}

		return link.GetEndPoints();

	}

	public Boolean IsOccupied(Integer index) {
		for (Integer i = 0; i < this._firstFreeOccupiedMarkerSlot; i++) {
			if (this._occupiedMarker[i] == index) {

				return true;
			}
		}

		return false;

	}

	public void MarkOccupied(Integer index) {
		this._occupiedMarker[this._firstFreeOccupiedMarkerSlot++] = index;

	}

	public Boolean Route(java.lang.Object node, ArrayList links) {
		this._node = node;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(links);

		while (enumerator.HasMoreElements()) {
			LLLink link = (LLLink) enumerator.NextElement();
			link.SetPreferedSide(this.GetLinkSide(link));
		}
		this.CalcLinksAtSide(2, links);
		this.CalcLinksAtSide(3, links);
		this.CalcLinksAtSide(0, links);
		this.CalcLinksAtSide(1, links);

		return true;

	}

	private void SetTerminationPoint(LLLink link, Integer index) {
		if (link.GetFromNode() == this.GetNode()) {
			link.SetActStartCandidate(index);
		} else {
			link.SetActEndCandidate(index);
		}

	}

	public class LinkSort extends QuickSort {
		public LLDirectRouteAlgorithm __outerThis;

		public ArrayList _links;

		public LinkSort(LLDirectRouteAlgorithm input__outerThis, ArrayList links) {
			this.__outerThis = input__outerThis;
			this._links = links;
		}

		@Override
		public Integer Compare(Integer loc1, Integer loc2) {
			LLLink link = (LLLink) this._links.get_Item(loc1);
			LLLink link2 = (LLLink) this._links.get_Item(loc2);
			if (link.GetSortValue() == link2.GetSortValue()) {
				LLTerminationPoint actEndPoint = null;
				LLTerminationPoint actStartPoint = null;
				if (link.GetFromNode() == this.__outerThis.GetNode()) {

					actEndPoint = link.GetActEndPoint();
				} else {

					actEndPoint = link.GetActStartPoint();
				}
				if (link2.GetFromNode() == this.__outerThis.GetNode()) {

					actStartPoint = link2.GetActEndPoint();
				} else {

					actStartPoint = link2.GetActStartPoint();
				}
				if ((actEndPoint != null) && (actStartPoint != null)) {
					if (actEndPoint.GetSide() == 0) {

						return (actEndPoint.GetIndex(1) - actStartPoint
								.GetIndex(1));
					} else if (actEndPoint.GetSide() == 1) {

						return (actStartPoint.GetIndex(1) - actEndPoint
								.GetIndex(1));
					} else if (actEndPoint.GetSide() == 2) {

						return (actStartPoint.GetIndex(0) - actEndPoint
								.GetIndex(0));
					} else if (actEndPoint.GetSide() == 3) {

						return (actEndPoint.GetIndex(0) - actStartPoint
								.GetIndex(0));
					}
				}

				return 0;
			}
			if (link.GetSortValue() < link2.GetSortValue()) {

				return -1;
			}

			return 1;

		}

		public void Dispose() {
			this._links = null;

		}

		public void Sort() {
			super.Sort(this._links.get_Count());

		}

		@Override
		public void Swap(Integer loc1, Integer loc2) {
			LLLink link = (LLLink) this._links.get_Item(loc1);
			LLLink link2 = (LLLink) this._links.get_Item(loc2);
			this._links.set_Item(loc2, link);
			this._links.set_Item(loc1, link2);

		}

	}
}
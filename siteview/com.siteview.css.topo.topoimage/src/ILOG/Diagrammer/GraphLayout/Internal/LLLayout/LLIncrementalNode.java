package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import system.Math;
import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.LongLinkLayout;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.QuickSort;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public final class LLIncrementalNode {
	private ArrayList _bottomLinks;

	private ArrayList _leftLinks;

	private InternalRect _newRect;

	private java.lang.Object _nodeObject;

	private InternalRect _oldRect;

	private ArrayList _rightLinks;

	private ArrayList _topLinks;

	public LLIncrementalNode(LongLinkLayout layout, java.lang.Object node) {
		this._nodeObject = node;
		this._topLinks = new ArrayList(2);
		this._bottomLinks = new ArrayList(2);
		this._leftLinks = new ArrayList(2);
		this._rightLinks = new ArrayList(2);
		this.Update(layout);
	}

	public java.lang.Object GetNodeObject() {

		return this._nodeObject;

	}

	public InternalRect GetOldRect() {

		return this._oldRect;

	}

	public Boolean NeedsReroute() {
		if (this._oldRect == null) {

			return true;
		}
		float num = this._newRect.X - this._oldRect.X;
		if (num != 0f) {

			return true;
		}
		num = this._newRect.Y - this._oldRect.Y;
		if (num != 0f) {

			return true;
		}
		num = this._newRect.Width - this._oldRect.Width;
		if (num != 0f) {

			return true;
		}
		num = this._newRect.Height - this._oldRect.Height;

		return (num != 0f);

	}

	public void Prepare(LongLinkLayout layout, LLGrid grid) {
		if (this._newRect == null) {

			this._newRect = layout.GetNodeBoxForConnection(this._nodeObject);
			if (layout.get_IncrementalConnectionPreserving()
					&& this.NeedsReroute()) {
				this.SortLinks(this._rightLinks);
				this.SortLinks(this._leftLinks);
				this.SortLinks(this._topLinks);
				this.SortLinks(this._bottomLinks);
				float minNodeCornerOffset = layout.get_MinNodeCornerOffset();
				float minCoord = this._newRect.Y + minNodeCornerOffset;
				float maxCoord = (this._newRect.Y + this._newRect.Height)
						- minNodeCornerOffset;
				Integer i = grid.GetMinGridIndexAtSide(this._newRect, 1,
						minNodeCornerOffset);
				Integer num5 = grid.GetMaxGridIndexAtSide(this._newRect, 1,
						minNodeCornerOffset);
				float gridCoord = grid.GetGridCoord(1, i);
				float maxGridCoord = grid.GetGridCoord(1, num5);
				float gridDist = grid.GetGridDist(1);
				this.ReassignLinkCoords(this._rightLinks, minCoord, maxCoord,
						gridCoord, maxGridCoord, gridDist);
				this.ReassignLinkCoords(this._leftLinks, minCoord, maxCoord,
						gridCoord, maxGridCoord, gridDist);
				minCoord = this._newRect.X + minNodeCornerOffset;
				maxCoord = (this._newRect.X + this._newRect.Width)
						- minNodeCornerOffset;

				i = grid.GetMinGridIndexAtSide(this._newRect, 0,
						minNodeCornerOffset);

				num5 = grid.GetMaxGridIndexAtSide(this._newRect, 0,
						minNodeCornerOffset);

				gridCoord = grid.GetGridCoord(0, i);

				maxGridCoord = grid.GetGridCoord(0, num5);

				gridDist = grid.GetGridDist(0);
				this.ReassignLinkCoords(this._topLinks, minCoord, maxCoord,
						gridCoord, maxGridCoord, gridDist);
				this.ReassignLinkCoords(this._bottomLinks, minCoord, maxCoord,
						gridCoord, maxGridCoord, gridDist);
				this.SetNewTerminationPoints(this._rightLinks, false);
				this.SetNewTerminationPoints(this._leftLinks, false);
				this.SetNewTerminationPoints(this._topLinks, true);
				this.SetNewTerminationPoints(this._bottomLinks, true);
			}
		}

	}

	private void ReassignLinkCoords(ArrayList v, float minCoord,
			float maxCoord, float minGridCoord, float maxGridCoord,
			float gridDist) {
		Integer count = v.get_Count();
		if (count != 0) {
			float num2 = 0;
			float num3 = 0;
			if (((count - 1) * gridDist) <= (maxGridCoord - minGridCoord)) {
				Integer num4 = (int) Math
						.Floor((double) (((maxGridCoord - minGridCoord) / gridDist) + 0.5f));
				num4 = (num4 - (count - 1)) / 2;
				num2 = minGridCoord + (num4 * gridDist);
				num3 = gridDist;
			} else {
				num2 = minCoord;
				num3 = (maxCoord - minCoord) / ((float) (count - 1));
			}
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(v);

			while (enumerator.HasMoreElements()) {
				LinkPointData data = (LinkPointData) enumerator.NextElement();
				data._coord = num2;
				num2 += num3;
			}
		}

	}

	private void SetNewTerminationPoint(LinkPointData lpdata, Boolean topBottom) {
		float x = 0;
		float y = 0;
		LLIncrementalLink link = lpdata._link;
		if (lpdata._fromSide) {
			x = link.GetFromPoint().X;
			y = link.GetFromPoint().Y;
		} else {
			x = link.GetToPoint().X;
			y = link.GetToPoint().Y;
		}
		if (topBottom) {
			float num3 = ((y - this._oldRect.Y) - (0.5f * this._oldRect.Height))
					/ this._oldRect.Height;
			y = (this._newRect.Y + (0.5f * this._newRect.Height))
					+ (num3 * this._newRect.Height);
			x = lpdata._coord;
		} else {
			float num4 = ((x - this._oldRect.X) - (0.5f * this._oldRect.Width))
					/ this._oldRect.Width;
			x = (this._newRect.X + (0.5f * this._newRect.Width))
					+ (num4 * this._newRect.Width);
			y = lpdata._coord;
		}
		if (lpdata._fromSide) {
			link.SetNewFromPoint(new InternalPoint(x, y));
		} else {
			link.SetNewToPoint(new InternalPoint(x, y));
		}

	}

	private void SetNewTerminationPoints(ArrayList v, Boolean topBottom) {
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(v);

		while (enumerator.HasMoreElements()) {
			LinkPointData lpdata = (LinkPointData) enumerator.NextElement();
			this.SetNewTerminationPoint(lpdata, topBottom);
		}

	}

	private void SortLinks(ArrayList v) {
		if (v.get_Count() > 1) {
			LinkPointDataSortAlg alg = new LinkPointDataSortAlg(v);
			alg.Sort(v.get_Count());
			alg.Dispose();
		}

	}

	public void Update(LongLinkLayout layout) {

		this._oldRect = layout.GetNodeBoxForConnection(this._nodeObject);
		this._newRect = null;

	}

	public void UpdateLink(LLIncrementalLink link, InternalPoint p,
			Boolean fromSide, Boolean isHorizontal) {
		if (isHorizontal) {
			if (p.X >= (this._oldRect.X + (0.5f * this._oldRect.Width))) {
				this._rightLinks.Add(new LinkPointData(link, p.Y, fromSide));
			} else {
				this._leftLinks.Add(new LinkPointData(link, p.Y, fromSide));
			}
		} else if (p.Y >= (this._oldRect.Y + (0.5f * this._oldRect.Height))) {
			this._bottomLinks.Add(new LinkPointData(link, p.X, fromSide));
		} else {
			this._topLinks.Add(new LinkPointData(link, p.X, fromSide));
		}

	}

	public final class LinkPointData {
		public float _coord;

		public Boolean _fromSide = false;

		public LLIncrementalLink _link;

		public LinkPointData(LLIncrementalLink link, float coord,
				Boolean fromSide) {
			this._link = link;
			this._coord = coord;
			this._fromSide = fromSide;
		}

	}

	public class LinkPointDataSortAlg extends QuickSort {
		public ArrayList _v;

		public LinkPointDataSortAlg(ArrayList v) {
			this._v = v;
		}

		@Override
		public Integer Compare(Integer loc1, Integer loc2) {
			LLIncrementalNode.LinkPointData data = (LLIncrementalNode.LinkPointData) this._v
					.get_Item(loc1);
			LLIncrementalNode.LinkPointData data2 = (LLIncrementalNode.LinkPointData) this._v
					.get_Item(loc2);
			if (data._coord == data2._coord) {

				return 0;
			}
			if (data._coord < data2._coord) {

				return -1;
			}

			return 1;

		}

		public void Dispose() {

		}

		@Override
		public void Swap(Integer loc1, Integer loc2) {
			java.lang.Object obj2 = this._v.get_Item(loc1);
			java.lang.Object obj3 = this._v.get_Item(loc2);
			this._v.set_Item(loc2, obj2);
			this._v.set_Item(loc1, obj3);

		}

	}
}
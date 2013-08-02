package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.Internal.QuickSort;

public abstract class IncidentLinksSorter extends QuickSort {
	public ShortLinkAlgorithm _layout;

	private ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData _node;

	private SLNodeSide _side;

	private ArrayList _vectLinks;

	public IncidentLinksSorter(ShortLinkAlgorithm layout) {
		this._layout = layout;
	}

	public void Clean() {
		this._side = null;
		this._vectLinks = null;
		this._node = null;

	}

	@Override
	public Integer Compare(Integer loc1, Integer loc2) {

		if (this.Compare1(loc1, loc2)) {

			return -1;
		}

		return 1;

	}

	private Boolean Compare1(Integer loc1, Integer loc2) {
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) this._vectLinks
				.get_Item(loc1);
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data2 = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) this._vectLinks
				.get_Item(loc2);
		Boolean origin = data.IsOrigin(this._node, this._side);
		Boolean flag2 = data2.IsOrigin(this._node, this._side);
		Boolean flag3 = data.IsConnectionPointFixed(origin);
		Boolean flag4 = data2.IsConnectionPointFixed(flag2);
		if (flag3 && flag4) {

			return (this._side.GetTangentDelta(data.GetConnectionPoint(origin),
					data2.GetConnectionPoint(flag2)) <= 0f);
		}

		if (data.IsSelfLink()) {

			if (data.IsSameSideSelfLink()) {

				return true;
			}

			if (!data2.IsSelfLink()) {
				if (this._side.GetSidePosition(data.GetNodeSide(!origin)
						.GetDirection()) == 0) {

					return true;
				} else if (this._side.GetSidePosition(data.GetNodeSide(!origin)
						.GetDirection()) == 1) {

					return false;
				}
			}
		} else if (data2.IsSelfLink()) {

			if (data2.IsSameSideSelfLink()) {

				return false;
			}

			if (!data.IsSelfLink()) {
				if (this._side.GetSidePosition(data2.GetNodeSide(!flag2)
						.GetDirection()) == 0) {

					return false;
				} else if (this._side.GetSidePosition(data2.GetNodeSide(!flag2)
						.GetDirection()) == 1) {

					return true;
				}
			}
		}

		return this.CompareLinks(data, data2, origin, flag2, this._side);

	}

	public abstract Boolean CompareLinks(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData link1,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData link2,
			Boolean origin1, Boolean origin2, SLNodeSide nodeSide);

	public void Sort(ArrayList vectLinks,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData node,
			SLNodeSide side, Integer bundleMode) {
		if (bundleMode != 0) {
			Integer numberOfElements = (vectLinks != null) ? vectLinks
					.get_Count() : 0;
			if (numberOfElements > 0) {
				this._vectLinks = vectLinks;
				this._node = node;
				this._side = side;
				super.Sort(numberOfElements);
			}
		}

	}

	@Override
	public void Swap(Integer loc1, Integer loc2) {
		java.lang.Object obj2 = this._vectLinks.get_Item(loc1);
		this._vectLinks.set_Item(loc1, this._vectLinks.get_Item(loc2));
		this._vectLinks.set_Item(loc2, obj2);

	}

}
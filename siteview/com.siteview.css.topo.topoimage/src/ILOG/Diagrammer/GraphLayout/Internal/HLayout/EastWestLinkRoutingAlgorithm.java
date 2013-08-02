package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;
import system.Collections.*;

public final class EastWestLinkRoutingAlgorithm extends HGraphAlgorithm {
	private Hashtable _eastlinkTable = new Hashtable();

	private Integer _edgeFlowDir;

	private Integer _levelFlowDir;

	private Boolean _neighborLinksAligned = false;

	public EastWestLinkRoutingAlgorithm(HGraph graph) {
		this.Init(graph);

		this._edgeFlowDir = graph.GetEdgeFlow();

		this._levelFlowDir = graph.GetLevelFlow();
		this._neighborLinksAligned = graph.GetLayout()
				.get_NeighborLinksAligned();
	}

	private void CalcConnectionPoints() {
		HLinkIterator eastWestLinks = super.GetGraph().GetEastWestLinks();

		while (eastWestLinks.HasNext()) {
			this.CalcConnectionPoints(eastWestLinks.Next());
		}

	}

	private void CalcConnectionPoints(HLink link) {

		if (link.IsFromSideFixed()) {
			link.SetFromCoordWhenFixed();
		}

		if (link.IsToSideFixed()) {
			link.SetToCoordWhenFixed();
		}

		if (!link.IsFromSideFixed() || !link.IsToSideFixed()) {
			Integer marker = null;
			Integer index = this._levelFlowDir;
			Integer num2 = this._edgeFlowDir;
			Integer multiLinkIndex = link.GetMultiLinkIndex();
			HNode from = link.GetFrom();
			HNode to = link.GetTo();
			if (from.GetPositionInLevel() < to.GetPositionInLevel()) {

				marker = from.GetMarker();
				link.SetFromCoordWhenFree(index,
						from.GetCoord(index) + from.GetSize(index));
				link.SetToCoordWhenFree(index, to.GetCoord(index));
			} else {

				marker = to.GetMarker();
				link.SetFromCoordWhenFree(index, from.GetCoord(index));
				link.SetToCoordWhenFree(index,
						to.GetCoord(index) + to.GetSize(index));
			}
			float coord = from.GetCoord(num2);
			float num6 = from.GetCoord(num2) + from.GetSize(num2);
			float num7 = to.GetCoord(num2);
			float num8 = to.GetCoord(num2) + to.GetSize(num2);
			float num9 = ((float) multiLinkIndex) / ((float) marker);
			if (this._neighborLinksAligned) {
				if ((coord <= num7) && (num6 >= num8)) {
					coord = num7;
					num6 = num8;
				} else if ((num7 <= coord) && (num8 >= num6)) {
					num7 = coord;
					num8 = num6;
				}
			}
			link.SetFromCoordWhenFree(num2, coord + (num9 * (num6 - coord)));
			link.SetToCoordWhenFree(num2, num7 + (num9 * (num8 - num7)));
		}

	}

	private void CalcEastSideLinkNumbers() {
		HLinkIterator eastWestLinks = super.GetGraph().GetEastWestLinks();

		while (eastWestLinks.HasNext()) {
			HLink link = eastWestLinks.Next();
			HNode westNode = this.GetWestNode(link);
			Integer marker = westNode.GetMarker();
			link.SetMultiLinkIndex(marker);
			westNode.SetMarker(marker + 1);
		}

	}

	private Integer GetLinkType(HLink link, Integer xdir, Integer ydir) {
		if (link.GetFromCoord(xdir) < link.GetToCoord(xdir)) {
			if (link.GetFromCoord(ydir) < link.GetToCoord(ydir)) {

				return 1;
			}

			return 2;
		}
		if (link.GetFromCoord(ydir) < link.GetToCoord(ydir)) {

			return 2;
		}

		return 1;

	}

	private HNode GetWestNode(HLink link) {
		if (link.GetFrom().GetPositionInLevel() < link.GetTo()
				.GetPositionInLevel()) {

			return link.GetFrom();
		}

		return link.GetTo();

	}

	private void MakeLinksOrthogonal() {
		HLinkIterator eastWestLinks = super.GetGraph().GetEastWestLinks();

		while (eastWestLinks.HasNext()) {
			HLink link = eastWestLinks.Next();

			if (link.IsOrthogonal()) {
				HNode westNode = this.GetWestNode(link);
				Integer num = westNode.GetMarker() - 1;
				if (num == 1) {
					this.MakeOrthogonal(link);
				} else {
					ArrayList list = (ArrayList) this._eastlinkTable
							.get_Item(westNode);
					if (list == null) {
						list = new ArrayList();
						list.Add(link);
						this._eastlinkTable.set_Item(westNode, list);
						continue;
					}
					list.Add(link);
				}
			}
		}
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum((ICollection) this._eastlinkTable
						.get_Values());

		while (enumerator.HasMoreElements()) {
			this.MakeOrthogonal((ArrayList) enumerator.NextElement());
		}

	}

	private void MakeOrthogonal(HLink link) {
		Integer index = this._levelFlowDir;
		float orthSegmentCoord = 0.5f * (link.GetFromCoord(index) + link
				.GetToCoord(index));
		this.MakeOrthogonal(link, orthSegmentCoord);

	}

	private void MakeOrthogonal(ArrayList linksVector) {
		Integer num10 = null;
		Integer index = this._levelFlowDir;
		Integer num2 = this._edgeFlowDir;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(linksVector);
		HLink link = null;

		while (enumerator.HasMoreElements()) {
			link = (HLink) enumerator.NextElement();
			link.SetMultiLinkIndex(0);
		}
		float fromCoord = link.GetFromCoord(index);
		float toCoord = link.GetToCoord(index);
		if (fromCoord > toCoord) {
			toCoord = fromCoord;

			fromCoord = link.GetToCoord(index);
		}
		float minDelta = Math.Min(link.GetFrom().GetSize(num2), link.GetTo()
				.GetSize(num2));
		minDelta = (0.9f * minDelta) / ((float) (linksVector.get_Count() + 1));
		HLink[] array = new HLink[linksVector.get_Count()];
		linksVector.CopyTo(array);
		for (num10 = 0; num10 < array.length; num10++) {
			float num6 = Math.Min(array[num10].GetFromCoord(num2),
					array[num10].GetToCoord(num2));
			float num8 = Math.Min(array[num10].GetFromCoord(num2),
					array[num10].GetToCoord(num2));
			for (Integer i = num10 + 1; i < array.length; i++) {
				float num7 = Math.Min(array[i].GetFromCoord(num2),
						array[i].GetToCoord(num2));
				float num9 = Math.Min(array[i].GetFromCoord(num2),
						array[i].GetToCoord(num2));
				if ((num7 < num6) || ((num6 == num7) && (num9 < num8))) {
					link = array[i];
					array[i] = array[num10];
					array[num10] = link;
					num6 = num7;
					num8 = num9;
				}
			}
		}
		for (Boolean flag = true; flag; flag = this.ResolveConflicts(array,
				minDelta, true) | this.ResolveConflicts(array, minDelta, false)) {
		}
		Integer multiLinkIndex = 0;
		for (num10 = 0; num10 < array.length; num10++) {
			if (multiLinkIndex < array[num10].GetMultiLinkIndex()) {

				multiLinkIndex = array[num10].GetMultiLinkIndex();
			}
		}
		multiLinkIndex++;
		for (num10 = 0; num10 < array.length; num10++) {
			this.MakeOrthogonal(
					array[num10],
					fromCoord
							+ (((toCoord - fromCoord) * (array[num10]
									.GetMultiLinkIndex() + 2)) / ((float) (multiLinkIndex + 3))));
		}

	}

	private void MakeOrthogonal(HLink link, float orthSegmentCoord) {
		Integer index = this._levelFlowDir;
		Integer num2 = this._edgeFlowDir;
		if (link.GetFromCoord(num2) != link.GetToCoord(num2)) {
			link.AddTwoDummyNodesAtEastWestLink();
			HNode to = link.GetStartSegment().GetTo();
			HNode from = link.GetEndSegment().GetFrom();
			to.SetCoord(num2, link.GetFromCoord(num2));
			from.SetCoord(num2, link.GetToCoord(num2));
			to.SetCoord(index, orthSegmentCoord);
			from.SetCoord(index, orthSegmentCoord);
		}

	}

	private Boolean ResolveConflicts(HLink[] links, float minDelta,
			Boolean forwardSweep) {
		Integer num3 = null;
		Integer num4 = null;
		Integer length = null;
		Integer num14 = null;
		Boolean flag = false;
		Integer xdir = this._levelFlowDir;
		Integer index = this._edgeFlowDir;
		if (forwardSweep) {
			num3 = 1;
			num4 = 0;
			length = links.length;
			num14 = 1;
		} else {
			num3 = -1;
			num4 = links.length - 1;
			length = -1;
			num14 = 2;
		}
		for (Integer i = num4; i != length; i += num3) {
			HLink link = links[i];
			float num6 = Math.Min(link.GetFromCoord(index),
					link.GetToCoord(index));
			float num7 = Math.Max(link.GetFromCoord(index),
					link.GetToCoord(index));
			if (num6 != num7) {
				Integer num10 = this.GetLinkType(link, xdir, index);
				for (Integer j = i + num3; j != length; j += num3) {
					HLink link2 = links[j];
					float num8 = Math.Min(link2.GetFromCoord(index),
							link2.GetToCoord(index));
					float num9 = Math.Max(link2.GetFromCoord(index),
							link2.GetToCoord(index));
					if (num8 != num9) {
						Integer num11 = this.GetLinkType(link2, xdir, index);
						if (link.GetMultiLinkIndex() == link2
								.GetMultiLinkIndex()) {
							if ((num6 <= num8) && (num8 <= (num7 + minDelta))) {
								if ((num10 == 1) && (num10 == num14)) {
									link.SetMultiLinkIndex(link
											.GetMultiLinkIndex() + 1);
									flag = true;
								} else if (num11 == num14) {
									link2.SetMultiLinkIndex(link2
											.GetMultiLinkIndex() + 1);
									flag = true;
								}
							} else if ((num8 <= num6)
									&& (num6 <= (num9 + minDelta))) {
								if ((num11 == 1) && (num11 == num14)) {
									link2.SetMultiLinkIndex(link2
											.GetMultiLinkIndex() + 1);
									flag = true;
								} else if (num10 == num14) {
									link.SetMultiLinkIndex(link
											.GetMultiLinkIndex() + 1);
									flag = true;
								}
							}
						}
					}
				}
			}
		}

		return flag;

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();
		Integer numberOfEastWestLinks = graph.GetNumberOfEastWestLinks();
		Boolean flag = graph.HasOrthogonalLinks();
		PercCompleteController percController = super.GetPercController();
		percController.StartStep(0.1f, 4);
		if (numberOfEastWestLinks != 0) {
			HLinkIterator eastWestLinks = graph.GetEastWestLinks();

			while (eastWestLinks.HasNext()) {
				HLink link = eastWestLinks.Next();
				link.GetFrom().SetMarker(1);
				link.GetTo().SetMarker(1);
			}
			percController.AddPoints(1);
			super.LayoutStepPerformed();
			this.CalcEastSideLinkNumbers();
			percController.AddPoints(1);
			super.LayoutStepPerformed();
			this.CalcConnectionPoints();
			percController.AddPoints(1);
			super.LayoutStepPerformed();
			if (flag) {
				this.MakeLinksOrthogonal();
			}
			percController.AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

}
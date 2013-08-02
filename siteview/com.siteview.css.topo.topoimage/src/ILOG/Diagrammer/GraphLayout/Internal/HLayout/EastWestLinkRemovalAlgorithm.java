package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class EastWestLinkRemovalAlgorithm extends HLevelAlgorithm {
	public EastWestLinkRemovalAlgorithm() {
	}

	private Integer DetectEastWestLinks() {
		Integer num = 0;
		HLevel upperLevel = super.GetUpperLevel();
		HNodeIterator nodes = upperLevel.GetNodes();
		upperLevel.UpdateInfo();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			HSegmentIterator segmentsFrom = node.GetSegmentsFrom();

			while (segmentsFrom.HasNext()) {
				HLink ownerLink = segmentsFrom.Next().GetOwnerLink();
				if ((ownerLink.GetFrom() == node)
						&& this.IsEastWestLink(ownerLink)) {
					this.GetUniqueDummyNode(ownerLink).SetMarker(1);
					num++;
				}
			}
		}

		return num;

	}

	private HNode GetUniqueDummyNode(HLink link) {
		HSegment startSegment = link.GetStartSegment();
		HNode node = startSegment.GetFrom().IsDummyNode() ? startSegment
				.GetFrom() : startSegment.GetTo();

		if (!node.IsDummyNode()) {

			return null;
		}
		if (node.GetOpposite(startSegment) != link.GetEndSegment()) {

			return null;
		}

		return node;

	}

	private Boolean IsEastWestLink(HLink link) {
		HNode from = link.GetFrom();
		HNode to = link.GetTo();
		if (from.GetLevelNumber() != to.GetLevelNumber()) {

			return false;
		}
		Integer num = from.GetPositionInLevel() - to.GetPositionInLevel();
		if ((num != 1) && (num != -1)) {

			return false;
		}

		if (from.IsEastWestPortAuxNode()) {

			return false;
		}

		if (to.IsEastWestPortAuxNode()) {

			return false;
		}

		if (link.IsFromPortNumberSpecified()) {

			return false;
		}

		if (link.IsToPortNumberSpecified()) {

			return false;
		}
		Integer num2 = (num < 0) ? link.GetOrigFromPortSide() : link
				.GetOrigToPortSide();
		Integer num3 = (num < 0) ? link.GetOrigToPortSide() : link
				.GetOrigFromPortSide();
		if (((num2 != -1) && (num2 != 1)) && (num2 != -3)) {

			return false;
		}
		if (((num3 != -1) && (num3 != 3)) && (num3 != -3)) {

			return false;
		}

		return true;

	}

	private void RemoveMarkedDummyNodesAndMakeEastWestLinks(
			Integer numEastWestLinks) {
		HGraph graph = super.GetGraph();
		HLevel lowerLevel = super.GetLowerLevel();
		Integer nodesCount = lowerLevel.GetNodesCount();
		HNode[] nodesField = lowerLevel.GetNodesField();
		HNode[] nodes = new HNode[nodesCount - numEastWestLinks];
		Integer num2 = 0;
		for (Integer i = 0; i < nodesCount; i++) {
			if (nodesField[i].GetMarker() == 1) {
				nodesField[i].SetLevelNumber(-1);
				graph.MakeEastWestLink(nodesField[i].GetOwnerLink());
			} else {
				nodes[num2++] = nodesField[i];
			}
		}
		lowerLevel.SetNodesField(nodes);
		lowerLevel.UpdateInfo();

	}

	@Override
	public void Run() {
		this.UnmarkLowerLevelNodes();
		Integer numEastWestLinks = this.DetectEastWestLinks();
		if (numEastWestLinks != 0) {
			this.RemoveMarkedDummyNodesAndMakeEastWestLinks(numEastWestLinks);
		}

	}

	private void UnmarkLowerLevelNodes() {
		HNodeIterator nodes = super.GetLowerLevel().GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().SetMarker(0);
		}

	}

}
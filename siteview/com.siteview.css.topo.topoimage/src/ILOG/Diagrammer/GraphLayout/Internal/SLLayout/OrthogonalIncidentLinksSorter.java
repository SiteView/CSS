package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.*;

public final class OrthogonalIncidentLinksSorter extends IncidentLinksSorter {
	public OrthogonalIncidentLinksSorter(ShortLinkAlgorithm layout) {
		super(layout);
	}

	@Override
	public Boolean CompareLinks(LinkData link1, LinkData link2,
			Boolean origin1, Boolean origin2, SLNodeSide nodeSide) {

		return nodeSide.CompareOrthogonalLinks(super._layout, link1, link2,
				origin1, origin2);

	}

}
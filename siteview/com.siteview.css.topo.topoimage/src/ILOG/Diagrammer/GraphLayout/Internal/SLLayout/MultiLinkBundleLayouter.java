package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Collections.*;

public final class MultiLinkBundleLayouter {
	public MultiLinkBundleLayouter() {
	}

	public void Clean() {

	}

	private void Layout(ShortLinkAlgorithm layout, IGraphModel graphModel,
			MultiLinkData masterLink, Integer bundleMode, float linkOffset,
			Boolean redraw) {
		ArrayList individualLinks = masterLink.GetIndividualLinks();
		Integer count = individualLinks.get_Count();
		float linkWidth = 0f;
		float num3 = 0f;
		float num4 = 0f;
		float originLinkOffset = 0f;
		float destinationLinkOffset = 0f;
		InternalPoint masterFromPoint = null;
		InternalPoint masterToPoint = null;
		InternalPoint[] masterBendPoints = null;
		Integer nBends = 0;
		Boolean goingLeftOrigin = false;
		float num8 = 0f;
		float num9 = 0f;
		float offset = 0f;
		float offsetOrig = 0f;
		float offsetDest = 0f;
		Boolean onlyFirstAndLastSeg = bundleMode != 2;
		Boolean copyPointLocations = onlyFirstAndLastSeg;
		if (bundleMode != 0) {

			masterFromPoint = masterLink.GetConnectionPoint(true);

			masterToPoint = masterLink.GetConnectionPoint(false);
			LinkShape linkShape = masterLink.GetLinkShape();

			masterBendPoints = linkShape.GetIntermediatePoints();
			nBends = linkShape.GetNumberOfPoints() - 2;

			linkWidth = masterLink.GetLinkWidth();

			num3 = masterLink.GetLinkWidth(masterLink.GetOriginLinkOffset());

			num4 = masterLink.GetLinkWidth(masterLink
					.GetDestinationLinkOffset());

			originLinkOffset = masterLink.GetOriginLinkOffset();

			destinationLinkOffset = masterLink.GetDestinationLinkOffset();

			goingLeftOrigin = masterLink.IsGoingLeft(true);
		}
		for (Integer i = 0; i < count; i++) {
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) individualLinks
					.get_Item(i);
			linkData.PrepareForBundleLayout(masterLink, copyPointLocations);
			if (bundleMode != 0) {
				num8 = linkData.GetLinkWidth() * 0.5f;
				if (i == 0) {
					offset = (linkWidth * 0.5f) - num8;
					offsetOrig = (num3 * 0.5f) - num8;
					offsetDest = (num4 * 0.5f) - num8;
				} else {
					offset -= (linkOffset + num8) + num9;
					offsetOrig -= (originLinkOffset + num8) + num9;
					offsetDest -= (destinationLinkOffset + num8) + num9;
				}
				num9 = num8;
				linkData.ApplyOffset(masterFromPoint, masterToPoint,
						masterBendPoints, nBends, offset, offsetOrig,
						offsetDest, goingLeftOrigin,
						masterLink.GetFromNode() != linkData.GetFromNode(),
						onlyFirstAndLastSeg);
			}
			layout.FinalShapeLink(graphModel, linkData, false, redraw);
		}

	}

	public void Layout(ShortLinkAlgorithm layout, IGraphModel graphModel,
			MultiLinkData masterLink, Integer bundleMode, float linkOffset,
			float minFinalSegmentLength, Boolean redraw) {
		ArrayList individualLinks = masterLink.GetIndividualLinks();
		if ((individualLinks != null) && (individualLinks.get_Count() != 0)) {
			this.Layout(layout, graphModel, masterLink, bundleMode, linkOffset,
					redraw);
		}

	}

}
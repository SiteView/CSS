package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Collections.*;

public final class NodeSideLayouter {
	private float _evenlySpacedPinsMarginRatio;

	private Integer[] _nSegments = new Integer[1];

	private float[] _totalWidth = new float[1];

	private ArrayList _vectLinks;

	public NodeSideLayouter() {
	}

	private float AdjustLinkOffsetIfAppropriate(float totalWidthWithoutOffsets,
			Integer nLineSegments, Integer nMargins, float availableLength,
			float linkOffset, Integer connectorStyle) {
		if ((linkOffset <= 0f) || (connectorStyle == 4)) {

			return linkOffset;
		}
		if ((totalWidthWithoutOffsets >= availableLength)
				|| (nLineSegments == 1)) {

			return 0f;
		}
		float num = nLineSegments
				+ (nMargins * this._evenlySpacedPinsMarginRatio);
		float num2 = this.ComputeTotalWidthOfLinks(totalWidthWithoutOffsets,
				num, linkOffset);
		if ((connectorStyle == 0) && (num2 <= availableLength)) {

			return linkOffset;
		}
		float num3 = availableLength - totalWidthWithoutOffsets;

		return (num3 / (num - 1f));

	}

	public void Clean() {
		if (this._vectLinks != null) {
			this._vectLinks.Clear();
		}

	}

	private float ComputeTotalWidthOfLinks(float totalWidthWithoutOffsets,
			float nLineSegments, float linkOffset) {
		if (linkOffset == 0f) {

			return totalWidthWithoutOffsets;
		}

		return (totalWidthWithoutOffsets + ((nLineSegments - 1f) * linkOffset));

	}

	private void ComputeTotalWidthOfLinksWithoutOffsets(ArrayList vectLinks,
			float[] totalWidth, Integer[] nLineSegments) {
		Integer num = (vectLinks != null) ? vectLinks.get_Count() : 0;
		if (num == 0) {
			totalWidth[0] = 0f;
			nLineSegments[0] = 0;
		} else {
			float num2 = 0f;
			Integer num3 = 0;
			for (Integer i = 0; i < num; i++) {
				ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) vectLinks
						.get_Item(i);

				if (data.IsSameSideSelfLink()) {
					num2 += 2f * data.GetLinkWidth(0f);
					num3 += 2 * data.GetNumberOfIndividualLinks();
				} else {

					num2 += data.GetLinkWidth(0f);

					num3 += data.GetNumberOfIndividualLinks();
				}
			}
			totalWidth[0] = num2;
			nLineSegments[0] = num3;
		}

	}

	private ArrayList Concatenate(ArrayList vect1, ArrayList vect2) {
		if (vect1 == null) {

			return vect2;
		}
		if (vect2 == null) {

			return vect1;
		}
		Integer count = vect1.get_Count();
		Integer num2 = vect2.get_Count();
		if (this._vectLinks == null) {
			this._vectLinks = new ArrayList(count + num2);
		} else {
			this._vectLinks.Clear();
			this._vectLinks.set_Capacity(count + num2);
		}
		for (Integer i = 0; i < count; i++) {
			this._vectLinks.Add(vect1.get_Item(i));
		}
		for (Integer j = 0; j < num2; j++) {
			this._vectLinks.Add(vect2.get_Item(j));
		}

		return this._vectLinks;

	}

	private ArrayList Concatenate(ArrayList vect1, ArrayList vect2,
			ArrayList vect3) {
		if (vect1 == null) {
			if (vect2 != null) {

				return this.Concatenate(vect2, vect3);
			}

			return vect3;
		}
		if (vect2 == null) {
			if (vect3 != null) {

				return this.Concatenate(vect1, vect3);
			}

			return vect1;
		}
		if (vect3 == null) {

			return this.Concatenate(vect1, vect2);
		}
		Integer count = vect1.get_Count();
		Integer num2 = vect2.get_Count();
		Integer num3 = vect3.get_Count();
		if (this._vectLinks == null) {
			this._vectLinks = new ArrayList((count + num2) + num3);
		} else {
			this._vectLinks.Clear();
			this._vectLinks.set_Capacity((count + num2) + num3);
		}
		for (Integer i = 0; i < count; i++) {
			this._vectLinks.Add(vect1.get_Item(i));
		}
		for (Integer j = 0; j < num2; j++) {
			this._vectLinks.Add(vect2.get_Item(j));
		}
		for (Integer k = 0; k < num3; k++) {
			this._vectLinks.Add(vect3.get_Item(k));
		}

		return this._vectLinks;

	}

	private void Layout(IncidentLinksSorter linkSorter,
			IncidentLinksRefiner linkRefiner, ArrayList vectLinks,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			SLNodeSide nodeSide, float initialDist, float linkOffset,
			float minFinalSegmentLength, Integer bundleMode,
			Boolean reversedOrder, Boolean withFixedConnectionPoints) {
		Integer length = (vectLinks != null) ? vectLinks.get_Count() : 0;
		if (length != 0) {
			if (linkSorter != null) {
				linkSorter.Sort(vectLinks, nodeData, nodeSide, bundleMode);
			}
			float tangentialDistForConnectionPoint = initialDist;
			InternalRect linkConnectionRect = nodeData.GetLinkConnectionRect();
			for (Integer i = 0; i < length; i++) {
				ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) LayoutUtil
						.ElementAt(vectLinks, i, length, reversedOrder);
				linkData.ShapePointsModified();
				float num3 = linkData.GetLinkWidth(linkOffset) * 0.5f;
				tangentialDistForConnectionPoint += num3;
				Boolean origin = linkData.IsOrigin(nodeData, nodeSide);

				if (linkData.IsSameSideSelfLink()) {
					nodeSide.ComputeConnectionPoint(linkConnectionRect,
							linkData, true, tangentialDistForConnectionPoint);
					tangentialDistForConnectionPoint += (2f * num3)
							+ linkOffset;
					nodeSide.ComputeConnectionPoint(linkConnectionRect,
							linkData, false, tangentialDistForConnectionPoint);
					linkData.SetOriginLinkOffset(linkOffset);
					linkData.SetDestinationLinkOffset(linkOffset);
				} else {
					nodeSide.ComputeConnectionPoint(linkConnectionRect,
							linkData, origin, tangentialDistForConnectionPoint);
					if (origin) {
						linkData.SetOriginLinkOffset(linkOffset);
					} else {
						linkData.SetDestinationLinkOffset(linkOffset);
					}
				}
				tangentialDistForConnectionPoint += num3 + linkOffset;
				linkData.ShapePointsModified();
			}
			if ((linkRefiner != null) && (bundleMode == 2)) {
				linkRefiner.Refine(vectLinks, nodeData, nodeSide, linkOffset,
						minFinalSegmentLength, reversedOrder,
						withFixedConnectionPoints);
			}
		}

	}

	public void Layout(ShortLinkAlgorithm layout,
			IncidentLinksSorter linkSorter, IncidentLinksRefiner linkRefiner,
			ILinkConnectionBoxProvider connectionBoxInterface,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			SLNodeSide nodeSide, ArrayList vectLinksOrig,
			ArrayList vectLinksDest, ArrayList vectFixedLinks,
			float linkOffset, float minFinalSegmentLength, Integer bundleMode) {
		Integer num = (vectLinksOrig != null) ? vectLinksOrig.get_Count() : 0;
		Integer num2 = (vectLinksDest != null) ? vectLinksDest.get_Count() : 0;
		Integer num3 = (vectFixedLinks != null) ? vectFixedLinks.get_Count()
				: 0;
		if (((num + num2) + num3) != 0) {
			if ((vectFixedLinks == null) || (vectFixedLinks.get_Count() == 0)) {
				if (linkSorter != null) {
					linkSorter.Sort(vectLinksOrig, nodeData, nodeSide,
							bundleMode);
					linkSorter.Sort(vectLinksDest, nodeData, nodeSide,
							bundleMode);
				}
				ArrayList vectLinks = this.Concatenate(vectLinksOrig,
						vectLinksDest);
				this.LayoutWithoutFixedLinks(layout, linkRefiner,
						connectionBoxInterface, nodeData, nodeSide, vectLinks,
						linkOffset, minFinalSegmentLength, bundleMode);
			} else {
				this.LayoutWithFixedLinks(layout, linkSorter, linkRefiner,
						connectionBoxInterface, nodeData, nodeSide,
						vectLinksOrig, vectLinksDest, vectFixedLinks,
						linkOffset, minFinalSegmentLength, bundleMode);
			}
		}

	}

	private void LayoutWithFixedLinks(ShortLinkAlgorithm layout,
			IncidentLinksSorter linkSorter, IncidentLinksRefiner linkRefiner,
			ILinkConnectionBoxProvider connectionBoxInterface,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			SLNodeSide nodeSide, ArrayList vectLinksOrig,
			ArrayList vectLinksDest, ArrayList vectFixedLinks,
			float linkOffset, float minFinalSegmentLength, Integer bundleMode) {
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data = null;
		Boolean flag = null;
		InternalPoint connectionPoint = null;
		float num6 = 0;
		Integer num = (vectLinksOrig != null) ? vectLinksOrig.get_Count() : 0;
		Integer num2 = (vectLinksDest != null) ? vectLinksDest.get_Count() : 0;
		float maxValue = Float.MAX_VALUE;
		float minValue = Float.MIN_VALUE;
		Integer num5 = (vectFixedLinks != null) ? vectFixedLinks.get_Count()
				: 0;
		if (num5 == 0) {
			throw (new system.Exception(
					"Internal error: should not be called when no links with fixed connection"));
		}
		if (bundleMode == 2) {
			linkSorter.Sort(vectFixedLinks, nodeData, nodeSide, bundleMode);
			if (num > 0) {
				data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) vectFixedLinks
						.get_Item(0);
				num6 = data.GetLinkWidth() * 0.5f;

				flag = data.IsOrigin(nodeData, nodeSide);

				connectionPoint = data.GetConnectionPoint(flag);
				maxValue = nodeSide.GetTangentialCoord(connectionPoint) - num6;
			}
			if (num2 > 0) {
				data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) vectFixedLinks
						.get_Item(num5 - 1);
				num6 = data.GetLinkWidth() * 0.5f;

				flag = data.IsOrigin(nodeData, nodeSide);

				connectionPoint = data.GetConnectionPoint(flag);
				minValue = nodeSide.GetTangentialCoord(connectionPoint) + num6;
			}
		} else {
			for (Integer i = 0; i < num5; i++) {
				data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) vectFixedLinks
						.get_Item(i);

				flag = data.IsOrigin(nodeData, nodeSide);

				connectionPoint = data.GetConnectionPoint(flag);
				float tangentialCoord = nodeSide
						.GetTangentialCoord(connectionPoint);
				num6 = data.GetLinkWidth() * 0.5f;
				if (num > 0) {
					float num8 = tangentialCoord - num6;
					if (num8 < maxValue) {
						maxValue = num8;
					}
				}
				if (num2 > 0) {
					float num9 = tangentialCoord + num6;
					if (num9 > minValue) {
						minValue = num9;
					}
				}
			}
		}
		InternalRect linkConnectionRect = nodeData.GetLinkConnectionRect();
		float num11 = linkOffset;
		float num12 = linkOffset;
		this.ComputeTotalWidthOfLinksWithoutOffsets(vectLinksOrig,
				this._totalWidth, this._nSegments);
		float totalWidthWithoutOffsets = this._totalWidth[0];
		Integer nLineSegments = this._nSegments[0] + 1;
		this.ComputeTotalWidthOfLinksWithoutOffsets(vectLinksDest,
				this._totalWidth, this._nSegments);
		float num15 = this._totalWidth[0];
		Integer num16 = this._nSegments[0] + 1;
		float minTangentialCoord = nodeSide
				.GetMinTangentialCoord(linkConnectionRect);
		float maxTangentialCoord = nodeSide
				.GetMaxTangentialCoord(linkConnectionRect);
		float initialDist = 0f;
		float num20 = 0f;
		Integer connectorStyle = nodeData.GetConnectorStyle();
		if (num > 0) {
			float availableLength = maxValue - minTangentialCoord;
			if (availableLength < 0f) {
				availableLength = 0f;
			}

			num11 = this.AdjustLinkOffsetIfAppropriate(
					totalWidthWithoutOffsets, nLineSegments, 1,
					availableLength, num11, connectorStyle);
			float num23 = this.ComputeTotalWidthOfLinks(
					totalWidthWithoutOffsets, (float) nLineSegments, num11);
			initialDist = (maxValue - minTangentialCoord) - num23;
		}
		if (num2 > 0) {
			float num24 = maxTangentialCoord - minValue;
			if (num24 < 0f) {
				num24 = 0f;
			}

			num12 = this.AdjustLinkOffsetIfAppropriate(num15, num16, 1, num24,
					num12, connectorStyle);
			num20 = (minValue - minTangentialCoord) + num12;
		}
		if (connectionBoxInterface != null) {
			float num25 = 0;
			IGraphModel graphModel = layout.GetGraphModel();
			java.lang.Object nodeOrLink = nodeData.get_nodeOrLink();
			if (graphModel instanceof SubgraphData) {

				num25 = ((SubgraphData) graphModel).GetTangentialOffset(
						connectionBoxInterface, nodeOrLink,
						(int) nodeSide.GetSide());
			} else {

				num25 = TranslateUtil.GetTangentialOffset(
						connectionBoxInterface, graphModel, nodeOrLink,
						nodeSide.GetSide());
			}
			initialDist += num25;
			num20 += num25;
		}
		if (num > 0) {
			this.Layout(null, null, vectLinksOrig, nodeData, nodeSide,
					initialDist, num11, minFinalSegmentLength, bundleMode,
					false, false);
		}
		if (num2 > 0) {
			this.Layout(null, null, vectLinksDest, nodeData, nodeSide, num20,
					num12, minFinalSegmentLength, bundleMode, false, false);
		}
		if ((linkRefiner != null) && (bundleMode == 2)) {
			this.Refine(linkRefiner, vectLinksOrig, vectFixedLinks,
					vectLinksDest, nodeData, nodeSide, linkOffset,
					minFinalSegmentLength, bundleMode);
		}

	}

	private void LayoutWithoutFixedLinks(ShortLinkAlgorithm layout,
			IncidentLinksRefiner linkRefiner,
			ILinkConnectionBoxProvider connectionBoxInterface,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			SLNodeSide nodeSide, ArrayList vectLinks, float linkOffset,
			float minFinalSegmentLength, Integer bundleMode) {
		Integer num = (vectLinks != null) ? vectLinks.get_Count() : 0;
		if (num != 0) {
			if ((num == 1) && (connectionBoxInterface == null)) {
				ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) vectLinks
						.get_Item(0);

				if (!data.IsSameSideSelfLink() && !data.IsMaster()) {

					return;
				}
			}
			InternalRect linkConnectionRect = nodeData.GetLinkConnectionRect();
			float availableLength = nodeSide
					.GetMaxTangentialCoord(linkConnectionRect)
					- nodeSide.GetMinTangentialCoord(linkConnectionRect);
			this.ComputeTotalWidthOfLinksWithoutOffsets(vectLinks,
					this._totalWidth, this._nSegments);
			float totalWidthWithoutOffsets = this._totalWidth[0];
			Integer nLineSegments = this._nSegments[0];

			linkOffset = this.AdjustLinkOffsetIfAppropriate(
					totalWidthWithoutOffsets, nLineSegments, 2,
					availableLength, linkOffset, nodeData.GetConnectorStyle());
			float num5 = this
					.ComputeTotalWidthOfLinks(totalWidthWithoutOffsets,
							(float) nLineSegments, linkOffset);
			float initialDist = (availableLength - num5) * 0.5f;
			if (connectionBoxInterface != null) {
				IGraphModel graphModel = layout.GetGraphModel();
				java.lang.Object nodeOrLink = nodeData.get_nodeOrLink();
				if (graphModel instanceof SubgraphData) {

					initialDist += ((SubgraphData) graphModel)
							.GetTangentialOffset(connectionBoxInterface,
									nodeOrLink, (int) nodeSide.GetSide());
				} else {

					initialDist += TranslateUtil.GetTangentialOffset(
							connectionBoxInterface, graphModel, nodeOrLink,
							nodeSide.GetSide());
				}
			}
			this.Layout(null, linkRefiner, vectLinks, nodeData, nodeSide,
					initialDist, linkOffset, minFinalSegmentLength, bundleMode,
					false, false);
		}

	}

	public void Refine(IncidentLinksRefiner linkRefiner,
			ArrayList vectLinksOrig, ArrayList vectLinksDest,
			ArrayList vectFixedLinks,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			SLNodeSide nodeSide, float linkOffset, float minFinalSegmentLength,
			Integer bundleMode) {
		if ((linkRefiner != null) && (bundleMode == 2)) {
			ArrayList vectLinks = this.Concatenate(vectLinksOrig,
					vectFixedLinks, vectLinksDest);
			if ((vectLinks != null) && (vectLinks.get_Count() > 1)) {
				linkRefiner.Refine(vectLinks, nodeData, nodeSide, linkOffset,
						minFinalSegmentLength, false, (vectFixedLinks != null)
								&& (vectFixedLinks.get_Count() > 0));
			}
		}

	}

	public void Update(ShortLinkAlgorithm layout) {
		ShortLinkLayout shortLinkLayout = layout.GetShortLinkLayout();
		this._evenlySpacedPinsMarginRatio = shortLinkLayout
				.get_EvenlySpacedConnectorMarginRatio();

	}

}
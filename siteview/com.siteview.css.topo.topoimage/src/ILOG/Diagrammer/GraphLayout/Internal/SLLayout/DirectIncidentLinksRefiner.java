package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.Math;
import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.LayoutUtil;

public final class DirectIncidentLinksRefiner extends IncidentLinksRefiner {
	public DirectIncidentLinksRefiner(ShortLinkAlgorithm layout) {
		super(layout);
	}

	@Override
	public void Refine(ArrayList vectLinks,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData,
			SLNodeSide nodeSide, float linkOffset, float minFinalSegmentLength,
			Boolean reversedOrder, Boolean withFixedConnectionPoints) {
		Integer length = (vectLinks == null) ? 0 : vectLinks.get_Count();
		if (length >= 2) {
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data = null;
			InternalPoint point2 = null;
			float tangentDelta = 0;
			float num5 = 0;
			float linkWidth = 0;
			float num7 = 0;
			Boolean flag2 = null;
			Boolean flag3 = null;
			float num8 = 0;
			float num9 = 0;
			Boolean flag4 = null;
			float radialDelta = 0f;
			Boolean flag = false;
			InternalPoint pointAt = null;
			InternalPoint point3 = this.GetLayoutAlgorithm().GetTempPoint1();
			float num4 = 0f;
			InternalPoint point4 = null;
			for (Integer i = 0; i < length; i++) {
				data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) LayoutUtil
						.ElementAt(vectLinks, i, length, reversedOrder);

				if (!data.IsFixed()) {

					flag3 = data.IsSameSideSelfLink();
					if (flag3) {
						if (flag) {
							continue;
						}
						flag = true;
					}

					flag4 = data.IsOrigin(nodeData, nodeSide);

					pointAt = data.GetPointAt(flag4, 1);

					point2 = data.GetPointAt(flag4, 2);
					if (i > 0) {

						num4 = nodeSide.GetTangentDelta(point3, pointAt);
						if (num4 < 0f) {
							num4 = -num4;
						}
					}
					point3.SetLocation(pointAt);

					tangentDelta = nodeSide.GetTangentDelta(pointAt, point2);
					if (point4 == null) {
						point4 = pointAt;
					}
					if ((tangentDelta < 0f) && !flag3) {
						break;
					}

					flag2 = !data.IsFixed()
							&& data.GetCurrentShapeType().IsOrthogonal();
					num5 = 0f;

					linkWidth = data.GetLinkWidth();
					if (flag2) {
						if (i >= (length - 1)) {
							break;
						}
						ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data2 = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) LayoutUtil
								.ElementAt(vectLinks, i + 1, length,
										reversedOrder);

						if (!data2.IsFixed()
								&& data2.GetCurrentShapeType().IsOrthogonal()) {

							num7 = data2.GetLinkWidth();
							super.RefineLinksOrthogonal(data, data2, flag4,
									data2.IsOrigin(nodeData, nodeSide),
									nodeSide, true, linkOffset, true);
							num5 = (i > 0) ? (((2f * linkOffset) + linkWidth) + num7)
									: (((1.5f * linkOffset) + (0.5f * linkWidth)) + num7);
							radialDelta += num5;
							continue;
						}
					}

					num8 = nodeSide.GetRadialDelta(pointAt, point2);
					num9 = num8
							+ ((float) Math
									.Sqrt((double) ((num8 * num8) + (tangentDelta * tangentDelta))));
					num5 = (linkWidth + linkOffset)
							* (((num9 > 1E-05) || (num9 < -1E-05)) ? (tangentDelta / num9)
									: 1f);
					if (num5 < 0f) {
						num5 = -num5;
					}

					num5 = Math.Min(num5, (i > 0) ? (num4 * 4f)
							: ((tangentDelta >= 0f) ? tangentDelta
									: -tangentDelta));
					if (i > 0) {
						radialDelta += num5 * 0.5f;

						if (!flag3 && !data.IsFixed()) {
							data.ShapePointsModified();
							nodeSide.SetRadialCoord(pointAt,
									nodeSide.GetRadialCoord(point4));
							nodeSide.TranslateOutside(pointAt, radialDelta);
							if (flag2) {
								nodeSide.SetRadialCoord(point2,
										nodeSide.GetRadialCoord(pointAt));
							}
							data.GetLinkShape().InvalidateBBox();
						}
					}
					radialDelta += num5 * 0.5f;
				}
			}
			radialDelta = 0f;
			num4 = 0f;
			flag = false;
			Integer num11 = length - 1;
			point4 = null;
			for (Integer j = num11; j >= 0; j--) {
				data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) LayoutUtil
						.ElementAt(vectLinks, j, length, reversedOrder);

				if (!data.IsFixed()) {

					flag3 = data.IsSameSideSelfLink();
					if (flag3) {
						if (flag) {
							continue;
						}
						flag = true;
					}

					flag4 = data.IsOrigin(nodeData, nodeSide);

					pointAt = data.GetPointAt(flag4, 1);

					point2 = data.GetPointAt(flag4, 2);
					if (j < num11) {

						num4 = nodeSide.GetTangentDelta(point3, pointAt);
						if (num4 < 0f) {
							num4 = -num4;
						}
					}
					point3.SetLocation(pointAt);
					if (point4 == null) {
						point4 = pointAt;
					}

					tangentDelta = nodeSide.GetTangentDelta(pointAt, point2);
					if ((tangentDelta >= 0f) && !flag3) {

						return;
					}

					flag2 = !data.IsFixed()
							&& data.GetCurrentShapeType().IsOrthogonal();
					num5 = 0f;

					linkWidth = data.GetLinkWidth();
					if (flag2) {
						if (j <= 0) {
							break;
						}
						ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData data3 = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) LayoutUtil
								.ElementAt(vectLinks, j - 1, length,
										reversedOrder);

						if (!data3.IsFixed()
								&& data3.GetCurrentShapeType().IsOrthogonal()) {

							num7 = data3.GetLinkWidth();
							super.RefineLinksOrthogonal(data, data3, flag4,
									data3.IsOrigin(nodeData, nodeSide),
									nodeSide, false, linkOffset, true);
							num5 = (j < num11) ? (((2f * linkOffset) + linkWidth) + num7)
									: (((1.5f * linkOffset) + (0.5f * linkWidth)) + num7);
							radialDelta += num5;
							continue;
						}
					}

					num8 = nodeSide.GetRadialDelta(pointAt, point2);
					num9 = num8
							+ ((float) Math
									.Sqrt((double) ((num8 * num8) + (tangentDelta * tangentDelta))));
					num5 = (linkWidth + linkOffset)
							* (((num9 > 1E-05) || (num9 < -1E-05)) ? (-tangentDelta / num9)
									: 1f);
					if (num5 < 0f) {
						num5 = -num5;
					}

					num5 = Math.Min(num5, (j < num11) ? (num4 * 4f)
							: ((tangentDelta >= 0f) ? tangentDelta
									: -tangentDelta));
					if (j < num11) {
						radialDelta += num5 * 0.5f;

						if (!flag3 && !data.IsFixed()) {
							data.ShapePointsModified();
							nodeSide.SetRadialCoord(pointAt,
									nodeSide.GetRadialCoord(point4));
							nodeSide.TranslateOutside(pointAt, radialDelta);
							if (flag2) {
								nodeSide.SetRadialCoord(point2,
										nodeSide.GetRadialCoord(pointAt));
							}
							data.GetLinkShape().InvalidateBBox();
						}
					}
					radialDelta += num5 * 0.5f;
				}
			}
		}

	}

}
package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.ArgumentException;
import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.IGraphModel;
import ILOG.Diagrammer.GraphLayout.ShortLinkLayout;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.LayoutUtil;
import ILOG.Diagrammer.GraphLayout.Internal.SubgraphData;

public final class CostComputer {
	private float _linkLinkPenalty;

	private float _linkNodePenalty;

	private InternalRect _tempRect1 = new InternalRect(0f, 0f, 0f, 0f);

	private InternalRect _tempRect2 = new InternalRect(0f, 0f, 0f, 0f);

	private InternalRect _tempRect3 = new InternalRect(0f, 0f, 0f, 0f);

	private float SMALL_PENALTY = 0.01f;

	public CostComputer() {
	}

	public void Clean() {

	}

	private void ComputeBounds(InternalRect rect, InternalPoint fromPoint,
			InternalPoint toPoint, Boolean isFromPointConnectionPoint,
			Boolean isToPointConnectionPoint, float halfLineWidth) {
		float num = toPoint.X - fromPoint.X;
		float num2 = toPoint.Y - fromPoint.Y;
		if (!isFromPointConnectionPoint && !isToPointConnectionPoint) {
			rect.Reshape((fromPoint.X <= toPoint.X) ? fromPoint.X : toPoint.X,
					(fromPoint.Y <= toPoint.Y) ? fromPoint.Y : toPoint.Y,
					(num >= 0f) ? num : -num, (num2 >= 0f) ? num2 : -num2);
			rect.Expand(halfLineWidth);
		} else {
			float x = 0;
			float num4 = 0;
			float y = 0;
			float num6 = 0;
			Boolean flag = ((num2 < 0f) ? -num2 : num2) < ((num < 0f) ? -num
					: num);
			if (num > 0f) {
				x = fromPoint.X;
				num4 = toPoint.X;
				if (!flag || !isFromPointConnectionPoint) {
					x -= halfLineWidth;
				}
				if (!flag || !isToPointConnectionPoint) {
					num4 += halfLineWidth;
				}
			} else {
				x = toPoint.X;
				num4 = fromPoint.X;
				if (!flag || !isToPointConnectionPoint) {
					x -= halfLineWidth;
				}
				if (!flag || !isFromPointConnectionPoint) {
					num4 += halfLineWidth;
				}
			}
			Boolean flag2 = !flag;
			if (num2 > 0f) {
				y = fromPoint.Y;
				num6 = toPoint.Y;
				if (!flag2 || !isFromPointConnectionPoint) {
					y -= halfLineWidth;
				}
				if (!flag2 || !isToPointConnectionPoint) {
					num6 += halfLineWidth;
				}
			} else {
				y = toPoint.Y;
				num6 = fromPoint.Y;
				if (!flag2 || !isToPointConnectionPoint) {
					y -= halfLineWidth;
				}
				if (!flag2 || !isFromPointConnectionPoint) {
					num6 += halfLineWidth;
				}
			}
			rect.Reshape(x, y, num4 - x, num6 - y);
		}

	}

	private float GetLinkAdjacentNodeCost(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			SubgraphData interGraphLinksRoutingModel, Boolean origin) {
		if (interGraphLinksRoutingModel != null) {
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData data = linkData
					.GetConnectionNode(origin);
			if ((linkData.IsInterGraphLink() && (interGraphLinksRoutingModel
					.FromEndsInside(linkData.get_nodeOrLink(),
							data.get_nodeOrLink()) || interGraphLinksRoutingModel
					.ToEndsInside(linkData.get_nodeOrLink(),
							data.get_nodeOrLink())))
					&& (origin ? linkData.IsQuasiSelfInterGraphLinkOrigin()
							: linkData.IsQuasiSelfInterGraphLinkDestination())) {
				this._tempRect1.SetRect(linkData.GetLinkShape().BoundingBox());

				return (linkData.GetConnectionNode(origin).boundingBox
						.Contains(this._tempRect1) ? ((float) 0) : ((float) 1));
			}
		}
		float num = 0f;
		SLNodeSide nodeSide = linkData.GetNodeSide(origin);
		InternalPoint pointAt = linkData.GetPointAt(origin, 0);
		InternalPoint point2 = linkData.GetPointAt(origin, 1);
		if (nodeSide.GetRadialDelta(pointAt, point2) < 0f) {
			num++;

			return num;
		}
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData connectionNode = linkData
				.GetConnectionNode(origin);
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData data3 = linkData
				.GetConnectionNode(!origin);
		if (linkData.IsOverlapping()
				&& (data3.boundingBox.Contains(connectionNode.boundingBox) || connectionNode.boundingBox
						.Contains(data3.boundingBox))) {

			return 0f;
		}
		InternalPoint from = linkData.GetPointAt(origin, 2);

		if (linkData.GetCurrentShapeType().IsOrthogonal()) {
			Integer numberOfBends = linkData.GetCurrentShapeType()
					.GetNumberOfBends();
			if ((linkData.IsOverlapping() && (numberOfBends >= 1))
					&& this.Intersects(pointAt, point2, 0f, data3.boundingBox,
							false, false)) {
				num++;
			}
			if (numberOfBends >= 2) {
				InternalPoint to = linkData.GetPointAt(origin, 3);

				if (this.Intersects(from, to, 0f, connectionNode.boundingBox,
						false, false)) {
					num++;
				}
			}

			return num;
		}

		if (this.Intersects(point2, from, 0f, connectionNode.boundingBox,
				false, false)) {
			num++;
		}
		if (nodeSide.GetRadialDelta(point2, from) < 0f) {
			num += 0.01f;
		}

		return num;

	}

	public float GetLinkCost(IGraphModel graphModel,
			SubgraphData interGraphLinksRoutingModel,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Boolean global) {

		return this.GetLinkCost(graphModel, interGraphLinksRoutingModel,
				linkData, false, global);

	}

	public float GetLinkCost(IGraphModel graphModel,
			SubgraphData interGraphLinksRoutingModel,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Boolean onlyLinkNodeOverlaps, Boolean global) {
		if (linkData == null) {
			throw (new ArgumentException("link data cannot be null"));
		}
		float num = 0f;
		float num2 = 0f;
		num += this.GetLinkAdjacentNodeCost(linkData,
				interGraphLinksRoutingModel, true)
				+ this.GetLinkAdjacentNodeCost(linkData,
						interGraphLinksRoutingModel, false);
		ArrayList intersectingObjects = linkData.GetIntersectingObjects();
		Integer num3 = (intersectingObjects == null) ? 0 : intersectingObjects
				.get_Count();
		if (num3 > 0) {
			Integer index = linkData.GetIndex();
			for (Integer i = 0; i < num3; i++) {
				ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeOrLinkData data = (ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeOrLinkData) intersectingObjects
						.get_Item(i);
				Integer num5 = 0;

				if (data.IsNode()) {

					num += this
							.GetLinkNodeCost(
									interGraphLinksRoutingModel,
									linkData,
									(ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData) data);
				} else if (!onlyLinkNodeOverlaps) {

					num5 = ((ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) data)
							.GetIndex();
					if (!global || (index < num5)) {

						num2 += this
								.GetLinkLinkCost(
										linkData,
										(ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData) data);
					}
				}
			}
		}
		float num7 = (num2 * this._linkLinkPenalty)
				+ (num * this._linkNodePenalty);
		if ((num7 != 0f) && !onlyLinkNodeOverlaps) {
			Integer numberOfIndividualLinks = linkData
					.GetNumberOfIndividualLinks();
			if (numberOfIndividualLinks > 1) {
				num7 *= numberOfIndividualLinks;
			}
		}

		return num7;

	}

	private float GetLinkLinkCost(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData1,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData2) {
		LinkShape linkShape = linkData1.GetLinkShape();
		LinkShape shape2 = linkData2.GetLinkShape();
		float delta = linkData1.GetLinkWidth() * 0.5f;
		float num2 = linkData2.GetLinkWidth() * 0.5f;
		this._tempRect1.SetRect(linkShape.BoundingBox());
		this._tempRect2.SetRect(shape2.BoundingBox());
		this._tempRect1.Expand(delta);
		this._tempRect2.Expand(num2);

		if (!IsOverlap(this._tempRect1, this._tempRect2)) {

			return 0f;
		}
		Integer num3 = linkShape.GetNumberOfPoints() - 2;
		Integer num4 = shape2.GetNumberOfPoints() - 2;
		Boolean flag = linkData1.IsOrthogonal() && linkData2.IsOrthogonal();
		float num5 = 0f;
		for (Integer i = 0; (i <= num3) && (num5 == 0f); i++) {
			InternalPoint pointAt = linkShape.GetPointAt(i);
			InternalPoint toPoint = linkShape.GetPointAt(i + 1);
			this.ComputeBounds(this._tempRect1, pointAt, toPoint, i == 0,
					i == num3, delta);

			if (IsOverlap(this._tempRect1, this._tempRect2)) {
				for (Integer j = 0; (j <= num4) && (num5 == 0f); j++) {
					InternalPoint fromPoint = shape2.GetPointAt(j);
					InternalPoint point4 = shape2.GetPointAt(j + 1);
					if (flag) {
						this.ComputeBounds(this._tempRect3, fromPoint, point4,
								j == 0, j == num4, num2);

						if (IsOverlap(this._tempRect1, this._tempRect3)) {
							num5++;
						}
					} else if (LayoutUtil.Intersects(pointAt.X, pointAt.Y,
							toPoint.X, toPoint.Y, fromPoint.X, fromPoint.Y,
							point4.X, point4.Y)) {
						num5++;
					}
				}
			}
		}

		return num5;

	}

	private float GetLinkNodeCost(SubgraphData interGraphLinksRoutingModel,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData nodeData) {
		if (linkData == null) {
			throw (new system.Exception("no link data on link"));
		}

		if (((interGraphLinksRoutingModel == null) || !linkData
				.IsInterGraphLink())
				|| (!interGraphLinksRoutingModel.FromEndsInside(
						linkData.get_nodeOrLink(), nodeData.get_nodeOrLink()) && !interGraphLinksRoutingModel
						.ToEndsInside(linkData.get_nodeOrLink(),
								nodeData.get_nodeOrLink()))) {
			LinkShape linkShape = linkData.GetLinkShape();
			if (linkShape == null) {
				throw (new system.Exception("no link shape on " + linkData));
			}
			InternalRect boundingBox = nodeData.boundingBox;
			float delta = linkData.GetLinkWidth() * 0.5f;
			this._tempRect1.SetRect(linkShape.BoundingBox());
			if (delta > 0f) {
				this._tempRect1.Expand(delta);
			}

			if (IsOverlap(this._tempRect1, boundingBox)) {
				Integer num2 = linkShape.GetNumberOfPoints() - 2;
				for (Integer i = 0; i <= num2; i++) {

					if (this.Intersects(linkShape.GetPointAt(i),
							linkShape.GetPointAt(i + 1), delta, boundingBox,
							i == 0, i == num2)) {

						return 1f;
					}
				}
			}
		}

		return 0f;

	}

	private Boolean Intersects(InternalPoint from, InternalPoint to,
			float halfWidth, InternalRect rect, Boolean firstPoint,
			Boolean lastPoint) {
		if ((from.X != to.X) && (from.Y != to.Y)) {
			float num = rect.X + rect.Width;
			float num2 = rect.Y + rect.Height;
			if ((((from.X < rect.X) && (to.X < rect.X)) || ((from.X > num) && (to.X > num)))
					|| (((from.Y < rect.Y) && (to.Y < rect.Y)) || ((from.Y > num2) && (to.Y > num2)))) {

				return false;
			}

			if (!LayoutUtil.Intersects(from.X, from.Y, to.X, to.Y, rect.X,
					rect.Y, num, rect.Y)) {

				if (LayoutUtil.Intersects(from.X, from.Y, to.X, to.Y, num,
						rect.Y, num, num2)) {

					return true;
				}

				if (LayoutUtil.Intersects(from.X, from.Y, to.X, to.Y, num,
						num2, rect.X, num2)) {

					return true;
				}

				if (!LayoutUtil.Intersects(from.X, from.Y, to.X, to.Y, rect.X,
						num2, rect.X, rect.Y)) {

					return false;
				}
			}

			return true;
		}
		float width = to.X - from.X;
		float height = to.Y - from.Y;
		if ((width == 0f) && (height == 0f)) {

			return false;
		}
		if (width < 0f) {
			width = -width;
		}
		if (height < 0f) {
			height = -height;
		}
		if (halfWidth > 0f) {
			if (firstPoint) {
				if (width == 0f) {
					if (from.Y <= to.Y) {
						this._tempRect1.Reshape(from.X - halfWidth, from.Y,
								2f * halfWidth, height + halfWidth);
					} else {
						this._tempRect1
								.Reshape(from.X - halfWidth, to.Y - halfWidth,
										2f * halfWidth, height + halfWidth);
					}
				} else if (from.X <= to.X) {
					this._tempRect1.Reshape(from.X, from.Y - halfWidth, width
							+ halfWidth, 2f * halfWidth);
				} else {
					this._tempRect1.Reshape(to.X - halfWidth, to.Y - halfWidth,
							width + halfWidth, 2f * halfWidth);
				}
			} else if (lastPoint) {
				if (width == 0f) {
					if (from.Y <= to.Y) {
						this._tempRect1
								.Reshape(from.X - halfWidth,
										from.Y + halfWidth, 2f * halfWidth,
										height - halfWidth);
					} else {
						this._tempRect1.Reshape(from.X - halfWidth, to.Y,
								2f * halfWidth, height - halfWidth);
					}
				} else if (from.X <= to.X) {
					this._tempRect1.Reshape(from.X + halfWidth, from.Y
							- halfWidth, width - halfWidth, 2f * halfWidth);
				} else {
					this._tempRect1.Reshape(to.X, to.Y - halfWidth, width
							- halfWidth, 2f * halfWidth);
				}
			} else if (width == 0f) {
				if (from.Y <= to.Y) {
					this._tempRect1.Reshape(from.X - halfWidth, from.Y
							+ halfWidth, 2f * halfWidth, height);
				} else {
					this._tempRect1.Reshape(from.X - halfWidth, to.Y
							- halfWidth, 2f * halfWidth, height);
				}
			} else if (from.X <= to.X) {
				this._tempRect1.Reshape(from.X + halfWidth, from.Y - halfWidth,
						width, 2f * halfWidth);
			} else {
				this._tempRect1.Reshape(to.X - halfWidth, to.Y - halfWidth,
						width, 2f * halfWidth);
			}
		} else {
			this._tempRect1.Reshape((from.X <= to.X) ? from.X : to.X,
					(from.Y <= to.Y) ? from.Y : to.Y, width, height);
		}

		return IsOverlap(this._tempRect1, rect);

	}

	private static Boolean IsOverlap(InternalRect rect1, InternalRect rect2) {

		return ((((rect1.X < (rect2.X + rect2.Width)) && (rect2.X < (rect1.X + rect1.Width))) && (rect1.Y < (rect2.Y + rect2.Height))) && (rect2.Y < (rect1.Y + rect1.Height)));

	}

	public void Update(ShortLinkAlgorithm layout) {
		ShortLinkLayout shortLinkLayout = layout.GetShortLinkLayout();
		this._linkLinkPenalty = shortLinkLayout.get_LinkToLinkCrossingPenalty();
		this._linkNodePenalty = shortLinkLayout.get_LinkToNodeCrossingPenalty();

	}

}
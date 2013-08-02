package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;

public abstract class LinkShapeType {

	public void ComputeIntermediatePoints(ShortLinkAlgorithm layout,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			float minFinalSegment, Boolean reversed) {
		minFinalSegment += linkData.GetLinkWidth() * 0.5f;
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData data = reversed ? linkData
				.GetToNode() : linkData.GetFromNode();
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData data2 = reversed ? linkData
				.GetFromNode() : linkData.GetToNode();
		SLNodeSide fromNodeSide = linkData.GetFromNodeSide();
		SLNodeSide toNodeSide = linkData.GetToNodeSide();
		InternalRect rect = layout.GetTempRect1();
		InternalRect rect2 = layout.GetTempRect2();
		InternalRect linkConnectionRect = data.GetLinkConnectionRect();
		rect.Reshape(linkConnectionRect.X, linkConnectionRect.Y,
				linkConnectionRect.Width, linkConnectionRect.Height);

		linkConnectionRect = data2.GetLinkConnectionRect();
		rect2.Reshape(linkConnectionRect.X, linkConnectionRect.Y,
				linkConnectionRect.Width, linkConnectionRect.Height);
		InternalPoint connectionPoint = linkData.GetConnectionPoint(!reversed);
		InternalPoint point = linkData.GetConnectionPoint(reversed);

		if (!linkData.IsFromPointFixed()) {
			fromNodeSide.MoveToDefaultConnectionPoint(connectionPoint, rect);
		}

		if (!linkData.IsToPointFixed()) {
			toNodeSide.MoveToDefaultConnectionPoint(point, rect2);
		}

		if (linkData.IsFromPointFixed()) {
			rect.Add(connectionPoint);
		}

		if (linkData.IsToPointFixed()) {
			rect2.Add(point);
		}
		this.ComputeIntermediatePoints(linkData, rect, rect2, fromNodeSide,
				toNodeSide, minFinalSegment);
		fromNodeSide.SetLayoutUpToDate(false);
		toNodeSide.SetLayoutUpToDate(false);

	}

	private void ComputeIntermediatePoints(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, SLNodeSide fromSide,
			SLNodeSide toSide, float minFinalSegment) {
		InternalPoint connectionPoint = linkData.GetConnectionPoint(true);
		InternalPoint toPoint = linkData.GetConnectionPoint(false);
		this.ComputeIntermediatePoints(linkData, fromRect, toRect, fromSide,
				toSide, connectionPoint, toPoint, minFinalSegment);
		linkData.GetLinkShape().SetNumberOfPoints(this.GetNumberOfBends() + 2);

	}

	public abstract void ComputeIntermediatePoints(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, SLNodeSide fromSide,
			SLNodeSide toSide, InternalPoint fromPoint, InternalPoint toPoint,
			float minFinalSegment);

	public abstract SLNodeSide GetFromNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData);

	public Integer GetFromSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return this.GetFromNodeSide(linkData).GetSide();

	}

	public abstract Integer GetNumberOfBends();

	public InternalPoint GetOrCreatePoint(InternalPoint[] bends, Integer index) {
		InternalPoint point = bends[index];
		if (point == null) {
			point = new InternalPoint(0f, 0f);
			bends[index] = point;
		}

		return point;

	}

	public abstract LinkShapeType GetReversedShapeType();

	public abstract SLNodeSide GetToNodeSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData);

	public Integer GetToSide(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return this.GetToNodeSide(linkData).GetSide();

	}

	public static float GetXMiddle(InternalPoint fromPoint,
			InternalPoint toPoint, InternalRect fromRect, InternalRect toRect) {
		if (fromRect.X > (toRect.X + toRect.Width)) {

			return (((fromRect.X + toRect.X) + toRect.Width) * 0.5f);
		}
		if (toRect.X > (fromRect.X + fromRect.Width)) {

			return (((toRect.X + fromRect.X) + fromRect.Width) * 0.5f);
		}

		return ((fromPoint.X + toPoint.X) * 0.5f);

	}

	public static float GetYMiddle(InternalPoint fromPoint,
			InternalPoint toPoint, InternalRect fromRect, InternalRect toRect) {
		if (fromRect.Y > (toRect.Y + toRect.Height)) {

			return (((fromRect.Y + toRect.Y) + toRect.Height) * 0.5f);
		}
		if (toRect.Y > (fromRect.Y + fromRect.Height)) {

			return (((toRect.Y + fromRect.Y) + fromRect.Height) * 0.5f);
		}

		return ((fromPoint.Y + toPoint.Y) * 0.5f);

	}

	public abstract Boolean IsOrthogonal();

}
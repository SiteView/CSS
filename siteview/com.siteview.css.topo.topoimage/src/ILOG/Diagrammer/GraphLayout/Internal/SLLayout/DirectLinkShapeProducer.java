package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.QuickSort;

public final class DirectLinkShapeProducer extends LinkShapeProducer {
	private LinkOrderSort _linkOrderSort;

	private static LinkShapeType[] ALL_QUASI_SELF_INTERGRAPH_LINK_TYPES = new LinkShapeType[] {
			IlvDirectLL.instance, IlvDirectRR.instance, IlvDirectTT.instance,
			IlvDirectBB.instance, IlvDirectLT.instance, IlvDirectLB.instance,
			IlvDirectRB.instance, IlvDirectRT.instance, IlvDirectTL.instance,
			IlvDirectTR.instance, IlvDirectBR.instance, IlvDirectBL.instance,
			IlvDirectBT.instance, IlvDirectTB.instance, IlvDirectRL.instance,
			IlvDirectLR.instance };

	private static LinkShapeType[] BOTTOM_BOTTOM_SIDE_ARRAY = new LinkShapeType[] { IlvDirectBB.instance };

	private static LinkShapeType[] BOTTOM_LEFT_SIDE_ARRAY = new LinkShapeType[] { IlvDirectBL.instance };

	private static LinkShapeType[] BOTTOM_LEFT_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendBL_OVERLAP.instance };

	private static LinkShapeType[] BOTTOM_LEFT_TO_TOP_RIGHT_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectTL.instance, IlvDirectRB.instance, IlvDirectTB.instance,
			IlvDirectRL.instance };

	private static LinkShapeType[] BOTTOM_RIGHT_SIDE_ARRAY = new LinkShapeType[] { IlvDirectBR.instance };

	private static LinkShapeType[] BOTTOM_RIGHT_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendBR_OVERLAP.instance };

	private static LinkShapeType[] BOTTOM_RIGHT_TO_TOP_LEFT_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectLR.instance, IlvDirectTR.instance, IlvDirectTB.instance,
			IlvDirectLB.instance };

	private static LinkShapeType[] BOTTOM_TO_TOP_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectTB.instance, IlvDirectLL.instance, IlvDirectRR.instance };

	private static LinkShapeType[] BOTTOM_TOP_SIDE_ARRAY = new LinkShapeType[] { IlvDirectBT.instance };

	private static LinkShapeType[] CLOSE_BOTTOM_TO_TOP_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectLL.instance, IlvDirectRR.instance };

	private static LinkShapeType[] CLOSE_LEFT_TO_RIGHT_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectTT.instance, IlvDirectBB.instance };

	private static LinkShapeType[] CLOSE_RIGHT_TO_LEFT_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectTT.instance, IlvDirectBB.instance };

	private static LinkShapeType[] CLOSE_TOP_TO_BOTTOM_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectLL.instance, IlvDirectRR.instance };

	private static LinkShapeType[] LEFT_BOTTOM_SIDE_ARRAY = new LinkShapeType[] { IlvDirectLB.instance };

	private static LinkShapeType[] LEFT_BOTTOM_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendLB_OVERLAP.instance };

	private static LinkShapeType[] LEFT_LEFT_SIDE_ARRAY = new LinkShapeType[] { IlvDirectLL.instance };

	private static LinkShapeType[] LEFT_RIGHT_SIDE_ARRAY = new LinkShapeType[] { IlvDirectLR.instance };

	private static LinkShapeType[] LEFT_TO_RIGHT_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectRL.instance, IlvDirectTT.instance, IlvDirectBB.instance };

	private static LinkShapeType[] LEFT_TOP_SIDE_ARRAY = new LinkShapeType[] { IlvDirectLT.instance };

	private static LinkShapeType[] LEFT_TOP_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendLT_OVERLAP.instance };

	private static LinkShapeType[] RIGHT_BOTTOM_SIDE_ARRAY = new LinkShapeType[] { IlvDirectRB.instance };

	private static LinkShapeType[] RIGHT_BOTTOM_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendRB_OVERLAP.instance };

	private static LinkShapeType[] RIGHT_LEFT_SIDE_ARRAY = new LinkShapeType[] { IlvDirectRL.instance };

	private static LinkShapeType[] RIGHT_RIGHT_SIDE_ARRAY = new LinkShapeType[] { IlvDirectRR.instance };

	private static LinkShapeType[] RIGHT_TO_LEFT_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectLR.instance, IlvDirectTT.instance, IlvDirectBB.instance };

	private static LinkShapeType[] RIGHT_TOP_SIDE_ARRAY = new LinkShapeType[] { IlvDirectRT.instance };

	private static LinkShapeType[] RIGHT_TOP_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendRT_OVERLAP.instance };

	private static LinkShapeType[] TOP_BOTTOM_SIDE_ARRAY = new LinkShapeType[] { IlvDirectTB.instance };

	private static LinkShapeType[] TOP_LEFT_SIDE_ARRAY = new LinkShapeType[] { IlvDirectTL.instance };

	private static LinkShapeType[] TOP_LEFT_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendTL_OVERLAP.instance };

	private static LinkShapeType[] TOP_LEFT_TO_BOTTOM_RIGHT_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectRL.instance, IlvDirectRT.instance, IlvDirectBT.instance,
			IlvDirectBL.instance };

	private static LinkShapeType[] TOP_RIGHT_SIDE_ARRAY = new LinkShapeType[] { IlvDirectTR.instance };

	private static LinkShapeType[] TOP_RIGHT_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendTR_OVERLAP.instance };

	private static LinkShapeType[] TOP_RIGHT_TO_BOTTOM_LEFT_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectLT.instance, IlvDirectBR.instance, IlvDirectBT.instance,
			IlvDirectLR.instance };

	private static LinkShapeType[] TOP_TO_BOTTOM_POSITION_ARRAY = new LinkShapeType[] {
			IlvDirectBT.instance, IlvDirectLL.instance, IlvDirectRR.instance };

	private static LinkShapeType[] TOP_TOP_SIDE_ARRAY = new LinkShapeType[] { IlvDirectTT.instance };

	public DirectLinkShapeProducer(ShortLinkAlgorithm layout) {
		super(layout);
		this._linkOrderSort = new LinkOrderSort();
	}

	@Override
	public LinkShapeType[] GetAllQuasiSelfInterGraphLinkShapeTypes() {

		return ALL_QUASI_SELF_INTERGRAPH_LINK_TYPES;

	}

	@Override
	public LinkShapeType[] GetBottomToBottomSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {

		return BOTTOM_BOTTOM_SIDE_ARRAY;

	}

	@Override
	public LinkShapeType[] GetBottomToLeftSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {
		if (!isOverlapping) {

			return BOTTOM_LEFT_SIDE_ARRAY;
		}

		return BOTTOM_LEFT_SIDE_OVERLAP_ARRAY;

	}

	@Override
	public LinkShapeType[] GetBottomToRightSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {
		if (!isOverlapping) {

			return BOTTOM_RIGHT_SIDE_ARRAY;
		}

		return BOTTOM_RIGHT_SIDE_OVERLAP_ARRAY;

	}

	@Override
	public LinkShapeType[] GetBottomToTopSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {

		return BOTTOM_TOP_SIDE_ARRAY;

	}

	@Override
	public float GetBypassDistance(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			float globalBypassDistance) {

		return globalBypassDistance;

	}

	@Override
	public LinkShapeType[] GetFromBottomLeftToTopRightPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return BOTTOM_LEFT_TO_TOP_RIGHT_POSITION_ARRAY;

	}

	@Override
	public LinkShapeType[] GetFromBottomRightToTopLeftPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return BOTTOM_RIGHT_TO_TOP_LEFT_POSITION_ARRAY;

	}

	@Override
	public LinkShapeType[] GetFromBottomToTopPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {
		if (((toRect.Y + toRect.Height) + (2f * bypassDistance)) >= fromRect.Y) {

			return CLOSE_BOTTOM_TO_TOP_POSITION_ARRAY;
		}

		return BOTTOM_TO_TOP_POSITION_ARRAY;

	}

	@Override
	public LinkShapeType[] GetFromLeftToRightPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {
		if ((toRect.X - (2f * bypassDistance)) <= (fromRect.X + fromRect.Width)) {

			return CLOSE_LEFT_TO_RIGHT_POSITION_ARRAY;
		}

		return LEFT_TO_RIGHT_POSITION_ARRAY;

	}

	@Override
	public LinkShapeType[] GetFromRightToLeftPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {
		if ((fromRect.X - (2f * bypassDistance)) <= (toRect.X + toRect.Width)) {

			return CLOSE_RIGHT_TO_LEFT_POSITION_ARRAY;
		}

		return RIGHT_TO_LEFT_POSITION_ARRAY;

	}

	@Override
	public LinkShapeType[] GetFromTopLeftToBottomRightPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return TOP_LEFT_TO_BOTTOM_RIGHT_POSITION_ARRAY;

	}

	@Override
	public LinkShapeType[] GetFromTopRightToBottomLeftPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return TOP_RIGHT_TO_BOTTOM_LEFT_POSITION_ARRAY;

	}

	@Override
	public LinkShapeType[] GetFromTopToBottomPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {
		if ((toRect.Y - (2f * bypassDistance)) <= (fromRect.Y + fromRect.Height)) {

			return CLOSE_TOP_TO_BOTTOM_POSITION_ARRAY;
		}

		return TOP_TO_BOTTOM_POSITION_ARRAY;

	}

	@Override
	public LinkShapeType[] GetLeftToBottomSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {
		if (!isOverlapping) {

			return LEFT_BOTTOM_SIDE_ARRAY;
		}

		return LEFT_BOTTOM_SIDE_OVERLAP_ARRAY;

	}

	@Override
	public LinkShapeType[] GetLeftToLeftSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {

		return LEFT_LEFT_SIDE_ARRAY;

	}

	@Override
	public LinkShapeType[] GetLeftToRightSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {

		return LEFT_RIGHT_SIDE_ARRAY;

	}

	@Override
	public LinkShapeType[] GetLeftToTopSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {
		if (!isOverlapping) {

			return LEFT_TOP_SIDE_ARRAY;
		}

		return LEFT_TOP_SIDE_OVERLAP_ARRAY;

	}

	@Override
	public Integer GetMaxNumberOfBends() {

		return 3;

	}

	@Override
	public LinkShapeType[] GetRightToBottomSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {
		if (!isOverlapping) {

			return RIGHT_BOTTOM_SIDE_ARRAY;
		}

		return RIGHT_BOTTOM_SIDE_OVERLAP_ARRAY;

	}

	@Override
	public LinkShapeType[] GetRightToLeftSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {

		return RIGHT_LEFT_SIDE_ARRAY;

	}

	@Override
	public LinkShapeType[] GetRightToRightSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {

		return RIGHT_RIGHT_SIDE_ARRAY;

	}

	@Override
	public LinkShapeType[] GetRightToTopSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {
		if (!isOverlapping) {

			return RIGHT_TOP_SIDE_ARRAY;
		}

		return RIGHT_TOP_SIDE_OVERLAP_ARRAY;

	}

	@Override
	public LinkShapeType[] GetTopToBottomSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {

		return TOP_BOTTOM_SIDE_ARRAY;

	}

	@Override
	public LinkShapeType[] GetTopToLeftSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {
		if (!isOverlapping) {

			return TOP_LEFT_SIDE_ARRAY;
		}

		return TOP_LEFT_SIDE_OVERLAP_ARRAY;

	}

	@Override
	public LinkShapeType[] GetTopToRightSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {
		if (!isOverlapping) {

			return TOP_RIGHT_SIDE_ARRAY;
		}

		return TOP_RIGHT_SIDE_OVERLAP_ARRAY;

	}

	@Override
	public LinkShapeType[] GetTopToTopSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {

		return TOP_TOP_SIDE_ARRAY;

	}

	@Override
	public LinkShapeType[] SortShapeTypes(LinkShapeType[] shapeTypes,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			float minFinalSegmentLength, Boolean copyBeforeSort) {
		LinkShapeType[] typeArray = copyBeforeSort ? super.CopyShapeTypes(
				shapeTypes, shapeTypes.length) : shapeTypes;
		this._linkOrderSort.Sort(typeArray, linkData, minFinalSegmentLength);

		return typeArray;

	}

	public class LinkOrderSort extends QuickSort {
		private double[] _lengthArray;

		private InternalPoint _p1 = new InternalPoint(0f, 0f);

		private InternalPoint _p2 = new InternalPoint(0f, 0f);

		private LinkShapeType[] _shapeTypes;

		@Override
		public Integer Compare(Integer loc1, Integer loc2) {
			if (this._lengthArray[loc1] == this._lengthArray[loc2]) {

				return 0;
			}
			if (this._lengthArray[loc1] < this._lengthArray[loc2]) {

				return -1;
			}

			return 1;

		}

		public void Sort(
				LinkShapeType[] shapeTypes,
				ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
				float minFinalSegmentLength) {
			if (shapeTypes != null) {
				Integer length = shapeTypes.length;
				if (length >= 2) {
					this._shapeTypes = shapeTypes;
					if ((this._lengthArray == null)
							|| (this._lengthArray.length < length)) {
						this._lengthArray = new double[length];
					}
					InternalRect linkConnectionRect = linkData.GetFromNode()
							.GetLinkConnectionRect();
					InternalRect rect = linkData.GetToNode()
							.GetLinkConnectionRect();
					for (Integer i = 0; i < length; i++) {
						LinkShapeType type = shapeTypes[i];
						SLNodeSide fromNodeSide = type
								.GetFromNodeSide(linkData);
						SLNodeSide toNodeSide = type.GetToNodeSide(linkData);
						fromNodeSide.MoveToDefaultConnectionPoint(this._p1,
								linkConnectionRect);
						fromNodeSide.TranslateOutside(this._p1,
								minFinalSegmentLength);
						toNodeSide.MoveToDefaultConnectionPoint(this._p2, rect);
						toNodeSide.TranslateOutside(this._p2,
								minFinalSegmentLength);
						this._lengthArray[i] = ((this._p2.X - this._p1.X) * (this._p2.X - this._p1.X))
								+ ((this._p2.Y - this._p1.Y) * (this._p2.Y - this._p1.Y));
					}
					super.Sort(length);
				}
			}

		}

		@Override
		public void Swap(Integer loc1, Integer loc2) {
			LinkShapeType type = this._shapeTypes[loc1];
			this._shapeTypes[loc1] = this._shapeTypes[loc2];
			this._shapeTypes[loc2] = type;
			double num = this._lengthArray[loc1];
			this._lengthArray[loc1] = this._lengthArray[loc2];
			this._lengthArray[loc2] = num;

		}

	}
}
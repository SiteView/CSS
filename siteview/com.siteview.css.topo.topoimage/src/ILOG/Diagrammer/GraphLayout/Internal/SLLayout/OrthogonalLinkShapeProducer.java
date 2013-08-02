package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class OrthogonalLinkShapeProducer extends LinkShapeProducer {
	private static LinkShapeType[] ALL_QUASI_SELF_INTERGRAPH_LINK_TYPES = new LinkShapeType[] {
			IlvOneBendTL.instance, IlvOneBendTR.instance,
			IlvOneBendBL.instance, IlvOneBendBR.instance,
			IlvOneBendLB.instance, IlvOneBendLT.instance,
			IlvOneBendRB.instance, IlvOneBendRT.instance,
			IlvTwoBendLL.instance, IlvTwoBendRR.instance,
			IlvTwoBendTT.instance, IlvTwoBendBB.instance,
			IlvTwoBendBT.instance, IlvTwoBendTB.instance,
			IlvTwoBendRL.instance, IlvTwoBendLR.instance,
			IlvThreeBendLT.instance, IlvThreeBendLB.instance,
			IlvThreeBendRB.instance, IlvThreeBendRT.instance,
			IlvThreeBendTL.instance, IlvThreeBendTR.instance,
			IlvThreeBendBR.instance, IlvThreeBendBL.instance };

	private static LinkShapeType[] BOTTOM_BOTTOM_SIDE_ARRAY = new LinkShapeType[] {
			IlvTwoBendBB.instance, IlvFourBendBBleft.instance,
			IlvFourBendBBright.instance };

	private static LinkShapeType[] BOTTOM_LEFT_SIDE_ARRAY = new LinkShapeType[] {
			IlvOneBendBL.instance, IlvThreeBendBL.instance };

	private static LinkShapeType[] BOTTOM_LEFT_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendBL_OVERLAP.instance };

	private static LinkShapeType[] BOTTOM_RIGHT_SIDE_ARRAY = new LinkShapeType[] {
			IlvOneBendBR.instance, IlvThreeBendBR.instance };

	private static LinkShapeType[] BOTTOM_RIGHT_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendBR_OVERLAP.instance };

	private static LinkShapeType[] BOTTOM_TOP_SIDE_2and4BENDS_ARRAY = new LinkShapeType[] {
			IlvTwoBendBT.instance, IlvFourBendBTright.instance,
			IlvFourBendBTleft.instance };

	private static LinkShapeType[] BOTTOM_TOP_SIDE_4BENDS_ARRAY = new LinkShapeType[] {
			IlvFourBendBTright.instance, IlvFourBendBTleft.instance };

	private static LinkShapeType[] LEFT_BOTTOM_SIDE_ARRAY = new LinkShapeType[] {
			IlvOneBendLB.instance, IlvThreeBendLB.instance };

	private static LinkShapeType[] LEFT_BOTTOM_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendLB_OVERLAP.instance };

	private static LinkShapeType[] LEFT_LEFT_SIDE_ARRAY = new LinkShapeType[] {
			IlvTwoBendLL.instance, IlvFourBendLLleft.instance,
			IlvFourBendLLright.instance };

	private static LinkShapeType[] LEFT_RIGHT_SIDE_2and4BENDS_ARRAY = new LinkShapeType[] {
			IlvTwoBendLR.instance, IlvFourBendLRmiddle.instance,
			IlvFourBendLRtop.instance, IlvFourBendLRbottom.instance };

	private static LinkShapeType[] LEFT_RIGHT_SIDE_4BENDS_ARRAY = new LinkShapeType[] {
			IlvFourBendLRmiddle.instance, IlvFourBendLRtop.instance,
			IlvFourBendLRbottom.instance };

	private static LinkShapeType[] LEFT_TOP_SIDE_ARRAY = new LinkShapeType[] {
			IlvOneBendLT.instance, IlvThreeBendLT.instance };

	private static LinkShapeType[] LEFT_TOP_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendLT_OVERLAP.instance };

	public Integer MAX_NUMBER_OF_BENDS = 4;

	private static LinkShapeType[] RIGHT_BOTTOM_SIDE_ARRAY = new LinkShapeType[] {
			IlvOneBendRB.instance, IlvThreeBendRB.instance };

	private static LinkShapeType[] RIGHT_BOTTOM_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendRB_OVERLAP.instance };

	private static LinkShapeType[] RIGHT_LEFT_SIDE_2and4BENDS_ARRAY = new LinkShapeType[] {
			IlvTwoBendRL.instance, IlvFourBendRLmiddle.instance,
			IlvFourBendRLtop.instance, IlvFourBendRLbottom.instance };

	private static LinkShapeType[] RIGHT_LEFT_SIDE_4BENDS_ARRAY = new LinkShapeType[] {
			IlvFourBendRLmiddle.instance, IlvFourBendRLtop.instance,
			IlvFourBendRLbottom.instance };

	private static LinkShapeType[] RIGHT_RIGHT_SIDE_ARRAY = new LinkShapeType[] {
			IlvTwoBendRR.instance, IlvFourBendRRleft.instance,
			IlvFourBendRRright.instance };

	private static LinkShapeType[] RIGHT_TOP_SIDE_ARRAY = new LinkShapeType[] {
			IlvOneBendRT.instance, IlvThreeBendRT.instance };

	private static LinkShapeType[] RIGHT_TOP_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendRT_OVERLAP.instance };

	private static LinkShapeType[] TOP_BOTTOM_SIDE_2and4BENDS_ARRAY = new LinkShapeType[] {
			IlvTwoBendTB.instance, IlvFourBendTBright.instance,
			IlvFourBendTBleft.instance };

	private static LinkShapeType[] TOP_BOTTOM_SIDE_4BENDS_ARRAY = new LinkShapeType[] {
			IlvFourBendTBright.instance, IlvFourBendTBleft.instance };

	private static LinkShapeType[] TOP_LEFT_SIDE_ARRAY = new LinkShapeType[] {
			IlvOneBendTL.instance, IlvThreeBendTL.instance };

	private static LinkShapeType[] TOP_LEFT_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendTL_OVERLAP.instance };

	private static LinkShapeType[] TOP_RIGHT_SIDE_ARRAY = new LinkShapeType[] {
			IlvOneBendTR.instance, IlvThreeBendTR.instance };

	private static LinkShapeType[] TOP_RIGHT_SIDE_OVERLAP_ARRAY = new LinkShapeType[] { IlvThreeBendTR_OVERLAP.instance };

	private static LinkShapeType[] TOP_TOP_SIDE_ARRAY = new LinkShapeType[] {
			IlvTwoBendTT.instance, IlvFourBendTTleft.instance,
			IlvFourBendTTright.instance };

	public OrthogonalLinkShapeProducer(ShortLinkAlgorithm layout) {
		super(layout);
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
		if (((toRect.Y - fromRect.Y) - fromRect.Height) <= (2f * bypassDistance)) {

			return BOTTOM_TOP_SIDE_4BENDS_ARRAY;
		}

		return BOTTOM_TOP_SIDE_2and4BENDS_ARRAY;

	}

	@Override
	public LinkShapeType[] GetFromBottomLeftToTopRightPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return this
				.GetSelectedShapeTypes(
						(toRect.X - (2f * bypassDistance)) > (fromRect.X + fromRect.Width),
						(fromRect.Y - (2f * bypassDistance)) > (toRect.Y + toRect.Height),
						this.GetPositionArray(
								linkData,
								OrthoWith4BendsPositionArrays.FROM_BL_TO_TR_POSITION_ARRAY,
								OrthoWithout4BendsPositionArrays.FROM_BL_TO_TR_POSITION_ARRAY));

	}

	@Override
	public LinkShapeType[] GetFromBottomRightToTopLeftPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return this
				.GetSelectedShapeTypes(
						(fromRect.X - (2f * bypassDistance)) > (toRect.X + toRect.Width),
						(fromRect.Y - (2f * bypassDistance)) > (toRect.Y + toRect.Height),
						this.GetPositionArray(
								linkData,
								OrthoWith4BendsPositionArrays.FROM_BR_TO_TL_POSITION_ARRAY,
								OrthoWithout4BendsPositionArrays.FROM_BR_TO_TL_POSITION_ARRAY));

	}

	@Override
	public LinkShapeType[] GetFromBottomToTopPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return this
				.GetSelectedShapeTypes(
						((toRect.Y + toRect.Height) + (2f * bypassDistance)) < fromRect.Y,
						this.GetPositionArray(
								linkData,
								OrthoWith4BendsPositionArrays.FROM_B_TO_T_POSITION_ARRAY,
								OrthoWithout4BendsPositionArrays.FROM_B_TO_T_POSITION_ARRAY));

	}

	@Override
	public LinkShapeType[] GetFromLeftToRightPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return this
				.GetSelectedShapeTypes(
						(toRect.X - (2f * bypassDistance)) > (fromRect.X + fromRect.Width),
						this.GetPositionArray(
								linkData,
								OrthoWith4BendsPositionArrays.FROM_L_TO_R_POSITION_ARRAY,
								OrthoWithout4BendsPositionArrays.FROM_L_TO_R_POSITION_ARRAY));

	}

	@Override
	public LinkShapeType[] GetFromRightToLeftPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return this
				.GetSelectedShapeTypes(
						((toRect.X + toRect.Width) + (2f * bypassDistance)) < fromRect.X,
						this.GetPositionArray(
								linkData,
								OrthoWith4BendsPositionArrays.FROM_R_TO_L_POSITION_ARRAY,
								OrthoWithout4BendsPositionArrays.FROM_R_TO_L_POSITION_ARRAY));

	}

	@Override
	public LinkShapeType[] GetFromTopLeftToBottomRightPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return this
				.GetSelectedShapeTypes(
						(toRect.X - (2f * bypassDistance)) > (fromRect.X + fromRect.Width),
						(toRect.Y - (2f * bypassDistance)) > (fromRect.Y + fromRect.Height),
						this.GetPositionArray(
								linkData,
								OrthoWith4BendsPositionArrays.FROM_TL_TO_BR_POSITION_ARRAY,
								OrthoWithout4BendsPositionArrays.FROM_TL_TO_BR_POSITION_ARRAY));

	}

	@Override
	public LinkShapeType[] GetFromTopRightToBottomLeftPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return this
				.GetSelectedShapeTypes(
						(fromRect.X - (2f * bypassDistance)) > (toRect.X + toRect.Width),
						(toRect.Y - (2f * bypassDistance)) > (fromRect.Y + fromRect.Height),
						this.GetPositionArray(
								linkData,
								OrthoWith4BendsPositionArrays.FROM_TR_TO_BL_POSITION_ARRAY,
								OrthoWithout4BendsPositionArrays.FROM_TR_TO_BL_POSITION_ARRAY));

	}

	@Override
	public LinkShapeType[] GetFromTopToBottomPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {

		return this
				.GetSelectedShapeTypes(
						(toRect.Y - (2f * bypassDistance)) > (fromRect.Y + fromRect.Height),
						this.GetPositionArray(
								linkData,
								OrthoWith4BendsPositionArrays.FROM_T_TO_B_POSITION_ARRAY,
								OrthoWithout4BendsPositionArrays.FROM_T_TO_B_POSITION_ARRAY));

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
		if (((toRect.X - fromRect.X) - fromRect.Width) <= (2f * bypassDistance)) {

			return LEFT_RIGHT_SIDE_4BENDS_ARRAY;
		}

		return LEFT_RIGHT_SIDE_2and4BENDS_ARRAY;

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

		return 4;

	}

	private LinkShapeType[][] GetPositionArray(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			LinkShapeType[][] arrayWith4Bend,
			LinkShapeType[][] arrayWithout4Bend) {

		if ((super._nodeSideFilter == null)
				&& !linkData.HasFixedConnectionPoint()) {

			return arrayWithout4Bend;
		}

		return arrayWith4Bend;

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
		if (((toRect.X - fromRect.X) - fromRect.Width) <= (2f * bypassDistance)) {

			return RIGHT_LEFT_SIDE_4BENDS_ARRAY;
		}

		return RIGHT_LEFT_SIDE_2and4BENDS_ARRAY;

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

	private LinkShapeType[] GetSelectedShapeTypes(Boolean condition,
			LinkShapeType[][] shapeTypesArray) {
		if (!condition) {

			return shapeTypesArray[1];
		}

		return shapeTypesArray[0];

	}

	private LinkShapeType[] GetSelectedShapeTypes(Boolean horizontalCondition,
			Boolean verticalCondition, LinkShapeType[][] shapeTypesArray) {
		if (horizontalCondition) {
			if (!verticalCondition) {

				return shapeTypesArray[1];
			}

			return shapeTypesArray[3];
		}
		if (verticalCondition) {

			return shapeTypesArray[2];
		}

		return shapeTypesArray[0];

	}

	@Override
	public LinkShapeType[] GetTopToBottomSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance) {
		if (((fromRect.Y - toRect.Y) - toRect.Height) <= (2f * bypassDistance)) {

			return TOP_BOTTOM_SIDE_4BENDS_ARRAY;
		}

		return TOP_BOTTOM_SIDE_2and4BENDS_ARRAY;

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
	public Boolean IsEnoughSpace(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			LinkShapeType shapeType, InternalRect subgraphBox,
			InternalRect nodeBox, float bypassDistance,
			Boolean enoughSpaceOnLeft, Boolean enoughSpaceOnRight,
			Boolean enoughSpaceOnTop, Boolean enoughSpaceOnBottom) {
		if (shapeType.IsOrthogonal() && (shapeType.GetNumberOfBends() == 1)) {
			Integer num = linkData.IsQuasiSelfInterGraphLinkOrigin() ? shapeType
					.GetToSide(linkData) : shapeType.GetFromSide(linkData);
			if (num == 0) {
				enoughSpaceOnLeft = nodeBox.X >= ((subgraphBox.X + (subgraphBox.Width * 0.5f)) + bypassDistance);
				return super.IsEnoughSpace(linkData, shapeType, subgraphBox,
						nodeBox, bypassDistance, enoughSpaceOnLeft,
						enoughSpaceOnRight, enoughSpaceOnTop,
						enoughSpaceOnBottom);
			} else if (num == 1) {
				enoughSpaceOnRight = (nodeBox.X + nodeBox.Width) <= ((subgraphBox.X + (subgraphBox.Width * 0.5f)) - bypassDistance);
				return super.IsEnoughSpace(linkData, shapeType, subgraphBox,
						nodeBox, bypassDistance, enoughSpaceOnLeft,
						enoughSpaceOnRight, enoughSpaceOnTop,
						enoughSpaceOnBottom);
			} else if (num == 2) {
				enoughSpaceOnTop = nodeBox.Y >= ((subgraphBox.Y + (subgraphBox.Height * 0.5f)) + bypassDistance);
				return super.IsEnoughSpace(linkData, shapeType, subgraphBox,
						nodeBox, bypassDistance, enoughSpaceOnLeft,
						enoughSpaceOnRight, enoughSpaceOnTop,
						enoughSpaceOnBottom);
			} else if (num == 3) {
				enoughSpaceOnBottom = (nodeBox.Y + nodeBox.Height) <= ((subgraphBox.Y + (subgraphBox.Height * 0.5f)) - bypassDistance);
				return super.IsEnoughSpace(linkData, shapeType, subgraphBox,
						nodeBox, bypassDistance, enoughSpaceOnLeft,
						enoughSpaceOnRight, enoughSpaceOnTop,
						enoughSpaceOnBottom);
			}
			throw (new system.Exception("unsupported node side: " + num));
		}
		Label_010E:

		return super.IsEnoughSpace(linkData, shapeType, subgraphBox, nodeBox,
				bypassDistance, enoughSpaceOnLeft, enoughSpaceOnRight,
				enoughSpaceOnTop, enoughSpaceOnBottom);

	}

}
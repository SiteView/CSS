﻿package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.*;

public final class OrthoWith4BendsPositionArrays {
	public static LinkShapeType[][] FROM_B_TO_T_POSITION_ARRAY = new LinkShapeType[][] {
			new LinkShapeType[] { IlvTwoBendTB.instance, IlvTwoBendLL.instance,
					IlvTwoBendRR.instance, IlvThreeBendRT_OVERLAP.instance,
					IlvThreeBendLT_OVERLAP.instance,
					IlvThreeBendBR_OVERLAP.instance,
					IlvThreeBendBL_OVERLAP.instance, IlvThreeBendRB.instance,
					IlvThreeBendLB.instance, IlvThreeBendTR.instance,
					IlvThreeBendTL.instance, IlvFourBendRLmiddle.instance,
					IlvFourBendLRmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvTwoBendLL.instance, IlvTwoBendRR.instance,
					IlvTwoBendTB.instance, IlvThreeBendLT_OVERLAP.instance,
					IlvThreeBendRT_OVERLAP.instance,
					IlvThreeBendBR_OVERLAP.instance,
					IlvThreeBendBL_OVERLAP.instance, IlvThreeBendLB.instance,
					IlvThreeBendRB.instance, IlvThreeBendTR.instance,
					IlvThreeBendTL.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance } };

	public static LinkShapeType[][] FROM_BL_TO_TR_POSITION_ARRAY = new LinkShapeType[][] {
			new LinkShapeType[] { IlvOneBendRB.instance, IlvOneBendTL.instance,
					IlvTwoBendTT.instance, IlvTwoBendBB.instance,
					IlvTwoBendRR.instance, IlvTwoBendLL.instance,
					IlvTwoBendRL.instance, IlvTwoBendTB.instance,
					IlvThreeBendLT.instance, IlvThreeBendLB.instance,
					IlvThreeBendBR.instance, IlvThreeBendTR.instance,
					IlvThreeBendRT.instance, IlvThreeBendBL.instance,
					IlvThreeBendRB.instance, IlvThreeBendTL.instance,
					IlvFourBendBTright.instance, IlvFourBendBTleft.instance,
					IlvFourBendTBright.instance, IlvFourBendTBleft.instance,
					IlvFourBendRLtop.instance, IlvFourBendRLbottom.instance,
					IlvFourBendLRtop.instance, IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendRB.instance, IlvOneBendTL.instance,
					IlvTwoBendRL.instance, IlvTwoBendTT.instance,
					IlvTwoBendBB.instance, IlvTwoBendRR.instance,
					IlvTwoBendLL.instance, IlvTwoBendTB.instance,
					IlvThreeBendRT.instance, IlvThreeBendBL.instance,
					IlvThreeBendLT.instance, IlvThreeBendLB.instance,
					IlvThreeBendBR.instance, IlvThreeBendTR.instance,
					IlvThreeBendTL.instance, IlvThreeBendRB.instance,
					IlvFourBendLRmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendRB.instance, IlvOneBendTL.instance,
					IlvTwoBendTB.instance, IlvTwoBendTT.instance,
					IlvTwoBendBB.instance, IlvTwoBendRR.instance,
					IlvTwoBendLL.instance, IlvTwoBendRL.instance,
					IlvThreeBendTR.instance, IlvThreeBendLB.instance,
					IlvThreeBendBR.instance, IlvThreeBendLT.instance,
					IlvThreeBendTL.instance, IlvThreeBendBL.instance,
					IlvThreeBendRT.instance, IlvThreeBendRB.instance,
					IlvFourBendBTmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendRB.instance, IlvOneBendTL.instance,
					IlvTwoBendRL.instance, IlvTwoBendTB.instance,
					IlvTwoBendTT.instance, IlvTwoBendBB.instance,
					IlvTwoBendRR.instance, IlvTwoBendLL.instance,
					IlvThreeBendTL.instance, IlvThreeBendBL.instance,
					IlvThreeBendRT.instance, IlvThreeBendRB.instance,
					IlvThreeBendBR.instance, IlvThreeBendTR.instance,
					IlvThreeBendLT.instance, IlvThreeBendLB.instance,
					IlvFourBendLRmiddle.instance, IlvFourBendBTmiddle.instance,
					IlvFourBendBTright.instance, IlvFourBendBTleft.instance,
					IlvFourBendTBright.instance, IlvFourBendTBleft.instance,
					IlvFourBendRLtop.instance, IlvFourBendRLbottom.instance,
					IlvFourBendLRtop.instance, IlvFourBendLRbottom.instance } };

	public static LinkShapeType[][] FROM_BR_TO_TL_POSITION_ARRAY = new LinkShapeType[][] {
			new LinkShapeType[] { IlvOneBendTR.instance, IlvOneBendLB.instance,
					IlvTwoBendTT.instance, IlvTwoBendBB.instance,
					IlvTwoBendRR.instance, IlvTwoBendLL.instance,
					IlvTwoBendLR.instance, IlvTwoBendTB.instance,
					IlvThreeBendTL.instance, IlvThreeBendBL.instance,
					IlvThreeBendRT.instance, IlvThreeBendRB.instance,
					IlvThreeBendBR.instance, IlvThreeBendTR.instance,
					IlvThreeBendLT.instance, IlvThreeBendLB.instance,
					IlvFourBendBTright.instance, IlvFourBendBTleft.instance,
					IlvFourBendTBright.instance, IlvFourBendTBleft.instance,
					IlvFourBendRLtop.instance, IlvFourBendRLbottom.instance,
					IlvFourBendLRtop.instance, IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendTR.instance, IlvOneBendLB.instance,
					IlvTwoBendLR.instance, IlvTwoBendTT.instance,
					IlvTwoBendBB.instance, IlvTwoBendRR.instance,
					IlvTwoBendLL.instance, IlvTwoBendTB.instance,
					IlvThreeBendBR.instance, IlvThreeBendTL.instance,
					IlvThreeBendBL.instance, IlvThreeBendRT.instance,
					IlvThreeBendRB.instance, IlvThreeBendTR.instance,
					IlvThreeBendLT.instance, IlvThreeBendLB.instance,
					IlvFourBendBTmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendTR.instance, IlvOneBendLB.instance,
					IlvTwoBendTB.instance, IlvTwoBendTT.instance,
					IlvTwoBendBB.instance, IlvTwoBendRR.instance,
					IlvTwoBendLL.instance, IlvTwoBendLR.instance,
					IlvThreeBendLT.instance, IlvThreeBendTL.instance,
					IlvThreeBendBL.instance, IlvThreeBendRT.instance,
					IlvThreeBendRB.instance, IlvThreeBendBR.instance,
					IlvThreeBendTR.instance, IlvThreeBendLB.instance,
					IlvFourBendRLmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendTR.instance, IlvOneBendLB.instance,
					IlvTwoBendLR.instance, IlvTwoBendTB.instance,
					IlvTwoBendTT.instance, IlvTwoBendBB.instance,
					IlvTwoBendRR.instance, IlvTwoBendLL.instance,
					IlvThreeBendBR.instance, IlvThreeBendTR.instance,
					IlvThreeBendLT.instance, IlvThreeBendLB.instance,
					IlvThreeBendTL.instance, IlvThreeBendBL.instance,
					IlvThreeBendRT.instance, IlvThreeBendRB.instance,
					IlvFourBendRLmiddle.instance, IlvFourBendBTmiddle.instance,
					IlvFourBendBTright.instance, IlvFourBendBTleft.instance,
					IlvFourBendTBright.instance, IlvFourBendTBleft.instance,
					IlvFourBendRLtop.instance, IlvFourBendRLbottom.instance,
					IlvFourBendLRtop.instance, IlvFourBendLRbottom.instance } };

	public static LinkShapeType[][] FROM_L_TO_R_POSITION_ARRAY = new LinkShapeType[][] {
			new LinkShapeType[] { IlvTwoBendRL.instance, IlvTwoBendTT.instance,
					IlvTwoBendBB.instance, IlvThreeBendTR_OVERLAP.instance,
					IlvThreeBendBR_OVERLAP.instance,
					IlvThreeBendLB_OVERLAP.instance,
					IlvThreeBendLT_OVERLAP.instance, IlvThreeBendRB.instance,
					IlvThreeBendRT.instance, IlvThreeBendTL.instance,
					IlvThreeBendBL.instance, IlvFourBendTBmiddle.instance,
					IlvFourBendBTmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvTwoBendTT.instance, IlvTwoBendBB.instance,
					IlvTwoBendRL.instance, IlvThreeBendTR_OVERLAP.instance,
					IlvThreeBendBR_OVERLAP.instance,
					IlvThreeBendLB_OVERLAP.instance,
					IlvThreeBendLT_OVERLAP.instance, IlvThreeBendRB.instance,
					IlvThreeBendRT.instance, IlvThreeBendTL.instance,
					IlvThreeBendBL.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance } };

	public static LinkShapeType[][] FROM_R_TO_L_POSITION_ARRAY = new LinkShapeType[][] {
			new LinkShapeType[] { IlvTwoBendLR.instance, IlvTwoBendBB.instance,
					IlvTwoBendTT.instance, IlvThreeBendRB_OVERLAP.instance,
					IlvThreeBendRT_OVERLAP.instance,
					IlvThreeBendBL_OVERLAP.instance,
					IlvThreeBendTL_OVERLAP.instance, IlvThreeBendBR.instance,
					IlvThreeBendTR.instance, IlvThreeBendLT.instance,
					IlvThreeBendLB.instance, IlvFourBendTBmiddle.instance,
					IlvFourBendBTmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance },
			new LinkShapeType[] { IlvTwoBendBB.instance, IlvTwoBendTT.instance,
					IlvTwoBendLR.instance, IlvThreeBendRB_OVERLAP.instance,
					IlvThreeBendRT_OVERLAP.instance,
					IlvThreeBendBL_OVERLAP.instance,
					IlvThreeBendTL_OVERLAP.instance, IlvThreeBendBR.instance,
					IlvThreeBendTR.instance, IlvThreeBendLT.instance,
					IlvThreeBendLB.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance } };

	public static LinkShapeType[][] FROM_T_TO_B_POSITION_ARRAY = new LinkShapeType[][] {
			new LinkShapeType[] { IlvTwoBendBT.instance, IlvTwoBendRR.instance,
					IlvTwoBendLL.instance, IlvThreeBendTR_OVERLAP.instance,
					IlvThreeBendTL_OVERLAP.instance,
					IlvThreeBendRB_OVERLAP.instance,
					IlvThreeBendLB_OVERLAP.instance, IlvThreeBendBR.instance,
					IlvThreeBendBL.instance, IlvThreeBendRT.instance,
					IlvThreeBendLT.instance, IlvFourBendRLmiddle.instance,
					IlvFourBendLRmiddle.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvTwoBendRR.instance, IlvTwoBendLL.instance,
					IlvTwoBendBT.instance, IlvThreeBendTR_OVERLAP.instance,
					IlvThreeBendTL_OVERLAP.instance,
					IlvThreeBendRB_OVERLAP.instance,
					IlvThreeBendLB_OVERLAP.instance, IlvThreeBendBR.instance,
					IlvThreeBendBL.instance, IlvThreeBendRT.instance,
					IlvThreeBendLT.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance } };

	public static LinkShapeType[][] FROM_TL_TO_BR_POSITION_ARRAY = new LinkShapeType[][] {
			new LinkShapeType[] { IlvOneBendBL.instance, IlvOneBendRT.instance,
					IlvTwoBendTT.instance, IlvTwoBendBB.instance,
					IlvTwoBendRR.instance, IlvTwoBendLL.instance,
					IlvTwoBendRL.instance, IlvTwoBendBT.instance,
					IlvThreeBendLT.instance, IlvThreeBendLB.instance,
					IlvThreeBendTR.instance, IlvThreeBendBR.instance,
					IlvThreeBendRB.instance, IlvThreeBendRT.instance,
					IlvThreeBendTL.instance, IlvThreeBendBL.instance,
					IlvFourBendBTright.instance, IlvFourBendBTleft.instance,
					IlvFourBendTBright.instance, IlvFourBendTBleft.instance,
					IlvFourBendRLtop.instance, IlvFourBendRLbottom.instance,
					IlvFourBendLRtop.instance, IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendBL.instance, IlvOneBendRT.instance,
					IlvTwoBendRL.instance, IlvTwoBendTT.instance,
					IlvTwoBendBB.instance, IlvTwoBendRR.instance,
					IlvTwoBendLL.instance, IlvTwoBendBT.instance,
					IlvThreeBendRB.instance, IlvThreeBendLT.instance,
					IlvThreeBendLB.instance, IlvThreeBendTR.instance,
					IlvThreeBendBR.instance, IlvThreeBendRT.instance,
					IlvThreeBendTL.instance, IlvThreeBendBL.instance,
					IlvFourBendTBmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendBL.instance, IlvOneBendRT.instance,
					IlvTwoBendBT.instance, IlvTwoBendTT.instance,
					IlvTwoBendBB.instance, IlvTwoBendRR.instance,
					IlvTwoBendLL.instance, IlvTwoBendRL.instance,
					IlvThreeBendTL.instance, IlvThreeBendLT.instance,
					IlvThreeBendLB.instance, IlvThreeBendTR.instance,
					IlvThreeBendBR.instance, IlvThreeBendRB.instance,
					IlvThreeBendRT.instance, IlvThreeBendBL.instance,
					IlvFourBendLRmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendBL.instance, IlvOneBendRT.instance,
					IlvTwoBendRL.instance, IlvTwoBendBT.instance,
					IlvTwoBendTT.instance, IlvTwoBendBB.instance,
					IlvTwoBendRR.instance, IlvTwoBendLL.instance,
					IlvThreeBendRB.instance, IlvThreeBendRT.instance,
					IlvThreeBendTL.instance, IlvThreeBendBL.instance,
					IlvThreeBendLT.instance, IlvThreeBendLB.instance,
					IlvThreeBendTR.instance, IlvThreeBendBR.instance,
					IlvFourBendLRmiddle.instance, IlvFourBendTBmiddle.instance,
					IlvFourBendBTright.instance, IlvFourBendBTleft.instance,
					IlvFourBendTBright.instance, IlvFourBendTBleft.instance,
					IlvFourBendRLtop.instance, IlvFourBendRLbottom.instance,
					IlvFourBendLRtop.instance, IlvFourBendLRbottom.instance } };

	public static LinkShapeType[][] FROM_TR_TO_BL_POSITION_ARRAY = new LinkShapeType[][] {
			new LinkShapeType[] { IlvOneBendLT.instance, IlvOneBendBR.instance,
					IlvTwoBendTT.instance, IlvTwoBendBB.instance,
					IlvTwoBendRR.instance, IlvTwoBendLL.instance,
					IlvTwoBendLR.instance, IlvTwoBendBT.instance,
					IlvThreeBendTL.instance, IlvThreeBendBL.instance,
					IlvThreeBendRB.instance, IlvThreeBendRT.instance,
					IlvThreeBendTR.instance, IlvThreeBendLB.instance,
					IlvThreeBendBR.instance, IlvThreeBendLT.instance,
					IlvFourBendBTright.instance, IlvFourBendBTleft.instance,
					IlvFourBendTBright.instance, IlvFourBendTBleft.instance,
					IlvFourBendRLtop.instance, IlvFourBendRLbottom.instance,
					IlvFourBendLRtop.instance, IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendLT.instance, IlvOneBendBR.instance,
					IlvTwoBendLR.instance, IlvTwoBendTT.instance,
					IlvTwoBendBB.instance, IlvTwoBendRR.instance,
					IlvTwoBendLL.instance, IlvTwoBendBT.instance,
					IlvThreeBendTR.instance, IlvThreeBendLB.instance,
					IlvThreeBendTL.instance, IlvThreeBendBL.instance,
					IlvThreeBendRB.instance, IlvThreeBendRT.instance,
					IlvThreeBendLT.instance, IlvThreeBendBR.instance,
					IlvFourBendTBmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendLT.instance, IlvOneBendBR.instance,
					IlvTwoBendBT.instance, IlvTwoBendTT.instance,
					IlvTwoBendBB.instance, IlvTwoBendRR.instance,
					IlvTwoBendLL.instance, IlvTwoBendLR.instance,
					IlvThreeBendRT.instance, IlvThreeBendBL.instance,
					IlvThreeBendRB.instance, IlvThreeBendTL.instance,
					IlvThreeBendLT.instance, IlvThreeBendLB.instance,
					IlvThreeBendTR.instance, IlvThreeBendBR.instance,
					IlvFourBendRLmiddle.instance, IlvFourBendBTright.instance,
					IlvFourBendBTleft.instance, IlvFourBendTBright.instance,
					IlvFourBendTBleft.instance, IlvFourBendRLtop.instance,
					IlvFourBendRLbottom.instance, IlvFourBendLRtop.instance,
					IlvFourBendLRbottom.instance },
			new LinkShapeType[] { IlvOneBendLT.instance, IlvOneBendBR.instance,
					IlvTwoBendLR.instance, IlvTwoBendBT.instance,
					IlvTwoBendTT.instance, IlvTwoBendBB.instance,
					IlvTwoBendRR.instance, IlvTwoBendLL.instance,
					IlvThreeBendLT.instance, IlvThreeBendLB.instance,
					IlvThreeBendTR.instance, IlvThreeBendBR.instance,
					IlvThreeBendRB.instance, IlvThreeBendRT.instance,
					IlvThreeBendTL.instance, IlvThreeBendBL.instance,
					IlvFourBendRLmiddle.instance, IlvFourBendTBmiddle.instance,
					IlvFourBendBTright.instance, IlvFourBendBTleft.instance,
					IlvFourBendTBright.instance, IlvFourBendTBleft.instance,
					IlvFourBendRLtop.instance, IlvFourBendRLbottom.instance,
					IlvFourBendLRtop.instance, IlvFourBendLRbottom.instance } };

}
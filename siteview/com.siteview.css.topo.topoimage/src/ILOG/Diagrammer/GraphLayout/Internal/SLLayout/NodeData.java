package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class NodeData extends
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeOrLinkData {
	private SLNodeSide _bottomSide;

	private SLNodeSide _bottomSideQuasiSelfIG;

	private Integer _connectorStyle;

	private Integer _degree;

	private SLNodeSide _leftSide;

	private SLNodeSide _leftSideQuasiSelfIG;

	private InternalRect _linkConnectionRect;

	private SLNodeSide _rightSide;

	private SLNodeSide _rightSideQuasiSelfIG;

	private Boolean _sameBoxesAsPreviously = false;

	private Integer _selfLinkIndex;

	private SLNodeSide _topSide;

	private SLNodeSide _topSideQuasiSelfIG;

	private float _totalIncidentLinkWidth;

	public NodeData(java.lang.Object node) {
		super(node);
		this._linkConnectionRect = new InternalRect(0f, 0f, 0f, 0f);
	}

	private void CleanNodeSides() {
		if (this._topSide != null) {
			this._topSide.Clean();
		}
		if (this._bottomSide != null) {
			this._bottomSide.Clean();
		}
		if (this._leftSide != null) {
			this._leftSide.Clean();
		}
		if (this._rightSide != null) {
			this._rightSide.Clean();
		}
		if (this._topSideQuasiSelfIG != null) {
			this._topSideQuasiSelfIG.Clean();
		}
		if (this._bottomSideQuasiSelfIG != null) {
			this._bottomSideQuasiSelfIG.Clean();
		}
		if (this._leftSideQuasiSelfIG != null) {
			this._leftSideQuasiSelfIG.Clean();
		}
		if (this._rightSideQuasiSelfIG != null) {
			this._rightSideQuasiSelfIG.Clean();
		}

	}

	private SLNodeSide GetBottomSide(Boolean forceCreation) {
		if (forceCreation && (this._bottomSide == null)) {
			this._bottomSide = new SLNodeSide.BottomSide();
		}

		return this._bottomSide;

	}

	public SLNodeSide GetBottomSide(Boolean forceCreation,
			Boolean isQuasiSelfInterGraphLink) {
		if (!isQuasiSelfInterGraphLink) {

			return this.GetBottomSide(forceCreation);
		}

		return this.GetBottomSideQuasiSelfIG(forceCreation);

	}

	private SLNodeSide GetBottomSideQuasiSelfIG(Boolean forceCreation) {
		if (forceCreation && (this._bottomSideQuasiSelfIG == null)) {
			this._bottomSideQuasiSelfIG = new SLNodeSide.BottomSide(true);
		}

		return this._bottomSideQuasiSelfIG;

	}

	public Integer GetConnectorStyle() {

		return this._connectorStyle;

	}

	public Integer GetDegree() {

		return this._degree;

	}

	private SLNodeSide GetLeftSide(Boolean forceCreation) {
		if (forceCreation && (this._leftSide == null)) {
			this._leftSide = new SLNodeSide.LeftSide();
		}

		return this._leftSide;

	}

	public SLNodeSide GetLeftSide(Boolean forceCreation,
			Boolean isQuasiSelfInterGraphLink) {
		if (!isQuasiSelfInterGraphLink) {

			return this.GetLeftSide(forceCreation);
		}

		return this.GetLeftSideQuasiSelfIG(forceCreation);

	}

	private SLNodeSide GetLeftSideQuasiSelfIG(Boolean forceCreation) {
		if (forceCreation && (this._leftSideQuasiSelfIG == null)) {
			this._leftSideQuasiSelfIG = new SLNodeSide.LeftSide(true);
		}

		return this._leftSideQuasiSelfIG;

	}

	public InternalRect GetLinkConnectionRect() {

		return this._linkConnectionRect;

	}

	private SLNodeSide GetRightSide(Boolean forceCreation) {
		if (forceCreation && (this._rightSide == null)) {
			this._rightSide = new SLNodeSide.RightSide();
		}

		return this._rightSide;

	}

	public SLNodeSide GetRightSide(Boolean forceCreation,
			Boolean isQuasiSelfInterGraphLink) {
		if (!isQuasiSelfInterGraphLink) {

			return this.GetRightSide(forceCreation);
		}

		return this.GetRightSideQuasiSelfIG(forceCreation);

	}

	private SLNodeSide GetRightSideQuasiSelfIG(Boolean forceCreation) {
		if (forceCreation && (this._rightSideQuasiSelfIG == null)) {
			this._rightSideQuasiSelfIG = new SLNodeSide.RightSide(true);
		}

		return this._rightSideQuasiSelfIG;

	}

	public Integer GetSelfLinkIndex() {

		return this._selfLinkIndex;

	}

	public SLNodeSide GetSideOfPoint(InternalPoint point,
			Boolean isQuasiSelfInterGraphLink) {
		Integer sideOfPoint = GetSideOfPoint(this.GetLinkConnectionRect(),
				point);
		if (sideOfPoint == 0) {

			return this.GetLeftSide(true, isQuasiSelfInterGraphLink);
		} else if (sideOfPoint == 1) {

			return this.GetRightSide(true, isQuasiSelfInterGraphLink);
		} else if (sideOfPoint == 2) {

			return this.GetTopSide(true, isQuasiSelfInterGraphLink);
		} else if (sideOfPoint == 3) {

			return this.GetBottomSide(true, isQuasiSelfInterGraphLink);
		}
		throw (new system.Exception("unsupported side: " + sideOfPoint));

	}

	public static Integer GetSideOfPoint(InternalRect rect, InternalPoint point) {
		if (point.X <= rect.X) {

			return 0;
		}
		if (point.X < (rect.X + rect.Width)) {
			if (point.Y <= rect.Y) {

				return 2;
			}
			if (point.Y >= (rect.Y + rect.Height)) {

				return 3;
			}
			float num = point.X - rect.X;
			float num2 = (rect.X + rect.Width) - point.X;
			float num3 = point.Y - rect.Y;
			float num4 = (rect.Y + rect.Height) - point.Y;
			if (num <= num2) {
				if (num3 <= num4) {
					if (num > num3) {

						return 2;
					}

					return 0;
				}
				if (num > num4) {

					return 3;
				}

				return 0;
			}
			if (num3 <= num4) {
				if (num2 > num3) {

					return 2;
				}

				return 1;
			}
			if (num2 > num4) {

				return 3;
			}
		}

		return 1;

	}

	private SLNodeSide GetTopSide(Boolean forceCreation) {
		if (forceCreation && (this._topSide == null)) {
			this._topSide = new SLNodeSide.TopSide();
		}

		return this._topSide;

	}

	public SLNodeSide GetTopSide(Boolean forceCreation,
			Boolean isQuasiSelfInterGraphLink) {
		if (!isQuasiSelfInterGraphLink) {

			return this.GetTopSide(forceCreation);
		}

		return this.GetTopSideQuasiSelfIG(forceCreation);

	}

	private SLNodeSide GetTopSideQuasiSelfIG(Boolean forceCreation) {
		if (forceCreation && (this._topSideQuasiSelfIG == null)) {
			this._topSideQuasiSelfIG = new SLNodeSide.TopSide(true);
		}

		return this._topSideQuasiSelfIG;

	}

	public float GetTotalIncidentLinkWidth() {

		return this._totalIncidentLinkWidth;

	}

	public Boolean HasSameBoxesAsPreviously() {

		return this._sameBoxesAsPreviously;

	}

	public void IncrementSelfLinkIndex(Integer maxSelfIndex) {
		if (this._selfLinkIndex >= maxSelfIndex) {
			this._selfLinkIndex = 0;
		} else {
			this._selfLinkIndex++;
		}

	}

	@Override
	public Boolean IsIntersectingObjectValid(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {

		return ((linkData.GetFromNode() != this) && (linkData.GetToNode() != this));

	}

	@Override
	public Boolean IsLink() {

		return false;

	}

	@Override
	public Boolean IsNode() {

		return true;

	}

	public void LayoutNodeSides(ShortLinkAlgorithm layout,
			NodeSideLayouter nodeSideLayouter, IncidentLinksSorter linkSorter,
			IncidentLinksRefiner linkRefiner,
			ILinkConnectionBoxProvider connectionBoxInterface,
			float linkOffset, float minFinalSegmentLength, Integer bundleMode) {
		if (this._topSide != null) {
			this._topSide.Layout(layout, nodeSideLayouter, linkSorter,
					linkRefiner, connectionBoxInterface, this, linkOffset,
					minFinalSegmentLength, bundleMode);
		}
		if (this._bottomSide != null) {
			this._bottomSide.Layout(layout, nodeSideLayouter, linkSorter,
					linkRefiner, connectionBoxInterface, this, linkOffset,
					minFinalSegmentLength, bundleMode);
		}
		if (this._leftSide != null) {
			this._leftSide.Layout(layout, nodeSideLayouter, linkSorter,
					linkRefiner, connectionBoxInterface, this, linkOffset,
					minFinalSegmentLength, bundleMode);
		}
		if (this._rightSide != null) {
			this._rightSide.Layout(layout, nodeSideLayouter, linkSorter,
					linkRefiner, connectionBoxInterface, this, linkOffset,
					minFinalSegmentLength, bundleMode);
		}
		if (this._topSideQuasiSelfIG != null) {
			this._topSideQuasiSelfIG.Layout(layout, nodeSideLayouter,
					linkSorter, linkRefiner, connectionBoxInterface, this,
					linkOffset, minFinalSegmentLength, bundleMode);
		}
		if (this._bottomSideQuasiSelfIG != null) {
			this._bottomSideQuasiSelfIG.Layout(layout, nodeSideLayouter,
					linkSorter, linkRefiner, connectionBoxInterface, this,
					linkOffset, minFinalSegmentLength, bundleMode);
		}
		if (this._leftSideQuasiSelfIG != null) {
			this._leftSideQuasiSelfIG.Layout(layout, nodeSideLayouter,
					linkSorter, linkRefiner, connectionBoxInterface, this,
					linkOffset, minFinalSegmentLength, bundleMode);
		}
		if (this._rightSideQuasiSelfIG != null) {
			this._rightSideQuasiSelfIG.Layout(layout, nodeSideLayouter,
					linkSorter, linkRefiner, connectionBoxInterface, this,
					linkOffset, minFinalSegmentLength, bundleMode);
		}

	}

	public void RefineNodeSides(ShortLinkAlgorithm layout,
			NodeSideLayouter nodeSideLayouter,
			IncidentLinksRefiner linkRefiner, float linkOffset,
			float minFinalSegmentLength, Integer bundleMode) {
		if (this._topSide != null) {
			this._topSide.Refine(layout, nodeSideLayouter, linkRefiner, this,
					linkOffset, minFinalSegmentLength, bundleMode);
		}
		if (this._bottomSide != null) {
			this._bottomSide.Refine(layout, nodeSideLayouter, linkRefiner,
					this, linkOffset, minFinalSegmentLength, bundleMode);
		}
		if (this._leftSide != null) {
			this._leftSide.Refine(layout, nodeSideLayouter, linkRefiner, this,
					linkOffset, minFinalSegmentLength, bundleMode);
		}
		if (this._rightSide != null) {
			this._rightSide.Refine(layout, nodeSideLayouter, linkRefiner, this,
					linkOffset, minFinalSegmentLength, bundleMode);
		}
		if (this._topSideQuasiSelfIG != null) {
			this._topSideQuasiSelfIG.Refine(layout, nodeSideLayouter,
					linkRefiner, this, linkOffset, minFinalSegmentLength,
					bundleMode);
		}
		if (this._bottomSideQuasiSelfIG != null) {
			this._bottomSideQuasiSelfIG.Refine(layout, nodeSideLayouter,
					linkRefiner, this, linkOffset, minFinalSegmentLength,
					bundleMode);
		}
		if (this._leftSideQuasiSelfIG != null) {
			this._leftSideQuasiSelfIG.Refine(layout, nodeSideLayouter,
					linkRefiner, this, linkOffset, minFinalSegmentLength,
					bundleMode);
		}
		if (this._rightSideQuasiSelfIG != null) {
			this._rightSideQuasiSelfIG.Refine(layout, nodeSideLayouter,
					linkRefiner, this, linkOffset, minFinalSegmentLength,
					bundleMode);
		}

	}

	public void SetDegree(Integer degree) {
		this._degree = degree;

	}

	public void SetTotalIncidentLinkWidth(float width) {
		this._totalIncidentLinkWidth = width;

	}

	public void Update(InternalRect boundingBox,
			InternalRect linkConnectionRect, Integer connectorStyle,
			IGraphModel graphModel, Boolean incrementalMode) {
		if (incrementalMode) {
			this._sameBoxesAsPreviously = (((super.boundingBox != null) && super.boundingBox
					.equals(boundingBox)) && (this._linkConnectionRect != null))
					&& this._linkConnectionRect.equals(linkConnectionRect);
		}
		super.boundingBox.SetRect(boundingBox);
		this._linkConnectionRect.SetRect(linkConnectionRect);
		this._connectorStyle = connectorStyle;
		this._totalIncidentLinkWidth = 0f;
		this._degree = GraphModelUtil.GetLinks(graphModel,
				super.get_nodeOrLink()).get_Count();
		this._selfLinkIndex = 0;
		this.CleanNodeSides();

	}

}
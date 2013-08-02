package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;
import system.Collections.*;

public final class LLGraph {
	public Boolean _animation = false;

	private Integer _firstFreeLinkSlot;

	private LongLinkLayout _graphLayout;

	private IGraphModel _graphModel;

	private LLGrid _grid;

	public Boolean _interGraphLinkRouting = false;

	private LLLink[] _links;

	private float _maxX;

	private float _maxY;

	public Integer _minimalBorder;

	private float _minX;

	private float _minY;

	public Boolean _redraw = true;

	private String DIRECT_LINKS_AT_NODE_PROPERTY;

	public LLGraph(LongLinkLayout graphLayout, Boolean redraw) {
		this._graphLayout = graphLayout;
		this._graphModel = null;
		this._grid = null;
		this.DIRECT_LINKS_AT_NODE_PROPERTY = "__ilvLongLinkLayoutLinkStyle"
				+ graphLayout.GetInstanceId();
		this._redraw = redraw;
	}

	private void AddDirectLinkToNode(java.lang.Object node, LLLink link) {
		ArrayList directLinksAtNode = this.GetDirectLinksAtNode(node);
		if (directLinksAtNode == null) {
			directLinksAtNode = new ArrayList(3);
			this.GetModel().SetProperty(node,
					this.DIRECT_LINKS_AT_NODE_PROPERTY, directLinksAtNode);
		}
		directLinksAtNode.Add(link);

	}

	private void AfterRoute(LLLink link) {
		if (!this._interGraphLinkRouting) {
			link.UpdateGrid();
			if (this._animation) {
				link.DrawLinkForAnimation();
			}
		}

	}

	public void Attach(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.GraphLayout layoutAlgorithm) {
		this._grid = null;
		this._graphModel = graphModel;
		this.LayoutStepPerformed();
		LongLinkLayout layout = this.GetLayout();
		float horizontalGridOffset = layout.get_HorizontalGridOffset();
		float verticalGridOffset = layout.get_VerticalGridOffset();
		this._minimalBorder = 2;
		Integer num = (int) Math.Ceiling((double) (layout
				.get_MinStartSegmentLength() / horizontalGridOffset));
		if (num > this._minimalBorder) {
			this._minimalBorder = num;
		}
		num = (int) Math
				.Ceiling((double) (layout.get_MinStartSegmentLength() / verticalGridOffset));
		if (num > this._minimalBorder) {
			this._minimalBorder = num;
		}
		num = (int) Math
				.Ceiling((double) (layout.get_MinEndSegmentLength() / horizontalGridOffset));
		if (num > this._minimalBorder) {
			this._minimalBorder = num;
		}
		num = (int) Math
				.Ceiling((double) (layout.get_MinEndSegmentLength() / verticalGridOffset));
		if (num > this._minimalBorder) {
			this._minimalBorder = num;
		}

	}

	public void AttachAllLinks() {
		java.lang.Object obj2 = null;
		Integer num = 0;
		IGraphModel model = this.GetModel();
		LongLinkLayout layout = this.GetLayout();
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(model.get_Links());

		while (enumerator.HasMoreElements()) {

			obj2 = enumerator.NextElement();

			if (this.IsRoutable(obj2)) {
				num++;
			}
		}
		this._links = new LLLink[num];
		this._firstFreeLinkSlot = 0;

		enumerator = TranslateUtil.Collection2JavaStyleEnum(model.get_Links());

		while (enumerator.HasMoreElements()) {

			obj2 = enumerator.NextElement();
			layout.AddStepCount(1);

			if (this.IsRoutable(obj2)) {
				this._links[this._firstFreeLinkSlot++] = new LLLink(this, obj2);
				this.LayoutStepPerformed();
			}
		}

	}

	public void AttachLinks(ArrayList v) {
		java.lang.Object obj2 = null;
		LongLinkLayout layout = this.GetLayout();
		Integer num = 0;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(v);

		while (enumerator.HasMoreElements()) {

			obj2 = enumerator.NextElement();

			if (this.IsRoutable(obj2)) {
				num++;
			}
		}
		this._links = new LLLink[num];
		this._firstFreeLinkSlot = 0;

		enumerator = TranslateUtil.Collection2JavaStyleEnum(v);

		while (enumerator.HasMoreElements()) {

			obj2 = enumerator.NextElement();
			layout.AddStepCount(1);

			if (this.IsRoutable(obj2)) {
				this._links[this._firstFreeLinkSlot++] = new LLLink(this, obj2);
				this.LayoutStepPerformed();
			}
		}

	}

	private void BeforeRoute(LLLink link) {
		if (this._interGraphLinkRouting) {
			this.InitGrid(link.GetLinkObject());
		} else {
			LLGrid grid = this.GetGrid();
			grid.EnsureBorder(this._minimalBorder + 1);
			grid.ReorganizeDirtyBeforeSearch();
		}

	}

	private void CalcDrawingArea() {
		java.lang.Object obj2 = null;
		InternalRect nodeBoxForObstacle = null;
		IGraphModel model = this.GetModel();
		LongLinkLayout layout = this.GetLayout();
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(model.get_Nodes());
		this._minX = Float.MAX_VALUE;
		this._minY = Float.MAX_VALUE;
		this._maxX = Float.MIN_VALUE;
		this._maxY = Float.MIN_VALUE;

		while (enumerator.HasMoreElements()) {

			obj2 = enumerator.NextElement();

			nodeBoxForObstacle = layout.GetNodeBoxForObstacle(obj2);
			this.UpdateDrawingArea(nodeBoxForObstacle);

			nodeBoxForObstacle = layout.GetNodeBoxForConnection(obj2);
			this.UpdateDrawingArea(nodeBoxForObstacle);
			this.LayoutStepPerformed();
		}

		enumerator = TranslateUtil.Collection2JavaStyleEnum(layout
				.GetRectObstacles());

		while (enumerator.HasMoreElements()) {
			nodeBoxForObstacle = (InternalRect) enumerator.NextElement();
			this.UpdateDrawingArea(nodeBoxForObstacle);
			this.LayoutStepPerformed();
		}

		enumerator = TranslateUtil.Collection2JavaStyleEnum(layout
				.GetLineObstacles());

		while (enumerator.HasMoreElements()) {
			nodeBoxForObstacle = (InternalRect) enumerator.NextElement();
			this.UpdateDrawingArea(nodeBoxForObstacle);
			this.LayoutStepPerformed();
		}
		IJavaStyleEnumerator enumerator2 = TranslateUtil
				.Collection2JavaStyleEnum(layout.GetFromLineObstacles());
		IJavaStyleEnumerator enumerator3 = TranslateUtil
				.Collection2JavaStyleEnum(layout.GetToLineObstacles());
		while (enumerator2.HasMoreElements() && enumerator3.HasMoreElements()) {
			this.UpdateDrawingArea((InternalPoint) enumerator2.NextElement());
			this.UpdateDrawingArea((InternalPoint) enumerator3.NextElement());
			this.LayoutStepPerformed();
		}

		enumerator = TranslateUtil.Collection2JavaStyleEnum(model.get_Links());

		while (enumerator.HasMoreElements()) {

			obj2 = enumerator.NextElement();

			if (!this.IsRoutable(obj2)) {
				this.UpdateDrawingAreaWithLinkObject(obj2);
			} else {
				Boolean flag = layout.IsFromPointFixed(obj2);
				Boolean flag2 = layout.IsToPointFixed(obj2);
				if (flag || flag2) {
					InternalPoint[] linkPoints = GraphModelUtil.GetLinkPoints(
							model, obj2);
					Integer length = linkPoints.length;
					if ((linkPoints != null) && (length >= 2)) {
						if (flag) {
							this.UpdateDrawingArea(linkPoints[0]);
						}
						if (flag2) {
							this.UpdateDrawingArea(linkPoints[length - 1]);
						}
					}
				}
			}
			this.LayoutStepPerformed();
		}
		Integer numberOfLinks = this.GetNumberOfLinks();
		for (Integer i = 0; i < numberOfLinks; i++) {
			LLLink link = this.GetLink(i);

			if (link.IsFrozen()) {
				this.UpdateDrawingAreaWithLinkObject(link.GetLinkObject());
			}

			else if (!link.NeedsRoute()) {

				if (link.IsDirect()) {
					this.UpdateDrawingAreaWithLinkObject(link.GetLinkObject());
				} else {
					this.UpdateDrawingAreaWithLink(link);
				}
			}
			this.LayoutStepPerformed();
		}
		float num4 = Math.Max(layout.get_MinStartSegmentLength(),
				layout.get_MinEndSegmentLength());
		this._minX -= num4;
		this._maxX += num4;
		this._minY -= num4;
		this._maxY += num4;

	}

	private void CleanupDirectLinkProperties() {
		IGraphModel model = this.GetModel();
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(model.get_Nodes());

		while (enumerator.HasMoreElements()) {
			model.SetProperty(enumerator.NextElement(),
					this.DIRECT_LINKS_AT_NODE_PROPERTY, null);
		}

	}

	public void CreateCompleteGrid() {
		if ((this._links != null) && (this._links.length != 0)) {
			this.SetGrid(new LLGrid());
			this.InitGrid(null);
		}

	}

	public void Detach() {
		if (this._firstFreeLinkSlot > 0) {
			for (Integer i = 0; i < this._firstFreeLinkSlot; i++) {
				this._links[i].Dispose();
				this._links[i] = null;
			}
		}
		this._links = null;
		this._firstFreeLinkSlot = -1;

	}

	public void Dispose() {
		this._graphModel = null;
		if (this._grid != null) {
			this._grid.Dispose();
		}
		this._grid = null;

	}

	private ArrayList GetDirectLinksAtNode(java.lang.Object node) {

		return (ArrayList) this.GetModel().GetProperty(node,
				this.DIRECT_LINKS_AT_NODE_PROPERTY);

	}

	public LLGrid GetGrid() {

		return this._grid;

	}

	public LongLinkLayout GetLayout() {

		return this._graphLayout;

	}

	public LLLink GetLink(Integer i) {

		return this._links[i];

	}

	public IGraphModel GetModel() {

		return this._graphModel;

	}

	public Integer GetNumberOfLinks() {
		if (this._firstFreeLinkSlot <= 0) {

			return 0;
		}

		return this._firstFreeLinkSlot;

	}

	public void InitGrid(java.lang.Object linkObject) {
		java.lang.Object obj2 = null;
		InternalRect nodeBoxForObstacle = null;
		IGraphModel model = this.GetModel();
		LongLinkLayout layout = this.GetLayout();
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(model.get_Nodes());
		this.CalcDrawingArea();
		float horizontalGridOffset = layout.get_HorizontalGridOffset();
		float verticalGridOffset = layout.get_VerticalGridOffset();
		float horizontalGridBase = layout.get_HorizontalGridBase();
		float verticalGridBase = layout.get_VerticalGridBase();
		float horizontalMinOffset = layout.get_HorizontalMinOffset();
		float verticalMinOffset = layout.get_VerticalMinOffset();
		Integer num12 = (int) Math
				.Floor((double) ((this._minX - horizontalGridBase) / horizontalGridOffset));
		if ((horizontalGridBase + (num12 * horizontalGridOffset)) >= this._minX) {
			num12--;
		}
		num12--;
		float baseCoordX = horizontalGridBase + (num12 * horizontalGridOffset);
		Integer num13 = (int) Math
				.Ceiling((double) ((this._maxX - horizontalGridBase) / horizontalGridOffset));
		if ((horizontalGridBase + (num13 * horizontalGridOffset)) <= this._maxX) {
			num13++;
		}
		num13++;
		Integer gridSizeX = (num13 - num12) + 1;
		num12 = (int) Math
				.Floor((double) ((this._minY - verticalGridBase) / verticalGridOffset));
		if ((verticalGridBase + (num12 * verticalGridOffset)) >= this._minY) {
			num12--;
		}
		num12--;
		float baseCoordY = verticalGridBase + (num12 * verticalGridOffset);
		num13 = (int) Math
				.Ceiling((double) ((this._maxY - verticalGridBase) / verticalGridOffset));
		if ((verticalGridBase + (num13 * verticalGridOffset)) <= this._maxY) {
			num13++;
		}
		num13++;
		Integer gridSizeY = (num13 - num12) + 1;
		if (gridSizeX < 1) {
			gridSizeX = 1;
		}
		if (gridSizeY < 1) {
			gridSizeY = 1;
		}
		LLGrid grid = this.GetGrid();
		grid.Init(baseCoordX, baseCoordY, horizontalGridOffset,
				verticalGridOffset, horizontalMinOffset, verticalMinOffset,
				gridSizeX, gridSizeY);
		grid.SetImmediateReorganizationNeeded(false);
		SubgraphData interGraphLinksRoutingModel = layout
				.GetInterGraphLinksRoutingModel();
		InternalPoint point = new InternalPoint(0f, 0f);
		InternalPoint point2 = new InternalPoint(0f, 0f);
		if (layout.get_NodeObstacleEnabled()) {

			enumerator = TranslateUtil.Collection2JavaStyleEnum(model
					.get_Nodes());

			while (enumerator.HasMoreElements()) {

				obj2 = enumerator.NextElement();

				nodeBoxForObstacle = layout.GetNodeBoxForObstacle(obj2);
				if (linkObject == null) {
					grid.SetObstacle(nodeBoxForObstacle);
				} else if (interGraphLinksRoutingModel.FromEndsInside(
						linkObject, obj2)
						|| interGraphLinksRoutingModel.ToEndsInside(linkObject,
								obj2)) {
					point.X = nodeBoxForObstacle.X;
					point.Y = nodeBoxForObstacle.Y;
					point2.X = nodeBoxForObstacle.X + nodeBoxForObstacle.Width;
					point2.Y = nodeBoxForObstacle.Y;
					grid.SetObstacle(point, point2);
					point.X = point2.X;
					point.Y = nodeBoxForObstacle.Y + nodeBoxForObstacle.Height;
					grid.SetObstacle(point, point2);
					point2.X = nodeBoxForObstacle.X;
					point2.Y = point.Y;
					grid.SetObstacle(point, point2);
					point.X = nodeBoxForObstacle.X;
					point.Y = nodeBoxForObstacle.Y;
					grid.SetObstacle(point, point2);
				} else {
					grid.SetObstacle(nodeBoxForObstacle);
				}
				this.LayoutStepPerformed();
			}
		}

		enumerator = TranslateUtil.Collection2JavaStyleEnum(layout
				.GetRectObstacles());

		while (enumerator.HasMoreElements()) {
			nodeBoxForObstacle = (InternalRect) enumerator.NextElement();
			grid.SetObstacle(nodeBoxForObstacle);
			this.LayoutStepPerformed();
		}
		if (layout.get_LinkObstacleEnabled()) {

			enumerator = TranslateUtil.Collection2JavaStyleEnum(model
					.get_Links());

			while (enumerator.HasMoreElements()) {

				obj2 = enumerator.NextElement();

				if (!this.IsRoutable(obj2)) {
					this.UpdateGridWithLink(obj2);
				}
				this.LayoutStepPerformed();
			}
		}

		enumerator = TranslateUtil.Collection2JavaStyleEnum(layout
				.GetLineObstacles());

		while (enumerator.HasMoreElements()) {
			nodeBoxForObstacle = (InternalRect) enumerator.NextElement();
			point.X = nodeBoxForObstacle.X;
			point.Y = nodeBoxForObstacle.Y;
			point2.X = nodeBoxForObstacle.X + nodeBoxForObstacle.Width;
			point2.Y = nodeBoxForObstacle.Y + nodeBoxForObstacle.Height;
			grid.SetObstacle(point, point2);
			this.LayoutStepPerformed();
		}
		IJavaStyleEnumerator enumerator2 = TranslateUtil
				.Collection2JavaStyleEnum(layout.GetFromLineObstacles());
		IJavaStyleEnumerator enumerator3 = TranslateUtil
				.Collection2JavaStyleEnum(layout.GetToLineObstacles());
		while (enumerator2.HasMoreElements() && enumerator3.HasMoreElements()) {
			point = (InternalPoint) enumerator2.NextElement();
			point2 = (InternalPoint) enumerator3.NextElement();
			grid.SetObstacle(point, point2);
			this.LayoutStepPerformed();
		}
		Integer numberOfLinks = this.GetNumberOfLinks();
		for (Integer i = 0; i < numberOfLinks; i++) {
			LLLink link = this.GetLink(i);

			if (link.IsFrozen()) {
				this.UpdateGridWithLink(link.GetLinkObject());
			}

			else if (!link.NeedsRoute()) {

				if (link.IsDirect()) {
					this.UpdateGridWithLink(link.GetLinkObject());
				} else {
					link.UpdateGrid();
				}
			}
			this.LayoutStepPerformed();
		}
		grid.ReorganizeAllBeforeSearch();
		grid.SetImmediateReorganizationNeeded(true);

	}

	private Boolean IsRoutable(java.lang.Object link) {
		LongLinkLayout layout = this.GetLayout();
		SubgraphData interGraphLinksRoutingModel = layout
				.GetInterGraphLinksRoutingModel();
		if ((interGraphLinksRoutingModel != null)
				&& interGraphLinksRoutingModel.IsFixedLink(link)) {

			return false;
		}
		if (layout.get_PreserveFixedLinks() && layout.GetFixed(link)) {

			return false;
		}
		Integer linkStyle = (Integer) layout.get_LinkStyle();
		if (linkStyle == 0) {

			return false;
		}
		if ((linkStyle == 0x63)
				&& (layout.GetLinkStyle(link) == LinkLayoutLinkStyle.NoReshape)) {

			return false;
		}

		return true;

	}

	public void LayoutStepPerformed() {
		LongLinkLayout layout = this.GetLayout();
		if (layout != null) {
			layout.OnLayoutStepPerformedIfNeeded();
		}

	}

	public void PrepareIncrementalLinks() {
		if ((this._links != null) && (this._links.length != 0)) {
			LongLinkLayout layout = this.GetLayout();
			if (layout.GetInterGraphLinksRoutingModel() == null) {
				LLIncrementalData incrementalData = layout.GetIncrementalData();
				LLGrid grid = this.GetGrid();
				if (incrementalData != null) {
					incrementalData.Prepare(layout, grid);
					IGraphModel model = this.GetModel();
					Integer numberOfLinks = this.GetNumberOfLinks();
					for (Integer i = 0; i < numberOfLinks; i++) {
						LLLink link2 = this.GetLink(i);

						if (link2.NeedsRoute()) {
							LLIncrementalLink linkData = incrementalData
									.GetLinkData(link2.GetLinkObject());
							if (linkData != null) {
								if (linkData.NeedsReroute(model, grid)
										|| link2.IsDirect()) {
									if (link2.GetFromPoint() == null) {
										link2.SetFromPoint(linkData
												.GetNewFromPoint());
									}
									if (link2.GetToPoint() == null) {
										link2.SetToPoint(linkData
												.GetNewToPoint());
									}
								} else {
									link2.SetFrozen(true);
								}
							}
						}
					}
					this.CreateCompleteGrid();
				}
			}
		}

	}

	private void ReduceCrossings(Integer numberOfIterations,
			LLCrossRedAlgorithm crossRedAlg) {
		LongLinkLayout layout = this.GetLayout();
		Integer numberOfLinks = this.GetNumberOfLinks();
		Integer num3 = 0;
		Boolean flag = true;
		while ((flag && (num3 < numberOfIterations)) && layout.MayContinue()) {
			num3++;
			flag = false;
			for (Integer i = 1; i < numberOfLinks; i++) {
				layout.AddStepCount(2);

				flag |= this.ReduceCrossings(this.GetLink(i), i, crossRedAlg);
				this.LayoutStepPerformed();
			}
		}

	}

	private Boolean ReduceCrossings(LLLink link, Integer linkIndex,
			LLCrossRedAlgorithm crossRedAlg) {
		Integer num = null;

		if (link.IsDirect()) {

			return false;
		}

		if (link.IsFrozen()) {

			return false;
		}

		if (link.NeedsRoute()) {

			return false;
		}
		LongLinkLayout layout = this.GetLayout();
		Boolean flag = false;
		Boolean flag2 = false;
		Integer numberOfLinks = this.GetNumberOfLinks();
		Integer[] bounds = new Integer[4];
		Integer[] numArray2 = link.GetBounds();
		for (num = 0; num < 4; num++) {
			bounds[num] = numArray2[num];
		}
		for (num = 0; num < numberOfLinks; num++) {
			if (num != linkIndex) {

				if (((num % 20) == 0) && !layout.MayContinue()) {
					break;
				}
				LLLink link2 = this.GetLink(num);
				flag2 = false;

				if ((!link2.IsFrozen() && !link2.NeedsRoute())
						&& !link2.IsDirect()) {

					numArray2 = link2.GetBounds();

					flag2 = crossRedAlg.Run(link, link2);
					flag |= flag2;
					if (flag) {
						if (numArray2[0] < bounds[0]) {
							bounds[0] = numArray2[0];
						}
						if (numArray2[1] > bounds[1]) {
							bounds[1] = numArray2[1];
						}
						if (numArray2[2] < bounds[2]) {
							bounds[2] = numArray2[2];
						}
						if (numArray2[3] > bounds[3]) {
							bounds[3] = numArray2[3];
						}

						numArray2 = link.GetBounds();
						if (numArray2[0] < bounds[0]) {
							bounds[0] = numArray2[0];
						}
						if (numArray2[1] > bounds[1]) {
							bounds[1] = numArray2[1];
						}
						if (numArray2[2] < bounds[2]) {
							bounds[2] = numArray2[2];
						}
						if (numArray2[3] > bounds[3]) {
							bounds[3] = numArray2[3];
						}

						numArray2 = link2.GetBounds();
						if (numArray2[0] < bounds[0]) {
							bounds[0] = numArray2[0];
						}
						if (numArray2[1] > bounds[1]) {
							bounds[1] = numArray2[1];
						}
						if (numArray2[2] < bounds[2]) {
							bounds[2] = numArray2[2];
						}
						if (numArray2[3] > bounds[3]) {
							bounds[3] = numArray2[3];
						}
						this.LayoutStepPerformed();
					}
				}
			}
		}
		if (flag) {
			for (Integer i = 0; i < numberOfLinks; i++) {
				this.GetLink(i).UpdateGridIfIntersects(bounds);
			}
		}

		return flag;

	}

	public void RouteAttachedLinks() {
		if ((this._links != null) && (this._links.length != 0)) {
			Integer num = null;
			LLLink link = null;
			LLGrid grid = this.GetGrid();
			LongLinkLayout layout = this.GetLayout();
			LLLinkRouteAlgorithm algorithm = new LLLinkRouteAlgorithm(layout,
					grid);
			LLTerminationCandAlgorithm termCandAlg = new LLTerminationCandAlgorithm(
					layout, grid);
			LLCrossRedAlgorithm crossRedAlg = null;
			Integer numberOfLinks = this.GetNumberOfLinks();
			Boolean crossingReductionEnabled = layout
					.get_CrossingReductionEnabled();
			this._animation = false;
			this._interGraphLinkRouting = false;
			if (layout.GetInterGraphLinksRoutingModel() != null) {
				this._animation = false;
				crossingReductionEnabled = false;
				this._interGraphLinkRouting = true;
			}
			if (crossingReductionEnabled) {
				crossRedAlg = new LLCrossRedAlgorithm(layout, grid);
			}
			Boolean flag2 = false;
			this.RouteDirectLinks(termCandAlg);
			if (layout.get_StraightRouteEnabled()) {
				grid.SetImmediateReorganizationNeeded(false);
				for (num = 0; num < numberOfLinks; num++) {

					if (((num % 20) == 0) && !layout.MayContinue()) {
						break;
					}

					link = this.GetLink(num);
					layout.AddStepCount(2);

					if ((link.NeedsRoute() && link
							.IsCandidateForStraightRoute()) && !link.IsFrozen()) {
						this.BeforeRoute(link);
						termCandAlg.Run(link);
						algorithm.RouteStraight(link);
						this.AfterRoute(link);
					}
					this.LayoutStepPerformed();
				}
			}
			grid.SetImmediateReorganizationNeeded(true);
			for (num = 0; num < numberOfLinks; num++) {

				if (((num % 20) == 0) && !layout.MayContinue()) {
					break;
				}

				link = this.GetLink(num);
				layout.AddStepCount(10);

				if (link.NeedsRoute() && !link.IsFrozen()) {
					this.BeforeRoute(link);
					termCandAlg.Run(link);
					algorithm.Route(link);
					this.AfterRoute(link);
					if (crossingReductionEnabled) {

						flag2 |= this.ReduceCrossings(link, num, crossRedAlg);
					}
					this.LayoutStepPerformed();
				}
			}
			if (crossingReductionEnabled && flag2) {
				this.ReduceCrossings(
						layout.get_NumberCrossingReductionIterations(),
						crossRedAlg);
				this.LayoutStepPerformed();
			}
			if (crossRedAlg != null) {
				crossRedAlg.Dispose();
				crossRedAlg = null;
			}
			grid.SetImmediateReorganizationNeeded(false);
			Boolean fallbackRouteEnabled = layout.get_FallbackRouteEnabled();
			LLFallbackRouteAlgorithm algorithm4 = new LLFallbackRouteAlgorithm(
					layout, grid);
			for (num = 0; num < numberOfLinks; num++) {

				if (((num % 20) == 0) && !layout.MayContinue()) {
					break;
				}

				link = this.GetLink(num);
				layout.AddStepCount(1);

				if (link.NeedsRoute() && !link.IsFrozen()) {
					layout.AddCalcFallbackLink(link.GetLinkObject());
					if (fallbackRouteEnabled) {
						this.BeforeRoute(link);
						termCandAlg.Run(link);
						algorithm4.Route(link);
						this.AfterRoute(link);
						this.LayoutStepPerformed();
					}
				}
			}
			grid.SetImmediateReorganizationNeeded(true);
			termCandAlg.Dispose();
			algorithm.Dispose();
			algorithm4.Dispose();

			if (layout.MayContinue()) {
				layout._allowStopImmediately = false;
				for (num = 0; num < numberOfLinks; num++) {
					this.GetLink(num).ReshapeLink(true, this._redraw);
					this.LayoutStepPerformed();
				}
			}
		}

	}

	private void RouteDirectLinks(LLTerminationCandAlgorithm termCandAlg) {
		LLLink link = null;
		LongLinkLayout layout = this.GetLayout();
		LLGrid grid = this.GetGrid();
		Integer numberOfLinks = this.GetNumberOfLinks();
		Boolean flag = false;
		Integer i = 0;
		while (i < numberOfLinks) {

			link = this.GetLink(i);
			layout.AddStepCount(1);

			if (link.IsDirect()) {
				if (this._interGraphLinkRouting) {
					this.BeforeRoute(link);
				}
				termCandAlg.Run(link);
				if (((link.GetStartPoints() != null) && (link.GetEndPoints() != null))
						&& ((link.GetStartPoints().length > 0) && (link
								.GetEndPoints().length > 0))) {
					link.SetFrozen(false);
					this.AddDirectLinkToNode(link.GetFromNode(), link);
					this.AddDirectLinkToNode(link.GetToNode(), link);
					flag = true;
				} else {
					link.SetFrozen(true);
					layout.AddCalcFallbackLink(link);
				}
			}
			i++;
		}
		if (flag) {
			LLDirectRouteAlgorithm algorithm = new LLDirectRouteAlgorithm(
					layout, grid);
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(this.GetModel().get_Nodes());

			while (enumerator.HasMoreElements()) {
				java.lang.Object node = enumerator.NextElement();
				ArrayList directLinksAtNode = this.GetDirectLinksAtNode(node);
				if (directLinksAtNode != null) {
					algorithm.Route(node, directLinksAtNode);
				}
			}
			algorithm.Dispose();
			this.CleanupDirectLinkProperties();
			for (i = 0; i < numberOfLinks; i++) {

				link = this.GetLink(i);

				if (link.IsDirect() && !link.IsFrozen()) {
					if ((link.GetActStartPoint() != null)
							&& (link.GetActEndPoint() != null)) {
						link.SetSegments(new Integer[0], null, null);
						link.ReshapeLink(true, this._redraw);
					} else {
						layout.AddCalcFallbackLink(link);
					}
					link.SetFrozen(true);
				}
				this.LayoutStepPerformed();
			}
		}

	}

	public void SetGrid(LLGrid grid) {
		this._grid = grid;

	}

	private void UpdateDrawingArea(InternalPoint point) {
		float x = point.X;
		if (x < this._minX) {
			this._minX = x;
		}
		if (x > this._maxX) {
			this._maxX = x;
		}
		x = point.Y;
		if (x < this._minY) {
			this._minY = x;
		}
		if (x > this._maxY) {
			this._maxY = x;
		}

	}

	private void UpdateDrawingArea(InternalRect rect) {
		float x = rect.X;
		if (x < this._minX) {
			this._minX = x;
		}
		if (x > this._maxX) {
			this._maxX = x;
		}
		x = rect.Y;
		if (x < this._minY) {
			this._minY = x;
		}
		if (x > this._maxY) {
			this._maxY = x;
		}
		x = rect.X + rect.Width;
		if (x < this._minX) {
			this._minX = x;
		}
		if (x > this._maxX) {
			this._maxX = x;
		}
		x = rect.Y + rect.Height;
		if (x < this._minY) {
			this._minY = x;
		}
		if (x > this._maxY) {
			this._maxY = x;
		}

	}

	private void UpdateDrawingAreaWithLink(LLLink link) {
		LLGrid grid = this.GetGrid();
		if (grid != null) {
			Integer[] numArray = new Integer[2];
			InternalPoint point = new InternalPoint(0f, 0f);
			Integer num = 0;

			while (link.GetNextBend(numArray, num++)) {

				point.X = grid.GetGridCoord(0, numArray[0]);

				point.Y = grid.GetGridCoord(1, numArray[1]);
				this.UpdateDrawingArea(point);
			}
		}

	}

	private void UpdateDrawingAreaWithLinkObject(java.lang.Object linkObj) {
		InternalPoint[] linkPoints = GraphModelUtil.GetLinkPoints(
				this.GetModel(), linkObj);
		Integer length = linkPoints.length;
		for (Integer i = 0; i < length; i++) {
			this.UpdateDrawingArea(linkPoints[i]);
		}

	}

	private void UpdateGridWithLink(java.lang.Object linkObj) {
		InternalPoint[] linkPoints = GraphModelUtil.GetLinkPoints(
				this.GetModel(), linkObj);
		LLGrid grid = this.GetGrid();
		if ((linkPoints != null) && (linkPoints.length > 0)) {
			if (linkPoints.length == 1) {
				grid.SetObstacle(linkPoints[0]);
			} else {
				for (Integer i = 1; i < linkPoints.length; i++) {
					grid.SetObstacle(linkPoints[i - 1], linkPoints[i]);
				}
			}
		}

	}

}
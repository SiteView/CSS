package ILOG.Diagrammer.GraphLayout.Internal.TLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;

public final class TGraph {
	private Boolean _allowAlternating = false;

	private double[] _angleTranslationTable;

	private double _aspectRatio;

	private ILinkConnectionBoxProvider _connBoxInterface;

	private Integer _connectorStyle;

	private float[] _desiredMinDist;

	private Integer _firstFreeNodeSlot;

	private Integer _flowDirection;

	private TreeLayout _graphlayout;

	private IGraphModel _graphModel;

	private TNode _invisibleRoot;

	private float[] _levelCoord;

	private Integer _levelJustification;

	private Boolean _levelLayout = false;

	private float[] _levelSize;

	private Integer _maxDepth;

	private float _maxNodeSize;

	private float[] _minDistBetweenBranches;

	private float[] _minDistBetweenNodes;

	private TNode[] _nodes;

	private float _orthForkFactor;

	private float _overlapFactor;

	private float _percAbsCoord;

	private PercCompleteController _percCompleteController;

	private float _percFinalCoordToLeaves;

	private float _percFinalCorr;

	private float _percForAttach;

	private float _percForDetachLinks;

	private float _percForDetachNodes;

	private float _percForSpanTree;

	private float _percPrelimCoordToLeaves;

	private float _percRadial;

	private float _percRelCoord;

	private Boolean _radialLayout = false;

	private TRadial _radialTransAlg;

	public TGraph(TreeLayout graphlayout) {
		this._graphlayout = graphlayout;
		this._graphModel = null;
		this._nodes = null;
		this._invisibleRoot = null;
		this._firstFreeNodeSlot = -1;
		this._levelSize = null;
		this._levelCoord = null;
		this._desiredMinDist = null;
		this._levelLayout = true;
		this._radialLayout = false;
		this._allowAlternating = false;
		this._flowDirection = 0;
		this._levelJustification = 0;
		this._minDistBetweenNodes = new float[] { 30f, 30f };
		this._minDistBetweenBranches = new float[] { 50f, 50f };
		this._aspectRatio = 1.0;
		this._overlapFactor = 0.3f;
		this._orthForkFactor = 0.45f;
		this._maxDepth = 0;
		this._maxNodeSize = 0f;
		this._connectorStyle = 0;
		this._radialTransAlg = null;
		this._connBoxInterface = null;
		this._percCompleteController = new PercCompleteController();
		this._angleTranslationTable = null;
	}

	private void AddPrefixBorder(Integer dir, Integer lowerOrHigher,
			TPath parentBorder, TPath prefixBorder, TPath parentBorderSuffix,
			TNode rootOfParentBorderSuffix) {
		TPath path = null;
		float num = 0f;
		float num2 = 0f;
		float num3 = 0f;
		float num4 = 0f;
		for (path = parentBorder.GetNext(); (path != null)
				&& (path != parentBorderSuffix); path = path.GetNext()) {

			num += path.GetCoordCorrection(0);

			num2 += path.GetCoordCorrection(1);
		}
		TPath path2 = null;
		for (path = prefixBorder; path != null; path = path.GetNext()) {

			num3 += path.GetCoordCorrection(0);

			num4 += path.GetCoordCorrection(1);
			path2 = path;
		}
		path2.ShiftCoordCorrection(0, num - num3);
		path2.ShiftCoordCorrection(1, num2 - num4);
		rootOfParentBorderSuffix.SetFirstVisibleAtBorder(dir, lowerOrHigher,
				parentBorderSuffix);
		parentBorderSuffix.SetSubtreeRoot(rootOfParentBorderSuffix);
		prefixBorder.Append(parentBorderSuffix, parentBorder.GetLast());
		parentBorder.DisposeNextUntil(parentBorderSuffix);
		parentBorder.Append(prefixBorder, prefixBorder.GetLast());

	}

	public Boolean AllowAlternating() {

		return this._allowAlternating;

	}

	public void Attach(IGraphModel graphModel) {
		this._nodes = null;
		this._graphModel = graphModel;
		TreeLayout layout = this.GetLayout();
		this._connBoxInterface = layout.get_LinkConnectionBoxProvider();
		Integer count = graphModel.get_Nodes().get_Count();//
		Integer num2 = graphModel.get_Links().get_Count();
		if (layout.get_InvisibleRootUsed()) {
			count++;
		}
		this._percCompleteController.StartStep(this._percForAttach, count
				+ num2);
		this._nodes = new TNode[count];
		this.CreateNodes();// 类型转换异常
		this.CreateLinks();

		if (this.IsRadialLayout()) {
			Integer maxChildrenAngle = layout.get_MaxChildrenAngle();
			float num4 = 0f;
			if (maxChildrenAngle > 0) {
				num4 = (float) ((((double) maxChildrenAngle) / 180.0) * 3.1415926535897931);
				if (num4 < 0f) {
					num4 += 6.283f;
				}
			}
			this._radialTransAlg = new TRadial(this, num4);
		}
		this.LayoutStepPerformed();

	}

	public InternalRect CalcBoundingBox() {
		InternalRect rect = new InternalRect(0f, 0f, 0f, 0f);
		InternalRect rect2 = null;
		Boolean flag = false;
		for (Integer i = 0; i < this._firstFreeNodeSlot; i++) {
			TNode node = this._nodes[i];

			rect.X = node.GetMinCoord(0);

			rect.Y = node.GetMinCoord(1);

			rect.Width = node.GetSize(0);

			rect.Height = node.GetSize(1);
			if (flag) {
				rect2.Add(rect);
			} else {
				rect2 = rect;
				rect = new InternalRect(0f, 0f, 0f, 0f);
				flag = true;
			}
		}

		return rect2;

	}

	public void CalcCoords() {
		Integer num = null;
		Integer numberOfNodes = this.GetNumberOfNodes();
		this._percCompleteController.StartStep(this._percRelCoord,
				numberOfNodes);
		for (num = 0; (num < this._firstFreeNodeSlot)
				&& (this._nodes[num].GetImmediateParent() == null); num++) {
			this.CalcCoords(this._nodes[num]);
		}
		float num3 = 0f;
		Integer dirTowardsSiblings = this.GetDirTowardsSiblings();
		float[] bounds = new float[2];

		if (!this.IsRadialLayout()) {
			Integer num5 = 0;
			for (num = 0; (num < this._firstFreeNodeSlot)
					&& (this._nodes[num].GetImmediateParent() == null); num++) {
				num5++;
			}
			this._percCompleteController.StartStep(this._percAbsCoord,
					(num5 + 3) * numberOfNodes);
			for (num = 0; (num < this._firstFreeNodeSlot)
					&& (this._nodes[num].GetImmediateParent() == null); num++) {
				bounds[0] = system.ClrInt32.MaxValue;
				bounds[1] = system.ClrInt32.MinValue;
				this.CalcFinalCoordNormal(this._nodes[num], 0f, 0f, bounds);
				if (dirTowardsSiblings == 0) {
					this.ShiftBy(this._nodes[num], num3 - bounds[0], 0f);
				} else {
					this.ShiftBy(this._nodes[num], 0f, num3 - bounds[0]);
				}
				num3 += (bounds[1] - bounds[0])
						+ (2f * this._minDistBetweenBranches[dirTowardsSiblings]);
				this._percCompleteController.AddPoints(numberOfNodes);
				this.LayoutStepPerformed();
			}
		}

	}

	public void CalcCoords(TNode root) {
		TPath path3 = null;
		TPath path4 = null;
		Integer dirTowardsSiblings = this.GetDirTowardsSiblings();
		Integer dirTowardsLeaves = this.GetDirTowardsLeaves();
		TPath parentLeftBorder = new TPath(root, true, dirTowardsSiblings);
		TPath parentRightBorder = new TPath(root, false, dirTowardsSiblings);

		if (root.IsTipOver()) {
			path3 = new TPath(root, true, dirTowardsLeaves);
			path4 = new TPath(root, false, dirTowardsLeaves);
		} else {
			path3 = null;
			path4 = null;
		}
		this.CalcPrelimCoord(dirTowardsLeaves, null, root, parentLeftBorder,
				parentRightBorder, path3, path4, 0);
		parentLeftBorder.DisposeNextUntil(null);
		parentLeftBorder.Dispose();
		parentRightBorder.DisposeNextUntil(null);
		parentRightBorder.Dispose();
		if (path3 != null) {
			path3.DisposeNextUntil(null);
			path3.Dispose();
		}
		if (path4 != null) {
			path4.DisposeNextUntil(null);
			path4.Dispose();
		}

	}

	public void CalcFinalCoordInDirTowardsLeaves() {

		if (this.IsLevelLayout()) {
			Integer num2 = null;
			this._percCompleteController.StartStep(
					this._percFinalCoordToLeaves, this.GetNumberOfNodes());
			Integer dirTowardsLeaves = this.GetDirTowardsLeaves();
			float num3 = 0f;
			float num4 = 0f;
			for (num2 = 0; num2 < this._levelSize.length; num2++) {
				num4 = this._levelSize[num2];
				this._levelCoord[num2] = num3;
				if (this._levelJustification == 0) {
					this._levelCoord[num2] += 0.5f * num4;
				} else if (this._levelJustification == 1) {
					this._levelCoord[num2] += num4;
				}
				num3 += num4 + this._minDistBetweenNodes[dirTowardsLeaves];
			}
			for (num2 = 0; (num2 < this._firstFreeNodeSlot)
					&& (this._nodes[num2].GetImmediateParent() == null); num2++) {
				this.CalcFinalCoordInDirTowardsLeaves(this._nodes[num2], 0);
			}
			this._levelSize = null;
			this._levelCoord = null;
		}

	}

	private void CalcFinalCoordInDirTowardsLeaves(TNode node, Integer depth) {
		Integer dirTowardsLeaves = this.GetDirTowardsLeaves();
		float orthForkFactor = this.GetOrthForkFactor();
		this._percCompleteController.AddPoints(1);
		this.LayoutStepPerformed();
		float num3 = (depth > 0) ? this._levelCoord[depth - 1] : 0f;
		float num4 = (depth > 0) ? this._levelSize[depth - 1] : 0f;
		if (this._levelJustification == -1) {
			node.SetMinCoord(dirTowardsLeaves, this._levelCoord[depth]);
			node.SetOrthSplitCoord(
					dirTowardsLeaves,
					(num3 + num4)
							+ (orthForkFactor * this
									.GetMinDistBetweenNodes(dirTowardsLeaves)));
			// NOTICE: break ignore!!!
		} else if (this._levelJustification == 0) {
			node.SetVirtualCenter(dirTowardsLeaves, this._levelCoord[depth]);
			node.SetOrthSplitCoord(
					dirTowardsLeaves,
					(num3 + (0.5f * num4))
							+ (orthForkFactor * this
									.GetMinDistBetweenNodes(dirTowardsLeaves)));
			// NOTICE: break ignore!!!
		} else if (this._levelJustification == 1) {
			node.SetMaxCoord(dirTowardsLeaves, this._levelCoord[depth]);
			node.SetOrthSplitCoord(
					dirTowardsLeaves,
					num3
							+ (orthForkFactor * this
									.GetMinDistBetweenNodes(dirTowardsLeaves)));
			// NOTICE: break ignore!!!
		}
		for (Integer i = 0; i < node.GetNumberOfChildren(); i++) {
			TNode child = node.GetChild(i);

			if (node.HasEastWestNeighbor(child)) {
				this.CalcFinalCoordInDirTowardsLeaves(child, depth);
			} else {
				this.CalcFinalCoordInDirTowardsLeaves(child, depth + 1);
			}
		}

	}

	public void CalcFinalCoordNormal(TNode node, float shiftCoord0,
			float shiftCoord1, float[] bounds) {
		this._percCompleteController.AddPoints(3);
		this.LayoutStepPerformed();
		if (node.GetImmediateParent() == null) {
			TNode node2 = null;
			for (node2 = node.GetEastWestNeighbor(0); node2 != null; node2 = node2
					.GetEastWestNeighbor(0)) {
				this.CalcFinalCoordNormal(node2, shiftCoord0, shiftCoord1,
						bounds);
			}
			for (node2 = node.GetEastWestNeighbor(1); node2 != null; node2 = node2
					.GetEastWestNeighbor(1)) {
				this.CalcFinalCoordNormal(node2, shiftCoord0, shiftCoord1,
						bounds);
			}
		}
		Integer dirTowardsSiblings = this.GetDirTowardsSiblings();
		Integer dirTowardsLeaves = this.GetDirTowardsLeaves();
		node.ShiftCoord(dirTowardsSiblings, shiftCoord0);
		node.ShiftCoord(dirTowardsLeaves, shiftCoord1);

		shiftCoord0 += node.GetSubtreeCoordCorrection(dirTowardsSiblings);

		shiftCoord1 += node.GetSubtreeCoordCorrection(dirTowardsLeaves);
		if (node.GetMinCoord(dirTowardsSiblings) < bounds[0]) {

			bounds[0] = node.GetMinCoord(dirTowardsSiblings);
		}
		if (node.GetMaxCoord(dirTowardsSiblings) > bounds[1]) {

			bounds[1] = node.GetMaxCoord(dirTowardsSiblings);
		}
		TNodeIterator virtualChildren = node.GetVirtualChildren();

		while (virtualChildren.HasNext()) {
			this.CalcFinalCoordNormal(virtualChildren.Next(), shiftCoord0,
					shiftCoord1, bounds);
		}

	}

	public void CalcFinalCoordRadial() {
		float num = 0f;
		Integer dirTowardsSiblings = this.GetDirTowardsSiblings();
		if (this.IsRadialLayout() && (this.GetNumberOfNodes() > 1)) {
			Integer num3 = this.AllowAlternating() ? 3 : 1;
			this._percCompleteController
					.StartStep(this._percRadial, (5 * this.GetNumberOfNodes())
							+ (num3 * this.GetMaxDepth()));
			this._radialTransAlg
					.SetEllipsoidTransTable(this._angleTranslationTable);
			TNodeIterator roots = this.GetRoots();

			while (roots.HasNext()) {
				num = this._radialTransAlg.Run(roots.Next(), num, 0f)
						+ (2f * this._minDistBetweenBranches[dirTowardsSiblings]);
			}
		}

	}

	public Integer CalcInitPercParameter() {
		if (((int) this.GetLayout().get_LayoutMode()) == 10
				|| ((Integer) this.GetLayout().get_LayoutMode()) == 4
				|| ((Integer) this.GetLayout().get_LayoutMode()) == 5
				|| ((Integer) this.GetLayout().get_LayoutMode()) == 6) {

			return this.PercParameterForAutoTipOverLayout(8);
		}

		if (this.IsRadialLayout()) {

			return this.PercParameterForRadialLayout();
		}

		if (this.IsLevelLayout()) {

			return this.PercParameterForLevelLayout();
		}

		return this.PercParameterForFreeLayout();

	}

	private void CalcLinkShapeForRadial(TNode fromNode, TNode toNode,
			float[] fromPoint, float[] toPoint, double[] fromP, double[] toP,
			double[] rect) {

		fromP[0] = fromNode.GetVirtualCenter(0);

		fromP[1] = fromNode.GetVirtualCenter(1);

		toP[0] = toNode.GetVirtualCenter(0);

		toP[1] = toNode.GetVirtualCenter(1);

		rect[0] = fromNode.GetMinCoord(0);

		rect[1] = fromNode.GetMaxCoord(0);

		rect[2] = fromNode.GetMinCoord(1);

		rect[3] = fromNode.GetMaxCoord(1);
		LayoutUtil.Clip(fromP, toP, rect);
		fromPoint[0] = (float) fromP[0];
		fromPoint[1] = (float) fromP[1];

		fromP[0] = fromNode.GetCenter(0);

		fromP[1] = fromNode.GetCenter(1);

		rect[0] = toNode.GetMinCoord(0);

		rect[1] = toNode.GetMaxCoord(0);

		rect[2] = toNode.GetMinCoord(1);

		rect[3] = toNode.GetMaxCoord(1);
		LayoutUtil.Clip(toP, fromP, rect);
		toPoint[0] = (float) toP[0];
		toPoint[1] = (float) toP[1];

	}

	public Integer CalcMaxDepth() {
		this._maxDepth = 0;
		TNodeIterator roots = this.GetRoots();

		while (roots.HasNext()) {
			Integer num = this.CalcMaxDepth(roots.Next());
			if (num > this._maxDepth) {
				this._maxDepth = num;
			}
		}

		return this._maxDepth;

	}

	public Integer CalcMaxDepth(TNode node) {
		Integer num = 1;
		for (Integer i = 0; i < node.GetNumberOfChildren(); i++) {
			Integer num2 = 1 + this.CalcMaxDepth(node.GetChild(i));
			if (num2 > num) {
				num = num2;
			}
		}

		return num;

	}

	private void CalcPrelimCoord(Integer flowDir, TNode virtualParent,
			TNode node, TPath parentLeftBorder, TPath parentRightBorder,
			TPath parentTopBorder, TPath parentBottomBorder, Integer depth) {
		this._percCompleteController.AddPoints(1);
		TNode immediateParent = node.GetImmediateParent();
		TNode eastWestNeighbor = node.GetEastWestNeighbor(0);
		if (eastWestNeighbor != null) {
			this.CalcPrelimCoord(flowDir, virtualParent, eastWestNeighbor,
					parentLeftBorder, parentRightBorder, parentTopBorder,
					parentBottomBorder, depth);
		}
		Integer numberOfChildren = node.GetNumberOfChildren();
		Integer dir = 1 - flowDir;
		float minDistBetweenNodes = this.GetMinDistBetweenNodes(flowDir);
		TPath path = new TPath(node, true, dir);
		TPath path2 = new TPath(node, false, dir);
		TPath path3 = null;
		TPath path4 = null;
		if (node.IsTipOver() || (parentTopBorder != null)) {
			path3 = new TPath(node, true, flowDir);
			path4 = new TPath(node, false, flowDir);
		}

		if (!this.IsLevelLayout() && !node.IsEastWestNeighbor(1)) {
			if (virtualParent != null) {
				node.SetMinCoord(flowDir, virtualParent.GetMaxCoord(flowDir)
						+ minDistBetweenNodes);
			} else {
				node.SetMinCoord(flowDir, 0f);
			}
			node.SetOrthSplitCoord(dir, node.GetVirtualCenter(dir, false));
			node.SetOrthSplitCoord(flowDir, node.GetMinCoord(flowDir)
					- ((1f - this.GetOrthForkFactor()) * minDistBetweenNodes));
		}
		for (Integer i = 0; i < numberOfChildren; i++) {

			eastWestNeighbor = node.GetChild(i);

			if (!node.HasEastWestNeighbor(eastWestNeighbor)) {
				this.CalcPrelimCoord(flowDir, node, eastWestNeighbor, path,
						path2, path3, path4, depth + 1);
			}
		}
		for (eastWestNeighbor = node.GetLastVirtualChild(); eastWestNeighbor != null; eastWestNeighbor = eastWestNeighbor
				.GetPrevSibling()) {
			for (Integer j = 0; j < 2; j++) {
				for (Integer k = 0; k < 2; k++) {
					if (eastWestNeighbor.GetFirstVisibleAtBorder(j, k) != null) {
						eastWestNeighbor.GetFirstVisibleAtBorder(j, k)
								.SetSubtreeRoot(null);
						eastWestNeighbor.SetFirstVisibleAtBorder(j, k, null);
					}
				}
			}
		}
		float positionFromChildren = this.GetPositionFromChildren(node);
		node.SetVirtualCenter(dir, false, positionFromChildren);
		node.SetSubtreeCoordCorrection(flowDir, 0f);
		node.SetSubtreeCoordCorrection(dir, 0f);
		this.UpdateBordersWithOwnCoord(node, flowDir, path, path2, path3, path4);
		if (((immediateParent == virtualParent) && (virtualParent != null))
				&& virtualParent.IsTipOver()) {

			positionFromChildren = path3.GetStartCoord(dir);
			if (positionFromChildren > path4.GetStartCoord(dir)) {

				positionFromChildren = path4.GetStartCoord(dir);
			}
		}
		node.ShiftCoord(dir, -positionFromChildren);
		node.ShiftSubtreeCoordCorrection(dir, -positionFromChildren);
		this.ShiftBorders(path, path2, path3, path4, dir, -positionFromChildren);
		if (node.GetPrevSibling() == null) {
			float minValue = Float.MIN_VALUE;
			if (parentTopBorder != null) {
				parentTopBorder.SetStartCoord(flowDir, minValue);
				parentTopBorder.SetEndCoord(flowDir, minValue);
				parentTopBorder.SetStartCoord(dir, minValue);
				parentTopBorder.SetEndCoord(dir, minValue);
			}
			if (parentBottomBorder != null) {
				parentBottomBorder.SetStartCoord(flowDir, minValue);
				parentBottomBorder.SetEndCoord(flowDir, minValue);
				parentBottomBorder.SetStartCoord(dir, minValue);
				parentBottomBorder.SetEndCoord(dir, minValue);
			}
			if (parentLeftBorder != null) {
				parentLeftBorder.SetStartCoord(flowDir, minValue);
				parentLeftBorder.SetEndCoord(flowDir, minValue);
				parentLeftBorder.SetStartCoord(dir, minValue);
				parentLeftBorder.SetEndCoord(dir, minValue);
			}
			if (parentRightBorder != null) {
				parentRightBorder.SetStartCoord(flowDir, minValue);
				parentRightBorder.SetEndCoord(flowDir, minValue);
				parentRightBorder.SetStartCoord(dir, minValue);
				parentRightBorder.SetEndCoord(dir, minValue);
			}
		}
		if (((immediateParent == virtualParent) && (virtualParent != null))
				&& virtualParent.IsTipOver()) {
			float coord = this.GetMinDistBetweenNodes(dir)
					+ (0.5f * immediateParent.GetSize(dir));
			node.ShiftCoord(dir, coord);
			node.ShiftSubtreeCoordCorrection(dir, coord);
			this.ShiftBorders(path, path2, path3, path4, dir, coord);
			this.ShiftSubtreeAway(flowDir, virtualParent, node,
					parentTopBorder, parentBottomBorder, path3, path4, path,
					path2, depth);
			this.UpdateOtherBorder(flowDir, parentLeftBorder, path, false);
			this.UpdateOtherBorder(flowDir, parentRightBorder, path2, true);
		} else {
			this.ShiftSubtreeAway(dir, virtualParent, node, parentLeftBorder,
					parentRightBorder, path, path2, path3, path4, depth);
			if (parentTopBorder != null) {
				this.UpdateOtherBorder(dir, parentTopBorder, path3, false);
				this.UpdateOtherBorder(dir, parentBottomBorder, path4, true);
			}
		}

		eastWestNeighbor = node.GetEastWestNeighbor(1);
		if (eastWestNeighbor != null) {

			if (!this.IsLevelLayout()) {
				eastWestNeighbor
						.SetMinCoord(flowDir, node.GetMinCoord(flowDir));
				eastWestNeighbor.SetOrthSplitCoord(dir,
						eastWestNeighbor.GetVirtualCenter(dir, false));
				eastWestNeighbor
						.SetOrthSplitCoord(
								flowDir,
								eastWestNeighbor.GetMinCoord(flowDir)
										- ((1f - this.GetOrthForkFactor()) * minDistBetweenNodes));
			}
			this.CalcPrelimCoord(flowDir, virtualParent, eastWestNeighbor,
					parentLeftBorder, parentRightBorder, parentTopBorder,
					parentBottomBorder, depth);
		}

	}

	public void CalcPrelimCoordInDirTowardsLeaves() {

		if (this.IsLevelLayout()) {
			this._percCompleteController.StartStep(
					this._percPrelimCoordToLeaves, this.GetNumberOfNodes());
			this._levelSize = new float[this._maxDepth + 1];
			this._levelCoord = new float[this._maxDepth + 1];
			for (Integer i = 0; (i < this._firstFreeNodeSlot)
					&& (this._nodes[i].GetImmediateParent() == null); i++) {
				this.CalcPrelimCoordInDirTowardsLeaves(this._nodes[i], 0);
			}
		}

	}

	private void CalcPrelimCoordInDirTowardsLeaves(TNode node, Integer depth) {
		Integer dirTowardsLeaves = this.GetDirTowardsLeaves();
		this._percCompleteController.AddPoints(1);
		this.LayoutStepPerformed();
		if (node.GetSize(dirTowardsLeaves) > this._levelSize[depth]) {

			this._levelSize[depth] = node.GetSize(dirTowardsLeaves);
		}
		node.SetVirtualCenter(1 - dirTowardsLeaves, 0f);
		node.SetMaxCoord(dirTowardsLeaves, depth * (this._maxNodeSize + 10f));
		for (Integer i = 0; i < node.GetNumberOfChildren(); i++) {
			TNode child = node.GetChild(i);

			if (node.HasEastWestNeighbor(child)) {
				this.CalcPrelimCoordInDirTowardsLeaves(child, depth);
			} else {
				this.CalcPrelimCoordInDirTowardsLeaves(child, depth + 1);
			}
		}

	}

	public void ChangePercParameterForAutoTipOverLayout(Integer numberTries) {
		float num = (100f - this._percForAttach) - this._percForSpanTree;
		this.PercParameterForFreeLayout();
		float num2 = num
				/ (num + ((numberTries - 1) * (this._percRelCoord + this._percAbsCoord)));
		this._percForDetachNodes *= num2;
		this._percForDetachLinks *= num2;
		this._percForSpanTree *= num2;
		this._percPrelimCoordToLeaves *= num2;
		this._percRelCoord *= num2;
		this._percAbsCoord *= num2;
		this._percFinalCoordToLeaves *= num2;
		this._percRadial *= num2;
		this._percFinalCorr *= num2;

	}

	private void CorrectOrthSplitCoordinate(TNode parent) {

		if (!parent.IsTipOver() && !parent.IsTipOverBothSides()) {
			TNode node = null;
			Integer dirTowardsLeaves = this.GetDirTowardsLeaves();
			Integer index = 1 - dirTowardsLeaves;
			float maxValue = Float.MAX_VALUE;
			float minValue = Float.MIN_VALUE;
			Integer num5 = 0;
			Integer num6 = 0;
			TNodeIterator realChildren = parent.GetRealChildren();

			while (realChildren.HasNext()) {

				node = realChildren.Next();
				if (node.GetMinCoord(dirTowardsLeaves) < maxValue) {

					maxValue = node.GetMinCoord(dirTowardsLeaves);
				}
				if (node.GetMaxCoord(dirTowardsLeaves) < minValue) {

					minValue = node.GetMaxCoord(dirTowardsLeaves);
				}
				if (parent.GetCenter(index) <= node.GetCenter(index)) {
					num5++;
				} else {
					num6++;
				}
			}
			Integer num7 = (int) Math.Max(num5, num6);
			if (num7 >= 2) {
				float num10 = 0;
				Integer num8 = num5 - 1;
				Integer num9 = 0;
				if (parent.GetCenter(dirTowardsLeaves) <= maxValue) {
					num10 = maxValue;
				} else {
					num10 = minValue;
				}

				realChildren = parent.GetRealChildren();

				while (realChildren.HasNext()) {

					node = realChildren.Next();
					float orthSplitCoord = node
							.GetOrthSplitCoord(dirTowardsLeaves);
					if (parent.GetCenter(index) <= node.GetCenter(index)) {
						node.SetOrthSplitCoord(
								dirTowardsLeaves,
								orthSplitCoord
										+ ((num8-- * (num10 - orthSplitCoord)) / ((float) num7)));
					} else {
						node.SetOrthSplitCoord(
								dirTowardsLeaves,
								orthSplitCoord
										+ ((num9++ * (num10 - orthSplitCoord)) / ((float) num7)));
					}
				}
			}
		}

	}

	private void CorrectOrthSplitCoordinates() {
		TNodeIterator nodes = this.GetNodes();

		while (nodes.HasNext()) {
			this.CorrectOrthSplitCoordinate(nodes.Next());
		}

	}

	public void CorrectPercentageForTipOver() {
		this._percCompleteController.StartStep(this._percRelCoord, 1);
		this._percCompleteController.AddPoints(1);
		this._percCompleteController.StartStep(this._percAbsCoord, 1);
		this._percCompleteController.AddPoints(1);

	}

	public TNode CreateDummyNode() {
		TNode node = new TNode();
		this._nodes[this._firstFreeNodeSlot++] = node;

		return node;

	}

	private void CreateLinks() {
		IGraphModel model = this.GetModel();
		TreeLayout layout = this.GetLayout();
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(model.get_Links());
		Boolean preserveFixedLinks = layout.get_PreserveFixedLinks();

		while (enumerator.HasMoreElements()) {
			java.lang.Object nodeOrLink = enumerator.NextElement();
			this._percCompleteController.AddPoints(1);

			if (!preserveFixedLinks || !layout.GetFixed(nodeOrLink)) {
				TNode child = this.GetNode(model.GetFrom(nodeOrLink));
				TNode node = this.GetNode(model.GetTo(nodeOrLink));
				if (((child != null) && (node != null)) && (child != node)) {
					child.AddChild(node);
					node.AddChild(child);
					this.LayoutStepPerformed();
				}
			}
		}

	}

	private void CreateNodes() {
		TNode node = null;
		IGraphModel model = this.GetModel();
		TreeLayout layout = this.GetLayout();
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(model.get_Nodes());
		Boolean preserveFixedNodes = layout.get_PreserveFixedNodes();
		Integer dirTowardsLeaves = this.GetDirTowardsLeaves();
		Integer alignment = (Integer) layout.get_Alignment();
		Boolean flag2 = this.IsLevelLayout();
		Boolean incrementalMode = layout.get_IncrementalMode();
		this._firstFreeNodeSlot = 0;
		if (layout.get_InvisibleRootUsed()) {
			node = new TNode();
			this._nodes[this._firstFreeNodeSlot++] = node;
			this._invisibleRoot = node;
			if (this._maxNodeSize < node.GetSize(dirTowardsLeaves)) {

				this._maxNodeSize = node.GetSize(dirTowardsLeaves);
			}
		}
		float maxCoord = 0f;
		float num8 = 0f;

		while (enumerator.HasMoreElements()) {
			java.lang.Object nodeOrLink = enumerator.NextElement();
			this._percCompleteController.AddPoints(1);

			if (!preserveFixedNodes || !layout.GetFixed(nodeOrLink)) {
				TNode node2 = null;
				node = new TNode();
				this._nodes[this._firstFreeNodeSlot++] = node;
				TNodeParameters.BeforeLayout(layout, nodeOrLink, node);
				this.TransferCoordinates(nodeOrLink, node);
				if (!incrementalMode) {
					node.SetMinCoord(0, maxCoord + 1f);
					node.SetMinCoord(1, num8 + 1f);

					maxCoord = node.GetMaxCoord(0);

					num8 = node.GetMaxCoord(1);
				}
				if (this._maxNodeSize < node.GetSize(dirTowardsLeaves)) {

					this._maxNodeSize = node.GetSize(dirTowardsLeaves);
				}
				java.lang.Object eastNeighbor = layout
						.GetEastNeighbor(nodeOrLink);
				java.lang.Object westNeighbor = layout
						.GetWestNeighbor(nodeOrLink);
				Integer count = model.GetLinksTo(nodeOrLink).get_Count();
				Integer num2 = model.GetLinksFrom(nodeOrLink).get_Count();
				Integer num3 = 0;
				if (eastNeighbor != null) {
					num3++;
				}
				if (westNeighbor != null) {
					num3++;
				}
				node.SetChildrenCapacity((count + num2) + num3);
				Integer rootPreference = layout.GetRootPreference(nodeOrLink);
				if (rootPreference > 0) {
					node.SetRootNodePreference(rootPreference);
				} else if (count == 0) {
					node.SetRootNodePreference(num2 * 500);
				} else if (num2 == 0) {
					node.SetRootNodePreference(-count * 500);
				} else {
					node.SetRootNodePreference(num2 - count);
				}
				if (alignment == 0x63) {
					node.SetAlignmentStyle((Integer) layout
							.GetAlignment(nodeOrLink));
				} else {
					node.SetAlignmentStyle(alignment);
				}
				Integer alignmentStyle = node.GetAlignmentStyle();
				if (flag2 && ((alignmentStyle == 10) || (alignmentStyle == 11))) {
					node.SetAlignmentStyle(0);
				}
				if (eastNeighbor != null) {

					node2 = this.GetNode(eastNeighbor);
					if (node2 != null) {
						node.AddChild(node2);
						node.SetEastWestNeighbor(1, node2);
						node2.AddChild(node);
						node2.SetEastWestNeighbor(0, node);
					}
				}
				if (westNeighbor != null) {

					node2 = this.GetNode(westNeighbor);
					if (node2 != null) {
						node.AddChild(node2);
						node.SetEastWestNeighbor(0, node2);
						node2.AddChild(node);
						node2.SetEastWestNeighbor(1, node);
					}
				}
				this.LayoutStepPerformed();
			}
		}

	}

	public void Detach(Boolean redraw) {
		TreeLayout layout = this.GetLayout();
		IGraphModel model = this.GetModel();
		Integer count = model.get_Nodes().get_Count();
		Integer num2 = model.get_Links().get_Count();
		this._percCompleteController.StartStep(this._percForDetachNodes, count);
		this.PositionRealNodes(redraw);
		this._percCompleteController.StartFinalStep(this._percForDetachLinks,
				count + (3 * num2));
		this.RouteRealLinks(redraw);
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(model.get_Nodes());

		while (enumerator.HasMoreElements()) {
			java.lang.Object nodeObj = enumerator.NextElement();
			TNodeParameters.AfterLayout(layout, nodeObj);
			this._percCompleteController.AddPoints(1);
			this.LayoutStepPerformed();
		}
		this._percCompleteController.StopAll();

	}

	public void Dispose() {
		this._graphModel = null;
		this._nodes = null;
		this._levelSize = null;
		this._levelCoord = null;
		if (this._radialTransAlg != null) {
			this._radialTransAlg.DisposeIt();
		}
		this._radialTransAlg = null;

	}

	public void FinalPositionCorrection() {
		if (this.GetNumberOfNodes() >= 1) {
			this._percCompleteController.StartStep(this._percFinalCorr, 3);
			this.SetBackToOriginalSizes();
			if (this.GetConnectorStyle() == 3) {
				this.CorrectOrthSplitCoordinates();
			}
			if (this.GetFlowDirection() == 2) {
				this.Mirror(0);
				// NOTICE: break ignore!!!
			} else if (this.GetFlowDirection() == 3) {
				this.Mirror(1);
				// NOTICE: break ignore!!!
			}
			this._percCompleteController.AddPoints(1);
			this.LayoutStepPerformed();
			TreeLayout layout = this.GetLayout();
			InternalPoint point = TranslateUtil.Point2D2InternalPoint(layout
					.get_Position());
			float x = 0f;
			float y = 0f;
			if (point != null) {
				x = point.X;
				y = point.Y;
			}
			if (!layout.get_RootPosition()) {
				InternalRect rect2 = this.CalcBoundingBox();
				this._percCompleteController.AddPoints(1);
				this.LayoutStepPerformed();
				this.ShiftBy(x - rect2.X, y - rect2.Y);
				this._percCompleteController.AddPoints(1);
				this.LayoutStepPerformed();
			} else {
				TNode node = this.GetNode(0);
				if ((node == this._invisibleRoot)
						&& (this.GetNumberOfNodes() > 1)) {

					node = this.GetNode(1);
				}
				IGraphModel model = this.GetModel();
				if (point == null) {
					IJavaStyleEnumerator enumerator = TranslateUtil
							.Collection2JavaStyleEnum(model.get_Nodes());
					java.lang.Object nodeObj = null;

					while (enumerator.HasMoreElements()) {

						nodeObj = enumerator.NextElement();
						if (node == this.GetNode(nodeObj)) {
							break;
						}
					}
					if (nodeObj != null) {
						InternalRect rect = GraphModelUtil.BoundingBox(model,
								nodeObj);
						x = rect.X;
						y = rect.Y;
					}
				}
				this._percCompleteController.AddPoints(1);
				this.LayoutStepPerformed();
				this.ShiftBy(x - node.GetRealMinCoord(0),
						y - node.GetRealMinCoord(1));
				this._percCompleteController.AddPoints(1);
				this.LayoutStepPerformed();
			}
		}

	}

	public double GetAspectRatio() {

		return this._aspectRatio;

	}

	public Integer GetConnectorStyle() {

		return this._connectorStyle;

	}

	public Integer GetDirTowardsLeaves() {

		return (this._flowDirection % 2);

	}

	public Integer GetDirTowardsSiblings() {

		return ((this._flowDirection + 1) % 2);

	}

	public Integer GetFlowDirection() {

		return this._flowDirection;

	}

	public TNode GetInvisibleRoot() {

		return this._invisibleRoot;

	}

	public TreeLayout GetLayout() {

		return this._graphlayout;

	}

	public Integer GetLevelJustification() {

		return this._levelJustification;

	}

	public Integer GetMaxDepth() {

		return this._maxDepth;

	}

	public float GetMinDistBetweenBranches(Integer direction) {

		return this._minDistBetweenBranches[direction % 2];

	}

	public float GetMinDistBetweenNodes(Integer direction) {

		return this._minDistBetweenNodes[direction % 2];

	}

	public IGraphModel GetModel() {

		return this._graphModel;

	}

	public TNode GetNode(Integer i) {

		return this._nodes[i];

	}

	public TNode GetNode(java.lang.Object nodeObj) {
		TNodeParameters parameters = TNodeParameters.Get(this.GetLayout(),
				nodeObj);
		if (parameters == null) {

			return null;
		}

		return parameters.GetNode();

	}

	public TNodeIterator GetNodes() {

		return new AnonClass_1(this);

	}

	public Integer GetNumberOfNodes() {
		if (this._firstFreeNodeSlot <= 0) {

			return 0;
		}

		return this._firstFreeNodeSlot;

	}

	public float GetOrthForkFactor() {

		return this._orthForkFactor;

	}

	public float GetOverlapFactor() {

		return this._overlapFactor;

	}

	public PercCompleteController GetPercController() {

		return this._percCompleteController;

	}

	public float GetPercForSpanTree() {

		return this._percForSpanTree;

	}

	private float GetPositionFromChildren(TNode node) {
		Integer numberOfChildren = node.GetNumberOfChildren();
		Integer dirTowardsSiblings = this.GetDirTowardsSiblings();
		if (numberOfChildren == 0) {

			return (node.GetSize(dirTowardsSiblings) * 0.5f);
		}
		TNode firstRealChild = node.GetFirstRealChild();
		TNode lastRealChild = node.GetLastRealChild();
		if (firstRealChild == null) {

			return (node.GetSize(dirTowardsSiblings) * 0.5f);
		}
		if (node.GetAlignmentStyle() == 5) {
			if (firstRealChild != lastRealChild) {

				return ((firstRealChild.GetMinCoord(dirTowardsSiblings) + lastRealChild
						.GetMaxCoord(dirTowardsSiblings)) / 2f);
			}

			return firstRealChild.GetVirtualCenter(dirTowardsSiblings, true);
		} else if (node.GetAlignmentStyle() == 6) {
			if ((this.GetFlowDirection() != 0)
					&& (this.GetFlowDirection() != 3)) {

				return (lastRealChild.GetMaxCoord(dirTowardsSiblings) - (0.5f * node
						.GetSize(dirTowardsSiblings)));
			}

			return (firstRealChild.GetMinCoord(dirTowardsSiblings) + (0.5f * node
					.GetSize(dirTowardsSiblings)));
		} else if (node.GetAlignmentStyle() == 7) {
			if ((this.GetFlowDirection() != 0)
					&& (this.GetFlowDirection() != 3)) {

				return (firstRealChild.GetMinCoord(dirTowardsSiblings) + (0.5f * node
						.GetSize(dirTowardsSiblings)));
			}

			return (lastRealChild.GetMaxCoord(dirTowardsSiblings) - (0.5f * node
					.GetSize(dirTowardsSiblings)));
		} else if (node.GetAlignmentStyle() == 10) {

			return (node.GetSize(dirTowardsSiblings) * 0.5f);
		} else if (node.GetAlignmentStyle() == 11
				|| node.GetAlignmentStyle() == 0) {
			if (firstRealChild == lastRealChild) {

				return firstRealChild
						.GetVirtualCenter(dirTowardsSiblings, true);
			}

			return ((firstRealChild.GetVirtualCenter(dirTowardsSiblings, true) + lastRealChild
					.GetVirtualCenter(dirTowardsSiblings, true)) / 2f);
		}

		return 0f;

	}

	public TNodeIterator GetRoots() {

		return new AnonClass_2(this);

	}

	public void IncreaseNodeCapacity(Integer numNewNodes) {
		if (this._nodes == null) {
			this._nodes = new TNode[numNewNodes];
		} else {
			TNode[] nodeArray = new TNode[this._nodes.length + numNewNodes];
			for (Integer i = 0; i < this._nodes.length; i++) {
				nodeArray[i] = this._nodes[i];
			}
			this._nodes = nodeArray;
		}

	}

	private Boolean IsClippedConnectorStyle(Integer linkStyle, TNode parentNode) {

		if (this.IsRadialLayout()) {

			return true;
		}
		if (parentNode.IsTipOver() || parentNode.IsTipOverBothSides()) {

			return false;
		}

		return ((this.GetConnectorStyle() == 2) && (linkStyle == 1));

	}

	public Boolean IsLevelLayout() {

		return this._levelLayout;

	}

	public Boolean IsRadialLayout() {

		return this._radialLayout;

	}

	public void LayoutStepPerformed() {
		TreeLayout layout = this.GetLayout();
		if (layout != null) {
			layout.OnLayoutStepPerformedIfNeeded();
		}

	}

	public void Mirror(Integer coordinateIndex) {
		for (Integer i = 0; i < this._firstFreeNodeSlot; i++) {
			this._nodes[i].Mirror(coordinateIndex);
		}

	}

	public Integer PercParameterForAutoTipOverLayout(Integer numberTries) {
		Integer num = this.PercParameterForFreeLayout() - 2;
		float num2 = 100f / (100f + ((numberTries - 1) * (this._percRelCoord + this._percAbsCoord)));
		this._percForAttach *= num2;
		this._percForDetachNodes *= num2;
		this._percForDetachLinks *= num2;
		this._percForSpanTree *= num2;
		this._percPrelimCoordToLeaves *= num2;
		this._percRelCoord *= num2;
		this._percAbsCoord *= num2;
		this._percFinalCoordToLeaves *= num2;
		this._percRadial *= num2;
		this._percFinalCorr *= num2;

		return (num + (numberTries * 2));

	}

	private Integer PercParameterForFreeLayout() {
		this._percForAttach = 11f;
		this._percForDetachNodes = 44f;
		this._percForDetachLinks = 28.5f;
		this._percForSpanTree = 3.5f;
		this._percPrelimCoordToLeaves = 0f;
		this._percRelCoord = 9f;
		this._percAbsCoord = 1.5f;
		this._percFinalCoordToLeaves = 0f;
		this._percRadial = 0f;
		this._percFinalCorr = 2.5f;

		return 7;

	}

	private Integer PercParameterForLevelLayout() {
		this._percForAttach = 11f;
		this._percForDetachNodes = 43f;
		this._percForDetachLinks = 28f;
		this._percForSpanTree = 3.5f;
		this._percPrelimCoordToLeaves = 0.8f;
		this._percRelCoord = 9f;
		this._percAbsCoord = 1.5f;
		this._percFinalCoordToLeaves = 0.7f;
		this._percRadial = 0f;
		this._percFinalCorr = 2.5f;

		return 9;

	}

	private Integer PercParameterForRadialLayout() {
		this._percForAttach = 8f;
		this._percForDetachNodes = 39f;
		this._percForDetachLinks = 28f;
		this._percForSpanTree = 3.5f;
		this._percPrelimCoordToLeaves = 0.8f;
		this._percRelCoord = 4.5f;
		this._percAbsCoord = 0f;
		this._percFinalCoordToLeaves = 0.7f;
		this._percRadial = 14f;
		this._percFinalCorr = 1.5f;

		return 9;

	}

	public void PositionRealNodes(Boolean redraw) {
		IGraphModel model = this.GetModel();
		if (model != null) {
			TreeLayout layout = this.GetLayout();
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(model.get_Nodes());

			while (enumerator.HasMoreElements()) {
				java.lang.Object nodeObj = enumerator.NextElement();
				this._percCompleteController.AddPoints(1);
				TNode node = this.GetNode(nodeObj);
				if (node != null) {
					float minCoord = 0;
					float maxCoord = 0;
					if (node.GetSize(0) >= 0f) {

						minCoord = node.GetMinCoord(0);
					} else {

						minCoord = node.GetMaxCoord(0);
					}
					if (node.GetSize(1) >= 0f) {

						maxCoord = node.GetMinCoord(1);
					} else {

						maxCoord = node.GetMaxCoord(1);
					}

					minCoord += node.GetOffsetToRealTopLeft(0);

					maxCoord += node.GetOffsetToRealTopLeft(1);
					model.MoveNode(nodeObj, minCoord, maxCoord);
					if (node.GetImmediateParent() == this._invisibleRoot) {
					}
					// layout.AddCalcRoot(nodeObj);
					this.LayoutStepPerformed();
				}
			}
		}

	}

	private void RouteRealLinks(Boolean redraw) {
		IGraphModel model = this.GetModel();
		if (model != null) {
			TreeLayout layout = this.GetLayout();
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(model.get_Links());
			Boolean preserveFixedLinks = layout.get_PreserveFixedLinks();
			Integer dirTowardsLeaves = this.GetDirTowardsLeaves();
			Integer index = 1 - dirTowardsLeaves;
			Integer side = 2 * dirTowardsLeaves;
			Integer num4 = side + 1;
			Integer num5 = 2 * index;
			Integer num6 = num5 + 1;
			Integer linkStyle = (Integer) layout.get_LinkStyle();
			InternalPoint fromPoint = new InternalPoint(0f, 0f);
			InternalPoint toPoint = new InternalPoint(0f, 0f);
			InternalPoint[] points = new InternalPoint[] {
					new InternalPoint(0f, 0f), new InternalPoint(0f, 0f) };
			float[] numArray = new float[2];
			float[] numArray2 = new float[2];
			double[] fromP = new double[2];
			double[] toP = new double[2];
			double[] rect = new double[4];
			Integer connectorStyle = this.GetConnectorStyle();
			if (connectorStyle == 3) {
				this.SetChildIndexe();
			}

			while (enumerator.HasMoreElements()) {
				java.lang.Object nodeOrLink = enumerator.NextElement();
				Boolean flag2 = false;
				this._percCompleteController.AddPoints(3);

				if (!preserveFixedLinks || !layout.GetFixed(nodeOrLink)) {
					TNode fromNode = this.GetNode(model.GetFrom(nodeOrLink));
					TNode node = this.GetNode(model.GetTo(nodeOrLink));
					if ((fromNode != null) && (node != null)) {
						TNode node3 = null;
						TNode node4 = null;
						float[] numArray3 = null;
						float[] numArray4 = null;
						if (fromNode.GetOriginalParent() == node) {
							node3 = node;
							node4 = fromNode;
							numArray3 = numArray2;
							numArray4 = numArray;
							layout.AddCalcBackwardTreeLink(nodeOrLink);
						} else if (node.GetOriginalParent() == fromNode) {
							node3 = fromNode;
							node4 = node;
							numArray3 = numArray;
							numArray4 = numArray2;
							layout.AddCalcForwardTreeLink(nodeOrLink);
						} else {
							node3 = null;
							node4 = null;
							numArray3 = null;
							numArray4 = null;
							layout.AddCalcNonTreeLink(nodeOrLink);
							flag2 = true;
						}
						Integer num8 = (Integer) layout
								.GetLinkStyle(nodeOrLink);
						if (linkStyle != 0x63) {
							num8 = linkStyle;
						}
						if (num8 != 0) {
							if (flag2) {
								if (layout.get_NonTreeLinksStraight()) {
									num8 = 1;
									this.CalcLinkShapeForRadial(fromNode, node,
											numArray, numArray2, fromP, toP,
											rect);
									fromPoint.Move(numArray[0], numArray[1]);
									toPoint.Move(numArray2[0], numArray2[1]);
									GraphModelUtil.ReshapeLink(model,
											this._graphlayout, nodeOrLink,
											num8, fromPoint, null, 0, 0,
											toPoint);
								}
							} else {

								if (this.IsClippedConnectorStyle(num8, node3)) {
									this.CalcLinkShapeForRadial(fromNode, node,
											numArray, numArray2, fromP, toP,
											rect);
								} else if (node3.HasEastWestNeighbor(node4)) {
									float offsetToVirtualCenter = 0;
									float num12 = 0;
									if (fromNode.GetCenter(index) < node
											.GetCenter(index)) {

										numArray[index] = fromNode
												.GetMaxCoord(index);

										numArray2[index] = node
												.GetMinCoord(index);

										offsetToVirtualCenter = fromNode
												.GetOffsetToVirtualCenter(num4);

										num12 = node
												.GetOffsetToVirtualCenter(side);
									} else {

										numArray[index] = fromNode
												.GetMinCoord(index);

										numArray2[index] = node
												.GetMaxCoord(index);

										offsetToVirtualCenter = fromNode
												.GetOffsetToVirtualCenter(side);

										num12 = node
												.GetOffsetToVirtualCenter(num4);
									}
									if (fromNode.GetSize(dirTowardsLeaves) < node
											.GetSize(dirTowardsLeaves)) {
										numArray[dirTowardsLeaves] = fromNode
												.GetCenter(dirTowardsLeaves)
												+ offsetToVirtualCenter;
									} else {
										numArray[dirTowardsLeaves] = node
												.GetCenter(dirTowardsLeaves)
												+ num12;
									}
									numArray2[dirTowardsLeaves] = numArray[dirTowardsLeaves];
								} else if (node3.IsTipOver()
										|| node3.IsTipOverBothSides()) {
									float num13 = 0;
									float num14 = 0;
									num8 = 2;
									if (node3.GetCenter(dirTowardsLeaves) < node4
											.GetCenter(dirTowardsLeaves)) {

										numArray3[dirTowardsLeaves] = node3
												.GetMaxCoord(dirTowardsLeaves);

										num13 = fromNode
												.GetOffsetToVirtualCenter(num6);
									} else {

										numArray3[dirTowardsLeaves] = node3
												.GetMinCoord(dirTowardsLeaves);

										num13 = fromNode
												.GetOffsetToVirtualCenter(num5);
									}
									if ((node3.GetCenter(index) + num13) < node4
											.GetCenter(index)) {

										numArray4[index] = node4
												.GetMinCoord(index);

										num14 = node4
												.GetOffsetToVirtualCenter(side);
									} else {

										numArray4[index] = node4
												.GetMaxCoord(index);

										num14 = node4
												.GetOffsetToVirtualCenter(num4);
									}
									numArray3[index] = node3.GetCenter(index)
											+ num13;
									numArray4[dirTowardsLeaves] = node4
											.GetCenter(dirTowardsLeaves)
											+ num14;
								} else {
									float num15 = 0;
									float num16 = 0;
									if (fromNode.GetCenter(dirTowardsLeaves) < node
											.GetCenter(dirTowardsLeaves)) {

										numArray[dirTowardsLeaves] = fromNode
												.GetMaxCoord(dirTowardsLeaves);

										numArray2[dirTowardsLeaves] = node
												.GetMinCoord(dirTowardsLeaves);

										num15 = fromNode
												.GetOffsetToVirtualCenter(num6);

										num16 = node
												.GetOffsetToVirtualCenter(num5);
									} else {

										numArray[dirTowardsLeaves] = fromNode
												.GetMinCoord(dirTowardsLeaves);

										numArray2[dirTowardsLeaves] = node
												.GetMaxCoord(dirTowardsLeaves);

										num15 = fromNode
												.GetOffsetToVirtualCenter(num5);

										num16 = node
												.GetOffsetToVirtualCenter(num6);
									}
									numArray2[index] = node.GetCenter(index)
											+ num16;
									Integer numberOfChildren = node3
											.GetNumberOfChildren();
									if (node3.GetEastWestNeighbor(0) != null) {
										numberOfChildren--;
									}
									if (node3.GetEastWestNeighbor(1) != null) {
										numberOfChildren--;
									}
									if ((connectorStyle == 3)
											&& (numberOfChildren > 0)) {
										float num18 = fromNode.GetCenter(index)
												+ num15;
										float num19 = Math
												.Min((float) (num18 - fromNode
														.GetMinCoord(index)),
														(float) (fromNode
																.GetMaxCoord(index) - num18));
										if (num19 < 0f) {
											num19 = 0f;
										}
										numArray[index] = (num18 - num19)
												+ ((2f * num19) * (((float) (node4
														.GetIndex() + 1)) / ((float) (numberOfChildren + 1))));
									} else {
										numArray[index] = fromNode
												.GetCenter(index) + num15;
									}
								}

								if (node3.IsTipOverBothSides()) {
									TNode immediateParent = node4
											.GetImmediateParent();
									TNode eastWestNeighbor = immediateParent
											.GetEastWestNeighbor(0);
									TNode node7 = immediateParent
											.GetEastWestNeighbor(1);
									if ((eastWestNeighbor != null)
											&& (node7 != null)) {
										float num20 = 0;
										float num21 = 0;
										if (eastWestNeighbor.GetCenter(index) < node7
												.GetCenter(index)) {

											num20 = eastWestNeighbor
													.GetOffsetToVirtualCenter(num4);

											num21 = node7
													.GetOffsetToVirtualCenter(side);
										} else {

											num20 = eastWestNeighbor
													.GetOffsetToVirtualCenter(side);

											num21 = node7
													.GetOffsetToVirtualCenter(num4);
										}
										if (eastWestNeighbor
												.GetSize(dirTowardsLeaves) < node7
												.GetSize(dirTowardsLeaves)) {
											numArray4[dirTowardsLeaves] = eastWestNeighbor
													.GetCenter(dirTowardsLeaves)
													+ num20;
										} else {
											numArray4[dirTowardsLeaves] = node7
													.GetCenter(dirTowardsLeaves)
													+ num21;
										}
									}
								}
								fromPoint.X = numArray[0];
								fromPoint.Y = numArray[1];
								toPoint.X = numArray2[0];
								toPoint.Y = numArray2[1];
								if (node3.IsTipOver()
										|| node3.IsTipOverBothSides()) {
									if (dirTowardsLeaves == 0) {
										points[0].Y = numArray3[1];
										points[0].X = numArray4[0];
									} else {
										points[0].X = numArray3[0];
										points[0].Y = numArray4[1];
									}
									GraphModelUtil.ReshapeLink(model,
											this._graphlayout, nodeOrLink,
											num8, fromPoint, points, 0, 1,
											toPoint);
								}

								else if ((!this.IsRadialLayout() && !node3
										.HasEastWestNeighbor(node4))
										&& (num8 == 2)) {
									points[0].X = fromPoint.X;
									points[0].Y = fromPoint.Y;
									points[1].X = toPoint.X;
									points[1].Y = toPoint.Y;
									float orthSplitCoord = node4
											.GetOrthSplitCoord(dirTowardsLeaves);
									if (this.GetDirTowardsLeaves() == 0) {
										points[0].X = orthSplitCoord;
										points[1].X = orthSplitCoord;
									} else {
										points[0].Y = orthSplitCoord;
										points[1].Y = orthSplitCoord;
									}
									GraphModelUtil.ReshapeLink(model,
											this._graphlayout, nodeOrLink,
											num8, fromPoint, points, 0, 2,
											toPoint);
								} else if ((this.IsRadialLayout() || this._graphlayout
										.IsClippingEnabled())
										|| (node3.HasEastWestNeighbor(node4) || (num8 == 1))) {
									num8 = 1;
									GraphModelUtil.ReshapeLink(model,
											this._graphlayout, nodeOrLink,
											num8, fromPoint, null, 0, 0,
											toPoint);
								}
								this.LayoutStepPerformed();
							}
						}
					}
				}
			}
		}

	}

	public void SetAllowAlternating(Boolean allow) {
		this._allowAlternating = allow;

	}

	public void SetAngleTranslationTable(double[] table) {
		this._angleTranslationTable = table;

	}

	public void SetAspectRatio(double aspectRatio) {
		if (aspectRatio > 0.0) {
			this._aspectRatio = aspectRatio;
		}

	}

	public void SetBackToOriginalSizes() {
		Boolean respectNodeSizes = this.GetLayout().get_RespectNodeSizes();
		for (Integer i = 0; i < this._firstFreeNodeSlot; i++) {
			this._nodes[i].SetBackToOriginalSize(respectNodeSizes);
		}

	}

	private void SetChildIndexe() {
		TNodeIterator nodes = this.GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().SetIndex(-1);
		}

		nodes = this.GetNodes();

		while (nodes.HasNext()) {
			TNode parent = nodes.Next();
			this.SetChildIndexe(parent);
		}

	}

	private void SetChildIndexe(TNode parent) {
		TNodeIterator realChildren = parent.GetRealChildren();
		Integer num = 0;

		while (realChildren.HasNext()) {
			realChildren.Next().SetIndex(num++);
		}

	}

	public void SetConnectorStyle(Integer style) {
		this._connectorStyle = style;

	}

	public void SetDesiredMinDistTable(float[] table) {
		this._desiredMinDist = table;

	}

	public void SetFlowDirection(Integer flowDirection) {
		this._flowDirection = flowDirection;

	}

	public void SetLevelJustification(Integer levelJustification) {
		this._levelJustification = levelJustification;

	}

	public void SetLevelLayout(Boolean levelLayout) {
		this._levelLayout = levelLayout;

	}

	public void SetMinDistBetweenBranches(Integer direction, float dist) {
		if (dist <= 2f) {
			dist = 2f;
		}
		this._minDistBetweenBranches[direction % 2] = dist;

	}

	public void SetMinDistBetweenNodes(Integer direction, float dist) {
		if (dist <= 2f) {
			dist = 2f;
		}
		this._minDistBetweenNodes[direction % 2] = dist;

	}

	public void SetOrthForkFactor(float factor) {
		this._orthForkFactor = factor;

	}

	public void SetOverlapFactor(float factor) {
		this._overlapFactor = factor;

	}

	public void SetRadialLayout(Boolean radialLayout) {
		this._radialLayout = radialLayout;

	}

	private void ShiftBorders(TPath leftBorder, TPath rightBorder,
			TPath topBorder, TPath bottomBorder, Integer dir, float shiftDist) {
		leftBorder.ShiftCoord(dir, shiftDist);
		leftBorder.ShiftCoordCorrection(dir, shiftDist);
		rightBorder.ShiftCoord(dir, shiftDist);
		rightBorder.ShiftCoordCorrection(dir, shiftDist);
		if (topBorder != null) {
			topBorder.ShiftCoord(dir, shiftDist);
			topBorder.ShiftCoordCorrection(dir, shiftDist);
		}
		if (bottomBorder != null) {
			bottomBorder.ShiftCoord(dir, shiftDist);
			bottomBorder.ShiftCoordCorrection(dir, shiftDist);
		}

	}

	public void ShiftBy(float dx, float dy) {
		for (Integer i = 0; i < this._firstFreeNodeSlot; i++) {
			this._nodes[i].ShiftCoord(0, dx);
			this._nodes[i].ShiftCoord(1, dy);
		}

	}

	public void ShiftBy(TNode node, float dx, float dy) {
		node.ShiftCoord(0, dx);
		node.ShiftCoord(1, dy);
		for (Integer i = 0; i < node.GetNumberOfChildren(); i++) {
			this.ShiftBy(node.GetChild(i), dx, dy);
		}

	}

	public void ShiftCoordToEqualSpacingAtRoot(TNode root, float[] bounds) {
		Integer dirTowardsSiblings = this.GetDirTowardsSiblings();
		float num2 = 0f;
		float center = 0f;
		float num4 = 0f;
		float num5 = 0f;
		float dx = 0f;
		TNodeIterator children = root.GetChildren();

		if (children.HasNext()) {

			center = children.Next().GetCenter(dirTowardsSiblings);
			num4 = center;

			while (children.HasNext()) {

				num5 = children.Next().GetCenter(dirTowardsSiblings);
				if ((num5 - num4) > num2) {
					num2 = num5 - num4;
				}
				num4 = num5;
			}
			if ((((bounds[1] - bounds[0]) + center) - num5) > num2) {
				num2 = ((bounds[1] - bounds[0]) + center) - num5;
			}

			children = root.GetChildren();

			if (children.HasNext()) {

				num4 = children.Next().GetCenter(dirTowardsSiblings);
			}

			while (children.HasNext()) {
				TNode node = children.Next();

				num5 = node.GetCenter(dirTowardsSiblings);
				dx = (num4 + num2) - num5;
				if (dirTowardsSiblings == 0) {
					this.ShiftBy(node, dx, 0f);
				} else {
					this.ShiftBy(node, 0f, dx);
				}

				num4 = node.GetCenter(dirTowardsSiblings);
			}
			bounds[1] += dx;
			if (bounds[1] < ((num4 + num2) - (center - bounds[0]))) {
				bounds[1] = (num4 + num2) - (center - bounds[0]);
			}
		}

	}

	private void ShiftSubtreeAway(Integer shiftDir, TNode virtualParent,
			TNode node, TPath parentLeftBorder, TPath parentRightBorder,
			TPath nodeLeftBorder, TPath nodeRightBorder, TPath nodeTopBorder,
			TPath nodeBottomBorder, Integer depth) {
		TNode prevSibling = node.GetPrevSibling();
		Boolean flag = (virtualParent != null) && virtualParent.IsTipOver();
		Integer index = shiftDir;
		Integer direction = 1 - shiftDir;
		if (prevSibling != null) {
			float[] numArray = new float[2];
			float[] numArray2 = new float[2];
			numArray[0] = 0f;
			numArray[1] = 0f;
			numArray2[0] = 0f;
			numArray2[1] = 0f;
			float num3 = this._minDistBetweenNodes[index];
			float num4 = this._minDistBetweenBranches[index];
			float num5 = this.GetOverlapFactor()
					* this.GetMinDistBetweenNodes(direction);
			if (flag) {
				num5 = 0f;
				if (prevSibling.GetNumberOfChildren() > 0) {
					num3 = this._minDistBetweenBranches[index];
				}
			}
			float num6 = num3;
			TPath parentBorderSuffix = nodeLeftBorder;
			TPath next = parentRightBorder.GetNext();
			TNode rootOfParentBorderSuffix = null;
			TNode subtreeRoot = null;
			float endCoord = 0f;
			float num8 = 0f;
			float num9 = 0f;
			float num10 = 0f;
			if (parentBorderSuffix != null) {

				rootOfParentBorderSuffix = parentBorderSuffix.GetNode();

				endCoord = parentBorderSuffix.GetEndCoord(index);

				num9 = parentBorderSuffix.GetEndCoord(direction);

				if (!this.IsLevelLayout()
						&& (parentBorderSuffix.GetNode().GetNumberOfChildren() > 0)) {
					num9 += num5;
				}

				numArray[0] += parentBorderSuffix.GetCoordCorrection(0);

				numArray[1] += parentBorderSuffix.GetCoordCorrection(1);
			}
			if (next != null) {

				subtreeRoot = next.GetNode();

				num8 = next.GetEndCoord(index);

				num10 = next.GetEndCoord(direction);

				if (!this.IsLevelLayout()
						&& (next.GetNode().GetNumberOfChildren() > 0)) {
					num10 += num5;
				}

				numArray2[0] += next.GetCoordCorrection(0);

				numArray2[1] += next.GetCoordCorrection(1);
			}
			while ((parentBorderSuffix != null) && (next != null)) {
				Boolean flag2 = null;
				Boolean flag3 = null;
				if (this.IsRadialLayout() && (this._desiredMinDist != null)) {
					if (depth < (this._maxDepth + 1)) {
						num6 = this._desiredMinDist[depth];
					} else {
						num6 = this._minDistBetweenNodes[index];
					}
				}
				if (this.IsRadialLayout() && (depth < 5)) {
					num3 = num6;
					Integer numberOfChildren = parentBorderSuffix.GetNode()
							.GetNumberOfChildren();
					Integer num12 = next.GetNode().GetNumberOfChildren();
					if ((numberOfChildren == 0) && (num12 > 2)) {
						num3 = next.GetNext().GetEndCoord(index)
								+ numArray2[index];
						num3 -= num8;
					}
					if ((numberOfChildren > 2) && (num12 == 0)) {
						num3 = endCoord;
						num3 -= parentBorderSuffix.GetNext().GetEndCoord(index)
								+ numArray[index];
					}
					if (num3 > num6) {
						num6 = num3;
					}
				}
				if (((num8 + num6) - endCoord) > 0f) {
					this.LayoutStepPerformed();
					float coord = (num8 + num6) - endCoord;
					Integer num14 = 0;
					TNode node5 = node;
					TNode node6 = node.GetPrevSibling();

					if (!node.IsEastWestNeighbor(1) && !flag) {
						while (((node5 != null) && (node6 != null))
								&& (node5 != subtreeRoot)) {

							if (!node5.HasEastWestNeighbor(node6)
									&& !node6.HasEastWestNeighbor(node5)) {
								num14++;
							}
							node5 = node6;

							node6 = node6.GetPrevSibling();
						}
					}
					if (num14 != 0) {
						Integer num15 = num14;
						node5 = node;
						for (node6 = node.GetPrevSibling(); ((node5 != null) && (node6 != null))
								&& (node5 != subtreeRoot); node6 = node6
								.GetPrevSibling()) {
							float num16 = (coord * num15) / ((float) num14);
							node5.ShiftCoord(index, num16);
							node5.ShiftSubtreeCoordCorrection(index, num16);

							if (!node5.HasEastWestNeighbor(node6)
									&& !node6.HasEastWestNeighbor(node5)) {
								num15--;
							}
							node5 = node6;
						}
					} else {
						node.ShiftCoord(index, coord);
						node.ShiftSubtreeCoordCorrection(index, coord);
					}
					this.ShiftBorders(nodeLeftBorder, nodeRightBorder,
							nodeTopBorder, nodeBottomBorder, index, coord);
					numArray[index] += coord;
					endCoord += coord;
				}
				num6 = num4;
				if (num9 < num10) {
					flag2 = true;
					flag3 = false;
				} else if (num9 > num10) {
					flag2 = false;
					flag3 = true;
					depth++;
				} else {
					flag2 = true;
					flag3 = true;
					depth++;
				}
				if (flag2) {

					parentBorderSuffix = parentBorderSuffix.GetNext();
					if (parentBorderSuffix != null) {
						if (parentBorderSuffix.GetSubtreeRoot() != null) {

							rootOfParentBorderSuffix = parentBorderSuffix
									.GetSubtreeRoot();
						}
						endCoord = parentBorderSuffix.GetEndCoord(index)
								+ numArray[index];
						num9 = parentBorderSuffix.GetEndCoord(direction)
								+ numArray[direction];

						if (!this.IsLevelLayout()
								&& (parentBorderSuffix.GetNode()
										.GetNumberOfChildren() > 0)) {
							num9 += num5;
						}

						numArray[0] += parentBorderSuffix.GetCoordCorrection(0);

						numArray[1] += parentBorderSuffix.GetCoordCorrection(1);
					}
				}
				if (flag3) {

					next = next.GetNext();
					if (next != null) {
						if (next.GetSubtreeRoot() != null) {

							subtreeRoot = next.GetSubtreeRoot();
						}
						num8 = next.GetEndCoord(index) + numArray2[index];
						num10 = next.GetEndCoord(direction)
								+ numArray2[direction];

						if (!this.IsLevelLayout()
								&& (next.GetNode().GetNumberOfChildren() > 0)) {
							num10 += num5;
						}

						numArray2[0] += next.GetCoordCorrection(0);

						numArray2[1] += next.GetCoordCorrection(1);
					}
				}
			}
			if (parentBorderSuffix != null) {
				TPath path3 = parentLeftBorder.CutPath(parentLeftBorder);
				parentLeftBorder.Append(nodeLeftBorder,
						nodeLeftBorder.GetLast());
				nodeLeftBorder = path3;
				this.AddPrefixBorder(index, 0, parentLeftBorder,
						nodeLeftBorder, parentBorderSuffix,
						rootOfParentBorderSuffix);
				parentRightBorder.DisposeNextUntil(null);
				parentRightBorder.Append(nodeRightBorder,
						nodeRightBorder.GetLast());
			} else if (next != null) {
				this.AddPrefixBorder(index, 1, parentRightBorder,
						nodeRightBorder, next, subtreeRoot);
			} else {
				parentRightBorder.DisposeNextUntil(null);
				parentRightBorder.Append(nodeRightBorder,
						nodeRightBorder.GetLast());
			}
		} else {
			parentLeftBorder.Append(nodeLeftBorder, nodeLeftBorder.GetLast());
			parentRightBorder
					.Append(nodeRightBorder, nodeRightBorder.GetLast());
		}

	}

	public void SwapPosition(TNode node, Integer i) {
		Integer index = node.GetIndex();
		this._nodes[index] = this._nodes[i];
		this._nodes[index].SetIndex(index);
		this._nodes[i] = node;
		node.SetIndex(i);

	}

	public void TransferCoordinates(java.lang.Object nodeObj, TNode node) {
		InternalRect rect = null;
		IGraphModel model = this.GetModel();
		InternalRect realBBox = GraphModelUtil.BoundingBox(model, nodeObj);
		float[] offsetToVirtualCenter = null;
		if (this._connBoxInterface != null) {

			rect = TranslateUtil.GetBox(this._connBoxInterface, model, nodeObj);
			float num = TranslateUtil.GetTangentialOffset(
					this._connBoxInterface, model, nodeObj, Direction.Top);
			float num2 = TranslateUtil.GetTangentialOffset(
					this._connBoxInterface, model, nodeObj, Direction.Bottom);
			float num3 = TranslateUtil.GetTangentialOffset(
					this._connBoxInterface, model, nodeObj, Direction.Left);
			float num4 = TranslateUtil.GetTangentialOffset(
					this._connBoxInterface, model, nodeObj, Direction.Right);
			if (((num != 0f) || (num2 != 0f)) || ((num3 != 0f) || (num4 != 0f))) {
				offsetToVirtualCenter = new float[] { num, num2, num3, num4 };
			}
		} else {
			rect = realBBox;
		}
		node.SetBoundingBoxes(rect, realBBox, offsetToVirtualCenter, this
				.GetLayout().get_RespectNodeSizes(), this.IsRadialLayout());
		if (this.GetFlowDirection() == 2) {
			node.MirrorOffsetsToVirtualCenter(0);

			return;
		} else if (this.GetFlowDirection() == 3) {
			node.MirrorOffsetsToVirtualCenter(1);

			return;
		}

	}

	private void UpdateBordersWithOwnCoord(TNode node, Integer flowDir,
			TPath leftBorder, TPath rightBorder, TPath topBorder,
			TPath bottomBorder) {
		Integer dir = 1 - flowDir;
		Integer index = flowDir;
		TNode firstRealChild = node.GetFirstRealChild();
		TNode lastRealChild = node.GetLastRealChild();
		if (leftBorder != null) {

			if ((leftBorder.GetNext() == null)
					|| (!node.IsTipOver() && (node.GetMaxCoord(index) <= leftBorder
							.GetEndCoord(index)))) {
				leftBorder.Update(true, dir);
			} else {
				TPath nodeBorder = leftBorder.CutPath(leftBorder);
				leftBorder.Update(true, dir);
				if (node.IsTipOver() && (firstRealChild != null)) {
					leftBorder.SetEndCoord(index,
							lastRealChild.GetMaxCoord(index));
				}
				this.UpdateOtherBorder(index, leftBorder, nodeBorder, false);
			}
		}
		if (rightBorder != null) {
			if ((rightBorder.GetNext() == null)
					|| (node.GetMaxCoord(index) <= rightBorder
							.GetEndCoord(index))) {
				rightBorder.Update(false, dir);
			} else {
				TPath path2 = rightBorder.CutPath(rightBorder);
				rightBorder.Update(false, dir);
				this.UpdateOtherBorder(index, rightBorder, path2, true);
			}
		}
		if (topBorder != null) {

			if ((topBorder.GetNext() == null)
					|| (!node.IsTipOver() && (node.GetMaxCoord(dir) <= topBorder
							.GetEndCoord(dir)))) {
				topBorder.Update(true, index);
			} else {
				TPath path3 = topBorder.CutPath(topBorder);
				topBorder.Update(true, index);

				if (!node.IsTipOver() && (firstRealChild != null)) {
					if (topBorder.GetStartCoord(dir) > path3.GetStartCoord(dir)) {
						topBorder.SetStartCoord(dir, path3.GetStartCoord(dir));
					}
					if (topBorder.GetEndCoord(dir) < lastRealChild
							.GetMaxCoord(dir)) {
						topBorder.SetEndCoord(dir,
								lastRealChild.GetMaxCoord(dir));
					}
				}
				this.UpdateOtherBorder(dir, topBorder, path3, false);
			}
		}
		if (bottomBorder != null) {

			if ((bottomBorder.GetNext() == null)
					|| (!node.IsTipOver() && (node.GetMaxCoord(dir) <= bottomBorder
							.GetEndCoord(dir)))) {
				bottomBorder.Update(false, index);
			} else {
				TPath path4 = bottomBorder.CutPath(bottomBorder);
				bottomBorder.Update(false, index);
				if (node.IsTipOver() && (firstRealChild != null)) {
					bottomBorder.SetEndCoord(index,
							lastRealChild.GetMaxCoord(index));
				}
				this.UpdateOtherBorder(dir, bottomBorder, path4, true);
			}
		}

	}

	private void UpdateOtherBorder(Integer borderDir, TPath parentBorder,
			TPath nodeBorder, Boolean keepHigher) {
		TPath path3 = null;
		TPath next = null;
		Integer index = borderDir;
		Integer num2 = 1 - borderDir;
		TPath path = nodeBorder;
		TPath path2 = parentBorder;
		float dist = 0f;
		float startCoord = 0f;
		float endCoord = 0f;
		float num6 = 0f;
		for (path3 = path; path3 != null; path3 = path3.GetNext()) {
			path3.ShiftCoord(0, dist);
			path3.ShiftCoord(1, startCoord);

			dist += path3.GetCoordCorrection(0);

			startCoord += path3.GetCoordCorrection(1);
			path3.SetCoordCorrection(0, 0f);
			path3.SetCoordCorrection(1, 0f);
		}
		for (next = path2; next != null; next = next.GetNext()) {
			next.ShiftCoord(0, endCoord);
			next.ShiftCoord(1, num6);

			endCoord += next.GetCoordCorrection(0);

			num6 += next.GetCoordCorrection(1);
			next.SetCoordCorrection(0, 0f);
			next.SetCoordCorrection(1, 0f);
		}

		dist = path.GetStartCoord(index);

		startCoord = path.GetStartCoord(num2);

		endCoord = path2.GetStartCoord(index);

		num6 = path2.GetStartCoord(num2);
		if (dist < endCoord) {
			path.Swap(path2);

			dist = path.GetStartCoord(index);

			startCoord = path.GetStartCoord(num2);

			endCoord = path2.GetStartCoord(index);

			num6 = path2.GetStartCoord(num2);
		}
		next = path2;

		endCoord = path2.GetEndCoord(index);

		num6 = path2.GetEndCoord(num2);
		while ((next != null) && (endCoord < dist)) {

			next = next.GetNext();
			if (next != null) {

				endCoord = next.GetEndCoord(index);

				num6 = next.GetEndCoord(num2);
			}
		}
		if (next == null) {
			if (path2.GetEndCoord(index) != Float.MIN_VALUE) {
				path.SetStartCoord(index, path2.GetLast().GetEndCoord(index));
			} else {
				path2.GetLast().SetEndCoord(index, path.GetStartCoord(index));
			}
			path2.Append(path, path.GetLast());
		} else {
			TPath cutCell = next;
			path3 = path;

			dist = path.GetEndCoord(index);

			startCoord = path.GetEndCoord(num2);
			Boolean flag = ((num6 > startCoord) && keepHigher)
					|| ((num6 < startCoord) && !keepHigher);
			while ((path3 != null) && (next != null)) {
				if (dist < endCoord) {

					path3 = path3.GetNext();
					if (path3 != null) {

						dist = path3.GetEndCoord(index);

						startCoord = path3.GetEndCoord(num2);
					}
				} else {
					if (dist > endCoord) {

						next = next.GetNext();
						if (next != null) {

							endCoord = next.GetEndCoord(index);

							num6 = next.GetEndCoord(num2);
						}
						continue;
					}

					path3 = path3.GetNext();
					if (path3 != null) {

						dist = path3.GetEndCoord(index);

						startCoord = path3.GetEndCoord(num2);
					}

					next = next.GetNext();
					if (next != null) {

						endCoord = next.GetEndCoord(index);

						num6 = next.GetEndCoord(num2);
					}
				}
			}
			if (!flag) {
				TPath path6 = path2.CutPath(cutCell);
				if (next == cutCell) {
					next = new TPath(cutCell.GetNode(), false, 0);
					next.SetStartCoord(0, cutCell.GetStartCoord(0));
					next.SetStartCoord(1, cutCell.GetStartCoord(1));
					next.SetEndCoord(0, cutCell.GetEndCoord(0));
					next.SetEndCoord(1, cutCell.GetEndCoord(1));
					if (path6 != null) {
						next.Append(path6, path6.GetLast());
					}
					path6 = next;
				}
				cutCell.SetEndCoord(index, path.GetStartCoord(index));
				path2.Append(path, path.GetLast());
				if ((path3 == null) && (next != null)) {
					next.SetStartCoord(index, path2.GetLast()
							.GetEndCoord(index));
					path2.Append(next, path6.GetLast());
				}
				if ((path6 != null) && (path6 != next)) {
					path6.DisposeNextUntil(next);
					path6.Dispose();
				}
			} else {
				if (path3 != null) {
					path3.SetStartCoord(index,
							path2.GetLast().GetEndCoord(index));
					path2.Append(path3, path.GetLast());
					path.DisposeNextUntil(path3);
				}
				if (path != path3) {
					path.Dispose();
				}
			}
		}

	}

	private class AnonClass_1 implements TNodeIterator {
		private TGraph __outerThis;

		public Integer count;

		public AnonClass_1(TGraph input__outerThis) {
			this.__outerThis = input__outerThis;
			this.count = 0;
		}

		public Boolean HasNext() {

			return (this.count < this.__outerThis._firstFreeNodeSlot);

		}

		public TNode Next() {

			return this.__outerThis.GetNode(this.count++);

		}

	}

	private class AnonClass_2 implements TNodeIterator {
		private TGraph __outerThis;

		public Integer count;

		public AnonClass_2(TGraph input__outerThis) {
			this.__outerThis = input__outerThis;
			this.count = 0;
		}

		public Boolean HasNext() {

			return ((this.count < this.__outerThis._firstFreeNodeSlot) && (this.__outerThis._nodes[this.count]
					.GetImmediateParent() == null));

		}

		public TNode Next() {

			return this.__outerThis.GetNode(this.count++);

		}

	}
}
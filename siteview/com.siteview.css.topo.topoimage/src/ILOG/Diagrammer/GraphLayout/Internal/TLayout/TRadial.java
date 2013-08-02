package ILOG.Diagrammer.GraphLayout.Internal.TLayout;

import system.*;
import system.Math;

public final class TRadial extends TGraphAlgorithm {
	private Boolean _allLevelsAlternating = false;

	private Boolean _allowAlternating = false;

	private float[] _altInnerSmallestWidth;

	private float[] _altOuterSmallestWidth;

	private double[] _angleTransTable;

	private double _aspectRatio;

	private Boolean[] _considerAlternating;

	private float[] _desiredMinDist;

	private Integer _dir0;

	private Integer _dir1;

	private Boolean _firstCircleEvenlySpaced = false;

	private TNode[] _firstNode;

	private TNode[] _lastNode;

	private float[] _layerSize;

	private Integer _levelJustification;

	private float _maxChildrenAngle;

	private float[] _maxChildrenSpan;

	private Integer _maxDepth;

	private float[] _minDistBetweenBranches;

	private float[] _minDistBetweenNodes;

	private float[] _minNodeDist;

	private Boolean _needsRetry = false;

	private static double _pi = 3.1415926535897931;

	private TNode _root;

	private float[] _smallestWidth;

	private static double _twopi = 6.2831853071795862;

	private float _width;

	public TRadial(TGraph graph, float maxChildrenAngle) {
		super(graph);
		this._allowAlternating = true;
		this._maxChildrenAngle = maxChildrenAngle;
		this._angleTransTable = null;
		this._root = null;

		this._dir0 = graph.GetDirTowardsSiblings();

		this._dir1 = graph.GetDirTowardsLeaves();

		this._levelJustification = graph.GetLevelJustification();

		this._aspectRatio = graph.GetAspectRatio();
		this._firstCircleEvenlySpaced = graph.GetLayout()
				.get_FirstCircleEvenlySpacing();
		this._allLevelsAlternating = graph.GetLayout()
				.get_AllLevelsAlternating();
		this._minDistBetweenNodes = new float[] {
				graph.GetMinDistBetweenNodes(0),
				graph.GetMinDistBetweenNodes(1) };
		this._minDistBetweenBranches = new float[] {
				graph.GetMinDistBetweenBranches(0),
				graph.GetMinDistBetweenBranches(1) };

		this._allowAlternating = graph.AllowAlternating();

		this._maxDepth = graph.GetMaxDepth();
		this._width = 0f;
		this._needsRetry = false;
		this._firstNode = null;
		this._lastNode = null;
		this._layerSize = null;
		this._minNodeDist = null;
		this._desiredMinDist = null;
		this._smallestWidth = null;
		this._altInnerSmallestWidth = null;
		this._altOuterSmallestWidth = null;
		this._considerAlternating = null;
		this._maxChildrenSpan = null;
	}

	private Boolean AllowAlternating() {

		return this._allowAlternating;

	}

	private void BuildLayerTable() {
		Integer num = null;
		float num2 = 0;
		Integer direction = this.GetDir0();
		Integer num4 = this.GetDir1();
		float minDistBetweenNodes = this.GetMinDistBetweenNodes(direction);
		float num6 = this.GetMinDistBetweenNodes(num4);
		this._firstNode = new TNode[this._maxDepth];
		this._lastNode = new TNode[this._maxDepth];
		this._layerSize = new float[this._maxDepth];
		this._minNodeDist = new float[this._maxDepth];
		if (this.GetMaxChildrenAngle() != 0.0) {
			this._maxChildrenSpan = new float[this._maxDepth];
		}
		for (num = 0; num < this._maxDepth; num++) {
			this._minNodeDist[num] = Float.MAX_VALUE;
		}
		this.BuildLayerTableRecursive(this.GetRoot(), 0);
		for (num = 0; num < this._maxDepth; num++) {
			if ((this._firstNode[num] != null)
					&& (this._firstNode[num] != this._lastNode[num])) {
				this._firstNode[num].SetPrevNode(this._lastNode[num]);
				num2 = (this._width + this._firstNode[num]
						.GetMinCoord(direction))
						- this._lastNode[num].GetMaxCoord(direction);
				if (num2 < this._minNodeDist[num]) {
					if ((this._desiredMinDist != null)
							&& (num2 < this._desiredMinDist[num])) {
						this._width += this._desiredMinDist[num] - num2;
						this._minNodeDist[num] = this._desiredMinDist[num];
					} else {
						this._minNodeDist[num] = num2;
					}
				}
			}
		}
		this.CalcSmallestWidths();
		this.CalcMaxChildrenSpan();
		if (this._desiredMinDist == null) {
			this._desiredMinDist = new float[this._maxDepth];
		}
		for (num = 0; num < this._maxDepth; num++) {
			this._desiredMinDist[num] = minDistBetweenNodes;
		}
		float radius = 0f;
		for (num = 1; num < this._maxDepth; num++) {
			float num10 = this._smallestWidth[num];
			float num9 = (0.5f * (this._layerSize[num - 1] + this._layerSize[num]))
					+ num6;
			if (this._maxChildrenSpan != null) {
				float num11 = this.MaxChildrenAngleDelta(radius,
						this._maxChildrenSpan[num]);
				if (num11 > num9) {
					num9 = num11;
				}
			}
			if (this.GetAspectRatio() < 1.0) {
				radius += num9 / ((float) this.GetAspectRatio());
			} else {
				radius += num9;
			}
			float num7 = (float) Circumference((double) radius,
					this.GetAspectRatio());
			if (num7 < num10) {
				num2 = (float) RadiusFromCircumference((double) num10,
						this.GetAspectRatio());
				if ((num2 - radius) > (1.3f * num9)) {
					this._desiredMinDist[num] *= this._width / num7;
					this._needsRetry = true;
				} else {
					radius = num2;
				}
			}
		}

	}

	private void BuildLayerTableRecursive(TNode node, Integer depth) {
		super.AddPercPoints(1);
		super.LayoutStepPerformed();
		float size = node.GetSize(this.GetDir1());
		if (size > this._layerSize[depth]) {
			this._layerSize[depth] = size;
		}
		if (this._firstNode[depth] == null) {
			this._firstNode[depth] = node;
		}
		if (this._lastNode[depth] != null) {
			Integer index = this.GetDir0();
			float num3 = node.GetMinCoord(index)
					- this._lastNode[depth].GetMaxCoord(index);
			if (num3 < this._minNodeDist[depth]) {
				this._minNodeDist[depth] = num3;
			}
		}
		node.Reinitialize();
		node.SetPrevNode(this._lastNode[depth]);
		this._lastNode[depth] = node;
		TNodeIterator virtualChildren = node.GetVirtualChildren();

		while (virtualChildren.HasNext()) {
			this.BuildLayerTableRecursive(virtualChildren.Next(), depth + 1);
		}

	}

	private void CalcAlternatingSmallestWidths() {
		this._altInnerSmallestWidth = new float[this._maxDepth];
		this._altOuterSmallestWidth = new float[this._maxDepth];
		this._considerAlternating = new Boolean[this._maxDepth];
		this._altInnerSmallestWidth[0] = 0f;
		this._altOuterSmallestWidth[0] = 0f;
		Integer direction = this.GetDir0();
		float minDistBetweenNodes = this.GetMinDistBetweenNodes(direction);
		for (Integer i = 1; i < this._maxDepth; i++) {
			float num5 = 0;
			TNode node4 = null;
			float num4 = num5 = 0.0001f;
			TNode node = node4 = this._lastNode[i];
			TNode node2 = (node != null) ? node.GetPrevNode() : null;
			TNode prevNode = (node2 != null) ? node2.GetPrevNode() : null;
			this._considerAlternating[i] = false;
			if ((prevNode != null) && (prevNode != node)) {
				Integer num2 = 0;
				do {
					float num7 = 0;
					float num8 = 0;
					float num9 = 0;
					if ((node.GetSortValue() * node2.GetSortValue()) < 0.0) {
						num7 = 0.5f * node.GetSize(direction);
						num8 = 0.5f * prevNode.GetSize(direction);
						num9 = node.GetMinCoord(direction)
								- prevNode.GetMaxCoord(direction);
						if (num9 < 0f) {
							num9 += this._width;
						}
					} else {
						num7 = 0.5f * node.GetSize(direction);
						num8 = 0.5f * node2.GetSize(direction);
						num9 = node.GetMinCoord(direction)
								- node2.GetMaxCoord(direction);
						if (num9 < 0f) {
							num9 += this._width;
						}
					}
					num2++;
					float num6 = ((minDistBetweenNodes + num7) + num8)
							/ ((num9 + num7) + num8);
					if (node.GetSortValue() > 0.0) {
						if ((num6 <= 1f) && (num6 > num5)) {
							num5 = num6;
						}
					} else if ((num6 <= 1f) && (num6 > num4)) {
						num4 = num6;
					}
					node = node2;

					prevNode = prevNode.GetPrevNode();
				} while (node != node4);
				if (num2 > 4) {
					this._considerAlternating[i] = true;
				}
			}
			this._altInnerSmallestWidth[i] = this._width * num4;
			this._altOuterSmallestWidth[i] = this._width * num5;
			if ((this._altInnerSmallestWidth[i] >= this._smallestWidth[i])
					|| (this._altOuterSmallestWidth[i] >= this._smallestWidth[i])) {
				this._considerAlternating[i] = false;
			}
			super.AddPercPoints(1);
			super.LayoutStepPerformed();
		}

		if (this.ForceAlternating()) {
			for (Integer j = 1; j < this._maxDepth; j++) {
				this._considerAlternating[j] = true;
			}
		}
		this._considerAlternating[0] = false;

	}

	private void CalcChildrenCoordRadial(Integer depth, TNode parent,
			float parentOldCoord0, double angleOfParent, double radiusOfParent,
			float[] bounds) {
		Integer index = this.GetDir0();
		Integer direction = this.GetDir1();
		float num4 = 0f;
		double r = 0.0;
		float num8 = 0f;
		double num9 = 1.0;
		float num12 = this._width;
		float num13 = this._width;
		float minDistBetweenNodes = this.GetMinDistBetweenNodes(direction);
		Boolean flag = false;
		double aspectRatio = this.GetAspectRatio();
		float num16 = this._width;
		super.AddPercPoints(1);
		super.LayoutStepPerformed();
		TNode firstRealChild = parent.GetFirstRealChild();
		if (firstRealChild != null) {
			num12 = this._smallestWidth[depth + 1];
			num8 = (0.5f * (this._layerSize[depth] + this._layerSize[depth + 1]))
					+ minDistBetweenNodes;
			if (this._maxChildrenSpan != null) {
				float num17 = this.MaxChildrenAngleDelta(
						(float) radiusOfParent,
						this._maxChildrenSpan[depth + 1]);
				if (num17 > num8) {
					num8 = num17;
				}
			}
			if (aspectRatio < 1.0) {
				r = radiusOfParent + (num8 / ((float) aspectRatio));
			} else {
				r = radiusOfParent + num8;
			}
			float num18 = (float) Circumference(r, aspectRatio);
			if (num18 > num16) {
				num9 = num16 / num18;
				if (this.IsFirstCircleEvenlySpaced() && (depth == 0)) {
					num9 = 1.0;
				}
			} else if (num18 < num12) {
				double num19 = RadiusFromCircumference((double) num12,
						aspectRatio);
				if (this.AllowAlternating()
						&& this._considerAlternating[depth + 1]) {
					num8 = this._layerSize[depth + 1] + minDistBetweenNodes;
					if (aspectRatio < 1.0) {
						num8 /= (float) aspectRatio;
					}
					double num20 = r;
					num13 = this._altInnerSmallestWidth[depth + 1];
					if (num18 < num13) {
						double num21 = RadiusFromCircumference((double) num13,
								aspectRatio);
						if (num21 > num20) {
							num20 = num21;
						}
					}
					num13 = this._altOuterSmallestWidth[depth + 1];
					if (num18 < num13) {
						double num22 = RadiusFromCircumference((double) num13,
								aspectRatio);
						if ((num22 - num8) > num20) {
							num20 = num22 - num8;
						}
					}
					if (((num20 + num8) < num19) || this.ForceAlternating()) {
						r = num20 + num8;
						flag = true;
					} else {
						r = num19;
					}
				} else {
					r = num19;
				}
			}
			if (parent.GetImmediateParent() == null) {
				num4 = parentOldCoord0 - firstRealChild.GetCenter(index);
			}
		}
		TNodeIterator virtualChildren = parent.GetVirtualChildren();

		while (virtualChildren.HasNext()) {
			float num6 = 0;
			float num7 = 0;

			firstRealChild = virtualChildren.Next();
			float center = firstRealChild.GetCenter(index);
			double angle = angleOfParent
					+ (((_twopi * num9) * ((center - parentOldCoord0) + num4)) / ((double) num16));
			double a = this.TranslateToEllipse(angle);
			if (flag) {
				if (firstRealChild.GetSortValue() < 0.0) {
					num6 = (float) ((aspectRatio * (r - num8)) * Math.Sin(a));
					num7 = (float) ((r - num8) * Math.Cos(a));
				} else {
					num6 = (float) ((aspectRatio * r) * Math.Sin(a));
					num7 = (float) (r * Math.Cos(a));
				}
			} else {
				num6 = (float) ((aspectRatio * r) * Math.Sin(a));
				num7 = (float) (r * Math.Cos(a));
			}
			this.SetNodeCoordinate(firstRealChild, num6, num7);
			if (firstRealChild.GetMinCoord(index) < bounds[0]) {

				bounds[0] = firstRealChild.GetMinCoord(index);
			}
			if (firstRealChild.GetMaxCoord(index) > bounds[1]) {

				bounds[1] = firstRealChild.GetMaxCoord(index);
			}
			this.CalcChildrenCoordRadial(depth + 1, firstRealChild, center,
					angle, r, bounds);
		}

	}

	private float CalcFinalCoordRadial(TNode root, float border0, float border1) {
		float num4 = 0;
		float num5 = 0;
		Integer index = this.GetDir0();
		Integer num2 = this.GetDir1();
		float center = root.GetCenter(index);
		float[] bounds = new float[2];
		root.SetCenter(index, 0f);
		root.SetCenter(num2, 0f);

		bounds[0] = root.GetMinCoord(index);

		bounds[1] = root.GetMaxCoord(index);
		this.CalcChildrenCoordRadial(0, root, center, 0.0, 0.0, bounds);
		if (index == 0) {
			num4 = border0 - bounds[0];
			num5 = border1;
			bounds[0] += num4;
			bounds[1] += num4;
		} else {
			num4 = border1;
			num5 = border0 - bounds[0];
			bounds[0] += num5;
			bounds[1] += num5;
		}
		super.GetGraph().ShiftBy(root, num4, num5);
		super.LayoutStepPerformed();

		return bounds[1];

	}

	private void CalcMaxChildrenSpan() {
		if (this.GetMaxChildrenAngle() != 0f) {
			Integer index = this.GetDir0();
			for (Integer i = 2; i < this._maxDepth; i++) {
				TNode node2 = null;
				TNode node4 = null;
				float num3 = 0;
				TNode node = node2 = this._lastNode[i];
				TNode immediateParent = (node != null) ? node
						.GetImmediateParent() : null;
				TNode node3 = node4 = node;
				for (node = (node != null) ? node.GetPrevNode() : null; (node != node2)
						&& (node != null); node = node.GetPrevNode()) {
					if (node.GetImmediateParent() == immediateParent) {
						node4 = node;
					} else {
						if (node3 != node4) {
							num3 = node3.GetCenter(index)
									- node4.GetCenter(index);
							if (num3 > this._maxChildrenSpan[i]) {
								this._maxChildrenSpan[i] = num3;
							}
						}

						immediateParent = node.GetImmediateParent();
						node3 = node4 = node;
					}
				}
				if (node3 != node4) {
					num3 = node3.GetCenter(index) - node4.GetCenter(index);
					if (num3 > this._maxChildrenSpan[i]) {
						this._maxChildrenSpan[i] = num3;
					}
				}
			}
		}

	}

	private void CalcNonRadialLayout() {
		Integer direction = this.GetDir0();
		float[] bounds = new float[] { Float.MAX_VALUE, Float.MIN_VALUE };
		TNode root = this.GetRoot();
		super.GetGraph().CalcFinalCoordNormal(root, 0f, 0f, bounds);

		if (this.IsFirstCircleEvenlySpaced()) {
			super.GetGraph().ShiftCoordToEqualSpacingAtRoot(root, bounds);
		}
		float dx = (direction == 0) ? -bounds[0] : 0f;
		float dy = (direction == 1) ? -bounds[0] : 0f;
		super.GetGraph().ShiftBy(this.GetRoot(), dx, dy);
		this._width = bounds[1] - bounds[0];

		if (!this.IsFirstCircleEvenlySpaced()) {
			this._width += this.GetMinDistBetweenBranches(direction);
		}

	}

	private void CalcSmallestWidths() {
		this._smallestWidth = new float[this._maxDepth];
		this._smallestWidth[0] = 0f;
		Integer direction = this.GetDir0();
		float minDistBetweenNodes = this.GetMinDistBetweenNodes(direction);
		float minDistBetweenBranches = this
				.GetMinDistBetweenBranches(direction);
		for (Integer i = 1; i < this._maxDepth; i++) {
			float num5 = 0;
			float num6 = 0;
			TNode node3 = null;
			float num4 = 0.0001f;
			TNode node = node3 = this._lastNode[i];
			TNode prevNode = (node != null) ? node.GetPrevNode() : null;
			if ((prevNode == null) || (prevNode == node)) {
				if (num4 > 1f) {
					num4 = 1f;
				}
				this._smallestWidth[i] = this._width * num4;
				super.AddPercPoints(1);
				super.LayoutStepPerformed();
				continue;
			}
			Label_0071: do {
				num6 = 0.5f * node.GetSize(direction);
				float num7 = 0.5f * prevNode.GetSize(direction);
				float num8 = node.GetMinCoord(direction)
						- prevNode.GetMaxCoord(direction);
				if (num8 < 0f) {
					num8 += this._width;
				}
				if (node.GetImmediateParent() == prevNode.GetImmediateParent()) {
					num5 = ((minDistBetweenNodes + num6) + num7)
							/ ((num8 + num6) + num7);
				} else {
					num5 = ((minDistBetweenBranches + num6) + num7)
							/ ((num8 + num6) + num7);
				}
				if (num5 > num4) {
					num4 = num5;
				}
				if (num4 < 1f) {
					node = prevNode;

					prevNode = node.GetPrevNode();
				}

			} while (num4 < 1f && node != node3);

			Label_0114: if (num4 > 1f) {
				num4 = 1f;
			}
			this._smallestWidth[i] = this._width * num4;
			super.AddPercPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private static double Circumference(double r, double sx) {
		if (sx == 1.0) {

			return (_twopi * r);
		}
		double num = (sx - 1.0) / (sx + 1.0);
		double num2 = num * num;
		double num3 = 64.0 - (16.0 * num2);

		return ((((_pi * r) * (1.0 + sx)) * (64.0 - ((3.0 * num2) * num2))) / num3);

	}

	@Override
	public void DisposeIt() {
		super.DisposeIt();
		this._root = null;
		this._angleTransTable = null;
		this._minDistBetweenNodes = null;
		this._minDistBetweenBranches = null;
		this._firstNode = null;
		this._lastNode = null;
		this._layerSize = null;
		this._minNodeDist = null;
		this._desiredMinDist = null;
		this._smallestWidth = null;
		this._altInnerSmallestWidth = null;
		this._altOuterSmallestWidth = null;
		this._considerAlternating = null;
		this._maxChildrenSpan = null;

	}

	private Boolean ForceAlternating() {

		return this._allLevelsAlternating;

	}

	private double GetAspectRatio() {

		return this._aspectRatio;

	}

	private Integer GetDir0() {

		return this._dir0;

	}

	private Integer GetDir1() {

		return this._dir1;

	}

	public static double[] GetEllipsoidTransTable(double sx) {
		if (sx == 1.0) {

			return null;
		}
		double[] numArray = new double[0x1f5];
		double num2 = Circumference(1.0, sx) / 500.0;
		double num4 = 0.0;
		double num6 = 0.0;
		for (Integer i = 0; i < 500; i++) {
			double num10 = 0;
			double a = num4;
			double num5 = num6;
			double num7 = sx * Math.Sin(a);
			for (double j = Math.Cos(a); num5 < (i * num2); j = num10) {
				a += 0.0001;
				double num8 = sx * Math.Sin(a);

				num10 = Math.Cos(a);

				num5 += Math.Sqrt(((num8 - num7) * (num8 - num7))
						+ ((num10 - j) * (num10 - j)));
				num7 = num8;
			}
			numArray[i] = a;
			num4 = a;
			num6 = num5;
		}
		numArray[500] = _twopi;

		return numArray;

	}

	private Integer GetLevelJustification() {

		return this._levelJustification;

	}

	private float GetMaxChildrenAngle() {

		return this._maxChildrenAngle;

	}

	private float GetMinDistBetweenBranches(Integer direction) {

		return this._minDistBetweenBranches[direction % 2];

	}

	private float GetMinDistBetweenNodes(Integer direction) {

		return this._minDistBetweenNodes[direction % 2];

	}

	private TNode GetRoot() {

		return this._root;

	}

	private Boolean IsFirstCircleEvenlySpaced() {

		return this._firstCircleEvenlySpaced;

	}

	private void MarkAlternating() {
		Integer index = this.GetDir0();
		for (Integer i = 0; i < this._maxDepth; i++) {
			TNode node3 = null;
			TNode node4 = null;
			TNode node = node3 = node4 = this._lastNode[i];
			TNode prevNode = (node != null) ? node.GetPrevNode() : null;
			float num2 = 0f;
			while ((node != null) && (prevNode != null)) {
				float num3 = node.GetMinCoord(index)
						- prevNode.GetMaxCoord(index);
				if (num3 > num2) {
					node4 = prevNode;
					num2 = num3;
				}
				node = prevNode;

				prevNode = node.GetPrevNode();
				if (node == node3) {
					break;
				}
			}
			node = node3 = node4;
			double x = 1.0;
			while (node != null) {
				node.SetSortValue(x);
				x = -x;

				node = node.GetPrevNode();
				if (node == node3) {
					break;
				}
			}
			super.AddPercPoints(1);
			super.LayoutStepPerformed();
		}

	}

	public float MaxChildrenAngleDelta(float radius, float childrenSpan) {
		float maxChildrenAngle = this.GetMaxChildrenAngle();
		if (maxChildrenAngle < 0.01f) {

			return 0f;
		}
		if (childrenSpan < 0.01f) {

			return 0f;
		}
		float num2 = 0f;
		float num3 = childrenSpan / maxChildrenAngle;
		if (maxChildrenAngle < 3.14) {
			num2 = childrenSpan
					/ ((float) (2.0 * Math
							.Tan((double) (maxChildrenAngle / 2f))));
		}
		if (radius > (2f * childrenSpan)) {

			return num2;
		}

		return (((radius / (2f * childrenSpan)) * (num2 - num3)) + num3);

	}

	private static double RadiusFromCircumference(double c, double sx) {
		if (sx == 1.0) {

			return (c / _twopi);
		}
		double num = (sx - 1.0) / (sx + 1.0);
		double num2 = num * num;
		double num3 = 64.0 - ((3.0 * num2) * num2);

		return ((((c * (64.0 - (16.0 * num2))) / num3) / (1.0 + sx)) / _pi);

	}

	public float Run(TNode root, float border0, float border1) {
		TGraph graph = super.GetGraph();
		this._root = root;
		this._maxDepth = graph.GetMaxDepth() + 1;
		this._needsRetry = false;
		this._desiredMinDist = null;
		this.CalcNonRadialLayout();
		this.BuildLayerTable();
		if (this._needsRetry) {
			super.IncrPercPointEstimation(super.GetPercController().GetPoints()
					+ graph.GetNumberOfNodes());
			super.GetGraph().SetDesiredMinDistTable(this._desiredMinDist);
			Integer direction = this.GetDir1();
			float num2 = 10f + (2f * this.GetMinDistBetweenNodes(direction));
			float coord = 0f;
			for (Integer i = 0; i < this._maxDepth; i++) {
				TNode node2 = null;
				TNode prevNode = node2 = this._lastNode[i];
				coord += this._layerSize[i] + num2;
				while (prevNode != null) {
					prevNode.SetMaxCoord(direction, coord);

					prevNode = prevNode.GetPrevNode();
					if (prevNode == node2) {
						break;
					}
				}
			}
			this.GetRoot().InitPrevSiblings();
			super.GetGraph().CalcCoords(this.GetRoot());
			this._maxDepth = super.GetGraph().GetMaxDepth() + 1;
			this.CalcNonRadialLayout();
			this.BuildLayerTable();
		}

		if (this.AllowAlternating()) {
			this.MarkAlternating();
			this.CalcAlternatingSmallestWidths();
		}

		return this.CalcFinalCoordRadial(root, border0, border1);

	}

	public void SetEllipsoidTransTable(double[] table) {
		this._angleTransTable = table;

	}

	public void SetNodeCoordinate(TNode node, float x, float y) {
		Integer index = this.GetDir0();
		Integer num2 = this.GetDir1();
		if (this.GetLevelJustification() == -1) {
			if (x <= (0.5f * node.GetOriginalSize(index))) {
				if (x < (-0.5f * node.GetOriginalSize(index))) {
					node.SetCenter(index,
							x - (0.5f * node.GetOriginalSize(index)));
				} else {
					node.SetCenter(index, x);
				}

			}
			node.SetCenter(index, x + (0.5f * node.GetOriginalSize(index)));
			// NOTICE: break ignore!!!
		} else if (this.GetLevelJustification() == 1) {
			if (x <= (0.5f * node.GetOriginalSize(index))) {
				if (x < (-0.5f * node.GetOriginalSize(index))) {
					node.SetCenter(index,
							x + (0.5f * node.GetOriginalSize(index)));
				} else {
					node.SetCenter(index, x);
				}
			} else {
				node.SetCenter(index, x - (0.5f * node.GetOriginalSize(index)));
			}
			if (y > (0.5f * node.GetOriginalSize(num2))) {
				node.SetCenter(num2, y - (0.5f * node.GetOriginalSize(num2)));

				return;
			}
			if (y < (-0.5f * node.GetOriginalSize(num2))) {
				node.SetCenter(num2, y + (0.5f * node.GetOriginalSize(num2)));

				return;
			}
			node.SetCenter(num2, y);

			return;
		} else {
			node.SetCenter(index, x);
			node.SetCenter(num2, y);

			return;
		}
		if (y > (0.5f * node.GetOriginalSize(num2))) {
			node.SetCenter(num2, y + (0.5f * node.GetOriginalSize(num2)));
		} else if (y < (-0.5f * node.GetOriginalSize(num2))) {
			node.SetCenter(num2, y - (0.5f * node.GetOriginalSize(num2)));
		} else {
			node.SetCenter(num2, y);
		}

	}

	private double TranslateToEllipse(double angle) {
		if (this._angleTransTable == null) {

			return angle;
		}
		if (angle < 0.0) {
			angle += _twopi;
		}
		if (angle > _twopi) {
			angle -= _twopi;
		}
		double num = (500.0 * angle) / _twopi;
		Integer index = (int) num;
		if (index >= 500) {

			return _twopi;
		}
		if (index < 0) {

			return 0.0;
		}

		return (this._angleTransTable[index] + ((this._angleTransTable[index + 1] - this._angleTransTable[index]) * (num - index)));

	}

}
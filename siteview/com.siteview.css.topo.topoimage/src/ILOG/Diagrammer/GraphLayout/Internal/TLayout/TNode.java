package ILOG.Diagrammer.GraphLayout.Internal.TLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;

public final class TNode {
	private Integer _alignmentStyle = 0;

	private Integer _bestAlignmentStyle = 0;

	private TNode[] _children = null;

	private float[] _coord = new float[] { 0f, 0f };

	private TNode[] _ewNeighbors = null;

	private Integer _firstFreeChildrenSlot = -1;

	private TPath[][] _firstVisibleAtBorder = new TPath[2][];

	private Integer _iflag = 0;

	private TNode _immediateParent = null;

	private float[] _offsetToVirtualCenter = null;

	private Integer _origAlignmentStyle = 0;

	private TNode _originalParent = null;

	private float[] _origSize = new float[] { 0f, 0f };

	private float[] _orthSplitCoord = new float[] { 0f, 0f };

	private TNode _prevSibling = null;

	private float[] _realBounds = null;

	private Integer _rootNodePreference = 0;

	private float[] _size = new float[] { 0f, 0f };

	private double _sortValue = 0.0;

	private float[] _subtreeCoordCorrection = new float[2];

	public TNode() {
		for (Integer i = 0; i < 2; i++) {
			this._firstVisibleAtBorder[i] = new TPath[2];
		}
		this.Reinitialize();
	}

	public void AddChild(TNode child) {
		TNodeIterator children = this.GetChildren();

		while (children.HasNext()) {
			if (children.Next() == child) {

				return;
			}
		}
		if ((this._firstFreeChildrenSlot < 0)
				|| (this._firstFreeChildrenSlot >= this._children.length)) {
			throw (new system.Exception("Children capacity exceeded"));
		}
		this._children[this._firstFreeChildrenSlot++] = child;

	}

	private void AddSortValue(double x) {
		this._sortValue += x;

	}

	public double Angle(float x, float y) {

		return LayoutUtil.Angle(x, y, this.GetCenter(0), this.GetCenter(1));

	}

	public Boolean CanAutoTipOver() {

		if (this.IsTipOver()) {

			return false;
		}
		if (this.GetNumberOfChildren() < 2) {

			return false;
		}
		for (Integer i = 0; i < this.GetNumberOfChildren(); i++) {
			if (this.GetChild(i).GetEastWestNeighbor(0) != null) {

				return false;
			}
		}

		return true;

	}

	public Integer GetAlignmentStyle() {

		return this._alignmentStyle;

	}

	public float GetCenter(Integer index) {

		return (this._coord[index] + (0.5f * this._size[index]));

	}

	public TNode GetChild(Integer i) {

		return this._children[i];

	}

	public TNodeIterator GetChildren() {

		return new AnonClass_1(this);

	}

	public TNode GetEastWestNeighbor(Integer i) {
		if (this._ewNeighbors == null) {

			return null;
		}

		return this._ewNeighbors[i];

	}

	public TNode GetFirstRealChild() {
		if (this._firstFreeChildrenSlot <= 0) {

			return null;
		}
		Integer i = 0;
		TNode child = this.GetChild(i);
		while ((i < (this.GetNumberOfChildren() - 1))
				&& this.HasEastWestNeighbor(child)) {

			child = this.GetChild(++i);
		}

		if (this.HasEastWestNeighbor(child)) {

			return null;
		}

		return child;

	}

	public TPath GetFirstVisibleAtBorder(Integer dir, Integer lowerOrHigher) {

		return this._firstVisibleAtBorder[dir][lowerOrHigher];

	}

	public TNode GetImmediateParent() {

		return this._immediateParent;

	}

	public Integer GetIndex() {

		return this._iflag;

	}

	public TNode GetLastChild() {

		return this._children[this._firstFreeChildrenSlot - 1];

	}

	public TNode GetLastRealChild() {
		if (this._firstFreeChildrenSlot <= 0) {

			return null;
		}
		Integer i = this.GetNumberOfChildren() - 1;
		TNode child = this.GetChild(i);
		while ((i > 0) && this.HasEastWestNeighbor(child)) {

			child = this.GetChild(--i);
		}

		if (this.HasEastWestNeighbor(child)) {

			return null;
		}

		return child;

	}

	public TNode GetLastVirtualChild() {
		TNode lastRealChild = this.GetLastRealChild();
		if (lastRealChild != null) {
			while (lastRealChild.GetEastWestNeighbor(1) != null) {

				lastRealChild = lastRealChild.GetEastWestNeighbor(1);
			}
		}

		return lastRealChild;

	}

	public float GetMaxCoord(Integer index) {

		return (this._coord[index] + this._size[index]);

	}

	public float GetMinCoord(Integer index) {

		return this._coord[index];

	}

	public Integer GetNumberOfChildren() {
		if (this._firstFreeChildrenSlot <= 0) {

			return 0;
		}

		return this._firstFreeChildrenSlot;

	}

	public float GetOffsetToRealTopLeft(Integer index) {
		if (this._realBounds == null) {

			return 0f;
		}

		return this._realBounds[index];

	}

	public float GetOffsetToVirtualCenter(Integer side) {
		if (this._offsetToVirtualCenter == null) {

			return 0f;
		}

		return this._offsetToVirtualCenter[side];

	}

	public TNode GetOriginalParent() {

		return this._originalParent;

	}

	public float GetOriginalSize(Integer index) {

		return this._origSize[index];

	}

	public float GetOrthSplitCoord(Integer index) {

		return this._orthSplitCoord[index];

	}

	public TNode GetPrevNode() {

		return this._prevSibling;

	}

	public TNode GetPrevSibling() {

		return this._prevSibling;

	}

	public TNodeIterator GetRealChildren() {

		return new TRealChildrenIterator(this);

	}

	public float GetRealMinCoord(Integer index) {
		if (this._realBounds == null) {

			return this.GetMinCoord(index);
		}

		return (this._coord[index] + this._realBounds[index]);

	}

	public float GetRealSize(Integer index) {
		if (this._realBounds == null) {

			return this.GetSize(index);
		}

		return this._realBounds[2 + index];

	}

	public Integer GetRootNodePreference() {

		return this._rootNodePreference;

	}

	public float GetSize(Integer index) {

		return this._size[index];

	}

	public double GetSortValue() {

		return this._sortValue;

	}

	public float GetSubtreeCoordCorrection(Integer index) {

		return this._subtreeCoordCorrection[index];

	}

	public float GetVirtualCenter(Integer index) {

		if (this.HasDifferentVirtualCenter()) {

			return (this._coord[index] + (0.5f * ((this._size[index] + this._offsetToVirtualCenter[2 * index]) + this._offsetToVirtualCenter[(2 * index) + 1])));
		}

		return this.GetCenter(index);

	}

	public float GetVirtualCenter(Integer index, Boolean topleftside) {

		if (this.HasDifferentVirtualCenter()) {

			return ((this._coord[index] + (0.5f * this._size[index])) + this._offsetToVirtualCenter[(2 * index)
					+ (topleftside ? 0 : 1)]);
		}

		return this.GetCenter(index);

	}

	public TNodeIterator GetVirtualChildren() {

		return new TVirtualChildrenIterator(this);

	}

	private Boolean HasDifferentVirtualCenter() {
		if (this._offsetToVirtualCenter == null) {

			return false;
		}
		if ((this._size[0] == 0.0) && (this._size[1] == 0.0)) {

			return false;
		}

		return true;

	}

	public Boolean HasEastWestNeighbor(TNode node) {
		if (node.GetImmediateParent() != this) {

			return false;
		}
		if (this._ewNeighbors == null) {

			return false;
		}

		return ((this._ewNeighbors[0] == node) || (this._ewNeighbors[1] == node));

	}

	public Boolean HasEastWestNeighbor(Integer i, TNode node) {
		if (node.GetImmediateParent() != this) {

			return false;
		}
		if (this._ewNeighbors == null) {

			return false;
		}

		return (this._ewNeighbors[i] == node);

	}

	public void InitPrevSiblings() {
		TNode node = null;
		TNode eastWestNeighbor = this.GetEastWestNeighbor(0);
		if (eastWestNeighbor != null) {
			this._prevSibling = eastWestNeighbor;
			eastWestNeighbor.InitPrevSiblings();
		}

		eastWestNeighbor = this.GetEastWestNeighbor(1);
		if (eastWestNeighbor != null) {
			eastWestNeighbor._prevSibling = this;
			eastWestNeighbor.InitPrevSiblings();
		}
		TNodeIterator virtualChildren = this.GetVirtualChildren();

		while (virtualChildren.HasNext()) {

			eastWestNeighbor = virtualChildren.Next();
			eastWestNeighbor._prevSibling = node;
			node = eastWestNeighbor;
			eastWestNeighbor.InitPrevSiblings();
		}

	}

	public Boolean IsEastWestNeighbor(Integer i) {
		TNode immediateParent = this.GetImmediateParent();
		if (immediateParent == null) {

			return false;
		}
		if (immediateParent._ewNeighbors == null) {

			return false;
		}

		return (immediateParent._ewNeighbors[i] == this);

	}

	public Boolean IsTipOver() {

		return (this._alignmentStyle == 10);

	}

	public Boolean IsTipOverBothSides() {

		return (this._alignmentStyle == 11);

	}

	private Boolean IsTipOverFromCoord(TGraph graph) {
		Integer dirTowardsLeaves = graph.GetDirTowardsLeaves();
		Boolean flag = true;
		float num2 = 0f;
		float num3 = 0f;
		TNode firstRealChild = this.GetFirstRealChild();
		if (firstRealChild == null) {

			return false;
		}
		float minCoord = firstRealChild.GetMinCoord(0);
		float maxCoord = firstRealChild.GetMaxCoord(0);
		float num6 = firstRealChild.GetMinCoord(1);
		float num7 = firstRealChild.GetMaxCoord(1);
		TNodeIterator realChildren = this.GetRealChildren();

		while (realChildren.HasNext()) {

			firstRealChild = realChildren.Next();
			if (flag) {
				if ((num2 == 0f) && (num3 == 0f)) {

					num2 = firstRealChild.GetMinCoord(dirTowardsLeaves);

					num3 = firstRealChild.GetMaxCoord(dirTowardsLeaves);
				} else {
					if (firstRealChild.GetMinCoord(dirTowardsLeaves) > num3) {
						flag = false;
					} else if (firstRealChild.GetMaxCoord(dirTowardsLeaves) < num2) {
						flag = false;
					}
					if (firstRealChild.GetMinCoord(dirTowardsLeaves) < num2) {

						num2 = firstRealChild.GetMinCoord(dirTowardsLeaves);
					}
					if (firstRealChild.GetMaxCoord(dirTowardsLeaves) > num3) {

						num3 = firstRealChild.GetMaxCoord(dirTowardsLeaves);
					}
				}
			}
			if (firstRealChild.GetMinCoord(0) < minCoord) {

				minCoord = firstRealChild.GetMinCoord(0);
			}
			if (firstRealChild.GetMaxCoord(0) > minCoord) {

				maxCoord = firstRealChild.GetMaxCoord(0);
			}
			if (firstRealChild.GetMinCoord(1) < num6) {

				num6 = firstRealChild.GetMinCoord(1);
			}
			if (firstRealChild.GetMaxCoord(1) > num6) {

				num7 = firstRealChild.GetMaxCoord(1);
			}
		}
		float center = this.GetCenter(0);
		float num9 = this.GetCenter(1);
		if (flag || ((center >= minCoord) && (center <= maxCoord))) {

			return false;
		}
		if (num9 >= num6) {

			return (num9 > num7);
		}

		return true;

	}

	public Boolean IsVisited(Integer visitedLevel) {

		return (this._iflag < visitedLevel);

	}

	public void Mirror(Integer coordinateIndex) {
		this._coord[coordinateIndex] = -this._coord[coordinateIndex]
				- this._size[coordinateIndex];
		this._orthSplitCoord[coordinateIndex] = -this._orthSplitCoord[coordinateIndex];
		this.MirrorOffsetsToVirtualCenter(coordinateIndex);

	}

	public void MirrorOffsetsToVirtualCenter(Integer coordinateIndex) {
		if (this._offsetToVirtualCenter != null) {
			Integer index = 2 * coordinateIndex;
			Integer num2 = 2 - (2 * coordinateIndex);
			this._offsetToVirtualCenter[index] = -this._offsetToVirtualCenter[index];
			this._offsetToVirtualCenter[index + 1] = -this._offsetToVirtualCenter[index + 1];
			float num3 = this._offsetToVirtualCenter[num2];
			this._offsetToVirtualCenter[num2] = this._offsetToVirtualCenter[num2 + 1];
			this._offsetToVirtualCenter[num2 + 1] = num3;
		}

	}

	private double OrthogonalOrder(TGraph graph) {
		float[] numArray = new float[2];
		Integer[] numArray2 = new Integer[2];

		numArray[0] = this.GetMinCoord(0);

		numArray[1] = this.GetMinCoord(1);
		numArray2[0] = 1;
		numArray2[1] = 1;
		Integer flowDirection = graph.GetFlowDirection();
		if (flowDirection == 2) {
			numArray2[0] = -1;

			numArray[0] = this.GetMaxCoord(0);
			// NOTICE: break ignore!!!
		} else if (flowDirection == 3) {
			numArray2[1] = -1;

			numArray[1] = this.GetMaxCoord(1);
			// NOTICE: break ignore!!!
		}

		if (graph.IsLevelLayout()) {
			Integer dirTowardsLeaves = graph.GetDirTowardsLeaves();
			if (graph.GetLevelJustification() == 0) {

				numArray[dirTowardsLeaves] = this.GetCenter(dirTowardsLeaves);
				// NOTICE: break ignore!!!
			} else if (graph.GetLevelJustification() == 1) {
				if (numArray2[dirTowardsLeaves] >= 0) {

					numArray[dirTowardsLeaves] = this
							.GetMaxCoord(dirTowardsLeaves);
				}

				numArray[dirTowardsLeaves] = this.GetMinCoord(dirTowardsLeaves);
				// NOTICE: break ignore!!!
			}
		}
		if (this.GetImmediateParent() != null) {

			if (this.GetImmediateParent().IsTipOverFromCoord(graph)) {
				numArray2[1 - (flowDirection % 2)] = 0;
			} else {
				numArray2[flowDirection % 2] = 0;
			}
		}

		return ((numArray2[0] * numArray[0]) + (numArray2[1] * numArray[1]));

	}

	public void Reinitialize() {
		this._subtreeCoordCorrection[0] = 0f;
		this._subtreeCoordCorrection[1] = 0f;
		this._firstVisibleAtBorder[0][0] = null;
		this._firstVisibleAtBorder[1][0] = null;
		this._firstVisibleAtBorder[0][1] = null;
		this._firstVisibleAtBorder[1][1] = null;

	}

	public void RemoveChild(TNode child) {
		for (Integer i = 0; i < this.GetNumberOfChildren(); i++) {
			if (this.GetChild(i) == child) {
				clr.System.ArrayStaticWrapper.Copy(this._children, i + 1,
						this._children, i,
						(this._firstFreeChildrenSlot - i) - 1);
				this._firstFreeChildrenSlot--;
				this._children[this._firstFreeChildrenSlot] = null;

				return;
			}
		}

	}

	public void RestoreBestAlignmentStyle() {
		this._alignmentStyle = this._bestAlignmentStyle;

	}

	public void RestoreOriginalAlignmentStyle() {
		this._alignmentStyle = this._origAlignmentStyle;

	}

	public void SetAlignmentStyle(Integer alignmentStyle) {
		this._alignmentStyle = alignmentStyle;

	}

	public void SetBackToOriginalSize(Boolean respectNodeSizes) {
		float center = this.GetCenter(0);
		float coord = this.GetCenter(1);
		float originalSize = this.GetOriginalSize(0);
		float size = this.GetOriginalSize(1);
		if (respectNodeSizes) {
			this.SetSize(0, originalSize);
			this.SetSize(1, size);
			this.SetCenter(0, center);
			this.SetCenter(1, coord);
		} else {
			center -= 0.5f * ((originalSize + this.GetOffsetToVirtualCenter(0)) + this
					.GetOffsetToVirtualCenter(1));
			coord -= 0.5f * ((size + this.GetOffsetToVirtualCenter(2)) + this
					.GetOffsetToVirtualCenter(3));
			this.SetSize(0, originalSize);
			this.SetSize(1, size);
			this.SetMinCoord(0, center);
			this.SetMinCoord(1, coord);
		}

	}

	public void SetBoundingBoxes(InternalRect origBBox, InternalRect realBBox,
			float[] offsetToVirtualCenter, Boolean respectNodeSizes,
			Boolean radialLayout) {

		if (!realBBox.equals(origBBox)) {
			this._realBounds = new float[] { realBBox.X - origBBox.X,
					realBBox.Y - origBBox.Y, realBBox.Width, realBBox.Height };
		}
		this._offsetToVirtualCenter = offsetToVirtualCenter;
		if (origBBox.Width >= 0f) {
			this._coord[0] = origBBox.X;
			this._origSize[0] = origBBox.Width;
		} else {
			this._coord[0] = origBBox.X + origBBox.Width;
			this._origSize[0] = -origBBox.Width;
		}
		if (origBBox.Height >= 0f) {
			this._coord[1] = origBBox.Y;
			this._origSize[1] = origBBox.Height;
		} else {
			this._coord[1] = origBBox.Y + origBBox.Height;
			this._origSize[1] = -origBBox.Height;
		}
		if (!respectNodeSizes
				|| ((this._origSize[0] == 0f) && (this._origSize[1] == 0f))) {
			this._coord[0] += 0.5f * ((this.GetOriginalSize(0) + this
					.GetOffsetToVirtualCenter(0)) + this
					.GetOffsetToVirtualCenter(1));
			this._coord[1] += 0.5f * ((this.GetOriginalSize(1) + this
					.GetOffsetToVirtualCenter(2)) + this
					.GetOffsetToVirtualCenter(3));
			this._size[0] = this._size[1] = 0f;
		} else if (radialLayout) {
			double num = this._origSize[0];
			double num2 = this._origSize[1];
			float num3 = (float) Math.Sqrt((num * num) + (num2 * num2));
			this._size[0] = this._size[1] = num3;
		} else {
			this._size[0] = this._origSize[0];
			this._size[1] = this._origSize[1];
		}

	}

	public void SetCenter(Integer index, float coord) {
		this._coord[index] = coord - (0.5f * this._size[index]);

	}

	public void SetChildrenCapacity(Integer maxNumberOfChildren) {
		if (maxNumberOfChildren > 0) {
			this._children = new TNode[maxNumberOfChildren];
			this._firstFreeChildrenSlot = 0;
		} else {
			this._children = null;
			this._firstFreeChildrenSlot = -1;
		}

	}

	public void SetEastWestNeighbor(Integer i, TNode node) {
		if (node == null) {
			if (this._ewNeighbors != null) {
				this._ewNeighbors[i] = null;
			}
		} else {
			if (this._ewNeighbors == null) {
				this._ewNeighbors = new TNode[] { null, null };
			}
			this._ewNeighbors[i] = node;
		}

	}

	public void SetFirstVisibleAtBorder(Integer dir, Integer lowerOrHigher,
			TPath pathCell) {
		this._firstVisibleAtBorder[dir][lowerOrHigher] = pathCell;

	}

	public void SetImmediateParent(TNode parent) {
		this._immediateParent = parent;
		if (this._children != null) {
			this.RemoveChild(parent);
		}

	}

	public void SetIndex(Integer v) {
		this._iflag = v;

	}

	public void SetMaxCoord(Integer index, float coord) {
		this._coord[index] = coord - this._size[index];

	}

	public void SetMinCoord(Integer index, float coord) {
		this._coord[index] = coord;

	}

	public void SetOriginalParent(TNode parent) {
		this._originalParent = parent;
		this.SetImmediateParent(parent);

	}

	public void SetOrthSplitCoord(Integer index, float coord) {
		this._orthSplitCoord[index] = coord;

	}

	public void SetPrevNode(TNode node) {
		this._prevSibling = node;

	}

	public void SetRootNodePreference(Integer x) {
		this._rootNodePreference = x;

	}

	public void SetSize(Integer index, float size) {
		this._size[index] = size;

	}

	public void SetSortValue(double x) {
		this._sortValue = x;

	}

	public void SetSubtreeCoordCorrection(Integer index, float coord) {
		this._subtreeCoordCorrection[index] = coord;

	}

	public void SetVirtualCenter(Integer index, float coord) {

		if (this.HasDifferentVirtualCenter()) {
			this._coord[index] = coord
					- (0.5f * ((this._size[index] + this._offsetToVirtualCenter[2 * index]) + this._offsetToVirtualCenter[(2 * index) + 1]));
		} else {
			this.SetCenter(index, coord);
		}

	}

	public void SetVirtualCenter(Integer index, Boolean topleftside, float coord) {

		if (this.HasDifferentVirtualCenter()) {
			this._coord[index] = (coord - (0.5f * this._size[index]))
					- this._offsetToVirtualCenter[(2 * index)
							+ (topleftside ? 0 : 1)];
		} else {
			this.SetCenter(index, coord);
		}

	}

	public void ShiftCoord(Integer index, float coord) {
		this._coord[index] += coord;
		this._orthSplitCoord[index] += coord;

	}

	public void ShiftSubtreeCoordCorrection(Integer index, float dist) {
		this._subtreeCoordCorrection[index] += dist;

	}

	public void SortChildren(TGraph graph, Boolean initial) {
		if (!graph.GetLayout().get_IncrementalMode()) {
			this.SortChildrenForNonRadial(graph, initial);
		} else if (!initial && this.IsTipOverBothSides()) {
			this.SortChildrenForTipOverBothSides(graph);
		} else if (graph.IsRadialLayout()) {
			this.SortChildrenForRadial(graph, initial);
		} else {
			this.SortChildrenForNonRadial(graph, initial);
		}

	}

	private void SortChildrenForNonRadial(TGraph graph, Boolean initial) {
		if (this.GetNumberOfChildren() > 1) {
			TNodeIterator children = this.GetChildren();

			while (children.HasNext()) {
				TNode node = children.Next();
				node.SetSortValue(node.OrthogonalOrder(graph));
			}
			ChildrenSort sort = new ChildrenSort(this, this, initial);
			sort.Sort();
			sort.Dispose();
		}

	}

	private void SortChildrenForRadial(TGraph graph, Boolean initial) {
		if (this.GetNumberOfChildren() > 1) {
			TNode node2 = null;
			double num3 = 0;
			TNode immediateParent = this;
			while (immediateParent.GetImmediateParent() != null) {

				immediateParent = immediateParent.GetImmediateParent();
			}
			float center = immediateParent.GetCenter(0);
			float y = immediateParent.GetCenter(1);
			TNodeIterator children = this.GetChildren();

			while (children.HasNext()) {

				node2 = children.Next();
				if ((graph.GetFlowDirection() == 1)
						|| (graph.GetFlowDirection() == 2)) {
					node2.SetSortValue(6.2831853071795862 - node2.Angle(center,
							y));
				} else {
					node2.SetSortValue(node2.Angle(center, y));
				}
			}
			ChildrenSort sort = new ChildrenSort(this, this, initial);
			sort.Sort();
			TNode child = this.GetChild(0);
			if (this.GetImmediateParent() == null) {
				Integer dirTowardsLeaves = graph.GetDirTowardsLeaves();
				Boolean flag = graph.GetFlowDirection() < 2;

				center = child.GetCenter(dirTowardsLeaves);
				for (Integer i = 1; i < this.GetNumberOfChildren(); i++) {

					node2 = this.GetChild(i);
					if ((flag && (node2.GetCenter(dirTowardsLeaves) > center))
							|| (!flag && (node2.GetCenter(dirTowardsLeaves) < center))) {

						center = node2.GetCenter(dirTowardsLeaves);
						child = node2;
					}
				}
			} else {
				TNode lastChild = this.GetLastChild();
				double num6 = (child.GetSortValue() + 6.2831853071795862)
						- lastChild.GetSortValue();
				lastChild = child;
				for (Integer j = 1; j < this.GetNumberOfChildren(); j++) {

					node2 = this.GetChild(j);
					num3 = node2.GetSortValue() - lastChild.GetSortValue();
					if (num3 > num6) {
						num6 = num3;
						child = node2;
					}
					lastChild = node2;
				}
			}
			if (child != this.GetChild(0)) {

				num3 = -child.GetSortValue();

				children = this.GetChildren();

				while (children.HasNext()) {

					node2 = children.Next();
					node2.AddSortValue(num3);
					if (node2.GetSortValue() < 0.0) {
						node2.AddSortValue(6.2831853071795862);
					}
				}
				sort.Sort();
			}
			sort.Dispose();
		}

	}

	private void SortChildrenForTipOverBothSides(TGraph graph) {
		if (this.GetNumberOfChildren() > 1) {
			TNode node = null;
			Integer flowDirection = graph.GetFlowDirection();
			Integer dirTowardsLeaves = graph.GetDirTowardsLeaves();
			Integer index = 1 - dirTowardsLeaves;
			float[] numArray = new float[] { (flowDirection == 2) ? -1f : 1f,
					(flowDirection == 3) ? -1f : 1f };
			TNodeIterator children = this.GetChildren();

			while (children.HasNext()) {

				node = children.Next();
				node.SetSortValue((double) (node.GetCenter(dirTowardsLeaves) * numArray[dirTowardsLeaves]));
			}
			ChildrenSort sort = new ChildrenSort(this, this, false);
			sort.Sort();
			sort.Dispose();
			for (Integer i = 0; i < (this._firstFreeChildrenSlot - 1); i += 2) {
				node = this._children[i];
				TNode node2 = this._children[i + 1];
				if ((node.GetCenter(index) * numArray[index]) > (node2
						.GetCenter(index) * numArray[index])) {
					this._children[i] = node2;
					this._children[i + 1] = node;
				}
			}
		}

	}

	public void StoreBestAlignmentStyle() {
		this._bestAlignmentStyle = this._alignmentStyle;

	}

	public void StoreOriginalAlignmentStyle() {
		this._origAlignmentStyle = this._alignmentStyle;

	}

	public void ValidateEastWestNeighbor() {
		TNode eastWestNeighbor = null;
		if (this.GetEastWestNeighbor(0) == this.GetEastWestNeighbor(1)) {
			this.SetEastWestNeighbor(0, null);
		}
		TNodeIterator children = this.GetChildren();

		while (children.HasNext()) {

			eastWestNeighbor = children.Next();

			if (this.IsTipOver()) {
				eastWestNeighbor.SetEastWestNeighbor(0, null);
			} else if (this.IsTipOverBothSides()) {
				eastWestNeighbor.SetEastWestNeighbor(0, null);
				eastWestNeighbor.SetEastWestNeighbor(1, null);
			}
			eastWestNeighbor.ValidateEastWestNeighbor();
		}
		for (Integer i = 0; i <= 1; i++) {

			eastWestNeighbor = this.GetEastWestNeighbor(i);
			if (eastWestNeighbor != null) {
				if (eastWestNeighbor.GetImmediateParent() != this) {
					this.SetEastWestNeighbor(i, null);
				} else if (eastWestNeighbor.GetEastWestNeighbor(1 - i) != null) {
					this.SetEastWestNeighbor(i, null);
				}
			}
		}

	}

	private class AnonClass_1 implements TNodeIterator {
		private TNode __outerThis;

		public Integer count;

		public AnonClass_1(TNode input__outerThis) {
			this.__outerThis = input__outerThis;
			this.count = 0;
		}

		public Boolean HasNext() {

			return (this.count < this.__outerThis._firstFreeChildrenSlot);

		}

		public TNode Next() {

			return this.__outerThis.GetChild(this.count++);

		}

	}

	public class ChildrenSort extends QuickSort {
		public TNode __outerThis;

		public Boolean _initial = false;

		public Integer _numberOfElements;

		public ChildrenSort(TNode input__outerThis, TNode node, Boolean initial) {
			this.__outerThis = input__outerThis;

			this._numberOfElements = node.GetNumberOfChildren();
			this._initial = initial;
		}

		@Override
		public Integer Compare(Integer loc1, Integer loc2) {
			TNode child = this.__outerThis.GetChild(loc1);
			TNode node = this.__outerThis.GetChild(loc2);

			if (this.__outerThis.HasEastWestNeighbor(0, child)) {

				return -1;
			}

			if (!this.__outerThis.HasEastWestNeighbor(0, node)) {

				if (this.__outerThis.HasEastWestNeighbor(1, child)) {
					if (!this._initial) {

						return 1;
					}

					return -1;
				}

				if (this.__outerThis.HasEastWestNeighbor(1, node)) {
					if (!this._initial) {

						return -1;
					}

					return 1;
				}
				if (child.GetSortValue() == node.GetSortValue()) {

					return 0;
				}
				if (child.GetSortValue() < node.GetSortValue()) {

					return -1;
				}
			}

			return 1;

		}

		public void Dispose() {

		}

		public void Sort() {
			super.Sort(this._numberOfElements);

		}

		@Override
		public void Swap(Integer loc1, Integer loc2) {
			TNode child = this.__outerThis.GetChild(loc1);

			this.__outerThis._children[loc1] = this.__outerThis.GetChild(loc2);
			this.__outerThis._children[loc2] = child;

		}

	}

	public class TRealChildrenIterator implements TNodeIterator {
		public TNode __outerThis;

		public Integer count;

		public TRealChildrenIterator(TNode input__outerThis) {
			this.__outerThis = input__outerThis;
			this.count = 0;
			while ((this.count < this.__outerThis._firstFreeChildrenSlot)
					&& this.__outerThis.HasEastWestNeighbor(this.__outerThis
							.GetChild(this.count))) {
				this.count++;
			}
		}

		public Boolean HasNext() {

			return (this.count < this.__outerThis._firstFreeChildrenSlot);

		}

		public TNode Next() {
			TNode child = this.__outerThis.GetChild(this.count++);
			while ((this.count < this.__outerThis._firstFreeChildrenSlot)
					&& this.__outerThis.HasEastWestNeighbor(this.__outerThis
							.GetChild(this.count))) {
				this.count++;
			}

			return child;

		}

	}

	public class TVirtualChildrenIterator implements TNodeIterator {
		public TNode __outerThis;

		public TNode actChild;

		public Integer count;

		public Boolean isRight = false;

		public TVirtualChildrenIterator(TNode input__outerThis) {
			this.__outerThis = input__outerThis;
			this.count = 0;
			this.isRight = false;
			this.actChild = null;

			if (this.MoveCountToRealChild()) {

				this.actChild = this.GetFirstChild(this.count);
			}
		}

		private TNode GetChildAfterActChild(Integer branch) {
			if ((this.actChild == this.__outerThis.GetChild(branch))
					|| this.isRight) {
				this.isRight = true;

				return this.actChild.GetEastWestNeighbor(1);
			}
			TNode child = this.__outerThis.GetChild(branch);
			while (child.GetEastWestNeighbor(0) != this.actChild) {

				child = child.GetEastWestNeighbor(0);
			}

			return child;

		}

		private TNode GetFirstChild(Integer branch) {
			TNode child = this.__outerThis.GetChild(branch);
			while (child.GetEastWestNeighbor(0) != null) {

				child = child.GetEastWestNeighbor(0);
			}
			this.isRight = false;

			return child;

		}

		public Boolean HasNext() {

			return (this.actChild != null);

		}

		private Boolean MoveCountToRealChild() {
			Integer numberOfChildren = this.__outerThis.GetNumberOfChildren();
			while ((this.count < numberOfChildren)
					&& this.__outerThis.HasEastWestNeighbor(this.__outerThis
							.GetChild(this.count))) {
				this.count++;
			}

			return (this.count < numberOfChildren);

		}

		public TNode Next() {
			TNode actChild = this.actChild;

			this.actChild = this.GetChildAfterActChild(this.count);
			if (this.actChild == null) {
				this.count++;

				if (this.MoveCountToRealChild()) {

					this.actChild = this.GetFirstChild(this.count);
				}
			}

			return actChild;

		}

	}
}
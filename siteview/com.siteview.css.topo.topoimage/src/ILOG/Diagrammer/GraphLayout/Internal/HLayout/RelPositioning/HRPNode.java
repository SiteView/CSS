package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.GraphBase.*;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.MakeAcyclic.*;
import system.*;
import system.Collections.*;

public final class HRPNode extends HMANode {
	private java.lang.Object _extension;

	private short _flags;

	private HRPNode[] _neighbors;

	private Integer _numChildren;

	private Integer _numMarkedLeaves;

	private Integer _numRepresentedNodes;

	private HRPNode _parent;

	public Integer BLACK = 1;

	public Integer BLACKWHITE = 3;

	public Integer BLUE = 1;

	public Integer GRAY = 2;

	public Integer NOCOLOR = 5;

	public Integer RED = 3;

	public Integer VIOLET = 2;

	public Integer WHITE = 0;

	public Integer WHITEBLACK = 4;

	public HRPNode(Integer numRepresentedNodes) {
		this._numRepresentedNodes = numRepresentedNodes;
		this._numChildren = 0;
		this._numMarkedLeaves = 0;
		this._parent = null;
		this._neighbors = null;
		this._extension = null;
		this._flags = 0;
		this.SetColor(0);
	}

	public void AllocColorExtension() {
		if (this._extension == null) {
			this._extension = new HRPColorExtension();
		}

	}

	private void AllocOrderExtension() {
		if (this._extension == null) {
			this._extension = new HRPOrderExtension();
		}

	}

	private void AllocSolveExtension() {
		if (this._extension == null) {
			this._extension = new HRPSolveExtension();
		}

	}

	public void AppendToOrder(HRPNode child) {
		HRPSolveExtension solveExtension = this.GetSolveExtension();
		if (solveExtension._firstChildInOrder == null) {
			solveExtension._firstChildInOrder = child;
		} else {
			solveExtension._lastChildInOrder.GetSolveExtension()._nextSiblingInOrder = child;
		}
		solveExtension._lastChildInOrder = child;

	}

	public Boolean CalcFinalColor(Integer numAllMarkedLeaves) {
		if (this.GetColor() == 1) {

			return true;
		}
		HRPColorExtension colorExtension = this.GetColorExtension();
		if (colorExtension == null) {

			return true;
		}
		Integer num = colorExtension._numGrayChildren;
		Integer num2 = colorExtension._numBlackWhiteChildren;
		Integer num3 = colorExtension._numWhiteBlackChildren;
		if (num2 < 2) {
			if (num3 >= 2) {

				return false;
			}
			if (((num + num2) + num3) == 0) {
				this.SetColor(2);

				return true;
			} else if (((num + num2) + num3) == 1) {
				if (num2 != 1) {
					if (num3 == 1) {
						this.SetColor(4);
					} else {
						this.SetColor(2);
					}
				} else {
					this.SetColor(3);
				}

				return true;
			} else if (((num + num2) + num3) == 2) {
				if (numAllMarkedLeaves != this._numMarkedLeaves) {

					return false;
				}
				this.SetColor(2);

				return true;
			}
		}

		return false;

	}

	public void CopyOrdering(HRPNode source) {
		Integer num = source._flags & 3;
		this._flags = (short) ((this._flags & -4) | num);

	}

	public void Dispose() {
		this.SetParent(null);
		this._extension = null;
		this._neighbors = null;

	}

	public void EnsureOrderedNeighborsIfNecessary() {

		if (this.HasOrderedNeighbors()) {
			PropagateToLeft(this.GetLeftNeighbor(), this);
			PropagateToRight(this.GetRightNeighbor(), this);
		}

	}

	public float GetBaryCenter() {
		HRPSolveExtension solveExtension = this.GetSolveExtension();
		if (solveExtension._baryCenterDenomiator == 0f) {

			return 0f;
		}

		return (solveExtension._baryCenterNumerator / solveExtension._baryCenterDenomiator);

	}

	public HRPNode GetBlackSplitNode() {

		return this.GetColorExtension()._blackSplitNode;

	}

	public ArrayList GetBlueChildren() {

		return this.GetOrderExtension()._blueChildren;

	}

	public Integer GetColor() {
		if ((this._flags & 0x1c) == 0) {

			return 0;
		} else if ((this._flags & 0x1c) == 0x10) {

			return 2;
		} else if ((this._flags & 0x1c) == 20) {

			return 4;
		} else if ((this._flags & 0x1c) == 0x18) {

			return 3;
		} else if ((this._flags & 0x1c) == 0x1c) {

			return 1;
		}

		return 5;

	}

	private HRPColorExtension GetColorExtension() {

		return (HRPColorExtension) this._extension;

	}

	public HRPNode GetExtremeNeighbor(Integer index) {
		HRPNode node = this;
		for (HRPNode node2 = this.GetNeighbor(index); node2 != null; node2 = node
				.GetNeighbor(index)) {

			index = node2.GetOppositeNeighborIndex(node);
			node = node2;
		}

		return node;

	}

	public HRPNode GetFirstChildInOrder() {

		return this.GetSolveExtension()._firstChildInOrder;

	}

	public HRPNode GetGrayChainBlackCorner() {

		return this.GetColorExtension()._grayChainBlackCorner;

	}

	public Integer GetIndexOfBlackSideNeighbor() {

		return ((this._flags & 0x20) >> 5);

	}

	private Integer GetIndexOfLeftNeighbor() {

		return (this._flags & 1);

	}

	private Integer GetIndexOfRightNeighbor() {

		return (1 - (this._flags & 1));

	}

	public HRPNode GetLeftmostOrExtremeNeighbor() {

		return this.GetExtremeNeighbor(this.GetIndexOfLeftNeighbor());

	}

	public HRPNode GetLeftNeighbor() {

		if (this.HasOrderedNeighbors()) {

			return this.GetNeighbor(this.GetIndexOfLeftNeighbor());
		}

		return null;

	}

	public HRPNode GetMarkedAncestor() {
		if (this.GetColor() == 1) {

			return this;
		}
		if (this._parent != null) {

			return this._parent.GetMarkedAncestor();
		}

		return null;

	}

	public HRPNode GetNeighbor(Integer index) {
		if (this._neighbors == null) {

			return null;
		}

		return this._neighbors[index];

	}

	public HRPNodeIterator GetNeighborChain() {

		return new AnonClass_1(this);

	}

	public Integer GetNeighborIndex(HRPNode node) {
		if (this._neighbors == null) {
			if (node == null) {

				return 0;
			}

			return -1;
		}
		if (this._neighbors[0] == node) {

			return 0;
		}
		if (this._neighbors[1] == node) {

			return 1;
		}

		return -1;

	}

	public Integer GetNumberOfBlackChildren() {
		HRPColorExtension colorExtension = this.GetColorExtension();
		if (colorExtension != null) {

			return colorExtension._numBlackChildren;
		}

		return 0;

	}

	public Integer GetNumberOfChildren() {

		return this._numChildren;

	}

	public Integer GetNumberOfColorContributingMarkedLeaves() {
		if ((this._numChildren == 0) && (this.GetColor() == 1)) {

			return this._numRepresentedNodes;
		}
		HRPColorExtension colorExtension = this.GetColorExtension();
		if (colorExtension == null) {

			return 0;
		}

		return colorExtension._numColorContributingMarkedLeaves;

	}

	public Integer GetNumberOfGraytoneChildren() {
		HRPColorExtension colorExtension = this.GetColorExtension();
		if (colorExtension != null) {

			return ((colorExtension._numGrayChildren + colorExtension._numBlackWhiteChildren) + colorExtension._numWhiteBlackChildren);
		}

		return 0;

	}

	public Integer GetNumberOfMarkedLeaves() {

		return this._numMarkedLeaves;

	}

	public Integer GetNumberOfOrderingChildren() {
		HRPOrderExtension orderExtension = this.GetOrderExtension();
		if (orderExtension == null) {

			return 0;
		}

		return ((orderExtension._blueChildren.get_Count() + orderExtension._redChildren
				.get_Count()) + orderExtension._violetChildren.get_Count());

	}

	public Integer GetNumberOfRepresentedNodes() {

		return this._numRepresentedNodes;

	}

	public Integer GetNumberOfWhiteChildren() {
		if (this.GetColor() == 0) {

			return this._numChildren;
		}
		HRPColorExtension colorExtension = this.GetColorExtension();
		if (colorExtension != null) {

			return ((((this._numChildren - colorExtension._numBlackChildren) - colorExtension._numGrayChildren) - colorExtension._numBlackWhiteChildren) - colorExtension._numWhiteBlackChildren);
		}

		return 0;

	}

	public HRPNode GetOppositeNeighbor(HRPNode node) {

		return this.GetNeighbor(this.GetOppositeNeighborIndex(node));

	}

	public Integer GetOppositeNeighborIndex(HRPNode node) {
		if (this.GetNeighbor(0) == node) {

			return 1;
		}

		return 0;

	}

	private HRPOrderExtension GetOrderExtension() {

		return (HRPOrderExtension) this._extension;

	}

	public HMAEdgeIterator GetOutEdges() {

		return new HRPOutEdgeIterator(this);

	}

	public HRPNode GetParent() {

		return this._parent;

	}

	public Integer GetPositionNumber() {

		return super.GetOrderingNumber();

	}

	public ArrayList GetRedChildren() {

		return this.GetOrderExtension()._redChildren;

	}

	public HRPNode GetRightNeighbor() {

		if (this.HasOrderedNeighbors()) {

			return this.GetNeighbor(1 - this.GetIndexOfLeftNeighbor());
		}

		return null;

	}

	public HRPNode GetSiblingInOrder() {

		return this.GetSolveExtension()._nextSiblingInOrder;

	}

	private HRPSolveExtension GetSolveExtension() {

		return (HRPSolveExtension) this._extension;

	}

	public ArrayList GetVioletChildren() {

		return this.GetOrderExtension()._violetChildren;

	}

	public Boolean HasOrderedNeighbors() {

		return ((this._flags & 2) != 0);

	}

	public void Init() {
		this.AllocSolveExtension();
		HRPSolveExtension solveExtension = this.GetSolveExtension();
		solveExtension._numSolvingChildren = 0;
		solveExtension._baryCenterNumerator = 0f;
		solveExtension._baryCenterDenomiator = 0f;
		solveExtension._nextSiblingInOrder = null;
		solveExtension._firstChildInOrder = null;
		solveExtension._lastChildInOrder = null;

	}

	private Boolean IsChainedNeighbor(HRPNode node, HRPNode a, Integer index) {
		for (HRPNode node2 = a.GetNeighbor(index); node2 != null; node2 = node2
				.GetNeighbor(index)) {
			if (node == node2) {

				return true;
			}

			index = node2.GetOppositeNeighborIndex(a);
			a = node2;
		}

		return false;

	}

	public Boolean IsCompletelyColored() {
		HRPColorExtension colorExtension = this.GetColorExtension();

		return ((colorExtension == null) || (colorExtension._numColorContributingMarkedLeaves == this._numMarkedLeaves));

	}

	public Boolean IsNeighbored() {
		if (this.GetNeighbor(0) == null) {

			return (this.GetNeighbor(1) != null);
		}

		return true;

	}

	public Boolean IsThisOrChainedNeighbor(HRPNode node) {

		return ((node == this) || (this.IsChainedNeighbor(node, this, 0) || this
				.IsChainedNeighbor(node, this, 1)));

	}

	public void Mark() {
		this.SetColor(1);
		if (this._parent != null) {
			this._parent.Mark();
		}

	}

	public void MarkForColoring() {
		for (HRPNode node = this; node != null; node = node.GetParent()) {
			node._numMarkedLeaves++;
			if (node._numMarkedLeaves == node._numRepresentedNodes) {
				node.SetColor(1);
			}
			node.AllocColorExtension();
		}

	}

	public void MarkForOrdering(Integer orderingColor, Boolean newColoredChild) {
		if (this.GetColor() != orderingColor) {
			this.AllocOrderExtension();
			if (newColoredChild) {
				HRPOrderExtension orderExtension = this.GetOrderExtension();
				orderExtension._numOrderingChildren++;
			}
			if (this.GetColor() == 0) {
				newColoredChild = true;
			} else {
				newColoredChild = false;
				orderingColor = 2;
			}
			this.SetColor(orderingColor);
			HRPNode parent = this.GetParent();
			if (parent != null) {
				parent.MarkForOrdering(orderingColor, newColoredChild);
			}
		}

	}

	private static void PropagateToLeft(HRPNode leftNode, HRPNode rightNode) {
		while (leftNode != null) {
			Integer oppositeNeighborIndex = leftNode
					.GetOppositeNeighborIndex(rightNode);
			if (leftNode.HasOrderedNeighbors()
					&& (leftNode.GetIndexOfLeftNeighbor() == oppositeNeighborIndex)) {

				return;
			}
			leftNode._flags = (short) ((leftNode._flags & -4) | (2 | oppositeNeighborIndex));
			rightNode = leftNode;

			leftNode = leftNode.GetNeighbor(oppositeNeighborIndex);
		}

	}

	private static void PropagateToRight(HRPNode rightNode, HRPNode leftNode) {
		while (rightNode != null) {
			Integer neighborIndex = rightNode.GetNeighborIndex(leftNode);
			if (rightNode.HasOrderedNeighbors()
					&& (rightNode.GetIndexOfLeftNeighbor() == neighborIndex)) {

				return;
			}
			rightNode._flags = (short) ((rightNode._flags & -4) | (2 | neighborIndex));
			leftNode = rightNode;

			rightNode = rightNode.GetNeighbor(1 - neighborIndex);
		}

	}

	public void ReplaceNeighbor(HRPNode oldNeighbor, HRPNode newNeighbor) {
		Integer neighborIndex = this.GetNeighborIndex(oldNeighbor);
		this.SetNeighbor(neighborIndex, newNeighbor);

	}

	public void SetBarycenter(float a, float b) {
		HRPSolveExtension solveExtension = this.GetSolveExtension();
		solveExtension._baryCenterNumerator = a;
		solveExtension._baryCenterDenomiator = b;
		if (this._parent != null) {
			this._parent.UpdateBarycenterFromChild(this);
		}

	}

	public void SetBlackSplitNode(HRPNode blackSplitNode) {
		this.GetColorExtension()._blackSplitNode = blackSplitNode;

	}

	public void SetColor(Integer color) {
		if (color == 0) {
			this._flags = (short) (this._flags & -29);

			return;
		} else if (color == 1) {
			this._flags = (short) (this._flags | 0x1c);

			return;
		} else if (color == 2) {
			this._flags = (short) ((this._flags & -29) | 0x10);

			return;
		} else if (color == 3) {
			this._flags = (short) ((this._flags & -29) | 0x18);
			this.SetIndexOfBlackSideNeighbor(this.GetIndexOfLeftNeighbor());

			return;
		} else if (color == 4) {
			this._flags = (short) ((this._flags & -29) | 20);
			this.SetIndexOfBlackSideNeighbor(this.GetIndexOfRightNeighbor());

			return;
		}

	}

	public void SetGrayChainBlackCorner(HRPNode blackcorner) {
		this.GetColorExtension()._grayChainBlackCorner = blackcorner;

	}

	public void SetIndexOfBlackSideNeighbor(Integer i) {
		this._flags = (short) ((this._flags & -33) | (i << 5));

	}

	public void SetLeftNeighbor(HRPNode leftNode) {
		Integer num = null;
		if (this._neighbors == null) {
			this._neighbors = new HRPNode[2];
		}
		if (this._neighbors[0] == leftNode) {
			num = 0;
		} else if (this._neighbors[1] == leftNode) {
			num = 1;
		} else {
			num = 0;
			if (this._neighbors[num] != null) {
				num++;
			}
			this._neighbors[num] = leftNode;
		}
		this._flags = (short) ((this._flags & -4) | (2 | num));
		PropagateToLeft(this._neighbors[num], this);
		PropagateToRight(this._neighbors[1 - num], this);

	}

	public void SetNeighbor(HRPNode node) {
		if (this._neighbors == null) {
			this._neighbors = new HRPNode[2];
		}
		if ((this._neighbors[0] != node) && (this._neighbors[1] != node)) {
			if (this._neighbors[0] == null) {
				this._neighbors[0] = node;
			} else {
				this._neighbors[1] = node;
			}
		}

	}

	public void SetNeighbor(Integer i, HRPNode node) {
		if (this._neighbors == null) {
			this._neighbors = new HRPNode[2];
		}
		this._neighbors[i] = node;

	}

	public void SetParent(HRPNode newParent) {
		if (this._parent != newParent) {
			if (newParent != null) {
				newParent._numChildren++;
				newParent.UpdateExtensionOnAddChild(this);
			}
			if (this._parent != null) {
				this._parent._numChildren--;
				this._parent.UpdateExtensionOnRemoveChild(this);
			}
			this._parent = newParent;
		}

	}

	public void SetPositionNumber(Integer number) {
		super.SetOrderingNumber(number);

	}

	public void SetRightNeighbor(HRPNode rightNode) {
		Integer num = null;
		if (this._neighbors == null) {
			this._neighbors = new HRPNode[2];
		}
		if (this._neighbors[0] == rightNode) {
			num = 0;
		} else if (this._neighbors[1] == rightNode) {
			num = 1;
		} else {
			num = 0;
			if (this._neighbors[num] != null) {
				num++;
			}
			this._neighbors[num] = rightNode;
		}
		this._flags = (short) ((this._flags & -4) | (2 | (1 - num)));
		PropagateToLeft(this._neighbors[1 - num], this);
		PropagateToRight(this._neighbors[num], this);

	}

	public void Unmark() {
		this.SetColor(0);
		if (this._parent != null) {
			this._parent.Unmark();
		}

	}

	public void UnmarkForColoring() {
		for (HRPNode node = this; node != null; node = node.GetParent()) {
			if (((node.GetColor() == 0) && (node._extension == null))
					&& (node._numMarkedLeaves == 0)) {

				return;
			}
			node._extension = null;
			node._numMarkedLeaves = 0;
			node.SetColor(0);
		}

	}

	public void UnmarkForOrdering() {
		this._extension = null;
		this.SetColor(0);

	}

	private void UpdateBarycenterFromChild(HRPNode child) {
		HRPSolveExtension solveExtension = child.GetSolveExtension();
		float num = solveExtension._baryCenterNumerator;
		float num2 = solveExtension._baryCenterDenomiator;
		HRPSolveExtension extension2 = this.GetSolveExtension();
		extension2._baryCenterNumerator += num;
		extension2._baryCenterDenomiator += num2;
		extension2._numSolvingChildren++;
		if (this.GetNumberOfChildren() == extension2._numSolvingChildren) {
			HRPNode parent = this.GetParent();
			if (parent != null) {
				parent.UpdateBarycenterFromChild(this);
			}
		}

	}

	private void UpdateColorCountOnAddChild(Integer childcolor) {
		HRPColorExtension colorExtension = this.GetColorExtension();
		if (childcolor == 1) {
			colorExtension._numBlackChildren++;

			return;
		} else if (childcolor == 2) {
			colorExtension._numGrayChildren++;

			return;
		} else if (childcolor == 3) {
			colorExtension._numBlackWhiteChildren++;

			return;
		} else if (childcolor == 4) {
			colorExtension._numWhiteBlackChildren++;

			return;
		}

	}

	private void UpdateColorCountOnRemoveChild(Integer childcolor) {
		HRPColorExtension colorExtension = this.GetColorExtension();
		if (childcolor == 1) {
			colorExtension._numBlackChildren--;

			return;
		} else if (childcolor == 2) {
			colorExtension._numGrayChildren--;

			return;
		} else if (childcolor == 3) {
			colorExtension._numBlackWhiteChildren--;

			return;
		} else if (childcolor == 4) {
			colorExtension._numWhiteBlackChildren--;

			return;
		}

	}

	public void UpdateColorInfoFromChild(HRPNode child) {
		HRPColorExtension colorExtension = this.GetColorExtension();
		colorExtension._numColorContributingMarkedLeaves += child
				.GetNumberOfColorContributingMarkedLeaves();
		this.UpdateColorCountOnAddChild(child.GetColor());

	}

	private void UpdateExtensionOnAddChild(HRPNode child) {
		if ((this._extension != null)
				&& (this._extension instanceof HRPColorExtension)) {
			this.UpdateColorCountOnAddChild(child.GetColor());
		}

	}

	private void UpdateExtensionOnRemoveChild(HRPNode child) {
		if ((this._extension != null)
				&& (this._extension instanceof HRPColorExtension)) {
			this.UpdateColorCountOnRemoveChild(child.GetColor());
		}
		Integer color = this.GetColor();
		if (((color != 1) && (color != 0))
				&& ((this.GetNumberOfGraytoneChildren() == 0) && (this
						.GetNumberOfBlackChildren() == 0))) {
			HRPNode parent = this.GetParent();
			if (parent != null) {
				parent.UpdateExtensionOnRemoveChild(this);
			}
			this._extension = null;
			this.SetColor(0);
			if (parent != null) {
				parent.UpdateExtensionOnAddChild(this);
			}
		}

	}

	public void UpdateMarkedInfoOnAdd(HRPNode child) {
		this._numRepresentedNodes += child._numRepresentedNodes;
		this._numMarkedLeaves += child._numMarkedLeaves;

	}

	public void UpdateMarkedInfoOnRemove(HRPNode child) {
		this._numRepresentedNodes -= child._numRepresentedNodes;
		this._numMarkedLeaves -= child._numMarkedLeaves;

	}

	public void UpdateOrderingInfoFromChild(HRPNode child) {
		HRPOrderExtension orderExtension = this.GetOrderExtension();
		if (orderExtension != null) {
			if (child.GetColor() == 1) {
				orderExtension._blueChildren.Add(child);
				// NOTICE: break ignore!!!
			} else if (child.GetColor() == 2) {
				orderExtension._violetChildren.Add(child);
				// NOTICE: break ignore!!!
			} else if (child.GetColor() == 3) {
				orderExtension._redChildren.Add(child);
				// NOTICE: break ignore!!!
			}
			if (this.GetNumberOfOrderingChildren() == orderExtension._numOrderingChildren) {
				HRPNode parent = this.GetParent();
				if (parent != null) {
					parent.UpdateOrderingInfoFromChild(this);
				}
			}
		}

	}

	private class AnonClass_1 implements HRPNodeIterator {
		private HRPNode __outerThis;

		public HRPNode next;

		public HRPNode prev;

		public AnonClass_1(HRPNode input__outerThis) {
			this.__outerThis = input__outerThis;
			this.next = this.__outerThis;
			this.prev = (this.__outerThis.GetNeighborIndex(null) != -1) ? null
					: this.__outerThis.GetNeighbor(this.__outerThis
							.GetIndexOfLeftNeighbor());
		}

		public Boolean HasNext() {

			return (this.next != null);

		}

		public Boolean HasPrev() {

			return (this.prev != null);

		}

		public void Init(HTBaseNode node) {
			this.next = (HRPNode) node;
			if (this.next == null) {
				this.prev = null;
			} else {
				this.prev = (this.next.GetNeighborIndex(null) != -1) ? null
						: this.next.GetNeighbor(this.next
								.GetIndexOfLeftNeighbor());
			}

		}

		public HRPNode Next() {
			Integer oppositeNeighborIndex = this.next
					.GetOppositeNeighborIndex(this.prev);
			this.prev = this.next;

			this.next = this.next.GetNeighbor(oppositeNeighborIndex);

			return this.prev;

		}

		public HRPNode Prev() {
			Integer oppositeNeighborIndex = this.prev
					.GetOppositeNeighborIndex(this.next);
			this.next = this.prev;

			this.prev = this.prev.GetNeighbor(oppositeNeighborIndex);

			return this.next;

		}

	}
}
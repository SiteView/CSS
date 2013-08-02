package ILOG.Diagrammer.GraphLayout.Internal.HLayout.RelPositioning;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Collections.*;

public final class HRPColoring {
	public HRPGraph _graph;

	public Integer _numMarkedLeaves;

	private Integer BLACK = 1;

	private Integer BLACKWHITE = 3;

	private Integer GRAY = 2;

	private Integer GRAY_EXPECT_BLACK = 0x65;

	private Integer GRAY_EXPECT_WHITE = 100;

	private Integer NOCOLOR = 5;

	private Integer WHITE = 0;

	private Integer WHITEBLACK = 4;

	public HRPColoring(HRPGraph graph) {
		this.Init(graph);
	}

	private Boolean Color(HRPNode node) {

		if (!node.CalcFinalColor(this._numMarkedLeaves)) {

			return false;
		}

		return this.PropagateColor(node);

	}

	private Integer GetNeighborChainColor(HRPNode node) {

		node = node.GetLeftmostOrExtremeNeighbor();
		HRPNode node2 = null;
		Integer i = 0;
		Integer neighborIndex = node.GetNeighborIndex(null);
		Boolean flag = node.HasOrderedNeighbors();
		Integer num3 = 5;
		Integer num5 = 0;
		while (node != null) {
			Integer color = node.GetColor();

			num5 += node.GetNumberOfMarkedLeaves();

			neighborIndex = node.GetNeighborIndex(node2);
			if (num3 == 0) {
				if (color == 0) {
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				} else if (color == 1) {
					num3 = flag ? 4 : 0x65;
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				} else if (color == 2) {
					node.SetIndexOfBlackSideNeighbor(1 - neighborIndex);
					num3 = flag ? 4 : 0x65;
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				} else if (color == 3) {
					return 5;
				} else if (color == 4) {
					num3 = 4;
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				}
				return 5;
			} else if (num3 == 1) {
				if (color == 0) {
					num3 = flag ? 3 : 100;
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				} else if (color == 1) {
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				} else if (color == 3) {
					num3 = 3;
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				}

				return 5;
			} else if (num3 == 2) {
				if (color == 0) {
					node2.SetIndexOfBlackSideNeighbor(1 - i);
					num3 = flag ? 3 : 100;
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				} else if (color == 1) {
					node2.SetIndexOfBlackSideNeighbor(i);
					num3 = flag ? 4 : 0x65;
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				} else if (color == 2 || color == 3) {
					if (num5 == this._numMarkedLeaves) {
						node2.SetIndexOfBlackSideNeighbor(i);
						node.SetIndexOfBlackSideNeighbor(neighborIndex);
						return 2;
					}
					return 5;
				}

				return 5;
			} else if (num3 == 3 || num3 == 100) {
				if (color == 0) {
					num3 = flag ? 3 : 100;
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				}

				return 5;
			} else if (num3 == 4 || num3 == 0x65) {
				if (color == 0 || color == 2 || color == 3) {
					if (num5 != this._numMarkedLeaves) {

						return 5;
					}
					node.SetIndexOfBlackSideNeighbor(neighborIndex);

					return 2;
				} else if (color == 1) {
					num3 = flag ? 4 : 0x65;
					node2 = node;
					i = 1 - neighborIndex;
					node = node.GetNeighbor(i);
					continue;
				}

				return 5;
			} else if (num3 == 5) {
				num3 = color;
				node2 = node;
				i = 1 - neighborIndex;
				node = node.GetNeighbor(i);
				continue;
			} else {

				return 5;
			}

		}
		if ((num3 != 100) && (num3 != 0x65)) {

			return num3;
		}

		return 2;

	}

	public void Init(HRPGraph graph) {
		this._graph = graph;
		this._numMarkedLeaves = 0;

	}

	private Boolean IsGroupComplete(HRPNode node) {

		return (node.GetNumberOfMarkedLeaves() == this._numMarkedLeaves);

	}

	private Boolean PropagateColor(HRPNode node) {

		if (this.IsGroupComplete(node)) {

			return true;
		}
		HRPNode parent = node.GetParent();
		if (parent == null) {

			return true;
		}
		parent.UpdateColorInfoFromChild(node);

		if (!parent.IsCompletelyColored()) {

			return true;
		}

		if (!node.IsNeighbored()) {

			return this.Color(parent);
		}
		Integer neighborChainColor = this.GetNeighborChainColor(node);
		if (neighborChainColor == 5) {

			return false;
		}
		parent.SetColor(neighborChainColor);

		return this.PropagateColor(parent);

	}

	public Boolean Run(ArrayList nodes) {
		Boolean flag = true;
		if (nodes.get_Count() > 1) {
			this._numMarkedLeaves = nodes.get_Count();
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(nodes);

			while (enumerator.HasMoreElements()) {
				((HRPNode) enumerator.NextElement()).MarkForColoring();
			}

			enumerator = TranslateUtil.Collection2JavaStyleEnum(nodes);
			while (flag && enumerator.HasMoreElements()) {

				flag = this.Color((HRPNode) enumerator.NextElement());
			}
			if (flag) {

				return flag;
			}

			enumerator = TranslateUtil.Collection2JavaStyleEnum(nodes);

			while (enumerator.HasMoreElements()) {
				((HRPNode) enumerator.NextElement()).UnmarkForColoring();
			}
		}

		return flag;

	}

}
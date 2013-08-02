package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class CrossingReductionSwapAlgorithm extends HLevelAlgorithm {
	public CrossingReductionSwapAlgorithm() {
	}

	private void CheckAndSwap(HLevel level) {
		if (level.GetConstraintNetwork() == null) {
			Integer nodesCount = level.GetNodesCount();
			Boolean flag = true;
			while (flag) {
				flag = false;
				for (Integer i = 0; i < (nodesCount - 1); i++) {
					HNode node = level.GetNode(i);
					HNode node2 = level.GetNode(i + 1);

					if (this.NeedsSwap(node, node2)) {
						flag = true;
						Swap(node, node2);
					}
				}
			}
		}

	}

	@Override
	public void Clean() {
		super.Clean();

	}

	@Override
	public void Init(HLevel upperLevel, HLevel lowerLevel) {
		super.Init(upperLevel, lowerLevel);

	}

	public Boolean NeedsSwap(HNode node1, HNode node2) {
		HSegmentIterator segmentsTo = null;
		Integer positionInLevel = null;
		Integer num4 = null;
		if (node1.GetSpecPositionInLevel() >= 0) {

			return false;
		}
		if (node2.GetSpecPositionInLevel() >= 0) {

			return false;
		}
		if (node1.GetSwimLane() != node2.GetSwimLane()) {

			return false;
		}
		Integer num = 0;
		Integer num2 = 0;
		HSegmentIterator segmentsFrom = node1.GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {

			positionInLevel = segmentsFrom.Next().GetTo().GetPositionInLevel();

			segmentsTo = node2.GetSegmentsFrom();

			while (segmentsTo.HasNext()) {

				num4 = segmentsTo.Next().GetTo().GetPositionInLevel();
				if (num4 < positionInLevel) {
					num++;
				}
				if (num4 > positionInLevel) {
					num2++;
				}
			}
		}

		segmentsFrom = node1.GetSegmentsTo();

		while (segmentsFrom.HasNext()) {

			positionInLevel = segmentsFrom.Next().GetFrom()
					.GetPositionInLevel();

			segmentsTo = node2.GetSegmentsTo();

			while (segmentsTo.HasNext()) {

				num4 = segmentsTo.Next().GetFrom().GetPositionInLevel();
				if (num4 < positionInLevel) {
					num++;
				}
				if (num4 > positionInLevel) {
					num2++;
				}
			}
		}

		return (num > num2);

	}

	@Override
	public void Run() {
		HLevel upperLevel = super.GetUpperLevel();
		HLevel lowerLevel = super.GetLowerLevel();
		if ((upperLevel.GetNumberOfCrossings() > 0)
				&& (super.GetGraph().GetFirstLevel() == upperLevel)) {
			this.CheckAndSwap(upperLevel);
		}
		if ((upperLevel.GetNumberOfCrossings() > 0)
				|| (lowerLevel.GetNumberOfCrossings() > 0)) {
			this.CheckAndSwap(lowerLevel);
		}

	}

	public static void Swap(HNode node1, HNode node2) {
		HNode[] nodesField = node1.GetLevel().GetNodesField();
		Integer positionInLevel = node1.GetPositionInLevel();
		Integer index = node2.GetPositionInLevel();
		nodesField[positionInLevel] = node2;
		nodesField[index] = node1;
		node1.SetPositionInLevel(index);
		node2.SetPositionInLevel(positionInLevel);

	}

}
package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class EastWestExpansionAlgorithm extends HLevelSweepAlgorithm {
	private HLevel _actLevel;

	private HLevel _nextLevel;

	private HNodeSort _nodeSortAlg;

	private Integer _numberAuxNodes;

	private Integer _numberAuxNodesInActLevel;

	private HLevel _prevLevel;

	private Integer HAS_BP_LINK_TO_EAST = 1;

	private Integer HAS_BP_LINK_TO_WEST = 2;

	private Integer HAS_EW_LINK_TO_EAST = 4;

	private Integer HAS_EW_LINK_TO_WEST = 8;

	public EastWestExpansionAlgorithm(HGraph graph) {
		this.Init(graph);
		this._nodeSortAlg = new HNodeSort();
	}

	@Override
	public void Clean() {
		super.Clean();
		this._nodeSortAlg = null;

	}

	public void CreateAuxNode(HNode node) {
		node.SetSortValue((float) node.GetPositionInLevel());

		if (!node.IsDummyNode()) {
			Boolean flag = this.NeedsEastPortAuxNode(node);
			Boolean flag2 = this.NeedsWestPortAuxNode(node);
			HNode node2 = null;
			HNode node3 = null;
			if (flag) {

				node2 = node.CreateEastPortAuxNode();
				this._actLevel.Add(node2);
			}
			if (flag2) {

				node3 = node.CreateWestPortAuxNode();
				this._actLevel.Add(node3);
			}
			if ((node2 != null) || (node3 != null)) {
				HSegment segment = null;
				Integer fromPortSide = null;
				HSegmentIterator segmentsFrom = node.GetSegmentsFrom();

				while (segmentsFrom.HasNext()) {

					segment = segmentsFrom.Next();

					fromPortSide = segment.GetFromPortSide();
					if ((fromPortSide == 1) && (node2 != null)) {
						node.FromRemove(segment);
						segment.SetFrom(node2);
						node2.FromAdd(segment, null);
						segment.SetFromPortSide(2);
					} else if ((fromPortSide == 3) && (node3 != null)) {
						node.FromRemove(segment);
						segment.SetFrom(node3);
						node3.FromAdd(segment, null);
						segment.SetFromPortSide(2);
					}
				}

				segmentsFrom = node.GetSegmentsTo();

				while (segmentsFrom.HasNext()) {

					segment = segmentsFrom.Next();

					fromPortSide = segment.GetToPortSide();
					if ((fromPortSide == 1) && (node2 != null)) {
						node.ToRemove(segment);
						segment.SetTo(node2);
						node2.ToAdd(segment, null);
						segment.SetToPortSide(0);
					} else if ((fromPortSide == 3) && (node3 != null)) {
						node.ToRemove(segment);
						segment.SetTo(node3);
						node3.ToAdd(segment, null);
						segment.SetToPortSide(0);
					}
				}
				if (node2 != null) {
					node2.CorrectEastWestAuxSize();
				}
				if (node3 != null) {
					node3.CorrectEastWestAuxSize();
				}
			}
		}

	}

	private Boolean HasEastWestLinkToEast(HNode node) {

		return ((node.GetMarker() & 4) != 0);

	}

	private Boolean HasEastWestLinkToWest(HNode node) {

		return ((node.GetMarker() & 8) != 0);

	}

	private Boolean HasNormalLinkToEast(HNode node) {

		return ((node.GetMarker() & 1) != 0);

	}

	private Boolean HasNormalLinkToWest(HNode node) {

		return ((node.GetMarker() & 2) != 0);

	}

	@Override
	public void InitBackwardFirstLevel(HLevel level) {
		this._nextLevel = null;
		this._actLevel = level;
		Integer numID = level.GetNumID() - 1;
		if (numID >= 0) {

			this._prevLevel = super.GetGraph().GetLevel(numID);
		} else {
			this._prevLevel = null;
		}
		this.TreatBackwardLevel();

	}

	@Override
	public void InitForwardFirstLevel(HLevel level) {
		this._prevLevel = null;
		this._actLevel = level;
		Integer numID = level.GetNumID() + 1;
		if (super.GetGraph().GetNumberOfLevels() > numID) {

			this._nextLevel = super.GetGraph().GetLevel(numID);
		} else {
			this._nextLevel = null;
		}
		this.TreatForwardLevel();

	}

	private Boolean IsEastWestLink(HLink link) {
		HNode from = link.GetFrom();
		HNode to = link.GetTo();
		if (from.GetLevelNumber() != to.GetLevelNumber()) {

			return false;
		}
		Integer num = from.GetPositionInLevel() - to.GetPositionInLevel();
		if ((num != 1) && (num != -1)) {

			return false;
		}

		if (from.IsEastWestPortAuxNode()) {

			return false;
		}

		if (to.IsEastWestPortAuxNode()) {

			return false;
		}

		if (link.IsFromPortNumberSpecified()) {

			return false;
		}

		if (link.IsToPortNumberSpecified()) {

			return false;
		}
		Integer num2 = (num < 0) ? link.GetOrigFromPortSide() : link
				.GetOrigToPortSide();
		Integer num3 = (num < 0) ? link.GetOrigToPortSide() : link
				.GetOrigFromPortSide();
		if (((num2 != -1) && (num2 != 1)) && (num2 != -3)) {

			return false;
		}
		if (((num3 != -1) && (num3 != 3)) && (num3 != -3)) {

			return false;
		}

		return true;

	}

	private void MarkHasEastWestLinkToEast(HNode node) {
		node.SetMarker(node.GetMarker() | 4);

	}

	private void MarkHasEastWestLinkToWest(HNode node) {
		node.SetMarker(node.GetMarker() | 8);

	}

	private void MarkHasNormalLinkToEast(HNode node) {
		node.SetMarker(node.GetMarker() | 1);

	}

	private void MarkHasNormalLinkToWest(HNode node) {
		node.SetMarker(node.GetMarker() | 2);

	}

	private Boolean NeedsEastPortAuxNode(HNode node) {

		if (this.HasNormalLinkToEast(node)) {

			return true;
		}

		if (this.HasEastWestLinkToEast(node)) {
			HNode node2 = node.GetLevel()
					.GetNode(node.GetPositionInLevel() + 1);

			if (node2.IsEastWestPortAuxNode()) {

				return true;
			}

			if (this.HasNormalLinkToWest(node2)) {

				return true;
			}
		}

		return false;

	}

	private Boolean NeedsWestPortAuxNode(HNode node) {

		if (this.HasNormalLinkToWest(node)) {

			return true;
		}

		if (this.HasEastWestLinkToWest(node)) {
			HNode node2 = node.GetLevel()
					.GetNode(node.GetPositionInLevel() - 1);

			if (node2.IsEastWestPortAuxNode()) {

				return true;
			}

			if (this.HasNormalLinkToEast(node2)) {

				return true;
			}
		}

		return false;

	}

	private void ResolveEastWestPortSides(HNode node) {
		node.SetMarker(0);

		if (!node.IsDummyNode()) {
			HSegment segment = null;
			Integer fromPortSide = null;
			float priority = 0;
			HSegmentIterator segmentsFrom = node.GetSegmentsFrom();
			node.CalcSuccBaryCenter(0);
			float sortValue = node.GetSortValue();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();

				fromPortSide = segment.GetFromPortSide();
				if (fromPortSide == -3) {

					priority = segment.GetPriority();
					if ((segment.GetTo().GetPositionInLevel() * priority) < sortValue) {
						fromPortSide = 3;
					} else {
						fromPortSide = 1;
					}
					segment.SetFromPortSide(fromPortSide);
				}
				if (fromPortSide == 1) {

					if (this.IsEastWestLink(segment.GetOwnerLink())) {
						this.MarkHasEastWestLinkToEast(node);
					} else {
						this.MarkHasNormalLinkToEast(node);
					}
					continue;
				} else if (fromPortSide == 3) {

					if (this.IsEastWestLink(segment.GetOwnerLink())) {
						this.MarkHasEastWestLinkToWest(node);
						continue;
					}
					this.MarkHasNormalLinkToWest(node);
					// NOTICE: break ignore!!!
				}
			}
			node.CalcPredBaryCenter(0);

			sortValue = node.GetSortValue();

			segmentsFrom = node.GetSegmentsTo();

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();

				fromPortSide = segment.GetToPortSide();
				if (fromPortSide == -3) {

					priority = segment.GetPriority();
					if ((segment.GetFrom().GetPositionInLevel() * priority) < sortValue) {
						fromPortSide = 3;
					} else {
						fromPortSide = 1;
					}
					segment.SetToPortSide(fromPortSide);
				}
				if (fromPortSide == 1) {

					if (this.IsEastWestLink(segment.GetOwnerLink())) {
						this.MarkHasEastWestLinkToEast(node);
					} else {
						this.MarkHasNormalLinkToEast(node);
					}
					continue;
				} else if (fromPortSide == 3) {

					if (this.IsEastWestLink(segment.GetOwnerLink())) {
						this.MarkHasEastWestLinkToWest(node);
						continue;
					}
					this.MarkHasNormalLinkToWest(node);
					// NOTICE: break ignore!!!
				}
			}
			node.CorrectPortNumbers();
		}

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();
		super.GetPercController().StartStep(graph._percForEastWestExpansion,
				2 * (graph.GetNumberOfLevels() - 1));
		graph.UpdateLevelIDs();
		this._numberAuxNodes = 0;
		this.SweepForward();
		if (this._numberAuxNodes > 0) {
			this.SweepBackward();
		}

	}

	private void TreatBackwardLevel() {
		if (((this._prevLevel != null) || (this._nextLevel != null))
				&& (this._actLevel.GetNodesCount() != 0)) {
			HNode node = this._actLevel
					.GetNode(this._actLevel.GetNodesCount() - 1);
			HNodeIterator nodes = this._actLevel.GetNodes();

			while (nodes.HasNext()) {
				HNode node2 = nodes.Next();
				this.CreateAuxNode(node2);
				if (node2 == node) {
					break;
				}
			}
			this._nodeSortAlg.Sort(this._actLevel.GetNodesField());
			this._actLevel.MarkInfoOutOfDate();
			this._actLevel.UpdateInfo();
		}

	}

	@Override
	public void TreatBackwardLevel(HLevel nextLevel, HLevel level) {
		this._nextLevel = nextLevel;
		this._actLevel = level;
		Integer numID = level.GetNumID() - 1;
		if (numID >= 0) {

			this._prevLevel = super.GetGraph().GetLevel(numID);
		} else {
			this._prevLevel = null;
		}
		this.TreatBackwardLevel();

	}

	private void TreatForwardLevel() {
		if (((this._prevLevel != null) || (this._nextLevel != null))
				&& (this._actLevel.GetNodesCount() != 0)) {
			this._numberAuxNodesInActLevel = 0;
			HNodeIterator nodes = this._actLevel.GetNodes();

			while (nodes.HasNext()) {
				this.ResolveEastWestPortSides(nodes.Next());
			}

			nodes = this._actLevel.GetNodes();

			while (nodes.HasNext()) {
				HNode node = nodes.Next();

				if (this.NeedsEastPortAuxNode(node)) {
					this._numberAuxNodesInActLevel++;
				}

				if (this.NeedsWestPortAuxNode(node)) {
					this._numberAuxNodesInActLevel++;
				}
			}
			this._numberAuxNodes += this._numberAuxNodesInActLevel;
			if (this._numberAuxNodesInActLevel > 0) {
				this._actLevel.SetCapacity(this._actLevel.GetNodesCount()
						+ this._numberAuxNodesInActLevel);
			}
		}

	}

	@Override
	public void TreatForwardLevel(HLevel prevLevel, HLevel level) {
		this._prevLevel = prevLevel;
		this._actLevel = level;
		Integer numID = level.GetNumID() + 1;
		if (super.GetGraph().GetNumberOfLevels() > numID) {

			this._nextLevel = super.GetGraph().GetLevel(numID);
		} else {
			this._nextLevel = null;
		}
		this.TreatForwardLevel();

	}

}
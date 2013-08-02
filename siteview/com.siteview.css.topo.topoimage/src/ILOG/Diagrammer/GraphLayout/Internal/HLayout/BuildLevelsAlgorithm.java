package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class BuildLevelsAlgorithm extends HGraphAlgorithm {
	private HIntLevelMarker _levelNodesCount;

	public BuildLevelsAlgorithm() {
	}

	public BuildLevelsAlgorithm(HGraph graph) {
		this.Init(graph);
		this._levelNodesCount = null;
	}

	@Override
	public void Clean() {
		super.Clean();
		if (this._levelNodesCount != null) {
			this._levelNodesCount.Clean();
		}
		this._levelNodesCount = null;

	}

	private void CreateDummyNodes() {
		if (super.GetGraph().GetNumberOfSegments() > 0) {
			HSegmentIterator segments = super.GetGraph().GetSegments();
			HSegment lastSegment = super.GetGraph().GetLastSegment();

			while (segments.HasNext()) {
				HSegment segment = segments.Next();
				this.Split(segment);
				if (segment == lastSegment) {

					return;
				}
				super.GetPercController().AddPoints(1);
				super.LayoutStepPerformed();
			}
		}

	}

	private void FillLevels() {
		this._levelNodesCount = new HIntLevelMarker(super.GetGraph());
		HNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			this.IncrementNumberOfNodes(nodes.Next().GetLevel());
		}
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			HLevel level = levels.Next();
			level.SetCapacity(this.GetNumberOfNodes(level));
		}
		this._levelNodesCount.Clean();
		this._levelNodesCount = null;

		nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			node.GetLevel().Add(node);
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private Integer GetMaxLevelNumber() {
		Integer num = 0;
		HNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			Integer levelNumber = nodes.Next().GetLevelNumber();
			if (levelNumber > num) {
				num = levelNumber;
			}
		}

		return num;

	}

	private Integer GetNumberOfNodes(HLevel level) {

		return this._levelNodesCount.Get(level);

	}

	private void HandleDummyNodesForIncremental() {
		HLevelIterator levels = super.GetGraph().GetLevels();

		while (levels.HasNext()) {
			levels.Next().HandleDummyNodesForIncremental();
		}

	}

	private void IncrementNumberOfNodes(HLevel level) {
		this._levelNodesCount.Add(level, 1);

	}

	private void MakeBypassNode(HNode dummyNode, Integer levelNumber,
			float thickness) {
		HGraph graph = super.GetGraph();
		Integer levelFlow = graph.GetLevelFlow();
		float minDistBetweenLinks = graph.GetMinDistBetweenLinks(levelFlow);

		if (graph.HasOrthogonalLinks()) {
			minDistBetweenLinks /= 4f;
		} else {
			minDistBetweenLinks += thickness;
		}
		dummyNode.SetLevelNumber(levelNumber);
		dummyNode.SetSize(levelFlow, minDistBetweenLinks);

	}

	private void RestructureAdjacencies() {
		HSegmentIterator segments = super.GetGraph().GetSegments();

		while (segments.HasNext()) {
			HSegment segment = segments.Next();
			if (segment.GetSpan() < 0) {
				segment.Reverse();
			}
		}

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();
		Integer numberOfNodes = graph.GetNumberOfNodes();
		Integer numberOfLinks = graph.GetNumberOfLinks();
		Integer pointsOfStep = (10 + (3 * numberOfNodes)) + numberOfLinks;

		if (graph.HasPortSides()) {
			pointsOfStep += numberOfLinks;
		}
		super.GetPercController().StartStep(graph._percForBuildLevels,
				pointsOfStep);
		if (graph.HasPortSides() && graph.HasDummyFirstLevel()) {
			this.ShiftToHaveLevelZeroFree();
		}
		this.CreateDummyNodes();
		super.GetGraph().CreateLevels(this.GetMaxLevelNumber());
		this.FillLevels();
		this.RestructureAdjacencies();

		if (graph.IsIncrementalMode()) {
			this.HandleDummyNodesForIncremental();
		}
		if (super.GetGraph().GetLastLevel().GetNodesCount() == 0) {
			super.GetGraph().RemoveLastLevel();
		}
		HNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			nodes.Next().ResolveNorthOrSouth();
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void ShiftToHaveLevelZeroFree() {
		HNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			node.SetLevelNumber(node.GetLevelNumber() + 1);
		}

	}

	private void Split(HSegment segment) {
		Integer levelNumber = null;
		Integer num2 = null;
		Integer toPortSide = null;
		Integer fromPortSide = null;
		HNode node = null;
		HLink ownerLink = segment.GetOwnerLink();
		float thickness = ownerLink.GetThickness();

		if (segment.IsReversed()) {

			levelNumber = segment.GetTo().GetLevelNumber();

			num2 = segment.GetFrom().GetLevelNumber();

			toPortSide = segment.GetToPortSide();

			fromPortSide = segment.GetFromPortSide();
		} else {

			levelNumber = segment.GetFrom().GetLevelNumber();

			num2 = segment.GetTo().GetLevelNumber();

			toPortSide = segment.GetFromPortSide();

			fromPortSide = segment.GetToPortSide();
		}
		if (levelNumber == num2) {
			Integer num6 = levelNumber + 1;
			if ((toPortSide == 0) || (fromPortSide == 0)) {
				num6 = levelNumber - 1;
			}
			if ((toPortSide == 0) && (fromPortSide == 2)) {

				node = ownerLink.AddDummyNode(segment);
				this.MakeBypassNode(node, levelNumber + 1, thickness);
				ownerLink.AddDummyNode(segment).SetLevelNumber(levelNumber);

				node = ownerLink.AddDummyNode(segment);
				this.MakeBypassNode(node, levelNumber - 1, thickness);
			} else if ((toPortSide == 2) && (fromPortSide == 0)) {

				node = ownerLink.AddDummyNode(segment);
				this.MakeBypassNode(node, levelNumber - 1, thickness);
				ownerLink.AddDummyNode(segment).SetLevelNumber(levelNumber);

				node = ownerLink.AddDummyNode(segment);
				this.MakeBypassNode(node, levelNumber + 1, thickness);
			} else {

				node = ownerLink.AddDummyNode(segment);
				this.MakeBypassNode(node, num6, thickness);
			}
		} else if (levelNumber < num2) {
			if (fromPortSide == 2) {

				node = ownerLink.AddDummyNode(segment);
				this.MakeBypassNode(node, num2 + 1, thickness);
				ownerLink.AddDummyNode(segment).SetLevelNumber(num2);
			}
			for (Integer i = num2 - 1; i > levelNumber; i--) {
				ownerLink.AddDummyNode(segment).SetLevelNumber(i);
			}
			if (toPortSide == 0) {
				ownerLink.AddDummyNode(segment).SetLevelNumber(levelNumber);

				node = ownerLink.AddDummyNode(segment);
				this.MakeBypassNode(node, levelNumber - 1, thickness);
			}
		} else {
			if (fromPortSide == 0) {

				node = ownerLink.AddDummyNode(segment);
				this.MakeBypassNode(node, num2 - 1, thickness);
				ownerLink.AddDummyNode(segment).SetLevelNumber(num2);
			}
			for (Integer j = num2 + 1; j < levelNumber; j++) {
				ownerLink.AddDummyNode(segment).SetLevelNumber(j);
			}
			if (toPortSide == 2) {
				ownerLink.AddDummyNode(segment).SetLevelNumber(levelNumber);

				node = ownerLink.AddDummyNode(segment);
				this.MakeBypassNode(node, levelNumber + 1, thickness);
			}
		}

	}

}
package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class OrthRoutingAlgorithm extends HGraphAlgorithm {
	private OrthRoutingLevelAlgorithm _levelAlg;

	public OrthRoutingAlgorithm(HGraph graph) {
		super.Init(graph);
		this._levelAlg = new OrthRoutingLevelAlgorithm(graph);
	}

	@Override
	public void Clean() {
		super.Clean();
		if (this._levelAlg != null) {
			this._levelAlg.Clean();
		}
		this._levelAlg = null;

	}

	private void CreateOrthogonalLinkShapes() {
		HGraph graph = super.GetGraph();
		HSegmentIterator segments = graph.GetSegments();
		HSegment lastSegment = graph.GetLastSegment();
		Integer edgeFlow = graph.GetEdgeFlow();
		Integer levelFlow = graph.GetLevelFlow();

		while (segments.HasNext()) {
			HSegment segment = segments.Next();
			float orthogonalBend = segment.GetOrthogonalBend();
			if (orthogonalBend != Float.MAX_VALUE) {
				float coord = segment.GetFromPoint()[levelFlow];
				float num3 = segment.GetToPoint()[levelFlow];
				HLink ownerLink = segment.GetOwnerLink();
				HNode node = ownerLink.AddDummyNode(segment);
				node.SetCoord(edgeFlow, orthogonalBend);

				if (segment.IsReversed()) {
					node.SetCoord(levelFlow, coord);
				} else {
					node.SetCoord(levelFlow, num3);
				}

				node = ownerLink.AddDummyNode(segment);
				node.SetCoord(edgeFlow, orthogonalBend);

				if (segment.IsReversed()) {
					node.SetCoord(levelFlow, num3);
				} else {
					node.SetCoord(levelFlow, coord);
				}
			}
			if (segment == lastSegment) {
				break;
			}
		}
		super.LayoutStepPerformed();

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();
		super.GetPercController().StartStep(graph._percForOrthRouting,
				graph.GetNumberOfLevels() - 1);
		if (graph.GetNumberOfSegments() != 0) {
			new HLevelAlgApplicator(graph, this._levelAlg, false).Run();
			this._levelAlg.Clean();
			this._levelAlg = null;
			this.CreateOrthogonalLinkShapes();
		}

	}

}
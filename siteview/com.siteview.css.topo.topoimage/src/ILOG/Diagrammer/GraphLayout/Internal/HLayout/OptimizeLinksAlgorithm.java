package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class OptimizeLinksAlgorithm extends HGraphAlgorithm {
	public OptimizeLinksAlgorithm(HGraph graph) {
		super.Init(graph);
	}

	@Override
	public void Clean() {
		super.Clean();

	}

	private Integer NumberOfThinDummyNodes() {
		Integer num = 0;
		HNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			if (node.IsDummyNode()
					&& ((node.GetWidth() != 0f) || (node.GetHeight() != 0f))) {
				num++;
			}
		}

		return num;

	}

	private void OptimizeLinks() {
		HLinkIterator links = super.GetGraph().GetLinks();

		while (links.HasNext()) {
			float[] fromPoint = null;
			float[] toPoint = null;
			HLink link = links.Next();
			HNodeIterator nodes = link.GetNodes();
			HSegment startSegment = link.GetStartSegment();

			if (startSegment.GetTo().IsDummyNode()) {

				fromPoint = startSegment.GetFromPoint();

				toPoint = startSegment.GetToPoint();
			} else {

				fromPoint = startSegment.GetToPoint();

				toPoint = startSegment.GetFromPoint();
			}

			while (nodes.HasNext()) {
				float[] numArray3 = null;
				float[] numArray4 = null;
				HNode node = nodes.Next();

				startSegment = node.GetOpposite(startSegment);
				if (startSegment.GetFrom() == node) {

					numArray3 = startSegment.GetFromPoint();

					numArray4 = startSegment.GetToPoint();
				} else {

					numArray3 = startSegment.GetToPoint();

					numArray4 = startSegment.GetFromPoint();
				}
				if (toPoint == numArray3) {
					if (((fromPoint[0] == toPoint[0]) && (numArray3[0] == numArray4[0]))
							|| ((fromPoint[1] == toPoint[1]) && (numArray3[1] == numArray4[1]))) {
						node.MarkInvalid();
					} else {
						fromPoint = numArray3;
					}
				} else {
					fromPoint = numArray3;
				}
				toPoint = numArray4;
			}
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	@Override
	public void Run() {
		HGraph graph = super.GetGraph();
		super.GetPercController().StartStep(graph._percForOptimizeLinks,
				graph.GetNumberOfLinks());
		this.TranslateThinDummyNodes();
		this.OptimizeLinks();

	}

	private void TranslateThinDummyNodes() {
		if (this.NumberOfThinDummyNodes() != 0) {
			HGraph graph = super.GetGraph();
			HNodeIterator nodes = graph.GetNodes();
			HNode lastNode = graph.GetLastNode();

			while (nodes.HasNext()) {
				HNode node = nodes.Next();
				if (node.IsDummyNode()
						&& ((node.GetWidth() != 0f) || (node.GetHeight() != 0f))) {
					HSegment firstSegmentFrom = null;
					Integer num2 = null;
					float toCoord = 0;
					if (node.GetWidth() == 0f) {
						num2 = 1;
					} else {
						num2 = 0;
					}
					if (node.GetSegmentsFromCount() > 0) {

						firstSegmentFrom = node.GetFirstSegmentFrom();

						toCoord = firstSegmentFrom.GetToCoord(num2);
					} else if (node.GetSegmentsToCount() > 0) {

						firstSegmentFrom = node.GetFirstSegmentTo();

						toCoord = firstSegmentFrom.GetFromCoord(num2);
					} else {
						firstSegmentFrom = null;
						toCoord = 0f;
					}
					if (firstSegmentFrom != null) {
						float fromCoord = 0;
						HSegment opposite = node.GetOpposite(firstSegmentFrom);
						if (opposite.GetFrom() == node) {

							fromCoord = opposite.GetToCoord(num2);
						} else {

							fromCoord = opposite.GetFromCoord(num2);
						}
						HNode node3 = firstSegmentFrom.GetOwnerLink()
								.AddDummyNode(firstSegmentFrom);
						node3.SetCoord(0, node.GetX());
						node3.SetCoord(1, node.GetY());
						if (toCoord < fromCoord) {
							node3.SetCoord(num2, node.GetCoord(num2));
							node.SetCoord(num2,
									node.GetCoord(num2) + node.GetSize(num2));
						} else if (toCoord > fromCoord) {
							node3.SetCoord(num2,
									node.GetCoord(num2) + node.GetSize(num2));
						} else if (firstSegmentFrom.IsReversed()) {
							node3.SetCoord(num2,
									node.GetCoord(num2) + node.GetSize(num2));
						} else {
							node3.SetCoord(num2, node.GetCoord(num2));
							node.SetCoord(num2,
									node.GetCoord(num2) + node.GetSize(num2));
						}
						node.SetSize(num2, 0f);
					}
				}
				if (node == lastNode) {

					return;
				}
			}
		}

	}

}
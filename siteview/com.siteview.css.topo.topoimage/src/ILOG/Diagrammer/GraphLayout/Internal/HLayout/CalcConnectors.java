package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Math;

public final class CalcConnectors extends HGraphAlgorithm {
	private AnchorPointOptimization _anchorOptimAlg;

	private Integer _edgeFlowDirection;

	private Integer _levelFlowDirection;

	private HSegment _segmentToValidNode;

	private Integer _style;

	private float _thicknessFactor;

	private HNode _validNode;

	public CalcConnectors(HGraph graph) {
		super.Init(graph);
		this._anchorOptimAlg = new AnchorPointOptimization(graph);

		this._levelFlowDirection = graph.GetLevelFlow();

		this._edgeFlowDirection = graph.GetEdgeFlow();

		this._style = graph.GetConnectorStyle();
	}

	private void CalcAuxNodeSegmentAttachCoordBounds(HNode auxNode,
			float[] bounds) {
		Integer index = this._levelFlowDirection;
		HGraph graph = super.GetGraph();
		bounds[0] = Float.MAX_VALUE;
		bounds[1] = Float.MIN_VALUE;
		Integer positionInLevel = auxNode.GetPositionInLevel();
		HLevel level = auxNode.GetLevel();
		if (positionInLevel > 0) {
			HNode oneEndNode = level.GetNode(positionInLevel - 1);

			if (oneEndNode.IsEastWestPortAuxNode()) {
				bounds[0] = 0.5f * (((oneEndNode.GetCoord(index) + oneEndNode
						.GetSize(index)) + auxNode.GetCoord(index)) + graph
						.GetMinDistBetweenLinks(index));
			} else if (oneEndNode.IsDummyNode()) {
				if (oneEndNode.IsInvalid()
						&& oneEndNode.GetOwnerLink().IsOrthogonal()) {
					HSegment segment = (oneEndNode.GetSegmentsFromCount() > 0) ? oneEndNode
							.GetFirstSegmentFrom() : oneEndNode
							.GetFirstSegmentTo();
					while (oneEndNode.IsDummyNode() && oneEndNode.IsInvalid()) {

						oneEndNode = oneEndNode.GetOpposite(segment)
								.GetOpposite(oneEndNode);
					}
				}
				bounds[0] = (oneEndNode.GetCoord(index) + oneEndNode
						.GetSize(index)) + graph.GetMinDistBetweenLinks(index);
			} else {
				bounds[0] = (oneEndNode.GetCoord(index) + oneEndNode
						.GetSize(index))
						+ graph.GetMinDistBetweenNodeAndLink(index);
			}
		}
		if (positionInLevel < (level.GetNodesCount() - 1)) {
			HNode node = level.GetNode(positionInLevel + 1);

			if (node.IsEastWestPortAuxNode()) {
				bounds[1] = 0.5f * (((auxNode.GetCoord(index) + auxNode
						.GetSize(index)) + node.GetCoord(index)) - graph
						.GetMinDistBetweenLinks(index));
			} else if (node.IsDummyNode()) {
				if (node.IsInvalid() && node.GetOwnerLink().IsOrthogonal()) {
					HSegment segment2 = (node.GetSegmentsFromCount() > 0) ? node
							.GetFirstSegmentFrom() : node.GetFirstSegmentTo();
					while (node.IsDummyNode() && node.IsInvalid()) {

						node = node.GetOpposite(segment2).GetOpposite(node);
					}
				}
				bounds[1] = node.GetCoord(index)
						- graph.GetMinDistBetweenLinks(index);
			} else {
				bounds[1] = node.GetCoord(index)
						- graph.GetMinDistBetweenNodeAndLink(index);
			}
		}

	}

	private void CalcAuxNodeWantedAttachCoord(HNode auxNode) {
		HSegment segment = null;
		float num3 = 0;
		float[] bounds = new float[2];
		this.CalcAuxNodeSegmentAttachCoordBounds(auxNode, bounds);
		HSegmentIterator segmentsFrom = auxNode.GetSegmentsFrom();
		Integer numberOfSegments = 0;
		Integer num2 = 0;
		Boolean flag = false;

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();
			if (segment.GetFromPortNumber() != -1) {
				flag = true;
			}

			num3 = this.GetAuxNodeSegmentAttachCoord(segment, bounds, true);
			segment.SetWantedAnchorCoord(num3);
			numberOfSegments++;
		}

		segmentsFrom = auxNode.GetSegmentsTo();

		while (segmentsFrom.HasNext()) {

			segment = segmentsFrom.Next();
			if (segment.GetToPortNumber() != -1) {
				flag = true;
			}

			num3 = this.GetAuxNodeSegmentAttachCoord(segment, bounds, false);
			segment.SetWantedAnchorCoord(num3);
			num2++;
		}
		if (flag) {
			this._anchorOptimAlg.Init(auxNode.GetSegmentsFrom(),
					auxNode.GetSegmentsTo(), numberOfSegments + num2, true);
			this._anchorOptimAlg.Run();
			this._anchorOptimAlg.Init(auxNode.GetSegmentsFrom(),
					auxNode.GetSegmentsTo(), numberOfSegments + num2, false);
			this._anchorOptimAlg.Run();
		} else {

			segmentsFrom = auxNode.GetSegmentsFrom();
			this._anchorOptimAlg.Init(segmentsFrom, numberOfSegments, true);
			this._anchorOptimAlg.Run();

			segmentsFrom = auxNode.GetSegmentsTo();
			this._anchorOptimAlg.Init(segmentsFrom, num2, false);
			this._anchorOptimAlg.Run();
		}

	}

	public void CalcCenteredConnectors() {
		this.CalcNorthSouthPortConnectors();
		this.CalcEastWestAuxPortNodeConnectors();
		HLinkIterator links = super.GetGraph().GetLinks();

		while (links.HasNext()) {
			this.CalcCenteredConnectors(links.Next());
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void CalcCenteredConnectors(HLink link) {
		this.CalcCenteredConnectorsFromSide(link);
		this.CalcCenteredConnectorsToSide(link);

	}

	public void CalcCenteredConnectorsFromSide(HLink link) {

		if (!link.GetFrom().IsEastWestPortAuxNode()) {

			if (link.IsFromSideFixed()) {
				link.SetFromCoordWhenFixed();
			}

			else if (!link.IsFromPortNumberSpecified()) {
				this.CalcCenteredConnectorsFromSideImpl(link,
						link.GetFromPortSide());
			}
		}

	}

	private void CalcCenteredConnectorsFromSideImpl(HLink link, Integer side) {
		HNode node3 = null;
		float coord = 0;
		float num2 = 0;
		HSegment startSegment = link.GetStartSegment();
		HNode from = startSegment.GetFrom();
		HNode to = startSegment.GetTo();
		Integer segmentDir = this.GetSegmentDir(startSegment, side);
		Integer index = 1 - segmentDir;

		if (startSegment.IsReversed()) {

			coord = to.GetCoord(segmentDir);

			num2 = from.GetCoord(segmentDir);
			node3 = to;
		} else {

			coord = from.GetCoord(segmentDir);

			num2 = to.GetCoord(segmentDir);
			node3 = from;
		}
		if ((side == 1) || (side == 0)) {
			link.SetFromCoord(segmentDir, coord);
		} else if ((side == 3) || (side == 2)) {
			link.SetFromCoord(segmentDir, coord + node3.GetSize(segmentDir));
		} else if (coord > num2) {
			link.SetFromCoord(segmentDir, coord);
		} else {
			link.SetFromCoord(segmentDir, coord + node3.GetSize(segmentDir));
		}
		link.SetFromCoord(index, node3.GetCenter(index));

	}

	public void CalcCenteredConnectorsToSide(HLink link) {

		if (!link.GetTo().IsEastWestPortAuxNode()) {

			if (link.IsToSideFixed()) {
				link.SetToCoordWhenFixed();
			}

			else if (!link.IsToPortNumberSpecified()) {
				this.CalcCenteredConnectorsToSideImpl(link,
						link.GetToPortSide());
			}
		}

	}

	private void CalcCenteredConnectorsToSideImpl(HLink link, Integer side) {
		HNode node3 = null;
		float coord = 0;
		float num2 = 0;
		HSegment endSegment = link.GetEndSegment();
		HNode to = endSegment.GetTo();
		HNode from = endSegment.GetFrom();
		Integer segmentDir = this.GetSegmentDir(endSegment, side);
		Integer index = 1 - segmentDir;

		if (endSegment.IsReversed()) {

			coord = from.GetCoord(segmentDir);

			num2 = to.GetCoord(segmentDir);
			node3 = from;
		} else {

			coord = to.GetCoord(segmentDir);

			num2 = from.GetCoord(segmentDir);
			node3 = to;
		}
		if ((side == 1) || (side == 0)) {
			link.SetToCoord(segmentDir, coord);
		} else if ((side == 3) || (side == 2)) {
			link.SetToCoord(segmentDir, coord + node3.GetSize(segmentDir));
		} else if (coord > num2) {
			link.SetToCoord(segmentDir, coord);
		} else {
			link.SetToCoord(segmentDir, coord + node3.GetSize(segmentDir));
		}
		link.SetToCoord(index, node3.GetCenter(index));

	}

	private void CalcCenteredSideConnectors(HNode node, HNode auxNode) {

		if (!node.IsDummyNode()) {
			auxNode.CorrectPortNumbers();
			Boolean flag = node.GetPositionInLevel() < auxNode
					.GetPositionInLevel();
			float coord = node.GetCoord(this._levelFlowDirection);
			float center = node.GetCenter(this._edgeFlowDirection);
			float baseCoord = node.GetCoord(this._edgeFlowDirection);
			float size = node.GetSize(this._edgeFlowDirection);
			Integer numberOfPorts = auxNode.GetNumberOfPorts(2);
			if (auxNode.GetNumberOfPorts(0) > numberOfPorts) {

				numberOfPorts = auxNode.GetNumberOfPorts(0);
			}
			if (flag) {
				if (node.GetNumberOfPorts(1) > numberOfPorts) {

					numberOfPorts = node.GetNumberOfPorts(1);
				}
			} else if (node.GetNumberOfPorts(3) > numberOfPorts) {

				numberOfPorts = node.GetNumberOfPorts(3);
			}
			float[] portTable = null;
			if (numberOfPorts > 0) {
				portTable = new float[numberOfPorts];
				this.CalcPortTable(auxNode, portTable, baseCoord, size, true,
						true);
			}
			if (flag) {

				coord += node.GetSize(this._levelFlowDirection);
			}
			this.CalcAuxNodeWantedAttachCoord(auxNode);
			HSegmentIterator segmentsTo = auxNode.GetSegmentsTo();

			while (segmentsTo.HasNext()) {
				this.RouteToSegmentAtSideConnector(segmentsTo.Next(), coord,
						center, portTable);
			}

			segmentsTo = auxNode.GetSegmentsFrom();

			while (segmentsTo.HasNext()) {
				this.RouteFromSegmentAtSideConnector(segmentsTo.Next(), coord,
						center, portTable);
			}
		}

	}

	public void CalcClippedConnectors() {
		this.CalcNorthSouthPortConnectors();
		this.CalcEastWestAuxPortNodeConnectors();
		HLinkIterator links = super.GetGraph().GetLinks();

		while (links.HasNext()) {
			this.CalcClippedConnectors(links.Next());
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	public void CalcClippedConnectors(HLink link) {
		this.CalcFromClippedConnector(link);
		this.CalcToClippedConnector(link);

	}

	public void CalcEastWestAuxPortNodeConnectors() {

		if (super.GetGraph().HasPortSides()) {
			HNodeIterator nodes = super.GetGraph().GetNodes();

			while (nodes.HasNext()) {
				HNode node = nodes.Next();

				if (node.IsEastWestPortAuxNode()) {
					this.CalcEastWestPortConnectors(node);
					super.GetPercController().AddPoints(1);
					super.LayoutStepPerformed();
				}
			}
		}

	}

	private void CalcEastWestPortConnectors(HNode node) {
		node.CorrectPortNumbers();
		if ((node.GetNumberOfPorts(2) == 0) && (node.GetNumberOfPorts(0) == 0)) {
			this.CalcEvenSpacedConnectors(node, false);
		} else {
			float size = node.GetSize(this._levelFlowDirection);
			float coord = node.GetCoord(this._levelFlowDirection);
			float num3 = this.CalcMinDistUpdateThickness(node, size, true,
					true, true);
			HSegmentIterator segmentsFrom = node.GetSegmentsFrom();
			HSegmentIterator segmentsTo = node.GetSegmentsTo();
			float num4 = coord + num3;
			float num5 = node.GetCoord(this._edgeFlowDirection);
			float num6 = num5 + node.GetSize(this._edgeFlowDirection);
			while (segmentsFrom.HasNext() || segmentsTo.HasNext()) {
				HSegment segment = null;
				float[] fromPoint = null;
				float thickness = 0;

				if (segmentsFrom.HasNext()) {

					segment = segmentsFrom.Next();

					thickness = segment.GetOwnerLink().GetThickness();

					fromPoint = segment.GetFromPoint();
					fromPoint[this._levelFlowDirection] = num4
							+ (0.5f * thickness);
					fromPoint[this._edgeFlowDirection] = num6;
					num4 += num3 + thickness;
				}

				if (segmentsTo.HasNext()) {

					segment = segmentsTo.Next();

					thickness = segment.GetOwnerLink().GetThickness();

					fromPoint = segment.GetToPoint();
					fromPoint[this._levelFlowDirection] = num4
							+ (0.5f * thickness);
					fromPoint[this._edgeFlowDirection] = num5;
					num4 += num3 + thickness;
				}
			}
		}

	}

	public void CalcEvenSpacedConnectors() {
		this.CalcNorthSouthPortConnectors();
		HNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();

			if (node.IsEastWestPortAuxNode()) {
				this.CalcEastWestPortConnectors(node);
			} else {
				this.CalcEvenSpacedConnectors(node, false);
			}
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void CalcEvenSpacedConnectors(HNode node, Boolean ignorePortNumbers) {

		if (!node.IsDummyNode()) {
			HSegment segment = null;
			float[] fromPoint = null;
			float num6 = 0;

			Boolean flag = !node.IsEastWestPortAuxNode();
			float size = node.GetSize(this._levelFlowDirection);
			float coord = node.GetCoord(this._levelFlowDirection);
			float num3 = this.CalcMinDistUpdateThickness(node, size, true,
					false, ignorePortNumbers);
			HSegmentIterator segmentsFrom = node.GetSegmentsFrom();
			float num4 = coord + num3;
			float num5 = node.GetCoord(this._edgeFlowDirection)
					+ node.GetSize(this._edgeFlowDirection);

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();
				if (segment.IsFromSideFixed() && flag) {
					segment.SetFromCoordWhenFixed();
				} else if ((segment.GetFromPortNumber() == -1)
						|| ignorePortNumbers) {
					num6 = this._thicknessFactor
							* segment.GetOwnerLink().GetThickness();

					fromPoint = segment.GetFromPoint();
					fromPoint[this._levelFlowDirection] = num4 + (0.5f * num6);
					fromPoint[this._edgeFlowDirection] = num5;
					num4 += num3 + num6;
				}
			}

			num3 = this.CalcMinDistUpdateThickness(node, size, false, true,
					ignorePortNumbers);

			segmentsFrom = node.GetSegmentsTo();
			num4 = coord + num3;

			num5 = node.GetCoord(this._edgeFlowDirection);

			while (segmentsFrom.HasNext()) {

				segment = segmentsFrom.Next();
				if (segment.IsToSideFixed() && flag) {
					segment.SetToCoordWhenFixed();
				} else if ((segment.GetToPortNumber() == -1)
						|| ignorePortNumbers) {
					num6 = this._thicknessFactor
							* segment.GetOwnerLink().GetThickness();

					fromPoint = segment.GetToPoint();
					fromPoint[this._levelFlowDirection] = num4 + (0.5f * num6);
					fromPoint[this._edgeFlowDirection] = num5;
					num4 += num3 + num6;
				}
			}
		}

	}

	private void CalcEvenSpacedSideConnectors(HNode node, HNode auxNode) {

		if (!node.IsDummyNode()) {
			HSegment segment = null;
			float num5 = 0;
			auxNode.CorrectPortNumbers();
			Boolean flag = node.GetPositionInLevel() < auxNode
					.GetPositionInLevel();
			float coord = node.GetCoord(this._edgeFlowDirection);
			float size = node.GetSize(this._edgeFlowDirection);
			float num3 = this.CalcMinDistUpdateThickness(auxNode, size, true,
					true, false);
			Integer numberOfPorts = auxNode.GetNumberOfPorts(2);
			if (auxNode.GetNumberOfPorts(0) > numberOfPorts) {

				numberOfPorts = auxNode.GetNumberOfPorts(0);
			}
			if (flag) {
				if (node.GetNumberOfPorts(1) > numberOfPorts) {

					numberOfPorts = node.GetNumberOfPorts(1);
				}
			} else if (node.GetNumberOfPorts(3) > numberOfPorts) {

				numberOfPorts = node.GetNumberOfPorts(3);
			}
			float[] portTable = null;
			if (numberOfPorts > 0) {
				portTable = new float[numberOfPorts];
				this.CalcPortTable(auxNode, portTable, coord, size, true, true);
			}
			this.CalcAuxNodeWantedAttachCoord(auxNode);
			HSegmentIterator iterator = flag ? auxNode.GetSegmentsTo()
					: auxNode.GetSegmentsToInReverseOrder();
			float num6 = coord + num3;
			float normalNodeBorderCoord = node
					.GetCoord(this._levelFlowDirection);
			if (flag) {

				normalNodeBorderCoord += node.GetSize(this._levelFlowDirection);
			}

			while (iterator.HasNext()) {

				segment = iterator.Next();
				num5 = this._thicknessFactor
						* segment.GetOwnerLink().GetThickness();

				if (this.RouteToSegmentAtSideConnector(segment,
						normalNodeBorderCoord, num6 + (0.5f * num5), portTable)) {
					num6 += num3 + num5;
				}
			}
			iterator = flag ? auxNode.GetSegmentsFromInReverseOrder() : auxNode
					.GetSegmentsFrom();

			while (iterator.HasNext()) {

				segment = iterator.Next();
				num5 = this._thicknessFactor
						* segment.GetOwnerLink().GetThickness();

				if (this.RouteFromSegmentAtSideConnector(segment,
						normalNodeBorderCoord, num6 + (0.5f * num5), portTable)) {
					num6 += num3 + num5;
				}
			}
		}

	}

	public void CalcFromClippedConnector(HLink link) {

		if (!link.GetFrom().IsEastWestPortAuxNode()) {

			if (link.IsFromSideFixed()) {
				link.SetFromCoordWhenFixed();
			}

			else if (!link.IsFromPortNumberSpecified()) {
				this.CalcFromClippedConnectorImpl(link);
			}
		}

	}

	private void CalcFromClippedConnectorImpl(HLink link) {
		HNode to = null;
		HNode from = null;
		double[] firstPoint = new double[2];
		double[] secondPoint = new double[2];
		double[] rect = new double[4];
		HSegment startSegment = link.GetStartSegment();

		if (startSegment.IsReversed()) {

			to = startSegment.GetTo();

			from = startSegment.GetFrom();
		} else {

			to = startSegment.GetFrom();

			from = startSegment.GetTo();
		}

		if (!to.IsInvalid()) {
			float[] fromPoint = null;
			Integer fromPortNumber = null;

			while (from.IsInvalid()) {

				startSegment = from.GetOpposite(startSegment);

				from = startSegment.GetOpposite(from);
			}
			if (startSegment.GetFrom() == from) {

				fromPortNumber = startSegment.GetFromPortNumber();

				fromPoint = startSegment.GetFromPoint();
			} else {

				fromPortNumber = startSegment.GetToPortNumber();

				fromPoint = startSegment.GetToPoint();
			}
			if (fromPortNumber == -1) {

				secondPoint[0] = from.GetCenter(0);

				secondPoint[1] = from.GetCenter(1);
			} else {
				secondPoint[0] = fromPoint[0];
				secondPoint[1] = fromPoint[1];
			}

			firstPoint[0] = to.GetCenter(0);

			firstPoint[1] = to.GetCenter(1);

			rect[0] = to.GetX();
			rect[1] = to.GetX() + to.GetWidth();

			rect[2] = to.GetY();
			rect[3] = to.GetY() + to.GetHeight();
			LayoutUtil.Clip(firstPoint, secondPoint, rect);
			link.SetFromCoord(0, (float) firstPoint[0]);
			link.SetFromCoord(1, (float) firstPoint[1]);
		}

	}

	public float CalcMinDistUpdateThickness(HNode node, float size,
			Boolean fromSide, Boolean toSide, Boolean ignorePorts) {
		HSegmentIterator iterator = fromSide ? (toSide ? node.GetSegments()
				: node.GetSegmentsFrom()) : node.GetSegmentsTo();
		Integer num = 0;
		float num3 = 0f;

		Boolean flag2 = !node.IsEastWestPortAuxNode();

		while (iterator.HasNext()) {
			Integer fromPortNumber = null;
			Boolean flag = null;
			HSegment segment = iterator.Next();
			if (segment.GetFrom() == node) {

				fromPortNumber = segment.GetFromPortNumber();
				flag = flag2 && segment.IsFromSideFixed();
			} else {

				fromPortNumber = segment.GetToPortNumber();
				flag = flag2 && segment.IsToSideFixed();
			}
			if (!flag && ((fromPortNumber == -1) || ignorePorts)) {
				num++;

				num3 += segment.GetOwnerLink().GetThickness();
			}
		}
		this._thicknessFactor = 1f;
		float num4 = (size - num3) / ((float) (num + 1));
		if (num4 < 0.0) {
			num4 = 0f;
			this._thicknessFactor = size / num3;
		}

		return num4;

	}

	public void CalcNorthSouthPortConnectors() {
		HNodeIterator nodes = super.GetGraph().GetNodes();

		while (nodes.HasNext()) {
			this.CalcNorthSouthPortConnectors(nodes.Next());
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void CalcNorthSouthPortConnectors(HNode node) {

		if (!node.IsDummyNode() && !node.IsEastWestPortAuxNode()) {
			Integer fromPortNumber = null;
			float[] fromPoint = null;
			float num5 = 0;
			node.CorrectPortNumbers();
			float size = node.GetSize(this._levelFlowDirection);
			float coord = node.GetCoord(this._levelFlowDirection);
			Integer numberOfPorts = node.GetNumberOfPorts(2);
			if (numberOfPorts >= 0) {
				float[] portTable = new float[numberOfPorts];
				this.CalcPortTable(node, portTable, coord, size, true, false);
				num5 = node.GetCoord(this._edgeFlowDirection)
						+ node.GetSize(this._edgeFlowDirection);
				HSegmentIterator segmentsFrom = node.GetSegmentsFrom();

				while (segmentsFrom.HasNext()) {
					HSegment segment = segmentsFrom.Next();

					fromPortNumber = segment.GetFromPortNumber();
					if (fromPortNumber != -1) {

						fromPoint = segment.GetFromPoint();
						fromPoint[this._levelFlowDirection] = portTable[fromPortNumber];
						fromPoint[this._edgeFlowDirection] = num5;
					}
				}
				portTable = null;
			}

			numberOfPorts = node.GetNumberOfPorts(0);
			if (numberOfPorts >= 0) {
				float[] numArray3 = new float[numberOfPorts];
				this.CalcPortTable(node, numArray3, coord, size, false, true);

				num5 = node.GetCoord(this._edgeFlowDirection);
				HSegmentIterator segmentsTo = node.GetSegmentsTo();

				while (segmentsTo.HasNext()) {
					HSegment segment2 = segmentsTo.Next();

					fromPortNumber = segment2.GetToPortNumber();
					if (fromPortNumber != -1) {

						fromPoint = segment2.GetToPoint();
						fromPoint[this._levelFlowDirection] = numArray3[fromPortNumber];
						fromPoint[this._edgeFlowDirection] = num5;
					}
				}
				numArray3 = null;
			}
		}

	}

	private void CalcPortTable(HNode node, float[] portTable, float baseCoord,
			float size, Boolean fromSide, Boolean toSide) {
		Integer num = null;
		Integer length = portTable.length;
		for (num = 0; num < length; num++) {
			portTable[num] = 0f;
		}
		HSegmentIterator iterator = fromSide ? (toSide ? node.GetSegments()
				: node.GetSegmentsFrom()) : node.GetSegmentsTo();

		while (iterator.HasNext()) {
			HSegment segment = iterator.Next();
			Integer index = (segment.GetFrom() == node) ? segment
					.GetFromPortNumber() : segment.GetToPortNumber();
			if (index != -1) {

				portTable[index] = Math.Max(segment.GetOwnerLink()
						.GetThickness(), portTable[index]);
			}
		}
		float num4 = 0f;
		for (num = 0; num < portTable.length; num++) {
			num4 += portTable[num];
		}
		float maxValue = Float.MAX_VALUE;
		float num6 = (size - num4) / ((float) (length + 1));
		if (num6 < 0.0) {
			num6 = size / ((float) (length + 1));
			maxValue = 0f;
		}
		float num7 = baseCoord + num6;
		for (num = 0; num < length; num++) {
			float num8 = portTable[num];
			if (num8 > maxValue) {
				num8 = maxValue;
			}
			portTable[num] = num7 + (0.5f * num8);
			num7 += num6 + num8;
		}

	}

	public void CalcToClippedConnector(HLink link) {

		if (!link.GetTo().IsEastWestPortAuxNode()) {

			if (link.IsToSideFixed()) {
				link.SetToCoordWhenFixed();
			}

			else if (!link.IsToPortNumberSpecified()) {
				this.CalcToClippedConnectorImpl(link);
			}
		}

	}

	private void CalcToClippedConnectorImpl(HLink link) {
		HNode from = null;
		HNode to = null;
		double[] firstPoint = new double[2];
		double[] secondPoint = new double[2];
		double[] rect = new double[4];
		HSegment endSegment = link.GetEndSegment();

		if (endSegment.IsReversed()) {

			from = endSegment.GetFrom();

			to = endSegment.GetTo();
		} else {

			from = endSegment.GetTo();

			to = endSegment.GetFrom();
		}

		if (!from.IsInvalid()) {
			float[] fromPoint = null;
			Integer fromPortNumber = null;

			while (to.IsInvalid()) {

				endSegment = to.GetOpposite(endSegment);

				to = endSegment.GetOpposite(to);
			}
			if (endSegment.GetFrom() == to) {

				fromPortNumber = endSegment.GetFromPortNumber();

				fromPoint = endSegment.GetFromPoint();
			} else {

				fromPortNumber = endSegment.GetToPortNumber();

				fromPoint = endSegment.GetToPoint();
			}
			if (fromPortNumber == -1) {

				secondPoint[0] = to.GetCenter(0);

				secondPoint[1] = to.GetCenter(1);
			} else {
				secondPoint[0] = fromPoint[0];
				secondPoint[1] = fromPoint[1];
			}

			firstPoint[0] = from.GetCenter(0);

			firstPoint[1] = from.GetCenter(1);

			rect[0] = from.GetX();
			rect[1] = from.GetX() + from.GetWidth();

			rect[2] = from.GetY();
			rect[3] = from.GetY() + from.GetHeight();
			LayoutUtil.Clip(firstPoint, secondPoint, rect);
			link.SetToCoord(0, (float) firstPoint[0]);
			link.SetToCoord(1, (float) firstPoint[1]);
		}

	}

	@Override
	public void Clean() {
		super.Clean();
		if (this._anchorOptimAlg != null) {
			this._anchorOptimAlg.Clean();
		}
		this._anchorOptimAlg = null;

	}

	public void FixEastWestPorts() {
		HGraph graph = super.GetGraph();
		Integer numberOfNodes = graph.GetNumberOfNodes();
		super.GetPercController().StartStep(graph._percForFixEastWestPorts,
				2 * numberOfNodes);
		HNodeIterator nodes = graph.GetNodes();

		while (nodes.HasNext()) {
			HNode node = nodes.Next();
			HNode eastPortAuxNode = node.GetEastPortAuxNode();
			if (eastPortAuxNode != null) {
				this.FixEastWestPorts(node, eastPortAuxNode);
			}

			eastPortAuxNode = node.GetWestPortAuxNode();
			if (eastPortAuxNode != null) {
				this.FixEastWestPorts(node, eastPortAuxNode);
			}
			super.GetPercController().AddPoints(1);
			super.LayoutStepPerformed();
		}

	}

	private void FixEastWestPorts(HNode node, HNode auxNode) {
		if (this._style == 0x63) {
			this.CalcCenteredSideConnectors(node, auxNode);

			return;
		} else if (this._style == 100) {
			this.CalcEvenSpacedSideConnectors(node, auxNode);

			return;
		} else if (this._style == 0x65) {
			this.CalcEvenSpacedSideConnectors(node, auxNode);

			return;
		}
		throw (new system.Exception("CalcConnectors: Illegal connector style"));

	}

	private float GetAuxNodeSegmentAttachCoord(HSegment segment,
			float[] bounds, Boolean fromSide) {
		float fromCoord = 0;
		HNode from = null;
		Integer index = this._levelFlowDirection;
		if (fromSide) {

			fromCoord = segment.GetFromCoord(index);

			from = segment.GetFrom();
		} else {

			fromCoord = segment.GetToCoord(index);

			from = segment.GetTo();
		}
		HLink ownerLink = segment.GetOwnerLink();
		if (ownerLink.GetFrom().GetLevelNumber() != ownerLink.GetTo()
				.GetLevelNumber()) {
			HNode nextValidOrLeveledNode = this.GetNextValidOrLeveledNode(from,
					segment);

			if (nextValidOrLeveledNode.IsInvalid()) {

				return fromCoord;
			}
			float nextValidNodeAttachCoord = this.GetNextValidNodeAttachCoord();
			if ((ownerLink.IsOrthogonal() && nextValidOrLeveledNode
					.IsDummyNode())
					&& (nextValidOrLeveledNode.GetLevelNumber() == -1)) {

				if (this.GetNextValidOrLeveledNode(
						nextValidOrLeveledNode,
						nextValidOrLeveledNode
								.GetOpposite(this._segmentToValidNode))
						.IsInvalid()) {

					return fromCoord;
				}

				nextValidNodeAttachCoord = this.GetNextValidNodeAttachCoord();
			}
			if ((nextValidNodeAttachCoord >= bounds[0])
					&& (nextValidNodeAttachCoord <= bounds[1])) {

				return nextValidNodeAttachCoord;
			}
		}

		return fromCoord;

	}

	public float GetNextValidNodeAttachCoord() {
		if (this._segmentToValidNode.GetFrom() == this._validNode) {

			return this._segmentToValidNode
					.GetFromCoord(this._levelFlowDirection);
		}

		return this._segmentToValidNode.GetToCoord(this._levelFlowDirection);

	}

	public HNode GetNextValidOrLeveledNode(HNode node, HSegment segment) {

		node = segment.GetOpposite(node);
		while (node.IsInvalid() && (node.GetLevelNumber() == -1)) {

			segment = node.GetOpposite(segment);

			node = segment.GetOpposite(node);
		}
		this._validNode = node;
		this._segmentToValidNode = segment;

		return node;

	}

	private Integer GetSegmentDir(HSegment segment, Integer side) {
		if (((side == 1) || (side == 3)) || (side == -3)) {

			return this._levelFlowDirection;
		}
		if ((((side != 0) && (side != 2)) && (side != -2))
				&& (segment.GetSpan() == 0)) {

			return this._levelFlowDirection;
		}

		return this._edgeFlowDirection;

	}

	private void RouteAtSideConnector(HSegment segment, Boolean fromSide,
			float x1, float y1, float x2, float y2, Boolean isSideFixed) {
		HNode node = null;
		HNode from = null;
		float[] fromPoint = null;
		HLink ownerLink = segment.GetOwnerLink();
		if (fromSide) {

			fromPoint = segment.GetFromPoint();

			from = segment.GetFrom();
		} else {

			fromPoint = segment.GetToPoint();

			from = segment.GetTo();
		}

		if (ownerLink.IsOrthogonal()) {
			HNode nextValidOrLeveledNode = this.GetNextValidOrLeveledNode(from,
					segment);

			if ((!nextValidOrLeveledNode.IsInvalid() && nextValidOrLeveledNode
					.IsDummyNode())
					&& (nextValidOrLeveledNode.GetLevelNumber() == -1)) {
				nextValidOrLeveledNode.SetCoord(this._levelFlowDirection, x2);
			}

			node = ownerLink.AddDummyNode(segment);
			HNode node2 = ownerLink.AddDummyNode(segment);
			if (segment.IsReversed() == fromSide) {
				HNode node3 = node;
				node = node2;
				node2 = node3;
			}
			node2.SetCoord(this._levelFlowDirection, x2);
			node2.SetCoord(this._edgeFlowDirection, y1);
		} else {

			node = ownerLink.AddDummyNode(segment);
		}
		node.SetCoord(this._levelFlowDirection, x2);
		node.SetCoord(this._edgeFlowDirection, y2);
		if (!isSideFixed) {
			fromPoint[this._levelFlowDirection] = x1;
			fromPoint[this._edgeFlowDirection] = y1;
		}

	}

	private Boolean RouteFromSegmentAtSideConnector(HSegment segment,
			float normalNodeBorderCoord, float attachCoordForNormalLinks,
			float[] portTable) {
		float num4 = 0;
		Integer fromPortNumber = segment.GetFromPortNumber();
		Boolean isSideFixed = false;
		Boolean flag2 = false;
		float fromCoord = segment.GetFromCoord(this._edgeFlowDirection);
		float wantedAnchorCoord = segment.GetWantedAnchorCoord();

		if (segment.IsFromSideFixed()) {
			segment.SetFromCoordWhenFixed();

			num4 = segment.GetFromCoord(this._edgeFlowDirection);
			isSideFixed = true;
		} else if (fromPortNumber == -1) {
			num4 = attachCoordForNormalLinks;
			flag2 = true;
		} else {
			num4 = portTable[fromPortNumber];
		}
		this.RouteAtSideConnector(segment, true, normalNodeBorderCoord, num4,
				wantedAnchorCoord, fromCoord, isSideFixed);

		return flag2;

	}

	private Boolean RouteToSegmentAtSideConnector(HSegment segment,
			float normalNodeBorderCoord, float attachCoordForNormalLinks,
			float[] portTable) {
		float num4 = 0;
		Integer toPortNumber = segment.GetToPortNumber();
		Boolean isSideFixed = false;
		Boolean flag2 = false;
		float toCoord = segment.GetToCoord(this._edgeFlowDirection);
		float wantedAnchorCoord = segment.GetWantedAnchorCoord();

		if (segment.IsToSideFixed()) {
			segment.SetToCoordWhenFixed();

			num4 = segment.GetToCoord(this._edgeFlowDirection);
			isSideFixed = true;
		} else if (toPortNumber == -1) {
			num4 = attachCoordForNormalLinks;
			flag2 = true;
		} else {
			num4 = portTable[toPortNumber];
		}
		this.RouteAtSideConnector(segment, false, normalNodeBorderCoord, num4,
				wantedAnchorCoord, toCoord, isSideFixed);

		return flag2;

	}

	@Override
	public void Run() {
		Integer num = null;
		HGraph graph = super.GetGraph();
		Integer numberOfNodes = graph.GetNumberOfNodes();
		Integer numberOfLinks = graph.GetNumberOfLinks();
		if (this._style == 0x63) {
			num = (numberOfLinks + numberOfNodes) + (numberOfNodes / 20);
			super.GetPercController().StartStep(graph._percForCalcConnectors,
					num);
			this.CalcCenteredConnectors();

			return;
		} else if (this._style == 100) {
			num = (numberOfLinks + numberOfNodes) + (numberOfNodes / 20);
			super.GetPercController().StartStep(graph._percForCalcConnectors,
					num);
			this.CalcClippedConnectors();

			return;
		} else if (this._style == 0x65) {
			num = (2 * numberOfNodes) + (numberOfNodes / 20);
			super.GetPercController().StartStep(graph._percForCalcConnectors,
					num);
			this.CalcEvenSpacedConnectors();

			return;
		}
		throw (new system.Exception("CalcConnectors: Illegal connector style"));

	}

}
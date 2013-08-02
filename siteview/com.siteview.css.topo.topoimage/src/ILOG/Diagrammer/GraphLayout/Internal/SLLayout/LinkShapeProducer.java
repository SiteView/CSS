package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public abstract class LinkShapeProducer {
	private float _bypassDistance;

	private Integer _counter;

	private java.lang.Object _fromNode;

	private ShortLinkAlgorithm _layout;

	private java.lang.Object _link;

	private float _minFinalSegmentLength;

	public INodeSideFilter _nodeSideFilter;

	private LinkShapeType[][] _selfLinkTypes;

	private LinkShapeType[] _shapeTypes = new LinkShapeType[60];

	private java.lang.Object _toNode;

	private static LinkShapeType[] ADDITIONAL_OVERLAPPING_NODES_TYPES = new LinkShapeType[] {
			IlvFourBendRLtop.instance, IlvFourBendRLbottom.instance,
			IlvFourBendBTleft.instance, IlvFourBendBTright.instance,
			IlvFourBendLRtop.instance, IlvFourBendLRbottom.instance,
			IlvFourBendTBleft.instance, IlvFourBendTBright.instance };

	private Integer NOT_FIXED_SIDE = 5;

	private static LinkShapeType[] OVERLAPPING_NODES_TYPES = new LinkShapeType[] {
			IlvThreeBendRT_OVERLAP.instance, IlvThreeBendTR_OVERLAP.instance,
			IlvThreeBendBL_OVERLAP.instance, IlvThreeBendLB_OVERLAP.instance,
			IlvThreeBendLT_OVERLAP.instance, IlvThreeBendTL_OVERLAP.instance,
			IlvThreeBendBR_OVERLAP.instance, IlvThreeBendRB_OVERLAP.instance };

	private static LinkShapeType[][] THREE_BENDS_SELF_LINK_TYPES = new LinkShapeType[][] {
			new LinkShapeType[] { IlvThreeBendTR.instance,
					IlvThreeBendRB.instance, IlvThreeBendBL.instance,
					IlvThreeBendLT.instance },
			new LinkShapeType[] { IlvThreeBendRB.instance,
					IlvThreeBendBL.instance, IlvThreeBendLT.instance,
					IlvThreeBendTR.instance },
			new LinkShapeType[] { IlvThreeBendBL.instance,
					IlvThreeBendLT.instance, IlvThreeBendTR.instance,
					IlvThreeBendRB.instance },
			new LinkShapeType[] { IlvThreeBendLT.instance,
					IlvThreeBendTR.instance, IlvThreeBendRB.instance,
					IlvThreeBendBL.instance } };

	private static LinkShapeType[][] TWO_BENDS_SELF_LINK_TYPES = new LinkShapeType[][] {
			new LinkShapeType[] { IlvTwoBendTT.instance, IlvTwoBendBB.instance,
					IlvTwoBendLL.instance, IlvTwoBendRR.instance },
			new LinkShapeType[] { IlvTwoBendBB.instance, IlvTwoBendLL.instance,
					IlvTwoBendRR.instance, IlvTwoBendTT.instance },
			new LinkShapeType[] { IlvTwoBendLL.instance, IlvTwoBendRR.instance,
					IlvTwoBendTT.instance, IlvTwoBendBB.instance },
			new LinkShapeType[] { IlvTwoBendRR.instance, IlvTwoBendTT.instance,
					IlvTwoBendBB.instance, IlvTwoBendLL.instance } };

	public LinkShapeProducer(ShortLinkAlgorithm layout) {
		this.Update(layout);
	}

	private void AddShapeIfAcceptedByFilter(LinkShapeType[] shapes,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Boolean checkOrigin, Boolean checkDestination) {
		this.AddShapeIfAcceptedByFilter(shapes, linkData, 5, 5, checkOrigin,
				checkDestination);

	}

	private void AddShapeIfAcceptedByFilter(LinkShapeType[] shapes,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			Integer fixedFromSide, Integer fixedToSide, Boolean checkOrigin,
			Boolean checkDestination) {
		for (Integer i = 0; i < shapes.length; i++) {
			Integer fromSide = null;
			LinkShapeType type = shapes[i];
			Boolean flag = true;
			if (checkOrigin) {

				fromSide = type.GetFromSide(linkData);
				if (fixedFromSide == 5) {

					if (!this.IsAllowedByFilter((int) fromSide, true)) {
						flag = false;
					}
				} else if (fromSide != fixedFromSide) {
					flag = false;
				}
			}
			if (flag) {
				if (checkDestination) {

					fromSide = type.GetToSide(linkData);
					if (fixedToSide == 5) {

						if (!this.IsAllowedByFilter((int) fromSide, false)) {
							flag = false;
						}
					} else if (fromSide != fixedToSide) {
						flag = false;
					}
				}
				if (flag) {
					this._shapeTypes[this._counter++] = type;
				}
			}
		}

	}

	public void Clean() {
		this._layout = null;
		this._link = null;
		this._fromNode = null;
		this._toNode = null;
		this._nodeSideFilter = null;

	}

	private void ClearFilteredShapes() {
		this._counter = 0;

	}

	private LinkShapeType[] ComputeAllowedLinkShapes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, Integer fixedFromSide,
			Integer fixedToSide, Boolean isOverlappingNodes,
			float bypassDistance) {
		this.ClearFilteredShapes();
		if (isOverlappingNodes) {
			this.AddShapeIfAcceptedByFilter(ADDITIONAL_OVERLAPPING_NODES_TYPES,
					linkData, fixedFromSide, fixedToSide, true, true);
		} else if (linkData.IsQuasiSelfInterGraphLinkOrigin()
				|| linkData.IsQuasiSelfInterGraphLinkDestination()) {
			this.AddShapeIfAcceptedByFilter(
					this.GetAllQuasiSelfInterGraphLinkShapeTypes(), linkData,
					fixedFromSide, fixedToSide, true, true);
		}
		if (this._counter == 0) {
			if ((fixedFromSide == 5) || (fixedToSide == 5)) {
				if (fixedFromSide == 0) {
					this.AddShapeIfAcceptedByFilter(this.GetLeftToTopSideTypes(
							fromRect, toRect, isOverlappingNodes,
							bypassDistance), linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetLeftToBottomSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetLeftToLeftSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetLeftToRightSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					return this.GetCompactedFilteredShapes();
				} else if (fixedFromSide == 1) {
					this.AddShapeIfAcceptedByFilter(this
							.GetRightToTopSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetRightToBottomSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetRightToLeftSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetRightToRightSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					return this.GetCompactedFilteredShapes();
				} else if (fixedFromSide == 2) {
					this.AddShapeIfAcceptedByFilter(this.GetTopToTopSideTypes(
							fromRect, toRect, isOverlappingNodes,
							bypassDistance), linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetTopToBottomSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this.GetTopToLeftSideTypes(
							fromRect, toRect, isOverlappingNodes,
							bypassDistance), linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetTopToRightSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					return this.GetCompactedFilteredShapes();
				} else if (fixedFromSide == 3) {
					this.AddShapeIfAcceptedByFilter(this
							.GetBottomToTopSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetBottomToBottomSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetBottomToLeftSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					this.AddShapeIfAcceptedByFilter(this
							.GetBottomToRightSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, true);
					return this.GetCompactedFilteredShapes();
				} else if (fixedFromSide == 5) {
					if (fixedToSide == 0) {
						this.AddShapeIfAcceptedByFilter(this
								.GetTopToLeftSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetBottomToLeftSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetLeftToLeftSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetRightToLeftSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						return this.GetCompactedFilteredShapes();
					} else if (fixedToSide == 1) {
						this.AddShapeIfAcceptedByFilter(this
								.GetTopToRightSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetBottomToRightSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetLeftToRightSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetRightToRightSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						return this.GetCompactedFilteredShapes();
					} else if (fixedToSide == 2) {
						this.AddShapeIfAcceptedByFilter(this
								.GetTopToTopSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetBottomToTopSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetLeftToTopSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetRightToTopSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						return this.GetCompactedFilteredShapes();
					} else if (fixedToSide == 3) {
						this.AddShapeIfAcceptedByFilter(this
								.GetTopToBottomSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetBottomToBottomSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetLeftToBottomSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						this.AddShapeIfAcceptedByFilter(this
								.GetRightToBottomSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, false);
						return this.GetCompactedFilteredShapes();
					} else if (fixedToSide == 5) {
						this.AddShapeIfAcceptedByFilter(this
								.GetTopToTopSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetTopToBottomSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetTopToLeftSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetTopToRightSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetBottomToTopSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetBottomToBottomSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetBottomToLeftSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetBottomToRightSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetLeftToTopSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetLeftToBottomSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetLeftToLeftSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetLeftToRightSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetRightToTopSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetRightToBottomSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetRightToLeftSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						this.AddShapeIfAcceptedByFilter(this
								.GetRightToRightSideTypes(fromRect, toRect,
										isOverlappingNodes, bypassDistance),
								linkData, true, true);
						return this.GetCompactedFilteredShapes();
					}
					throw (new ArgumentException(
							"unsupported fixed destination side: "
									+ fixedToSide));
				}
				throw (new ArgumentException("unsupported fixed origin side: "
						+ fixedFromSide));
			}
			if (fixedFromSide == 0) {
				if (fixedToSide == 0) {
					this.AddShapeIfAcceptedByFilter(this
							.GetLeftToLeftSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 1) {
					this.AddShapeIfAcceptedByFilter(this
							.GetLeftToRightSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 2) {
					this.AddShapeIfAcceptedByFilter(this.GetLeftToTopSideTypes(
							fromRect, toRect, isOverlappingNodes,
							bypassDistance), linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 3) {
					this.AddShapeIfAcceptedByFilter(this
							.GetLeftToBottomSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				}
				throw (new ArgumentException("unexpected destination side: "
						+ fixedToSide));
			} else if (fixedFromSide == 1) {
				if (fixedToSide == 0) {
					this.AddShapeIfAcceptedByFilter(this
							.GetRightToLeftSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 1) {
					this.AddShapeIfAcceptedByFilter(this
							.GetRightToRightSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 2) {
					this.AddShapeIfAcceptedByFilter(this
							.GetRightToTopSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 3) {
					this.AddShapeIfAcceptedByFilter(this
							.GetRightToBottomSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				}
				throw (new ArgumentException("unexpected destination side: "
						+ fixedToSide));
			} else if (fixedFromSide == 2) {
				if (fixedToSide == 0) {
					this.AddShapeIfAcceptedByFilter(this.GetTopToLeftSideTypes(
							fromRect, toRect, isOverlappingNodes,
							bypassDistance), linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 1) {
					this.AddShapeIfAcceptedByFilter(this
							.GetTopToRightSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 2) {
					this.AddShapeIfAcceptedByFilter(this.GetTopToTopSideTypes(
							fromRect, toRect, isOverlappingNodes,
							bypassDistance), linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 3) {
					this.AddShapeIfAcceptedByFilter(this
							.GetTopToBottomSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				}
				throw (new ArgumentException("unexpected destination side: "
						+ fixedToSide));
			} else if (fixedFromSide == 3) {
				if (fixedToSide == 0) {
					this.AddShapeIfAcceptedByFilter(this
							.GetBottomToLeftSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 1) {
					this.AddShapeIfAcceptedByFilter(this
							.GetBottomToRightSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 2) {
					this.AddShapeIfAcceptedByFilter(this
							.GetBottomToTopSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				} else if (fixedToSide == 3) {
					this.AddShapeIfAcceptedByFilter(this
							.GetBottomToBottomSideTypes(fromRect, toRect,
									isOverlappingNodes, bypassDistance),
							linkData, false, false);
					return this.GetCompactedFilteredShapes();
				}
				throw (new ArgumentException("unexpected destination side: "
						+ fixedToSide));
			} else {
				throw (new ArgumentException("unexpected origin side: "
						+ fixedFromSide));
			}
		}
		Label_0791:

		return this.GetCompactedFilteredShapes();

	}

	private LinkShapeType[] ComputeFilteredShapeTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			LinkShapeType[] shapeTypes, InternalRect fromRect,
			InternalRect toRect, Integer fixedFromSide, Integer fixedToSide,
			Boolean isOverlappingNodes, float bypassDistance) {
		if (((fixedFromSide == 5) && (fixedToSide == 5))
				&& (this._nodeSideFilter == null)) {

			return shapeTypes;
		}
		this.ClearFilteredShapes();
		this.AddShapeIfAcceptedByFilter(shapeTypes, linkData, fixedFromSide,
				fixedToSide, true, true);
		if ((this._counter == shapeTypes.length) && (this._counter > 0)) {

			return shapeTypes;
		}
		LinkShapeType[] compactedFilteredShapes = this
				.GetCompactedFilteredShapes();
		if (compactedFilteredShapes == null) {

			compactedFilteredShapes = this.ComputeAllowedLinkShapes(linkData,
					fromRect, toRect, fixedFromSide, fixedToSide,
					isOverlappingNodes, bypassDistance);
			if (compactedFilteredShapes == null) {
				throw (new system.Exception(
						clr.System.StringStaticWrapper
								.Concat(new java.lang.Object[] {
										"The filter ",
										this._nodeSideFilter,
										" has rejected all the combinations of node side for link = ",
										linkData.get_nodeOrLink() })));
			}
		}

		return compactedFilteredShapes;

	}

	public LinkShapeType[] CopyShapeTypes(LinkShapeType[] oldShapeTypes,
			Integer counter) {
		LinkShapeType[] destinationArray = new LinkShapeType[counter];
		clr.System.ArrayStaticWrapper.Copy(oldShapeTypes, 0, destinationArray,
				0, counter);

		return destinationArray;

	}

	public LinkShapeType[] GetAllowedLinkShapes(ShortLinkAlgorithm layout,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {
		if (linkData == null) {
			throw (new ArgumentException("link data cannot be null"));
		}
		this._link = linkData.get_nodeOrLink();
		this._layout = layout;
		if (linkData.boundingBox == null) {
			linkData.boundingBox = new InternalRect(0f, 0f, 0f, 0f);
		}
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData fromNode = linkData
				.GetFromNode();
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData toNode = linkData
				.GetToNode();
		this._fromNode = fromNode.get_nodeOrLink();
		this._toNode = toNode.get_nodeOrLink();
		InternalRect rect = layout.GetTempRect1();
		InternalRect rect2 = layout.GetTempRect2();
		InternalRect linkConnectionRect = fromNode.GetLinkConnectionRect();
		rect.Reshape(linkConnectionRect.X, linkConnectionRect.Y,
				linkConnectionRect.Width, linkConnectionRect.Height);

		linkConnectionRect = toNode.GetLinkConnectionRect();
		rect2.Reshape(linkConnectionRect.X, linkConnectionRect.Y,
				linkConnectionRect.Width, linkConnectionRect.Height);
		Boolean flag = linkData.IsFromPointFixed();
		Boolean flag2 = linkData.IsToPointFixed();
		InternalPoint connectionPoint = linkData.GetConnectionPoint(true);
		InternalPoint point = linkData.GetConnectionPoint(false);
		if (flag) {
			rect.Add(connectionPoint);
		}
		if (flag2) {
			rect2.Add(point);
		}
		Boolean flag3 = this._fromNode == this._toNode;
		Boolean isOverlappingNodes = flag3;
		Integer fixedFromSide = linkData.IsFromSideFixed() ? linkData
				.GetSide(true)
				: (flag ? ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData
						.GetSideOfPoint(rect, connectionPoint) : 5);
		Integer fixedToSide = linkData.IsToSideFixed() ? linkData
				.GetSide(false)
				: (flag2 ? ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData
						.GetSideOfPoint(rect2, point) : 5);
		LinkShapeType[] shapeTypes = null;
		float bypassDistance = this.GetBypassDistance(linkData,
				this._bypassDistance);
		if (flag3) {

			shapeTypes = this.GetSelfLinkShapeTypes(linkData);
		} else {

			shapeTypes = this.GetShapeTypesFromRelativePositionOfNodes(
					linkData, rect, rect2, bypassDistance);
		}

		if (linkData.IsOverlapping()) {
			isOverlappingNodes = true;
		}
		LinkShapeType[] typeArray2 = this.ComputeFilteredShapeTypes(linkData,
				shapeTypes, rect, rect2, fixedFromSide, fixedToSide,
				isOverlappingNodes, bypassDistance);
		if (!isOverlappingNodes) {

			typeArray2 = this.SortShapeTypes(typeArray2, linkData,
					this._minFinalSegmentLength, typeArray2 == shapeTypes);
		}

		return typeArray2;

	}

	public abstract LinkShapeType[] GetAllQuasiSelfInterGraphLinkShapeTypes();

	public abstract LinkShapeType[] GetBottomToBottomSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetBottomToLeftSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetBottomToRightSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetBottomToTopSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public float GetBypassDistance(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			float globalBypassDistance) {

		return (globalBypassDistance + linkData.GetLinkWidth());

	}

	private LinkShapeType[] GetCompactedFilteredShapes() {
		if (this._counter < 1) {

			return null;
		}

		return this.CopyShapeTypes(this._shapeTypes, this._counter);

	}

	public abstract LinkShapeType[] GetFromBottomLeftToTopRightPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance);

	public abstract LinkShapeType[] GetFromBottomRightToTopLeftPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance);

	public abstract LinkShapeType[] GetFromBottomToTopPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance);

	public abstract LinkShapeType[] GetFromLeftToRightPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance);

	public abstract LinkShapeType[] GetFromRightToLeftPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance);

	public abstract LinkShapeType[] GetFromTopLeftToBottomRightPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance);

	public abstract LinkShapeType[] GetFromTopRightToBottomLeftPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance);

	public abstract LinkShapeType[] GetFromTopToBottomPositionTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance);

	public abstract LinkShapeType[] GetLeftToBottomSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetLeftToLeftSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetLeftToRightSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetLeftToTopSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract Integer GetMaxNumberOfBends();

	private LinkShapeType[] GetOverlapPositionTypes(InternalRect fromRect,
			InternalRect toRect, float bypassDistance) {
		if (((fromRect.X == toRect.X) && (fromRect.Width == toRect.Width))
				&& ((fromRect.Y == toRect.Y) && (fromRect.Height == toRect.Height))) {

			return OVERLAPPING_NODES_TYPES;
		}
		Boolean flag = toRect.X < fromRect.X;
		Boolean flag2 = toRect.Y < fromRect.Y;
		Boolean flag3 = (toRect.X + toRect.Width) < (fromRect.X + fromRect.Width);
		Boolean flag4 = (toRect.Y + toRect.Height) < (fromRect.Y + fromRect.Height);
		Integer counter = 0;
		if (flag3) {
			if (flag2) {
				this._shapeTypes[counter++] = IlvThreeBendRT_OVERLAP.instance;
			}
			if (!flag4) {
				this._shapeTypes[counter++] = IlvThreeBendRB_OVERLAP.instance;
			}
		} else {
			if (!flag2) {
				this._shapeTypes[counter++] = IlvThreeBendTR_OVERLAP.instance;
			}
			if (flag4) {
				this._shapeTypes[counter++] = IlvThreeBendBR_OVERLAP.instance;
			}
		}
		if (flag) {
			if (flag4) {
				this._shapeTypes[counter++] = IlvThreeBendBL_OVERLAP.instance;
			}
			if (!flag2) {
				this._shapeTypes[counter++] = IlvThreeBendTL_OVERLAP.instance;
			}
		} else {
			if (!flag4) {
				this._shapeTypes[counter++] = IlvThreeBendLB_OVERLAP.instance;
			}
			if (flag2) {
				this._shapeTypes[counter++] = IlvThreeBendLT_OVERLAP.instance;
			}
		}
		if (counter < 1) {

			return OVERLAPPING_NODES_TYPES;
		}

		return this.CopyShapeTypes(this._shapeTypes, counter);

	}

	private LinkShapeType[] GetQuasiSelfInterGraphLinkShapeTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {
		InternalRect rect = null;
		InternalRect rect2 = null;

		if (linkData.IsQuasiSelfInterGraphLinkOrigin()) {
			rect = fromRect;
			rect2 = toRect;
		} else {

			if (!linkData.IsQuasiSelfInterGraphLinkDestination()) {
				throw (new system.Exception(
						"Internal error: expected quasi self-intergraph link"));
			}
			rect = toRect;
			rect2 = fromRect;
		}
		if ((((rect.X == rect2.X) && (rect.Width == rect2.Width)) && ((rect.Y == rect2.Y) && (rect.Height == rect2.Height)))
				|| ((rect.Width <= rect2.Width) && (rect.Height <= rect2.Height))) {

			return this.GetAllQuasiSelfInterGraphLinkShapeTypes();
		}
		Boolean enoughSpaceOnLeft = rect2.X >= (rect.X + bypassDistance);
		Boolean enoughSpaceOnRight = (rect2.X + rect2.Width) <= ((rect.X + rect.Width) - bypassDistance);
		Boolean enoughSpaceOnTop = rect2.Y >= (rect.Y + bypassDistance);
		Boolean enoughSpaceOnBottom = (rect2.Y + rect2.Height) <= ((rect.Y + rect.Height) - bypassDistance);
		Integer counter = 0;
		LinkShapeType[] allQuasiSelfInterGraphLinkShapeTypes = this
				.GetAllQuasiSelfInterGraphLinkShapeTypes();
		Integer length = allQuasiSelfInterGraphLinkShapeTypes.length;
		for (Integer i = 0; i < length; i++) {
			LinkShapeType shapeType = allQuasiSelfInterGraphLinkShapeTypes[i];

			if (this.IsEnoughSpace(linkData, shapeType, rect, rect2,
					bypassDistance, enoughSpaceOnLeft, enoughSpaceOnRight,
					enoughSpaceOnTop, enoughSpaceOnBottom)) {
				this._shapeTypes[counter++] = shapeType;
			}
		}
		if (counter < 1) {

			return this.GetAllQuasiSelfInterGraphLinkShapeTypes();
		}

		return this.CopyShapeTypes(this._shapeTypes, counter);

	}

	public abstract LinkShapeType[] GetRightToBottomSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetRightToLeftSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetRightToRightSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetRightToTopSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	private LinkShapeType[] GetSelfLinkShapeTypes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData) {
		ILOG.Diagrammer.GraphLayout.Internal.SLLayout.NodeData fromNode = linkData
				.GetFromNode();
		Integer selfLinkIndex = fromNode.GetSelfLinkIndex();
		LinkShapeType[] typeArray = this._selfLinkTypes[selfLinkIndex];
		fromNode.IncrementSelfLinkIndex(this._selfLinkTypes.length - 1);

		return typeArray;

	}

	private LinkShapeType[] GetShapeTypesFromRelativePositionOfNodes(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			InternalRect fromRect, InternalRect toRect, float bypassDistance) {
		LinkShapeType[] typeArray = null;
		if (linkData.IsQuasiSelfInterGraphLinkOrigin()
				|| linkData.IsQuasiSelfInterGraphLinkDestination()) {

			return this.GetQuasiSelfInterGraphLinkShapeTypes(linkData,
					fromRect, toRect, bypassDistance);
		}
		if (toRect.X > ((fromRect.X + fromRect.Width) + bypassDistance)) {
			if (((toRect.Y + toRect.Height) + bypassDistance) < fromRect.Y) {

				return this.GetFromBottomLeftToTopRightPositionTypes(linkData,
						fromRect, toRect, bypassDistance);
			}
			if (toRect.Y > ((fromRect.Y + fromRect.Height) + bypassDistance)) {

				return this.GetFromTopLeftToBottomRightPositionTypes(linkData,
						fromRect, toRect, bypassDistance);
			}

			return this.GetFromLeftToRightPositionTypes(linkData, fromRect,
					toRect, bypassDistance);
		}
		if (((toRect.X + toRect.Width) + bypassDistance) < fromRect.X) {
			if (((toRect.Y + toRect.Height) + bypassDistance) < fromRect.Y) {

				return this.GetFromBottomRightToTopLeftPositionTypes(linkData,
						fromRect, toRect, bypassDistance);
			}
			if (toRect.Y > ((fromRect.Y + fromRect.Height) + bypassDistance)) {

				return this.GetFromTopRightToBottomLeftPositionTypes(linkData,
						fromRect, toRect, bypassDistance);
			}

			return this.GetFromRightToLeftPositionTypes(linkData, fromRect,
					toRect, bypassDistance);
		}
		if (((toRect.Y + toRect.Height) + bypassDistance) < fromRect.Y) {

			return this.GetFromBottomToTopPositionTypes(linkData, fromRect,
					toRect, bypassDistance);
		}
		if (toRect.Y > ((fromRect.Y + fromRect.Height) + bypassDistance)) {

			return this.GetFromTopToBottomPositionTypes(linkData, fromRect,
					toRect, bypassDistance);
		}

		typeArray = this.GetOverlapPositionTypes(fromRect, toRect,
				bypassDistance);
		linkData.SetOverlapping(true);

		return typeArray;

	}

	public abstract LinkShapeType[] GetTopToBottomSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetTopToLeftSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetTopToRightSideTypes(
			InternalRect fromRect, InternalRect toRect, Boolean isOverlapping,
			float bypassDistance);

	public abstract LinkShapeType[] GetTopToTopSideTypes(InternalRect fromRect,
			InternalRect toRect, Boolean isOverlapping, float bypassDistance);

	private Boolean IsAllowedByFilter(int side, Boolean origin) {
		if (this._nodeSideFilter == null) {

			return true;
		}
		java.lang.Object obj2 = this._link;
		IGraphModel graphModel = this._layout.GetGraphModel();
		java.lang.Object obj3 = origin ? this._fromNode : this._toNode;
		if (graphModel instanceof SubgraphData) {
			SubgraphData data = (SubgraphData) graphModel;
			java.lang.Object original = data.GetOriginal(obj2);
			java.lang.Object obj5 = data.GetOriginal(obj3);
			if ((original != null) && (obj5 != null)) {

				graphModel = data.GetOriginalGraphModel(obj3);
				obj2 = original;
				obj3 = obj5;
			}
		}

		return this._nodeSideFilter
				.Accept(graphModel, obj2, origin, obj3, side);

	}

	private Boolean IsEnoughSpace(Integer nodeSide, Boolean enoughSpaceOnLeft,
			Boolean enoughSpaceOnRight, Boolean enoughSpaceOnTop,
			Boolean enoughSpaceOnBottom) {
		if (nodeSide == 0) {
			if (enoughSpaceOnLeft) {
			} else

				return false;
		} else if (nodeSide == 1) {
			if (enoughSpaceOnRight) {
			} else

				return false;
		} else if (nodeSide == 2) {
			if (enoughSpaceOnTop) {
			} else

				return false;
		} else if (nodeSide == 3) {
			if (enoughSpaceOnBottom) {
			} else

				return false;
		} else {
			throw (new system.Exception("unsupported node side: " + nodeSide));
		}

		return true;

	}

	public Boolean IsEnoughSpace(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			LinkShapeType shapeType, InternalRect subgraphBox,
			InternalRect nodeBox, float bypassDistance,
			Boolean enoughSpaceOnLeft, Boolean enoughSpaceOnRight,
			Boolean enoughSpaceOnTop, Boolean enoughSpaceOnBottom) {

		return (this.IsEnoughSpace(shapeType.GetFromSide(linkData),
				enoughSpaceOnLeft, enoughSpaceOnRight, enoughSpaceOnTop,
				enoughSpaceOnBottom) && this.IsEnoughSpace(
				shapeType.GetToSide(linkData), enoughSpaceOnLeft,
				enoughSpaceOnRight, enoughSpaceOnTop, enoughSpaceOnBottom));

	}

	private void SetGlobalSelfLinkStyle(Integer selfLinkStyle) {
		if (selfLinkStyle == 5) {
			this._selfLinkTypes = TWO_BENDS_SELF_LINK_TYPES;
		} else {
			if (selfLinkStyle != 6) {
				throw (new ArgumentException("unsupported self-link style: "
						+ selfLinkStyle));
			}
			this._selfLinkTypes = THREE_BENDS_SELF_LINK_TYPES;
		}

	}

	public LinkShapeType[] SortShapeTypes(LinkShapeType[] shapeTypes,
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData,
			float minFinalSegmentLength, Boolean copyBeforeSort) {

		return shapeTypes;

	}

	public void Update(ShortLinkAlgorithm layout) {
		this._layout = layout;
		ShortLinkLayout shortLinkLayout = layout.GetShortLinkLayout();
		this._nodeSideFilter = shortLinkLayout.get_NodeSideFilter();
		this._minFinalSegmentLength = shortLinkLayout
				.get_MinFinalSegmentLength();
		this._bypassDistance = shortLinkLayout.get_BypassDistance();
		if (this._bypassDistance < 0f) {
			this._bypassDistance = this._minFinalSegmentLength;
		}
		this.SetGlobalSelfLinkStyle((Integer) shortLinkLayout
				.get_SelfLinkStyle());

	}

}
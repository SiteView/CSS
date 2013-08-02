package ILOG.Diagrammer.GraphLayout;

import system.ArgumentException;
import system.EventHandler;
import system.ComponentModel.PropertyChangedEventHandler;
import ILOG.Diagrammer.GraphLayout.Internal.SLLayout.ShortLinkAlgorithm;

public class ShortLinkLayout extends ILOG.Diagrammer.GraphLayout.GraphLayout {
	private float _bypassDistance;

	private Boolean _combinedInterGraphLinksMode = false;

	public float _defaultBypassDistance = -1f;

	public Boolean _defaultCombinedInterGraphLinksMode = true;

	public Integer _defaultDestinationPointMode = 0;

	public float _defaultEvenlySpacedPinsMarginRatio = 0.5f;

	public Integer _defaultGlobalConnectorStyle = 0;

	public Integer _defaultGlobalLinkStyle = 2;

	public Integer _defaultGlobalSelfLinkStyle = 5;

	public Boolean _defaultIncrementalMode = false;

	public Integer _defaultIncrementalModifiedLinkReshapeMode = 0;

	public Integer _defaultIncrementalUnmodifiedLinkReshapeMode = 4;

	public Boolean _defaultInterGraphLinksMode = true;

	public Integer _defaultLinkBundlesMode = 2;

	public float _defaultLinkLinkPenalty = 1f;

	public float _defaultLinkNodePenalty = 1f;

	public float _defaultLinkOffset = 2f;

	public Boolean _defaultLinkOverlapNodesForbidden = false;

	public float _defaultMinFinalSegment = 10f;

	public Integer _defaultNumberOfIterations = 3;

	public Integer _defaultOriginPointMode = 0;

	public Boolean _defaultSameShapeForMultipleLinks = false;

	private Integer _destinationPointMode;

	private float _evenlySpacedPinsMarginRatio;

	private Integer _globalConnectorStyle;

	private Integer _globalLinkStyle;

	private Integer _globalSelfLinkStyle;

	private Boolean _incrementalMode = false;

	private Integer _incrementalModifiedLinkReshapeMode;

	private Integer _incrementalUnmodifiedLinkReshapeMode;

	private Boolean _interGraphLinksMode = false;

	private Integer _linkBundlesMode;

	private float _linkLinkPenalty;

	private float _linkNodePenalty;

	private float _linkOffset;

	private Boolean _linkOverlapNodesForbidden = false;

	private float _minFinalSegment;

	private INodeBoxProvider _nodeBoxInterface;

	private INodeSideFilter _nodeSideFilter;

	private Integer _numberOfIterations;

	private Integer _originPointMode;

	private Boolean _sameShapeForMultipleLinks = false;

	private ShortLinkAlgorithm _shortLinkAlgorithm;

	private static String CONNECTOR_STYLE_PROPERTY = "ConnectorStyle";

	private static String DESTINATION_POINT_MODE_PROPERTY = "DestinationPointMode";

	private static String INCREMENTAL_MODIFIED_LINK_RESHAPE_MODE_PROPERTY = "IncrementalModifiedLinkReshapeMode";

	private static String INCREMENTAL_UNMODIFIED_LINK_RESHAPE_MODE_PROPERTY = "IncrementalUnmodifiedLinkReshapeMode";

	private static String LINK_STYLE_PROPERTY = "LinkStyle";

	private static String ORIGIN_POINT_MODE_PROPERTY = "OriginPointMode";

	public ShortLinkLayout() {
	}

	public ShortLinkLayout(ShortLinkLayout source) {
		super(source);
	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy() {

		return new ShortLinkLayout(this);

	}

	@Override
	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		super.CopyParameters(source);
		if (source instanceof ShortLinkLayout) {
			ShortLinkLayout layout = (ShortLinkLayout) source;
			this.set_LinkStyle(layout.get_LinkStyle());
			this.set_SelfLinkStyle(layout.get_SelfLinkStyle());
			this.set_OriginPointMode(layout.get_OriginPointMode());
			this.set_DestinationPointMode(layout.get_DestinationPointMode());
			this.set_ConnectorStyle(layout.get_ConnectorStyle());
			this.set_IncrementalUnmodifiedLinkReshapeMode(layout
					.get_IncrementalUnmodifiedLinkReshapeMode());
			this.set_IncrementalModifiedLinkReshapeMode(layout
					.get_IncrementalModifiedLinkReshapeMode());
			this.set_EvenlySpacedConnectorMarginRatio(layout
					.get_EvenlySpacedConnectorMarginRatio());
			this.set_LinkBundlesMode(layout.get_LinkBundlesMode());
			this.set_MinFinalSegmentLength(layout.get_MinFinalSegmentLength());
			this.set_BypassDistance(layout.get_BypassDistance());
			this.set_LinkOffset(layout.get_LinkOffset());
			this.set_LinkToNodeCrossingPenalty(layout
					.get_LinkToNodeCrossingPenalty());
			this.set_LinkToLinkCrossingPenalty(layout
					.get_LinkToLinkCrossingPenalty());
			this.set_AllowedNumberOfIterations(layout
					.get_AllowedNumberOfIterations());
			this.set_SameShapeForMultipleLinks(layout
					.get_SameShapeForMultipleLinks());
			this.set_IncrementalMode(layout.get_IncrementalMode());
			this.set_InterGraphLinksMode(layout.get_InterGraphLinksMode());
			this.set_CombinedInterGraphLinksMode(layout
					.get_CombinedInterGraphLinksMode());
			this.set_NodeSideFilter(layout.get_NodeSideFilter());
			this.set_NodeBoxProvider(layout.get_NodeBoxProvider());
			this.set_LinkOverlapNodesForbidden(layout
					.get_LinkOverlapNodesForbidden());
		}

	}

	@Override
	public void Detach() {
		if (this._shortLinkAlgorithm != null) {
			this._shortLinkAlgorithm.Detach();
		}
		super.Detach();

	}

	public LongLinkLayout GetAuxiliaryLongLinkLayout() {

		return this._shortLinkAlgorithm.GetAuxiliaryLongLinkLayout();

	}

	public int GetConnectorStyle(java.lang.Object node) {

		return (int) LayoutParametersUtil.GetNodeParameter(this, node,
				CONNECTOR_STYLE_PROPERTY, 0);

	}

	public int GetDestinationPointMode(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				DESTINATION_POINT_MODE_PROPERTY, 0);

	}

	public int GetIncrementalModifiedLinkReshapeMode(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				INCREMENTAL_MODIFIED_LINK_RESHAPE_MODE_PROPERTY, 0);

	}

	public int GetIncrementalUnmodifiedLinkReshapeMode(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				INCREMENTAL_UNMODIFIED_LINK_RESHAPE_MODE_PROPERTY, 4);

	}

	public int GetLinkStyle(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				LINK_STYLE_PROPERTY, 2);

	}

	public int GetOriginPointMode(java.lang.Object link) {

		return (int) LayoutParametersUtil.GetLinkParameter(this, link,
				ORIGIN_POINT_MODE_PROPERTY, 0);

	}

	@Override
	public void Init() {
		super.Init();
		this._globalLinkStyle = 2;
		this._globalSelfLinkStyle = 5;
		this._originPointMode = 0;
		this._destinationPointMode = 0;
		this._globalConnectorStyle = 0;
		this._incrementalUnmodifiedLinkReshapeMode = 4;
		this._incrementalModifiedLinkReshapeMode = 0;
		this._evenlySpacedPinsMarginRatio = 0.5f;
		this._linkBundlesMode = 2;
		this._minFinalSegment = 10f;
		this._bypassDistance = -1f;
		this._linkOffset = 2f;
		this._linkNodePenalty = 1f;
		this._linkLinkPenalty = 1f;
		this._numberOfIterations = 3;
		this._sameShapeForMultipleLinks = false;
		this._incrementalMode = false;
		this._interGraphLinksMode = true;
		this._combinedInterGraphLinksMode = true;
		this._linkOverlapNodesForbidden = false;
		this._shortLinkAlgorithm = new ShortLinkAlgorithm(this);

	}

	private Boolean IsFromPointFixed(java.lang.Object link) {
		int originPointMode = this.get_OriginPointMode();
		if (originPointMode != ConnectionPointMode.Free) {
			if (originPointMode == ConnectionPointMode.Fixed) {

				return true;
			}
			if (originPointMode != ConnectionPointMode.Mixed) {
				throw (new system.Exception("unsupported mode: "
						+ ((Integer) this.get_OriginPointMode())));
			}
		} else {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, true);
		}
		if (this.GetOriginPointMode(link) != ConnectionPointMode.Fixed) {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, true);
		}

		return true;

	}

	public Boolean IsLayoutTimeElapsedPackage() {

		return this.IsLayoutTimeElapsed();

	}

	private Boolean IsToPointFixed(java.lang.Object link) {
		int destinationPointMode = this.get_DestinationPointMode();
		if (destinationPointMode != ConnectionPointMode.Free) {
			if (destinationPointMode == ConnectionPointMode.Fixed) {

				return true;
			}
			if (destinationPointMode != ConnectionPointMode.Mixed) {
				throw (new system.Exception("unsupported mode: "
						+ ((Integer) this.get_DestinationPointMode())));
			}
		} else {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, false);
		}
		if (this.GetDestinationPointMode(link) != ConnectionPointMode.Fixed) {

			return this.GetGraphModel().HasPinnedConnectionPoint(link, false);
		}

		return true;

	}

	@Override
	public void Layout() {
		if (this.get_LinkStyle() != LinkLayoutLinkStyle.NoReshape) {
			this._shortLinkAlgorithm.Layout(false);
		}

	}

	public void SetAutoLayoutDuringRead(Boolean enable) {
		this.set_AutoLayout(this.get_AutoLayout() || enable);

	}

	public void SetConnectorStyle(java.lang.Object node, int style) {
		if (((style != ShortLinkLayoutConnectorStyle.Automatic) && (style != ShortLinkLayoutConnectorStyle.FixedOffset))
				&& (style != ShortLinkLayoutConnectorStyle.EvenlySpaced)) {
			throw (new ArgumentException("unsupported connector style: "
					+ ((Integer) style)));
		}
		LayoutParametersUtil.SetNodeParameter(this, node,
				CONNECTOR_STYLE_PROPERTY, (int) style, 0);

	}

	public void SetDestinationPointMode(java.lang.Object link, int mode) {
		if ((mode != ConnectionPointMode.Free)
				&& (mode != ConnectionPointMode.Fixed)) {
			throw (new ArgumentException("Unsupported mode: "
					+ ((Integer) mode)));
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				DESTINATION_POINT_MODE_PROPERTY, (int) mode, 0);

	}

	public void SetIncrementalModifiedLinkReshapeMode(java.lang.Object link,
			int mode) {
		if ((((mode != ShortLinkLayoutLinkReshapeMode.Free) && (mode != ShortLinkLayoutLinkReshapeMode.FixedShapeType)) && ((mode != ShortLinkLayoutLinkReshapeMode.FixedNodeSides) && (mode != ShortLinkLayoutLinkReshapeMode.FixedConnectionPoints)))
				&& (mode != ShortLinkLayoutLinkReshapeMode.Fixed)) {
			throw (new ArgumentException("unsupported mode: "
					+ ((Integer) mode)));
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				INCREMENTAL_MODIFIED_LINK_RESHAPE_MODE_PROPERTY, (int) mode, 0);

	}

	public void SetIncrementalUnmodifiedLinkReshapeMode(java.lang.Object link,
			int mode) {
		if ((((mode != ShortLinkLayoutLinkReshapeMode.Free) && (mode != ShortLinkLayoutLinkReshapeMode.FixedShapeType)) && ((mode != ShortLinkLayoutLinkReshapeMode.FixedNodeSides) && (mode != ShortLinkLayoutLinkReshapeMode.FixedConnectionPoints)))
				&& (mode != ShortLinkLayoutLinkReshapeMode.Fixed)) {
			throw (new ArgumentException("unsupported mode: "
					+ ((Integer) mode)));
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				INCREMENTAL_UNMODIFIED_LINK_RESHAPE_MODE_PROPERTY, (int) mode,
				4);

	}

	public void SetLinkStyle(java.lang.Object link, int style) {
		if (((style != LinkLayoutLinkStyle.Orthogonal) && (style != LinkLayoutLinkStyle.NoReshape))
				&& (style != LinkLayoutLinkStyle.Direct)) {
			throw (new ArgumentException("unsupported link style: "
					+ ((Integer) style)));
		}
		LayoutParametersUtil.SetLinkParameter(this, link, LINK_STYLE_PROPERTY,
				(int) style, 2);

	}

	public void SetOriginPointMode(java.lang.Object link, int mode) {
		if ((mode != ConnectionPointMode.Free)
				&& (mode != ConnectionPointMode.Fixed)) {
			throw (new ArgumentException("Unsupported mode: " + ((int) mode)));
		}
		LayoutParametersUtil.SetLinkParameter(this, link,
				ORIGIN_POINT_MODE_PROPERTY, (int) mode, 0);

	}

	@Override
	public Boolean StopImmediately() {
		this._shortLinkAlgorithm.StopImmediately();

		return super.StopImmediately();

	}

	@Override
	public Boolean SupportsAllowedTime() {

		return true;

	}

	@Override
	public Boolean SupportsLinkConnectionBox() {

		return true;

	}

	@Override
	public Boolean SupportsPreserveFixedLinks() {

		return true;

	}

	@Override
	public Boolean SupportsStopImmediately() {

		return true;

	}

	public Integer get_AllowedNumberOfIterations() {

		return this._numberOfIterations;
	}

	public void set_AllowedNumberOfIterations(Integer value) {
		if (value < 0) {
			throw (new ArgumentException(
					"iterations number cannot be negative!"));
		}
		if (this._numberOfIterations != value) {
			this._numberOfIterations = value;
			this.OnParameterChanged("AllowedNumberOfIterations");
		}
	}

	public long get_AllowedTime() {

		return super.get_AllowedTime();
	}

	public void set_AllowedTime(long value) {
		super.set_AllowedTime(value);
	}

	public float get_BypassDistance() {

		return this._bypassDistance;
	}

	public void set_BypassDistance(float value) {
		if (this._bypassDistance != value) {
			this._bypassDistance = value;
			this.OnParameterChanged("BypassDistance");
		}
	}

	public Boolean get_CombinedInterGraphLinksMode() {

		return this._combinedInterGraphLinksMode;
	}

	public void set_CombinedInterGraphLinksMode(Boolean value) {
		if (value != this._combinedInterGraphLinksMode) {
			this._combinedInterGraphLinksMode = value;
			this.OnParameterChanged("CombinedInterGraphLinksMode");
		}
	}

	public int get_ConnectorStyle() {

		return (int) this._globalConnectorStyle;
	}

	public void set_ConnectorStyle(int value) {
		if (((value != ShortLinkLayoutConnectorStyle.Automatic) && (value != ShortLinkLayoutConnectorStyle.FixedOffset))
				&& ((value != ShortLinkLayoutConnectorStyle.EvenlySpaced) && (value != ShortLinkLayoutConnectorStyle.Mixed))) {
			throw (new ArgumentException("unsupported connector style: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._globalConnectorStyle) {
			this._globalConnectorStyle = (int) value;
			this.OnParameterChanged("GlobalConnectorStyle");
		}
	}

	public int get_DestinationPointMode() {

		return (int) this._destinationPointMode;
	}

	public void set_DestinationPointMode(int value) {
		if (((value != ConnectionPointMode.Free) && (value != ConnectionPointMode.Fixed))
				&& (value != ConnectionPointMode.Mixed)) {
			throw (new ArgumentException("Unsupported mode: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._destinationPointMode) {
			this._destinationPointMode = (Integer) value;
			this.OnParameterChanged("GlobalDestinationPointMode");
		}
	}

	public float get_EvenlySpacedConnectorMarginRatio() {

		return this._evenlySpacedPinsMarginRatio;
	}

	public void set_EvenlySpacedConnectorMarginRatio(float value) {
		if (value < 0f) {
			throw (new ArgumentException("ratio cannot be negative: " + value));
		}
		if (value != this._evenlySpacedPinsMarginRatio) {
			this._evenlySpacedPinsMarginRatio = value;
			this.OnParameterChanged("EvenlySpacedPinsMarginRatio");
		}
	}

	public Boolean get_IncrementalMode() {

		return this._incrementalMode;
	}

	public void set_IncrementalMode(Boolean value) {
		if (value != this._incrementalMode) {
			this._incrementalMode = value;
			this.OnParameterChanged("IncrementalMode");
		}
	}

	public int get_IncrementalModifiedLinkReshapeMode() {

		return (int) this._incrementalModifiedLinkReshapeMode;
	}

	public void set_IncrementalModifiedLinkReshapeMode(int value) {
		if ((((value != ShortLinkLayoutLinkReshapeMode.Free) && (value != ShortLinkLayoutLinkReshapeMode.FixedShapeType)) && ((value != ShortLinkLayoutLinkReshapeMode.FixedNodeSides) && (value != ShortLinkLayoutLinkReshapeMode.FixedConnectionPoints)))
				&& ((value != ShortLinkLayoutLinkReshapeMode.Fixed) && (value != ShortLinkLayoutLinkReshapeMode.Mixed))) {
			throw (new ArgumentException("unsupported mode: " + ((int) value)));
		}
		if ((Integer) value != this._incrementalModifiedLinkReshapeMode) {
			this._incrementalModifiedLinkReshapeMode = (Integer) value;
			this.OnParameterChanged("GlobalIncrementalModifiedLinkReshapeMode");
		}
	}

	public int get_IncrementalUnmodifiedLinkReshapeMode() {

		return (int) this._incrementalUnmodifiedLinkReshapeMode;
	}

	public void set_IncrementalUnmodifiedLinkReshapeMode(int value) {
		if ((((value != ShortLinkLayoutLinkReshapeMode.Free) && (value != ShortLinkLayoutLinkReshapeMode.FixedShapeType)) && ((value != ShortLinkLayoutLinkReshapeMode.FixedNodeSides) && (value != ShortLinkLayoutLinkReshapeMode.FixedConnectionPoints)))
				&& ((value != ShortLinkLayoutLinkReshapeMode.Fixed) && (value != ShortLinkLayoutLinkReshapeMode.Mixed))) {
			throw (new ArgumentException("unsupported mode: " + ((int) value)));
		}
		if ((Integer) value != this._incrementalUnmodifiedLinkReshapeMode) {
			this._incrementalUnmodifiedLinkReshapeMode = (int) value;
			this.OnParameterChanged("GlobalIncrementalUnmodifiedLinkReshapeMode");
		}
	}

	public Boolean get_InterGraphLinksMode() {

		return this._interGraphLinksMode;
	}

	public void set_InterGraphLinksMode(Boolean value) {
		if (value != this._interGraphLinksMode) {
			this._interGraphLinksMode = value;
			this.OnParameterChanged("InterGraphLinksMode");
		}
	}

	public int get_LinkBundlesMode() {

		return (int) this._linkBundlesMode;
	}

	public void set_LinkBundlesMode(int value) {
		if (((value != ShortLinkLayoutLinkBundlesMode.NoBundle) && (value != ShortLinkLayoutLinkBundlesMode.FirstLastSegment))
				&& (value != ShortLinkLayoutLinkBundlesMode.ImprovedFirstLastSegment)) {
			throw (new ArgumentException("unsupported link bundles mode: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._linkBundlesMode) {
			this._linkBundlesMode = (Integer) value;
			this.OnParameterChanged("LinkBundlesMode");
		}
	}

	public ILinkConnectionBoxProvider get_LinkConnectionBoxProvider() {

		return super.get_LinkConnectionBoxProvider();
	}

	public void set_LinkConnectionBoxProvider(ILinkConnectionBoxProvider value) {
		ILinkConnectionBoxProvider provider = value;
		super.set_LinkConnectionBoxProvider(provider);
	}

	public float get_LinkOffset() {

		return this._linkOffset;
	}

	public void set_LinkOffset(float value) {
		if (this._linkOffset != value) {
			this._linkOffset = value;
			this.OnParameterChanged("LinkOffset");
		}
	}

	public Boolean get_LinkOverlapNodesForbidden() {

		return this._linkOverlapNodesForbidden;
	}

	public void set_LinkOverlapNodesForbidden(Boolean value) {
		if (value != this._linkOverlapNodesForbidden) {
			this._linkOverlapNodesForbidden = value;
			this.OnParameterChanged("LinkOverlapNodesForbidden");
		}
	}

	public int get_LinkStyle() {

		return (int) this._globalLinkStyle;
	}

	public void set_LinkStyle(int value) {
		if (((value != LinkLayoutLinkStyle.Direct) && (value != LinkLayoutLinkStyle.Orthogonal))
				&& ((value != LinkLayoutLinkStyle.Mixed) && (value != LinkLayoutLinkStyle.NoReshape))) {
			throw (new ArgumentException("unsupported link style: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._globalLinkStyle) {
			this._globalLinkStyle = (Integer) value;
			if (this._globalLinkStyle == 0x63) {
				this._shortLinkAlgorithm.NotifyMixedLinkStyle();
			}
			this.OnParameterChanged("GlobalLinkStyle");
		}
	}

	public float get_LinkToLinkCrossingPenalty() {

		return this._linkLinkPenalty;
	}

	public void set_LinkToLinkCrossingPenalty(float value) {
		if (this._linkLinkPenalty != value) {
			this._linkLinkPenalty = value;
			this.OnParameterChanged("LinkToLinkCrossingPenalty");
		}
	}

	public float get_LinkToNodeCrossingPenalty() {

		return this._linkNodePenalty;
	}

	public void set_LinkToNodeCrossingPenalty(float value) {
		if (this._linkNodePenalty != value) {
			this._linkNodePenalty = value;
			this.OnParameterChanged("LinkToNodeCrossingPenalty");
		}
	}

	public float get_MinFinalSegmentLength() {

		return this._minFinalSegment;
	}

	public void set_MinFinalSegmentLength(float value) {
		if (this._minFinalSegment != value) {
			this._minFinalSegment = value;
			this.OnParameterChanged("MinFinalSegmentLength");
		}
	}

	public INodeBoxProvider get_NodeBoxProvider() {

		return this._nodeBoxInterface;
	}

	public void set_NodeBoxProvider(INodeBoxProvider value) {
		if (this._nodeBoxInterface != value) {
			this._nodeBoxInterface = value;
			this.OnParameterChanged("NodeBoxInterface");
		}
	}

	public INodeSideFilter get_NodeSideFilter() {

		return this._nodeSideFilter;
	}

	public void set_NodeSideFilter(INodeSideFilter value) {
		if (value != this._nodeSideFilter) {
			this._nodeSideFilter = value;
			this.OnParameterChanged("NodeSideFilter");
		}
	}

	public int get_OriginPointMode() {

		return (int) this._originPointMode;
	}

	public void set_OriginPointMode(int value) {
		if (((value != ConnectionPointMode.Free) && (value != ConnectionPointMode.Fixed))
				&& (value != ConnectionPointMode.Mixed)) {
			throw (new ArgumentException("Unsupported mode: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._originPointMode) {
			this._originPointMode = (Integer) value;
			this.OnParameterChanged("GlobalOriginPointMode");
		}
	}

	public Boolean get_PreserveFixedLinks() {

		return super.get_PreserveFixedLinks();
	}

	public void set_PreserveFixedLinks(Boolean value) {
		super.set_PreserveFixedLinks(value);
	}

	public Boolean get_SameShapeForMultipleLinks() {

		return this._sameShapeForMultipleLinks;
	}

	public void set_SameShapeForMultipleLinks(Boolean value) {
		if (value != this._sameShapeForMultipleLinks) {
			this._sameShapeForMultipleLinks = value;
			this.OnParameterChanged("SameShapeForMultipleLinks");
		}
	}

	public int get_SelfLinkStyle() {

		return (int) this._globalSelfLinkStyle;
	}

	public void set_SelfLinkStyle(int value) {
		if ((value != ShortLinkLayoutSelfLinkStyle.TwoBendsOrthogonal)
				&& (value != ShortLinkLayoutSelfLinkStyle.ThreeBendsOrthogonal)) {
			throw (new ArgumentException("unsupported self-link style: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._globalSelfLinkStyle) {
			this._globalSelfLinkStyle = (int) value;
			this.OnParameterChanged("GlobalSelfLinkStyle");
		}
	}

}
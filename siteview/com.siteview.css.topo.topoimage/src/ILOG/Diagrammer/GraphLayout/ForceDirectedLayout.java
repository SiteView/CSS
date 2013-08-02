package ILOG.Diagrammer.GraphLayout;

import java.awt.List;
import java.util.ArrayList;
import java.util.Iterator;

import system.ArgumentException;
import system.Math;
import system.Random;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.GraphLayout.Internal.GraphLayoutData;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.LayoutUtil;
import ILOG.Diagrammer.GraphLayout.Internal.PositionData;
import ILOG.Diagrammer.GraphLayout.Internal.TopologicalData;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public class ForceDirectedLayout extends GraphLayout {
	private float _additionalNodeRepulsionWeight;

	private float _convergenceThreshold;

	public float _defaultAdditionalNodeRepulsionWeight = 0.2f;

	public float _defaultConvergenceThreshold = 1f;

	public Boolean _defaultIsForceFitToLayoutRegion = false;

	public float _defaultLinkLengthWeight = 1f;

	public Integer _defaultLinkStyle = 1;

	public float _defaultMaxAllowedMovePerIteration = 5f;

	public float _defaultNodeDistanceThreshold = 30f;

	public Integer _defaultNumberOfIterations = 0x3e8;

	public float _defaultPreferredIndividualLinksLength = -1f;

	public float _defaultPreferredLinksLength = 60f;

	public Boolean _defaultRespectNodeSizes = true;

	private IGraphModel _graphModel;

	private Boolean _hasConverged = false;

	private Boolean _isForceFitToLayoutRegion = false;

	private GraphLayoutData _layoutData;

	private ForceDirectedLayoutReport _layoutReport;

	private float _limitedValue;

	private float _linkLengthWeight;

	private Integer _linkStyle;

	private float _maxAllowedMovePerIteration;

	private float _maxMovement;

	private float _nodeDistanceThreshold;

	private Boolean _nodesAlreadyPlaced = false;

	private Integer _numberOfIterations;

	private PositionData _positionData;

	private float _preferredLinksLength;

	private Boolean _respectNodeSizes = false;

	private TopologicalData _topologicalData;

	private Integer[] _vectFromId;

	private float[] _vectHalfNodeDiag;

	private float[] _vectNodesDx;

	private float[] _vectNodesDy;

	private Integer[] _vectToId;

	private float[] _vectWishedLength;

	private Integer ALLOWED_NUMBER_OF_ITERATIONS_FOR_FINETUNING = 20;

	private Integer ANIMATION_REDRAW_RATE = 8;

	private Integer ITER_BIG_EPSILON = 60;

	private Integer ITER_SMALL_EPSILON = 20;

	private static String LINK_LENGTH_PROPERTY = "PreferredLength";

	private static String NO_CONVERGENCE_MESSAGE = "Cannot reach convergence";

	public ForceDirectedLayout() {
	}

	public ForceDirectedLayout(ForceDirectedLayout source) {
		super(source);
	}

	private void ApplyNodeDeplacement(InternalRect rect,
			Boolean fitToLayoutRegion, float maxAllowedMove) {
		Integer numberOfNodes = this._topologicalData.GetNumberOfNodes();
		Boolean preserveFixedNodes = this.get_PreserveFixedNodes();
		for (Integer i = 0; i < numberOfNodes; i++) {

			if (!preserveFixedNodes
					|| !this._positionData.HasBeenIdentifiedAsFixed(i)) {
				float deltaX = this.LimitedValue((double) this._vectNodesDx[i],
						maxAllowedMove);
				float deltaY = this.LimitedValue((double) this._vectNodesDy[i],
						maxAllowedMove);
				float x = this._positionData.GetXofNode(i) + deltaX;
				float y = this._positionData.GetYofNode(i) + deltaY;
				if (fitToLayoutRegion) {
					if (x < rect.X) {
						x = rect.X;
					} else if (x > (rect.X + rect.Width)) {
						x = rect.X + rect.Width;
					}
					if (y < rect.Y) {
						y = rect.Y;
					} else if (y > (rect.Y + rect.Height)) {
						y = rect.Y + rect.Height;
					}
					this._positionData.RecordMove(
							x - this._positionData.GetXofNode(i), y
									- this._positionData.GetYofNode(i));
				} else {
					this._positionData.RecordMove(deltaX, deltaY);
				}
				this._positionData.SetNodePosition(i, x, y);
			}
			this._vectNodesDx[i] /= 2f;
			this._vectNodesDy[i] /= 2f;
		}
		super.OnLayoutStepPerformedIfNeeded();

	}

	private void ComputeFromToIds() {
		Integer numberOfLinks = this._topologicalData.GetNumberOfLinks();
		for (Integer i = 0; i < numberOfLinks; i++) {
			java.lang.Object link = this._topologicalData.GetLink(i);

			this._vectFromId[i] = this._layoutData
					.GetIntIdentifier(this._graphModel.GetFrom(link));

			this._vectToId[i] = this._layoutData
					.GetIntIdentifier(this._graphModel.GetTo(link));
		}
		super.OnLayoutStepPerformedIfNeeded();

	}

	private void ComputeLinkForces(float linkLengthWeight,
			float convergenceThreshold, Random random) {
		if (linkLengthWeight != 0f) {
			linkLengthWeight /= 3f;
			Integer numberOfLinks = this._topologicalData.GetNumberOfLinks();
			float distX = 0f;
			float distY = 0f;
			double num4 = 0.0;
			float range = convergenceThreshold * 2f;
			for (Integer i = 0; i < numberOfLinks; i++) {
				Integer num6 = this._vectFromId[i];
				Integer num7 = this._vectToId[i];
				if (num7 != num6) {

					distX = this._positionData.GetDistX(num7, num6);

					distY = this._positionData.GetDistY(num7, num6);
					float num11 = distX;
					float num12 = distY;
					if (num11 < 0f) {
						num11 = -num11;
					}
					if (num12 < 0f) {
						num12 = -num12;
					}
					if ((num11 < 1E-20f) && (num12 < 1E-20f)) {

						distX = this.GetRandomValue(range, random);

						distY = this.GetRandomValue(range, random);
					}

					num4 = Math
							.Sqrt((double) ((distX * distX) + (distY * distY)));
					num4 = (num4 == 0.0) ? 0.0001 : num4;
					float num8 = (float) ((linkLengthWeight * (this._vectWishedLength[i] - num4)) / num4);
					float num9 = this.LimitedValue((double) (num8 * distX),
							this._limitedValue);
					float num10 = this.LimitedValue((double) (num8 * distY),
							this._limitedValue);
					this._vectNodesDx[num7] += num9;
					this._vectNodesDy[num7] += num10;
					this._vectNodesDx[num6] -= num9;
					this._vectNodesDy[num6] -= num10;
				}
			}
		}

	}

	private void ComputeNodeForces(float minNodeDist,
			float additionalNodeRepulsionWeight, float convergenceThreshold,
			Boolean respectNodeSizes, Random random) {
		float distX = 0f;
		float distY = 0f;
		float num3 = 0f;
		float num4 = 0f;
		float num5 = 0f;
		float num6 = 0f;
		float num10 = additionalNodeRepulsionWeight / 3f;
		float num11 = minNodeDist;
		float range = convergenceThreshold * 2f;
		Integer numberOfNodes = this._topologicalData.GetNumberOfNodes();
		for (Integer i = 0; i < numberOfNodes; i++) {
			num3 = 0f;
			num4 = 0f;
			for (Integer j = 0; j < numberOfNodes; j++) {
				if (i != j) {

					distX = this._positionData.GetDistX(i, j);

					distY = this._positionData.GetDistY(i, j);
					double num8 = (distX * distX) + (distY * distY);
					if (num10 > 0f) {
						double num9 = Math.Sqrt(num8);
						num9 = (num9 == 0.0) ? 0.0001 : num9;
						if (respectNodeSizes) {
							num11 = (minNodeDist + this._vectHalfNodeDiag[i])
									+ this._vectHalfNodeDiag[j];
						}
						if (num9 < num11) {
							float num7 = (float) (num10 * ((num11 - num9) / num9));

							num5 = this.LimitedValue((double) (num7 * distX),
									this._limitedValue);

							num6 = this.LimitedValue((double) (num7 * distY),
									this._limitedValue);
							this._vectNodesDx[i] += num5;
							this._vectNodesDy[i] += num6;
							this._vectNodesDx[j] -= num5;
							this._vectNodesDy[j] -= num6;
						}
					}
					if (num8 <= 9.9999996826552254E-21) {

						num3 += this.GetRandomValue(range, random);

						num4 += this.GetRandomValue(range, random);
						num3 += distX / 3f;
						num4 += distY / 3f;
					} else {

						num3 += this.LimitedValue(((double) distX) / num8,
								this._limitedValue);

						num4 += this.LimitedValue(((double) distY) / num8,
								this._limitedValue);
					}
				}
			}
			double d = (num3 * num3) + (num4 * num4);
			if (d <= 9.9999996826552254E-21) {

				this._vectNodesDx[i] += this.GetRandomValue(range, random);

				this._vectNodesDy[i] += this.GetRandomValue(range, random);
			} else {
				d = Math.Sqrt(d) / 2.0;
				if (d > 9.9999996826552254E-21) {

					this._vectNodesDx[i] += this.LimitedValue(((double) num3)
							/ d, this._limitedValue);

					this._vectNodesDy[i] += this.LimitedValue(((double) num4)
							/ d, this._limitedValue);
				}
			}
		}
		super.OnLayoutStepPerformedIfNeeded();

	}

	private void ComputeNodeHalfDiagonals(float[] vectHalfNodeDiag,
			Integer nNodes) {
		for (Integer i = 0; i < nNodes; i++) {
			vectHalfNodeDiag[i] = 0.5f * LayoutUtil.GetDiagonal(
					this._topologicalData.GetNode(i), this._graphModel);
		}

	}

	private float ComputeWishedLength(Boolean respectNodeSizes) {
		if (respectNodeSizes) {
			Integer numberOfNodes = this._topologicalData.GetNumberOfNodes();
			this.ComputeNodeHalfDiagonals(this._vectHalfNodeDiag, numberOfNodes);
		}
		Integer numberOfLinks = this._topologicalData.GetNumberOfLinks();
		float num6 = 0f;
		float preferredLinksLength = this.get_PreferredLinksLength();
		for (Integer i = 0; i < numberOfLinks; i++) {
			java.lang.Object link = this._topologicalData.GetLink(i);
			Integer index = this._vectFromId[i];
			Integer num5 = this._vectToId[i];
			if (index != num5) {
				float preferredLength = this.GetPreferredLength(link);
				this._vectWishedLength[i] = (preferredLength >= 0f) ? preferredLength
						: preferredLinksLength;
				if (respectNodeSizes) {
					this._vectWishedLength[i] += this._vectHalfNodeDiag[index]
							+ this._vectHalfNodeDiag[num5];
				}
				if (this._vectWishedLength[i] > num6) {
					num6 = this._vectWishedLength[i];
				}
			}
		}
		super.OnLayoutStepPerformedIfNeeded();

		return num6;

	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy() {

		return new ForceDirectedLayout(this);

	}

	@Override
	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		super.CopyParameters(source);
		if (source instanceof ForceDirectedLayout) {
			ForceDirectedLayout layout = (ForceDirectedLayout) source;
			this.set_AllowedNumberOfIterations(layout
					.get_AllowedNumberOfIterations());
			this.set_ConvergenceThreshold(layout.get_ConvergenceThreshold());
			this.set_MaxAllowedMovePerIteration(layout
					.get_MaxAllowedMovePerIteration());
			this.set_PreferredLinksLength(layout.get_PreferredLinksLength());
			this.set_NodeDistanceThreshold(layout.get_NodeDistanceThreshold());
			this.set_ForceFitToLayoutRegion(layout.get_ForceFitToLayoutRegion());
			this.set_RespectNodeSizes(layout.get_RespectNodeSizes());
			this.set_LinkLengthWeight(layout.get_LinkLengthWeight());
			this.set_AdditionalNodeRepulsionWeight(layout
					.get_AdditionalNodeRepulsionWeight());
			this.set_LinkStyle(layout.get_LinkStyle());
		}

	}

	@Override
	public GraphLayoutReport CreateLayoutReport() {

		return new ForceDirectedLayoutReport();

	}

	@Override
	public void Detach() {
		super.Detach();
		this._vectNodesDx = null;
		this._vectNodesDy = null;
		this._vectFromId = null;
		this._vectToId = null;
		this._vectWishedLength = null;
		this._vectHalfNodeDiag = null;
		this._layoutData = null;
		this._topologicalData = null;
		this._positionData = null;

	}

	private Integer DoIterations(Integer allowedNumberOfIterations,
			InternalRect rect, InternalPoint centerPoint,
			Boolean fitToLayoutRegion, float additionalNodeRepulsionWeight,
			Boolean respectNodeSizes, Random random, Boolean redraw) {
		float nodeDistanceThreshold = this.get_NodeDistanceThreshold();
		Boolean flag = false;
		float convergenceThreshold = this.get_ConvergenceThreshold();
		float num3 = 2f * convergenceThreshold;
		float maxAllowedMovePerIteration = this
				.get_MaxAllowedMovePerIteration();
		float linkLengthWeight = this.get_LinkLengthWeight();
		Integer num6 = 0;
		Integer num7 = 0;
		Integer currentNumberOfIterations = 0;

		while (this.MayContinue(currentNumberOfIterations,
				allowedNumberOfIterations)) {
			this._positionData.ResetMaxMovementCounter();
			this.PerformOneIteration(rect, nodeDistanceThreshold,
					fitToLayoutRegion, maxAllowedMovePerIteration,
					linkLengthWeight, additionalNodeRepulsionWeight,
					convergenceThreshold, respectNodeSizes, random);
			this._nodesAlreadyPlaced = false;

			this._maxMovement = this._positionData.GetMaxMovement();
			if ((flag && (this._maxMovement > convergenceThreshold))
					&& ((currentNumberOfIterations % 8) == 0)) {

				if (!this._positionData.PlaceNodesAtPositionInAnimation(redraw,
						centerPoint)) {
					throw (new GraphLayoutException(NO_CONVERGENCE_MESSAGE));
				}
				this._nodesAlreadyPlaced = true;
			}
			this._layoutReport
					.SetNumberOfIterations(currentNumberOfIterations + 1);
			this._layoutReport.SetMaxMovePerIteration(this._maxMovement);
			super.OnLayoutStepPerformedIfNeeded();
			currentNumberOfIterations++;
			if (this._maxMovement <= convergenceThreshold) {
				if (num6 > 20) {
					this._hasConverged = true;

					return currentNumberOfIterations;
				}
				num6++;
			} else {
				num6 = 0;
			}
			if (this._maxMovement <= num3) {
				if (num7 > 60) {
					this._hasConverged = true;

					return currentNumberOfIterations;
				}
				num7++;
			} else {
				num7 = 0;
			}
		}

		return currentNumberOfIterations;

	}

	// ------
	private void DoLayout(Boolean redraw) {
		InternalRect modifiedLayoutRegion = null;
		Boolean forceFitToLayoutRegion = this.get_ForceFitToLayoutRegion();
		if (forceFitToLayoutRegion) {

			modifiedLayoutRegion = this._topologicalData
					.GetModifiedLayoutRegion(TranslateUtil
							.Rectangle2D2InternalRect(this
									.GetCalcLayoutRegion()));
		}
		if (this.get_LinkStyle() == ForceDirectedLayoutLinkStyle.Straight) {
			GraphModelUtil.DeleteIntermediatePointsOnLinks(this._graphModel,
					this);
		}
		this.ComputeFromToIds();
		Boolean respectNodeSizes = this.get_RespectNodeSizes();
		float num = this.ComputeWishedLength(respectNodeSizes);
		this._limitedValue = num * 80f;
		if (this._limitedValue < 1000f) {
			this._limitedValue = 1000f;
		}
		InternalPoint centerPoint = null;
		if (this._positionData.GetNumberOfFixedNodes() == 0) {
			centerPoint = forceFitToLayoutRegion ? new InternalPoint(
					modifiedLayoutRegion.X
							+ (modifiedLayoutRegion.Width * 0.5f),
					modifiedLayoutRegion.Y
							+ (modifiedLayoutRegion.Height * 0.5f))
					: this._positionData.GetNodesBarycenter();
		}
		Random random = new Random(0);
		float additionalNodeRepulsionWeight = 0f;
		Integer allowedNumberOfIterations = this
				.get_AllowedNumberOfIterations();
		this.InitBeforeIterations();
		Integer number = this
				.DoIterations(allowedNumberOfIterations, modifiedLayoutRegion,
						centerPoint, forceFitToLayoutRegion,
						additionalNodeRepulsionWeight, respectNodeSizes,
						random, redraw);
		additionalNodeRepulsionWeight = this
				.get_AdditionalNodeRepulsionWeight();
		if (additionalNodeRepulsionWeight > 0f) {
			this.DoIterations(20, modifiedLayoutRegion, centerPoint,
					forceFitToLayoutRegion, additionalNodeRepulsionWeight,
					respectNodeSizes, random, redraw);
		}

		if (!this._nodesAlreadyPlaced
				&& !this._positionData
						.PlaceNodesAtPosition(redraw, centerPoint)) {
			throw (new GraphLayoutException(NO_CONVERGENCE_MESSAGE));
		}
		this._layoutReport
				.set_Code(this._hasConverged ? GraphLayoutReportCode.LayoutDone
						: GraphLayoutReportCode.StoppedAndValid);
		this._layoutReport.SetNumberOfIterations(number);
		this._layoutReport.SetMaxMovePerIteration(this._maxMovement);

	}

	public float GetPreferredLength(java.lang.Object link) {

		return LayoutParametersUtil.GetLinkParameter(this, link,
				LINK_LENGTH_PROPERTY, (float) -1f);

	}

	private float GetRandomValue(float range, Random random) {
		float num = (((float) random.NextDouble()) * range) - (range * 0.5f);
		if (Math.Abs(num) >= 1E-20f) {

			return num;
		}

		return this.GetRandomValue(range, random);

	}

	@Override
	public void Init() {
		super.Init();
		this._numberOfIterations = 0x3e8;
		this._convergenceThreshold = 1f;
		this._maxAllowedMovePerIteration = 5f;
		this._preferredLinksLength = 60f;
		this._nodeDistanceThreshold = 30f;
		this._isForceFitToLayoutRegion = false;
		this._respectNodeSizes = true;
		this._linkLengthWeight = 1f;
		this._additionalNodeRepulsionWeight = 0.2f;
		this._linkStyle = 1;

	}

	private void InitBeforeIterations() {
		this._maxMovement = 0f;
		this._nodesAlreadyPlaced = false;
		this._hasConverged = false;

	}

	private void InitializeDataStructures() {
		if (this._positionData == null) {
			throw (new GraphLayoutException("public error 1"));
		}

		if (!this._positionData.IdentifyFixedAndMoveableNodes(false, true)) {
			throw (new GraphLayoutException("public error 2"));
		}
		Integer numberOfNodes = this._topologicalData.GetNumberOfNodes();
		Integer numberOfLinks = this._topologicalData.GetNumberOfLinks();
		if ((this._vectNodesDx == null)
				|| (numberOfNodes != this._vectNodesDx.length)) {
			this._vectNodesDx = new float[numberOfNodes];
			this._vectNodesDy = new float[numberOfNodes];
		} else {
			for (Integer i = 0; i < numberOfNodes; i++) {
				this._vectNodesDx[i] = 0f;
				this._vectNodesDy[i] = 0f;
			}
		}
		if ((this._vectFromId == null)
				|| (numberOfLinks != this._vectFromId.length)) {
			this._vectFromId = new Integer[numberOfLinks];
			this._vectToId = new Integer[numberOfLinks];
		}
		if ((this._vectWishedLength == null)
				|| (numberOfLinks != this._vectWishedLength.length)) {
			this._vectWishedLength = new float[numberOfLinks];
		}
		if (this.get_RespectNodeSizes()
				&& ((this._vectHalfNodeDiag == null) || (numberOfNodes != this._vectHalfNodeDiag.length))) {
			this._vectHalfNodeDiag = new float[numberOfNodes];
		}

	}

	@Override
	public Boolean IsLayoutOfConnectedComponentsEnabledByDefault() {

		return true;

	}

	/**
	 * 1.扫描数据，获得连接的数据。 2.设置连接模型的坐标。
	 */
	@Override
	public void Layout() {
		// 1.模拟数据，相关连接模型 2.给模型设置坐标

		Boolean redraw = false;
		ArrayList<String> aList = new ArrayList<String>();
		String t = "0-1,0-2,0-3,1-4,1-5,1-6,2-7,2-8,2-9,3-10,3-11,3-12,3-13,3-14";
		String string[] = t.split(",");
		for (int i = 0; i < string.length; i++) {
			System.out.println(string[i]);
			aList.add(string[i]);
		}

		this._graphModel = this.GetGraphModel();
		this._layoutReport = (ForceDirectedLayoutReport) super
				.GetLayoutReport();
		this._layoutData = new GraphLayoutData(this);
		this._layoutData.BeforeLayout(true, false, false, false);

		this._topologicalData = this._layoutData.GetTopologicalData();// date

		this._positionData = this._layoutData.GetPositionData();
		if ((this._topologicalData == null) || (this._positionData == null)) {
			throw (new GraphLayoutException("Internal error"));
		}

		if (this._topologicalData.IsNoMoveableNode()) {
			this._layoutReport.set_Code(GraphLayoutReportCode.LayoutDone);
		} else if (this._topologicalData.GetNumberOfLinks() < 1) {
			this._layoutReport.set_Code(GraphLayoutReportCode.LayoutDone);
		} else {
			if ((this._topologicalData == null) || (this._positionData == null)) {
				throw (new GraphLayoutException(
						"could not initialize public data"));
			}
			this.InitializeDataStructures();
			try {
				this.DoLayout(redraw);
			} finally {
				this._layoutData = null;
				this._topologicalData = null;
				this._positionData = null;
			}
		}

	}

	private float LimitedValue(double val, float limitValue) {
		if (val > limitValue) {

			return limitValue;
		}
		if (-val > limitValue) {

			return -limitValue;
		}

		return (float) val;

	}

	private Boolean MayContinue(Integer currentNumberOfIterations,
			Integer allowedNumberOfIterations) {
		// return (((currentNumberOfIterations < allowedNumberOfIterations) &&
		// !this.IsLayoutTimeElapsed()) && !this.IsStoppedImmediately());

		return ((currentNumberOfIterations < allowedNumberOfIterations) && !this
				.IsStoppedImmediately());

	}

	private void PerformOneIteration(InternalRect rect, float minNodeDist,
			Boolean fitToLayoutRegion, float maxAllowedMove,
			float linkLengthWeight, float additionalNodeRepulsionWeight,
			float convergenceThreshold, Boolean respectNodeSizes, Random random) {
		this.ComputeLinkForces(linkLengthWeight, convergenceThreshold, random);
		this.ComputeNodeForces(minNodeDist, additionalNodeRepulsionWeight,
				convergenceThreshold, respectNodeSizes, random);
		this.ApplyNodeDeplacement(rect, fitToLayoutRegion, maxAllowedMove);

	}

	public void SetPreferredLength(java.lang.Object link, float length) {
		if (length < 0f) {
			length = -1f;
		}
		LayoutParametersUtil.SetLinkParameter(this, link, LINK_LENGTH_PROPERTY,
				length, -1f);

	}

	@Override
	public Boolean SupportsAllowedTime() {

		return true;

	}

	@Override
	public Boolean SupportsLayoutOfConnectedComponents() {

		return true;

	}

	@Override
	public Boolean SupportsLayoutRegion() {

		return true;

	}

	@Override
	public Boolean SupportsLinkConnectionBox() {

		return true;

	}

	@Override
	public Boolean SupportsPreserveFixedNodes() {

		return true;

	}

	@Override
	public Boolean SupportsStopImmediately() {

		return true;

	}

	public float get_AdditionalNodeRepulsionWeight() {

		return this._additionalNodeRepulsionWeight;
	}

	public void set_AdditionalNodeRepulsionWeight(float value) {
		if (value != this._additionalNodeRepulsionWeight) {
			this._additionalNodeRepulsionWeight = value;
			this.OnParameterChanged("AdditionalNodeRepulsionWeight");
		}
	}

	public Integer get_AllowedNumberOfIterations() {

		return this._numberOfIterations;
	}

	public void set_AllowedNumberOfIterations(Integer value) {
		if (value < 0) {
			throw (new ArgumentException(
					"number of iterations cannot be negative!"));
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

	public float get_ConvergenceThreshold() {

		return this._convergenceThreshold;
	}

	public void set_ConvergenceThreshold(float value) {

		value = Math.Max(value, 1E-20f);
		if (value != this._convergenceThreshold) {
			this._convergenceThreshold = value;
			this.OnParameterChanged("ConvergenceThreshold");
		}
	}

	public Boolean get_ForceFitToLayoutRegion() {

		return this._isForceFitToLayoutRegion;
	}

	public void set_ForceFitToLayoutRegion(Boolean value) {
		if (value != this._isForceFitToLayoutRegion) {
			this._isForceFitToLayoutRegion = value;
			this.OnParameterChanged("ForceFitToLayoutRegion");
		}
	}

	public Boolean get_LayoutOfConnectedComponentsEnabled() {

		return super.get_LayoutOfConnectedComponentsEnabled();
	}

	public void set_LayoutOfConnectedComponentsEnabled(Boolean value) {
		super.set_LayoutOfConnectedComponentsEnabled(value);
	}

	public Rectangle2D get_LayoutRegion() {

		return super.get_LayoutRegion();
	}

	// public void set_LayoutRegion(Rectangle2D value){
	// super.set_LayoutRegion(value);
	// }
	//
	// public GraphLayoutRegionMode get_LayoutRegionMode(){
	//
	// return super.get_LayoutRegionMode();
	// }
	// public void set_LayoutRegionMode(GraphLayoutRegionMode value){
	// super.set_LayoutRegionMode(value);
	// }

	public ILinkConnectionBoxProvider get_LinkConnectionBoxProvider() {

		return super.get_LinkConnectionBoxProvider();
	}

	public void set_LinkConnectionBoxProvider(ILinkConnectionBoxProvider value) {
		super.set_LinkConnectionBoxProvider(value);
	}

	public float get_LinkLengthWeight() {

		return this._linkLengthWeight;
	}

	public void set_LinkLengthWeight(float value) {
		if (value != this._linkLengthWeight) {
			this._linkLengthWeight = value;
			this.OnParameterChanged("LinkLengthWeight");
		}
	}

	public int get_LinkStyle() {

		return (int) this._linkStyle;
	}

	public void set_LinkStyle(int value) {
		if ((value != ForceDirectedLayoutLinkStyle.Straight)
				&& (value != ForceDirectedLayoutLinkStyle.NoReshape)) {
			throw (new ArgumentException("unsupported style option: "
					+ ((Integer) value)));
		}
		if ((Integer) value != this._linkStyle) {
			this._linkStyle = (Integer) value;
			this.OnParameterChanged("LinkStyle");
		}
	}

	public float get_MaxAllowedMovePerIteration() {

		return this._maxAllowedMovePerIteration;
	}

	public void set_MaxAllowedMovePerIteration(float value) {
		if (value < 1E-20f) {
			value = 1E-20f;
		}
		if (value != this._maxAllowedMovePerIteration) {
			this._maxAllowedMovePerIteration = value;
			this.OnParameterChanged("MaxAllowedMovePerIteration");
		}
	}

	public float get_NodeDistanceThreshold() {

		return this._nodeDistanceThreshold;
	}

	public void set_NodeDistanceThreshold(float value) {
		if (value != this._nodeDistanceThreshold) {
			this._nodeDistanceThreshold = value;
			this.OnParameterChanged("NodeDistanceThreshold");
		}
	}

	public float get_PreferredLinksLength() {

		return this._preferredLinksLength;
	}

	public void set_PreferredLinksLength(float value) {
		if (value < 0f) {
			value = 0f;
		}
		if (value != this._preferredLinksLength) {
			this._preferredLinksLength = value;
			this.OnParameterChanged("PreferredLinksLength");
		}
	}

	public Boolean get_PreserveFixedNodes() {

		return super.get_PreserveFixedNodes();
	}

	public void set_PreserveFixedNodes(Boolean value) {
		super.set_PreserveFixedNodes(value);
	}

	public Boolean get_RespectNodeSizes() {

		return this._respectNodeSizes;
	}

	public void set_RespectNodeSizes(Boolean value) {
		if (value != this._respectNodeSizes) {
			this._respectNodeSizes = value;
			this.OnParameterChanged("RespectNodeSizes");
		}
	}

	public static void main(String[] a) {
		new ForceDirectedLayout().Layout();
	}

}
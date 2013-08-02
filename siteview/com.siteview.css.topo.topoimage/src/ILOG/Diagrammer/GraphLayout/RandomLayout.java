package ILOG.Diagrammer.GraphLayout;

import system.ArgumentException;
import system.Math;
import system.Random;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public class RandomLayout extends GraphLayout {
	public int _defaultLinkStyle = RandomLayoutLinkStyle.Straight;

	private int _linksStyle;

	public RandomLayout() {
	}

	public RandomLayout(RandomLayout source) {
		super(source);
	}

	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy() {

		return new RandomLayout(this);

	}

	@Override
	public void CopyParameters(ILOG.Diagrammer.GraphLayout.GraphLayout source) {
		super.CopyParameters(source);
		if (source instanceof RandomLayout) {
			RandomLayout layout = (RandomLayout) source;
			this.set_LinkStyle(layout.get_LinkStyle());
		}

	}

	@Override
	public void Init() {
		super.Init();
		this._linksStyle = RandomLayoutLinkStyle.Straight;

	}

	@Override
	public void Layout() {
		IGraphModel graphModel = this.GetGraphModel();
		GraphLayoutReport layoutReport = super.GetLayoutReport();
		Integer count = graphModel.get_Nodes().get_Count();
		Integer num2 = 1;
		int layoutDone = GraphLayoutReportCode.LayoutDone;
		InternalRect rect = TranslateUtil.Rectangle2D2InternalRect(this
				.GetCalcLayoutRegion());
		float x = rect.X;
		float y = rect.Y;
		float num5 = rect.X + rect.Width;
		float num6 = rect.Y + rect.Height;
		Random random = this.get_UseSeedValueForRandomGenerator() ? new Random(
				this.get_SeedValueForRandomGenerator()) : new Random();
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Nodes());
		Boolean preserveFixedNodes = this.get_PreserveFixedNodes();

		while (enumerator.HasMoreElements()) {
			java.lang.Object nodeOrLink = enumerator.NextElement();
			if (nodeOrLink == null) {
				throw (new system.Exception(
						"null node retrieved from graph model"));
			}

			if (!preserveFixedNodes || !this.GetFixed(nodeOrLink)) {
				InternalRect rect2 = GraphModelUtil.BoundingBox(graphModel,
						nodeOrLink);
				float num7 = x
						+ (Math.Max((float) 0f,
								(float) ((num5 - rect2.Width) - x)) * ((float) random
								.NextDouble()));
				float num8 = y
						+ (Math.Max((float) 0f,
								(float) ((num6 - rect2.Height) - y)) * ((float) random
								.NextDouble()));
				graphModel.MoveNode(nodeOrLink, num7, num8);
				this.IncreasePercentageComplete((num2++ * 100) / count);
				super.OnLayoutStepPerformedIfNeeded();
				if (this.IsLayoutTimeElapsed() || this.IsStoppedImmediately()) {
					layoutDone = GraphLayoutReportCode.StoppedAndInvalid;
					break;
				}
			}
		}
		if (this.get_LinkStyle() == RandomLayoutLinkStyle.Straight) {
			GraphModelUtil.DeleteIntermediatePointsOnLinks(graphModel, this);
		}
		this.IncreasePercentageComplete(100);
		this.OnLayoutStepPerformed(false, false);
		layoutReport.set_Code(layoutDone);

	}

	@Override
	public Boolean SupportsAllowedTime() {

		return true;

	}

	@Override
	public Boolean SupportsLayoutRegion() {

		return true;

	}

	@Override
	public Boolean SupportsPercentageComplete() {

		return true;

	}

	@Override
	public Boolean SupportsPreserveFixedNodes() {

		return true;

	}

	@Override
	public Boolean SupportsRandomGenerator() {

		return true;

	}

	@Override
	public Boolean SupportsStopImmediately() {

		return true;

	}

	public long get_AllowedTime() {

		return super.get_AllowedTime();
	}

	public void set_AllowedTime(long value) {
		super.set_AllowedTime(value);
	}

	public Rectangle2D get_LayoutRegion() {

		return super.get_LayoutRegion();
	}

	public void set_LayoutRegion(Rectangle2D value) {
		super.set_LayoutRegion(value);
	}

	public GraphLayoutRegionMode get_LayoutRegionMode() {

		return super.get_LayoutRegionMode();
	}

	public void set_LayoutRegionMode(GraphLayoutRegionMode value) {
		super.set_LayoutRegionMode(value);
	}

	public int get_LinkStyle() {

		return this._linksStyle;
	}

	public void set_LinkStyle(int value) {
		if ((value != RandomLayoutLinkStyle.Straight)
				&& (value != RandomLayoutLinkStyle.NoReshape)) {
			throw (new ArgumentException("unsupported style option: "
					+ ((int) value)));
		}
		if (value != this._linksStyle) {
			this._linksStyle = value;
			this.OnParameterChanged("LinkStyle");
		}
	}

	public Boolean get_PreserveFixedNodes() {

		return super.get_PreserveFixedNodes();
	}

	public void set_PreserveFixedNodes(Boolean value) {
		super.set_PreserveFixedNodes(value);
	}

	public Boolean get_UseSeedValueForRandomGenerator() {

		return super.get_UseSeedValueForRandomGenerator();
	}

	public void set_UseSeedValueForRandomGenerator(Boolean value) {
		super.set_UseSeedValueForRandomGenerator(value);
	}

}
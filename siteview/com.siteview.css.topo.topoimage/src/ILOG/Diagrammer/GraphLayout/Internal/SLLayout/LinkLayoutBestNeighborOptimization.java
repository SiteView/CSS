package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;
import system.Collections.*;

public final class LinkLayoutBestNeighborOptimization extends
		BestNeighborOptimization {
	private IGraphModel _graphModel;

	private Boolean _isLinkBundlesRequired = false;

	private ShortLinkAlgorithm _layout;

	public LinkLayoutBestNeighborOptimization(ShortLinkAlgorithm layout,
			Integer numberOfIterations, long allowedTime, ArrayList variables) {
		super(variables, numberOfIterations, allowedTime);
		if (layout == null) {
			throw (new ArgumentException("layout cannot be null"));
		}

		this._graphModel = layout.GetGraphModel();
		if (this._graphModel == null) {
			throw (new ArgumentException("graph model cannot be null"));
		}
		this._layout = layout;
		this._isLinkBundlesRequired = layout.GetShortLinkLayout()
				.get_LinkBundlesMode() != ShortLinkLayoutLinkBundlesMode.NoBundle;
	}

	@Override
	public Boolean ApplyVariant(java.lang.Object variable,
			Integer variableIndex, Integer variant) {
		if (variant == super.GetVariant(variableIndex)) {

			return false;
		}
		LinkData linkData = (LinkData) variable;
		super.SetVariant(variableIndex, variant);
		LinkShapeType shapeType = linkData.GetShapeType(variant);
		if (shapeType == linkData.GetCurrentShapeType()) {

			return false;
		}
		this._layout.ReshapeLink(this._graphModel, linkData, shapeType,
				this._isLinkBundlesRequired);

		return true;

	}

	@Override
	public float GetCost(java.lang.Object variable) {

		return this._layout.GetLinkCost(this._graphModel, (LinkData) variable,
				false);

	}

	@Override
	public Integer GetVariantsCount(java.lang.Object variable) {

		return ((LinkData) variable).GetShapeTypesCount();

	}

	@Override
	public Boolean IsBetterVariant(java.lang.Object variable, Integer variant1,
			float cost1, Integer variant2, float cost2) {
		if (cost1 < cost2) {

			return true;
		}
		if (cost1 > cost2) {

			return false;
		}
		LinkData data = (LinkData) variable;
		LinkShapeType shapeType = data.GetShapeType(variant1);
		LinkShapeType type2 = data.GetShapeType(variant2);

		return (shapeType.GetNumberOfBends() < type2.GetNumberOfBends());

	}

	@Override
	public void VariantStepPerformed() {
		this._layout.GetShortLinkLayout().OnLayoutStepPerformedIfNeeded();

	}

}
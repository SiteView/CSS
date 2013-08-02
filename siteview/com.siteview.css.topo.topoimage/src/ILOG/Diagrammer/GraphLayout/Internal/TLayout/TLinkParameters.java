package ILOG.Diagrammer.GraphLayout.Internal.TLayout;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;

public final class TLinkParameters {
	private Boolean _isCalcBackwardTreeLink = false;

	private Boolean _isCalcForwardTreeLink = false;

	private Boolean _isCalcNonTreeLink = false;

	private Integer _linkStyle = 1;

	public TLinkParameters(TreeLayout layout, java.lang.Object link) {
		this._linkStyle = 1;
		this._isCalcForwardTreeLink = false;
		this._isCalcBackwardTreeLink = false;
		this._isCalcNonTreeLink = false;
		layout.GetGraphModel().SetProperty(link, layout.LINK_PROPERTY, this);
	}

	public void Dispose(TreeLayout layout, java.lang.Object link) {
		layout.GetGraphModel().SetProperty(link, layout.LINK_PROPERTY, null);

	}

	public static TLinkParameters Get(TreeLayout layout, java.lang.Object link) {
		IGraphModel graphModel = layout.GetGraphModel();
		if (graphModel == null) {

			return null;
		}

		return (TLinkParameters) graphModel.GetProperty(link,
				layout.LINK_PROPERTY);

	}

	public static Integer GetLinkStyle(TreeLayout layout, java.lang.Object link) {
		TLinkParameters parameters = Get(layout, link);
		if (parameters != null) {

			return parameters._linkStyle;
		}

		return 1;

	}

	public static TLinkParameters GetOrAllocate(TreeLayout layout,
			java.lang.Object link) {
		TLinkParameters parameters = Get(layout, link);
		if (parameters == null) {
			parameters = new TLinkParameters(layout, link);
		}

		return parameters;

	}

	public Boolean IsCalcBackwardTreeLink() {

		return this._isCalcBackwardTreeLink;

	}

	public Boolean IsCalcForwardTreeLink() {

		return this._isCalcForwardTreeLink;

	}

	public Boolean IsCalcNonTreeLink() {

		return this._isCalcNonTreeLink;

	}

	public Boolean IsNeeded() {
		if (((this._linkStyle == 1) && !this._isCalcForwardTreeLink)
				&& !this._isCalcBackwardTreeLink) {

			return this._isCalcNonTreeLink;
		}

		return true;

	}

	public void SetCalcBackwardTreeLink(Boolean flag) {
		this._isCalcBackwardTreeLink = flag;

	}

	public void SetCalcForwardTreeLink(Boolean flag) {
		this._isCalcForwardTreeLink = flag;

	}

	public void SetCalcNonTreeLink(Boolean flag) {
		this._isCalcNonTreeLink = flag;

	}

	public static void SetLinkStyle(TreeLayout layout, java.lang.Object link,
			Integer style) {
		TLinkParameters orAllocate = GetOrAllocate(layout, link);
		Integer num = orAllocate._linkStyle;
		orAllocate._linkStyle = style;

		if (!orAllocate.IsNeeded()) {
			orAllocate.Dispose(layout, link);
		}
		if (num != style) {
			layout.OnParameterChanged(link, "LinkStyle");
		}

	}

}
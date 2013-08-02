package ILOG.Diagrammer.GraphLayout.Internal.TLayout;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;

public final class TNodeParameters {
	private Integer _alignment = 0;

	private java.lang.Object _eastNeighbor = null;

	private Boolean _isCalcRoot = false;

	private Boolean _isSpecRoot = false;

	private TNode _node = null;

	private Integer _rootPreference = -1;

	private java.lang.Object _westNeighbor = null;

	public TNodeParameters(TreeLayout layout, java.lang.Object node) {
		layout.GetGraphModel().SetProperty(node, layout.NODE_PROPERTY, this);
	}

	public static void AfterLayout(TreeLayout layout, java.lang.Object nodeObj) {
		TNodeParameters parameters = Get(layout, nodeObj);
		if (parameters != null) {
			parameters._node = null;

			if (!parameters.IsNeeded()) {
				parameters.Dispose(layout, nodeObj);
			}
		}

	}

	public static void BeforeLayout(TreeLayout layout,
			java.lang.Object nodeObj, TNode node) {
		GetOrAllocate(layout, nodeObj)._node = node;

	}

	public void Dispose(TreeLayout layout, java.lang.Object node) {
		this._eastNeighbor = null;
		this._westNeighbor = null;
		this._node = null;
		layout.GetGraphModel().SetProperty(node, layout.NODE_PROPERTY, null);

	}

	public static TNodeParameters Get(TreeLayout layout, java.lang.Object node) {
		IGraphModel graphModel = layout.GetGraphModel();
		if (graphModel == null) {

			return null;
		}

		return (TNodeParameters) graphModel.GetProperty(node,
				layout.NODE_PROPERTY);

	}

	public static Integer GetAlignment(TreeLayout layout, java.lang.Object node) {
		TNodeParameters parameters = Get(layout, node);
		if (parameters != null) {

			return parameters._alignment;
		}

		return 0;

	}

	public java.lang.Object GetEastNeighbor() {

		return this._eastNeighbor;

	}

	public TNode GetNode() {

		return this._node;

	}

	public static TNodeParameters GetOrAllocate(TreeLayout layout,
			java.lang.Object node) {
		TNodeParameters parameters = Get(layout, node);
		if (parameters == null) {
			parameters = new TNodeParameters(layout, node);
		}

		return parameters;

	}

	public static Integer GetRootPreference(TreeLayout layout,
			java.lang.Object node) {
		TNodeParameters parameters = Get(layout, node);
		if (parameters != null) {

			return parameters._rootPreference;
		}

		return -1;

	}

	public java.lang.Object GetWestNeighbor() {

		return this._westNeighbor;

	}

	public Boolean IsCalcRoot() {

		return this._isCalcRoot;

	}

	public Boolean IsNeeded() {
		if ((((this._rootPreference < 0) && (this._alignment == 0)) && ((this._eastNeighbor == null) && (this._westNeighbor == null)))
				&& (!this._isSpecRoot && !this._isCalcRoot)) {

			return (this._node != null);
		}

		return true;

	}

	public Boolean IsSpecRoot() {

		return this._isSpecRoot;

	}

	public static void SetAlignment(TreeLayout layout, java.lang.Object node,
			Integer alignment) {
		TNodeParameters orAllocate = GetOrAllocate(layout, node);
		Integer num = orAllocate._alignment;
		orAllocate._alignment = alignment;

		if (!orAllocate.IsNeeded()) {
			orAllocate.Dispose(layout, node);
		}
		if (num != alignment) {
			layout.OnParameterChanged(node, "Alignment");
		}

	}

	public void SetCalcRoot(Boolean flag) {
		this._isCalcRoot = flag;

	}

	public void SetEastNeighbor(java.lang.Object node) {
		this._eastNeighbor = node;

	}

	public static void SetRootPreference(TreeLayout layout,
			java.lang.Object node, Integer pref) {
		TNodeParameters orAllocate = GetOrAllocate(layout, node);
		Integer num = orAllocate._rootPreference;
		orAllocate._rootPreference = pref;

		if (!orAllocate.IsNeeded()) {
			orAllocate.Dispose(layout, node);
		}
		if (num != pref) {
			layout.OnParameterChanged(node, "RootPreference");
		}

	}

	public void SetSpecRoot(Boolean flag) {
		this._isSpecRoot = flag;

	}

	public void SetWestNeighbor(java.lang.Object node) {
		this._westNeighbor = node;

	}

}
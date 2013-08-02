package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;
import system.Collections.*;

public class DefaultLayoutProvider implements ILayoutProvider {
	private Hashtable _layoutHashMap = new Hashtable();

	private Integer _uniqueId;

	private static Integer _uniqueIdCounter = -1;

	public DefaultLayoutProvider() {
		this.InitPropertiesNames();
	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout Copy(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if (layout == null) {

			return null;
		}

		return layout.Copy();

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout CreateGraphLayout(
			IGraphModel graphModel) {

		return this.Copy(this.GetParentLayout(graphModel));

	}

	public void DetachLayouts(IGraphModel graphModel, Boolean traverse) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		if (traverse) {
			IEnumerator enumerator = this._layoutHashMap.GetEnumerator();

			while (enumerator.MoveNext()) {
				DictionaryEntry current = (DictionaryEntry) enumerator
						.get_Current();
				IGraphModel key = (IGraphModel) current.get_Key();
				ILOG.Diagrammer.GraphLayout.GraphLayout layout = (ILOG.Diagrammer.GraphLayout.GraphLayout) current
						.get_Value();
				if ((layout != null) && (layout.GetGraphModel() == key)) {
					layout.RemoveLayoutProviderThatContainsMe(this);
					layout.Detach();
				}
			}
			this._layoutHashMap.Clear();
		} else {
			ILOG.Diagrammer.GraphLayout.GraphLayout storedPreferredLayout = this
					.GetStoredPreferredLayout(graphModel);
			this.StorePreferredLayout(graphModel, null);
			if ((storedPreferredLayout != null)
					&& (storedPreferredLayout.GetGraphModel() == graphModel)) {
				storedPreferredLayout.RemoveLayoutProviderThatContainsMe(this);
				storedPreferredLayout.Detach();
			}
		}

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout GetGraphLayout(
			IGraphModel graphModel) {
		ILOG.Diagrammer.GraphLayout.GraphLayout preferredLayout = this
				.GetPreferredLayout(graphModel);
		if (preferredLayout != null) {

			return preferredLayout;
		}
		ILOG.Diagrammer.GraphLayout.GraphLayout layout = this
				.CreateGraphLayout(graphModel);
		if (layout == null) {

			return null;
		}
		layout.Attach(graphModel);
		this.SetPreferredLayout(graphModel, layout);

		return layout;

	}

	public Integer GetInstanceId() {

		return this._uniqueId;

	}

	private ILOG.Diagrammer.GraphLayout.GraphLayout GetParentLayout(
			IGraphModel graphModel) {
		ILOG.Diagrammer.GraphLayout.GraphLayout graphLayout = null;
		for (IGraphModel model = graphModel.get_Parent(); model != null; model = model
				.get_Parent()) {

			graphLayout = this.GetGraphLayout(model);
			if (graphLayout != null) {

				return graphLayout;
			}
		}

		return graphLayout;

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout GetPreferredLayout(
			IGraphModel graphModel) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		ILOG.Diagrammer.GraphLayout.GraphLayout storedPreferredLayout = this
				.GetStoredPreferredLayout(graphModel);
		if (storedPreferredLayout != null) {
			IGraphModel model = storedPreferredLayout.GetGraphModel();
			if (model == graphModel) {

				return storedPreferredLayout;
			}

			if ((model == null) || !model.IsDisposed()) {
				storedPreferredLayout.Attach(graphModel);

				return storedPreferredLayout;
			}
			if (GraphModelData.Get(graphModel).get_InternalGraphModelChecking()) {
				throw (new system.Exception(
						"The preferred layout is attached to a disposed graph model.\nYou should detach the layout before disposing any graph model."));
			}
		}

		return storedPreferredLayout;

	}

	private ILOG.Diagrammer.GraphLayout.GraphLayout GetStoredPreferredLayout(
			IGraphModel graphModel) {

		return (ILOG.Diagrammer.GraphLayout.GraphLayout) this._layoutHashMap
				.get_Item(graphModel);

	}

	private void InitPropertiesNames() {
		_uniqueIdCounter++;
		this._uniqueId = _uniqueIdCounter;

	}

	public void SetPreferredLayout(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		this.SetPreferredLayout(graphModel, layout, true);

	}

	public void SetPreferredLayout(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			Boolean detachPrevious) {
		this.SetPreferredLayout(graphModel, layout, detachPrevious, false);

	}

	public void SetPreferredLayout(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			Boolean detachPrevious, Boolean traverse) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		this.SetPreferredLayoutRecursive(graphModel, graphModel, layout,
				detachPrevious, traverse);

	}

	private void SetPreferredLayoutNonRecursive(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			Boolean detachPrevious) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		if (GraphModelData.Get(graphModel).get_InternalGraphModelChecking()) {
			if (graphModel.IsDisposed() && (layout != null)) {
				throw (new ArgumentException(
						"You cannot set the preferred layout for a graph model that is already disposed"));
			}
			if ((layout != null)
					&& (graphModel instanceof GraphicContainerAdapter)) {
				GraphicContainerAdapter adapter = (GraphicContainerAdapter) graphModel;
				if ((adapter.GetOriginatingLayout() != null)
						&& (adapter.GetOriginatingLayout() != layout)) {
					throw (new ArgumentException(
							clr.System.StringStaticWrapper
									.Concat(new java.lang.Object[] {
											"Shouldn't be called for a model internally created by other layout; ",
											graphModel,
											" has been created by ",
											adapter.GetOriginatingLayout(),
											" not by the specified preferred layout: ",
											layout })));
				}
			}
		}
		ILOG.Diagrammer.GraphLayout.GraphLayout storedPreferredLayout = this
				.GetStoredPreferredLayout(graphModel);
		if (storedPreferredLayout != layout) {
			if ((detachPrevious && (storedPreferredLayout != null))
					&& (storedPreferredLayout.GetGraphModel() != null)) {
				storedPreferredLayout.RemoveLayoutProviderThatContainsMe(this);
				storedPreferredLayout.Detach();
			}
			if ((layout != null) && (layout.GetGraphModel() != graphModel)) {
				layout.Attach(graphModel);
			}
			this.StorePreferredLayout(graphModel, layout);
		}

	}

	private void SetPreferredLayoutRecursive(IGraphModel startModel,
			IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.GraphLayout startLayout,
			Boolean detachPrevious, Boolean traverse) {
		if (traverse) {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(graphModel.get_Subgraphs());

			while (enumerator.HasMoreElements()) {
				java.lang.Object subgraph = enumerator.NextElement();
				IGraphModel model = graphModel.GetGraphModel(subgraph);
				if (model != null) {
					this.SetPreferredLayoutRecursive(startModel, model,
							startLayout, detachPrevious, true);
				}
			}
		}
		ILOG.Diagrammer.GraphLayout.GraphLayout layout = (startModel == graphModel) ? startLayout
				: ((startLayout != null) ? this.Copy(startLayout) : null);
		this.SetPreferredLayoutNonRecursive(graphModel, layout, detachPrevious);

	}

	public void StorePreferredLayout(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if (layout != null) {
			layout.AddLayoutProviderThatContainsMe(this);
			this._layoutHashMap.set_Item(graphModel, layout);
		} else {
			this._layoutHashMap.Remove(graphModel);
		}

	}

}
package ILOG.Diagrammer.GraphLayout;

import system.ArgumentException;
import system.Collections.ArrayList;
import system.Collections.Hashtable;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import ILOG.Diagrammer.Point2D;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.GraphLayout.Internal.DefaultPropertyContainer;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public abstract class AbstractGraphModel implements IGraphModel {
	private ArrayList _attachedLayouts;

	private int _eventTypesDuringAdjSession;

	private Boolean _isDisposed = false;

	private Hashtable _nodeOrLinkProperties;

	private IPropertyContainer _properties;

	private IGraphModel _rootModel;

	public static String _rootModelPropertyName = "__RootModel";

	public String _subgraphModelPropertyName;

	private static Integer _uniqueIdCounter = -1;

	/* TODO: Event Declare */
	public java.util.ArrayList<GraphModelContentsChangedEventHandler> ContentsChanged = new java.util.ArrayList<GraphModelContentsChangedEventHandler>();

	public AbstractGraphModel() {
		Integer id = _uniqueIdCounter++;
		this.InitPropertyNames(id);
	}

	public void AfterLayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {

	}

	public void BeforeLayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {

	}

	public void BeginUpdate() {
		int beginUpdate = GraphModelContentsChangedEventAction.BeginUpdate;
		GraphModelContentsChangedEventArgs e = new GraphModelContentsChangedEventArgs(
				this, beginUpdate, null);
		this.FireEvent(e);

	}

	public abstract Rectangle2D BoundingBox(java.lang.Object nodeOrLink);

	public void CleanSubmodelProperty() {
		AbstractGraphModel root = (AbstractGraphModel) this.get_Root();
		if (root != null) {
			IGraphModel parent = this.get_Parent();
			if (parent != null) {
				IEnumerator it1 = parent.get_Subgraphs().GetEnumerator();
				while (it1.MoveNext()) {
					java.lang.Object obj3 = (java.lang.Object) it1
							.get_Current();
					if (root.GetStoredGraphModel(obj3) == this) {
						root.RemoveStoredGraphModel(obj3);
					}
				}
			}
		}

	}

	public abstract AbstractGraphModel CreateGraphModel(
			java.lang.Object subgraph);

	private void DetachLayouts() {
		if (this._attachedLayouts != null) {
			Integer count = this._attachedLayouts.get_Count();
			java.lang.Object[] array = new java.lang.Object[count];
			this._attachedLayouts.CopyTo(0, array, 0, count);
			for (Integer i = 0; i < count; i++) {
				ILOG.Diagrammer.GraphLayout.GraphLayout layout = (ILOG.Diagrammer.GraphLayout.GraphLayout) array[i];
				if (layout.GetGraphModel() == this) {
					layout.Detach();
				}
			}
		}

	}

	public void Dispose() {
		if (!this._isDisposed) {
			this.DisposeChildren();
			this.DisposeThis();
		}

	}

	public void DisposeChildren() {
		if (!this._isDisposed) {
			AbstractGraphModel model = (this.get_Root() != null) ? ((AbstractGraphModel) this
					.get_Root()) : this;
			IEnumerator it1 = this.get_Subgraphs().GetEnumerator();
			while (it1.MoveNext()) {
				java.lang.Object obj2 = (java.lang.Object) it1.get_Current();
				IGraphModel storedGraphModel = model.GetStoredGraphModel(obj2);
				if (storedGraphModel != null) {
					storedGraphModel.Dispose();
				}
				model.RemoveStoredGraphModel(obj2);
			}
		}

	}

	public void DisposeThis() {
		if (!this._isDisposed) {
			this._isDisposed = true;
			this.DetachLayouts();
			this.CleanSubmodelProperty();
			this._nodeOrLinkProperties = null;
			this._properties = null;
			this._rootModel = null;
		}

	}

	public void EndUpdate() {
		int action = GraphModelContentsChangedEventAction.EndUpdate
				| this._eventTypesDuringAdjSession;
		action &= ~GraphModelContentsChangedEventAction.Updating;
		GraphModelContentsChangedEventArgs e = new GraphModelContentsChangedEventArgs(
				this, action, null);
		this._eventTypesDuringAdjSession = 0;
		this.FireEvent(e);

	}

	public void FireEvent(GraphModelContentsChangedEventArgs e) {
		if (this.ContentsChanged != null) {
			this.ContentsChanged(this, e);
		}

	}

	private void ContentsChanged(AbstractGraphModel abstractGraphModel,
			GraphModelContentsChangedEventArgs e) {
		// TODO Auto-generated method stub

	}

	public void FireEvent(java.lang.Object nodeOrLink, int eventType,
			Boolean updating) {
		if (updating) {
			this._eventTypesDuringAdjSession |= eventType;
		}
		if (updating) {
			eventType |= GraphModelContentsChangedEventAction.Updating;
		}
		eventType &= ~GraphModelContentsChangedEventAction.EndUpdate;
		GraphModelContentsChangedEventArgs e = new GraphModelContentsChangedEventArgs(
				this, eventType, nodeOrLink);
		this.FireEvent(e);

	}

	public abstract java.lang.Object GetFrom(java.lang.Object link);

	public IGraphModel GetGraphModel(java.lang.Object subgraph) {
		if (subgraph == null) {
			throw (new ArgumentException("subgraph cannot be null"));
		}

		if (this.IsDisposed()) {
			throw (new system.Exception("Called on disposed model: " + this));
		}
		IGraphModel root = this.get_Root();
		if (root != null) {

			if (root.IsDisposed()) {
				throw (new system.Exception("root model of " + this
						+ " is disposed"));
			}

			return root.GetGraphModel(subgraph);
		}
		AbstractGraphModel storedGraphModel = this
				.GetStoredGraphModel(subgraph);
		if (storedGraphModel == null) {
			this.SetProperty(subgraph, _rootModelPropertyName, this);

			storedGraphModel = this.CreateGraphModel(subgraph);
			this.SetProperty(subgraph, _rootModelPropertyName, null);
			if (storedGraphModel == null) {
				throw (new system.Exception(
						clr.System.StringStaticWrapper
								.Concat(new java.lang.Object[] {
										"createGraphModel returned null on ",
										this, " for subgraph: ", subgraph })));
			}

			if (storedGraphModel.IsDisposed()) {
				throw (new system.Exception(
						clr.System.StringStaticWrapper
								.Concat(new java.lang.Object[] {
										"createGraphModel returned a disposed model: ",
										storedGraphModel, " on ", this,
										" for subgraph: ", subgraph })));
			}

			if ((storedGraphModel.get_Parent() != null)
					&& !storedGraphModel.get_Parent().IsNode(subgraph)) {
				throw (new ArgumentException(
						"The subgraph must be a node in the parent graph model"));
			}
			this.StoreGraphModel(subgraph, storedGraphModel);

			return storedGraphModel;
		}

		if (storedGraphModel.IsDisposed()) {
			throw (new system.Exception(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] {
									"The graph model ", storedGraphModel,
									" stored for ", subgraph, " on ", this,
									" is disposed" })));
		}

		return storedGraphModel;

	}

	public abstract Point2D[] GetLinkPoints(java.lang.Object link);

	public abstract ICollection GetLinksFrom(java.lang.Object node);

	public abstract ICollection GetLinksTo(java.lang.Object node);

	public float GetLinkWidth(java.lang.Object link) {

		return 1f;

	}

	public java.lang.Object GetProperty(String key) {
		if (this._properties == null) {

			return null;
		}

		return this._properties.GetProperty(key);

	}

	public java.lang.Object GetProperty(java.lang.Object nodeOrLink, String key) {
		if (this._nodeOrLinkProperties != null) {
			java.lang.Object obj2 = this._nodeOrLinkProperties
					.get_Item(nodeOrLink);
			if (obj2 != null) {
				return ((IPropertyContainer) obj2).GetProperty(key);
			}
		}
		return null;
	}

	public String GetPropertyName(String name, Integer id) {

		return name + id;

	}

	public AbstractGraphModel GetStoredGraphModel(java.lang.Object subgraph) {

		return (AbstractGraphModel) this.GetProperty(subgraph,
				this._subgraphModelPropertyName);

	}

	public abstract java.lang.Object GetTo(java.lang.Object link);

	public Boolean HasMoveableConnectionPoint(java.lang.Object link,
			Boolean origin) {

		return true;

	}

	public Boolean HasPinnedConnectionPoint(java.lang.Object link,
			Boolean origin) {

		return false;

	}

	public void InitPropertyNames(Integer id) {

		this._subgraphModelPropertyName = this.GetPropertyName(
				"__SubgraphModel", id);

	}

	public Boolean IsDisposed() {

		return this._isDisposed;

	}

	public Boolean IsInterGraphLink(java.lang.Object obj) {

		return false;

	}

	public Boolean IsLayoutNeeded(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if (layout.get_StructureUpToDate() && layout.get_GeometryUpToDate()) {

			return !layout.get_ParametersUpToDate();
		}

		return true;

	}

	public abstract Boolean IsLink(java.lang.Object obj);

	public abstract Boolean IsNode(java.lang.Object obj);

	public Boolean IsSubgraph(java.lang.Object obj) {

		return false;

	}

	public abstract void MoveNode(java.lang.Object node, float x, float y);

	public void OnAttach(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if (this._attachedLayouts == null) {
			this._attachedLayouts = new ArrayList();
		}

		if (!this._attachedLayouts.Contains(layout)) {
			this._attachedLayouts.Add(layout);
		}

	}

	public void OnDetach(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if (this._attachedLayouts != null) {
			this._attachedLayouts.Remove(layout);
			if (this._attachedLayouts.get_Count() == 0) {
				this._attachedLayouts = null;
			}
		}

	}

	public void RemoveStoredGraphModel(java.lang.Object subgraph) {
		this.SetProperty(subgraph, this._subgraphModelPropertyName, null);

	}

	public abstract void ReshapeLink(java.lang.Object link,
			ReshapeLinkStyle style, Point2D fromPoint,
			ReshapeLinkMode fromPointMode, Point2D[] points,
			Integer startIndex, Integer length, Point2D toPoint,
			ReshapeLinkMode toPointMode);

	public void SetProperty(String key, java.lang.Object val) {
		if (this._properties == null) {
			this._properties = new DefaultPropertyContainer();
		}
		this._properties.SetProperty(key, val);

	}

	public void SetProperty(java.lang.Object nodeOrLink, String key,
			java.lang.Object val) {
		if (this._nodeOrLinkProperties == null) {
			this._nodeOrLinkProperties = new Hashtable(300);
		}
		IPropertyContainer container = (IPropertyContainer) this._nodeOrLinkProperties
				.get_Item(nodeOrLink);
		if (val != null) {
			if (container == null) {
				container = new DefaultPropertyContainer();
				this._nodeOrLinkProperties.set_Item(nodeOrLink, container);
			}
			container.SetProperty(key, val);
		} else if (container != null) {
			container.SetProperty(key, null);

			if (container.IsEmpty()) {
				this._nodeOrLinkProperties.Remove(nodeOrLink);
			}
		}

	}

	private void SetRootModel(IGraphModel rootModel) {
		this._rootModel = rootModel;

	}

	private void StoreGraphModel(java.lang.Object subgraph,
			AbstractGraphModel graphModel) {
		if (graphModel == this) {
			throw (new ArgumentException(
					"The root model cannot be reused for subgraphs"));
		}
		this.SetProperty(subgraph, this._subgraphModelPropertyName, graphModel);
		if (graphModel != null) {
			graphModel.SetRootModel(this);
		}

	}

	public ICollection get_InterGraphLinks() {

		return TranslateUtil.GetEmptyCollection();
	}

	public abstract ICollection get_Links();

	public abstract ICollection get_Nodes();

	public abstract IGraphModel get_Parent();

	public IGraphModel get_Root() {

		return this._rootModel;
	}

	public ICollection get_Subgraphs() {

		return TranslateUtil.GetEmptyCollection();
	}

}
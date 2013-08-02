package ILOG.Diagrammer.GraphLayout;

import org.eclipse.draw2d.geometry.Rectangle;

import system.Collections.ICollection;
import ILOG.Diagrammer.GraphicContainer;
import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.IDiagramView;
import ILOG.Diagrammer.Link;
import ILOG.Diagrammer.Point2D;
import ILOG.Diagrammer.Rectangle2D;

public class GraphicContainerAdapter extends AbstractGraphModel {
	private GraphicContainer container;

	public GraphicContainerAdapter(GraphicContainer grapher) {
		container = grapher;
	}

	@Override
	public void ReshapeLink(Object link, int style, Point2D fromPoint,
			int fromPointMode, Point2D[] points, Integer startIndex,
			Integer length, Point2D toPoint, int toPointMode) {
		// TODO Auto-generated method stub
	}

	@Override
	public Rectangle2D BoundingBox(Object nodeOrLink) {
		if (nodeOrLink instanceof GraphicObject) {
			GraphicObject go = (GraphicObject) nodeOrLink;
			if (go.isNode()) {
				Rectangle rect = go.getMode().getBounds();
				// System.out.println(rect.x+"--"+rect.y+"--"+rect.width+"--"+rect.height);
				return new Rectangle2D(rect.x, rect.y, rect.width, rect.height);
			}
			return new Rectangle2D();
		}
		return null;
	}

	@Override
	public AbstractGraphModel CreateGraphModel(Object subgraph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object GetFrom(Object link) {
		return ((Link) link).getSource();
	}

	@Override
	public Point2D[] GetLinkPoints(Object link) {
		// GraphicObject gTarget = ((Link)link).getTarget();
		// GraphicObject gSource = ((Link)link).getSource();
		return null;
	}

	@Override
	public ICollection GetLinksFrom(Object node) {

		System.out.println(node);
		// TopoNode tNode = (TopoNode) node;
		// String str = tNode.getTopoDataPair().getFirst();
		// System.out.println("-----"+str);
		system.Collections.ArrayList alArrayList = new system.Collections.ArrayList();
		for (GraphicObject gObject : container.getNodes()) {

			// System.out.println(((Link) gObject).getSource());

			if (gObject.isLink() && ((Link) gObject).getSource() == node) {// 数据没有进来
				System.out.println("添加数据");
				alArrayList.Add(gObject);
			}
		}
		return alArrayList;
	}

	@Override
	public ICollection GetLinksTo(Object node) {
		system.Collections.ArrayList alArrayList = new system.Collections.ArrayList();
		for (GraphicObject gObject : container.getNodes()) {
			if (gObject.isLink() && ((Link) gObject).getTarget() == node) {
				alArrayList.Add(gObject);
			}
		}
		return alArrayList;
	}

	@Override
	public Object GetTo(Object link) {
		return ((Link) link).getTarget();
	}

	@Override
	public Boolean IsLink(Object obj) {
		if (obj instanceof GraphicObject) {
			return true;
		}
		return false;
	}

	@Override
	public Boolean IsNode(Object obj) {
		if (obj instanceof GraphicObject) {
			return true;
		}
		return false;
	}

	@Override
	public void MoveNode(Object node, float x, float y) {
		((GraphicObject) node).transform(x, y);

	}

	@Override
	public void ReshapeLink(Object link, ReshapeLinkStyle style,
			Point2D fromPoint, ReshapeLinkMode fromPointMode, Point2D[] points,
			Integer startIndex, Integer length, Point2D toPoint,
			ReshapeLinkMode toPointMode) {
	}

	@Override
	public ICollection get_Links() {
		system.Collections.ArrayList alArrayList = new system.Collections.ArrayList();
		for (GraphicObject gObject : container.getNodes()) {
			if (gObject.isLink())
				alArrayList.Add(gObject);
		}
		return alArrayList;
	}

	@Override
	public ICollection get_Nodes() {
		system.Collections.ArrayList alArrayList = new system.Collections.ArrayList();
		for (GraphicObject gObject : container.getNodes()) {
			if (gObject.isNode())
				alArrayList.Add(gObject);
		}
		return alArrayList;
	}

	@Override
	public IGraphModel get_Parent() {
		// TODO Auto-generated method stub
		return null;
	}

	// private static java.lang.Object _adaptersProperty = new
	// java.lang.Object();
	//
	// private static java.lang.Object _beingRemovedProperty = new
	// java.lang.Object();
	//
	// private ILOG.Diagrammer.GraphLayout.GraphLayout _currentLayout;
	//
	// private Stack<ILOG.Diagrammer.GraphLayout.GraphLayout> _currentLayouts;
	//
	// private IGraphLayoutFilter _filter;
	//
	// private Boolean _fireEndUpdate = false;
	//
	// private static java.lang.Object _graphStateProperty = new
	// java.lang.Object();
	//
	// private static java.lang.Object _maxStateProperty = new
	// java.lang.Object();
	//
	// private IComparer _nodeComparer;
	//
	// private Transform _referenceTransform = Transform.Identity;
	//
	// private IDiagramView _referenceView;
	//
	private ILOG.Diagrammer.GraphLayout.CoordinatesMode coordinatesMode;
	//
	// private ILOG.Diagrammer.GraphicContainer grapher;
	//
	// private ILOG.Diagrammer.GraphLayout.GraphLayout
	// layoutThatStartedTransaction;
	//
	private ILOG.Diagrammer.GraphLayout.GraphLayout originaringLayout;

	//
	// private static java.lang.Object referenceViewProperty = new
	// java.lang.Object();
	//
	// private ILOG.Diagrammer.GraphLayout.CoordinatesMode saveCoordMode;
	//
	// private Transform saveReferenceTransform;
	//
	// private IDiagramView saveReferenceView;
	//
	// private DesignerTransaction transaction;
	//
	// private Boolean undoEventHandlerAdded = false;
	//
	// /* TODO: Event Declare */
	// public ArrayList<EventHandler> ReferenceViewChanged =new
	// ArrayList<EventHandler>();
	// public GraphicContainerAdapter (ILOG.Diagrammer.GraphicContainer
	// container){
	// this.grapher = container;
	// List<GraphicContainerAdapter> list =
	// (List<GraphicContainerAdapter>)container.get_Properties().get_Item(_adaptersProperty);
	// if(list == null){
	// list = new List<GraphicContainerAdapter>();
	// container.get_Properties().set_Item(_adaptersProperty,list);
	// }
	//
	// if(!list.Contains(this)){
	// list.Add(this);
	// }
	// container.ChildrenHierarchyChanged += this.ChildrenHierarchyChanged;
	// container.ChildChanged += this.ChildChanged;
	// container.IsUpdatingChanged += this.IsUpdatingChanged;
	// container.Painting += this.GrapherPainting;
	// container.Painted += this.GrapherPainted;
	// }
	//
	// @Override
	// public void AfterLayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout)
	// {
	// if(layout == this.grapher.get_GraphLayout()){
	// INestedGraph grapher = (INestedGraph)this.grapher;
	// if((grapher != null) && (grapher.get_NestedGraphMode() ==
	// NestedGraphMode.Subgraph)){
	// grapher.CorrectSubgraph(layout);
	// }
	// }
	// if((this.grapher.get_Site() != null) &&
	// this.grapher.IsTopLayout(layout)){
	// IDesignerHost service =
	// (IDesignerHost)this.grapher.get_Site().GetService(Type.GetType(IDesignerHost.class.getName()));
	// if((service != null) && (!this.get_InAutoLayout() ||
	// service.get_InTransaction())){
	// DiagramUtil.RaiseComponentChanging(this.grapher,"GraphState");
	// this.RecordGraphState();
	// DiagramUtil.RaiseComponentChanged(this.grapher,"GraphState");
	// }
	// }
	// if(((this.transaction != null) && (this.layoutThatStartedTransaction ==
	// layout)) && !this.get_InAutoLayout()){
	// this.transaction.Commit();
	// this.transaction = null;
	// }
	// this.coordinatesMode =
	// (ILOG.Diagrammer.GraphLayout.CoordinatesMode)layout.GetOriginalCoordModeOfModel();
	// if((this._currentLayouts != null) && (this._currentLayouts.get_Count() >
	// 0)){
	//
	// this._currentLayout = this._currentLayouts.Pop();
	// if(this._currentLayouts.get_Count() == 0){
	// this._currentLayouts = null;
	// }
	// }
	// else{
	// this._currentLayout = null;
	// }
	//
	// }
	//
	// @Override
	// public void BeforeLayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout)
	// {
	// if(this._currentLayout != null){
	// if(this._currentLayouts == null){
	// this._currentLayouts = new
	// Stack<ILOG.Diagrammer.GraphLayout.GraphLayout>();
	// }
	// this._currentLayouts.Push(this._currentLayout);
	// }
	// this._currentLayout = layout;
	// layout.SetOriginalCoordModeOfModel((Integer)this.coordinatesMode);
	// this.coordinatesMode = layout.get_CoordinatesMode();
	// if((this.grapher.get_Site() != null) &&
	// this.grapher.IsTopLayout(layout)){
	// if(!this.get_InAutoLayout()){
	//
	// this.transaction = DiagramUtil.CreateTransaction(this.grapher.get_Site(),
	// GraphLayoutTransactionDescription);
	// this.layoutThatStartedTransaction = layout;
	// }
	// IDesignerHost service =
	// (IDesignerHost)this.grapher.get_Site().GetService(Type.GetType(IDesignerHost.class.getName()));
	// if((service != null) && (!this.get_InAutoLayout() ||
	// service.get_InTransaction())){
	// this.RecordGraphState();
	// }
	// }
	// if((this.grapher.get_Site() != null) && !this.undoEventHandlerAdded){
	// UndoEngine engine =
	// (UndoEngine)this.grapher.get_Site().GetService(Type.GetType(UndoEngine.class.getName()));
	// if(engine != null){
	// engine.Undoing += this.undoEngine_Undoing;
	// this.undoEventHandlerAdded = true;
	// }
	// }
	//
	// }
	//
	// public static Boolean BelongsTo(java.lang.Object obj,
	// ILOG.Diagrammer.GraphicContainer grapher)
	// {
	// GraphicObject obj2 = (GraphicObject)obj;
	// if(obj2 != null){
	//
	// if(obj2.get_Properties().Contains(_beingRemovedProperty)){
	//
	// return false;
	// }
	// if(obj2.get_IsCollapsed()){
	//
	// return false;
	// }
	// ILOG.Diagrammer.GraphicContainer container =
	// (ILOG.Diagrammer.GraphicContainer)obj2;
	// if((container != null) && (GetNestedGraphMode(container) ==
	// NestedGraphMode.Transparent)){
	//
	// return false;
	// }
	// for( ILOG.Diagrammer.GraphicContainer container2 =
	// obj2.get_LogicalParent();
	// container2 != null;container2 = container2.get_LogicalParent()){
	// if(container2 == grapher){
	//
	// return true;
	// }
	// if(GetNestedGraphMode(container2) != NestedGraphMode.Transparent){
	//
	// return false;
	// }
	// }
	// }
	//
	// return false;
	//
	// }
	//
	// @Override
	// public Rectangle2D BoundingBox(java.lang.Object obj)
	// {
	// GraphicObject obj2 = (GraphicObject)obj;
	// if(this.coordinatesMode ==
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.InverseViewCoordinates){
	// Transform objectToViewTransform = this.GetObjectToViewTransform(obj2);
	// Transform transform2 = objectToViewTransform.Inverse() *
	// this.GetGeometryToGrapherTransform(obj2);
	//
	//
	// return transform2.TransformRectangle(GetBounds(obj2,
	// objectToViewTransform));
	// }
	// else if(this.coordinatesMode ==
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.ViewCoordinates){
	//
	//
	// return GetBounds(obj2, this.GetObjectToViewTransform(obj2));
	// }
	//
	//
	// return GetBounds(obj2, this.GetGeometryToGrapherTransform(obj2));
	//
	// }
	//
	// private void CheckLink(java.lang.Object obj)
	// {
	//
	// if(!this.IsLinkOrInterGraphLink(obj)){
	// throw(new ArgumentException(obj + " is not a link contained in graph " +
	// this.grapher));
	// }
	//
	// }
	//
	// private void CheckNode(java.lang.Object obj)
	// {
	//
	// if(!this.IsNode(obj)){
	// throw(new ArgumentException(obj + " is not a node contained in graph " +
	// this.grapher));
	// }
	//
	// }
	//
	// private void ChildChanged(java.lang.Object sender, GraphicChangeEventArgs
	// e)
	// {
	// if(e.get_IsGeometryChangeEvent()){
	// GraphicObject source = e.get_Source();
	// GraphModelContentsChangedEventAction geometryChanged =
	// GraphModelContentsChangedEventAction.GeometryChanged;
	//
	// if(this.IsLinkOrInterGraphLink(source)){
	// geometryChanged |=
	// GraphModelContentsChangedEventAction.LinkGeometryChanged;
	// }
	// else if(this.IsNode(source)){
	// geometryChanged |=
	// GraphModelContentsChangedEventAction.NodeGeometryChanged;
	// }
	// else{
	//
	// return ;
	// }
	// super.FireEvent(source,geometryChanged,this.IsUpdating());
	// }
	// else if(e.get_IsVisibilityChangeEvent()){
	// super.FireEvent(null,GraphModelContentsChangedEventAction.StructureChanged,this.IsUpdating());
	// }
	//
	// }
	//
	// private void ChildrenHierarchyChanged(java.lang.Object sender,
	// ChildrenHierarchyChangeEventArgs e)
	// {
	// IEnumerator it1 = e.get_Children().GetEnumerator();
	// while(it1.MoveNext()){
	// GraphicObject obj2 = (GraphicObject)it1.get_Current();
	// GraphModelContentsChangedEventAction structureChanged =
	// GraphModelContentsChangedEventAction.StructureChanged;
	// Boolean flag = this.IsLinkOrInterGraphLink(obj2);
	// Boolean flag2 = !flag && this.IsNode(obj2);
	// if(flag || flag2){
	// if((e.get_Action() == ChildrenHierarchyChangeAction.Remove) &&
	// (obj2.get_Parent() != null)){
	// obj2.get_Properties().set_Item(_beingRemovedProperty,true);
	// obj2.ParentChanged += this.obj_ParentChanged;
	// }
	// if(flag){
	// structureChanged |= (e.get_Action() == ChildrenHierarchyChangeAction.Add)
	// ? (GraphModelContentsChangedEventAction)0x10 :
	// (GraphModelContentsChangedEventAction)0x20;
	// }
	// else if(flag2){
	// structureChanged |= (e.get_Action() == ChildrenHierarchyChangeAction.Add)
	// ? (GraphModelContentsChangedEventAction)4 :
	// (GraphModelContentsChangedEventAction)8;
	// }
	// super.FireEvent(obj2,structureChanged,this.IsUpdating());
	// }
	// }
	//
	// }
	//
	// @Override
	// public AbstractGraphModel CreateGraphModel(java.lang.Object subgraph)
	// {
	//
	// return new
	// GraphicContainerAdapter((ILOG.Diagrammer.GraphicContainer)subgraph);
	//
	// }
	//
	// @Override
	// public void Dispose()
	// {
	// this.grapher.ChildrenHierarchyChanged -= this.ChildrenHierarchyChanged;
	// this.grapher.ChildChanged -= this.ChildChanged;
	// this.grapher.IsUpdatingChanged -= this.IsUpdatingChanged;
	// this.grapher.Painting -= this.GrapherPainting;
	// this.grapher.Painted -= this.GrapherPainted;
	// this.set_ReferenceView(null);
	// List<GraphicContainerAdapter> list =
	// (List<GraphicContainerAdapter>)this.grapher.get_Properties().get_Item(_adaptersProperty);
	// if(list != null){
	// list.Remove(this);
	// if(list.get_Count() == 0){
	// this.grapher.get_Properties().Remove(_adaptersProperty);
	// }
	// }
	// this._currentLayouts = null;
	// this._currentLayout = null;
	// super.Dispose();
	//
	// }
	//
	// public static ICollection<GraphicContainerAdapter>
	// GetAdapters(ILOG.Diagrammer.GraphicContainer grapher)
	// {
	//
	// return
	// ((ICollection<GraphicContainerAdapter>)grapher.get_Properties().get_Item(_adaptersProperty));
	//
	// }
	//
	// private static Rectangle2D GetBounds(GraphicObject go, Transform t)
	// {
	// IGraphLayoutBoundsProvider provider = (IGraphLayoutBoundsProvider)go;
	// if(provider != null){
	//
	//
	// return provider.GetGraphLayoutBounds(t);
	// }
	//
	//
	// return go.GetStyledBounds(t);
	//
	// }
	//
	// @Override
	// public java.lang.Object GetFrom(java.lang.Object link)
	// {
	// this.CheckLink(link);
	//
	// return ((Link)link).get_Start();
	//
	// }
	//
	// private Transform GetGeometryToGrapherTransform(GraphicObject go)
	// {
	// Transform identity = Transform.Identity;
	// for( ILOG.Diagrammer.GraphicContainer container = go.get_LogicalParent();
	// container != this.grapher;container = container.get_LogicalParent()){
	// identity *= container.get_LogicalChildTransform();
	// identity *= container.get_Transform();
	// }
	//
	// return (go.get_Transform() * identity);
	//
	// }
	//
	// public static Integer GetGraphState(ILOG.Diagrammer.GraphicContainer
	// container)
	// {
	// java.lang.Object obj2 =
	// container.get_Properties().get_Item(_graphStateProperty);
	// if(obj2 != null){
	//
	// return (Integer)obj2;
	// }
	//
	// return 0;
	//
	// }
	//
	// @Override
	// public Point2D[] GetLinkPoints(java.lang.Object link)
	// {
	// Link go = (Link)link;
	// Point2D[] points = go.get_Points().ToArray();
	// Transform identity = Transform.Identity;
	// if(this.coordinatesMode ==
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.InverseViewCoordinates){
	//
	// identity = this.GetGeometryToGrapherTransform(go);
	// //NOTICE: break ignore!!!
	// }
	// else if(this.coordinatesMode ==
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.ViewCoordinates){
	//
	// identity = this.GetObjectToViewTransform(go);
	// //NOTICE: break ignore!!!
	// }
	// else {
	//
	// identity = this.GetGeometryToGrapherTransform(go);
	// //NOTICE: break ignore!!!
	// }
	// if(!identity.get_IsIdentity()){
	// identity.TransformPoints(points);
	// }
	//
	// return points;
	//
	// }
	//
	// @Override
	// public ICollection GetLinksFrom(java.lang.Object node)
	// {
	// this.CheckNode(node);
	// ReadOnlyCollection<ILink> links =
	// ((GraphicObject)node).GetLinks(LinkType.Starting);
	// List<ILink> list = new List<ILink>();
	// IEnumerator it1 = links.GetEnumerator();
	// while(it1.MoveNext()){
	// ILink link = (ILink)it1.get_Current();
	//
	// if(this.IsLink(link)){
	// list.Add(link);
	// }
	// }
	//
	// return list;
	//
	// }
	//
	// @Override
	// public ICollection GetLinksTo(java.lang.Object node)
	// {
	// this.CheckNode(node);
	// ReadOnlyCollection<ILink> links =
	// ((GraphicObject)node).GetLinks(LinkType.Ending);
	// List<ILink> list = new List<ILink>();
	// IEnumerator it1 = links.GetEnumerator();
	// while(it1.MoveNext()){
	// ILink link = (ILink)it1.get_Current();
	//
	// if(this.IsLink(link)){
	// list.Add(link);
	// }
	// }
	//
	// return list;
	//
	// }
	//
	// @Override
	// public float GetLinkWidth(java.lang.Object link)
	// {
	// Transform objectToViewTransform = null;
	// Transform geometryToGrapherTransform = null;
	// Link link2 = (Link)link;
	// Stroke stroke = link2.get_Stroke();
	// if(stroke == null){
	//
	//
	// return super.GetLinkWidth(link2);
	// }
	// if(this.coordinatesMode ==
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.InverseViewCoordinates){
	//
	// objectToViewTransform = this.GetObjectToViewTransform(link2);
	//
	// geometryToGrapherTransform = this.GetGeometryToGrapherTransform(link2);
	// //NOTICE: break ignore!!!
	// }
	// else if(this.coordinatesMode ==
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.ViewCoordinates){
	//
	// objectToViewTransform = this.GetObjectToViewTransform(link2);
	// geometryToGrapherTransform = objectToViewTransform;
	// //NOTICE: break ignore!!!
	// }
	// else {
	//
	// objectToViewTransform = this.GetGeometryToGrapherTransform(link2);
	// geometryToGrapherTransform = objectToViewTransform;
	// //NOTICE: break ignore!!!
	// }
	// float num = stroke.GetWidth(objectToViewTransform) +
	// link2.GetBorderWidth(objectToViewTransform);
	// double num2 = Math.Min(geometryToGrapherTransform.get_ScaleX(),
	// geometryToGrapherTransform.get_ScaleY());
	//
	// return (float)(num * num2);
	//
	// }
	//
	// private static Integer GetMaxGraphState(ILOG.Diagrammer.GraphicContainer
	// container)
	// {
	// java.lang.Object obj2 =
	// container.get_Properties().get_Item(_maxStateProperty);
	// if(obj2 != null){
	//
	// return (Integer)obj2;
	// }
	//
	// return 0;
	//
	// }
	//
	// private static NestedGraphMode
	// GetNestedGraphMode(ILOG.Diagrammer.GraphicContainer container)
	// {
	// if(container != null){
	// INestedGraph graph = (INestedGraph)container;
	// if(graph != null){
	//
	// return graph.get_NestedGraphMode();
	// }
	// }
	//
	// return NestedGraphMode.Node;
	//
	// }
	//
	// private static IObjectState
	// GetObjectState(ILOG.Diagrammer.GraphicContainer grapher, GraphicObject
	// obj, Integer graphState)
	// {
	// if(obj.get_Name() != null){
	// IObjectState state = null;
	// Dictionary<String,IObjectState> dictionary = GetStatesDictionary(grapher,
	// graphState, false);
	// if((dictionary != null) && IObjectState[] __value_0 = new
	// IObjectState[1];
	// dictionary.TryGetValue(obj.get_Name(),__value_0);
	// state = __value_0[0]){
	//
	// return state;
	// }
	// }
	//
	// return null;
	//
	// }
	//
	// private Transform GetObjectToViewTransform(GraphicObject obj)
	// {
	// Transform transform = (obj == this.grapher) ? Transform.Identity :
	// this.GetGeometryToGrapherTransform(obj);
	//
	// return (transform * this.get_ReferenceTransform());
	//
	// }
	//
	public ILOG.Diagrammer.GraphLayout.GraphLayout GetOriginatingLayout() {

		return this.originaringLayout;

	}

	//
	// @Override
	// public java.lang.Object GetProperty(String key)
	// {
	//
	// return this.grapher.get_Properties().get_Item(key);
	//
	// }
	//
	// @Override
	// public java.lang.Object GetProperty(java.lang.Object nodeOrLink, String
	// key)
	// {
	//
	// return ((GraphicObject)nodeOrLink).get_Properties().get_Item(key);
	//
	// }
	//
	// public static IDiagramView
	// GetReferenceView(ILOG.Diagrammer.GraphicContainer container)
	// {
	//
	// return
	// ((IDiagramView)container.get_Properties().get_Item(referenceViewProperty));
	//
	// }
	//
	// private static Dictionary<String,IObjectState>
	// GetStatesDictionary(ILOG.Diagrammer.GraphicContainer grapher, Integer
	// graphState, Boolean create)
	// {
	// String str = "_GraphicContainerAdapter_State_" + graphState;
	// Dictionary<String,IObjectState> dictionary =
	// (Dictionary<String,IObjectState>)grapher.get_Properties().get_Item(str);
	// if((dictionary == null) && create){
	// dictionary = new Dictionary<String,IObjectState>();
	// grapher.get_Properties().set_Item(str,dictionary);
	// }
	//
	// return dictionary;
	//
	// }
	//
	// @Override
	// public java.lang.Object GetTo(java.lang.Object link)
	// {
	// this.CheckLink(link);
	//
	// return ((Link)link).get_End();
	//
	// }
	//
	// private void GrapherPainted(java.lang.Object sender, EventArgs e)
	// {
	// this._fireEndUpdate = false;
	//
	// }
	//
	// private void GrapherPainting(java.lang.Object sender, EventArgs e)
	// {
	// if(this._fireEndUpdate){
	// super.EndUpdate();
	// this._fireEndUpdate = false;
	// }
	//
	// }
	//
	// @Override
	// public Boolean HasPinnedConnectionPoint(java.lang.Object link, Boolean
	// origin)
	// {
	//
	// if(this.IsLinkOrInterGraphLink(link)){
	// Link link2 = (Link)link;
	// BoundsAnchor anchor = (BoundsAnchor)(origin ?
	// ((BoundsAnchor)link2.get_StartAnchor()) :
	// ((BoundsAnchor)link2.get_EndAnchor()));
	// if(anchor != null){
	//
	// return ((anchor.get_Position() != AnchorPosition.Automatic) &&
	// !anchor.get_CanMove());
	// }
	// }
	//
	//
	// return super.HasPinnedConnectionPoint(link,origin);
	//
	// }
	//
	// private void host_TransactionClosing(java.lang.Object sender,
	// DesignerTransactionCloseEventArgs e)
	// {
	// IDesignerHost host = (IDesignerHost)sender;
	// host.TransactionClosing -= this.host_TransactionClosing;
	// if(this._fireEndUpdate){
	// super.EndUpdate();
	// this._fireEndUpdate = false;
	// }
	//
	// }
	//
	// public Boolean IsIgnored(java.lang.Object obj)
	// {
	// if(this._filter != null){
	// GraphicObject obj2 = (GraphicObject)obj;
	// if(obj2 != null){
	//
	//
	// return !this._filter.Accept(this,obj2);
	// }
	// }
	// if(this._currentLayout == null){
	//
	// return false;
	// }
	// ILOG.Diagrammer.GraphLayout.GraphLayout layout = this._currentLayout;
	// for( ILOG.Diagrammer.GraphLayout.GraphLayout layout2 =
	// layout.GetParentLayout();
	// layout2 != null;
	// layout2 = layout2.GetParentLayout()){
	// layout = layout2;
	// }
	//
	//
	// return layout.GetIgnored(obj);
	//
	// }
	//
	// @Override
	// public Boolean IsInterGraphLink(java.lang.Object obj)
	// {
	//
	//
	// return (IsInterGraphLink(obj, this.grapher) && !this.IsIgnored(obj));
	//
	// }
	//
	// private static Boolean IsInterGraphLink(java.lang.Object obj,
	// ILOG.Diagrammer.GraphicContainer grapher)
	// {
	// Link link = (Link)obj;
	//
	//
	//
	// return ((((((link != null) && (link.get_Start() != null)) &&
	// ((link.get_End() != null) && !link.get_StartAnchor().get_IsDefault())) &&
	// ((!link.get_EndAnchor().get_IsDefault() && BelongsTo(link, grapher)) &&
	// (!BelongsTo(link.get_Start(), grapher) || !BelongsTo(link.get_End(),
	// grapher)))) && !(link.get_Start() instanceof Link)) && !(link.get_End()
	// instanceof Link));
	//
	// }
	//
	// @Override
	// public Boolean IsLink(java.lang.Object obj)
	// {
	// Link link = (Link)obj;
	//
	//
	//
	//
	// return ((((((link != null) && (link.get_Start() != null)) &&
	// ((link.get_End() != null) && !link.get_StartAnchor().get_IsDefault())) &&
	// ((!link.get_EndAnchor().get_IsDefault() && BelongsTo(link, this.grapher))
	// && (BelongsTo(link.get_Start(), this.grapher) &&
	// BelongsTo(link.get_End(), this.grapher)))) && ((!this.IsIgnored(obj) &&
	// !this.IsIgnored(link.get_Start())) && (!this.IsIgnored(link.get_End()) &&
	// !(link.get_Start() instanceof Link)))) && !(link.get_End() instanceof
	// Link));
	//
	// }
	//
	// public Boolean IsLinkOrInterGraphLink(java.lang.Object obj)
	// {
	//
	//
	// return (IsLinkOrInterGraphLink(obj, this.grapher) &&
	// !this.IsIgnored(obj));
	//
	// }
	//
	// private static Boolean IsLinkOrInterGraphLink(java.lang.Object obj,
	// ILOG.Diagrammer.GraphicContainer grapher)
	// {
	// Link link = (Link)obj;
	//
	// return (((((link != null) && (link.get_Start() != null)) &&
	// ((link.get_End() != null) && !link.get_StartAnchor().get_IsDefault())) &&
	// ((!link.get_EndAnchor().get_IsDefault() && BelongsTo(link, grapher)) &&
	// !(link.get_Start() instanceof Link))) && !(link.get_End() instanceof
	// Link));
	//
	// }
	//
	// @Override
	// public Boolean IsNode(java.lang.Object obj)
	// {
	//
	//
	// return (IsNode(obj, this.grapher) && !this.IsIgnored(obj));
	//
	// }
	//
	// private static Boolean IsNode(java.lang.Object obj,
	// ILOG.Diagrammer.GraphicContainer grapher)
	// {
	//
	// return (((obj instanceof GraphicObject) && !(obj instanceof Link)) &&
	// BelongsTo(obj, grapher));
	//
	// }
	//
	// @Override
	// public Boolean IsSubgraph(java.lang.Object obj)
	// {
	// ILOG.Diagrammer.GraphicContainer container =
	// (ILOG.Diagrammer.GraphicContainer)obj;
	//
	//
	// return ((((container != null) && (GetNestedGraphMode(container) ==
	// NestedGraphMode.Subgraph)) && BelongsTo(container, this.grapher)) &&
	// !this.IsIgnored(container));
	//
	// }
	//
	// private Boolean IsUpdating()
	// {
	// this._fireEndUpdate = true;
	//
	// return true;
	//
	// }
	//
	// private void IsUpdatingChanged(java.lang.Object sender, EventArgs e)
	// {
	//
	// if(!this.IsUpdating()){
	// super.EndUpdate();
	// }
	//
	// }
	//
	// private void MoveAnchor(Link link, Point2D newPoint, Point2D
	// referencePoint, Boolean start)
	// {
	// Anchor anchor = start ? link.get_StartAnchor() : link.get_EndAnchor();
	// GraphicObject owner = anchor.get_Owner();
	// BoundsAnchor item = (BoundsAnchor)anchor;
	// AnchorPosition center = AnchorPosition.Center;
	// Rectangle2D bounds = GetBounds(owner, owner.get_Transform());
	// float num = Math.Min(bounds.get_Width(),bounds.get_Height()) / 10f;
	// if((newPoint.get_X() > (bounds.get_X() - num)) && (newPoint.get_X() <
	// (bounds.get_X() + num))){
	// center = AnchorPosition.Left;
	// }
	// else if((newPoint.get_X() > (bounds.get_Right() - num)) &&
	// (newPoint.get_X() < (bounds.get_Right() + num))){
	// center = AnchorPosition.Right;
	// }
	// else if((newPoint.get_Y() > (bounds.get_Y() - num)) && (newPoint.get_Y()
	// < (bounds.get_Y() + num))){
	// center = AnchorPosition.Top;
	// }
	// else if((newPoint.get_Y() > (bounds.get_Bottom() - num)) &&
	// (newPoint.get_Y() < (bounds.get_Bottom() + num))){
	// center = AnchorPosition.Bottom;
	// }
	// else{
	// center = AnchorPosition.Center;
	// }
	// if(((item != null) && item.CanBeMoved()) && (item.get_Links().get_Count()
	// <= 1)){
	// item.set_Position(center);
	// }
	// else{
	// item = null;
	// IEnumerator it1 = owner.get_Anchors().GetEnumerator();
	// while(it1.MoveNext()){
	// Anchor anchor3 = (Anchor)it1.get_Current();
	// if(((anchor3 instanceof BoundsAnchor) && (anchor3.get_Links().get_Count()
	// == 0)) && (((BoundsAnchor)anchor3).get_Position() == center)){
	// item = (BoundsAnchor)anchor3;
	// break;
	// }
	// }
	// if(item == null){
	// item = new BoundsAnchor(center);
	// if(owner.get_Site() != null){
	// owner.get_Anchors().Add(item);
	// if(owner.get_Site().get_Container() != null){
	// owner.get_Site().get_Container().Add(item);
	// }
	// }
	// else{
	// owner.get_Anchors().Add(item);
	// }
	// }
	// if(start){
	// link.set_StartAnchor(item);
	// }
	// else{
	// link.set_EndAnchor(item);
	// }
	// }
	//
	// newPoint = link.get_Transform().TransformPoint(newPoint);
	// item.MoveTo(newPoint,referencePoint,link.get_Parent());
	//
	// }
	//
	// private void MoveAndClip(Link l, Point2D newPoint, Point2D
	// referencePoint, Boolean start)
	// {
	// GraphicObject go = start ? l.get_Start() : l.get_End();
	// Transform geometryToContainerTransform =
	// l.GetGeometryToContainerTransform(go.get_Parent());
	//
	// newPoint = geometryToContainerTransform.TransformPoint(newPoint);
	//
	// referencePoint =
	// geometryToContainerTransform.TransformPoint(referencePoint);
	// Rectangle2D bounds = GetBounds(go, Transform.Identity);
	//
	// newPoint = AnchorUtil.GetPointOnOutline(go, bounds, referencePoint,
	// newPoint);
	//
	// geometryToContainerTransform = geometryToContainerTransform.Inverse();
	//
	// newPoint = geometryToContainerTransform.TransformPoint(newPoint);
	//
	// referencePoint =
	// geometryToContainerTransform.TransformPoint(referencePoint);
	// this.MoveAnchor(l,newPoint,referencePoint,start);
	//
	// }
	//
	// @Override
	// public void MoveNode(java.lang.Object node, float x, float y)
	// {
	// Transform objectToViewTransform = null;
	// float num = null;
	// float num2 = null;
	// Rectangle2D bounds = null;
	// GraphicObject obj2 = (GraphicObject)node;
	// if(this.coordinatesMode ==
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.InverseViewCoordinates){
	//
	// objectToViewTransform = this.GetObjectToViewTransform(obj2);
	//
	// bounds = GetBounds(obj2, objectToViewTransform);
	//
	// bounds = (objectToViewTransform.Inverse() *
	// this.GetGeometryToGrapherTransform(obj2)).TransformRectangle(bounds);
	// num = x - bounds.get_X();
	// num2 = y - bounds.get_Y();
	// //NOTICE: break ignore!!!
	// }
	// else if(this.coordinatesMode ==
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.ViewCoordinates){
	//
	// objectToViewTransform = this.GetObjectToViewTransform(obj2);
	//
	// bounds = GetBounds(obj2, objectToViewTransform);
	// num = (x - bounds.get_X()) / (objectToViewTransform.get_ScaleX() /
	// obj2.get_Transform().get_ScaleX());
	// num2 = (y - bounds.get_Y()) / (objectToViewTransform.get_ScaleY() /
	// obj2.get_Transform().get_ScaleY());
	// //NOTICE: break ignore!!!
	// }
	// else {
	//
	// objectToViewTransform = this.GetGeometryToGrapherTransform(obj2);
	//
	// bounds = GetBounds(obj2, objectToViewTransform);
	// num = x - bounds.get_X();
	// num2 = y - bounds.get_Y();
	// //NOTICE: break ignore!!!
	// }
	// if((num != 0f) || (num2 != 0f)){
	// Point2D point = new Point2D(x - bounds.get_X(), y - bounds.get_Y());
	//
	// point =
	// obj2.get_Parent().GetTransformToContainer(this.grapher).Inverse().TransformVector(point);
	// num = point.get_X();
	// num2 = point.get_Y();
	// obj2.Translate(num,num2);
	// }
	//
	// }
	//
	// private void obj_ParentChanged(java.lang.Object sender,
	// GraphicChangeEventArgs e)
	// {
	// GraphicObject obj2 = (GraphicObject)sender;
	// obj2.get_Properties().Remove(_beingRemovedProperty);
	// obj2.ParentChanged -= this.obj_ParentChanged;
	//
	// }
	//
	// private void RecordGraphState()
	// {
	// SetGraphState(this.grapher, GetMaxGraphState(this.grapher) + 1);
	//
	// }
	//
	// @Override
	// public void ReshapeLink(java.lang.Object link, ReshapeLinkStyle style,
	// Point2D fromPoint, ReshapeLinkMode fromPointMode, Point2D[] points,
	// Integer startIndex, Integer length, Point2D toPoint, ReshapeLinkMode
	// toPointMode)
	// {
	// Point2D[] pointdArray = null;
	// Link go = (Link)link;
	// Transform identity = Transform.Identity;
	// if(this.coordinatesMode ==
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.InverseViewCoordinates){
	//
	// identity = this.GetGeometryToGrapherTransform(go);
	// //NOTICE: break ignore!!!
	// }
	// else if(this.coordinatesMode ==
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.ViewCoordinates){
	//
	// identity = this.GetObjectToViewTransform(go);
	// //NOTICE: break ignore!!!
	// }
	// else {
	//
	// identity = this.GetGeometryToGrapherTransform(go);
	// //NOTICE: break ignore!!!
	// }
	// if(!identity.get_IsIdentity()){
	//
	// identity = identity.Inverse();
	//
	// fromPoint = identity.TransformPoint(fromPoint);
	// identity.TransformPoints(points);
	//
	// toPoint = identity.TransformPoint(toPoint);
	// }
	// if((length == 0) && (go.get_Points().get_Count() == 2)){
	// pointdArray = null;
	// }
	// else{
	// pointdArray = new Point2D[length + 2];
	// if(fromPointMode == ReshapeLinkMode.Ignore || fromPointMode ==
	// ReshapeLinkMode.Fix || fromPointMode == ReshapeLinkMode.Clip){
	// if(go.get_Points().get_Count() <= 0){
	// pointdArray[0] = Point2D.Empty;
	// break;
	// }
	// pointdArray[0] = go.get_Points()[0];
	// //NOTICE: break ignore!!!
	// }
	// else if(fromPointMode == ReshapeLinkMode.Move || fromPointMode ==
	// ReshapeLinkMode.MoveAndClip){
	// pointdArray[0] = fromPoint;
	// //NOTICE: break ignore!!!
	// }
	// if(toPointMode == ReshapeLinkMode.Ignore || toPointMode ==
	// ReshapeLinkMode.Fix || toPointMode == ReshapeLinkMode.Clip){
	// if(go.get_Points().get_Count() <= 0){
	// pointdArray[length + 1] = Point2D.Empty;
	// break;
	// }
	// pointdArray[length + 1] = go.get_Points()[go.get_Points().get_Count() -
	// 1];
	// //NOTICE: break ignore!!!
	// }
	// else if(toPointMode == ReshapeLinkMode.Move || toPointMode ==
	// ReshapeLinkMode.MoveAndClip){
	// pointdArray[length + 1] = toPoint;
	// //NOTICE: break ignore!!!
	// }
	// if(points != null){
	// clr.System.ArrayStaticWrapper.Copy(points,startIndex,pointdArray,1,length);
	// }
	// }
	// if(style == ReshapeLinkStyle.Ignore){
	// //NOTICE: break ignore!!!
	// }
	// else if(style == ReshapeLinkStyle.Straight){
	// go.set_ShapeType(LinkShapeType.Straight);
	// //NOTICE: break ignore!!!
	// }
	// else if(style == ReshapeLinkStyle.Orthogonal){
	// if(!identity.get_IsScale()){
	// go.set_ShapeType(LinkShapeType.Free);
	// break;
	// }
	// go.set_ShapeType(LinkShapeType.Orthogonal);
	// go.set_CanEditOrthogonalShape(true);
	// //NOTICE: break ignore!!!
	// }
	// else {
	// go.set_ShapeType(LinkShapeType.Free);
	// //NOTICE: break ignore!!!
	// }
	// if(pointdArray != null){
	// go.SetPoints(pointdArray);
	// }
	// if(go.get_StartAnchor().get_CanMove()){
	// if(fromPointMode == ReshapeLinkMode.Move){
	// this.MoveAnchor(go,fromPoint,(length > 0) ? points[startIndex] :
	// toPoint,true);
	// //NOTICE: break ignore!!!
	// }
	// else if(fromPointMode == ReshapeLinkMode.Fix){
	// if(go.get_StartAnchor().NeedsReferencePoint() &&
	// (go.get_Points().get_Count() > 0)){
	// this.MoveAnchor(go,go.get_Points()[0],Point2D.Empty,true);
	// }
	// //NOTICE: break ignore!!!
	// }
	// else if(fromPointMode == ReshapeLinkMode.Clip){
	// this.SetShapeAnchor(go,true,Point2D.Invalid);
	// //NOTICE: break ignore!!!
	// }
	// else if(fromPointMode == ReshapeLinkMode.MoveAndClip){
	// this.MoveAndClip(go,fromPoint,(length > 0) ? points[startIndex] :
	// toPoint,true);
	// //NOTICE: break ignore!!!
	// }
	// }
	// if(go.get_EndAnchor().get_CanMove()){
	// if(toPointMode == ReshapeLinkMode.Move){
	// this.MoveAnchor(go,toPoint,(length > 0) ? points[(startIndex + length) -
	// 1] : fromPoint,false);
	// //NOTICE: break ignore!!!
	// }
	// else if(toPointMode == ReshapeLinkMode.Fix){
	// if(go.get_EndAnchor().NeedsReferencePoint() &&
	// (go.get_Points().get_Count() > 0)){
	// this.MoveAnchor(go,go.get_Points()[go.get_Points().get_Count() -
	// 1],Point2D.Empty,false);
	// }
	// //NOTICE: break ignore!!!
	// }
	// else if(toPointMode == ReshapeLinkMode.Clip){
	// this.SetShapeAnchor(go,false,Point2D.Invalid);
	// //NOTICE: break ignore!!!
	// }
	// else if(toPointMode == ReshapeLinkMode.MoveAndClip){
	// this.MoveAndClip(go,toPoint,(length > 0) ? points[(startIndex + length) -
	// 1] : fromPoint,false);
	// //NOTICE: break ignore!!!
	// }
	// }
	// if(((length == 0) && (go.get_Points().get_Count() > 2)) &&
	// (go.get_ShapeType() == LinkShapeType.Orthogonal)){
	// Point2D pointd = go.get_Points()[0];
	// Point2D pointd2 = go.get_Points()[go.get_Points().get_Count() - 1];
	// if((pointd.get_X() != pointd2.get_X()) && (pointd.get_Y() !=
	// pointd2.get_Y())){
	// pointdArray = new Point2D[4];
	// pointdArray[0] = pointd;
	// Point2D pointd3 = new Point2D((pointd.get_X() + pointd2.get_X()) / 2f,
	// (pointd.get_Y() + pointd2.get_Y()) / 2f);
	// pointdArray[1] = pointd3;
	// pointdArray[2] = pointd3;
	// pointdArray[3] = pointd2;
	// go.SetPoints(pointdArray);
	// }
	// }
	//
	// }
	//
	public void RestoreAndDisableViewCoordinates() {
		// this.coordinatesMode = this.saveCoordMode;
		// this._referenceView = this.saveReferenceView;
		// this._referenceTransform = this.saveReferenceTransform;

	}

	//
	// private static void RestoreLinksState(ILOG.Diagrammer.GraphicContainer
	// grapher, ILOG.Diagrammer.GraphicContainer container, Integer graphState)
	// {
	// IEnumerator it1 = container.get_LogicalChildren().GetEnumerator();
	// while(it1.MoveNext()){
	// GraphicObject obj2 = (GraphicObject)it1.get_Current();
	// ILOG.Diagrammer.GraphicContainer container2 =
	// (ILOG.Diagrammer.GraphicContainer)obj2;
	// NestedGraphMode nestedGraphMode = GetNestedGraphMode(container2);
	// if((container2 != null) && (nestedGraphMode != NestedGraphMode.Node)){
	// RestoreLinksState((nestedGraphMode == NestedGraphMode.Transparent) ?
	// grapher : container2, container2, graphState);
	// }
	// else if(IsLinkOrInterGraphLink(obj2, grapher)){
	// IObjectState state = GetObjectState(grapher, obj2, graphState);
	// if(state != null){
	// state.Restore(grapher,obj2);
	// }
	// }
	// }
	//
	// }
	//
	// private static void RestoreNodesState(ILOG.Diagrammer.GraphicContainer
	// grapher, ILOG.Diagrammer.GraphicContainer container, Integer graphState)
	// {
	// IObjectState state = GetObjectState(grapher, container, graphState);
	// if(state != null){
	// state.Restore(grapher,container);
	// }
	// IEnumerator it1 = container.get_LogicalChildren().GetEnumerator();
	// while(it1.MoveNext()){
	// GraphicObject obj2 = (GraphicObject)it1.get_Current();
	// ILOG.Diagrammer.GraphicContainer container2 =
	// (ILOG.Diagrammer.GraphicContainer)obj2;
	// NestedGraphMode nestedGraphMode = GetNestedGraphMode(container2);
	// if((container2 != null) && (nestedGraphMode != NestedGraphMode.Node)){
	// RestoreNodesState((nestedGraphMode == NestedGraphMode.Transparent) ?
	// grapher : container2, container2, graphState);
	// }
	//
	// else if((nestedGraphMode == NestedGraphMode.Subgraph) || !IsNode(obj2,
	// grapher)){
	// continue;
	// }
	// IObjectState state2 = GetObjectState(grapher, obj2, graphState);
	// if(state2 != null){
	// state2.Restore(grapher,obj2);
	// }
	// }
	//
	// }
	//
	public void SaveAndEnableViewCoordinates(IGraphModel rootGraphModel) {
		// GraphicContainerAdapter adapter =
		// (GraphicContainerAdapter)rootGraphModel;
		// this.saveCoordMode = this.coordinatesMode;
		// this.saveReferenceView = this._referenceView;
		// this.saveReferenceTransform = this._referenceTransform;
		// IDiagramView referenceView = adapter.get_ReferenceView();
		// ILOG.Diagrammer.GraphicContainer graphicContainer =
		// adapter.get_GraphicContainer();
		// if(referenceView == null){
		// ILOG.Diagrammer.GraphicContainer container2 =
		// this.get_GraphicContainer();
		// this._referenceView = null;
		//
		// this._referenceTransform =
		// container2.GetTransformToContainer(graphicContainer);
		// }
		// else{
		// this._referenceView = referenceView;
		// }
		// this.coordinatesMode =
		// ILOG.Diagrammer.GraphLayout.CoordinatesMode.ViewCoordinates;

	}

	//
	// private static void SaveGraphState(ILOG.Diagrammer.GraphicContainer
	// grapher, ILOG.Diagrammer.GraphicContainer container, Integer graphState)
	// {
	// IEnumerator it1 = container.get_LogicalChildren().GetEnumerator();
	// while(it1.MoveNext()){
	// GraphicObject obj2 = (GraphicObject)it1.get_Current();
	// IObjectState state = null;
	// ILOG.Diagrammer.GraphicContainer container2 =
	// (ILOG.Diagrammer.GraphicContainer)obj2;
	// NestedGraphMode nestedGraphMode = GetNestedGraphMode(container2);
	// if((container2 != null) && (nestedGraphMode != NestedGraphMode.Node)){
	// SaveGraphState((nestedGraphMode == NestedGraphMode.Transparent) ? grapher
	// : container2, container2, graphState);
	// if(nestedGraphMode != NestedGraphMode.Transparent){
	// continue;
	// }
	// state = new GroupState();
	// }
	// else if(IsNode(obj2, grapher)){
	// state = new NodeState();
	// }
	// else{
	//
	// if(!IsLinkOrInterGraphLink(obj2, grapher)){
	// continue;
	// }
	// state = new LinkState();
	// }
	// state.Save(grapher,obj2);
	// SetObjectState(grapher, obj2, graphState, state);
	// }
	// if(GetNestedGraphMode(container) == NestedGraphMode.Subgraph){
	// IObjectState state2 = new NodeState();
	// state2.Save(grapher,container);
	// SetObjectState(grapher, container, graphState, state2);
	// }
	//
	// }
	//
	public void SetContentsAdjusting(Boolean adjusting, Boolean withParents) {
		// if(adjusting){
		// this.grapher.BeginUpdate();
		// if(withParents){
		// for( ILOG.Diagrammer.GraphicContainer container =
		// this.grapher.get_Parent();
		// container != null;container = container.get_Parent()){
		// container.BeginUpdate();
		// }
		// }
		// }
		// else if(this.grapher.get_IsUpdating()){
		// this.grapher.EndUpdate();
		// if(withParents){
		// for( ILOG.Diagrammer.GraphicContainer container2 =
		// this.grapher.get_Parent();
		// container2 != null;container2 = container2.get_Parent()){
		// container2.EndUpdate();
		// }
		// }
		// }

	}

	//
	// public static void SetGraphState(ILOG.Diagrammer.GraphicContainer
	// container, Integer graphState)
	// {
	// if(graphState != GetGraphState(container)){
	// container.get_Properties().set_Item(_graphStateProperty,graphState);
	// Integer maxGraphState = GetMaxGraphState(container);
	// if(graphState > maxGraphState){
	// container.get_Properties().set_Item(_maxStateProperty,graphState);
	// SaveGraphState(container, container, graphState);
	// }
	// else{
	// try{
	// container.BeginUpdate();
	// RestoreNodesState(container, container, graphState);
	// RestoreLinksState(container, container, graphState);
	// }
	// finally{
	// container.EndUpdate();
	// }
	// }
	// }
	//
	// }
	//
	// private static void SetObjectState(ILOG.Diagrammer.GraphicContainer
	// grapher, GraphicObject obj, Integer graphState, IObjectState state)
	// {
	// if(obj.get_Name() != null){
	// //change by QiCheng.Ai
	// GetStatesDictionary(grapher, graphState, true).put(obj.get_Name(),state);
	// }
	//
	// }
	//
	// public void SetOriginatingLayout(ILOG.Diagrammer.GraphLayout.GraphLayout
	// layout)
	// {
	// this.originaringLayout = layout;
	//
	// }
	//
	// @Override
	// public void SetProperty(String key, java.lang.Object value)
	// {
	// if(value != null){
	// this.grapher.get_Properties().set_Item(key,value);
	// }
	// else{
	// this.grapher.get_Properties().Remove(key);
	// }
	//
	// }
	//
	// @Override
	// public void SetProperty(java.lang.Object nodeOrLink, String key,
	// java.lang.Object value)
	// {
	// if(value != null){
	// ((GraphicObject)nodeOrLink).get_Properties().set_Item(key,value);
	// }
	// else{
	// ((GraphicObject)nodeOrLink).get_Properties().Remove(key);
	// }
	//
	// }
	//
	// public static void SetReferenceView(ILOG.Diagrammer.GraphicContainer
	// container, IDiagramView view)
	// {
	// if(view != null){
	// container.get_Properties().set_Item(referenceViewProperty,view);
	// }
	// else{
	// container.get_Properties().Remove(referenceViewProperty);
	// }
	//
	// }
	//
	// private void SetShapeAnchor(Link link, Boolean start, Point2D center)
	// {
	// Point2D empty = null;
	// Anchor anchor = start ? link.get_StartAnchor() : link.get_EndAnchor();
	// GraphicObject owner = anchor.get_Owner();
	// ShapeAnchor item = (ShapeAnchor)anchor;
	// if(center.get_IsInvalid()){
	// empty = Point2D.Empty;
	// }
	// else{
	//
	// center =
	// link.GetGeometryToContainerTransform(owner.get_Parent()).TransformPoint(center);
	// Rectangle2D bounds = GetBounds(owner, Transform.Identity);
	// Point2D pointd2 = GeometryUtil.GetCenter(bounds);
	// empty = new Point2D((center.get_X() - pointd2.get_X()) /
	// bounds.get_Width(), (center.get_Y() - pointd2.get_Y()) /
	// bounds.get_Height());
	// }
	// if((item == null) || ((item.get_Links().get_Count() > 1) &&
	// ((item.get_Distance() != 0f) || (item.get_CenterOffset() != empty)))){
	// item = null;
	// IEnumerator it1 = owner.get_Anchors().GetEnumerator();
	// while(it1.MoveNext()){
	// Anchor anchor3 = (Anchor)it1.get_Current();
	// if((anchor3 instanceof ShapeAnchor) && (anchor3.get_Links().get_Count()
	// == 0)){
	// item = (ShapeAnchor)anchor3;
	// break;
	// }
	// }
	// if(item == null){
	// item = new ShapeAnchor();
	// if(owner.get_Site() != null){
	// owner.get_Anchors().Add(item);
	// if(owner.get_Site().get_Container() != null){
	// owner.get_Site().get_Container().Add(item);
	// }
	// }
	// else{
	// owner.get_Anchors().Add(item);
	// }
	// }
	// if(start){
	// link.set_StartAnchor(item);
	// }
	// else{
	// link.set_EndAnchor(item);
	// }
	// }
	// item.set_Distance(0f);
	// item.set_CenterOffset(empty);
	//
	// }
	//
	// IEnumerator GetEnumerator()
	// {
	//
	// return new FilteredChildrenEnumerator(this);
	//
	// }
	//
	// private void undoEngine_Undoing(java.lang.Object sender, EventArgs e)
	// {
	// IDesignerHost service =
	// (IDesignerHost)this.grapher.get_Site().GetService(Type.GetType(IDesignerHost.class.getName()));
	// if(service != null){
	// service.TransactionClosing += this.host_TransactionClosing;
	// }
	//
	// }
	//
	public ILOG.Diagrammer.GraphLayout.CoordinatesMode get_CoordinatesMode() {

		return this.coordinatesMode;
	}

	// public void
	// set_CoordinatesMode(ILOG.Diagrammer.GraphLayout.CoordinatesMode value){
	// if(value != this.coordinatesMode){
	// if(((value !=
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.ViewCoordinates) && (value !=
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.GraphicContainerCoordinates))
	// && (value !=
	// ILOG.Diagrammer.GraphLayout.CoordinatesMode.InverseViewCoordinates)){
	// throw(new ArgumentException("Invalid coordinates mode: " + value));
	// }
	// this.coordinatesMode = value;
	// super.FireEvent(null,GraphModelContentsChangedEventAction.GeometryChanged,this.IsUpdating());
	// }
	// }
	//
	// public IGraphLayoutFilter get_Filter(){
	//
	// return this._filter;
	// }
	// public void set_Filter(IGraphLayoutFilter value){
	// this._filter = value;
	// if(this.grapher != null){
	// super.FireEvent(null,GraphModelContentsChangedEventAction.StructureChanged,this.IsUpdating());
	// }
	// }
	//
	// public ILOG.Diagrammer.GraphicContainer get_GraphicContainer(){
	//
	// return this.grapher;
	// }
	//
	// public static String get_GraphLayoutTransactionDescription(){
	//
	// return Resources.get_GraphLayout_GraphLayoutTransactionDescription();
	// }
	//
	// private Boolean get_InAutoLayout(){
	// for( ILOG.Diagrammer.GraphicContainer container = this.grapher;
	// container != null;container = container.get_Parent()){
	// if(container.get_InPaintContent()){
	//
	// return true;
	// }
	// }
	//
	// return false;
	// }
	//
	// public ICollection get_InterGraphLinks(){
	// ArrayList list = new ArrayList();
	// IEnumerator it1 = (IEnumerable)this.GetEnumerator();
	// while(it1.MoveNext()){
	// java.lang.Object obj2 = (java.lang.Object)it1.get_Current();
	//
	// if(this.IsInterGraphLink(obj2)){
	// list.Add(obj2);
	// }
	// }
	//
	// return list;
	// }
	//
	// public ICollection get_Links(){
	// ArrayList list = new ArrayList();
	// IEnumerator it2 = (IEnumerable)this.GetEnumerator();
	// while(it2.MoveNext()){
	// java.lang.Object obj2 = (java.lang.Object)it2.get_Current();
	//
	// if(this.IsLink(obj2)){
	// list.Add(obj2);
	// }
	// }
	//
	// return list;
	// }
	//
	// public IComparer get_NodeComparer(){
	//
	// return this._nodeComparer;
	// }
	// public void set_NodeComparer(IComparer value){
	// if(value != this._nodeComparer){
	// this._nodeComparer = value;
	// super.FireEvent(null,GraphModelContentsChangedEventAction.StructureChanged,this.IsUpdating());
	// }
	// }
	//
	// public ICollection get_Nodes(){
	// ArrayList list = new ArrayList();
	// IEnumerator it3 = (IEnumerable)this.GetEnumerator();
	// while(it3.MoveNext()){
	// java.lang.Object obj2 = (java.lang.Object)it3.get_Current();
	//
	// if(this.IsNode(obj2)){
	// list.Add(obj2);
	// }
	// }
	// if(this._nodeComparer != null){
	// list.Sort(this._nodeComparer);
	// }
	//
	// return list;
	// }
	//
	// public IGraphModel get_Parent(){
	// GraphicContainerAdapter root = (GraphicContainerAdapter)this.get_Root();
	// if((root == null) || (root == this)){
	//
	// return null;
	// }
	// java.lang.Object logicalParent = this.grapher.get_LogicalParent();
	// while(((logicalParent instanceof ILOG.Diagrammer.GraphicContainer) &&
	// (((ILOG.Diagrammer.GraphicContainer)logicalParent).get_LogicalParent() !=
	// null)) &&
	// (GetNestedGraphMode((ILOG.Diagrammer.GraphicContainer)logicalParent) ==
	// NestedGraphMode.Transparent)){
	// logicalParent =
	// ((ILOG.Diagrammer.GraphicContainer)logicalParent).get_LogicalParent();
	// }
	// if(logicalParent == null){
	//
	// return null;
	// }
	// if(root.grapher != logicalParent){
	//
	//
	// return root.GetGraphModel(logicalParent);
	// }
	//
	// return root;
	// }
	//
	// public Transform get_ReferenceTransform(){
	// IDiagramView view = this._referenceView;
	// if(view == null){
	// for( ILOG.Diagrammer.GraphicContainer container = this.grapher;
	// container != null;container = container.get_Parent()){
	// IDiagramView referenceView = GetReferenceView(container);
	// if(referenceView != null){
	// view = referenceView;
	// break;
	// }
	// }
	// }
	// if(view != null){
	//
	//
	// return this.grapher.GetLogicalTransformToView(view);
	// }
	//
	// return this._referenceTransform;
	// }
	// public void set_ReferenceTransform(Transform value){
	// if(value != this._referenceTransform){
	// this._referenceTransform = value;
	// super.FireEvent(null,GraphModelContentsChangedEventAction.GeometryChanged,this.IsUpdating());
	// }
	// }
	//
	// public IDiagramView get_ReferenceView(){
	//
	// return this._referenceView;
	// }
	// public void set_ReferenceView(IDiagramView value){
	// if(value != this._referenceView){
	// this._referenceView = value;
	// if(this.ReferenceViewChanged != null){
	// this.ReferenceViewChanged(this, EventArgs.Empty);
	// }
	// super.FireEvent(null,GraphModelContentsChangedEventAction.GeometryChanged,this.IsUpdating());
	// }
	// }
	//
	// public ICollection get_Subgraphs(){
	// ArrayList list = new ArrayList();
	// IEnumerator it4 = (IEnumerable)this.GetEnumerator();
	// while(it4.MoveNext()){
	// java.lang.Object obj2 = (java.lang.Object)it4.get_Current();
	//
	// if(this.IsSubgraph(obj2)){
	// list.Add(obj2);
	// }
	// }
	//
	// return list;
	// }
	//
	// public class FilteredChildrenEnumerator implements IEnumerator
	// {
	// private GraphicContainerAdapter adapter;
	//
	// private java.lang.Object current;
	//
	// private IEnumerator enumerator;
	//
	// private Stack<IEnumerator> stack = new Stack<IEnumerator>();
	//
	// public FilteredChildrenEnumerator (GraphicContainerAdapter adapter){
	// this.adapter = adapter;
	// this.Reset();
	// }
	//
	// public Boolean MoveNext()
	// {
	// Label_0000:
	// while(this.enumerator.MoveNext()){
	// this.current = this.enumerator.get_Current();
	// GraphicContainer current = (GraphicContainer)this.current;
	// if((current == null) ||
	// (GraphicContainerAdapter.GetNestedGraphMode(current) !=
	// NestedGraphMode.Transparent)){
	//
	// return true;
	// }
	// this.stack.Push(this.enumerator);
	//
	// this.enumerator = current.get_LogicalChildren().GetEnumerator();
	// }
	// if(this.stack.get_Count() > 0){
	//
	// this.enumerator = this.stack.Pop();
	// goto Label_0000;
	// }
	// this.current = null;
	//
	// return false;
	//
	// }
	//
	// public void Reset()
	// {
	//
	// this.enumerator =
	// this.adapter.grapher.get_LogicalChildren().GetEnumerator();
	// this.stack = new Stack<IEnumerator>();
	// this.current = null;
	//
	// }
	//
	// public java.lang.Object get_Current(){
	// if(this.current == null){
	// throw(new InvalidOperationException("No current element"));
	// }
	//
	// return this.current;
	// }
	//
	// }
	//
	// public class GroupState extends GraphicContainerAdapter.IObjectState
	// {
	// private Transform transform;
	//
	// public void Restore(GraphicContainer grapher, GraphicObject obj)
	// {
	// obj.set_Transform(this.transform);
	//
	// }
	//
	// public void Save(GraphicContainer grapher, GraphicObject obj)
	// {
	// this.transform = obj.get_Transform();
	//
	// }
	//
	// }
	//
	// public interface IObjectState
	// {
	// void Restore(GraphicContainer grapher, GraphicObject obj);
	//
	// void Save(GraphicContainer grapher, GraphicObject obj);
	//
	// }
	//
	// public class LinkState extends GraphicContainerAdapter.IObjectState
	// {
	// private Boolean canEditOrthogonal = false;
	//
	// private GraphicObject end;
	//
	// private String endAnchorName;
	//
	// private Point2D endAnchorOffset;
	//
	// private AnchorPosition endAnchorPosition;
	//
	// private String endName;
	//
	// private Point2D[] points;
	//
	// private LinkShapeType shapeType;
	//
	// private GraphicObject start;
	//
	// private String startAnchorName;
	//
	// private Point2D startAnchorOffset;
	//
	// private AnchorPosition startAnchorPosition;
	//
	// private String startName;
	//
	// private Transform transform;
	//
	// public void Restore(GraphicContainer grapher, GraphicObject obj)
	// {
	// Link link = (Link)obj;
	// link.set_Transform(this.transform);
	// link.set_ShapeType(this.shapeType);
	// link.set_CanEditOrthogonalShape(this.canEditOrthogonal);
	// if((this.shapeType == LinkShapeType.Free) || ((this.shapeType ==
	// LinkShapeType.Orthogonal) && this.canEditOrthogonal)){
	// link.get_Points().Clear();
	// link.get_Points().AddRange(this.points);
	// }
	// Anchor startAnchor = link.get_StartAnchor();
	// Anchor endAnchor = link.get_EndAnchor();
	// if(((startAnchor == null) ||
	// ((!startAnchor.get_Name().equals(this.startAnchorName)))) ||
	// (startAnchor.get_Site() == null)){
	// if((link.get_Site() != null) && (link.get_Site().get_Container() !=
	// null)){
	// startAnchor =
	// (Anchor)link.get_Site().get_Container().get_Components()[this.startAnchorName];
	// if(startAnchor == null){
	// throw(new system.Exception("Cannot find start anchor " +
	// this.startAnchorName));
	// }
	// if(((this.start == null) ||
	// ((!this.start.get_Name().equals(this.startName)))) ||
	// (this.start.get_Site() == null)){
	// this.start =
	// (GraphicObject)link.get_Site().get_Container().get_Components()[this.startName];
	// if(this.start == null){
	// throw(new system.Exception("Cannot find start node " + this.startName));
	// }
	// }
	// if(((startAnchor != null) && (this.start != null)) &&
	// (startAnchor.get_Owner() != this.start)){
	// this.start.get_Anchors().Add(startAnchor);
	// }
	// link.set_StartAnchor(startAnchor);
	// }
	// }
	// else if(startAnchor instanceof BoundsAnchor){
	// ((BoundsAnchor)startAnchor).set_Position(this.startAnchorPosition);
	// }
	// if(((endAnchor == null) ||
	// ((!endAnchor.get_Name().equals(this.endAnchorName)))) ||
	// (endAnchor.get_Site() == null)){
	// if((link.get_Site() != null) && (link.get_Site().get_Container() !=
	// null)){
	// endAnchor =
	// (Anchor)link.get_Site().get_Container().get_Components()[this.endAnchorName];
	// if(endAnchor == null){
	// throw(new system.Exception("Cannot find end anchor " +
	// this.endAnchorName));
	// }
	// if(((this.end == null) || ((!this.end.get_Name().equals(this.endName))))
	// || (this.end.get_Site() == null)){
	// this.end =
	// (GraphicObject)link.get_Site().get_Container().get_Components()[this.endName];
	// if(this.end == null){
	// throw(new system.Exception("Cannot find end node " + this.endName));
	// }
	// }
	// if(((endAnchor != null) && (this.end != null)) && (endAnchor.get_Owner()
	// != this.end)){
	// this.end.get_Anchors().Add(endAnchor);
	// }
	// link.set_EndAnchor(endAnchor);
	// }
	// }
	// else if(endAnchor instanceof BoundsAnchor){
	// ((BoundsAnchor)endAnchor).set_Position(this.endAnchorPosition);
	// }
	// if((startAnchor != null) && startAnchor.CanBeMoved()){
	// if(startAnchor instanceof BoundsAnchor){
	// ((BoundsAnchor)startAnchor).set_Offset(this.startAnchorOffset);
	// }
	// else if(this.points.length >= 2){
	// Point2D point = this.points[0];
	// Point2D pointd2 = this.points[1];
	// Transform transform = link.get_Transform();
	// if(!transform.get_IsIdentity()){
	//
	// point = transform.TransformPoint(point);
	//
	// pointd2 = transform.TransformPoint(pointd2);
	// }
	// startAnchor.MoveTo(point,pointd2,link.get_Parent());
	// }
	// }
	// if((endAnchor != null) && endAnchor.CanBeMoved()){
	// if(endAnchor instanceof BoundsAnchor){
	// ((BoundsAnchor)endAnchor).set_Offset(this.endAnchorOffset);
	// }
	// else if(this.points.length >= 2){
	// Point2D pointd3 = this.points[this.points.length - 1];
	// Point2D pointd4 = this.points[this.points.length - 2];
	// Transform transform2 = link.get_Transform();
	// if(!transform2.get_IsIdentity()){
	//
	// pointd3 = transform2.TransformPoint(pointd3);
	//
	// pointd4 = transform2.TransformPoint(pointd4);
	// }
	// endAnchor.MoveTo(pointd3,pointd4,link.get_Parent());
	// }
	// }
	//
	// }
	//
	// public void Save(GraphicContainer grapher, GraphicObject obj)
	// {
	// Link link = (Link)obj;
	// this.transform = link.get_Transform();
	// this.shapeType = link.get_ShapeType();
	// this.canEditOrthogonal = link.get_CanEditOrthogonalShape();
	//
	// this.points = link.get_Points().ToArray();
	// this.start = link.get_Start();
	// this.end = link.get_End();
	// this.startName = this.start.get_Name();
	// this.endName = this.end.get_Name();
	// if(link.get_StartAnchor() != null){
	// this.startAnchorName = link.get_StartAnchor().get_Name();
	// if(link.get_StartAnchor() instanceof BoundsAnchor){
	// this.startAnchorPosition =
	// ((BoundsAnchor)link.get_StartAnchor()).get_Position();
	//
	// if(link.get_StartAnchor().CanBeMoved()){
	// this.startAnchorOffset =
	// ((BoundsAnchor)link.get_StartAnchor()).get_Offset();
	// }
	// }
	// }
	// if(link.get_EndAnchor() != null){
	// this.endAnchorName = link.get_EndAnchor().get_Name();
	// if(link.get_EndAnchor() instanceof BoundsAnchor){
	// this.endAnchorPosition =
	// ((BoundsAnchor)link.get_EndAnchor()).get_Position();
	//
	// if(link.get_EndAnchor().CanBeMoved()){
	// this.endAnchorOffset = ((BoundsAnchor)link.get_EndAnchor()).get_Offset();
	// }
	// }
	// }
	//
	// }
	//
	// }
	//
	// public class NodeState extends GraphicContainerAdapter.IObjectState
	// {
	// private Rectangle2D bounds;
	//
	// private Transform transform;
	//
	// public void Restore(GraphicContainer grapher, GraphicObject obj)
	// {
	// obj.set_Transform(this.transform);
	// obj.set_GeometryBounds(this.bounds);
	//
	// }
	//
	// public void Save(GraphicContainer grapher, GraphicObject obj)
	// {
	// this.bounds = obj.get_GeometryBounds();
	// this.transform = obj.get_Transform();
	//
	// }
	//
	// }

	public GraphicContainer get_GraphicContainer() {
		return this.container;
	}

	public void SetOriginatingLayout(GraphLayout graphLayout) {
		// TODO Auto-generated method stub

	}

	public IDiagramView get_ReferenceView() {
		return null;
	}
}
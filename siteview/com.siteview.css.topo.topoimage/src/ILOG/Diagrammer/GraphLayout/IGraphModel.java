package ILOG.Diagrammer.GraphLayout;

import java.util.ArrayList;

import system.Collections.ICollection;
import ILOG.Diagrammer.Point2D;
import ILOG.Diagrammer.Rectangle2D;

public interface IGraphModel {
	/* TODO: Event Declare */
	ArrayList<GraphModelContentsChangedEventHandler> ContentsChanged = new ArrayList<GraphModelContentsChangedEventHandler>();

	void AfterLayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout);

	void BeforeLayout(ILOG.Diagrammer.GraphLayout.GraphLayout layout);

	Rectangle2D BoundingBox(java.lang.Object nodeOrLink);

	void Dispose();

	java.lang.Object GetFrom(java.lang.Object link);

	IGraphModel GetGraphModel(java.lang.Object subgraph);

	Point2D[] GetLinkPoints(java.lang.Object link);

	ICollection GetLinksFrom(java.lang.Object node);

	ICollection GetLinksTo(java.lang.Object node);

	float GetLinkWidth(java.lang.Object link);

	java.lang.Object GetProperty(String key);

	java.lang.Object GetProperty(java.lang.Object nodeOrLink, String key);

	java.lang.Object GetTo(java.lang.Object link);

	Boolean HasMoveableConnectionPoint(java.lang.Object link, Boolean origin);

	Boolean HasPinnedConnectionPoint(java.lang.Object link, Boolean origin);

	Boolean IsDisposed();

	Boolean IsInterGraphLink(java.lang.Object obj);

	Boolean IsLayoutNeeded(ILOG.Diagrammer.GraphLayout.GraphLayout layout);

	Boolean IsLink(java.lang.Object obj);

	Boolean IsNode(java.lang.Object obj);

	Boolean IsSubgraph(java.lang.Object obj);

	void MoveNode(java.lang.Object node, float x, float y);

	void OnAttach(ILOG.Diagrammer.GraphLayout.GraphLayout layout);

	void OnDetach(ILOG.Diagrammer.GraphLayout.GraphLayout layout);

	void ReshapeLink(java.lang.Object link, int style, Point2D fromPoint,
			int fromPointMode, Point2D[] points, Integer startIndex,
			Integer length, Point2D toPoint, int toPointMode);

	void SetProperty(String key, java.lang.Object val);

	void SetProperty(java.lang.Object nodeOrLink, String key,
			java.lang.Object val);

	ICollection get_InterGraphLinks();

	ICollection get_Links();

	ICollection get_Nodes();

	IGraphModel get_Parent();

	IGraphModel get_Root();

	ICollection get_Subgraphs();

}
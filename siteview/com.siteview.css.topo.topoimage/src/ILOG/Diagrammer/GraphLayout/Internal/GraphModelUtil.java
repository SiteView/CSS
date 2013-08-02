package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.*;
import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.Util.*;
import system.*;
import system.Collections.*;

public class GraphModelUtil {
	public static void AfterLayout(IGraphModel model,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout, Boolean redraw) {
		model.AfterLayout(layout);
		layout.AddToModelEventHandlers(model);

	}

	public static void ApplyLayout(IGraphModel model,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout, Boolean redraw) {
		layout.Layout();

	}

	public static void BeforeLayout(IGraphModel model,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout, Boolean redraw) {
		layout.RemoveFromModelEventHandlers(model);
		model.BeforeLayout(layout);

	}

	public static InternalRect BoundingBox(IGraphModel model) {
		Rectangle2D rectangled = BoundingBoxAsRectangle2D(model);

		return new InternalRect(rectangled.get_X(), rectangled.get_Y(),
				rectangled.get_Width(), rectangled.get_Height());

	}

	public static InternalRect BoundingBox(IGraphModel model,
			java.lang.Object nodeOrLink) {
		Rectangle2D rectangled = model.BoundingBox(nodeOrLink);

		return new InternalRect(rectangled.get_X(), rectangled.get_Y(),
				rectangled.get_Width(), rectangled.get_Height());

	}

	public static Rectangle2D BoundingBoxAsRectangle2D(IGraphModel model) {
		Rectangle2D rectangled = new Rectangle2D(0f, 0f, 0f, 0f);
		Boolean flag = true;
		IEnumerator it1 = model.get_Nodes().GetEnumerator();
		while (it1.MoveNext()) {
			java.lang.Object obj2 = (java.lang.Object) it1.get_Current();
			if (flag) {

				rectangled = model.BoundingBox(obj2);
			} else {
				Rectangle2D[] __rect1_1 = new Rectangle2D[] { rectangled };
				TranslateUtil.AddRect(__rect1_1, model.BoundingBox(obj2));
				rectangled = __rect1_1[0];
			}
			flag = false;
		}
		IEnumerator it3 = model.get_Links().GetEnumerator();
		while (it3.MoveNext()) {
			java.lang.Object obj3 = (java.lang.Object) it3.get_Current();
			if (flag) {

				rectangled = model.BoundingBox(obj3);
			} else {
				Rectangle2D[] __rect1_3 = new Rectangle2D[] { rectangled };
				TranslateUtil.AddRect(__rect1_3, model.BoundingBox(obj3));
				rectangled = __rect1_3[0];
			}
			flag = false;
		}

		return rectangled;

	}

	public static void DeleteIntermediatePointsOnLinks(IGraphModel graphModel,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {

		if (layout.SupportsPreserveFixedLinks()) {
			Boolean preserveFixedLinks = layout.get_PreserveFixedLinks();
			IEnumerator it1 = graphModel.get_Links().GetEnumerator();
			while (it1.MoveNext()) {
				java.lang.Object obj2 = (java.lang.Object) it1.get_Current();

				if (!preserveFixedLinks || !layout.GetFixed(obj2)) {
					ReshapeLink(graphModel, layout, obj2, 1, null, null, 0, 0,
							null);
				}
			}
		} else {
			IEnumerator it2 = graphModel.get_Links().GetEnumerator();
			while (it2.MoveNext()) {
				java.lang.Object obj3 = (java.lang.Object) it2.get_Current();
				ReshapeLink(graphModel, layout, obj3, 1, null, null, 0, 0, null);
			}
		}

	}

	public static InternalPoint[] GetLinkPoints(IGraphModel model,
			java.lang.Object link) {

		return InternalPoint.GetInternalPoints(model.GetLinkPoints(link));

	}

	public static ICollection GetLinks(IGraphModel model, java.lang.Object node) {
		ArrayList list = new ArrayList();
		list.AddRange(model.GetLinksFrom(node));
		list.AddRange(model.GetLinksTo(node));

		return list;

	}

	public static ICollection GetNeighbors(IGraphModel model,
			java.lang.Object node) {
		ArrayList list = new ArrayList();
		java.lang.Object obj2 = null;
		IEnumerator it1 = model.GetLinksFrom(node).GetEnumerator();
		while (it1.MoveNext()) {
			java.lang.Object obj3 = (java.lang.Object) it1.get_Current();

			obj2 = GetOpposite(model, obj3, node);
			if ((obj2 != null) && (obj2 != node)) {
				list.Add(obj2);
			}
		}
		IEnumerator it2 = model.GetLinksTo(node).GetEnumerator();
		while (it2.MoveNext()) {
			java.lang.Object obj4 = (java.lang.Object) it2.get_Current();

			obj2 = GetOpposite(model, obj4, node);
			if ((obj2 != null) && (obj2 != node)) {
				list.Add(obj2);
			}
		}

		return list;

	}

	public static Integer GetNodeDegree(IGraphModel model, java.lang.Object node) {
		HashSet set = new HashSet();
		java.lang.Object v = null;
		IEnumerator it1 = model.GetLinksFrom(node).GetEnumerator();
		while (it1.MoveNext()) {
			java.lang.Object obj3 = (java.lang.Object) it1.get_Current();

			v = GetOpposite(model, obj3, node);
			if (v != node) {
				set.Add(v);
			}
		}
		IEnumerator it2 = model.GetLinksTo(node).GetEnumerator();
		while (it2.MoveNext()) {
			java.lang.Object obj4 = (java.lang.Object) it2.get_Current();

			v = GetOpposite(model, obj4, node);
			if (v != node) {
				set.Add(v);
			}
		}

		return set.get_Count();

	}

	public static ICollection GetNodesAndLinks(IGraphModel model) {
		ArrayList list = new ArrayList();
		list.AddRange(model.get_Nodes());
		list.AddRange(model.get_Links());

		return list;

	}

	public static java.lang.Object GetOpposite(IGraphModel model,
			java.lang.Object link, java.lang.Object node) {
		if (node == model.GetFrom(link)) {

			return model.GetTo(link);
		}
		if (node != model.GetTo(link)) {
			throw (new ArgumentException(
					"node is not the origin node, nor the destination node"));
		}

		return model.GetFrom(link);

	}

	public static InternalPoint GetPointAt(IGraphModel model,
			java.lang.Object link, Integer index) {

		return new InternalPoint(model.GetLinkPoints(link)[index]);

	}

	public static Boolean IsLinkBetween(IGraphModel model,
			java.lang.Object node1, java.lang.Object node2) {
		IEnumerator it1 = model.GetLinksFrom(node1).GetEnumerator();
		while (it1.MoveNext()) {
			java.lang.Object obj2 = (java.lang.Object) it1.get_Current();
			if (model.GetTo(obj2) == node2) {

				return true;
			}
		}
		IEnumerator it2 = model.GetLinksFrom(node2).GetEnumerator();
		while (it2.MoveNext()) {
			java.lang.Object obj3 = (java.lang.Object) it2.get_Current();
			if (model.GetTo(obj3) == node1) {

				return true;
			}
		}

		return false;

	}

	public static void Move(IGraphModel model, float x, float y) {
		Rectangle2D rectangled = BoundingBoxAsRectangle2D(model);
		float num = x - rectangled.get_X();
		float num2 = y - rectangled.get_Y();
		if ((num != 0f) || (num2 != 0f)) {
			ICollection nodes = model.get_Nodes();
			if (nodes.get_Count() != 0) {
				IEnumerator it1 = nodes.GetEnumerator();
				while (it1.MoveNext()) {
					java.lang.Object obj2 = (java.lang.Object) it1
							.get_Current();
					Rectangle2D rectangled2 = model.BoundingBox(obj2);
					model.MoveNode(obj2, rectangled2.get_X() + num,
							rectangled2.get_Y() + num2);
				}
				IEnumerator it2 = model.get_Links().GetEnumerator();
				while (it2.MoveNext()) {
					java.lang.Object obj3 = (java.lang.Object) it2
							.get_Current();
					Point2D[] linkPoints = model.GetLinkPoints(obj3);
					if (linkPoints != null) {
						Integer index = linkPoints.length - 1;
						if (index > 1) {
							for (Integer i = 0; i <= index; i++) {
								linkPoints[i]
										.set_X(linkPoints[i].get_X() + num);
								linkPoints[i].set_Y(linkPoints[i].get_Y()
										+ num2);
							}
							model.ReshapeLink(obj3, ReshapeLinkStyle.Ignore,
									linkPoints[0], ReshapeLinkMode.Ignore,
									linkPoints, 1, index - 1,
									linkPoints[index], ReshapeLinkMode.Ignore);
						}
					}
				}
			}
		}

	}

	public static Integer PerformLayout(IGraphModel model,
			ILayoutProvider layoutProvider, Boolean force, Boolean redraw,
			Boolean traverse) {

		return PerformLayout(model, layoutProvider, null, force, redraw,
				traverse);

	}

	public static Integer PerformLayout(IGraphModel model,
			ILayoutProvider layoutProvider,
			ILOG.Diagrammer.GraphLayout.GraphLayout recursiveLayout,
			Boolean force, Boolean redraw, Boolean traverse) {

		return 0;

	}

	public static void ReshapeLink(IGraphModel model,
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, Integer linkStyle, InternalPoint fromPoint,
			InternalPoint[] points, Integer startIndex, Integer length,
			InternalPoint toPoint) {
		int fix = 0;
		int mode2 = 0;
		int orthogonal = 0;
		Boolean linkClipping = layout.get_LinkClipping();
		Point2D pointd = (fromPoint != null) ? new Point2D(fromPoint.X,
				fromPoint.Y) : Point2D.Empty;
		Point2D pointd2 = (toPoint != null) ? new Point2D(toPoint.X, toPoint.Y)
				: Point2D.Empty;
		Point2D[] pointdArray = new Point2D[length];
		InternalPoint point = null;
		for (Integer i = startIndex; i < length; i++) {
			point = points[i];
			pointdArray[i] = new Point2D(point.X, point.Y);
		}
		if (fromPoint instanceof FixedPoint) {
			fix = ReshapeLinkMode.Fix;
		} else if (fromPoint != null) {
			fix = linkClipping ? ReshapeLinkMode.MoveAndClip
					: ReshapeLinkMode.Move;
		} else {
			fix = linkClipping ? ReshapeLinkMode.Clip : ReshapeLinkMode.Ignore;
		}
		if (toPoint instanceof FixedPoint) {
			mode2 = ReshapeLinkMode.Fix;
		} else if (toPoint != null) {
			mode2 = linkClipping ? ReshapeLinkMode.MoveAndClip
					: ReshapeLinkMode.Move;
		} else {
			mode2 = linkClipping ? ReshapeLinkMode.Clip
					: ReshapeLinkMode.Ignore;
		}
		if (linkStyle == 2 || linkStyle == 5 || linkStyle == 6) {
			orthogonal = ReshapeLinkStyle.Orthogonal;
			// NOTICE: break ignore!!!
		} else if (linkStyle == 3) {
			orthogonal = ReshapeLinkStyle.Polyline;
			// NOTICE: break ignore!!!
		} else if (linkStyle == 4) {
			orthogonal = ReshapeLinkStyle.Direct;
			// NOTICE: break ignore!!!
		} else {
			orthogonal = ReshapeLinkStyle.Straight;
			// NOTICE: break ignore!!!
		}
		model.ReshapeLink(link, orthogonal, pointd, fix, pointdArray,
				startIndex, length, pointd2, mode2);

	}

	public static void SetContentsAdjusting(IGraphModel model,
			Boolean adjusting, Boolean withParents) {

	}

}
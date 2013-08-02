package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.*;
import ILOG.Diagrammer.GraphLayout.*;
import system.*;
import system.Collections.*;

public class TranslateUtil {
	private static long _currentTimeBase = 0L;

	private static Integer _lastTime = 0;

	private static ICollection _void = null;

	public float NEAR_ZERO_FLOAT = 1E-20f;

	public static void AddRect(
	/* TODO: REF PARAMETER, Wrapper it to array[0] */Rectangle2D[] rect1,
			Rectangle2D rect2) {
		float x = rect1[0].get_X();
		float y = rect2.get_X();
		float num3 = (x < y) ? x : y;
		x += rect1[0].get_Width();
		y += rect2.get_Width();
		float num5 = (x > y) ? x : y;
		x = rect1[0].get_Y();
		y = rect2.get_Y();
		float num4 = (x < y) ? x : y;
		x += rect1[0].get_Height();
		y += rect2.get_Height();
		float num6 = (x > y) ? x : y;
		rect1[0].set_X(num3);
		rect1[0].set_Y(num4);
		rect1[0].set_Width(num5 - num3);
		rect1[0].set_Height(num6 - num4);

	}

	public static IJavaStyleEnumerator Collection2JavaStyleEnum(ICollection coll) {

		return new JavaStyleEnumerator(coll);

	}

	public static long CurrentTimeMillis() {
		Integer tickCount = Environment.get_TickCount();
		if (tickCount < _lastTime) {
			_currentTimeBase += (long) 0xffffffffL;
		}
		_lastTime = tickCount;

		return (_currentTimeBase + tickCount);

	}

	public static IJavaStyleEnumerator Enumerator2JavaStyleEnum(
			IEnumerator enumerator) {

		return new JavaStyleEnumerator(enumerator);

	}

	public static InternalRect GetBox(INodeBoxProvider boxProvider,
			IGraphModel graphModel, java.lang.Object node) {
		Rectangle2D box = boxProvider.GetBox(graphModel, node);

		return new InternalRect(box.get_X(), box.get_Y(), box.get_Width(),
				box.get_Height());

	}

	public static ICollection GetEmptyCollection() {

		return _void;

	}

	public static Integer GetPenalty(ILongLinkTerminationPointFilter filter,
			IGraphModel graphModel, java.lang.Object link, Boolean origin,
			java.lang.Object node, InternalPoint point, int side,
			Integer proposedPenalty) {

		return filter.GetPenalty(graphModel, link, origin, node, new Point2D(
				point.X, point.Y), side, proposedPenalty);

	}

	// public static float GetTangentialOffset(ILinkConnectionBoxProvider
	// provider, IGraphModel graphModel, java.lang.Object node, Direction
	// nodeSide)
	// {
	//
	//
	// return provider.GetTangentialOffset(graphModel,node,(NodeSide)nodeSide);
	//
	// }

	public static float GetTangentialOffset(
			ILinkConnectionBoxProvider provider, IGraphModel graphModel,
			java.lang.Object node, int nodeSide) {

		return provider.GetTangentialOffset(graphModel, node, nodeSide);

	}

	public static float GetTangentialOffset(
			ILinkConnectionBoxProvider provider, IGraphModel graphModel,
			java.lang.Object node, Integer nodeSide) {

		return provider.GetTangentialOffset(graphModel, node, (int) nodeSide);

	}

	public static Point2D InternalPoint2Point2D(InternalPoint point) {
		if (point == null) {

			return Point2D.Invalid;
		}

		return new Point2D(point.X, point.Y);

	}

	public static Rectangle2D InternalRect2Rectangle2D(InternalRect rect) {
		if (rect == null) {

			return Rectangle2D.Invalid;
		}

		return new Rectangle2D(rect.X, rect.Y, rect.Width, rect.Height);

	}

	public static Boolean IntersectsWith(/*
										 * TODO: REF PARAMETER, Wrapper it to
										 * array[0]
										 */Rectangle2D[] rect1,
			Rectangle2D rect2) {

		return ((((rect1[0].get_Width() > 0f) && (rect1[0].get_Height() > 0f)) && ((rect2
				.get_Width() >= 0f) && (rect2.get_Height() >= 0f))) && rect1[0]
				.IntersectsWith(rect2));

	}

	public static ICollection JavaStyleEnum2Collection(IJavaStyleEnumerator ijse) {
		if (ijse == null) {

			return null;
		}
		if (ijse instanceof JavaStyleEnumerator) {
			JavaStyleEnumerator enumerator = (JavaStyleEnumerator) ijse;

			if (enumerator.IsCreatedFromCollection()) {

				return enumerator.GetCollection();
			}
		}
		ArrayList list = new ArrayList();

		while (ijse.HasMoreElements()) {
			list.Add(ijse.NextElement());
		}

		return list;

	}

	public static void Noop() {

	}

	public static InternalPoint Point2D2InternalPoint(Point2D point) {

		if (point.equals(Point2D.Invalid)) {

			return null;
		}

		return new InternalPoint(point);

	}

	public static InternalRect Rectangle2D2InternalRect(Rectangle2D rect) {

		if (rect.equals(Rectangle2D.Invalid)) {

			return null;
		}

		return new InternalRect(rect);

	}

	public static Boolean Remove(ArrayList list, java.lang.Object obj) {
		if (list == null) {

			return false;
		}
		Integer count = list.get_Count();
		list.Remove(obj);

		return (list.get_Count() != count);

	}

	public static void TemporaryNoop() {

	}

	// public static Array ToArray(ICollection c, java.lang.Object[] a)
	// {
	// if(c.get_Count() > a.length){
	// java.lang.Object[] __array_0 = new java.lang.Object[]{a};
	// Array.Resize(__array_0,c.get_Count());
	// a = __array_0[0];
	// }
	// c.CopyTo(a,0);
	//
	// return a;
	//
	// }

}
package ILOG.Diagrammer.GraphLayout.Internal;

import system.ArgumentException;
import system.Math;
import system.Collections.ArrayList;
import ILOG.Diagrammer.GraphLayout.IGraphModel;

public final class LayoutUtil {

	private float FLOAT_MAX_VALUE = Float.MAX_VALUE;

	private float FLOAT_NEGATIVE_INFINITY = Float.NEGATIVE_INFINITY;

	private float FLOAT_POSITIVE_INFINITY = Float.POSITIVE_INFINITY;

	public static String MUST_BE_CALLED_AFTER_LAYOUT = "you must call this method after the layout is performed on the current graph";

	public static String NO_ATTACHED_GRAPH_MODEL_OR_NULL = "no attached graph model or graph model is null";

	public static String NODE_NOT_IN_GRAPHER = ("node must be a node in the attached " + GetText(
			"grapher", "graphic container"));

	public static String NODE_REJECTED_BY_FILTER = "node must be a node which is accepted by the filtering mechanism";

	public static String ONLY_FOR_GRAPHER_METHOD = ("This method can be called only if the attached graph is " + GetText(
			"an IlvGrapher or IlvGrapherAdapter",
			"a GraphicContainer or GraphicContainerAdapter"));

	private double PI = 3.1415926535897931;

	private double TWO_PI = 6.2831853071795862;

	private static IJavaStyleEnumerator _voidEnum = new IlvVoidEnumeration();

	private static void AddRect(InternalRect rect1, InternalRect rect2) {
		double x = rect1.X;
		double y = rect2.X;
		double num3 = (x < y) ? x : y;
		x += rect1.Width;
		y += rect2.Width;
		double num5 = (x > y) ? x : y;
		x = rect1.Y;
		y = rect2.Y;
		double num4 = (x < y) ? x : y;
		x += rect1.Height;
		y += rect2.Height;
		double num6 = (x > y) ? x : y;
		rect1.X = (float) num3;
		rect1.Y = (float) num4;
		rect1.Width = (float) (num5 - num3);
		rect1.Height = (float) (num6 - num4);

	}

	public static double Angle(float startX, float startY, float endX,
			float endY) {
		double num4 = 0;
		float num = endX - startX;
		float num2 = endY - startY;
		if ((num == 0f) && (num2 == 0f)) {

			return 0.0;
		}
		if ((num >= 0f) && (num2 >= 0f)) {
			num4 = 0.0;
		} else if ((num < 0f) && (num2 < 0f)) {
			num4 = 3.1415926535897931;
		} else {
			float num3 = 0;
			if (num < 0f) {
				num3 = num;
				num = num2;
				num2 = -num3;
				num4 = 1.5707963267948966;
			} else {
				num3 = num;
				num = -num2;
				num2 = num3;
				num4 = 4.71238898038469;
			}
		}
		if (num == 0f) {
			num4 += 1.5707963267948966;
		} else if (num2 == 0f) {
			num4 += 0.0;
		} else if (num < num2) {

			num4 += Math.Atan(((double) num2) / ((double) num));
		} else {
			num4 += 1.5707963267948966 - Math.Atan(((double) num)
					/ ((double) num2));
		}
		CorrectAngle(num4);

		return num4;

	}

	public static Boolean Between(double a, double b, double t) {

		return (((a <= t) && (b >= t)) || ((a >= t) && (b <= t)));

	}

	public static InternalRect BoundingBox(IGraphModel graphModel,
			IJavaStyleEnumerator nodesAndLinks, InternalRect bbox) {
		bbox.Reshape(0f, 0f, 0f, 0f);

		if (nodesAndLinks.HasMoreElements()) {
			java.lang.Object nodeOrLink = nodesAndLinks.NextElement();
			InternalRect rect = GraphModelUtil.BoundingBox(graphModel,
					nodeOrLink);
			bbox.Reshape(rect.X, rect.Y, rect.Width, rect.Height);

			while (nodesAndLinks.HasMoreElements()) {
				AddRect(bbox,
						GraphModelUtil.BoundingBox(graphModel,
								nodesAndLinks.NextElement()));
			}
		}

		return bbox;

	}

	public static void Clip(double[] firstPoint, double[] secondPoint,
			double[] rect) {
		double t = RayAtX(firstPoint, secondPoint, rect[0]);
		if ((t != Double.MAX_VALUE) && Between(rect[2], rect[3], t)) {
			firstPoint[0] = rect[0];
			firstPoint[1] = t;
		} else {

			t = RayAtX(firstPoint, secondPoint, rect[1]);
			if ((t != Double.MAX_VALUE) && Between(rect[2], rect[3], t)) {
				firstPoint[0] = rect[1];
				firstPoint[1] = t;
			} else {

				t = RayAtY(firstPoint, secondPoint, rect[2]);
				if ((t != Double.MAX_VALUE) && Between(rect[0], rect[1], t)) {
					firstPoint[0] = t;
					firstPoint[1] = rect[2];
				} else {

					t = RayAtY(firstPoint, secondPoint, rect[3]);
					if ((t != Double.MAX_VALUE) && Between(rect[0], rect[1], t)) {
						firstPoint[0] = t;
						firstPoint[1] = rect[3];
					}
				}
			}
		}

	}

	public static void Clip(InternalPoint firstPoint,
			InternalPoint secondPoint, InternalRect rect) {
		double[] numArray = new double[2];
		double[] numArray2 = new double[2];
		double[] numArray3 = new double[4];
		numArray[0] = firstPoint.X;
		numArray[1] = firstPoint.Y;
		numArray2[0] = secondPoint.X;
		numArray2[1] = secondPoint.Y;
		numArray3[0] = rect.X;
		numArray3[1] = rect.X + rect.Width;
		numArray3[2] = rect.Y;
		numArray3[3] = rect.Y + rect.Height;
		Clip(numArray, numArray2, numArray3);
		firstPoint.X = (float) numArray[0];
		firstPoint.Y = (float) numArray[1];

	}

	public static double CorrectAngle(double angle) {
		if (angle < 0.0) {
			do {
				angle += 6.2831853071795862;
			} while (angle < 0.0);

			return angle;
		}
		if (angle > 6.2831853071795862) {
			do {
				angle -= 6.2831853071795862;
			} while (angle > 6.2831853071795862);
		}

		return angle;

	}

	public static java.lang.Object ElementAt(ArrayList vect, Integer index,
			Integer length, Boolean reversedOrder) {
		if (!reversedOrder) {

			return vect.get_Item(index);
		}

		return vect.get_Item((length - index) - 1);

	}

	public static double GetDeltaAlpha(double alpha1, double alpha2) {
		double num = alpha1 - alpha2;
		if (num < 0.0) {
			num = -num;
		}
		double num2 = 6.2831853071795862 - num;
		double num3 = (num < num2) ? num : num2;
		if (num3 <= 0.0) {

			return -num3;
		}

		return num3;

	}

	public static float GetDiagonal(java.lang.Object obj, IGraphModel graphModel) {
		InternalRect rect = GraphModelUtil.BoundingBox(graphModel, obj);

		return (float) Math
				.Sqrt((double) ((rect.Width * rect.Width) + (rect.Height * rect.Height)));

	}

	public static double GetDistance(double deltaX, double deltaY) {

		return Math.Sqrt((deltaX * deltaX) + (deltaY * deltaY));

	}

	public static Integer GetEnumerationCount(IJavaStyleEnumerator e) {
		if (e == null) {

			return 0;
		}
		Integer num = 0;

		while (e.HasMoreElements()) {
			e.NextElement();
			num++;
		}

		return num;

	}

	public static double GetEqualSidesTriangleAngle(float ab, float bc) {
		if (ab == 0f) {

			return 0.0;
		}
		if (bc > (2f * ab)) {

			return 0.0;
		}

		return (2.0 * Math.Asin((double) (bc / (2f * ab))));

	}

	public static InternalRect GetEstimatedLayoutRegion(IGraphModel graphModel) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Nodes());

		if ((enumerator == null) || !enumerator.HasMoreElements()) {

			return null;
		}
		Integer num = 0;
		double num2 = 0.0;
		double num3 = 0.0;

		while (enumerator.HasMoreElements()) {
			InternalRect rect = GraphModelUtil.BoundingBox(graphModel,
					enumerator.NextElement());
			num2 += rect.Width;
			num3 += rect.Height;
			num++;
		}
		if (num == 0) {

			return null;
		}
		double num4 = 2.0 / Math.Sqrt((double) num);
		float width = (float) (num2 * num4);
		float height = (float) (num3 * num4);
		float x = 0f;

		return new InternalRect(x, 0f, width, height);

	}

	public static double GetOrientedDeltaAlpha(double end, double start) {
		if (((start < 0.0) || (end < 0.0))
				|| ((start > 6.2831853071795862) || (end > 6.2831853071795862))) {
			throw (new ArgumentException("angle must be from zero to 2pi"));
		}
		if (start > end) {

			return ((6.2831853071795862 - start) + end);
		}

		return (end - start);

	}

	public static double GetOrientedDeltaAlpha1(double end, double start) {
		double num = (start <= end) ? (end - start)
				: ((6.2831853071795862 - start) + end);
		if (num < 0.0) {
			num = -num;
		}
		if (num >= 3.1415926535897931) {

			return 3.1415926535897931;
		}

		return num;

	}

	public static String GetText(String javaString, String dotNetString) {

		return dotNetString;

	}

	public static Integer GetTripletOrientation(float p1x, float p1y,
			float p2x, float p2y, float p3x, float p3y) {
		float num = (((((p1x * p2y) - (p2x * p1y)) + (p3x * p1y)) - (p1x * p3y)) + (p2x * p3y))
				- (p3x * p2y);
		if (num > 0f) {

			return -1;
		}
		if (num < 0f) {

			return 1;
		}

		return 0;

	}

	// public static float GetValueInManagerCoordinates(float val, Transform
	// transformer)
	// {
	// if(transformer.get_IsIdentity()){
	//
	// return val;
	// }
	// double scaleX = transformer.get_ScaleX();
	// if(scaleX == 0.0){
	// throw(new
	// system.Exception("Zoom factor is not allowed to be zero. Transformer on reference view: "
	// + transformer));
	// }
	//
	// return (float)(((double)val) / scaleX);
	//
	// }

	public static IJavaStyleEnumerator GetVoidEnumeration() {

		return _voidEnum;

	}

	public static Boolean Intersects(InternalPoint from1, InternalPoint to1,
			InternalPoint from2, InternalPoint to2) {

		return Intersects(from1.X, from1.Y, to1.X, to1.Y, from2.X, from2.Y,
				to2.X, to2.Y);

	}

	public static Boolean Intersects(float from1x, float from1y, float to1x,
			float to1y, float from2x, float from2y, float to2x, float to2y) {
		Integer num = GetTripletOrientation(from1x, from1y, to1x, to1y, from2x,
				from2y);
		Integer num2 = GetTripletOrientation(from1x, from1y, to1x, to1y, to2x,
				to2y);
		Integer num3 = GetTripletOrientation(from2x, from2y, to2x, to2y,
				from1x, from1y);
		Integer num4 = GetTripletOrientation(from2x, from2y, to2x, to2y, to1x,
				to1y);
		if (((num * num2) == -1) && ((num3 * num4) == -1)) {

			return true;
		}
		if ((((num != 0) || (num2 != 0)) || ((num3 != 0) || (num4 != 0)))
				|| (((from1x <= ((from2x < to2x) ? from2x : to2x)) || (from1x >= ((from2x > to2x) ? from2x
						: to2x))) && ((from2x <= ((from1x < to1x) ? from1x
						: to1x)) || (from2x >= ((from1x > to1x) ? from1x : to1x))))) {

			return false;
		}

		return (((from1y > ((from2y < to2y) ? from2y : to2y)) && (from1y < ((from2y > to2y) ? from2y
				: to2y))) || ((from2y > ((from1y < to1y) ? from1y : to1y)) && (from2y < ((from1y > to1y) ? from1y
				: to1y))));

	}

	public static Boolean IntersectsOrthogonal(InternalPoint from1,
			InternalPoint to1, InternalPoint from2, InternalPoint to2) {

		return IntersectsOrthogonal(from1.X, from1.Y, to1.X, to1.Y, from2.X,
				from2.Y, to2.X, to2.Y);

	}

	public static Boolean IntersectsOrthogonal(float from1x, float from1y,
			float to1x, float to1y, float from2x, float from2y, float to2x,
			float to2y) {
		Boolean flag = from1y == to1y;
		Boolean flag2 = from2y == to2y;
		if (flag) {
			if ((((!flag2 && (((from2y <= to2y) ? from2y : to2y) < from1y)) && (((from2y >= to2y) ? from2y
					: to2y) > from1y)) && (((from1x <= to1x) ? from1x : to1x) < from2x))
					&& (((from1x >= to1x) ? from1x : to1x) > from2x)) {

				return true;
			}
			if (from1y == from2y) {

				return OverlappingCoords(from1x, to1x, from2x, to2x, 0f, 0f);
			}
		} else {
			if ((((flag2 && (((from2x <= to2x) ? from2x : to2x) < from1x)) && (((from2x >= to2x) ? from2x
					: to2x) > from1x)) && (((from1y <= to1y) ? from1y : to1y) < from2y))
					&& (((from1y >= to1y) ? from1y : to1y) > from2y)) {

				return true;
			}
			if (from1x == from2x) {

				return OverlappingCoords(from1y, to1y, from2y, to2y, 0f, 0f);
			}
		}

		return false;

	}

	public static Boolean IsEmpty(IGraphModel graphModel) {
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Nodes());
		if (enumerator != null) {

			return !enumerator.HasMoreElements();
		}

		return true;

	}

	public static Boolean IsValidNumber(float x, float y) {

		return ((((((x < Float.MAX_VALUE) && (-x < Float.MAX_VALUE)) && ((y < Float.MAX_VALUE) && (-y < Float.MAX_VALUE))) && (((x != Float.POSITIVE_INFINITY) && (x != Float.NEGATIVE_INFINITY)) && ((y != Float.POSITIVE_INFINITY) && (y != Float.NEGATIVE_INFINITY)))) && !Float
				.isNaN(x)) && !Float.isNaN(y));

	}

	public static Boolean OverlappingCoords(float coord1, float coord2,
			float coord3, float coord4, float halfWidth1, float halfWidth2) {
		float num = 0;
		float num2 = 0;
		float num3 = 0;
		float num4 = 0;
		if (coord1 < coord2) {
			num = coord1;
			num2 = coord2;
		} else {
			num = coord2;
			num2 = coord1;
		}
		if (halfWidth1 != 0f) {
			num -= halfWidth1;
			num2 += halfWidth1;
		}
		if (coord3 < coord4) {
			num3 = coord3;
			num4 = coord4;
		} else {
			num3 = coord4;
			num4 = coord3;
		}
		if (halfWidth2 != 0f) {
			num3 -= halfWidth2;
			num4 += halfWidth2;
		}
		if ((((num < num3) || (num >= num4)) && ((num2 <= num3) || (num2 >= num4)))
				&& ((num3 < num) || (num3 >= num2))) {

			return ((num4 > num) && (num4 < num2));
		}

		return true;

	}

	public static double RayAtX(double[] firstPoint, double[] secondPoint,
			double x) {
		if (firstPoint[0] == x) {

			return firstPoint[1];
		}
		if (secondPoint[0] == x) {

			return secondPoint[1];
		}

		if (SameSide(firstPoint[0], secondPoint[0], x)) {
			double num = firstPoint[1] - secondPoint[1];
			double num2 = firstPoint[0] - secondPoint[0];

			return (((num * (x - firstPoint[0])) / num2) + firstPoint[1]);
		}

		return Double.MAX_VALUE;

	}

	public static double RayAtY(double[] firstPoint, double[] secondPoint,
			double y) {
		if (firstPoint[1] == y) {

			return firstPoint[0];
		}
		if (secondPoint[1] == y) {

			return secondPoint[0];
		}

		if (SameSide(firstPoint[1], secondPoint[1], y)) {
			double num = firstPoint[0] - secondPoint[0];
			double num2 = firstPoint[1] - secondPoint[1];

			return (((num * (y - firstPoint[1])) / num2) + firstPoint[0]);
		}

		return Double.MAX_VALUE;

	}

	public static Boolean SameSide(double a, double t1, double t2) {

		return (((a < t1) && (a < t2)) || ((a > t1) && (a > t2)));

	}

	public final class IlvDoubleEnumerator implements IJavaStyleEnumerator {
		private IJavaStyleEnumerator enum1;

		private IJavaStyleEnumerator enum2;

		public IlvDoubleEnumerator(IJavaStyleEnumerator v1,
				IJavaStyleEnumerator v2) {
			if ((v1 == null) || (v2 == null)) {
				throw (new ArgumentException("null enumeration"));
			}
			this.enum1 = v1;
			this.enum2 = v2;
		}

		public Boolean HasMoreElements() {

			if (!this.enum1.HasMoreElements()) {

				return this.enum2.HasMoreElements();
			}

			return true;

		}

		public java.lang.Object NextElement() {

			if (this.enum1.HasMoreElements()) {

				return this.enum1.NextElement();
			}

			return this.enum2.NextElement();

		}

	}

	public final class IlvDoubleIlvUnsyncVectorEnumerator implements
			IJavaStyleEnumerator {
		private Integer _count;

		private ArrayList _vector1;

		private ArrayList _vector2;

		public IlvDoubleIlvUnsyncVectorEnumerator(ArrayList v1, ArrayList v2) {
			this._vector1 = v1;
			this._vector2 = v2;
			this._count = 0;
		}

		public Boolean HasMoreElements() {
			if ((this._vector1 == null) && (this._vector2 == null)) {

				return false;
			}
			if (this._vector1 == null) {

				return (this._count < this._vector2.get_Count());
			}
			if (this._vector2 == null) {

				return (this._count < this._vector1.get_Count());
			}

			return (this._count < (this._vector1.get_Count() + this._vector2
					.get_Count()));

		}

		public java.lang.Object NextElement() {
			Integer count = 0;
			Integer num2 = 0;
			if (this._vector1 != null) {
				count = this._vector1.get_Count();
			}
			if (this._vector2 != null) {
				num2 = this._vector2.get_Count();
			}
			if (count > 0) {
				if (this._count < count) {

					return this._vector1.get_Item(this._count++);
				}
				if (num2 > 0) {
					if ((this._count - count) >= num2) {
						throw (new system.Exception(
								"IlvDoubleIlvUnsyncVectorEnumerator"));
					}

					return this._vector2.get_Item(this._count++ - count);
				}
			} else if ((num2 > 0) && (this._count < num2)) {

				return this._vector2.get_Item(this._count++);
			}
			throw (new system.Exception("IlvDoubleIlvUnsyncVectorEnumerator"));

		}

	}

	public final class IlvDoubleVectorEnumerator implements
			IJavaStyleEnumerator {
		private Integer _count;

		private ArrayList _vector1;

		private ArrayList _vector2;

		public IlvDoubleVectorEnumerator(ArrayList v1, ArrayList v2) {
			this._vector1 = v1;
			this._vector2 = v2;
			this._count = 0;
		}

		public Boolean HasMoreElements() {
			if ((this._vector1 == null) && (this._vector2 == null)) {

				return false;
			}
			if (this._vector1 == null) {

				return (this._count < this._vector2.get_Count());
			}
			if (this._vector2 == null) {

				return (this._count < this._vector1.get_Count());
			}

			return (this._count < (this._vector1.get_Count() + this._vector2
					.get_Count()));

		}

		public java.lang.Object NextElement() {
			Integer count = 0;
			Integer num2 = 0;
			if (this._vector1 != null) {
				count = this._vector1.get_Count();
			}
			if (this._vector2 != null) {
				num2 = this._vector2.get_Count();
			}
			if (count > 0) {
				if (this._count < count) {

					return this._vector1.get_Item(this._count++);
				}
				if (num2 > 0) {
					if ((this._count - count) >= num2) {
						throw (new system.Exception("IlvDoubleVectorEnumerator"));
					}

					return this._vector2.get_Item(this._count++ - count);
				}
			} else if ((num2 > 0) && (this._count < num2)) {

				return this._vector2.get_Item(this._count++);
			}
			throw (new system.Exception("IlvDoubleVectorEnumerator"));

		}

	}

	public final class IlvModelNeighborsEnumeration implements
			IJavaStyleEnumerator {
		private IGraphModel _graphModel;

		private IJavaStyleEnumerator _links;

		private java.lang.Object _next;

		private java.lang.Object _node;

		public IlvModelNeighborsEnumeration(IGraphModel graphModel,
				java.lang.Object node) {
			if (node == null) {
				throw (new ArgumentException("node cannot be null"));
			}
			this._node = node;
			this._graphModel = graphModel;

			this._links = TranslateUtil.Collection2JavaStyleEnum(GraphModelUtil
					.GetLinks(graphModel, node));
		}

		public Boolean HasMoreElements() {
			if (this._next != null) {

				return true;
			}
			if (this._links != null) {

				while (this._links.HasMoreElements()) {
					java.lang.Object link = this._links.NextElement();
					if (link != null) {

						this._next = GraphModelUtil.GetOpposite(
								this._graphModel, link, this._node);
						if ((this._next != null) && (this._next != this._node)) {

							return true;
						}
					}
				}
				this._next = null;
			}

			return false;

		}

		public java.lang.Object NextElement() {

			if (!this.HasMoreElements()) {
				throw (new system.Exception("no next element"));
			}
			java.lang.Object obj2 = this._next;
			this._next = null;

			return obj2;

		}

	}

	private static class IlvVoidEnumeration implements IJavaStyleEnumerator {
		public Boolean HasMoreElements() {

			return false;

		}

		public java.lang.Object NextElement() {
			throw (new system.Exception("IlvVoidEnumeration"));

		}

	}
}
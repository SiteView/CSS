package ILOG.Diagrammer.GraphLayout;

import system.ArgumentException;
import system.Math;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Collections.Stack;
import ILOG.Diagrammer.GraphLayout.Internal.ConnectedComponentAdapter;
import ILOG.Diagrammer.GraphLayout.Internal.ConnectedComponentCollectionAdapter;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelDFS;
import ILOG.Diagrammer.GraphLayout.Internal.GraphModelUtil;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.InternalPoint;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public final class GraphLayoutUtil {
	private static String CONNECTED_COMP_FINDER_PROP = "__GLayoutUtilConnCompFinder";

	private static String TEST_CONNECTED_PROP = "__GLayoutUtilCountNodesConnComp";

	private static String TEST_TREE_PROP = "__GLayoutUtilTestTree";

	private GraphLayoutUtil() {
	}

	public static Integer ComputeNumberOfLinkBends(IGraphModel graphModel) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		Integer num = 0;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Links());

		while (enumerator.HasMoreElements()) {
			java.lang.Object link = enumerator.NextElement();
			num += graphModel.GetLinkPoints(link).length - 2;
		}

		return num;

	}

	public static Integer ComputeNumberOfLinkCrossings(IGraphModel graphModel) {
		java.lang.Object obj2 = null;
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		Integer num = 0;
		Integer count = graphModel.get_Links().get_Count();
		java.lang.Object[] objArray = new java.lang.Object[count];
		InternalRect[] rectArray = new InternalRect[count];
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Links());
		Integer index = 0;

		while (enumerator.HasMoreElements()) {

			obj2 = enumerator.NextElement();
			objArray[index] = obj2;

			rectArray[index++] = GraphModelUtil.BoundingBox(graphModel, obj2);
		}
		for (index = 0; index < count; index++) {
			obj2 = objArray[index];
			InternalRect rect = rectArray[index];
			InternalPoint[] pointArray = null;
			Integer num7 = 0;
			for (Integer i = index; i < count; i++) {
				java.lang.Object link = objArray[i];
				InternalRect rect2 = rectArray[i];
				if (((rect.X <= (rect2.X + rect2.Width)) && (rect2.X <= (rect.X + rect.Width)))
						&& ((rect.Y <= (rect2.Y + rect2.Height)) && (rect2.Y <= (rect.Y + rect.Height)))) {
					if (pointArray == null) {

						pointArray = GraphModelUtil.GetLinkPoints(graphModel,
								obj2);
						num7 = pointArray.length;
					}
					InternalPoint[] linkPoints = GraphModelUtil.GetLinkPoints(
							graphModel, link);
					Integer length = linkPoints.length;
					for (Integer j = 0; j < (num7 - 1); j++) {
						for (Integer k = 0; k < (length - 1); k++) {

							if (((((index != i) || (k > (j + 1))) && (SegmentsCross(
									pointArray[j], pointArray[j + 1],
									linkPoints[k], linkPoints[k + 1], true) && (((j != 0) || (k != 0)) || !pointArray[j]
									.equals(linkPoints[k])))) && ((((j != 0) || (k != (length - 2))) || !pointArray[j]
									.equals(linkPoints[k + 1])) && (((j != (num7 - 2)) || (k != 0)) || !pointArray[j + 1]
									.equals(linkPoints[k]))))
									&& (((j != (num7 - 2)) || (k != (length - 2))) || !pointArray[j + 1]
											.equals(linkPoints[k + 1]))) {
								num++;
							}
						}
					}
				}
			}
		}
		objArray = null;
		rectArray = null;

		return num;

	}

	public static Integer ComputeNumberOfLinkOverlaps(IGraphModel graphModel) {
		java.lang.Object obj2 = null;
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		Integer num = 0;
		Integer count = graphModel.get_Links().get_Count();
		java.lang.Object[] objArray = new java.lang.Object[count];
		InternalRect[] rectArray = new InternalRect[count];
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Links());
		Integer index = 0;

		while (enumerator.HasMoreElements()) {

			obj2 = enumerator.NextElement();
			objArray[index] = obj2;

			rectArray[index++] = GraphModelUtil.BoundingBox(graphModel, obj2);
		}
		InternalPoint point = new InternalPoint(0f, 0f);
		for (index = 0; index < count; index++) {
			obj2 = objArray[index];
			InternalRect rect = rectArray[index];
			InternalPoint[] pointArray = null;
			Integer num7 = 0;
			float num9 = 0f;
			for (Integer i = index; i < count; i++) {
				java.lang.Object link = objArray[i];
				InternalRect rect2 = rectArray[i];
				if (((rect.X <= (rect2.X + rect2.Width)) && (rect2.X <= (rect.X + rect.Width)))
						&& ((rect.Y <= (rect2.Y + rect2.Height)) && (rect2.Y <= (rect.Y + rect.Height)))) {
					if (pointArray == null) {

						pointArray = GraphModelUtil.GetLinkPoints(graphModel,
								obj2);
						num7 = pointArray.length;

						num9 = graphModel.GetLinkWidth(obj2);
					}
					InternalPoint[] linkPoints = GraphModelUtil.GetLinkPoints(
							graphModel, link);
					Integer length = linkPoints.length;
					float linkWidth = graphModel.GetLinkWidth(link);
					for (Integer j = 0; j < (num7 - 1); j++) {

						if ((index != i)
								|| !pointArray[j].equals(pointArray[j + 1])) {
							for (Integer k = 0; k < (length - 1); k++) {

								if (((index != i) || ((k > j) && !linkPoints[k]
										.equals(linkPoints[k + 1])))
										&& SegmentsOverlap(pointArray[j],
												pointArray[j + 1], num9,
												linkPoints[k],
												linkPoints[k + 1], linkWidth)) {
									if (index == i) {
										Boolean flag = true;
										for (Integer m = j + 1; m < k; m++) {
											if (Math.Sqrt((double) (((pointArray[j + 1].X - pointArray[m + 1].X) * (pointArray[j + 1].X - pointArray[m + 1].X)) + ((pointArray[j + 1].Y - pointArray[m + 1].Y) * (pointArray[j + 1].Y - pointArray[m + 1].Y)))) > num9) {
												num++;
												flag = false;
												break;
											}
										}
										if (flag) {
											point.X = 0.5f * (linkPoints[k].X + linkPoints[k + 1].X);
											point.Y = 0.5f * (linkPoints[k].Y + linkPoints[k + 1].Y);

											if (SegmentsOverlap(pointArray[j],
													pointArray[j + 1], num9,
													point, linkPoints[k + 1],
													linkWidth)) {
												num++;
											} else {
												point.X = 0.5f * (pointArray[j].X + pointArray[j + 1].X);
												point.Y = 0.5f * (pointArray[j].Y + pointArray[j + 1].Y);

												if (SegmentsOverlap(
														pointArray[j], point,
														num9, linkPoints[k],
														linkPoints[k + 1],
														linkWidth)) {
													num++;
												}
											}
										}
									} else {
										num++;
									}
								}
							}
						}
					}
				}
			}
		}
		objArray = null;
		rectArray = null;

		return num;

	}

	public static Integer ComputeNumberOfNodeLinkOverlaps(IGraphModel graphModel) {
		java.lang.Object obj2 = null;
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		Integer num = 0;
		Integer count = graphModel.get_Links().get_Count();
		java.lang.Object[] objArray = new java.lang.Object[count];
		InternalRect[] rectArray = new InternalRect[count];
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Links());
		Integer index = 0;

		while (enumerator.HasMoreElements()) {

			obj2 = enumerator.NextElement();
			objArray[index] = obj2;

			rectArray[index++] = GraphModelUtil.BoundingBox(graphModel, obj2);
		}
		InternalPoint[] pointArray2 = new InternalPoint[] {
				new InternalPoint(0f, 0f), new InternalPoint(0f, 0f),
				new InternalPoint(0f, 0f), new InternalPoint(0f, 0f),
				new InternalPoint(0f, 0f) };

		enumerator = TranslateUtil.Collection2JavaStyleEnum(graphModel
				.get_Nodes());

		while (enumerator.HasMoreElements()) {
			java.lang.Object nodeOrLink = enumerator.NextElement();
			InternalRect rect = GraphModelUtil.BoundingBox(graphModel,
					nodeOrLink);
			pointArray2[0].X = rect.X;
			pointArray2[0].Y = rect.Y;
			pointArray2[1].X = rect.X + rect.Width;
			pointArray2[1].Y = rect.Y;
			pointArray2[2].X = rect.X + rect.Width;
			pointArray2[2].Y = rect.Y + rect.Height;
			pointArray2[3].X = rect.X;
			pointArray2[3].Y = rect.Y + rect.Height;
			pointArray2[4].X = rect.X;
			pointArray2[4].Y = rect.Y;
			for (index = 0; index < count; index++) {
				obj2 = objArray[index];
				InternalRect rect2 = rectArray[index];
				if ((((graphModel.GetFrom(obj2) != nodeOrLink) && (graphModel
						.GetTo(obj2) != nodeOrLink)) && ((rect.X <= (rect2.X + rect2.Width)) && (rect2.X <= (rect.X + rect.Width))))
						&& ((rect.Y <= (rect2.Y + rect2.Height)) && (rect2.Y <= (rect.Y + rect.Height)))) {
					InternalPoint[] linkPoints = GraphModelUtil.GetLinkPoints(
							graphModel, obj2);
					Integer length = linkPoints.length;
					for (Integer i = 0; i < 4; i++) {
						for (Integer j = 0; j < (length - 1); j++) {

							if (SegmentsCross(pointArray2[i],
									pointArray2[i + 1], linkPoints[j],
									linkPoints[j + 1], true)) {
								num++;
								i = 5;
								break;
							}
						}
					}
				}
			}
		}
		pointArray2 = null;
		objArray = null;
		rectArray = null;

		return num;

	}

	public static Integer ComputeNumberOfNodeOverlaps(IGraphModel graphModel) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		Integer num = 0;
		Integer count = graphModel.get_Nodes().get_Count();
		java.lang.Object[] objArray = new java.lang.Object[count];
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Nodes());
		Integer index = 0;

		while (enumerator.HasMoreElements()) {

			objArray[index++] = enumerator.NextElement();
		}
		for (index = 0; index < count; index++) {
			java.lang.Object nodeOrLink = objArray[index];
			InternalRect rect = GraphModelUtil.BoundingBox(graphModel,
					nodeOrLink);
			for (Integer i = index + 1; i < count; i++) {
				java.lang.Object obj3 = objArray[i];
				InternalRect rect2 = GraphModelUtil.BoundingBox(graphModel,
						obj3);
				if (((rect.X <= (rect2.X + rect2.Width)) && (rect2.X <= (rect.X + rect.Width)))
						&& ((rect.Y <= (rect2.Y + rect2.Height)) && (rect2.Y <= (rect.Y + rect.Height)))) {
					num++;
				}
			}
		}
		objArray = null;

		return num;

	}

	public static IGraphModel GetConnectedComponents(IGraphModel graphModel) {

		return GetConnectedComponents(graphModel, false);

	}

	public static IGraphModel GetConnectedComponents(IGraphModel graphModel,
			Boolean isConnectivityTestDone) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		if (!isConnectivityTestDone && IsConnected(graphModel)) {

			return graphModel;
		}
		ConnectedComponentsFinder finder = new ConnectedComponentsFinder(
				graphModel, CONNECTED_COMP_FINDER_PROP);

		return finder.GetConnectedComponents();

	}

	public static IEnumerator GetLayouts(IGraphModel model,
			ILayoutProvider layoutProvider, Boolean preOrder) {
		if (layoutProvider == null) {
			throw (new ArgumentException("layoutProvider cannot be null"));
		}
		if (preOrder) {

			return new LayoutsPreorderEnumerator(model, layoutProvider);
		}

		return new LayoutsPostorderEnumerator(model, layoutProvider);

	}

	public static Boolean IsConnected(IGraphModel graphModel) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(graphModel.get_Nodes());

		if (!enumerator.HasMoreElements()) {

			return true;
		}

		if (!TranslateUtil.Collection2JavaStyleEnum(graphModel.get_Links())
				.HasMoreElements()) {
			enumerator.NextElement();

			return !enumerator.HasMoreElements();
		}
		Integer num = new CountNodesInConnectedComponent(graphModel,
				TEST_CONNECTED_PROP).CountNodes(enumerator.NextElement());
		try {
			for (Integer i = 1; i < num; i++) {
				enumerator.NextElement();
			}
		} catch (system.Exception e) {
			throw (new system.Exception(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] {
									"Internal error during connectivity test: there are ",
									num,
									" nodes in a connected component and only ",
									graphModel.get_Nodes().get_Count(),
									" nodes in the graph model ", graphModel })));
		}

		return !enumerator.HasMoreElements();

	}

	public static Boolean IsTree(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object startNode) {
		IGraphModel graphModel = layout.GetGraphModel();
		if (graphModel == null) {
			throw (new system.Exception("no attached graph"));
		}

		return IsTree(graphModel, startNode);

	}

	public static Boolean IsTree(IGraphModel graphModel,
			java.lang.Object startNode) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		if (startNode == null) {
			throw (new ArgumentException("startNode cannot be null"));
		}
		TestTree tree = new TestTree(graphModel, TEST_TREE_PROP);

		return tree.Execute(startNode);

	}

	private static Boolean SegmentsCross(InternalPoint p1, InternalPoint p2,
			InternalPoint p3, InternalPoint p4, Boolean excludeEnds) {
		double num = p2.X - p1.X;
		double num2 = p2.Y - p1.Y;
		double num3 = p4.X - p3.X;
		double num4 = p4.Y - p3.Y;
		if ((num == 0.0) && (num2 == 0.0)) {

			return false;
		}
		if ((num3 == 0.0) && (num4 == 0.0)) {

			return false;
		}
		double num5 = num * num4;
		double num6 = num3 * num2;
		if (num5 == num6) {

			return false;
		}
		float num7 = (float) ((((p1.X * num6) - (p3.X * num5)) + (((p3.Y - p1.Y) * num) * num3)) / (num6 - num5));
		float num8 = (float) ((((p1.Y * num5) - (p3.Y * num6)) + (((p3.X - p1.X) * num2) * num4)) / (num5 - num6));
		if (Math.Abs(num) > Math.Abs(num2)) {
			if ((num7 < Math.Min(p1.X, p2.X)) || (Math.Max(p1.X, p2.X) < num7)) {

				return false;
			}
		} else if ((num8 < Math.Min(p1.Y, p2.Y))
				|| (Math.Max(p1.Y, p2.Y) < num8)) {

			return false;
		}
		if (Math.Abs(num3) > Math.Abs(num4)) {
			if ((num7 < Math.Min(p3.X, p4.X)) || (Math.Max(p3.X, p4.X) < num7)) {

				return false;
			}
		} else if ((num8 < Math.Min(p3.Y, p4.Y))
				|| (Math.Max(p3.Y, p4.Y) < num8)) {

			return false;
		}
		if (excludeEnds) {
			if ((num7 == p2.X) && (num8 == p2.Y)) {

				return false;
			}
			if ((num7 == p4.X) && (num8 == p4.Y)) {

				return false;
			}
		}

		return true;

	}

	private static Boolean SegmentsOverlap(InternalPoint p1, InternalPoint p2,
			float width1, InternalPoint p3, InternalPoint p4, float width2) {
		float num7 = 0;
		float num8 = 0;
		double num = p2.X - p1.X;
		double num2 = p2.Y - p1.Y;
		double num3 = p4.X - p3.X;
		double num4 = p4.Y - p3.Y;
		double num5 = num * num4;
		double num6 = num3 * num2;
		if (num5 != num6) {

			return false;
		}
		if (width1 <= 0f) {
			width1 = 0.5f;
		}
		if (width2 <= 0f) {
			width2 = 0.5f;
		}
		if (num2 == 0.0) {
			num7 = 0f;
		} else {
			num7 = (float) ((0.5 * width1) / Math
					.Sqrt((((num / num2) * num) / num2) + 1.0));
		}
		if (num == 0.0) {
			num8 = 0f;
		} else {
			num8 = (float) ((0.5 * width1) / Math
					.Sqrt((((num2 / num) * num2) / num) + 1.0));
		}
		if ((num * num2) > 0.0) {
			num7 = -num7;
		}
		InternalPoint point = new InternalPoint(p1.X + num7, p1.Y + num8);
		InternalPoint point2 = new InternalPoint(p1.X - num7, p1.Y - num8);
		InternalPoint point3 = new InternalPoint(p2.X + num7, p2.Y + num8);
		InternalPoint point4 = new InternalPoint(p2.X - num7, p2.Y - num8);
		if (num4 == 0.0) {
			num7 = 0f;
		} else {
			num7 = (float) ((0.5 * width2) / Math
					.Sqrt((((num3 / num4) * num3) / num4) + 1.0));
		}
		if (num3 == 0.0) {
			num8 = 0f;
		} else {
			num8 = (float) ((0.5 * width2) / Math
					.Sqrt((((num4 / num3) * num4) / num3) + 1.0));
		}
		if ((num3 * num4) > 0.0) {
			num7 = -num7;
		}
		InternalPoint point5 = new InternalPoint(p3.X + num7, p3.Y + num8);
		InternalPoint point6 = new InternalPoint(p3.X - num7, p3.Y - num8);
		InternalPoint point7 = new InternalPoint(p4.X + num7, p4.Y + num8);
		InternalPoint point8 = new InternalPoint(p4.X - num7, p4.Y - num8);

		return (SegmentsCross(point, point2, point5, point7, false) || (SegmentsCross(
				point, point2, point6, point8, false) || (SegmentsCross(point3,
				point4, point5, point7, false) || (SegmentsCross(point3,
				point4, point6, point8, false) || (SegmentsCross(point5,
				point6, point, point3, false) || (SegmentsCross(point5, point6,
				point2, point4, false) || (SegmentsCross(point7, point8, point,
				point3, false) || SegmentsCross(point7, point8, point2, point4,
				false))))))));

	}

	private final static class ConnectedComponentsFinder extends GraphModelDFS {
		private ConnectedComponentAdapter _connectedComp;

		public ConnectedComponentsFinder(IGraphModel graphModel, String propName) {
			super(graphModel, propName);
		}

		public IGraphModel GetConnectedComponents() {
			ConnectedComponentCollectionAdapter adapter = new ConnectedComponentCollectionAdapter();
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(super._graphModel.get_Nodes());

			if ((enumerator == null) || !enumerator.HasMoreElements()) {
				throw (new system.Exception(
						"should not be called for empty graphs"));
			}

			while (enumerator.HasMoreElements()) {
				java.lang.Object node = enumerator.NextElement();

				if (!this.IsMarkedNode(node)) {
					this._connectedComp = new ConnectedComponentAdapter(
							super._graphModel);
					adapter.AddNode(this._connectedComp);
					super.Execute(node, false);
				}
			}
			IJavaStyleEnumerator enumerator2 = TranslateUtil
					.Collection2JavaStyleEnum(adapter.get_Nodes());

			while (enumerator2.HasMoreElements()) {
				IJavaStyleEnumerator enumerator3 = TranslateUtil
						.Collection2JavaStyleEnum(GraphModelUtil
								.GetNodesAndLinks((IGraphModel) enumerator2
										.NextElement()));

				while (enumerator3.HasMoreElements()) {
					super._graphModel.SetProperty(enumerator3.NextElement(),
							super._propName, null);
				}
			}

			return adapter;

		}

		@Override
		public void MarkLink(java.lang.Object link) {
			super.MarkLink(link);
			this._connectedComp.AddLink(link);

		}

		@Override
		public void MarkNode(java.lang.Object node) {
			super.MarkNode(node);
			this._connectedComp.AddNode(node);

		}

	}

	private final static class CountNodesInConnectedComponent extends
			GraphModelDFS {
		private Integer _count;

		public CountNodesInConnectedComponent(IGraphModel graphModel,
				String propName) {
			super(graphModel, propName);
		}

		public Integer CountNodes(java.lang.Object startNode) {
			this._count = 0;
			super.Execute(startNode, true);

			return this._count;

		}

		@Override
		public void MarkNode(java.lang.Object node) {
			super.MarkNode(node);
			this._count++;

		}

	}

	public static class LayoutsPostorderEnumerator implements IEnumerator {
		private ILayoutProvider _layoutProvider;

		private ILOG.Diagrammer.GraphLayout.GraphLayout _nextLayout;

		private Stack _nextStack = new Stack();

		private IGraphModel _rootModel;

		private Stack _siblingEnumStack = new Stack();

		private IGraphModel _startModel;

		public LayoutsPostorderEnumerator(IGraphModel model,
				ILayoutProvider layoutProvider) {
			this._rootModel = (model.get_Root() != null) ? model.get_Root()
					: model;
			this._layoutProvider = layoutProvider;
			this._nextLayout = null;
			this._startModel = model;
			this.PushNext(this._startModel);
		}

		public boolean MoveNext() {
			this.MoveToNextValidLayout();

			return (this._nextLayout != null);

		}

		private void MoveToNextValidLayout() {
			if (this._nextStack.get_Count() > 0) {

				this._nextLayout = this._layoutProvider.GetGraphLayout(this
						.NextModel());
			} else {
				this._nextLayout = null;
			}
			while ((this._nextLayout == null)
					&& (this._nextStack.get_Count() > 0)) {

				this._nextLayout = this._layoutProvider.GetGraphLayout(this
						.NextModel());
			}

		}

		private IGraphModel NextModel() {
			IGraphModel model = (IGraphModel) this._nextStack.Pop();
			if (this._siblingEnumStack.get_Count() > 0) {
				IEnumerator enumerator = (IEnumerator) this._siblingEnumStack
						.Pop();

				if (enumerator.MoveNext()) {
					this._siblingEnumStack.Push(enumerator);
					this.PushNext(this._rootModel.GetGraphModel(enumerator
							.get_Current()));
				}
			}

			return model;

		}

		private void PushNext(IGraphModel model) {
			this._nextStack.Push(model);
			ICollection subgraphs = model.get_Subgraphs();
			if (subgraphs.get_Count() > 0) {
				IEnumerator enumerator = subgraphs.GetEnumerator();
				enumerator.MoveNext();
				this._siblingEnumStack.Push(enumerator);
				this.PushNext(this._rootModel.GetGraphModel(enumerator
						.get_Current()));
			}

		}

		public void Reset() {
			this._siblingEnumStack = new Stack();
			this._nextStack = new Stack();
			this._nextLayout = null;
			this.PushNext(this._startModel);

		}

		public java.lang.Object get_Current() {

			return this._nextLayout;
		}

	}

	public static class LayoutsPreorderEnumerator implements IEnumerator {
		private Stack _childrenEnumStack = new Stack();

		private ILayoutProvider _layoutProvider;

		private ILOG.Diagrammer.GraphLayout.GraphLayout _nextLayout;

		private IGraphModel _nextModel;

		private IGraphModel _rootModel;

		private Boolean _started = false;

		private IGraphModel _startModel;

		public LayoutsPreorderEnumerator(IGraphModel model,
				ILayoutProvider layoutProvider) {
			this._startModel = model;
			this._rootModel = (model.get_Root() != null) ? model.get_Root()
					: model;
			this._layoutProvider = layoutProvider;
			this._nextModel = null;
			this._nextLayout = null;
			this._started = false;
		}

		public boolean MoveNext() {
			this.MoveToNextModel();
			this.MoveToValidLayout();

			return (this._nextLayout != null);

		}

		private void MoveToNextModel() {
			if (!this._started) {
				this._nextModel = this._startModel;
				this._started = true;
			} else {
				ICollection subgraphs = this._nextModel.get_Subgraphs();
				if (subgraphs.get_Count() <= 0) {
					while (this._childrenEnumStack.get_Count() > 0) {
						IEnumerator enumerator2 = (IEnumerator) this._childrenEnumStack
								.Pop();

						if (enumerator2.MoveNext()) {

							this._nextModel = this._rootModel
									.GetGraphModel(enumerator2.get_Current());
							if (this._nextModel == null) {
								this._nextModel = null;
							}
							this._childrenEnumStack.Push(enumerator2);

							return;
						}
					}
					this._nextModel = null;
				} else {
					IEnumerator enumerator = subgraphs.GetEnumerator();
					enumerator.MoveNext();

					this._nextModel = this._rootModel.GetGraphModel(enumerator
							.get_Current());
					if (this._nextModel == null) {
						this._nextModel = null;
					}
					this._childrenEnumStack.Push(enumerator);
				}
			}

		}

		private void MoveToValidLayout() {
			if (this._nextModel != null) {

				this._nextLayout = this._layoutProvider
						.GetGraphLayout(this._nextModel);
				if (this._nextLayout == null) {
					this._nextLayout = null;
				}
			} else {
				this._nextLayout = null;
			}
			while ((this._nextLayout == null) && (this._nextModel != null)) {
				this.MoveToNextModel();
				if (this._nextModel != null) {

					this._nextLayout = this._layoutProvider
							.GetGraphLayout(this._nextModel);
					if (this._nextLayout == null) {
						this._nextLayout = null;
					}
				}
			}

		}

		public void Reset() {
			this._childrenEnumStack = new Stack();
			this._nextModel = null;
			this._nextLayout = null;
			this._started = false;

		}

		public java.lang.Object get_Current() {

			return this._nextLayout;
		}

	}

	private final static class TestTree extends GraphModelDFS {
		private Boolean _done = false;

		private Boolean _isTree = false;

		public TestTree(IGraphModel graphModel, String propName) {
			super(graphModel, propName);
		}

		public Boolean Execute(java.lang.Object startNode) {
			this._isTree = true;
			this._done = false;
			super.Execute(startNode, true);

			return this._isTree;

		}

		@Override
		public Boolean IsDone() {

			return this._done;

		}

		@Override
		public void TraverseBack(java.lang.Object link,
				java.lang.Object fromNode) {
			if (super._graphModel.GetFrom(link) != super._graphModel
					.GetTo(link)) {
				this._isTree = false;
				this._done = true;
			}

		}

	}
}
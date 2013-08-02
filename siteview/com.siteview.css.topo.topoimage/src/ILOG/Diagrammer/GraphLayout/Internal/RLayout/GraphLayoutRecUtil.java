package ILOG.Diagrammer.GraphLayout.Internal.RLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public class GraphLayoutRecUtil {
	private static String REC_PROPERTY = "DuringRecLayout";

	public static int ApplyLayoutToSubgraph(IGraphModel model,
			java.lang.Object subgraph, IGraphModel subGraphModel,
			ILayoutProvider layoutProvider,
			ILOG.Diagrammer.GraphLayout.GraphLayout recursiveLayout,
			IGraphModel rootModel, Boolean force, Boolean redraw,
			Boolean userRedraw) {

		return PerformLayoutInternal(subGraphModel, subgraph, layoutProvider,
				recursiveLayout, rootModel, force, redraw, userRedraw, true);

	}

	public static int PerformLayout(IGraphModel model,
			ILayoutProvider layoutProvider,
			ILOG.Diagrammer.GraphLayout.GraphLayout recursiveLayout,
			Boolean force, Boolean redraw, Boolean traverse) {
		if (layoutProvider == null) {
			throw (new ArgumentException("layoutProvider cannot be null"));
		}
		SetContentsAdjusting(model, true, true);
		int initialCode = GraphLayoutReportCode.InitialCode;
		try {
			IGraphModel rootModel = (model.get_Root() != null) ? model
					.get_Root() : model;

			initialCode = PerformLayoutInternal(model, null, layoutProvider,
					recursiveLayout, rootModel, force, redraw, redraw, traverse);
		} finally {
			SetContentsAdjusting(model, false, true);
		}

		return initialCode;

	}

	public static int PerformLayoutInternal(IGraphModel model,
			java.lang.Object subgraph, ILayoutProvider layoutProvider,
			ILOG.Diagrammer.GraphLayout.GraphLayout recursiveLayout,
			IGraphModel rootModel, Boolean force, Boolean redraw,
			Boolean userRedraw, Boolean traverse) {
		int code4 = 0;
		SetDuringRecursiveLayout(model, true);
		SetContentsAdjusting(model, true, false);
		try {
			int notNeeded = GraphLayoutReportCode.NotNeeded;
			if (traverse) {
				IJavaStyleEnumerator enumerator = TranslateUtil
						.Collection2JavaStyleEnum(model.get_Subgraphs());
				TranslateUtil.Noop();
				try {

					while (enumerator.HasMoreElements()) {
						java.lang.Object obj2 = enumerator.NextElement();
						IGraphModel graphModel = rootModel.GetGraphModel(obj2);
						if (graphModel != null) {
							int code2 = ApplyLayoutToSubgraph(model, obj2,
									graphModel, layoutProvider,
									recursiveLayout, rootModel, force, false,
									userRedraw);
							if (notNeeded < code2) {
								notNeeded = code2;
							}
							if ((notNeeded == GraphLayoutReportCode.StoppedAndInvalid)
									|| (notNeeded == GraphLayoutReportCode.ExceptionDuringLayout)) {

								return notNeeded;
							}
						}
					}
				} finally {
					TranslateUtil.Noop();
				}
			}
			ILOG.Diagrammer.GraphLayout.GraphLayout graphLayout = layoutProvider
					.GetGraphLayout(model);
			if (graphLayout != null) {
				int code3 = 0;
				if (recursiveLayout != null) {

					code3 = recursiveLayout.PerformSublayout(subgraph,
							graphLayout, force);
				} else {

					code3 = graphLayout.PerformLayout(force, false);
				}
				if (graphLayout.IsStoppedImmediately()
						&& (notNeeded < GraphLayoutReportCode.StoppedAndInvalid)) {
					notNeeded = GraphLayoutReportCode.StoppedAndInvalid;
				}
				if (notNeeded < code3) {
					notNeeded = code3;
				}
			}
			code4 = notNeeded;
		} finally {
			SetDuringRecursiveLayout(model, false);
			SetContentsAdjusting(model, false, false);
		}

		return code4;

	}

	public static void SetContentsAdjusting(IGraphModel model,
			Boolean adjusting, Boolean includeParents) {
		if (model instanceof GraphicContainerAdapter) {
			((GraphicContainerAdapter) model).SetContentsAdjusting(adjusting,
					includeParents);
		}

	}

	public static void SetDuringRecursiveLayout(IGraphModel model, Boolean flag) {
		if (flag) {
			if (model.GetProperty(REC_PROPERTY) != null) {
				throw (new GraphLayoutException(
						"A recursive layout has been already started for "
								+ model));
			}
			model.SetProperty(REC_PROPERTY, REC_PROPERTY);
		} else {
			model.SetProperty(REC_PROPERTY, null);
		}

	}

}
package ILOG.Diagrammer.GraphLayout;

import system.ArgumentException;
import ILOG.Diagrammer.GraphLayout.Internal.LayoutUtil;

public final class LayoutParametersUtil {
	public static void CheckInputLink(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String objDescr) {
		CheckInputNodeOrLink(layout, link, objDescr, "The link", "a link",
				false, true);

	}

	public static void CheckInputNode(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object node, String objDescr) {
		CheckInputNodeOrLink(layout, node, objDescr, "The node", "a node",
				true, false);

	}

	public static void CheckInputNodeOrLink(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String objDescr) {
		CheckInputNodeOrLink(layout, nodeOrLink, objDescr, "The object",
				"a node or link", true, true);

	}

	private static void CheckInputNodeOrLink(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String objDescr, String defaultDescr,
			String reqDescr, Boolean checkNode, Boolean checkLink) {
		IGraphModel graphModel = GetGraphModel(layout);
		if (graphModel == null) {
			throw (new system.Exception(
					LayoutUtil.NO_ATTACHED_GRAPH_MODEL_OR_NULL));
		}
		if (nodeOrLink == null) {
			throw (new ArgumentException(((objDescr != null) ? objDescr
					: defaultDescr) + " cannot not be null"));
		}

		if (layout.get_InputCheckEnabled()
				&& ((!checkNode || !graphModel.IsNode(nodeOrLink)) && (!checkLink || (!graphModel
						.IsLink(nodeOrLink) && !graphModel
						.IsInterGraphLink(nodeOrLink))))) {
			throw (new ArgumentException(
					clr.System.StringStaticWrapper
							.Concat(new java.lang.Object[] {
									(objDescr != null) ? objDescr
											: defaultDescr, " ", nodeOrLink,
									" must be ", reqDescr,
									" in the attached grapher ", graphModel })));
		}

	}

	public static IGraphModel GetGraphModel(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		IGraphModel graphModel = layout.GetGraphModel();
		if (graphModel == null) {
			return null;
			// graphModel = new TempGraphicContainerAdapter();
			// layout.Attach(graphModel);
		}

		return graphModel;

	}

	private static IGraphModel GetGraphModel(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String objectDescr, Boolean checkNode,
			Boolean checkLink) {
		if (checkNode && checkLink) {
			CheckInputNodeOrLink(layout, nodeOrLink, objectDescr);
		} else if (checkNode) {
			CheckInputNode(layout, nodeOrLink, objectDescr);
		} else if (checkLink) {
			CheckInputLink(layout, nodeOrLink, objectDescr);
		}
		IGraphModel graphModel = GetGraphModel(layout);
		if (graphModel == null) {
			throw (new system.Exception(
					LayoutUtil.NO_ATTACHED_GRAPH_MODEL_OR_NULL));
		}

		return graphModel;

	}

	public static java.lang.Object GetLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String key) {

		return GetNodeOrLinkProperty(layout, link, key);

	}

	public static Boolean GetLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String key, Boolean defaultValue) {

		return GetNodeOrLinkParameter(layout, link, key, defaultValue);

	}

	public static Integer GetLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String key, Integer defaultValue) {

		return GetNodeOrLinkParameter(layout, link, key, defaultValue);

	}

	public static float GetLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String key, float defaultValue) {

		return GetNodeOrLinkParameter(layout, link, key, defaultValue);

	}

	public static java.lang.Object GetLinkProperty(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String key) {

		return GetNodeOrLinkProperty(layout, link, key);

	}

	public static String GetLinkPropertyName(String propertyName) {

		return (propertyName + "L");

	}

	public static java.lang.Object GetNodeOrLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String key) {

		return GetNodeOrLinkProperty(layout, nodeOrLink, key);

	}

	public static Boolean GetNodeOrLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String key, Boolean defaultValue) {
		java.lang.Object obj2 = GetNodeOrLinkProperty(layout, nodeOrLink, key);
		if (!defaultValue) {

			return (obj2 != null);
		}

		return (obj2 == null);

	}

	public static Integer GetNodeOrLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String key, Integer defaultValue) {
		java.lang.Object obj2 = GetNodeOrLinkProperty(layout, nodeOrLink, key);
		if (obj2 != null) {

			return (Integer) obj2;
		}

		return defaultValue;

	}

	public static float GetNodeOrLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String key, float defaultValue) {
		java.lang.Object obj2 = GetNodeOrLinkProperty(layout, nodeOrLink, key);
		if (obj2 != null) {

			return (Float) obj2;
		}

		return defaultValue;

	}

	public static java.lang.Object GetNodeOrLinkProperty(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String key) {
		IGraphModel graphModel = GetGraphModel(layout);
		if (graphModel == null) {
			throw (new system.Exception(
					LayoutUtil.NO_ATTACHED_GRAPH_MODEL_OR_NULL));
		}

		return layout.GetProperty(graphModel, nodeOrLink, key);

	}

	public static java.lang.Object GetNodeParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String key) {

		return GetNodeOrLinkProperty(layout, nodeOrLink, key);

	}

	public static Boolean GetNodeParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object node, String key, Boolean defaultValue) {

		return GetNodeOrLinkParameter(layout, node, key, defaultValue);

	}

	public static Integer GetNodeParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object node, String key, Integer defaultValue) {

		return GetNodeOrLinkParameter(layout, node, key, defaultValue);

	}

	public static float GetNodeParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object node, String key, float defaultValue) {

		return GetNodeOrLinkParameter(layout, node, key, defaultValue);

	}

	public static java.lang.Object GetNodeProperty(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object node, String key) {

		return GetNodeOrLinkProperty(layout, node, key);

	}

	public static String GetNodePropertyName(String propertyName) {

		return (propertyName + "N");

	}

	private static Boolean Same(java.lang.Object obj1, java.lang.Object obj2) {
		if (obj1 == null) {

			return (obj2 == null);
		}

		return obj1.equals(obj2);

	}

	public static Boolean SetLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String key, java.lang.Object value) {

		return SetNodeOrLinkParameter(layout, link, "The input link", false,
				true, key, value);

	}

	public static Boolean SetLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String key, Boolean value,
			Boolean defaultValue) {

		return SetLinkParameter(layout, link, key,
				(value != defaultValue) ? value : false);

	}

	public static Boolean SetLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String key, Integer value,
			Integer defaultValue) {

		return SetLinkParameter(layout, link, key,
				(value != defaultValue) ? value : 0);

	}

	public static Boolean SetLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String key, float value, float defaultValue) {

		return SetLinkParameter(layout, link, key,
				(value != defaultValue) ? value : 0);

	}

	public static Boolean SetLinkProperty(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object link, String objectDescr, Boolean checkLink,
			String key, java.lang.Object value) {

		return SetNodeOrLinkProperty(layout, link, objectDescr, false,
				checkLink, key, value);

	}

	public static Boolean SetNodeOrLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String key, java.lang.Object value) {

		return SetNodeOrLinkParameter(layout, nodeOrLink,
				"The input node or link", true, true, key, value);

	}

	public static Boolean SetNodeOrLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String key, Boolean value,
			Boolean defaultValue) {

		return SetNodeOrLinkParameter(layout, nodeOrLink, key,
				(value != defaultValue) ? value : false);

	}

	public static Boolean SetNodeOrLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String key, Integer value,
			Integer defaultValue) {

		return SetNodeOrLinkParameter(layout, nodeOrLink, key,
				(value != defaultValue) ? value : 0);

	}

	public static Boolean SetNodeOrLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String key, float value,
			float defaultValue) {

		return SetNodeOrLinkParameter(layout, nodeOrLink, key,
				(value != defaultValue) ? value : 0);

	}

	public static Boolean SetNodeOrLinkParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String objectDescr, Boolean checkNode,
			Boolean checkLink, String key, java.lang.Object value) {
		IGraphModel graphModel = GetGraphModel(layout, nodeOrLink, objectDescr,
				checkNode, checkLink);

		Boolean flag = !Same(
				layout.SetProperty(graphModel, nodeOrLink, key, value), value);
		if (flag) {
			layout.set_ParametersUpToDate(false);
		}

		return flag;

	}

	public static Boolean SetNodeOrLinkProperty(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object nodeOrLink, String objectDescr, Boolean checkNode,
			Boolean checkLink, String key, java.lang.Object value) {
		IGraphModel graphModel = GetGraphModel(layout, nodeOrLink, objectDescr,
				checkNode, checkLink);

		return !Same(layout.SetProperty(graphModel, nodeOrLink, key, value),
				value);

	}

	public static Boolean SetNodeParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object node, String key, java.lang.Object value) {

		return SetNodeOrLinkParameter(layout, node, "The input node", true,
				false, key, value);

	}

	public static Boolean SetNodeParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object node, String key, Boolean value,
			Boolean defaultValue) {

		return SetNodeParameter(layout, node, key,
				(value != defaultValue) ? value : false);

	}

	public static Boolean SetNodeParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object node, String key, Integer value,
			Integer defaultValue) {

		return SetNodeParameter(layout, node, key,
				(value != defaultValue) ? value : 0);

	}

	public static Boolean SetNodeParameter(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object node, String key, float value, float defaultValue) {

		return SetNodeParameter(layout, node, key,
				(value != defaultValue) ? value : 0);

	}

	public static Boolean SetNodeProperty(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout,
			java.lang.Object node, String objectDescr, Boolean checkNode,
			String key, java.lang.Object value) {

		return SetNodeOrLinkProperty(layout, node, objectDescr, checkNode,
				false, key, value);

	}

}
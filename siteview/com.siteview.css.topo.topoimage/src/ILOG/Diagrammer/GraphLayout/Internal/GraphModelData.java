package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;

public class GraphModelData {
	private Boolean _checkOriginatingLayout = true;

	private Boolean _duringRecursiveLayout = false;

	private String GraphModelDataKey = "__GraphModelData";

	private GraphModelData(IGraphModel model) {
		model.SetProperty("__GraphModelData", this);
	}

	public static GraphModelData Get(IGraphModel model) {
		java.lang.Object property = model.GetProperty("__GraphModelData");
		if (property == null) {

			return new GraphModelData(model);
		}

		return (GraphModelData) property;

	}

	public static void SetInternalGraphModelChecking(IGraphModel model,
			Boolean flag) {
		Get(model).set_InternalGraphModelChecking(flag);

	}

	public Boolean get_DuringRecursiveLayout() {

		return this._duringRecursiveLayout;
	}

	public void set_DuringRecursiveLayout(Boolean value) {
		this._duringRecursiveLayout = value;
	}

	public Boolean get_InternalGraphModelChecking() {

		return this._checkOriginatingLayout;
	}

	public void set_InternalGraphModelChecking(Boolean value) {
		this._checkOriginatingLayout = value;
	}

}
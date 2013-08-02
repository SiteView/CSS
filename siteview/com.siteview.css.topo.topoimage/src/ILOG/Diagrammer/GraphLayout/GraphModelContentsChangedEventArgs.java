package ILOG.Diagrammer.GraphLayout;

import system.*;

public class GraphModelContentsChangedEventArgs extends EventArgs {
	private int _action;

	private IGraphModel _model;

	private java.lang.Object _nodeOrLink;

	public GraphModelContentsChangedEventArgs(IGraphModel model, int action,
			java.lang.Object nodeOrLink) {
		this._model = model;
		this._action = action;
		this._nodeOrLink = nodeOrLink;
	}

	public Boolean IsBeginUpdate() {

		return ((this._action & GraphModelContentsChangedEventAction.BeginUpdate) != 0);

	}

	public Boolean IsEndUpdate() {

		return ((this._action & GraphModelContentsChangedEventAction.EndUpdate) != 0);

	}

	public Boolean IsUpdating() {

		if (this.IsEndUpdate()) {

			return false;
		}

		return ((this._action & GraphModelContentsChangedEventAction.Updating) != 0);

	}

	public int get_Action() {

		return this._action;
	}

	public IGraphModel get_Model() {

		return this._model;
	}

	public java.lang.Object get_NodeOrLink() {

		return this._nodeOrLink;
	}

}
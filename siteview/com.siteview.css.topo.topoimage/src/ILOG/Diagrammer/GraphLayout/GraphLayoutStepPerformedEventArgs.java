package ILOG.Diagrammer.GraphLayout;

import system.*;
import system.ComponentModel.*;

public final class GraphLayoutStepPerformedEventArgs extends EventArgs {
	private Boolean _isFinished = false;

	private Boolean _isStarted = false;

	private ILOG.Diagrammer.GraphLayout.GraphLayout _layout;

	public GraphLayoutStepPerformedEventArgs(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		this._layout = layout;
	}

	public void SetLayoutFinished(Boolean finished) {
		this._isFinished = finished;

	}

	public void SetLayoutStarted(Boolean started) {
		this._isStarted = started;

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout get_GraphLayout() {

		return this._layout;
	}

	public Boolean get_LayoutFinished() {

		return this._isFinished;
	}

	public GraphLayoutReport get_LayoutReport() {

		return this.get_GraphLayout().GetLayoutReport();
	}

	public Boolean get_LayoutStarted() {

		return this._isStarted;
	}

}
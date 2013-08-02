package ILOG.Diagrammer.GraphLayout;

import system.*;
import system.ComponentModel.*;

public class RecursiveLayoutReport extends GraphLayoutReport {
	public ILOG.Diagrammer.GraphLayout.GraphLayout _runningLayout;

	public RecursiveLayoutReport() {
	}

	public void SetCurrentlyRunningLayout(
			ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		this._runningLayout = layout;

	}

	public ILOG.Diagrammer.GraphLayout.GraphLayout get_CurrentlyRunningLayout() {

		return this._runningLayout;
	}

}
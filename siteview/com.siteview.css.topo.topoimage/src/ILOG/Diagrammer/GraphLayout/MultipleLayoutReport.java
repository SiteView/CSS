package ILOG.Diagrammer.GraphLayout;

import system.*;
import system.ComponentModel.*;

public class MultipleLayoutReport extends GraphLayoutReport {
	public GraphLayoutReport _firstLayoutReport;

	public GraphLayoutReport _secondLayoutReport;

	public MultipleLayoutReport() {
	}

	public GraphLayoutReport get_FirstGraphLayoutReport() {

		return this._firstLayoutReport;
	}

	public GraphLayoutReport get_SecondGraphLayoutReport() {

		return this._secondLayoutReport;
	}

}
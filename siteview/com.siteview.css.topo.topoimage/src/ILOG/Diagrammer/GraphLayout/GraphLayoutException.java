package ILOG.Diagrammer.GraphLayout;

import system.*;
import system.ComponentModel.*;

public class GraphLayoutException extends system.Exception {
	private GraphLayoutException _next;

	public GraphLayoutException() {
	}

	public GraphLayoutException(String message) {
		super(message);
	}

	public GraphLayoutException get_Next() {

		return this._next;
	}

	public void set_Next(GraphLayoutException value) {
		this._next = value;
	}

}
package ILOG.Diagrammer.GraphLayout;

import system.*;
import system.ComponentModel.*;

public class GraphLayoutReport {
	private int _code;

	private long _endLayoutTime;

	private Integer _percentageComplete;

	private long _startLayoutTime;

	public GraphLayoutReport() {
	}

	public String CodeToString(int code) {
		if (code == GraphLayoutReportCode.InitialCode) {

			return "layout not yet called";
		} else if (code == GraphLayoutReportCode.EmptyGraph) {

			return "layout not done (empty graph)";
		} else if (code == GraphLayoutReportCode.NotNeeded) {

			return "layout not needed";
		} else if (code == GraphLayoutReportCode.LayoutDone) {

			return "layout done";
		} else if (code == GraphLayoutReportCode.StoppedAndValid) {

			return "layout stopped with valid result";
		} else if (code == GraphLayoutReportCode.StoppedAndInvalid) {

			return "layout stopped with invalid result";
		} else if (code == GraphLayoutReportCode.LayoutStarted) {

			return "layout started";
		} else if (code == GraphLayoutReportCode.LayoutFinished) {

			return "layout finished";
		} else if (code == GraphLayoutReportCode.ExceptionDuringLayout) {

			return "exception during layout";
		}

		return ("unknown code " + ((Integer) code));

	}

	public long GetStartLayoutTime() {

		return this._startLayoutTime;

	}

	public void SetEndLayoutTime(long time) {
		this._endLayoutTime = time;

	}

	public void SetPercentageComplete(Integer percentage) {
		this._percentageComplete = percentage;

	}

	public void SetStartLayoutTime(long time) {
		this._startLayoutTime = time;

	}

	public int get_Code() {

		return this._code;
	}

	public void set_Code(int value) {
		this._code = value;
	}

	public long get_LayoutTime() {

		return (this._endLayoutTime - this._startLayoutTime);
	}

	public Integer get_PercentageComplete() {

		return this._percentageComplete;
	}

}
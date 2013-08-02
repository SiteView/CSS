package ILOG.Diagrammer.GraphLayout;

import system.*;

public class LongLinkLayoutReport extends GraphLayoutReport {
	private Integer _numberOfFallbackLinks;

	public LongLinkLayoutReport() {
	}

	public Integer GetNumberOfFallbackLinks() {

		return this._numberOfFallbackLinks;

	}

	public void IncrNumberOfFallbackLinks() {
		this._numberOfFallbackLinks++;

	}

	public void SetNumberOfFallbackLinks(Integer number) {
		this._numberOfFallbackLinks = number;

	}

}
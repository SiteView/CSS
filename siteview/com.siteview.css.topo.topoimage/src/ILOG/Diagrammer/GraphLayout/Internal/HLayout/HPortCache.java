package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import system.*;

public final class HPortCache {
	private HNode _eastPortAuxNode = null;

	private Boolean _isEastWestAuxNode = false;

	private Integer _numEastFromPorts = -1;

	private Integer _numEastToPorts = -1;

	private Integer _numFromPorts = -1;

	private Integer[] _numPorts = new Integer[] { 0, 0, 0, 0 };

	private Integer _numToPorts = -1;

	private Integer _numWestFromPorts = -1;

	private Integer _numWestToPorts = -1;

	private HNode _westPortAuxNode = null;

	public HPortCache() {
	}

	public HNode GetEastPortAuxNode() {

		return this._eastPortAuxNode;

	}

	public Integer GetMaxEastFromPortNumber(HNode node) {
		if (this._numEastFromPorts < 0) {
			this.RecalculateFrom(node);
		}

		return this._numEastFromPorts;

	}

	public Integer GetMaxEastToPortNumber(HNode node) {
		if (this._numEastToPorts < 0) {
			this.RecalculateTo(node);
		}

		return this._numEastToPorts;

	}

	public Integer GetMaxNorthSouthFromPortNumber(HNode node) {
		if (this._numFromPorts < 0) {
			this.RecalculateFrom(node);
		}

		return this._numFromPorts;

	}

	public Integer GetMaxNorthSouthToPortNumber(HNode node) {
		if (this._numToPorts < 0) {
			this.RecalculateTo(node);
		}

		return this._numToPorts;

	}

	public Integer GetMaxWestFromPortNumber(HNode node) {
		if (this._numWestFromPorts < 0) {
			this.RecalculateFrom(node);
		}

		return this._numWestFromPorts;

	}

	public Integer GetMaxWestToPortNumber(HNode node) {
		if (this._numWestToPorts < 0) {
			this.RecalculateTo(node);
		}

		return this._numWestToPorts;

	}

	public Integer GetNumberOfPorts(Integer side) {

		return this._numPorts[side];

	}

	public HNode GetWestPortAuxNode() {

		return this._westPortAuxNode;

	}

	public void InvalidateFrom() {
		this._numFromPorts = -1;
		this._numEastFromPorts = -1;
		this._numWestFromPorts = -1;

	}

	public void InvalidateTo() {
		this._numToPorts = -1;
		this._numEastToPorts = -1;
		this._numWestToPorts = -1;

	}

	public Boolean IsEastWestAuxNode() {

		return this._isEastWestAuxNode;

	}

	public void MarkEastWestAuxNode() {
		this._isEastWestAuxNode = true;

	}

	public void RecalculateFrom(HNode node) {
		this._numFromPorts = 0;
		this._numEastFromPorts = 0;
		this._numWestFromPorts = 0;
		HSegmentIterator segmentsFrom = node.GetSegmentsFrom();

		while (segmentsFrom.HasNext()) {
			HSegment segment = segmentsFrom.Next();
			Integer fromPortSide = segment.GetFromPortSide();
			Integer fromPortNumber = segment.GetFromPortNumber();
			if (fromPortSide == 1) {
				if (fromPortNumber > this._numEastFromPorts) {
					this._numEastFromPorts = fromPortNumber;
				}
				continue;
			} else if (fromPortSide == 3) {
				if (fromPortNumber > this._numWestFromPorts) {
					this._numWestFromPorts = fromPortNumber;
				}
				continue;
			}
			if (fromPortNumber > this._numFromPorts) {
				this._numFromPorts = fromPortNumber;
			}
		}

	}

	public void RecalculateTo(HNode node) {
		this._numToPorts = 0;
		this._numEastToPorts = 0;
		this._numWestToPorts = 0;
		HSegmentIterator segmentsTo = node.GetSegmentsTo();

		while (segmentsTo.HasNext()) {
			HSegment segment = segmentsTo.Next();
			Integer toPortSide = segment.GetToPortSide();
			Integer toPortNumber = segment.GetToPortNumber();
			if (toPortSide == 1) {
				if (toPortNumber > this._numEastToPorts) {
					this._numEastToPorts = toPortNumber;
				}
				continue;
			} else if (toPortSide == 3) {
				if (toPortNumber > this._numWestToPorts) {
					this._numWestToPorts = toPortNumber;
				}
				continue;
			}
			if (toPortNumber > this._numToPorts) {
				this._numToPorts = toPortNumber;
			}
		}

	}

	public void SetEastPortAuxNode(HNode node) {
		this._eastPortAuxNode = node;

	}

	public void SetNumberOfPorts(Integer[] numPorts) {
		if (numPorts != null) {
			this._numPorts[0] = numPorts[0];
			this._numPorts[1] = numPorts[1];
			this._numPorts[2] = numPorts[2];
			this._numPorts[3] = numPorts[3];
		}

	}

	public void SetNumberOfPorts(Integer side, Integer numPorts) {
		this._numPorts[side] = numPorts;

	}

	public void SetWestPortAuxNode(HNode node) {
		this._westPortAuxNode = node;

	}

}
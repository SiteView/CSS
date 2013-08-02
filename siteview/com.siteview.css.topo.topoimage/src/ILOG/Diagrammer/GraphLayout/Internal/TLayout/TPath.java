package ILOG.Diagrammer.GraphLayout.Internal.TLayout;

import system.*;

public final class TPath {
	private float[] _coordCorrection;

	private float[] _endCoord;

	private TPath _last;

	private TPath _next = null;

	private TNode _node;

	private float[] _startCoord;

	private TNode _subtreeRoot;

	public TPath(TNode node, Boolean isLeftOrTop, Integer dir) {
		this._last = this;
		this._startCoord = new float[2];
		this._endCoord = new float[2];
		this._coordCorrection = new float[] { 0f, 0f };
		this._subtreeRoot = null;
		this._node = node;
		this.Update(isLeftOrTop, dir);
	}

	public void Append(TPath path, TPath last) {
		this._last._next = path;
		this._last = last;
		path._last = null;

	}

	public TPath CutPath(TPath cutCell) {
		TPath path = cutCell._next;
		if (path != null) {
			path._last = this._last;
		}
		cutCell._next = null;
		this._last = cutCell;

		return path;

	}

	public void Dispose() {
		if (this._subtreeRoot != null) {
			if (this._subtreeRoot.GetFirstVisibleAtBorder(0, 0) == this) {
				this._subtreeRoot.SetFirstVisibleAtBorder(0, 0, null);
			}
			if (this._subtreeRoot.GetFirstVisibleAtBorder(0, 1) == this) {
				this._subtreeRoot.SetFirstVisibleAtBorder(0, 1, null);
			}
			if (this._subtreeRoot.GetFirstVisibleAtBorder(1, 0) == this) {
				this._subtreeRoot.SetFirstVisibleAtBorder(1, 0, null);
			}
			if (this._subtreeRoot.GetFirstVisibleAtBorder(1, 1) == this) {
				this._subtreeRoot.SetFirstVisibleAtBorder(1, 1, null);
			}
		}
		this._subtreeRoot = null;
		this._startCoord = null;
		this._endCoord = null;
		this._coordCorrection = null;
		this._next = null;
		this._last = null;
		this._node = null;

	}

	public void DisposeNextUntil(TPath lastCell) {
		if (lastCell != this) {
			TPath next = null;
			for (TPath path = this._next; (path != null) && (path != lastCell); path = next) {

				next = path.GetNext();
				path.Dispose();
			}
			this._next = null;
			this._last = this;
		}

	}

	public float GetCoordCorrection(Integer index) {

		return this._coordCorrection[index];

	}

	public float GetEndCoord(Integer index) {

		return this._endCoord[index];

	}

	public TPath GetLast() {

		return this._last;

	}

	public TPath GetNext() {

		return this._next;

	}

	public TNode GetNode() {

		return this._node;

	}

	public float GetStartCoord(Integer index) {

		return this._startCoord[index];

	}

	public TNode GetSubtreeRoot() {

		return this._subtreeRoot;

	}

	public void SetCoordCorrection(Integer index, float coordCorrection) {
		this._coordCorrection[index] = coordCorrection;

	}

	public void SetEndCoord(Integer index, float coord) {
		this._endCoord[index] = coord;

	}

	public void SetStartCoord(Integer index, float coord) {
		this._startCoord[index] = coord;

	}

	public void SetSubtreeRoot(TNode root) {
		this._subtreeRoot = root;

	}

	public void ShiftCoord(Integer index, float dist) {
		this._startCoord[index] += dist;
		this._endCoord[index] += dist;

	}

	public void ShiftCoordCorrection(Integer index, float dist) {
		this._coordCorrection[index] += dist;

	}

	public void Swap(TPath path) {
		TNode node = this._node;

		this._node = path.GetNode();
		path._node = node;
		node = this._subtreeRoot;

		this._subtreeRoot = path.GetSubtreeRoot();
		path._subtreeRoot = node;
		float num = this._startCoord[0];

		this._startCoord[0] = path.GetStartCoord(0);
		path._startCoord[0] = num;
		num = this._startCoord[1];

		this._startCoord[1] = path.GetStartCoord(1);
		path._startCoord[1] = num;
		num = this._endCoord[0];

		this._endCoord[0] = path.GetEndCoord(0);
		path._endCoord[0] = num;
		num = this._endCoord[1];

		this._endCoord[1] = path.GetEndCoord(1);
		path._endCoord[1] = num;
		num = this._coordCorrection[0];

		this._coordCorrection[0] = path.GetCoordCorrection(0);
		path._coordCorrection[0] = num;
		num = this._coordCorrection[1];

		this._coordCorrection[1] = path.GetCoordCorrection(1);
		path._coordCorrection[1] = num;
		TPath path2 = this._next;

		this._next = path.GetNext();
		path._next = path2;
		path2 = this._last;
		if (path.GetLast() == path) {
			this._last = this;
		} else {

			this._last = path.GetLast();
		}
		if (path2 == this) {
			path._last = path;
		} else {
			path._last = path2;
		}

	}

	public void Update(Boolean isLeftOrTop, Integer dir) {
		if (isLeftOrTop) {

			this._endCoord[dir] = this._node.GetMinCoord(dir);
		} else {

			this._endCoord[dir] = this._node.GetMaxCoord(dir);
		}
		this._startCoord[dir] = this._endCoord[dir];

		this._startCoord[1 - dir] = this._node.GetMinCoord(1 - dir);

		this._endCoord[1 - dir] = this._node.GetMaxCoord(1 - dir);

	}

}
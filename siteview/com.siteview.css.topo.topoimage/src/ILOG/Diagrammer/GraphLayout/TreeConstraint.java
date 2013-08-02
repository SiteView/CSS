package ILOG.Diagrammer.GraphLayout;

import java.util.List;
import java.util.Vector;

import ILOG.Diagrammer.GraphicObject;

public class TreeConstraint implements IGraphLayoutConstraint {
	private List<GraphicObject> _firstSubject = new Vector<GraphicObject>();

	private float _priority;

	private List<GraphicObject> _secondSubject = new Vector<GraphicObject>();

	public void Add(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if ((this._firstSubject.size() == 1)
				&& (this._secondSubject.size() == 1)) {
			((TreeLayout) layout).SetEastWestNeighboring(
					this._firstSubject.get(0), this._secondSubject.get(0));
		} else {
			((IGraphLayoutConstraint) this).Remove(layout);
		}

	}

	public void Remove(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if (this._firstSubject.size() == 1) {
			((TreeLayout) layout).SetEastWestNeighboring(
					this._firstSubject.get(0), null);
			((TreeLayout) layout).SetEastWestNeighboring(null,
					this._firstSubject.get(0));
		}
		if (this._secondSubject.size() == 1) {
			((TreeLayout) layout).SetEastWestNeighboring(
					this._secondSubject.get(0), null);
			((TreeLayout) layout).SetEastWestNeighboring(null,
					this._secondSubject.get(0));
		}

	}

	public Boolean SupportsNodeGroups() {

		return false;

	}

	public List<GraphicObject> get_FirstSubject() {

		return this._firstSubject;
	}

	public float get_Priority() {

		return this._priority;
	}

	public void set_Priority(float value) {
		this._priority = value;
	}

	public List<GraphicObject> get_SecondSubject() {

		return this._secondSubject;
	}

	@Override
	public String[] get_SubjectDescriptions() {
		return null;
	}

}
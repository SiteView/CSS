package ILOG.Diagrammer.GraphLayout;

import java.util.List;
import java.util.Vector;

import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.ConstraintManager;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HGraph;

public abstract class HierarchicalConstraint implements IGraphLayoutConstraint {

	private HierarchicalConstraint _next;

	private float _priority;

	private List<GraphicObject> _firstSubject;

	private List<GraphicObject> _secondSubject;

	private Boolean _validForLayout = false;

	public Integer Unspecificed = -1;

	public HierarchicalConstraint(HierarchicalConstraint source) {
		this._next = null;
		this._priority = source.get_Priority();
	}

	public HierarchicalConstraint(float priority) {
		this._next = null;
		this._priority = priority;
		this._validForLayout = true;
	}

	public abstract void ActAfterAdd(ConstraintManager manager);

	public abstract HierarchicalConstraint Copy();

	public java.lang.Object GetFirstSubject() {

		return null;

	}

	public HierarchicalConstraint GetNext() {

		return this._next;

	}

	public java.lang.Object GetSecondSubject() {

		return null;

	}

	public void Add(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		if ((this._firstSubject != null) && (this._firstSubject.size() > 0)) {

			if (((IGraphLayoutConstraint) this).SupportsNodeGroups()) {
				HierarchicalNodeGroup subject = new HierarchicalNodeGroup();

				for (GraphicObject obj2 : this._firstSubject) {
					subject.Add(obj2);
				}
				this.SetFirstSubject(subject);
			} else {
				this.SetFirstSubject(this._firstSubject.get(0));
			}
		}
		if ((this._secondSubject != null) && (this._secondSubject.size() > 0)) {

			if (((IGraphLayoutConstraint) this).SupportsNodeGroups()) {
				HierarchicalNodeGroup group2 = new HierarchicalNodeGroup();

				for (GraphicObject obj3 : this._secondSubject) {
					group2.Add(obj3);
				}
				this.SetSecondSubject(group2);
			} else {
				this.SetSecondSubject(this._secondSubject.get(0));
			}
		}
		((HierarchicalLayout) layout).AddConstraint(this);

	}

	public void Remove(ILOG.Diagrammer.GraphLayout.GraphLayout layout) {
		((HierarchicalLayout) layout).RemoveConstraint(this);

	}

	public Boolean SupportsNodeGroups() {

		return true;

	}

	public Boolean IsValidForLayout() {

		return this._validForLayout;

	}

	public void SetFirstSubject(java.lang.Object subject) {

	}

	public void SetNext(HierarchicalConstraint next) {
		this._next = next;

	}

	public void SetSecondSubject(java.lang.Object subject) {

	}

	public void SetValidForLayout(Boolean flag) {
		this._validForLayout = flag;

	}

	public abstract Boolean Validate(IGraphModel model);

	public abstract void ValidateForLayout(HGraph graph);

	public Boolean ValidateGroup(IGraphModel model,
			HierarchicalNodeGroup subject) {
		if (subject == null) {

			return false;
		}

		return subject.Validate(model);

	}

	public void ValidateGroupForLayout(HGraph graph,
			HierarchicalNodeGroup subject) {
		if (subject == null) {
			this.SetValidForLayout(false);
		}

		else if (!subject.IsValidForLayout()) {
			this.SetValidForLayout(false);
		}

	}

	public Boolean ValidateNode(IGraphModel model, java.lang.Object subject) {
		if (subject == null) {

			return false;
		}

		return model.IsNode(subject);

	}

	public void ValidateNodeForLayout(HGraph graph, java.lang.Object subject) {
		if (subject == null) {
			this.SetValidForLayout(false);
		} else if (graph.GetNode(subject) == null) {
			this.SetValidForLayout(false);
		}

	}

	public Boolean ValidateNodeOrGroup(IGraphModel model,
			java.lang.Object subject) {
		if (subject == null) {

			return false;
		}
		if (subject instanceof HierarchicalNodeGroup) {

			return ((HierarchicalNodeGroup) subject).Validate(model);
		}

		return model.IsNode(subject);

	}

	public void ValidateNodeOrGroupForLayout(HGraph graph,
			java.lang.Object subject) {
		if (subject == null) {
			this.SetValidForLayout(false);
		} else if (subject instanceof HierarchicalNodeGroup) {

			if (!((HierarchicalNodeGroup) subject).IsValidForLayout()) {
				this.SetValidForLayout(false);
			}
		} else if (graph.GetNode(subject) == null) {
			this.SetValidForLayout(false);
		}

	}

	public List<GraphicObject> get_FirstSubject() {
		if (this._firstSubject == null) {
			this._firstSubject = new Vector<GraphicObject>();
		}

		return this._firstSubject;
	}

	public String[] get_SubjectDescriptions() {

		return new String[0];
	}

	public float get_Priority() {

		return this._priority;
	}

	public void set_Priority(float value) {
		this._priority = value;
	}

	public List<GraphicObject> get_SecondSubject() {
		if (this._secondSubject == null) {
			this._secondSubject = new Vector<GraphicObject>();
		}

		return this._secondSubject;
	}

}
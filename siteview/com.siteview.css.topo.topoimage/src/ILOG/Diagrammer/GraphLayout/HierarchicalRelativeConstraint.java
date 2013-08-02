package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.*;
import system.*;

public abstract class HierarchicalRelativeConstraint extends
		HierarchicalConstraint {
	public java.lang.Object _hgNodeOrGroup;

	public java.lang.Object _lwNodeOrGroup;

	public HierarchicalRelativeConstraint(HierarchicalRelativeConstraint source) {
		super(source);
		this._lwNodeOrGroup = source._lwNodeOrGroup;
		this._hgNodeOrGroup = source._hgNodeOrGroup;
	}

	public HierarchicalRelativeConstraint(java.lang.Object lowerNodeOrGroup,
			java.lang.Object higherNodeOrGroup, float priority) {
		super(priority);
		this._lwNodeOrGroup = lowerNodeOrGroup;
		this._hgNodeOrGroup = higherNodeOrGroup;
	}

	@Override
	public void ActAfterAdd(ConstraintManager manager) {
		java.lang.Object firstSubject = this.GetFirstSubject();
		if (firstSubject instanceof HierarchicalNodeGroup) {
			manager.AddGroup((HierarchicalNodeGroup) firstSubject);
		}

		firstSubject = this.GetSecondSubject();
		if (firstSubject instanceof HierarchicalNodeGroup) {
			manager.AddGroup((HierarchicalNodeGroup) firstSubject);
		}

	}

	@Override
	public abstract HierarchicalConstraint Copy();

	@Override
	public java.lang.Object GetFirstSubject() {

		return this._lwNodeOrGroup;

	}

	@Override
	public java.lang.Object GetSecondSubject() {

		return this._hgNodeOrGroup;

	}

	@Override
	public void SetFirstSubject(java.lang.Object subject) {
		this._lwNodeOrGroup = subject;

	}

	@Override
	public void SetSecondSubject(java.lang.Object subject) {
		this._hgNodeOrGroup = subject;

	}

	@Override
	public Boolean Validate(IGraphModel model) {

		if (!super.ValidateNodeOrGroup(model, this.GetFirstSubject())) {

			return false;
		}

		if (!super.ValidateNodeOrGroup(model, this.GetSecondSubject())) {

			return false;
		}

		return true;

	}

	@Override
	public void ValidateForLayout(HGraph graph) {
		super.SetValidForLayout(true);
		super.ValidateNodeOrGroupForLayout(graph, this.GetFirstSubject());
		super.ValidateNodeOrGroupForLayout(graph, this.GetSecondSubject());

	}

}
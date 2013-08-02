package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.ConstraintManager;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HGraph;

public class HierarchicalSideBySideConstraint extends HierarchicalConstraint
		implements IGraphLayoutConstraint {
	private HierarchicalNodeGroup _group;

	public HierarchicalSideBySideConstraint() {
		this(new HierarchicalNodeGroup(), 0f);
	}

	public HierarchicalSideBySideConstraint(
			HierarchicalSideBySideConstraint source) {
		super(source);
		this._group = source._group;
	}

	public HierarchicalSideBySideConstraint(HierarchicalNodeGroup group,
			float priority) {
		super(priority);
		this._group = group;
	}

	@Override
	public void ActAfterAdd(ConstraintManager manager) {
		manager.AddGroup(this.GetGroup());

	}

	@Override
	public HierarchicalConstraint Copy() {

		return new HierarchicalSideBySideConstraint(this);

	}

	@Override
	public java.lang.Object GetFirstSubject() {

		return this._group;

	}

	public HierarchicalNodeGroup GetGroup() {

		return this._group;

	}

	@Override
	public void SetFirstSubject(java.lang.Object subject) {
		this._group = (HierarchicalNodeGroup) subject;

	}

	@Override
	public Boolean Validate(IGraphModel model) {

		return super.ValidateGroup(model, this.GetGroup());

	}

	@Override
	public void ValidateForLayout(HGraph graph) {
		super.SetValidForLayout(true);
		super.ValidateGroupForLayout(graph, this.GetGroup());

	}
	//
	// String[] get_SubjectDescriptions(){
	//
	// return new String[]{Resources.get_ConstraintEditor_SideBySideGroup()};
	// }

}
package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.ConstraintManager;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HGraph;

public class HierarchicalSameLevelConstraint extends HierarchicalConstraint
		implements IGraphLayoutConstraint {
	private java.lang.Object _node1;

	private java.lang.Object _node2;

	public HierarchicalSameLevelConstraint() {
		this(null, null);
	}

	public HierarchicalSameLevelConstraint(
			HierarchicalSameLevelConstraint source) {
		super(source);
		this._node1 = source._node1;
		this._node2 = source._node2;
	}

	public HierarchicalSameLevelConstraint(java.lang.Object node1,
			java.lang.Object node2) {
		super(Float.MAX_VALUE);
		this._node1 = node1;
		this._node2 = node2;
	}

	@Override
	public void ActAfterAdd(ConstraintManager manager) {

	}

	@Override
	public HierarchicalConstraint Copy() {

		return new HierarchicalSameLevelConstraint(this);

	}

	public java.lang.Object GetFirstNode() {

		return this._node1;

	}

	@Override
	public java.lang.Object GetFirstSubject() {

		return this._node1;

	}

	public java.lang.Object GetSecondNode() {

		return this._node2;

	}

	@Override
	public java.lang.Object GetSecondSubject() {

		return this._node2;

	}

	public Boolean SupportsNodeGroups() {

		return false;

	}

	@Override
	public void SetFirstSubject(java.lang.Object subject) {
		this._node1 = subject;

	}

	@Override
	public void SetSecondSubject(java.lang.Object subject) {
		this._node2 = subject;

	}

	@Override
	public Boolean Validate(IGraphModel model) {

		if (!super.ValidateNode(model, this.GetFirstNode())) {

			return false;
		}

		if (!super.ValidateNode(model, this.GetSecondNode())) {

			return false;
		}

		return true;

	}

	@Override
	public void ValidateForLayout(HGraph graph) {
		super.SetValidForLayout(true);
		super.ValidateNodeForLayout(graph, this.GetFirstNode());
		super.ValidateNodeForLayout(graph, this.GetSecondNode());

	}

}
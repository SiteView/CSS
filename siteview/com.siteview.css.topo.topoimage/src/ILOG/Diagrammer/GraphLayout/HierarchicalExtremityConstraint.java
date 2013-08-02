package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.ConstraintManager;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HGraph;

public class HierarchicalExtremityConstraint extends HierarchicalConstraint
		implements IGraphLayoutConstraint {
	private java.lang.Object _node;

	private int _side;

	public HierarchicalExtremityConstraint() {
		this(null, HierarchicalLayoutSide.North);
	}

	public HierarchicalExtremityConstraint(
			HierarchicalExtremityConstraint source) {
		super(source);
		this._node = source._node;
		this._side = source._side;
	}

	public HierarchicalExtremityConstraint(java.lang.Object node, int side) {
		super((float) 2.722259E+38f);
		this._node = node;
		this._side = side;
	}

	@Override
	public void ActAfterAdd(ConstraintManager manager) {

	}

	@Override
	public HierarchicalConstraint Copy() {

		return new HierarchicalExtremityConstraint(this);

	}

	@Override
	public java.lang.Object GetFirstSubject() {

		return this._node;

	}

	public java.lang.Object GetNode() {

		return this._node;

	}

	public Boolean SupportsNodeGroups() {

		return false;

	}

	@Override
	public void SetFirstSubject(java.lang.Object subject) {
		this._node = subject;

	}

	@Override
	public Boolean Validate(IGraphModel model) {

		return super.ValidateNode(model, this.GetNode());

	}

	@Override
	public void ValidateForLayout(HGraph graph) {
		super.SetValidForLayout(true);
		super.ValidateNodeForLayout(graph, this.GetNode());

	}

	public int get_Side() {

		return this._side;
	}

	public void set_Side(int value) {
		this._side = value;
	}

}
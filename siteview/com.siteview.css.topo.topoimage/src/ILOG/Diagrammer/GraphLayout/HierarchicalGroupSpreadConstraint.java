package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.GraphLayout.Internal.HLayout.ConstraintManager;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HGraph;

public class HierarchicalGroupSpreadConstraint extends HierarchicalConstraint
		implements IGraphLayoutConstraint {
	private HierarchicalNodeGroup _group;

	private Integer _spreadSize;

	public HierarchicalGroupSpreadConstraint() {
		this(new HierarchicalNodeGroup(), 0);
	}

	public HierarchicalGroupSpreadConstraint(
			HierarchicalGroupSpreadConstraint source) {
		super(source);
		this._group = source._group;
		this._spreadSize = source._spreadSize;
	}

	public HierarchicalGroupSpreadConstraint(HierarchicalNodeGroup group,
			Integer spreadSize) {
		super(Float.MAX_VALUE);
		this._group = group;
		this.set_SpreadSize(spreadSize);
	}

	@Override
	public void ActAfterAdd(ConstraintManager manager) {
		manager.AddGroup(this.GetGroup());

	}

	@Override
	public HierarchicalConstraint Copy() {

		return new HierarchicalGroupSpreadConstraint(this);

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

	public Integer get_SpreadSize() {

		return this._spreadSize;
	}

	public void set_SpreadSize(Integer value) {
		if (value < 0) {
			this._spreadSize = 0x7fffffff;
		} else {
			this._spreadSize = value;
		}
	}

}
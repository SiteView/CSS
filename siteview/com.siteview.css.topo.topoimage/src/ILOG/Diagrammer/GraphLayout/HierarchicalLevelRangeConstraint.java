package ILOG.Diagrammer.GraphLayout;

import system.Math;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.ConstraintManager;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HGraph;

public class HierarchicalLevelRangeConstraint extends HierarchicalConstraint
		implements IGraphLayoutConstraint {
	private Integer _maxLevel;

	private Integer _minLevel;

	private java.lang.Object _nodeOrGroup;

	public HierarchicalLevelRangeConstraint() {
		this(new HierarchicalNodeGroup(), -1, -1);
	}

	public HierarchicalLevelRangeConstraint(
			HierarchicalLevelRangeConstraint source) {
		super(source);
		this._nodeOrGroup = source._nodeOrGroup;
		this._minLevel = source._minLevel;
		this._maxLevel = source._maxLevel;
	}

	public HierarchicalLevelRangeConstraint(java.lang.Object nodeOrGroup,
			Integer minLevel, Integer maxLevel) {
		super((float) 3.062541E+38f);
		this._nodeOrGroup = nodeOrGroup;
		this.SetLevelRange(minLevel, maxLevel);
	}

	@Override
	public void ActAfterAdd(ConstraintManager manager) {
		java.lang.Object subject = this.GetSubject();
		if (subject instanceof HierarchicalNodeGroup) {
			manager.AddGroup((HierarchicalNodeGroup) subject);
		}

	}

	@Override
	public HierarchicalConstraint Copy() {

		return new HierarchicalLevelRangeConstraint(this);

	}

	@Override
	public java.lang.Object GetFirstSubject() {

		return this._nodeOrGroup;

	}

	public java.lang.Object GetSubject() {

		return this._nodeOrGroup;

	}

	@Override
	public void SetFirstSubject(java.lang.Object subject) {
		this._nodeOrGroup = subject;

	}

	public void SetLevelRange(Integer minLevel, Integer maxLevel) {
		if (maxLevel < 0) {
			this._minLevel = minLevel;
			this._maxLevel = -1;
		} else if (minLevel <= maxLevel) {
			this._minLevel = minLevel;
			this._maxLevel = maxLevel;
		} else {
			this._minLevel = maxLevel;
			this._maxLevel = minLevel;
		}

	}

	@Override
	public Boolean Validate(IGraphModel model) {

		return super.ValidateNodeOrGroup(model, this.GetSubject());

	}

	@Override
	public void ValidateForLayout(HGraph graph) {
		super.SetValidForLayout(true);
		super.ValidateNodeOrGroupForLayout(graph, this.GetSubject());

	}

	public Integer get_MaxLevel() {

		return this._maxLevel;
	}

	public void set_MaxLevel(Integer value) {
		if (value < 0) {
			this._maxLevel = -1;
		} else {
			this._maxLevel = value;
			if (this._minLevel >= 0) {

				this._minLevel = (int) Math.Min(this._minLevel, value);
			}
		}
	}

	public Integer get_MinLevel() {

		return this._minLevel;
	}

	public void set_MinLevel(Integer value) {
		if (value < 0) {
			this._minLevel = -1;
		} else {
			this._minLevel = value;
			if (this._maxLevel >= 0) {

				this._maxLevel = (int) Math.Max(this._maxLevel, value);
			}
		}
	}

}
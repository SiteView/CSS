package ILOG.Diagrammer.GraphLayout;

import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.GraphLayout.Internal.InternalRect;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.ConstraintManager;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HGraph;

public class HierarchicalSwimLaneConstraint extends HierarchicalConstraint
		implements IGraphLayoutConstraint {
	private InternalRect _calcBBox;

	private Integer _calcPositionIndex;

	private HierarchicalNodeGroup _group;

	private float _minMargin;

	private Integer _specPositionIndex;

	private float _specSize;

	public HierarchicalSwimLaneConstraint() {
		this(new HierarchicalNodeGroup());
	}

	public HierarchicalSwimLaneConstraint(HierarchicalNodeGroup group) {
		this(group, 0f, -1);
	}

	public HierarchicalSwimLaneConstraint(HierarchicalSwimLaneConstraint source) {
		super(source);
		this._group = source._group;
		this._calcBBox = new InternalRect(source._calcBBox.X,
				source._calcBBox.Y, source._calcBBox.Width,
				source._calcBBox.Height);
		this._specSize = source._specSize;
		this._specPositionIndex = source._specPositionIndex;
		this._calcPositionIndex = source._calcPositionIndex;
		this._minMargin = source._minMargin;
	}

	public HierarchicalSwimLaneConstraint(HierarchicalNodeGroup group,
			float relativeSize, Integer positionIndex) {
		this(group, relativeSize, positionIndex, 0f);
	}

	public HierarchicalSwimLaneConstraint(HierarchicalNodeGroup group,
			float relativeSize, Integer positionIndex, float minMargin) {
		super(Float.MAX_VALUE);
		this._group = group;
		this._calcBBox = new InternalRect(0f, 0f, 0f, 0f);
		this._calcPositionIndex = 0;
		this.set_RelativeSize(relativeSize);
		this.set_SpecPositionIndex(positionIndex);
		this.set_MinMargin(minMargin);
	}

	@Override
	public void ActAfterAdd(ConstraintManager manager) {
		manager.AddGroup(this.GetGroup());

	}

	@Override
	public HierarchicalConstraint Copy() {

		return new HierarchicalSwimLaneConstraint(this);

	}

	public Rectangle2D GetCalcBoundingBox() {

		return TranslateUtil.InternalRect2Rectangle2D(new InternalRect(
				this._calcBBox.X, this._calcBBox.Y, this._calcBBox.Width,
				this._calcBBox.Height));

	}

	public Integer GetCalcPositionIndex() {

		return this._calcPositionIndex;

	}

	@Override
	public java.lang.Object GetFirstSubject() {

		return this._group;

	}

	public HierarchicalNodeGroup GetGroup() {

		return this._group;

	}

	public void SetCalcBoundingBox(InternalRect box) {
		this._calcBBox = box;

	}

	public void SetCalcPositionIndex(Integer pos) {
		this._calcPositionIndex = pos;

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

	// String[] get_SubjectDescriptions(){
	//
	// return new String[]{Resources.get_ConstraintEditor_SwimLaneGroup()};
	// }

	public float get_MinMargin() {

		return this._minMargin;
	}

	public void set_MinMargin(float value) {
		if (value < 0f) {
			this._minMargin = 0f;
		} else {
			this._minMargin = value;
		}
	}

	public float get_RelativeSize() {

		return this._specSize;
	}

	public void set_RelativeSize(float value) {
		if (value < 0f) {
			this._specSize = 0f;
		} else {
			this._specSize = value;
		}
	}

	public Integer get_SpecPositionIndex() {

		return this._specPositionIndex;
	}

	public void set_SpecPositionIndex(Integer value) {
		if (value < 0) {
			this._specPositionIndex = -1;
		} else {
			this._specPositionIndex = value;
		}
	}

}
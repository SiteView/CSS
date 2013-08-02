package ILOG.Diagrammer.GraphLayout;

public class HierarchicalRelativeLevelConstraint extends
		HierarchicalRelativeConstraint implements IGraphLayoutConstraint {
	public HierarchicalRelativeLevelConstraint() {
		this(new HierarchicalNodeGroup(), new HierarchicalNodeGroup(), 0f);
	}

	public HierarchicalRelativeLevelConstraint(
			HierarchicalRelativeLevelConstraint source) {
		super(source);
	}

	public HierarchicalRelativeLevelConstraint(
			java.lang.Object lowerNodeOrGroup,
			java.lang.Object higherNodeOrGroup, float priority) {
		super(lowerNodeOrGroup, higherNodeOrGroup, priority);
	}

	@Override
	public HierarchicalConstraint Copy() {

		return new HierarchicalRelativeLevelConstraint(this);

	}

	public java.lang.Object GetHigherSubject() {

		return super._hgNodeOrGroup;

	}

	public java.lang.Object GetLowerSubject() {

		return super._lwNodeOrGroup;

	}

	// String[] get_SubjectDescriptions(){
	//
	// return new
	// String[]{Resources.get_ConstraintEditor_LowerGroup(),Resources.get_ConstraintEditor_UpperGroup()};
	// }

}
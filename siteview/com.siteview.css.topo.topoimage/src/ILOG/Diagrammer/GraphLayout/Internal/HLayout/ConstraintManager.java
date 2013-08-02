package ILOG.Diagrammer.GraphLayout.Internal.HLayout;

import ILOG.Diagrammer.GraphLayout.*;
import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public final class ConstraintManager {
	private HierarchicalConstraint _firstConstraint;

	private HierarchicalNodeGroup _firstGroup;

	private Integer _numberOfConstraints;

	private Integer _numberOfGroups;

	private long serialVersionUID = 0x155e9b8a46e42L;

	public ConstraintManager() {
	}

	public void AddConstraint(HierarchicalConstraint constraint) {
		if (constraint != null) {
			constraint.SetNext(this._firstConstraint);
			this._firstConstraint = constraint;
			this._numberOfConstraints++;
			constraint.ActAfterAdd(this);
		}

	}

	public void AddGroup(HierarchicalNodeGroup group) {
		if ((group != null) && (group.GetOwner() != this)) {
			if (group.GetOwner() != null) {
				group.GetOwner().RemoveGroup(group);
			}
			group.SetNext(this._firstGroup);
			group.SetOwner(this);
			this._firstGroup = group;
			this._numberOfGroups++;
		}

	}

	public void AfterLayout(HGraph graph) {
		IJavaStyleEnumerator groups = this.GetGroups();

		while (groups.HasMoreElements()) {
			((HierarchicalNodeGroup) groups.NextElement()).AfterLayout(graph);
		}

	}

	public void BeforeLayout(HGraph graph) {
		IJavaStyleEnumerator groups = this.GetGroups();

		while (groups.HasMoreElements()) {
			((HierarchicalNodeGroup) groups.NextElement()).BeforeLayout(graph);
		}

		groups = this.GetConstraints();

		while (groups.HasMoreElements()) {
			((HierarchicalConstraint) groups.NextElement())
					.ValidateForLayout(graph);
		}

	}

	public ConstraintManager Copy() {
		ConstraintManager manager = new ConstraintManager();
		IJavaStyleEnumerator groups = this.GetGroups();

		while (groups.HasMoreElements()) {
			manager.AddGroup(((HierarchicalNodeGroup) groups.NextElement())
					.Copy());
		}

		groups = this.GetConstraints();

		while (groups.HasMoreElements()) {
			HierarchicalConstraint constraint = ((HierarchicalConstraint) groups
					.NextElement()).Copy();
			java.lang.Object firstSubject = constraint.GetFirstSubject();
			if (firstSubject instanceof HierarchicalNodeGroup) {
				constraint
						.SetFirstSubject(((HierarchicalNodeGroup) firstSubject)
								.GetRecentCopy());
			}

			firstSubject = constraint.GetSecondSubject();
			if (firstSubject instanceof HierarchicalNodeGroup) {
				constraint
						.SetSecondSubject(((HierarchicalNodeGroup) firstSubject)
								.GetRecentCopy());
			}
			manager.AddConstraint(constraint);
		}

		return manager;

	}

	public IJavaStyleEnumerator GetConstraints() {

		return new AnonClass_2(this);

	}

	public IJavaStyleEnumerator GetGroups() {

		return new AnonClass_1(this);

	}

	public Integer GetNumberOfConstraints() {

		return this._numberOfConstraints;

	}

	public Integer GetNumberOfGroups() {

		return this._numberOfGroups;

	}

	public void PrepareWriting(HierarchicalLayout layout) {
		if (layout != null) {
			IGraphModel graphModel = layout.GetGraphModel();
			IJavaStyleEnumerator groups = this.GetGroups();

			while (groups.HasMoreElements()) {
				HierarchicalNodeGroup constraintOrGroup = (HierarchicalNodeGroup) groups
						.NextElement();
				IJavaStyleEnumerator enumerator2 = new JavaStyleEnumerator(
						constraintOrGroup.GetEnumerator());

				while (enumerator2.HasMoreElements()) {
					java.lang.Object obj2 = enumerator2.NextElement();
					if ((obj2 != null) && graphModel.IsNode(obj2)) {
						layout.AddConstraintOrGroupToSubjectNode(obj2, 0,
								constraintOrGroup);
					}
				}
			}

			groups = this.GetConstraints();

			while (groups.HasMoreElements()) {
				HierarchicalConstraint constraint = (HierarchicalConstraint) groups
						.NextElement();
				java.lang.Object firstSubject = constraint.GetFirstSubject();
				if ((firstSubject != null) && graphModel.IsNode(firstSubject)) {
					layout.AddConstraintOrGroupToSubjectNode(firstSubject, 1,
							constraint);
				}

				firstSubject = constraint.GetSecondSubject();
				if ((firstSubject != null) && graphModel.IsNode(firstSubject)) {
					layout.AddConstraintOrGroupToSubjectNode(firstSubject, 2,
							constraint);
				}
			}
		}

	}

	public void RemoveConstraint(HierarchicalConstraint constraint) {
		if (constraint != null) {
			if (this._firstConstraint == constraint) {
				this.RemoveNextConstraint(null);
			} else {
				HierarchicalConstraint prevConstraint = this._firstConstraint;
				while ((prevConstraint != null)
						&& (prevConstraint.GetNext() != constraint)) {

					prevConstraint = prevConstraint.GetNext();
				}
				if (prevConstraint != null) {
					this.RemoveNextConstraint(prevConstraint);
				}
			}
		}

	}

	public void RemoveGroup(HierarchicalNodeGroup group) {
		if ((group != null) && (group.GetOwner() == this)) {
			if (this._firstGroup == group) {
				this.RemoveNextGroup(null);
			} else {
				HierarchicalNodeGroup prevGroup = this._firstGroup;
				while ((prevGroup != null) && (prevGroup.GetNext() != group)) {

					prevGroup = prevGroup.GetNext();
				}
				if (prevGroup != null) {
					this.RemoveNextGroup(prevGroup);
				}
			}
		}

	}

	public void RemoveNextConstraint(HierarchicalConstraint prevConstraint) {
		HierarchicalConstraint next = null;
		if (prevConstraint == null) {
			next = this._firstConstraint;
		} else {

			next = prevConstraint.GetNext();
		}
		if (next != null) {
			if (prevConstraint == null) {

				this._firstConstraint = next.GetNext();
			} else {
				prevConstraint.SetNext(next.GetNext());
			}
			next.SetNext(null);
			this._numberOfConstraints--;
		}

	}

	public void RemoveNextGroup(HierarchicalNodeGroup prevGroup) {
		HierarchicalNodeGroup next = null;
		if (prevGroup == null) {
			next = this._firstGroup;
		} else {

			next = prevGroup.GetNext();
		}
		if ((next != null) && (next.GetOwner() == this)) {
			if (prevGroup == null) {

				this._firstGroup = next.GetNext();
			} else {
				prevGroup.SetNext(next.GetNext());
			}
			next.SetNext(null);
			next.SetOwner(null);
			this._numberOfGroups--;
		}

	}

	public void ValidateConstraints(IGraphModel model) {
		IJavaStyleEnumerator constraints = this.GetConstraints();
		HierarchicalConstraint prevConstraint = null;

		while (constraints.HasMoreElements()) {
			HierarchicalConstraint constraint2 = (HierarchicalConstraint) constraints
					.NextElement();

			if (!constraint2.Validate(model)) {
				this.RemoveNextConstraint(prevConstraint);
			} else {
				prevConstraint = constraint2;
			}
		}

	}

	public void ValidateGroups(IGraphModel model) {
		IJavaStyleEnumerator groups = this.GetGroups();
		HierarchicalNodeGroup prevGroup = null;

		while (groups.HasMoreElements()) {
			HierarchicalNodeGroup group2 = (HierarchicalNodeGroup) groups
					.NextElement();

			if (!group2.Validate(model)) {
				this.RemoveNextGroup(prevGroup);
			} else {
				prevGroup = group2;
			}
		}

	}

	private class AnonClass_1 implements IJavaStyleEnumerator {
		private ConstraintManager __outerThis;

		public HierarchicalNodeGroup actGroup;

		public AnonClass_1(ConstraintManager input__outerThis) {
			this.__outerThis = input__outerThis;
			this.actGroup = this.__outerThis._firstGroup;
		}

		public Boolean HasMoreElements() {

			return (this.actGroup != null);

		}

		public java.lang.Object NextElement() {
			if (this.actGroup == null) {
				throw (new system.Exception("no more groups"));
			}
			HierarchicalNodeGroup actGroup = this.actGroup;

			this.actGroup = actGroup.GetNext();

			return actGroup;

		}

	}

	private class AnonClass_2 implements IJavaStyleEnumerator {
		private ConstraintManager __outerThis;

		public HierarchicalConstraint actConstraint;

		public AnonClass_2(ConstraintManager input__outerThis) {
			this.__outerThis = input__outerThis;
			this.actConstraint = this.__outerThis._firstConstraint;
		}

		public Boolean HasMoreElements() {

			return (this.actConstraint != null);

		}

		public java.lang.Object NextElement() {
			if (this.actConstraint == null) {
				throw (new system.Exception("no more constraints"));
			}
			HierarchicalConstraint actConstraint = this.actConstraint;

			this.actConstraint = actConstraint.GetNext();

			return actConstraint;

		}

	}
}
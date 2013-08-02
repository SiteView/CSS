package ILOG.Diagrammer.GraphLayout;

import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerable;
import system.Collections.IEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.ConstraintManager;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.HGraph;
import ILOG.Diagrammer.GraphLayout.Internal.HLayout.Leveling.HLVNode;

public class HierarchicalNodeGroup implements ICollection, IEnumerable {
	private HLVNode _groupEntryNode;

	private HLVNode _groupExitNode;

	private HierarchicalNodeGroup _next;

	private ArrayList _nodes;

	private ArrayList _originalNodes;

	private ConstraintManager _owner;

	private HierarchicalNodeGroup _recentCopy;

	private Boolean _validForLayout = false;

	public HierarchicalNodeGroup() {
		this((ArrayList) null);
	}

	public HierarchicalNodeGroup(HierarchicalNodeGroup source) {
		this(source._nodes);
	}

	public HierarchicalNodeGroup(ArrayList nodes) {
		this._owner = null;
		this._next = null;
		if (nodes == null) {
			this._nodes = null;
		} else {
			this._nodes = (ArrayList) nodes.Clone();
		}
	}

	public void Add(java.lang.Object node) {
		if (this._nodes == null) {
			this._nodes = new ArrayList(2);
		}
		this._nodes.Add(node);

	}

	public void AfterLayout(HGraph graph) {
		this._nodes = this._originalNodes;
		this._originalNodes = null;

	}

	public void BeforeLayout(HGraph graph) {
		java.lang.Object obj2 = null;
		this._originalNodes = this._nodes;
		Boolean flag = true;
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(this._nodes);
		while (enumerator.HasMoreElements() && flag) {

			obj2 = enumerator.NextElement();
			if (graph.GetNode(obj2) == null) {
				flag = false;
			}
		}
		if (!flag) {
			ArrayList list = new ArrayList(this._nodes.get_Count());

			enumerator = TranslateUtil.Collection2JavaStyleEnum(this._nodes);

			while (enumerator.HasMoreElements()) {

				obj2 = enumerator.NextElement();
				if (graph.GetNode(obj2) != null) {
					list.Add(obj2);
				}
			}
			this._nodes = list;
		}
		this._validForLayout = this._nodes.get_Count() > 0;

	}

	public Boolean Contains(java.lang.Object node) {
		if (this._nodes == null) {

			return false;
		}

		return this._nodes.Contains(node);

	}

	public HierarchicalNodeGroup Copy() {
		this._recentCopy = new HierarchicalNodeGroup(this);

		return this._recentCopy;

	}

	public void CopyTo(java.lang.Object array, int index) {
		if (this._nodes != null) {
			this._nodes.CopyTo(array, index);
		}

	}

	public HLVNode GetEntryNode() {

		return this._groupEntryNode;

	}

	public IEnumerator GetEnumerator() {
		if (this._nodes == null) {

			return new ArrayList().GetEnumerator();
		}

		return this._nodes.GetEnumerator();

	}

	public HLVNode GetExitNode() {

		return this._groupExitNode;

	}

	public HierarchicalNodeGroup GetNext() {

		return this._next;

	}

	public ConstraintManager GetOwner() {

		return this._owner;

	}

	public HierarchicalNodeGroup GetRecentCopy() {

		return this._recentCopy;

	}

	public void InitializeBeforeLeveling() {
		this._groupEntryNode = (HLVNode) (this._groupExitNode = null);

	}

	public Boolean IsValidForLayout() {

		return this._validForLayout;

	}

	public void Remove(java.lang.Object node) {

		while (this.Contains(node)) {
			this._nodes.Remove(node);
		}

	}

	public void SetEntryNode(HLVNode node) {
		this._groupEntryNode = node;

	}

	public void SetExitNode(HLVNode node) {
		this._groupExitNode = node;

	}

	public void SetNext(HierarchicalNodeGroup next) {
		this._next = next;

	}

	public void SetOwner(ConstraintManager manager) {
		this._owner = manager;

	}

	public Boolean Validate(IGraphModel model) {
		if (this._nodes == null) {

			return false;
		}
		ArrayList list = new ArrayList(this._nodes.get_Count());
		IEnumerator it1 = this._nodes.GetEnumerator();
		while (it1.MoveNext()) {
			java.lang.Object obj2 = (java.lang.Object) it1.get_Current();

			if (model.IsNode(obj2) && !list.Contains(obj2)) {
				list.Add(obj2);
			}
		}
		list.TrimToSize();
		this._nodes = list;

		return (this._nodes.get_Count() > 0);

	}

	public int get_Count() {
		if (this._nodes == null) {

			return 0;
		}

		return this._nodes.get_Count();
	}

	public boolean get_IsSynchronized() {

		return false;
	}

	public java.lang.Object get_SyncRoot() {

		return this;
	}

}
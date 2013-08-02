package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import system.Collections.ArrayList;
import system.Collections.Hashtable;
import system.Collections.ICollection;
import ILOG.Diagrammer.GraphLayout.IGraphModel;
import ILOG.Diagrammer.GraphLayout.LongLinkLayout;
import ILOG.Diagrammer.GraphLayout.Internal.IJavaStyleEnumerator;
import ILOG.Diagrammer.GraphLayout.Internal.TranslateUtil;

public final class LLIncrementalData {
	private Hashtable _linkData = null;

	private Hashtable _nodeData = null;

	public LLIncrementalData() {
	}

	public void CleanLink(java.lang.Object link) {
		if (this._linkData != null) {
			this._linkData.Remove(link);
		}

	}

	public void CleanNode(java.lang.Object node) {
		if (this._nodeData != null) {
			this._nodeData.Remove(node);
		}

	}

	public void Clear(java.lang.Object link) {
		LLIncrementalLink linkData = this.GetLinkData(link);
		if (linkData != null) {
			linkData.Clear();
		}

	}

	public LLIncrementalLink GetLinkData(java.lang.Object link) {
		if (this._linkData == null) {

			return null;
		}

		return (LLIncrementalLink) this._linkData.get_Item(link);

	}

	public LLIncrementalNode GetNodeData(java.lang.Object node) {
		if (this._nodeData == null) {

			return null;
		}

		return (LLIncrementalNode) this._nodeData.get_Item(node);

	}

	public void Prepare(LongLinkLayout layout, LLGrid grid) {
		IGraphModel graphModel = layout.GetGraphModel();
		if ((graphModel != null) && (this._nodeData != null)) {
			ArrayList coll = new ArrayList();
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum((ICollection) this._nodeData
							.get_Values());

			while (enumerator.HasMoreElements()) {
				LLIncrementalNode node = (LLIncrementalNode) enumerator
						.NextElement();

				if (graphModel.IsNode(node.GetNodeObject())) {
					node.Prepare(layout, grid);
				} else {
					coll.Add(node);
				}
				layout.AddStepCount(1);
			}

			enumerator = TranslateUtil.Collection2JavaStyleEnum(coll);

			while (enumerator.HasMoreElements()) {
				this._nodeData.Remove(((LLIncrementalNode) enumerator
						.NextElement()).GetNodeObject());
			}
		}

	}

	public void Update(LongLinkLayout layout) {
		IGraphModel graphModel = layout.GetGraphModel();
		if (graphModel != null) {
			java.lang.Object obj2 = null;
			Integer count = graphModel.get_Nodes().get_Count();
			Integer num2 = graphModel.get_Links().get_Count();
			this._nodeData = new Hashtable((count + (count / 2)) + 1);
			this._linkData = new Hashtable((num2 + (num2 / 2)) + 1);
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(graphModel.get_Nodes());

			while (enumerator.HasMoreElements()) {

				obj2 = enumerator.NextElement();
				this._nodeData.set_Item(obj2, new LLIncrementalNode(layout,
						obj2));
				layout.AddStepCount(1);
			}

			enumerator = TranslateUtil.Collection2JavaStyleEnum(graphModel
					.get_Links());

			while (enumerator.HasMoreElements()) {

				obj2 = enumerator.NextElement();
				this._linkData.set_Item(obj2, new LLIncrementalLink(layout,
						obj2));
				layout.AddStepCount(1);
			}
		}

	}

}
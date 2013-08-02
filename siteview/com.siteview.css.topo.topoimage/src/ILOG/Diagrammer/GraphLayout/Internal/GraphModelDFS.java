package ILOG.Diagrammer.GraphLayout.Internal;

import ILOG.Diagrammer.GraphLayout.*;
import system.*;

public abstract class GraphModelDFS {
	public IGraphModel _graphModel;

	public String _propName;

	public GraphModelDFS(IGraphModel graphModel, String propName) {
		if (graphModel == null) {
			throw (new ArgumentException("graphModel cannot be null"));
		}
		if (propName == null) {
			throw (new ArgumentException("propName cannot be null"));
		}
		this._graphModel = graphModel;
		this._propName = propName;
	}

	public void Clean() {
		if (this._graphModel != null) {
			IJavaStyleEnumerator enumerator = TranslateUtil
					.Collection2JavaStyleEnum(GraphModelUtil
							.GetNodesAndLinks(this._graphModel));

			while (enumerator.HasMoreElements()) {
				this._graphModel.SetProperty(enumerator.NextElement(),
						this._propName, null);
			}
		}

	}

	private void DfsVisit(java.lang.Object node) {
		this.StartVisit(node);
		this.MarkNode(node);
		IJavaStyleEnumerator enumerator = TranslateUtil
				.Collection2JavaStyleEnum(GraphModelUtil.GetLinks(
						this._graphModel, node));

		while (enumerator.HasMoreElements()) {
			java.lang.Object link = enumerator.NextElement();

			if (!this.IsMarkedLink(link)) {
				this.MarkLink(link);
				java.lang.Object obj3 = GraphModelUtil.GetOpposite(
						this._graphModel, link, node);

				if (!this.IsMarkedNode(obj3)) {
					this.TraverseDiscovery(link, node);

					if (!this.IsDone()) {
						this.DfsVisit(obj3);
					}
				} else {
					this.TraverseBack(link, node);
				}
			}
		}
		this.FinishVisit(node);

	}

	public void Execute(java.lang.Object startNode, Boolean clean) {
		if (startNode == null) {
			throw (new ArgumentException("startNode cannot be null"));
		}
		try {
			this.DfsVisit(startNode);
		} finally {
			if (clean) {
				this.Clean();
			}
		}

	}

	public void FinishVisit(java.lang.Object node) {

	}

	public Boolean IsDone() {

		return false;

	}

	public Boolean IsMarkedLink(java.lang.Object link) {

		return (this._graphModel.GetProperty(link, this._propName) != null);

	}

	public Boolean IsMarkedNode(java.lang.Object node) {

		return (this._graphModel.GetProperty(node, this._propName) != null);

	}

	public void MarkLink(java.lang.Object link) {
		this._graphModel.SetProperty(link, this._propName, link);

	}

	public void MarkNode(java.lang.Object node) {
		this._graphModel.SetProperty(node, this._propName, node);

	}

	public void StartVisit(java.lang.Object node) {

	}

	public void TraverseBack(java.lang.Object link, java.lang.Object fromNode) {

	}

	public void TraverseDiscovery(java.lang.Object link,
			java.lang.Object fromNode) {

	}

}
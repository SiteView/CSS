package ILOG.Diagrammer.GraphLayout;

public class RecursiveLayoutProvider extends DefaultLayoutProvider {
	@Override
	public ILOG.Diagrammer.GraphLayout.GraphLayout GetGraphLayout(
			IGraphModel graphModel) {

		return this.GetPreferredLayout(graphModel);

	}

}
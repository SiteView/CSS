package ILOG.Diagrammer.GraphLayout;

public interface ILayoutProvider {
	ILOG.Diagrammer.GraphLayout.GraphLayout GetGraphLayout(
			IGraphModel graphModel);

}
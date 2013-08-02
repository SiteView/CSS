package ILOG.Diagrammer.GraphLayout;

public interface ILinkConnectionBoxProvider extends INodeBoxProvider {
	float GetTangentialOffset(IGraphModel graphModel, java.lang.Object node,
			int nodeSide);

}
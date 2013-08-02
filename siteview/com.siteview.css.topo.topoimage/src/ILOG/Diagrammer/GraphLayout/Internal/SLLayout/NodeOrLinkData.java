package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import ILOG.Diagrammer.GraphLayout.Internal.*;
import system.*;

public abstract class NodeOrLinkData {
	public InternalRect boundingBox = new InternalRect(0f, 0f, 0f, 0f);

	public java.lang.Object get_nodeOrLink() {
		return null;
	}

	public void set_nodeOrLink(java.lang.Object value) {
	}

	public NodeOrLinkData(java.lang.Object nodeOrLink) {
		this.set_nodeOrLink(nodeOrLink);
	}

	public abstract Boolean IsIntersectingObjectValid(
			ILOG.Diagrammer.GraphLayout.Internal.SLLayout.LinkData linkData);

	public abstract Boolean IsLink();

	public abstract Boolean IsNode();

}